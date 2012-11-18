package org.nnsoft.guice.gspi;

/*
 *  Copyright 2012 The 99 Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import static org.junit.Assert.assertEquals;
import static org.nnsoft.guice.gspi.GuiceServiceLoader.loadModules;
import static com.google.inject.Guice.createInjector;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public final class GuiceServiceLoaderTestCase
{

    @Inject
    private AcmeService acmeService;

    public void setAcmeService( AcmeService acmeService )
    {
        this.acmeService = acmeService;
    }

    @Before
    public void setUp()
    {
        createInjector( loadModules() )
        .getMembersInjector( GuiceServiceLoaderTestCase.class )
        .injectMembers( this );
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
