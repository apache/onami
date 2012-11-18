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

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.cache.annotation.CacheAnnotationConfigurationException;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheKeyParam;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheRemoveEntry;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Injector;

abstract class CacheInterceptor<A extends Annotation>
    implements MethodInterceptor
{

    protected static final Object NULL_PLACEHOLDER = new Object();

    @Inject
    private Injector injector;

    @Inject
    private CacheResolverFactory cacheResolverFactory;

    @Inject
    private CacheKeyGenerator cacheKeyGenerator;

    public final void setInjector( Injector injector )
    {
        this.injector = injector;
    }

    public final void setCacheResolverFactory( CacheResolverFactory cacheResolverFactory )
    {
        this.cacheResolverFactory = cacheResolverFactory;
    }

    public final void setCacheKeyGenerator( CacheKeyGenerator cacheKeyGenerator )
    {
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    public abstract Class<A> getInterceptedAnnotationType();

    public final Object invoke( MethodInvocation invocation )
        throws Throwable
    {
        String cacheName = getCacheName( invocation.getMethod() );
        Object target = invocation.getThis();
        A annotation = invocation.getMethod().getAnnotation( getInterceptedAnnotationType() );
        Set<Annotation> methodAnnotations = toAnnotationsSet( invocation.getMethod().getAnnotations() );

        boolean cacheValueAllowed = CachePut.class == getInterceptedAnnotationType();

        CacheInvocationParameter[] allParameters = new CacheInvocationParameter[invocation.getArguments().length];
        List<CacheInvocationParameter> keyParametersList = new ArrayList<CacheInvocationParameter>( allParameters.length );
        CacheInvocationParameter valueParameter = null;

        for ( int i = 0; i < invocation.getArguments().length; i++ )
        {
            Class<?> parameterType = invocation.getMethod().getParameterTypes()[i];

            CacheInvocationParameter parameter = new DefaultCacheInvocationParameter( parameterType,
                                                                                      invocation.getArguments()[i],
                                                                                      toAnnotationsSet( invocation.getMethod().getParameterAnnotations()[i] ),
                                                                                      i );

            for ( Annotation parameterAnnotation : invocation.getMethod().getParameterAnnotations()[i] )
            {
                if ( CacheKeyParam.class == parameterAnnotation.annotationType() )
                {
                    keyParametersList.add( parameter );
                }
                else if ( CacheValue.class == parameterAnnotation.annotationType() )
                {
                    if ( !cacheValueAllowed )
                    {
                        throw new CacheAnnotationConfigurationException( format( "CacheValue parameter annotation is not allowed on %s",
                                                                                 invocation.getMethod() ) );
                    }
                    else if ( valueParameter != null )
                    {
                        throw new CacheAnnotationConfigurationException( format( "Multiple CacheValue parameter annotations are not allowed on %s",
                                                                                 invocation.getMethod() ) );
                    }
                    else
                    {
                        valueParameter = parameter;
                    }
                }
            }

            allParameters[i] = parameter;
        }

        CacheInvocationParameter[] keyParameters;

        if ( keyParametersList.isEmpty() )
        {
            keyParameters = allParameters;
        }
        else
        {
            keyParameters = keyParametersList.toArray( new CacheInvocationParameter[keyParametersList.size()] );
        }

        return invoke( new DefaultCacheKeyInvocationContext<A>( injector,
                                                                cacheName,
                                                                target,
                                                                invocation.getMethod(),
                                                                allParameters,
                                                                keyParameters,
                                                                valueParameter,
                                                                methodAnnotations,
                                                                annotation ),
                       invocation );
    }

    protected abstract Object invoke( CacheInvocationContext<A> context, MethodInvocation invocation )
        throws Throwable;

    protected final CacheResolverFactory getCacheResolverFactory( CacheInvocationContext<A> context )
    {
        Class<? extends CacheResolverFactory> cacheKeyGeneratorType = getCacheResolverFactoryType( context.getCacheAnnotation() );

        if ( CacheResolverFactory.class != cacheKeyGeneratorType )
        {
            return injector.getInstance( cacheKeyGeneratorType );
        }

        CacheDefaults cacheDefaults = context.getTarget().getClass().getAnnotation( CacheDefaults.class );

        if ( cacheDefaults != null && CacheResolverFactory.class != cacheDefaults.cacheKeyGenerator() )
        {
            return injector.getInstance( cacheKeyGeneratorType );
        }

        return cacheResolverFactory;
    }

    protected final CacheKeyGenerator getCacheKeyGenerator( CacheInvocationContext<A> context )
    {
        Class<? extends CacheKeyGenerator> cacheKeyGeneratorType = getCacheKeyGeneratorType( context.getCacheAnnotation() );

        if ( CacheKeyGenerator.class != cacheKeyGeneratorType )
        {
            return injector.getInstance( cacheKeyGeneratorType );
        }

        CacheDefaults cacheDefaults = context.getTarget().getClass().getAnnotation( CacheDefaults.class );

        if ( cacheDefaults != null && CacheKeyGenerator.class != cacheDefaults.cacheKeyGenerator() )
        {
            return injector.getInstance( cacheKeyGeneratorType );
        }

        return cacheKeyGenerator;
    }

    @SuppressWarnings( "unchecked" )
    private static String getCacheName( Method method )
    {
        for ( Class<? extends Annotation> annotationType : asList( CachePut.class,
                                                                   CacheRemoveAll.class,
                                                                   CacheRemoveEntry.class,
                                                                   CacheResult.class ) )
        {
            if ( method.isAnnotationPresent( annotationType ) )
            {
                Annotation annotation = method.getAnnotation( annotationType );
                String cacheName;
                try
                {
                    cacheName = (String) annotationType.getMethod( "cacheName" ).invoke( annotation );
                }
                catch ( Exception e )
                {
                    // should not happen, all enlisted annotations have "cacheName()" method
                    cacheName = null;
                }

                if ( !isEmpty( cacheName ) )
                {
                    return cacheName;
                }
            }
        }

        if ( method.getDeclaringClass().isAnnotationPresent( CacheDefaults.class ) )
        {
            CacheDefaults cacheDefaults = method.getDeclaringClass().getAnnotation( CacheDefaults.class );
            if ( !isEmpty( cacheDefaults.cacheName() ) )
            {
                return cacheDefaults.cacheName();
            }
        }

        return method.toGenericString();
    }

    static <T extends Throwable> boolean include( T throwable,
                                                  Class<? extends T>[] includes,
                                                  Class<? extends T>[] excludes,
                                                  boolean includeBothEmpty )
    {
        boolean includedEmpty = isEmpty( includes );
        boolean excludedEmpty = isEmpty( excludes );

        if ( includedEmpty && excludedEmpty )
        {
            return includeBothEmpty;
        }

        boolean isAssignableFromIncludes = isAssignable( throwable, includes );
        boolean isAssignableFromExcludes = isAssignable( throwable, excludes );

        if ( includedEmpty )
        {
            return !isAssignableFromExcludes;
        }

        if ( excludedEmpty )
        {
            return isAssignableFromIncludes;
        }

        return isAssignableFromIncludes && !isAssignableFromExcludes;
    }

    private static <T extends Throwable> boolean isAssignable( T target, Class<? extends T>[] from )
    {
        for ( final Class<? extends T> throwable : from )
        {
            if ( throwable.isAssignableFrom( target.getClass() ) )
            {
                return true;
            }
        }

        return false;
    }

    private static final <T extends Throwable> boolean isEmpty( Class<? extends T>...types )
    {
        return types == null || types.length == 0;
    }

    private static boolean isEmpty( String value )
    {
        return value == null || value.length() == 0;
    }

    private static Set<Annotation> toAnnotationsSet( Annotation...annotations )
    {
        return unmodifiableSet( new LinkedHashSet<Annotation>( asList( annotations ) ) );
    }

    private static <A extends Annotation> Class<? extends CacheKeyGenerator> getCacheKeyGeneratorType( A annotation )
    {
        if ( CachePut.class.isInstance( annotation ) )
        {
            return ( (CachePut) annotation).cacheKeyGenerator();
        }
        else if ( CacheRemoveEntry.class.isInstance( annotation ) )
        {
            return ( (CacheRemoveEntry) annotation).cacheKeyGenerator();
        }
        else if ( CacheResult.class.isInstance( annotation ) )
        {
            return ( (CacheResult) annotation).cacheKeyGenerator();
        }

        // doesn't happen
        return null;
    }

    private static <A extends Annotation> Class<? extends CacheResolverFactory> getCacheResolverFactoryType( A annotation )
    {
        if ( CachePut.class.isInstance( annotation ) )
        {
            return ( (CachePut) annotation).cacheResolverFactory();
        }
        else if ( CacheRemoveAll.class.isInstance( annotation ) )
        {
            return ( (CacheRemoveAll) annotation).cacheResolverFactory();
        }
        else if ( CacheRemoveEntry.class.isInstance( annotation ) )
        {
            return ( (CacheRemoveEntry) annotation).cacheResolverFactory();
        }
        else if ( CacheResult.class.isInstance( annotation ) )
        {
            return ( (CacheResult) annotation).cacheResolverFactory();
        }

        // doesn't happen
        return null;
    }

}
