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

import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.util.Providers;

import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import java.util.Properties;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Module for configuring a single persistence unit.
 *
 * @see PersistenceModule
 */
class PersistenceUnitModule
    extends PrivateModule
{

    /**
     * The configuration for the persistence unit.
     */
    private final PersistenceUnitModuleConfiguration config;

    /**
     * Transaction interceptor for this persistence unit.
     */
    private final TxnInterceptor transactionInterceptor;

    /**
     * Container for adding this persistence unit.
     */
    private final AllPersistenceUnits allPersistenceUnits;

    /**
     * Constructor.
     *
     * @param configurator           the configuration holding all configs.
     * @param transactionInterceptor interceptor for the transactional annotation.
     * @param allPersistenceUnits    container holding all persistence units.
     */
    PersistenceUnitModule( PersistenceUnitModuleConfiguration configurator, TxnInterceptor transactionInterceptor,
                           AllPersistenceUnits allPersistenceUnits )
    {
        this.config = checkNotNull( configurator, "config is mandatory!" );
        this.transactionInterceptor = checkNotNull( transactionInterceptor, "transactionInterceptor is mandatory!" );
        this.allPersistenceUnits = checkNotNull( allPersistenceUnits, "allPersistenceUnits is mandatory!" );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure()
    {
        bind( AnnotationHolder.class ).toInstance( config.getAnnotationHolder() );

        bindPersistenceServiceAndEntityManagerFactoryProviderAndProperties();
        bindTransactionFacadeFactory();

        bind( EntityManagerProvider.class ).to( EntityManagerProviderImpl.class );
        bind( UnitOfWork.class ).to( EntityManagerProviderImpl.class );

        exposePersistenceServiceAndEntityManagerProviderAndUnitOfWork();

        // request injection into transaction interceptor - this adds the required dependencies to the interceptor.
        if ( transactionInterceptor != null )
        {
            requestInjection( transactionInterceptor );
        }

        allPersistenceUnits.add( getPersistenceKey(), getUnitOfWorkKey() );
    }

    /**
     * exposes the following interfaces (optionally annotated if an annotation is defined in the configuration).
     * <ul>
     * <li>{@link PersistenceService}</li>
     * <li>{@link EntityManagerProvider}</li>
     * <li>{@link UnitOfWork}</li>
     * </ul>
     */
    private void exposePersistenceServiceAndEntityManagerProviderAndUnitOfWork()
    {
        if ( config.isAnnotated() )
        {
            bindAndExposedAnnotated( PersistenceService.class );
            bindAndExposedAnnotated( EntityManagerProvider.class );
            bindAndExposedAnnotated( UnitOfWork.class );
        }
        else
        {
            expose( PersistenceService.class );
            expose( EntityManagerProvider.class );
            expose( UnitOfWork.class );
        }
    }

    /**
     * helper to expose a binding with annotation added.
     *
     * @param type the type to expose.
     * @param <T>  the type to expose.
     */
    private <T> void bindAndExposedAnnotated( Class<T> type )
    {
        bind( type ).annotatedWith( config.getAnnotation() ).to( Key.get( type ) );
        expose( type ).annotatedWith( config.getAnnotation() );
    }


    private Key<PersistenceService> getPersistenceKey()
    {
        if ( config.isAnnotated() )
        {
            return Key.get( PersistenceService.class, config.getAnnotation() );
        }
        else
        {
            return Key.get( PersistenceService.class );
        }
    }

    private Key<UnitOfWork> getUnitOfWorkKey()
    {
        if ( config.isAnnotated() )
        {
            return Key.get( UnitOfWork.class, config.getAnnotation() );
        }
        else
        {
            return Key.get( UnitOfWork.class );
        }
    }

    private void bindPersistenceServiceAndEntityManagerFactoryProviderAndProperties()
    {
        if ( config.isApplicationManagedPersistenceUnit() )
        {
            bindApplicationManagedPersistenceServiceAndEntityManagerFactoryProviderAndProperties();
        }
        else
        {
            bindContainerManagedPersistenceServiceAndEntityManagerFactoryProviderAndProperties();
        }
    }

    private void bindApplicationManagedPersistenceServiceAndEntityManagerFactoryProviderAndProperties()
    {
        bind( PersistenceService.class ).to( ApplicationManagedEntityManagerFactoryProvider.class );
        bind( EntityManagerFactoryProvider.class ).to( ApplicationManagedEntityManagerFactoryProvider.class );
        bind( Properties.class ).annotatedWith( ForContainerManaged.class ).toProvider(
            Providers.<Properties>of( null ) );
        bind( Properties.class ).annotatedWith( ForApplicationManaged.class ).toProvider(
            Providers.of( config.getProperties() ) );

        // required in ApplicationManagedEntityManagerFactoryProvider
        bind( EntityManagerFactoryFactory.class );
        // required in EntityManagerFactoryFactory
        bind( String.class ).annotatedWith( ForApplicationManaged.class ).toInstance( config.getPuName() );
    }

    private void bindContainerManagedPersistenceServiceAndEntityManagerFactoryProviderAndProperties()
    {
        bind( PersistenceService.class ).to( ContainerManagedEntityManagerFactoryProvider.class );
        bind( EntityManagerFactoryProvider.class ).to( ContainerManagedEntityManagerFactoryProvider.class );
        bind( Properties.class ).annotatedWith( ForContainerManaged.class ).toProvider(
            Providers.of( config.getProperties() ) );
        bind( Properties.class ).annotatedWith( ForApplicationManaged.class ).toProvider(
            Providers.<Properties>of( null ) );

        // required in ContainerManagedEntityManagerFactoryProvider
        bindEntityManagerFactorySource();
    }

    private void bindEntityManagerFactorySource()
    {
        if ( config.isEmfProvidedByJndiLookup() )
        {
            bind( EntityManagerFactorySource.class ).to( EntityManagerFactorySourceByJndiLookup.class );

            // required in EntityManagerFactorySourceByJndiLookup
            bind( String.class ).annotatedWith( ForContainerManaged.class ).toInstance( config.getEmfJndiName() );
        }
        else
        {
            bind( EntityManagerFactorySource.class ).to( EntityManagerFactorySourceViaProvider.class );

            // required in EntityManagerFactorySourceViaProvider
            bindInternalEntityManagerFactoryProvider();
        }
    }

    private void bindInternalEntityManagerFactoryProvider()
    {
        if ( config.isEmfProvidedByInstance() )
        {
            bind( EntityManagerFactory.class ).annotatedWith( ForContainerManaged.class ).toInstance( config.getEmf() );
        }
        else if ( config.isEmfProvidedByProvider() )
        {
            bind( EntityManagerFactory.class ).annotatedWith( ForContainerManaged.class ).toProvider(
                Providers.guicify( config.getEmfProvider() ) );
        }
        else if ( config.isEmfProvidedByProviderKey() )
        {
            bind( EntityManagerFactory.class ).annotatedWith( ForContainerManaged.class ).toProvider(
                config.getEmfProviderKey() );
        }
        else
        {
            throw new RuntimeException( "EntityManager is improperly configured" );
        }
    }

    private void bindTransactionFacadeFactory()
    {
        if ( config.isJta() )
        {
            bindJtaTransactionFacadeFactory();
        }
        else
        {
            bind( TransactionFacadeFactory.class ).to( ResourceLocalTransactionFacadeFactory.class );
        }
    }

    private void bindJtaTransactionFacadeFactory()
    {
        bind( TransactionFacadeFactory.class ).to( JtaTransactionFacadeFactory.class );

        // required in JtaTransactionFacadeFactory
        binInternalUserTransactionProvider();
    }

    private void binInternalUserTransactionProvider()
    {
        if ( config.isUserTransactionProvidedByInstance() )
        {
            bind( UserTransaction.class ).toInstance( config.getUserTransaction() );
        }
        else if ( config.isUserTransactionProvidedByJndiLookup() )
        {
            bind( UserTransaction.class ).toProvider( UserTransactionProviderByJndiLookup.class );

            // required in UserTransactionProviderByJndiLookup
            bind( String.class ).annotatedWith( UserTransactionJndiName.class ).toInstance( config.getUtJndiName() );
        }
        else if ( config.isUserTransactionProvidedByProvider() )
        {
            bind( UserTransaction.class ).toProvider( Providers.guicify( config.getUtProvider() ) );
        }
        else if ( config.isUserTransactionProvidedByProviderKey() )
        {
            bind( UserTransaction.class ).toProvider( config.getUtProviderKey() );
        }
        else
        {
            throw new RuntimeException( "UserTransaction is improperly configured" );
        }
    }

}
