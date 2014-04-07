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
 * Implementation of {@link PersistenceService} and {@link EntityManagerFactoryProvider} for
 * application managed persistence units.
 * <p/>
 * This class is a singleton and all methods of the {@link PersistenceService} interface are synchronized.
 */
@Singleton
class ApplicationManagedEntityManagerFactoryProvider
    implements EntityManagerFactoryProvider, PersistenceService
{

    /**
     * Factory for creating the {@link EntityManagerFactory}.
     */
    private final EntityManagerFactoryFactory emfFactory;

    /**
     * Currently active entity manager factory.
     * Is {@code null} when the persistence service is not running.
     */
    private EntityManagerFactory emf;

    /**
     * Constructor.
     *
     * @param emfFactory the factory for the  {@link EntityManagerFactory}. Must not be {@code null}.
     */
    @Inject
    ApplicationManagedEntityManagerFactoryProvider( EntityManagerFactoryFactory emfFactory )
    {
        this.emfFactory = checkNotNull( emfFactory, "emfFactory is mandatory!" );
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public EntityManagerFactory get()
    {
        if ( isRunning() )
        {
            return emf;
        }

        throw new IllegalStateException( "PersistenceService is not running." );
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public synchronized void start()
    {
        if ( isRunning() )
        {
            throw new IllegalStateException( "PersistenceService is already running." );
        }
        emf = emfFactory.createApplicationManagedEntityManagerFactory();
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public synchronized boolean isRunning()
    {
        return null != emf;
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public synchronized void stop()
    {
        if ( isRunning() )
        {
            try
            {
                emf.close();
            }
            finally
            {
                emf = null;
            }
        }
    }

}
