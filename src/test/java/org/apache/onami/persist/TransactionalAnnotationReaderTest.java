package org.apache.onami.persist;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * Test for {@link TransactionalAnnotationReader}.
 */
public class TransactionalAnnotationReaderTest
{
    private TransactionalAnnotationReader sut;

    @Before
    public void setUp()
        throws Exception
    {
        sut = new TransactionalAnnotationReader();
    }

    @Test
    public void shouldReadFromMethodWithAnnotation()
        throws Exception
    {
        final MethodInvocation invocation = methodInvocation( new WithMethodAnnotations(), "withAnno" );
        final Transactional result = sut.readAnnotationFrom( invocation );

        Assert.assertThat( result, TransactionalAnnotationMatcher.transactionalAnnotation(
            new Class[]{ OtherPersistenceUnit.class }, new Class[]{ NullPointerException.class }, new Class[]{ } ) );
    }

    @Test
    public void shouldReadFromClassWithAnnotation()
        throws Exception
    {
        final MethodInvocation invocation = methodInvocation( new WithClassAnnotations(), "noAnno" );
        final Transactional result = sut.readAnnotationFrom( invocation );

        Assert.assertThat( result, TransactionalAnnotationMatcher.transactionalAnnotation(
            new Class[]{ TestPersistenceUnit.class }, new Class[]{ IllegalArgumentException.class }, new Class[]{ } ) );
    }

    @Test
    public void shouldReadFromDefaultsClassAndMethodWithoutAnnotation()
        throws Exception
    {
        final MethodInvocation invocation = methodInvocation( new WithoutAnyAnnotations(), "noAnno" );
        final Transactional result = sut.readAnnotationFrom( invocation );

        Assert.assertThat( result, TransactionalAnnotationMatcher.transactionalAnnotation( new Class[]{ }, new Class[]{
            RuntimeException.class }, new Class[]{ } ) );
    }

    @Test
    public void shouldReadFromMethodWithAnnotationWhenBothClassAndMethodAnnotationAreGiven()
        throws Exception
    {
        final MethodInvocation invocation = methodInvocation( new WithClassAndMethodAnnotations(), "withAnno" );
        final Transactional result = sut.readAnnotationFrom( invocation );

        Assert.assertThat( result, TransactionalAnnotationMatcher.transactionalAnnotation(
            new Class[]{ OtherPersistenceUnit.class }, new Class[]{ NullPointerException.class }, new Class[]{ } ) );
    }

    // classes and methods to pass to the TransactionalAnnotationReader for testing

    private static class WithoutAnyAnnotations
    {
        public void noAnno()
        {
            // nop
        }
    }

    @Transactional( onUnits = TestPersistenceUnit.class, rollbackOn = IllegalArgumentException.class )
    private static class WithClassAnnotations
    {
        public void noAnno()
        {
            // nop
        }
    }

    private static class WithMethodAnnotations
    {
        @Transactional( onUnits = OtherPersistenceUnit.class, rollbackOn = NullPointerException.class )
        public void withAnno()
        {
            // nop
        }
    }

    @Transactional( onUnits = TestPersistenceUnit.class, rollbackOn = IllegalArgumentException.class )
    private static class WithClassAndMethodAnnotations
    {
        @Transactional( onUnits = OtherPersistenceUnit.class, rollbackOn = NullPointerException.class )
        public void withAnno()
        {
            // nop
        }
    }


    private static MethodInvocation methodInvocation( final Object instance, String name )
    {
        try
        {
            final Method method = instance.getClass().getDeclaredMethod( name );
            return new MethodInvocation()
            {
                public Method getMethod()
                {
                    return method;
                }

                public Object[] getArguments()
                {
                    throw new RuntimeException( "not implemented in mock" );
                }

                public Object proceed()
                    throws Throwable
                {
                    throw new RuntimeException( "not implemented in mock" );
                }

                public Object getThis()
                {
                    return instance;
                }

                public AccessibleObject getStaticPart()
                {
                    throw new RuntimeException( "not implemented in mock" );
                }
            };
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
