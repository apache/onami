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

import org.apache.onami.lifecycle.standard.AfterInjectionModule;
import org.apache.onami.test.OnamiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Binder;
import com.google.inject.Guice;
import javax.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import javax.inject.Provider;

@RunWith(OnamiRunner.class)
public class TestLazySingleton
{
    public static class InjectedProvider
    {
        public final Provider<LazySingletonObject> provider;

        @Inject
        public InjectedProvider( Provider<LazySingletonObject> provider )
        {
            this.provider = provider;
        }
    }

    public static class InjectedAnnotatedProvider
    {
        public final Provider<AnnotatedLazySingletonObject> provider;

        @Inject
        public InjectedAnnotatedProvider( Provider<AnnotatedLazySingletonObject> provider )
        {
            this.provider = provider;
        }
    }

    @Before
    public void setup()
    {
        AnnotatedLazySingletonObject.constructorCount.set( 0 );
        AnnotatedLazySingletonObject.postConstructCount.set( 0 );
        LazySingletonObject.constructorCount.set( 0 );
        LazySingletonObject.postConstructCount.set( 0 );
    }

    @Test
    public void testUsingAnnotation()
    {
        Injector injector = Guice.createInjector( new AfterInjectionModule(), new ScopesModule() );

        assertEquals( AnnotatedLazySingletonObject.constructorCount.get(), 0 );
        assertEquals( AnnotatedLazySingletonObject.postConstructCount.get(), 0 );

        AnnotatedLazySingletonObject instance = injector.getInstance( AnnotatedLazySingletonObject.class );
        assertEquals( AnnotatedLazySingletonObject.constructorCount.get(), 1 );
        assertEquals( AnnotatedLazySingletonObject.postConstructCount.get(), 1 );

        AnnotatedLazySingletonObject instance2 = injector.getInstance( AnnotatedLazySingletonObject.class );
        assertEquals( AnnotatedLazySingletonObject.constructorCount.get(), 1 );
        assertEquals( AnnotatedLazySingletonObject.postConstructCount.get(), 1 );

        assertSame( instance, instance2 );
    }

    @Test
    public void testUsingInWithProviderAndAnnotation()
    {
        Injector injector = Guice.createInjector( new AfterInjectionModule(), new ScopesModule() );

        assertEquals( AnnotatedLazySingletonObject.constructorCount.get(), 0 );
        assertEquals( AnnotatedLazySingletonObject.postConstructCount.get(), 0 );

        InjectedAnnotatedProvider injectedProvider = injector.getInstance( InjectedAnnotatedProvider.class );
        assertEquals( AnnotatedLazySingletonObject.constructorCount.get(), 0 );
        assertEquals( AnnotatedLazySingletonObject.postConstructCount.get(), 0 );

        AnnotatedLazySingletonObject instance = injectedProvider.provider.get();
        assertEquals( AnnotatedLazySingletonObject.constructorCount.get(), 1 );
        assertEquals( AnnotatedLazySingletonObject.postConstructCount.get(), 1 );

        AnnotatedLazySingletonObject instance2 = injectedProvider.provider.get();
        assertEquals( AnnotatedLazySingletonObject.constructorCount.get(), 1 );
        assertEquals( AnnotatedLazySingletonObject.postConstructCount.get(), 1 );

        assertSame( instance, instance2 );
    }

    @Test
    public void testUsingIn()
    {
        Module module = new Module()
        {
            public void configure( Binder binder )
            {
                binder.bind( LazySingletonObject.class ).in( LazySingletonScope.get() );
            }
        };
        Injector injector = Guice.createInjector( new AfterInjectionModule(), new ScopesModule(), module );

        assertEquals( LazySingletonObject.constructorCount.get(), 0 );
        assertEquals( LazySingletonObject.postConstructCount.get(), 0 );

        LazySingletonObject instance = injector.getInstance( LazySingletonObject.class );
        assertEquals( LazySingletonObject.constructorCount.get(), 1 );
        assertEquals( LazySingletonObject.postConstructCount.get(), 1 );

        LazySingletonObject instance2 = injector.getInstance( LazySingletonObject.class );
        assertEquals( LazySingletonObject.constructorCount.get(), 1 );
        assertEquals( LazySingletonObject.postConstructCount.get(), 1 );

        assertSame( instance, instance2 );
    }

    @Test
    public void testUsingInWithProvider()
    {
        Module module = new Module()
        {
            public void configure( Binder binder )
            {
                binder.bind( LazySingletonObject.class ).in( LazySingletonScope.get() );
            }
        };
        Injector injector = Guice.createInjector( new AfterInjectionModule(), new ScopesModule(), module );

        assertEquals( LazySingletonObject.constructorCount.get(), 0 );
        assertEquals( LazySingletonObject.postConstructCount.get(), 0 );

        InjectedProvider injectedProvider = injector.getInstance( InjectedProvider.class );
        assertEquals( LazySingletonObject.constructorCount.get(), 0 );
        assertEquals( LazySingletonObject.postConstructCount.get(), 0 );

        LazySingletonObject instance = injectedProvider.provider.get();
        assertEquals( LazySingletonObject.constructorCount.get(), 1 );
        assertEquals( LazySingletonObject.postConstructCount.get(), 1 );

        LazySingletonObject instance2 = injectedProvider.provider.get();
        assertEquals( LazySingletonObject.constructorCount.get(), 1 );
        assertEquals( LazySingletonObject.postConstructCount.get(), 1 );

        assertSame( instance, instance2 );
    }
}
