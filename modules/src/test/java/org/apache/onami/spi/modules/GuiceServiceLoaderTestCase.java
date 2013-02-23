package org.apache.onami.spi.modules;

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

import static com.google.inject.util.Modules.combine;
import static org.apache.onami.spi.modules.GuiceServiceLoader.loadModules;
import static org.junit.Assert.assertEquals;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;

@RunWith( OnamiRunner.class )
public final class GuiceServiceLoaderTestCase
{

    @GuiceProvidedModules
    public static Module createTestModule()
    {
        return combine( loadModules() );
    }

    @Inject
    private AcmeService acmeService;

    public void setAcmeService( AcmeService acmeService )
    {
        this.acmeService = acmeService;
    }

    @Test
    public void verifyRightModulesWereLoaded()
    {
        assertEquals( AcmeServiceImpl1.class, acmeService.getClass() );
    }

    public static final class AcmeModule
        extends AbstractModule
    {

        @Override
        protected void configure()
        {
            bind( AcmeService.class ).to( AcmeServiceImpl1.class );
        }

    }

}
