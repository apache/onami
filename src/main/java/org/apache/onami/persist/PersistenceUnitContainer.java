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

import java.util.HashSet;
import java.util.Set;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Container of persistence units. This is a convenience wrapper for multiple
 * persistence units.
 */
@Singleton
class PersistenceUnitContainer
    implements AllPersistenceServices, AllUnitsOfWork
{

    /**
     * Collection of all known persistence services.
     */
    private final Set<PersistenceService> persistenceServices = new HashSet<PersistenceService>();

    /**
     * Collection of all known units of work.
     */
    private final Set<UnitOfWork> unitsOfWork = new HashSet<UnitOfWork>();

    /**
     * Adds a persistence service and a unit of work to this container.
     *
     * @param ps  the persistence service to add. Must not be {@code null}.
     * @param uow the unit of work to add. Must not be {@code null}.
     */
    @Inject
    void add( PersistenceService ps, UnitOfWork uow )
    {
        checkNotNull( ps );
        checkNotNull( uow );
        persistenceServices.add( ps );
        unitsOfWork.add( uow );
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
                if(! ps.isRunning())
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
    public void stopAllRunningPersistenceServices()
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
                if(! unitOfWork.isActive())
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
