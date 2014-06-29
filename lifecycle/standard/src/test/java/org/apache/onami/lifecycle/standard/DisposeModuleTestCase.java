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
import com.google.inject.CreationException;
import com.google.inject.Provides;
import org.apache.onami.lifecycle.core.DisposingStager;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stageable;
import org.apache.onami.lifecycle.core.Stager;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.*;

public final class DisposeModuleTestCase
{

    @Test
    public void disposeUsingModuleOnInjectorFailure()
    {
        DisposeModule disposeModule = new DisposeModule();
        try
        {
            createInjector( disposeModule, new AbstractModule()
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
            disposeModule.getStager().stage( new StageHandler()
            {

                public <I> void onSuccess( I injectee )
                {
                    assertTrue( injectee instanceof DisposableObject );
                    assertTrue( ( (DisposableObject) injectee ).disposed );
                }

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
        DisposeModule disposeModule = new DisposeModule();
        try
        {
            createInjector( disposeModule, new AbstractModule()
            {

                @Override
                protected void configure()
                {
                    bind( ThrowingExceptionConstructor2.class ).asEagerSingleton();
                }

                @Provides
                public ExecutorService provideExecutorService( DisposingStager<Dispose> stager )
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
            disposeModule.getStager().stage( new StageHandler()
            {

                public <I> void onSuccess( I injectee )
                {
                    assertTrue( injectee instanceof ExecutorService );
                    assertTrue( ( (ExecutorService) injectee ).isShutdown() );
                }

                public <I, E extends Throwable> void onError( I injectee, E error )
                {
                    fail( error.toString() );
                }

            } );
        }
    }

}
