package org.apache.onami.autobind.scanner.asm.tests.autobind.provider;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.onami.autobind.configuration.StartupModule;
import org.apache.onami.autobind.scanner.PackageFilter;
import org.apache.onami.autobind.scanner.asm.ASMClasspathScanner;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ProviderTest
{

    @Test
    public void createDynamicModule()
    {
        System.setProperty( "mode", "ALL" );
        StartupModule startup =
            StartupModule.create( ASMClasspathScanner.class, PackageFilter.create( ProviderTest.class ) );
        startup.bindSystemProperties().disableStartupConfiguration();
        Injector injector = Guice.createInjector( startup );
        assertNotNull( injector );
    }

    @Test
    public void createTestInterface()
    {
        System.setProperty( "mode", "ALL" );
        StartupModule startup =
            StartupModule.create( ASMClasspathScanner.class, PackageFilter.create( ProviderTest.class ) );
        startup.bindSystemProperties().disableStartupConfiguration();

        Injector injector = Guice.createInjector( startup );
        assertNotNull( injector );

        try
        {
            Container instance = injector.getInstance( Container.class );
            assertTrue( instance.get() == Mode.ALL );
        }
        catch ( Exception e )
        {
            // ignore
            fail( e.getMessage() );
        }
    }

}
