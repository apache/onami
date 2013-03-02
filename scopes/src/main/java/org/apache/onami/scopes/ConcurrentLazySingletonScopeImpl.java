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

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.internal.CircularDependencyProxy;

import java.util.HashMap;
import java.util.Map;

class ConcurrentLazySingletonScopeImpl
    implements Scope
{
    private static final Object NULL = new Object();

    private final Map<Key<?>, LockRecord> locks = new HashMap<Key<?>, LockRecord>();

    public <T> Provider<T> scope( final Key<T> key, final Provider<T> creator )
    {
        return new Provider<T>()
        {
            /*
             * The lazily initialized singleton instance. Once set, this will either have type T or will
             * be equal to NULL.
             */
            private volatile Object instance;

            // DCL on a volatile is safe as of Java 5, which we obviously require.
            @SuppressWarnings( "DoubleCheckedLocking" )
            public T get()
            {
                if ( instance == null )
                {
                    final LockRecord lock = getLock( key );
                    try
                    {
                        //noinspection SynchronizationOnLocalVariableOrMethodParameter
                        synchronized ( lock )
                        {
                            if ( instance == null )
                            {
                                T provided = creator.get();

                                // don't remember proxies; these exist only to serve circular dependencies
                                if ( provided instanceof CircularDependencyProxy )
                                {
                                    return provided;
                                }

                                Object providedOrSentinel = ( provided == null ) ? NULL : provided;
                                if ( ( instance != null ) && ( instance != providedOrSentinel ) )
                                {
                                    throw new ProvisionException( "Provider was reentrant while creating a singleton" );
                                }

                                instance = providedOrSentinel;
                            }
                        }
                    }
                    finally
                    {
                        releaseLock( lock, key );
                    }
                }

                Object localInstance = instance;
                // This is safe because instance has type T or is equal to NULL
                @SuppressWarnings( { "unchecked", "UnnecessaryLocalVariable" } ) T returnedInstance =
                    ( localInstance != NULL ) ? (T) localInstance : null;
                return returnedInstance;
            }

            public String toString()
            {
                return String.format( "%s[%s]", creator, instance );
            }
        };
    }

    private LockRecord getLock( Key<?> key )
    {
        synchronized ( locks )
        {
            LockRecord lock = locks.get( key );
            if ( lock == null )
            {
                lock = new LockRecord();
                locks.put( key, lock );
            }
            ++lock.useCount;
            return lock;
        }
    }

    private void releaseLock( LockRecord lock, Key<?> key )
    {
        synchronized ( locks )
        {
            if ( --lock.useCount <= 0 )
            {
                locks.remove( key );
            }
        }
    }

    @Override
    public String toString()
    {
        return "ConcurrentLazySingletonScope.SCOPE";
    }

    private static class LockRecord
    {
        private int useCount = 0;
    }
}
