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

import com.google.inject.Singleton;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Helper class which does a JNDI lookup and handles exceptions.
 */
@Singleton
public class JndiLookupHelper
{

    /**
     * Does the actual JNDI lookup.
     *
     * @param type     type of the object to lookup
     * @param jndiName name of the object to lookup
     * @param <T>      type of the object to lookup
     * @return the object provided by the JNDI context.
     */
    @SuppressWarnings( "unchecked" )
    <T> T doJndiLookup( Class<T> type, String jndiName )
    {
        try
        {
            final InitialContext ctx = new InitialContext();
            final T result = (T) ctx.lookup( jndiName );

            Preconditions.checkNotNull( result, "lookup for " + type.getSimpleName() + " with JNDI name '" + jndiName
                + "' returned null" );

            return result;
        }
        catch ( NamingException e )
        {
            throw new RuntimeException(
                "lookup for " + type.getSimpleName() + " with JNDI name '" + jndiName + "' failed", e );
        }
    }
}
