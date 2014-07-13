package org.apache.onami.lifecycle.jsr250;

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

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stager;
import org.junit.Before;
import org.junit.Test;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class PreDestroyTestCase
{

    @Inject
    private Stager<PreDestroy> stager;

    private boolean closeInvoked = false;

    public void setStager( Stager<PreDestroy> stager )
    {
        this.stager = stager;
    }

    @PreDestroy
    public void close()
    {
        closeInvoked = true;
    }

    @Before
    public void setUp()
    {
        createInjector( new PreDestroyModule() )
            .getMembersInjector( PreDestroyTestCase.class )
            .injectMembers( this );
    }

    @Test
    public void closeMethodInvoked()
    {
        stager.stage();
        assertTrue( closeInvoked );
    }

    @Test( expected = ConfigurationException.class )
    public void preDestroyAnnotatedMethodRequiresNoArgs()
    {
        createInjector( new PreDestroyModule() ).getInstance( WrongPreDestroyMethod.class );
    }

    @Test//( expected = ConfigurationException.class )
    public void disposeAnnotatedMethodThrowsException()
    {
        createInjector( new PreDestroyModule(), new AbstractModule()
        {

            @Override
            protected void configure()
            {
                bind( ThrowingExceptionDisposeMethod.class ).toInstance( new ThrowingExceptionDisposeMethod() );
            }

        } ).getInstance( Key.get( new TypeLiteral<Stager<PreDestroy>>() {} ) ).stage( new StageHandler()
        {

            @Override
            public <I> void onSuccess( I injectee )
            {
                fail();
            }

            @Override
            public <I, E extends Throwable> void onError( I injectee, E error )
            {
                assertTrue( injectee instanceof ThrowingExceptionDisposeMethod );
                assertTrue( error instanceof IllegalStateException );
            }

        } );
    }

}
