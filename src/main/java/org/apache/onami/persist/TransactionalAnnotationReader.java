package org.apache.onami.persist;

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

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * Reader which obtains the concrete @{@link Transactional} annotation on a method. The reader may use some sort of
 * caching.
 */
class TransactionalAnnotationReader
{
    private static final Transactional DEFAULT_TRANSACTIONAL = DefaultTransactional.class.getAnnotation( Transactional.class );

    /**
     * cache for {@link Transactional} annotations per method.
     */
    final TransactionalCache transactionalCache = new TransactionalCache();


    /**
     * Reads the @{@link Transactional} of a given method invocation.
     *
     * @param methodInvocation the method invocation for which to obtain the @{@link Transactional}.
     * @return the @{@link Transactional} of the given method invocation. Never {@code null}.
     */
    Transactional readAnnotationFrom( MethodInvocation methodInvocation )
    {
        final Method method = methodInvocation.getMethod();
        Transactional result;

        result = transactionalCache.get( method );
        if ( null == result )
        {
            result = getTransactional( methodInvocation, method );
            transactionalCache.put( method, result );
        }
        return result;
    }

    private Transactional getTransactional( MethodInvocation methodInvocation, Method method )
    {
        Transactional result;
        result = method.getAnnotation( Transactional.class );
        if ( null == result )
        {
            final Class<?> targetClass = methodInvocation.getThis().getClass();
            result = targetClass.getAnnotation( Transactional.class );
        }
        if ( null == result )
        {
            result = DEFAULT_TRANSACTIONAL;
        }
        return result;
    }

    /**
     * Helper class for obtaining the default of @{@link Transactional}.
     */
    @Transactional
    private static class DefaultTransactional
    {
    }

    private static class TransactionalCache
    {
        Transactional get( Method method )
        {
            return null;
        }

        void put( Method method, Transactional annotation )
        {
            // nop
        }
    }
}
