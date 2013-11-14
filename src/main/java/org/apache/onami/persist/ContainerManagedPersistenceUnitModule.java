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

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Persistence module for an container managed persistence unit.
 * <p/>
 * Use the {@link PersistenceUnitBuilder} to configure an instance of this class.
 * <p/>
 * This is a private module which will expose the following bindings:
 * <ul>
 * <li>{@link UnitOfWork}</li>
 * <li>{@link EntityManagerProvider}</li>
 * <li>{@link PersistenceService}</li>
 * </ul>
 * If an annotation has been defined for this module the above classes are exposed with this
 * annotation. Within the private module the above classes are also binded without any annotation.
 * <p/>
 * You can extend this class and override {@link #configurePersistence()} to bind and expose
 * additional classes within this private module. This is useful if you require injection of the
 * above classes without annotation.
 */
public class ContainerManagedPersistenceUnitModule
    extends AbstractPersistenceUnitModule
{

    // ---- Members

    /**
     * Provider for {@link EntityManagerFactory}
     */
    private final ContainerManagedEntityManagerFactoryProvider emfProvider;

    // ---- Constructors

    /**
     * Constructor.
     *
     * @param emfJndiName the JNDI name of the {@link EntityManagerFactory}.
     */
    public ContainerManagedPersistenceUnitModule( String emfJndiName )
    {
        this( emfJndiName, new Properties() );
    }

    /**
     * Constructor.
     *
     * @param emfJndiName the JNDI name of the {@link EntityManagerFactory}. Must not be {@code null}.
     * @param properties  the additional properties. Theses override the ones defined in the persistence.xml. Must not be {@code null}.
     */
    public ContainerManagedPersistenceUnitModule( String emfJndiName, Properties properties )
    {
        this( new ContainerManagedEntityManagerFactoryProvider( emfJndiName ), properties );
        checkNotNull( emfJndiName );
        checkNotNull( properties );
    }

    /**
     * Constructor.
     *
     * @param emfProvider the provider for {@link EntityManagerFactory}.
     * @param properties  the additional properties. Theses override the ones defined in the persistence.xml.
     */
    private ContainerManagedPersistenceUnitModule( ContainerManagedEntityManagerFactoryProvider emfProvider,
                                                   Properties properties )
    {
        super( new EntityManagerProviderImpl( emfProvider, properties ) );
        this.emfProvider = emfProvider;
    }

    // ---- Methods

    /**
     * {@inheritDoc}
     */
    @Override
    PersistenceService getPersistenceService()
    {
        return emfProvider;
    }

}
