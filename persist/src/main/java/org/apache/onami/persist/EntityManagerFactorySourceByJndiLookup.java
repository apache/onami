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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.persistence.EntityManagerFactory;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Source for {@link javax.persistence.EntityManagerFactory}.
 * The sourced instance is looked up via a JNDI call.
 */
@Singleton
class EntityManagerFactorySourceByJndiLookup
    implements EntityManagerFactorySource
{

    /**
     * The JNDI name of the persistence unit.
     */
    private final String jndiName;

    /**
     * Helper for JNDI lookup.
     */
    private final JndiLookupHelper jndiLookupHelper;

    /**
     * Constructor.
     *
     * @param jndiName         jndi name of the entity manager factory. Must not be {@code null}.
     * @param jndiLookupHelper the lookup helper. Must not be {@code null}.
     */
    @Inject
    EntityManagerFactorySourceByJndiLookup( @ForContainerManaged String jndiName, JndiLookupHelper jndiLookupHelper )
    {
        this.jndiName = checkNotNull( jndiName, "jndiName is mandatory!" );
        this.jndiLookupHelper = checkNotNull( jndiLookupHelper, "jndiLookupHelper is mandatory!" );
    }

    /**
     * Gets a {@link javax.persistence.EntityManagerFactory} by looking it up in the JNDI context.
     *
     * @return the found entity manager factory
     * @throws RuntimeException when no entity manager factory was found.
     */
    //@Override
    public EntityManagerFactory getEntityManagerFactory()
    {
        return jndiLookupHelper.doJndiLookup( EntityManagerFactory.class, jndiName );
    }

}
