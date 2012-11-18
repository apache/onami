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

import java.lang.annotation.Annotation;

import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheRemoveEntry;

import org.aopalliance.intercept.MethodInvocation;

abstract class AfterBeforeInvocationInterceptor<A extends Annotation>
    extends CacheInterceptor<A>
{

    @Override
    protected final Object invoke( CacheInvocationContext<A> context, MethodInvocation invocation )
        throws Throwable
    {
        InterceptedAnnotationProxy<A> annotationProxy = new InterceptedAnnotationProxy<A>( context.getCacheAnnotation() );

        if ( !annotationProxy.afterInvocation() )
        {
            hitCache( context );
        }

        final Object invocationResult;
        try
        {
            invocationResult = invocation.proceed();
        }
        catch ( Throwable t )
        {
            if ( annotationProxy.afterInvocation() )
            {
                // Exception is included
                if ( include( t, annotationProxy.include(), annotationProxy.exclude(), false ) )
                {
                    hitCache( context );
                }
            }

            throw t;
        }

        if ( annotationProxy.afterInvocation() )
        {
            hitCache( context );
        }

        return invocationResult;
    }

    protected abstract void hitCache( CacheInvocationContext<A> context );

    private static final class InterceptedAnnotationProxy<A extends Annotation>
    {

        private final A interceptedAnnotation;

        public InterceptedAnnotationProxy( A interceptedAnnotation )
        {
            this.interceptedAnnotation = interceptedAnnotation;
        }

        public boolean afterInvocation()
        {
            if ( CachePut.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CachePut) interceptedAnnotation).afterInvocation();
            }
            else if ( CacheRemoveAll.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CacheRemoveAll) interceptedAnnotation).afterInvocation();
            }
            else if ( CacheRemoveEntry.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CacheRemoveEntry) interceptedAnnotation).afterInvocation();
            }

            // don't happens
            return false;
        }

        public Class<? extends Throwable>[] include()
        {
            if ( CachePut.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CachePut) interceptedAnnotation).cacheFor();
            }
            else if ( CacheRemoveAll.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CacheRemoveAll) interceptedAnnotation).evictFor();
            }
            else if ( CacheRemoveEntry.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CacheRemoveEntry) interceptedAnnotation).evictFor();
            }

            // don't happens
            return null;
        }

        public Class<? extends Throwable>[] exclude()
        {
            if ( CachePut.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CachePut) interceptedAnnotation).noCacheFor();
            }
            else if ( CacheRemoveAll.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CacheRemoveAll) interceptedAnnotation).noEvictFor();
            }
            else if ( CacheRemoveEntry.class.isInstance( interceptedAnnotation ) )
            {
                return ( (CacheRemoveEntry) interceptedAnnotation).noEvictFor();
            }

            // don't happens
            return null;
        }

    }

}
