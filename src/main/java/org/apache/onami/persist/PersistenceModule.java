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
import com.google.inject.matcher.Matcher;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Main module of the onami persist guice extension.
 * <p/>
 * Add persistence unit using the methods
 * <ul>
 * <li>{@link #addApplicationManagedPersistenceUnit(String)}</li>
 * <li>{@link #addContainerManagedPersistenceUnitWithJndiName(String)}</li>
 * <li>{@link #addContainerManagedPersistenceUnit(EntityManagerFactory)}</li>
 * <li>{@link #addContainerManagedPersistenceUnitProvidedBy(Provider<EntityManagerFactory>)}</li>
 * </ul>
 */
public abstract class PersistenceModule
    extends AbstractModule
{

    private List<PersistenceUnitModuleConfigurator> configurators;

    private final PersistenceUnitContainer container = new PersistenceUnitContainer();

    private final Matcher<AnnotatedElement> transactionalMatcher = annotatedWith( Transactional.class );

    private final Matcher<Object> anyMatcher = any();

    @Override
    protected final void configure()
    {
        if ( configurators != null )
        {
            throw new RuntimeException( "cannot reenter the configure method" );
        }
        try
        {
            configurators = new ArrayList<PersistenceUnitModuleConfigurator>();
            doConfigure();
        }
        finally
        {
            configurators = null;
        }
    }

    private void doConfigure()
    {
        configurePersistence();

        for ( PersistenceUnitModuleConfigurator config : configurators )
        {
            final PersistenceUnitModule pu = config.createPuModule();
            final TxnInterceptor txnInterceptor = new TxnInterceptor();
            pu.setPersistenceUnitContainer( container );
            pu.setTransactionInterceptor( txnInterceptor );

            install( pu );

            bindInterceptor( anyMatcher, transactionalMatcher, txnInterceptor );
            bindInterceptor( transactionalMatcher, anyMatcher, txnInterceptor );
        }
    }

    protected abstract void configurePersistence();

    protected UnannotatedPersistenceUnitBuilder addApplicationManagedPersistenceUnit( String puName )
    {
        checkNotNull( configurators,
                      "calling addApplicationManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfigurator configurator = createAndAddConfigurator();
        configurator.setPuName( puName );
        return configurator;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnit( EntityManagerFactory emf )
    {
        checkNotNull( configurators,
                      "calling addContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfigurator configurator = createAndAddConfigurator();
        configurator.setEmf( emf );
        return configurator;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitWithJndiName( String jndiName )
    {
        checkNotNull( configurators,
                      "calling addContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfigurator configurator = createAndAddConfigurator();
        configurator.setEmfJndiName( jndiName );
        return configurator;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitProvidedBy(
        Provider<EntityManagerFactory> emfProvider )
    {
        checkNotNull( configurators,
                      "calling addContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfigurator configurator = createAndAddConfigurator();
        configurator.setEmfProvider( emfProvider );
        return configurator;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitProvidedBy(
        Class<? extends Provider<EntityManagerFactory>> emfProviderClass )
    {
        checkNotNull( configurators,
                      "calling addContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfigurator configurator = createAndAddConfigurator();
        configurator.setEmfProviderClass( emfProviderClass );
        return configurator;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitProvidedBy(
        TypeLiteral<? extends Provider<EntityManagerFactory>> emfProviderType )
    {
        checkNotNull( configurators,
                      "calling addContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfigurator configurator = createAndAddConfigurator();
        configurator.setEmfProviderType( emfProviderType );
        return configurator;
    }

    protected UnannotatedPersistenceUnitBuilder addContainerManagedPersistenceUnitProvidedBy(
        Key<? extends Provider<EntityManagerFactory>> emfProviderKey )
    {
        checkNotNull( configurators,
                      "calling addContainerManagedPersistenceUnit outside of configurePersistence is not supported" );
        final PersistenceUnitModuleConfigurator configurator = createAndAddConfigurator();
        configurator.setEmfProviderKey( emfProviderKey );
        return configurator;
    }

    private PersistenceUnitModuleConfigurator createAndAddConfigurator()
    {
        final PersistenceUnitModuleConfigurator configurator = new PersistenceUnitModuleConfigurator();
        configurators.add( configurator );
        return configurator;
    }
}
