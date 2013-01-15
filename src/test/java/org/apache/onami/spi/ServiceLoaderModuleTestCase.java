package org.apache.onami.spi;

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

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public final class ServiceLoaderModuleTestCase
{

    @Inject
    private AcmeService firstAcmeService;

    @Inject
    @Named( "second" )
    private AcmeService secondAcmeService;

    @Before
    public void setUp()
    {
        createInjector( new ServiceLoaderModule()
        {

            @Override
            protected void configureServices()
            {
                discover( AcmeService.class );
            }

        } )
        .getMembersInjector( ServiceLoaderModuleTestCase.class )
        .injectMembers( this );
    }

    @Test
    public void singleServiceInjection()
    {
        assertEquals( AcmeServiceImpl1.class, firstAcmeService.getClass() );
        assertEquals( AcmeServiceImpl2.class, secondAcmeService.getClass() );
    }

}
