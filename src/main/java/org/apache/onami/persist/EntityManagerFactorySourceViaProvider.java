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
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.persistence.EntityManagerFactory;

/**
 * Source for {@link javax.persistence.EntityManagerFactory}.
 * The sourced instance is provided by guice.
 */
@Singleton
class EntityManagerFactorySourceViaProvider
    implements EntityManagerFactorySource
{
    private final Provider<EntityManagerFactory> emfProvider;

    /**
     * Constructor.
     *
     * @param emfProvider the provider which gives access to the instance coming from the container.
     */
    @Inject
    public EntityManagerFactorySourceViaProvider( @ForContainerManaged Provider<EntityManagerFactory> emfProvider )
    {
        this.emfProvider = emfProvider;
    }

    /**
     * {@inheritDoc}
     */
    //@Override
    public EntityManagerFactory getEntityManagerFactory()
    {
        return emfProvider.get();
    }
}
