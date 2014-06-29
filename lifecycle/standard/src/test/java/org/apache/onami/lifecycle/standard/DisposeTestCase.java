package org.apache.onami.lifecycle.standard;

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
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stager;
import org.junit.Before;
import org.junit.Test;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class DisposeTestCase
{

    @Inject
    private Stager<Dispose> stager;

    private boolean disposeInvoked = false;

    public void setStager( Stager<Dispose> stager )
    {
        this.stager = stager;
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
        stager.stage();
        assertTrue( disposeInvoked );
    }

    @Test( expected = ConfigurationException.class )
    public void disposeAnnotatedMethodRequiresNoArgs()
    {
        createInjector( new DisposeModule() ).getInstance( WrongDisposeMethod.class );
    }

    @Test//( expected = ConfigurationException.class )
    public void disposeAnnotatedMethodThrowsException()
    {
        createInjector( new DisposeModule(), new AbstractModule()
        {

            @Override
            protected void configure()
            {
                bind( ThrowingExceptionDisposeMethod.class ).toInstance( new ThrowingExceptionDisposeMethod() );
            }

        } ).getInstance( Key.get( new TypeLiteral<Stager<Dispose>>() {} ) ).stage( new StageHandler()
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
