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

import static java.lang.System.getProperty;
import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import com.google.inject.ProvisionException;

/**
 * A simple service-provider loading facility.
 *
 * @param <S> The type of the service to be loaded by this loader.
 */
final class ServiceLoader<S>
    implements Iterable<Class<? extends S>>
{

    private static final String SERVICE_PREFIX = "META-INF/services/%s";

    /**
     * Creates a new service loader for the given service type, using the current thread's
     * {@linkplain java.lang.Thread#getContextClassLoader context class loader}.
     *
     * @param <S> The type of the service to be loaded by this loader.
     * @param service The class or interface representing the service being loaded.
     * @return A new service loader.
     */
    public static <S> ServiceLoader<S> load( Class<S> service )
    {
        return load( service, currentThread().getContextClassLoader() );
    }

    /**
     * Creates a new service loader for the given service type and class loader.
     *
     * @param <S> The type of the service to be loaded by this loader.
     * @param service The class or interface representing the service being loaded.
     * @param classLoader The class loader used to locate, load, and instantiate providers.
     * @return A new service loader.
     */
    public static <S> ServiceLoader<S> load( Class<S> service, ClassLoader classLoader )
    {
        checkArgument( service != null, "Parameter 'service' must not be null" );
        checkArgument( classLoader != null, "Parameter 'classLoader' must not be null" );

        return new ServiceLoader<S>( service, classLoader );
    }

    /**
     * Creates a new service loader for the given service type, using the extension class loader.
     *
     * @param <S> The type of the service to be loaded by this loader.
     * @param service The class or interface representing the service being loaded.
     * @return A new service loader.
     */
    public static <S> ServiceLoader<S> loadInstalled( Class<S> service )
    {
        ClassLoader parent = getSystemClassLoader();
        ClassLoader current = null;
        while ( parent != null )
        {
            current = parent;
            parent = parent.getParent();
        }
        return load( service, current );
    }

    /**
     * The class or interface representing the service being loaded.
     */
    private final String serviceName;

    /**
     * The current lazy-lookup class iterator.
     */
    private Iterator<Class<? extends S>> serviceClassIterator;

    /**
     * This class can't be instantiate directly, use static methods instead.
     *
     * @param service the class or interface representing the service being loaded.
     * @param classLoader the class loader used to locate, load, and instantiate providers.
     */
    private ServiceLoader( Class<S> service, ClassLoader classLoader )
    {
        serviceName = service.getName();

        String systemServiceName = getProperty( serviceName );

        if ( systemServiceName != null )
        {
            serviceClassIterator = new PropertyServiceClassIterator<S>( service, classLoader, systemServiceName );
        }
        else
        {
            String fullName = format( SERVICE_PREFIX, serviceName );
            try
            {
                Enumeration<URL> serviceResources = classLoader.getResources( fullName );
                serviceClassIterator = new URLServiceNamesIterator<S>( service, classLoader, serviceResources );
            }
            catch ( IOException e )
            {
                throw new ProvisionException( format( "An error occurred while loading '%s' Service resource(s) from classpath: %s",
                                                      fullName, e.getMessage() ) );
            }
        }
    }

    /**
     * Returns an iterator over a set of elements of type {@code Class<? extends S>}.
     *
     * @return an iterator over a set of elements of type {@code Class<? extends S>}.
     */
    public Iterator<Class<? extends S>> iterator()
    {
        return serviceClassIterator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return format( "%s[%s]", getClass().getSimpleName(), serviceName );
    }

}
