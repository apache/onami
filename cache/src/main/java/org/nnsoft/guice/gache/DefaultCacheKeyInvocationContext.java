package org.nnsoft.guice.gache;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKeyInvocationContext;

import com.google.inject.Injector;

final class DefaultCacheKeyInvocationContext<A extends Annotation>
    implements CacheKeyInvocationContext<A>
{

    private final Injector injector;

    private final String cacheName;

    private final Object target;

    protected final Method method;

    private final CacheInvocationParameter[] allParameters;

    private final CacheInvocationParameter[] keyParameters;

    private final CacheInvocationParameter valueParameter;

    private final Set<Annotation> methodAnnotations;

    private final A interceptedAnnotation;

    public DefaultCacheKeyInvocationContext( Injector injector,
                                             String cacheName,
                                             Object target,
                                             Method method,
                                             CacheInvocationParameter[] allParameters,
                                             CacheInvocationParameter[] keyParameters,
                                             CacheInvocationParameter valueParameter,
                                             Set<Annotation> methodAnnotations,
                                             A interceptedAnnotation )
    {
        this.injector = injector;
        this.cacheName = cacheName;
        this.target = target;
        this.method = method;
        this.allParameters = allParameters;
        this.keyParameters = keyParameters;
        this.valueParameter = valueParameter;
        this.methodAnnotations = methodAnnotations;
        this.interceptedAnnotation = interceptedAnnotation;
    }

    public final Method getMethod()
    {
        return method;
    }

    public final Set<Annotation> getAnnotations()
    {
        return methodAnnotations;
    }

    public final A getCacheAnnotation()
    {
        return interceptedAnnotation;
    }

    public final String getCacheName()
    {
        return cacheName;
    }

    public final Object getTarget()
    {
        return target;
    }

    public final CacheInvocationParameter[] getAllParameters()
    {
        return allParameters;
    }

    public final <T> T unwrap( Class<T> cls )
    {
        return injector.getInstance( cls );
    }

    public CacheInvocationParameter[] getKeyParameters()
    {
        return keyParameters;
    }

    public CacheInvocationParameter getValueParameter()
    {
        return valueParameter;
    }

}
