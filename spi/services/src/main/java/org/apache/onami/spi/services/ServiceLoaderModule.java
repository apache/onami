package org.apache.onami.spi.services;

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

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.apache.onami.spi.core.ServiceLoader.load;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Qualifier;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.ProvisionException;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;

/**
 * A Google Guice {@code Module} to simplify the task of
 * binding Services to Providers using the SPI pattern.
 */
public abstract class ServiceLoaderModule
    extends AbstractModule
{

    private List<ServiceInfo<?>> services = new LinkedList<ServiceInfo<?>>();

    @Override
    protected final void configure()
    {
        if ( !services.isEmpty() )
        {
            throw new IllegalStateException( "Re-entry is not allowed." );
        }

        configureServices();

        try
        {
            for ( ServiceInfo<?> builder : services )
            {
                bindService( builder );
            }
        }
        finally
        {
            services.clear();
        }
    }

    private <S> void bindService( ServiceInfo<S> serviceInfo )
    {
        Class<S> serviceType = serviceInfo.getServiceType();

        Iterator<Class<? extends S>> servicesIterator = load( serviceType, serviceInfo.getClassLoader() )
                                                        .iterator();
        boolean found = false;
        while ( servicesIterator.hasNext() )
        {
            if ( !found )
            {
                found = true;
            }

            Class<? extends S> serviceImplType = servicesIterator.next();

            bindService( serviceType, serviceImplType );
        }

        if ( !found )
        {
            throw new ProvisionException( format( "No Provider found for Service %s", serviceType.getName() ) );
        }
    }

    private <S> void bindService( Class<S> serviceType, Class<? extends S> serviceImplType )
    {
        AnnotatedBindingBuilder<S> annotatedBindingBuilder = bind( serviceType );
        LinkedBindingBuilder<S> linkedBindingBuilder = annotatedBindingBuilder;

        dance: for ( Annotation annotation : serviceImplType.getAnnotations() )
        {
            Class<? extends Annotation> annotationType = annotation.annotationType();

            /*
             * if the serviceImplType is a javax.inject.Qualifier annotation
             * or
             * if the serviceImplType is a com.google.inject.BindingAnnotation
             */
            if ( annotationType.isAnnotationPresent( Qualifier.class )
                 || annotationType.isAnnotationPresent( BindingAnnotation.class ) )
            {
                linkedBindingBuilder = annotatedBindingBuilder.annotatedWith( annotation );
                break dance;
            }
        }

        linkedBindingBuilder.to( serviceImplType );
    }

    protected abstract void configureServices();

    /**
     * EDSL to bind Services to Providers using the SPI pattern.
     *
     * @param service The type of the service to be loaded.
     * @return the chained EDSL builder.
     */
    protected final <S> FromClassLoaderBuilder discover( Class<S> service )
    {
        checkArgument( service != null, "Impossible to bind null service class!" );
        ServiceInfo<S> builder = new ServiceInfo<S>( service );
        services.add( builder );
        return builder;
    }

    /**
     * EDSL for SPI implementation.
     *
     * @param <S> The type of the service to be loaded.
     */
    private static final class ServiceInfo<S>
        implements FromClassLoaderBuilder
    {

        private final Class<S> serviceType;

        private ClassLoader classLoader;

        public ServiceInfo( Class<S> serviceType )
        {
            this.serviceType = serviceType;
            classLoader = currentThread().getContextClassLoader();
        }

        /**
         * {@inheritDoc}
         */
        public void fromClassLoader( ClassLoader classLoader )
        {
            checkArgument( classLoader != null,
                           "Impossible to load Service %s with a null ClassLoader", serviceType.getName() );
            this.classLoader = classLoader;
        }

        public Class<S> getServiceType()
        {
            return serviceType;
        }

        public ClassLoader getClassLoader()
        {
            return classLoader;
        }

    }

    private static void checkArgument( boolean expression, String errorMessagePattern, Object...args )
    {
        if ( !expression )
        {
            throw new IllegalArgumentException( format( errorMessagePattern, args ) );
        }
    }

}
