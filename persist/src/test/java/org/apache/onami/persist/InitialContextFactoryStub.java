package org.apache.onami.persist;

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

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * JNDI Context Factory for test.
 */
public class InitialContextFactoryStub
    implements InitialContextFactory
{

    private static final ThreadLocal<Context> THREAD_LOCAL_CONTEXT = new ThreadLocal<Context>();

    static
    {
        System.setProperty( Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryStub.class.getName() );
    }

    /**
     * {@inheritDoc}
     */
    public Context getInitialContext( Hashtable<?, ?> environment )
        throws NamingException
    {
        final Context context = THREAD_LOCAL_CONTEXT.get();
        if ( context == null )
        {
            throw new NamingException( "No context registered" );
        }
        return context;
    }

    /**
     * Registers a context which will be returned when a lookup in the same thread is done.
     *
     * @param context the context to return for lookups made by the same thread.
     */
    public static void registerContext( Context context )
    {
        THREAD_LOCAL_CONTEXT.set( context );
    }

}
