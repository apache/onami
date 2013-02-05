package org.apache.onami.spi;

import static org.junit.Assert.assertEquals;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Module;

@RunWith( OnamiRunner.class )
public final class FromSystemPropertiesTestCase
{

    @GuiceProvidedModules
    public static Module createTestModule()
    {
        // This simulates the SPI specification via Java System Properties,
        // equivalent to java -Dorg.apache.onami.spi.FooService=org.apac...
        System.setProperty( "org.apache.onami.spi.FooService",
                            "org.apache.onami.spi.FooServiceImpl1," +
                            "org.apache.onami.spi.FooServiceImpl2");

        return new ServiceLoaderModule()
        {

            @Override
            protected void configureServices()
            {
                discover( FooService.class );
            }

        };
    }

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

    @Test
    public void injectedServicesCaughtFromSystemProperties()
    {
        assertEquals( FooServiceImpl1.class, fooService1.getClass() );
        assertEquals( FooServiceImpl2.class, fooService2.getClass() );
    }

}
