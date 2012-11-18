package org.nnsoft.guice.gache;

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
