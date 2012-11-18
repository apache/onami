package org.nnsoft.guice.gspi;

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
import static com.google.inject.Key.get;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static java.lang.Thread.currentThread;
import static org.nnsoft.guice.gspi.ServiceLoader.load;

import java.lang.annotation.Annotation;
import java.util.Iterator;

import org.nnsoft.guice.gspi.binder.AnnotatedServiceBuilder;
import org.nnsoft.guice.gspi.binder.FromClassLoaderBuilder;
import org.nnsoft.guice.gspi.binder.ServiceBuilder;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.multibindings.Multibinder;

/**
 * A Google Guice {@code Module} to simplify the task of
 * binding Services to Providers using the SPI pattern.
 */
public abstract class ServiceLoaderModule
    extends AbstractModule
{

    /**
     * EDSL to bind Services to Providers using the SPI pattern.
     *
     * @param service The type of the service to be loaded.
     * @return the chained EDSL builder.
     */
    protected final <S> FromClassLoaderBuilder bindService( Class<S> service )
    {
        checkArgument( service != null, "Impossible to bind null service class!" );
        return new DefaultServiceBuilder<S>( service, binder() );
    }

    /**
     * EDSL for SPI implementation.
     *
     * @param <S> The type of the service to be loaded.
     */
    private static final class DefaultServiceBuilder<S>
        implements FromClassLoaderBuilder
    {

        private final Class<S> service;

        private final Binder binder;

        private ClassLoader classLoader;

        private Key<S> bindingKey;

        public DefaultServiceBuilder( Class<S> service, Binder binder )
        {
            this.service = service;
            this.binder = binder;

            bindingKey = get( service );
            classLoader = currentThread().getContextClassLoader();
        }

        /**
         * {@inheritDoc}
         */
        public ServiceBuilder annotatedWith( Class<? extends Annotation> annotationType )
        {
            bindingKey = get( service, annotationType );
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public ServiceBuilder annotatedWith( Annotation annotation )
        {
            bindingKey = get( service, annotation );
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public AnnotatedServiceBuilder fromClassLoader( ClassLoader classLoader )
        {
            checkArgument( classLoader != null,
                           "Impossible to load Service %s with a null ClassLoader", service.getName() );
            this.classLoader = classLoader;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public void loadingFirstService()
        {
            Iterator<Class<? extends S>> servicesIterator = load( service, classLoader ).iterator();
            if ( !servicesIterator.hasNext() )
            {
                throw new ProvisionException( format( "No Provider found for Service %s", service.getName() ) );
            }
            binder.bind( bindingKey ).to( servicesIterator.next() );
        }

        /**
         * {@inheritDoc}
         */
        public void loadingAllServices()
        {
            Multibinder<S> multiBinder;

            if ( bindingKey.getAnnotation() != null )
            {
                multiBinder = newSetBinder( binder, service, bindingKey.getAnnotation() );
            }
            else if ( bindingKey.getAnnotationType() != null )
            {
                multiBinder = newSetBinder( binder, service, bindingKey.getAnnotationType() );
            }
            else
            {
                multiBinder = newSetBinder( binder, service );
            }

            Iterator<Class<? extends S>> serviceProviders = load( service, classLoader ).iterator();
            while ( serviceProviders.hasNext() )
            {
                multiBinder.addBinding().to( serviceProviders.next() );
            }
        }

    }

}
