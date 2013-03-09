package org.apache.onami.scopes;

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
import static org.junit.Assert.assertSame;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.onami.lifecycle.standard.AfterInjectionModule;
import org.apache.onami.test.OnamiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

@RunWith(OnamiRunner.class)
public class TestConcurrentLazySingleton
{
    public static class InjectedAnnotatedProvider
    {
        public final Provider<AnnotatedConcurrentLazySingletonObject> provider;

        @Inject
        public InjectedAnnotatedProvider( Provider<AnnotatedConcurrentLazySingletonObject> provider )
        {
            this.provider = provider;
        }
    }

    @Before
    public void setup()
    {
        AnnotatedConcurrentLazySingletonObject.constructorCount.set( 0 );
        AnnotatedConcurrentLazySingletonObject.postConstructCount.set( 0 );
        LazySingletonObject.constructorCount.set( 0 );
        LazySingletonObject.postConstructCount.set( 0 );
    }

    @ConcurrentLazySingleton
    public static class DeadLockTester
    {
        @Inject
        public DeadLockTester( final Injector injector )
            throws InterruptedException
        {
            final CountDownLatch latch = new CountDownLatch( 1 );
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            try
            {
                executorService.submit( new Callable<Object>()
                {
                    public Object call()
                        throws Exception
                    {
                        injector.getInstance( AnnotatedConcurrentLazySingletonObject.class );
                        latch.countDown();
                        return null;
                    }
                } );
                latch.await();
            }
            finally
            {
                executorService.shutdownNow();
            }
        }
    }

    @Test
    public void testDeadLock()
        throws InterruptedException
    {
        Injector injector = Guice.createInjector( new ScopesModule() );
        injector.getInstance( DeadLockTester.class ); // if ConcurrentLazySingleton is not used, this line will deadlock
        assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
    }

    @Test
    public void testUsingAnnotation()
    {
        Injector injector = Guice.createInjector( new AfterInjectionModule(), new ScopesModule() );

        assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 0 );
        assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 0 );

        AnnotatedConcurrentLazySingletonObject instance =
            injector.getInstance( AnnotatedConcurrentLazySingletonObject.class );
        assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
        assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 1 );

        AnnotatedConcurrentLazySingletonObject instance2 =
            injector.getInstance( AnnotatedConcurrentLazySingletonObject.class );
        assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
        assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 1 );

        assertSame( instance, instance2 );
    }

    @Test
    public void testUsingInWithProviderAndAnnotation()
    {
        Injector injector = Guice.createInjector( new AfterInjectionModule(), new ScopesModule() );

        assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 0 );
        assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 0 );

        InjectedAnnotatedProvider injectedProvider = injector.getInstance( InjectedAnnotatedProvider.class );
        assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 0 );
        assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 0 );

        AnnotatedConcurrentLazySingletonObject instance = injectedProvider.provider.get();
        assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
        assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 1 );

        AnnotatedConcurrentLazySingletonObject instance2 = injectedProvider.provider.get();
        assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
        assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 1 );

        assertSame( instance, instance2 );
    }
}
