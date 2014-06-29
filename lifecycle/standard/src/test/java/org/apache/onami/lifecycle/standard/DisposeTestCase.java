package org.apache.onami.lifecycle.standard;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stager;
import org.junit.Before;
import org.junit.Test;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class DisposeTestCase
{

    @Inject
    private Stager<Dispose> stager;

    private boolean disposeInvoked = false;

    public void setStager( Stager<Dispose> stager )
    {
        this.stager = stager;
    }

    @Dispose
    public void close()
    {
        disposeInvoked = true;
    }

    @Before
    public void setUp()
    {
        createInjector( new DisposeModule() )
        .getMembersInjector( DisposeTestCase.class )
        .injectMembers( this );
    }

    @Test
    public void disposeMethodInvoked()
    {
        stager.stage();
        assertTrue( disposeInvoked );
    }

    @Test( expected = ConfigurationException.class )
    public void disposeAnnotatedMehthodRequiresNoArgs()
    {
        createInjector( new DisposeModule() ).getInstance( WrongDisposeMethod.class );
    }

    @Test//( expected = ConfigurationException.class )
    public void disposeAnnotatedMehthodThrowsException()
    {
        createInjector( new DisposeModule(), new AbstractModule()
        {

            @Override
            protected void configure()
            {
                bind( ThrowingExceptionDisposeMethod.class ).toInstance( new ThrowingExceptionDisposeMethod() );
            }

        } ).getInstance( Key.get( new TypeLiteral<Stager<Dispose>>() {} ) ).stage( new StageHandler()
        {

            public <I> void onSuccess( I injectee )
            {
                fail();
            }

            public <I, E extends Throwable> void onError( I injectee, E error )
            {
                assertTrue( injectee instanceof ThrowingExceptionDisposeMethod );
                assertTrue( error instanceof IllegalStateException );
            }

        } );
    }

}
