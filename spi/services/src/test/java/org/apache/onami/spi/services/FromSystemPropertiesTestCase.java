package org.apache.onami.spi.services;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static org.junit.Assert.assertEquals;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import com.google.inject.Module;

@RunWith( OnamiRunner.class )
public final class FromSystemPropertiesTestCase
{

    @GuiceProvidedModules
    public static Module createTestModule()
    {
        // This simulates the SPI specification via Java System Properties,
        // equivalent to java -Dorg.apache.onami.spi.services.FooService=org.apac...
        System.setProperty( "org.apache.onami.spi.services.FooService",
                            "org.apache.onami.spi.services.FooServiceImpl1," +
                            "org.apache.onami.spi.services.FooServiceImpl2");

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
