package org.apache.onami.persist;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class TransactionalAnnotationMatcher
    extends BaseMatcher<Transactional>
{
    public static TransactionalAnnotationMatcher transactionalAnnotation( Class[] expectedUnits,
                                                                          Class[] expectedRollback,
                                                                          Class[] expectedIgnore )
    {
        return new TransactionalAnnotationMatcher( expectedUnits, expectedRollback, expectedIgnore );
    }

    private final Set<Class> expectedUnits;

    private final Set<Class> expectedRollback;

    private final Set<Class> expectedIgnore;

    private TransactionalAnnotationMatcher( Class[] expectedUnits, Class[] expectedRollback, Class[] expectedIgnore )
    {
        this.expectedUnits = asSet( expectedUnits );
        this.expectedRollback = asSet( expectedRollback );
        this.expectedIgnore = asSet( expectedIgnore );
    }

    public boolean matches( Object item )
    {
        if ( item instanceof Transactional )
        {
            Transactional transactional = (Transactional) item;
            final Set<Class<? extends Annotation>> actualUnits = asSet( transactional.onUnits() );
            final Set<Class<? extends Exception>> actualRollback = asSet( transactional.rollbackOn() );
            final Set<Class<? extends Exception>> actualIgnore = asSet( transactional.ignore() );

            return actualUnits.equals( expectedUnits ) && actualRollback.equals( expectedRollback )
                && actualIgnore.equals( expectedIgnore );
        }
        return false;
    }

    private <T> Set<T> asSet( T... elements )
    {
        if ( elements == null )
        {
            return new HashSet<T>();
        }
        return new HashSet<T>( Arrays.asList( elements ) );
    }

    public void describeTo( Description description )
    {
        description
            .appendText( "<@org.apache.onami.persist.Transactional(")
            .appendText( "onUnits=" ).appendValueList( "[", ", ", "]", expectedUnits )
            .appendText( ", rollbackOn=" ).appendValueList( "[", ", ", "]", expectedRollback )
            .appendText( ", ignore=" ).appendValueList( "[", ", ", "]", expectedIgnore )
            .appendText( ")>" );
    }
}
