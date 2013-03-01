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

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.apache.onami.lifecycle.AfterInjectionModule;
import org.apache.onami.test.OnamiRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

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
            Executors.newSingleThreadExecutor().submit( new Callable<Object>()
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
    }

    @Test
    public void testDeadLock()
        throws InterruptedException
    {
        Injector injector = Guice.createInjector( new ScopesModule() );
        injector.getInstance( DeadLockTester.class ); // if ConcurrentLazySingleton is not used, this line will deadlock
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
    }

    @Test
    public void testUsingAnnotation()
    {
        Injector injector = Guice.createInjector( new AfterInjectionModule(), new ScopesModule() );

        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 0 );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 0 );

        AnnotatedConcurrentLazySingletonObject instance =
            injector.getInstance( AnnotatedConcurrentLazySingletonObject.class );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 1 );

        AnnotatedConcurrentLazySingletonObject instance2 =
            injector.getInstance( AnnotatedConcurrentLazySingletonObject.class );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 1 );

        Assert.assertSame( instance, instance2 );
    }

    @Test
    public void testUsingInWithProviderAndAnnotation()
    {
        Injector injector = Guice.createInjector( new AfterInjectionModule(), new ScopesModule() );

        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 0 );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 0 );

        InjectedAnnotatedProvider injectedProvider = injector.getInstance( InjectedAnnotatedProvider.class );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 0 );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 0 );

        AnnotatedConcurrentLazySingletonObject instance = injectedProvider.provider.get();
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 1 );

        AnnotatedConcurrentLazySingletonObject instance2 = injectedProvider.provider.get();
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.constructorCount.get(), 1 );
        Assert.assertEquals( AnnotatedConcurrentLazySingletonObject.postConstructCount.get(), 1 );

        Assert.assertSame( instance, instance2 );
    }
}
