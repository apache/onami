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

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Main module of the onami persist guice extension.
 */
public abstract class PersistenceModule
    extends AbstractModule
{

    /**
     * List of configurations. Each configurator can be used to build a {@link PersistenceUnitModule}.
     */
    private List<PersistenceUnitModuleConfiguration> configurations;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void configure()
    {
        if ( configurations != null )
        {
            throw new RuntimeException( "cannot reenter the configure method" );
        }
        try
        {
            configurations = new ArrayList<PersistenceUnitModuleConfiguration>();
            configurePersistenceUnits();
        }
        finally
        {
            configurations = null;
        }
    }

    /**
     * Configures the persistence units.
     */
    private void configurePersistenceUnits()
    {
        configurePersistence();

        bind( PersistenceFilter.class ).to( PersistenceFilterImpl.class );

        final AllPersistenceUnits allPersistenceUnits = new AllPersistenceUnits();
        bind( AllPersistenceServices.class ).toInstance( allPersistenceUnits );
        bind( AllUnitsOfWork.class ).toInstance( allPersistenceUnits );

        for ( PersistenceUnitModuleConfiguration config : configurations )
        {
            final TxnInterceptor txnInterceptor = new TxnInterceptor();

            install( new PersistenceUnitModule( config, txnInterceptor, allPersistenceUnits ) );

            bindInterceptor( any(), annotatedWith( Transactional.class ), txnInterceptor );
            bindInterceptor( annotatedWith( Transactional.class ), any(), txnInterceptor );
        }
    }

    /**
     * Configures the persistence units over the exposed methods.
     */
    protected abstract void configurePersistence();

    /**
     * Binds an application managed persistence unit.
     *
     * @param puName the name of the persistence unit as defined in the persistence.xml.
     * @return the next builder step.
     */
    protected UnannotatedPersistenceUnitBuilder bindApplicationManagedPersistenceUnit( String puName )
    {
        checkNotNull( configurations,
                      "calling bindApplicationManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfiguration configurator = createAndAddConfiguration();
        configurator.setPuName( puName );
        return configurator;
    }

    /**
     * Binds a container managed persistence unit for a given entity manager factory.
     *
     * @param emf the entity manager factory to use when creating new entity managers.
     * @return the next builder step.
     */
    protected UnannotatedPersistenceUnitBuilder bindContainerManagedPersistenceUnit( EntityManagerFactory emf )
    {
        checkNotNull( configurations,
                      "calling bindContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfiguration configurator = createAndAddConfiguration();
        configurator.setEmf( emf );
        return configurator;
    }

    /**
     * Binds a container managed persistence unit. The entity manager factory will be retrieved from the JNDI context.
     *
     * @param jndiName the JNDI name of the entity manager factory.
     * @return the next builder step.
     */
    protected UnannotatedPersistenceUnitBuilder bindContainerManagedPersistenceUnitWithJndiName( String jndiName )
    {
        checkNotNull( configurations,
                      "calling bindContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfiguration configurator = createAndAddConfiguration();
        configurator.setEmfJndiName( jndiName );
        return configurator;
    }

    /**
     * Binds a container managed persistence unit. The entity manager factory will be retrieved from the given provider.
     *
     * @param emfProvider the provider for the entity manager factory.
     * @return the next builder step.
     */
    protected UnannotatedPersistenceUnitBuilder bindContainerManagedPersistenceUnitProvidedBy(
        Provider<EntityManagerFactory> emfProvider )
    {
        checkNotNull( configurations,
                      "calling bindContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfiguration configurator = createAndAddConfiguration();
        configurator.setEmfProvider( emfProvider );
        return configurator;
    }

    /**
     * Binds a container managed persistence unit. The entity manager factory will be retrieved from the given provider.
     *
     * @param emfProviderClass the provider for the entity manager factory.
     * @return the next builder step.
     */
    protected UnannotatedPersistenceUnitBuilder bindContainerManagedPersistenceUnitProvidedBy(
        Class<? extends Provider<EntityManagerFactory>> emfProviderClass )
    {
        checkNotNull( configurations,
                      "calling bindContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfiguration configurator = createAndAddConfiguration();
        configurator.setEmfProviderClass( emfProviderClass );
        return configurator;
    }

    /**
     * Binds a container managed persistence unit. The entity manager factory will be retrieved from the given provider.
     *
     * @param emfProviderType the provider for the entity manager factory.
     * @return the next builder step.
     */
    protected UnannotatedPersistenceUnitBuilder bindContainerManagedPersistenceUnitProvidedBy(
        TypeLiteral<? extends Provider<EntityManagerFactory>> emfProviderType )
    {
        checkNotNull( configurations,
                      "calling bindContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfiguration configurator = createAndAddConfiguration();
        configurator.setEmfProviderType( emfProviderType );
        return configurator;
    }

    /**
     * Binds a container managed persistence unit. The entity manager factory will be retrieved from the given provider.
     *
     * @param emfProviderKey the provider for the entity manager factory.
     * @return the next builder step.
     */
    protected UnannotatedPersistenceUnitBuilder bindContainerManagedPersistenceUnitProvidedBy(
        Key<? extends Provider<EntityManagerFactory>> emfProviderKey )
    {
        checkNotNull( configurations,
                      "calling bindContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfiguration configuration = createAndAddConfiguration();
        configuration.setEmfProviderKey( emfProviderKey );
        return configuration;
    }

    private PersistenceUnitModuleConfiguration createAndAddConfiguration()
    {
        final PersistenceUnitModuleConfiguration configurator = new PersistenceUnitModuleConfiguration();
        configurations.add( configurator );
        return configurator;
    }
}
