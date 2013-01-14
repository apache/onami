package org.apache.onami.lifecycle;

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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;

public final class DisposeModuleTestCase
{

    @Test
    public void disposeUsingModuleOnInjectorFailure()
    {
        Disposer disposer = new Disposer();
        try
        {
            createInjector( DisposeModule.builder().withDisposer( disposer ).build(), new AbstractModule()
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
            disposer.dispose( new DisposeHandler()
            {

                public <I> void onSuccess( I injectee )
                {
                    assertTrue( injectee instanceof DisposableObject );
                    assertTrue( ((DisposableObject) injectee).disposed );
                }

                public <I, E extends Throwable> void onError( I injectee, E error )
                {
                    fail( error.toString() );
                }

            });
        }
    }

}
