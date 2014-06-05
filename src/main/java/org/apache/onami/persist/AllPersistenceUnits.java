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
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * All persistence units. This is a convenience wrapper for multiple persistence units.
 */
@Singleton
class AllPersistenceUnits
    implements AllPersistenceServices, AllUnitsOfWork
{

    /**
     * Collection of all known persistence services.
     */
    private final List<PersistenceService> persistenceServices = new ArrayList<PersistenceService>();

    /**
     * Collection of all known units of work.
     */
    private final List<UnitOfWork> unitsOfWork = new ArrayList<UnitOfWork>();

    /**
     * Collection of the keys of all known persistence services.
     */
    private final Set<Key<PersistenceService>> persistenceServiceKeys = new HashSet<Key<PersistenceService>>();

    /**
     * Collection of the keys of of all known units of work.
     */
    private final Set<Key<UnitOfWork>> unitOfWorkKeys = new HashSet<Key<UnitOfWork>>();

    /**
     * Adds a persistence service and a unit of work to this collection.
     *
     * @param psKey  the persistence service to add. Must not be {@code null}.
     * @param uowKey the unit of work to add. Must not be {@code null}.
     */
    void add( Key<PersistenceService> psKey, Key<UnitOfWork> uowKey )
    {
        persistenceServiceKeys.add( checkNotNull( psKey, "psKey is mandatory!" ) );
        unitOfWorkKeys.add( checkNotNull( uowKey, "ouwKey is mandatory!" ) );
    }

    @Inject
    private void init( Injector injector )
    {
        for ( Key<PersistenceService> persistenceServiceKey : persistenceServiceKeys )
        {
            persistenceServices.add( injector.getInstance( persistenceServiceKey ) );
        }
        for ( Key<UnitOfWork> unitOfWorkKey : unitOfWorkKeys )
        {
            unitsOfWork.add( injector.getInstance( unitOfWorkKey ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public void startAllStoppedPersistenceServices()
    {
        AggregatedException.Builder exceptionBuilder = new AggregatedException.Builder();
        for ( PersistenceService ps : persistenceServices )
        {
            try
            {
                if ( !ps.isRunning() )
                {
                    ps.start();
                }
            }
            catch ( Exception e )
            {
                exceptionBuilder.add( e );
            }
        }
        exceptionBuilder.throwRuntimeExceptionIfHasCauses(
            "multiple exception occurred while starting the persistence service" );
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public void stopAllPersistenceServices()
    {
        AggregatedException.Builder exceptionBuilder = new AggregatedException.Builder();
        for ( PersistenceService ps : persistenceServices )
        {
            try
            {
                ps.stop();
            }
            catch ( Exception e )
            {
                exceptionBuilder.add( e );
            }
        }
        exceptionBuilder.throwRuntimeExceptionIfHasCauses(
            "multiple exception occurred while stopping the persistence service" );
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public void beginAllInactiveUnitsOfWork()
    {
        AggregatedException.Builder exceptionBuilder = new AggregatedException.Builder();
        for ( UnitOfWork unitOfWork : unitsOfWork )
        {
            try
            {
                if ( !unitOfWork.isActive() )
                {
                    unitOfWork.begin();
                }
            }
            catch ( Exception e )
            {
                exceptionBuilder.add( e );
            }
        }
        exceptionBuilder.throwRuntimeExceptionIfHasCauses(
            "multiple exception occurred while starting the unit of work" );
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public void endAllUnitsOfWork()
    {
        AggregatedException.Builder exceptionBuilder = new AggregatedException.Builder();
        for ( UnitOfWork unitOfWork : unitsOfWork )
        {
            try
            {
                unitOfWork.end();
            }
            catch ( Exception e )
            {
                exceptionBuilder.add( e );
            }
        }
        exceptionBuilder.throwRuntimeExceptionIfHasCauses(
            "multiple exception occurred while ending the unit of work" );
    }

}
