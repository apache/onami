package org.nnsoft.guice.gspi;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;

public final class FromSystemPropertiesTestCase
{

    @Inject
    private Set<FooService> fooServices;

    public void setFooServices( Set<FooService> fooServices )
    {
        this.fooServices = fooServices;
    }

    @Before
    public void setUp()
    {
        createInjector( new ServiceLoaderModule()
        {

            @Override
            protected void configure()
            {
                bindService( FooService.class ).loadingAllServices();
            }

        } )
        .getMembersInjector( FromSystemPropertiesTestCase.class )
        .injectMembers( this );
    }

    @Test
    public void injectedServicesCaughtFromSystemProperties()
    {
        assertFalse( fooServices.isEmpty() );
        assertEquals( 2, fooServices.size() );
    }

}
