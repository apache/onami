package org.apache.onami.spi;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;

public final class FromSystemPropertiesTestCase
{

    @Inject
    @BarBindingAnnotation( 1 )
    private FooService fooService1;

    @Inject
    @BarBindingAnnotation( 2 )
    private FooService fooService2;

    public void setFooService1( FooService fooService1 )
    {
        this.fooService1 = fooService1;
    }
    
    public void setFooService2( FooService fooService2 )
    {
        this.fooService2 = fooService2;
    }

    @Before
    public void setUp()
    {
        // This simulates the SPI specification via Java System Properties,
        // equivalent to java -Dorg.apache.onami.spi.FooService=org.apac...
        System.setProperty( "org.apache.onami.spi.FooService",
                            "org.apache.onami.spi.FooServiceImpl1," +
                            "org.apache.onami.spi.FooServiceImpl2");

        createInjector( new ServiceLoaderModule()
        {

            @Override
            protected void configureServices()
            {
                discover( FooService.class );
            }

        } )
        .getMembersInjector( FromSystemPropertiesTestCase.class )
        .injectMembers( this );
    }

    @Test
    public void injectedServicesCaughtFromSystemProperties()
    {
        assertEquals( FooServiceImpl1.class, fooService1.getClass() );
        assertEquals( FooServiceImpl2.class, fooService2.getClass() );
    }

}
