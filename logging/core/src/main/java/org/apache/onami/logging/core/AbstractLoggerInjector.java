package org.apache.onami.logging.core;

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
import static java.lang.reflect.Modifier.isFinal;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.google.inject.MembersInjector;
import com.google.inject.ProvisionException;

/**
 * The abstract Logger injector implementation, takes care of injecting the
 * concrete Logger implementation to the logged filed.
 */
public abstract class AbstractLoggerInjector<L>
    implements MembersInjector<L>
{

    /**
     * The logger field has to be injected.
     */
    private final Field field;

    /**
     * Creates a new Logger injector.
     *
     * @param field the logger field has to be injected.
     */
    public AbstractLoggerInjector( final Field field )
    {
        this.field = field;
        AccessController.doPrivileged( new PrivilegedAction<Void>()
        {

            public Void run()
            {
                field.setAccessible( true );
                return null;
            }

        } );
    }

    /**
     * {@inheritDoc}
     */
    public final void injectMembers( Object target )
    {
        if ( isFinal( field.getModifiers() ) )
        {
            return;
        }

        try
        {
            if ( field.get( target ) == null )
            {
                field.set( target, createLogger( target.getClass() ) );
            }
        }
        catch ( Exception e )
        {
            throw new ProvisionException( format( "Impossible to set logger for field '%s', see nested exception: %s",
                                                  field, e.getMessage() ) );
        }
    }

    /**
     * Creates a new Logger implementation for the specified Class.
     *
     * @return a new Logger implementation.
     */
    protected abstract L createLogger(Class<?> klass);

}
