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
import com.google.inject.CreationException;
import com.google.inject.Provides;
import org.apache.onami.lifecycle.core.DisposingStager;
import org.apache.onami.lifecycle.core.StageHandler;
import org.junit.Test;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.*;

public final class PreDestroyModuleTestCase
{

    @Test
    public void disposeUsingModuleOnInjectorFailure()
    {
        PreDestroyModule preDestroyModule = new PreDestroyModule();
        try
        {
            createInjector( preDestroyModule, new AbstractModule()
            {

                @Override
                protected void configure()
                {
                    bind( ThrowingExceptionConstructor.class ).asEagerSingleton();
                }

            } );
            fail( "Expected exception was not thrown" );
        }
        catch( CreationException e )
        {
            Throwable cause = e.getCause();
            assertTrue( cause instanceof IllegalArgumentException );
            assertEquals( "Expected exception", cause.getMessage() );
        }
        finally
        {
            preDestroyModule.getStager().stage( new StageHandler()
            {

                @Override
                public <I> void onSuccess( I injectee )
                {
                    assertTrue( injectee instanceof DisposableObject );
                    assertTrue( ( (DisposableObject) injectee ).disposed );
                }

                @Override
                public <I, E extends Throwable> void onError( I injectee, E error )
                {
                    fail( error.toString() );
                }

            } );
        }
    }

    @Test
    public void disposeUsingModuleWithProvidesMethodOnInjectorFailure()
    {
        PreDestroyModule preDestroyModule = new PreDestroyModule();
        try
        {
            createInjector( preDestroyModule, new AbstractModule()
            {

                @Override
                protected void configure()
                {
                    bind( ThrowingExceptionConstructor2.class ).asEagerSingleton();
                }

                @Provides
                public ExecutorService provideExecutorService( DisposingStager<PreDestroy> stager )
                {
                    return stager.register( Executors.newCachedThreadPool() );
                }

            } );
            fail( "Expected exception was not thrown" );
        }
        catch( CreationException e )
        {
            Throwable cause = e.getCause();
            assertTrue( cause instanceof IllegalArgumentException );
            assertEquals( "Expected exception", cause.getMessage() );
        }
        finally
        {
            preDestroyModule.getStager().stage( new StageHandler()
            {

                @Override
                public <I> void onSuccess( I injectee )
                {
                    assertTrue( injectee instanceof ExecutorService );
                    assertTrue( ( (ExecutorService) injectee ).isShutdown() );
                }

                @Override
                public <I, E extends Throwable> void onError( I injectee, E error )
                {
                    fail( error.toString() );
                }

            } );
        }
    }

}
