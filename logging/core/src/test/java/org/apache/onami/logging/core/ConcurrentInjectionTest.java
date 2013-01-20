package org.apache.onami.logging.core;

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
import static org.testng.Assert.assertNotNull;

import java.lang.reflect.Field;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

public class ConcurrentInjectionTest {
    private Injector injector;

    @BeforeTest
    public void setUp()
    {
        injector = createInjector( new AbstractLoggingModule<Object>( Matchers.only( TypeLiteral.get( InjectionTarget.class ) ),
                                                                      TestLoggerInjector.class ) );
    }

    @Test( threadPoolSize = 25, invocationCount = 5000 )
    public void injectAndVerify()
    {
        InjectionTarget target = new InjectionTarget();
        injector.injectMembers( target );
        assertNotNull( target.logger );
    }

    private static class TestLoggerInjector
        extends AbstractLoggerInjector<Object>
    {

        private volatile Object barrier;

        public TestLoggerInjector( Field field )
        {
            super( field );
        }

        @Override
        protected Object createLogger( Class<?> clazz )
        {
            Object result = new Object();
            barrier = result; // artificially trigger a memory barrier
            try
            {
                Thread.sleep( 20 );
            }
            catch ( InterruptedException e )
            {
            }
            return result;
        }
    }

    private static class InjectionTarget
    {

        @InjectLogger
        private Object logger;

    }

}
