package org.apache.onami.persist.newapi;

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

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import javax.inject.Provider;
import javax.persistence.EntityManagerFactory;

public abstract class PersistenceModule
    extends AbstractModule
{

    @Override
    protected final void configure()
    {
        configurePersistence();
    }

    protected abstract void configurePersistence();

    protected UnannotatedPersistenceUnitBuilder addApplicationManagedPersistenceUnit( String puName )
    {
        // TODO
        return null;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitWithJndiName( String jndiName )
    {
        // TODO
        return null;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitProvidedBy(
        Provider<EntityManagerFactory> emfProvider )
    {
        // TODO
        return null;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitProvidedBy(
        Class<? extends Provider<EntityManagerFactory>> emfProviderType )
    {
        // TODO
        return null;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitProvidedBy(
        TypeLiteral<? extends Provider<EntityManagerFactory>> emfProviderType )
    {
        // TODO
        return null;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitProvidedBy(
        Key<? extends Provider<EntityManagerFactory>> emfProviderType )
    {
        // TODO
        return null;
    }
}
