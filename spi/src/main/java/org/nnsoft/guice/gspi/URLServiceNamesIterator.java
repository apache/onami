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

import static java.nio.charset.Charset.forName;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * @param <S> The type of the service to be loaded by this loader.
 */
final class URLServiceNamesIterator<S>
    extends AbstractServiceClassIterator<S>
{

    /**
     * The default <code>UTF-8</code> character encoding.
     */
    private static final Charset UTF_8 = forName( "UTF-8" );

    /**
     * The SPI files to be loaded.
     */
    private final Enumeration<URL> serviceResources;

    /**
     * @param service the class or interface representing the service being loaded.
     * @param classLoader the class loader used to locate, load, and instantiate providers.
     * @param serviceResources
     * @param providerTypes cached providers types, in instantiation order.
     */
    public URLServiceNamesIterator( Class<S> service, ClassLoader classLoader, Enumeration<URL> serviceResources )
    {
        super( service, classLoader );
        this.serviceResources = serviceResources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasMorePendingNames()
    {
        return serviceResources.hasMoreElements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<String> getPendingNames()
    {
        return parseServiceFile( serviceResources.nextElement() );
    }

    /**
     * Parse the content of the given URL as a provider-configuration file.
     *
     * Method taken from Apache Commons-Digester.
     *
     * @param url the URL naming the configuration file to be parsed.
     * @return a (possibly empty) iterator that will yield the provider-class names in the given configuration file that
     *         are not yet members of the returned set
     */
    private Iterator<String> parseServiceFile( URL url )
    {
        final List<String> results = new ArrayList<String>();

        try
        {
            final InputStream input = url.openStream();
            try
            {
                final BufferedReader reader = new BufferedReader( new InputStreamReader( input, UTF_8 ) );

                try
                {
                    String serviceImplName;
                    while ( ( serviceImplName = reader.readLine() ) != null )
                    {
                        int idx = serviceImplName.indexOf( '#' );
                        if ( idx >= 0 )
                        {
                            serviceImplName = serviceImplName.substring( 0, idx );
                        }
                        serviceImplName = serviceImplName.trim();

                        if ( serviceImplName.length() != 0 )
                        {
                            results.add( serviceImplName );
                        }
                    }
                }
                finally
                {
                    closeQuietly( reader );
                }
            }
            finally
            {
                closeQuietly( input );
            }
        }
        catch ( IOException e )
        {
            // ignore
        }

        return results.iterator();
    }

    /**
     * Unconditionally close a {@link Closeable} element.
     *
     * @param closeable the {@link Closeable} element.
     */
    private static void closeQuietly( Closeable closeable )
    {
        if ( closeable != null )
        {
            try
            {
                closeable.close();
            }
            catch ( IOException e )
            {
                // close quietly
            }
        }
    }

}
