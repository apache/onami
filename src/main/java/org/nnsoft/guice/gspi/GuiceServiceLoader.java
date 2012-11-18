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
import static java.lang.Thread.currentThread;
import static org.nnsoft.guice.gspi.ServiceLoader.load;
import static org.nnsoft.guice.gspi.ServiceLoader.loadInstalled;

import java.util.Iterator;

import com.google.inject.Module;
import com.google.inject.ProvisionException;

/**
 * Loads Google Guice {@code Module} instances using the SPI pattern.
 */
public final class GuiceServiceLoader
{

    /**
     * Loads all Google Guice {@code Module} found in the Context {@code ClassLoader}.
     *
     * @return all Google Guice {@code Module} found in the Context {@code ClassLoader}.
     */
    public static Iterable<? extends Module> loadModules()
    {
        return loadModules( currentThread().getContextClassLoader() );
    }

    /**
     * Loads all Google Guice {@code Module} found in the given {@code ClassLoader}.
     *
     * @param classLoader The {@code ClassLoader} used to load {@code Module} instances.
     * @return all Google Guice {@code Module} found in the given {@code ClassLoader}.
     */
    public static Iterable<? extends Module> loadModules( ClassLoader classLoader )
    {
        return wrap( load( Module.class, classLoader ).iterator() );
    }

    /**
     * Loads all Google Guice {@code Module} found in the extension {@code ClassLoader}.
     *
     * @return all Google Guice {@code Module} found in the extension {@code ClassLoader}.
     */
    public static Iterable<? extends Module> loadInstalledModules()
    {
        return wrap( loadInstalled( Module.class ).iterator() );
    }

    /**
     * Wraps the {@code Module} types iterator in a lazy-loader {@code Module} instances iterator.
     *
     * @param moduleTypesIterator the {@code Module} types iterator has to be wrapped.
     * @return a lazy-loader {@code Module} instances iterator.
     */
    private static Iterable<Module> wrap( final Iterator<Class<? extends Module>> moduleTypesIterator )
    {
        return new Iterable<Module>()
        {

            /**
             * {@inheritDoc}
             */
            public Iterator<Module> iterator()
            {
                return new ModulesIterator( moduleTypesIterator );
            }

        };
    }

    /**
     * Lazy-loader {@code Module} instances iterator.
     */
    private static final class ModulesIterator
        implements Iterator<Module>
    {

        /**
         * The adapted {@code Module} types iterator.
         */
        private final Iterator<Class<? extends Module>> moduleTypesIterator;

        /**
         * Creates a new lazy-loader {@code Module} instances iterator
         * wrapping a {@code Module} types iterator..
         *
         * @param moduleTypesIterator The adapted {@code Module} types iterator.
         */
        public ModulesIterator( Iterator<Class<? extends Module>> moduleTypesIterator )
        {
            this.moduleTypesIterator = moduleTypesIterator;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext()
        {
            return moduleTypesIterator.hasNext();
        }

        /**
         * {@inheritDoc}
         */
        public Module next()
        {
            Class<? extends Module> moduleClass = moduleTypesIterator.next();
            try
            {
                return moduleClass.newInstance();
            }
            catch ( Exception e )
            {
                throw new ProvisionException( format( "Provider '%s' could not be instantiated",
                                                      moduleClass.getName(), e.getMessage() ) );
            }
        }

        /**
         * {@inheritDoc}
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

    }

}
