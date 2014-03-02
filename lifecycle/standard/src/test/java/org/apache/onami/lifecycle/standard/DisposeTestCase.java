package org.apache.onami.lifecycle.standard;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class DisposeTestCase
{

    @Inject
    private Disposer disposer;

    private boolean disposeInvoked = false;

    public void setDisposer( Disposer disposer )
    {
        this.disposer = disposer;
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
        disposer.dispose();
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

        } ).getInstance( Disposer.class ).dispose( new DisposeHandler()
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
