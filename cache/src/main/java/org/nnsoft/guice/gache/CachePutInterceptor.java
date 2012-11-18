package org.nnsoft.guice.gache;

/*
 *  Copyright 2012 The 99 Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import javax.cache.Cache;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.CachePut;

import org.aopalliance.intercept.MethodInvocation;

final class CachePutInterceptor
    extends AfterBeforeInvocationInterceptor<CachePut>
{

    @Override
    public Class<CachePut> getInterceptedAnnotationType()
    {
        return CachePut.class;
    }

    @Override
    protected void hitCache( CacheInvocationContext<CachePut> context )
    {
        CacheKeyInvocationContext<CachePut> keyedContext = (CacheKeyInvocationContext<CachePut>) context;
        Object value = keyedContext.getValueParameter().getValue();

        if ( value == null )
        {
            if ( context.getCacheAnnotation().cacheNull() )
            {
                // Null values are cached, set value to the null placeholder
                value = NULL_PLACEHOLDER;
            }
            else
            {
                // Ignore null values
                return;
            }
        }

        Cache<Object, Object> cache = getCacheResolverFactory( context ).getCacheResolver( context ).resolveCache( context );
        CacheKey cacheKey = getCacheKeyGenerator( context ).generateCacheKey( keyedContext );
        cache.put( cacheKey, value );
    }

}
