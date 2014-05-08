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
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import java.lang.annotation.Annotation;
import java.util.Properties;

/**
 * Class holding the configuration for a single persistence unit.
 */
class PersistenceUnitModuleConfiguration
    implements UnannotatedPersistenceUnitBuilder, AnnotatedPersistenceUnitBuilder, UnconfiguredPersistenceUnitBuilder
{
    private Class<? extends Annotation> annotation;

    private boolean isJta = false;

    private UserTransaction userTransaction;

    private String utJndiName;

    private Provider<UserTransaction> utProvider;

    private Key<? extends Provider<UserTransaction>> utProviderKey;

    private Properties properties;

    private String puName;

    private EntityManagerFactory emf;

    private String emfJndiName;

    private Provider<EntityManagerFactory> emfProvider;

    private Key<? extends Provider<EntityManagerFactory>> emfProviderKey;

    /**
     * {@inheritDoc}
     */
    public AnnotatedPersistenceUnitBuilder annotatedWith( Class<? extends Annotation> annotation )
    {
        this.annotation = annotation;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useLocalTransaction()
    {
        isJta = false;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransaction( UserTransaction userTransaction )
    {
        this.isJta = true;
        this.userTransaction = userTransaction;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionWithJndiName( String utJndiName )
    {
        this.isJta = true;
        this.utJndiName = utJndiName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy( Provider<UserTransaction> utProvider )
    {
        this.isJta = true;
        this.utProvider = utProvider;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        Class<? extends Provider<UserTransaction>> utProviderClass )
    {
        return useGlobalTransactionProvidedBy( Key.get( utProviderClass ) );
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        TypeLiteral<? extends Provider<UserTransaction>> utProviderType )
    {
        return useGlobalTransactionProvidedBy( Key.get( utProviderType ) );
    }

    /**
     * {@inheritDoc}
     */
    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        Key<? extends Provider<UserTransaction>> utProviderKey )
    {
        this.isJta = true;
        this.utProviderKey = utProviderKey;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    void setPuName( String puName )
    {
        this.puName = puName;
    }

    void setEmf( EntityManagerFactory emf )
    {
        this.emf = emf;
    }

    void setEmfJndiName( String emfJndiName )
    {
        this.emfJndiName = emfJndiName;
    }

    void setEmfProvider( Provider<EntityManagerFactory> emfProvider )
    {
        this.emfProvider = emfProvider;
    }

    void setEmfProviderClass( Class<? extends Provider<EntityManagerFactory>> emfProviderClass )
    {
        this.emfProviderKey = Key.get( emfProviderClass );
    }

    void setEmfProviderType( TypeLiteral<? extends Provider<EntityManagerFactory>> emfProviderType )
    {
        this.emfProviderKey = Key.get( emfProviderType );
    }

    void setEmfProviderKey( Key<? extends Provider<EntityManagerFactory>> emfProviderKey )
    {
        this.emfProviderKey = emfProviderKey;
    }

    boolean isApplicationManagedPersistenceUnit()
    {
        return puName != null;
    }


    UserTransaction getUserTransaction()
    {
        return userTransaction;
    }

    String getUtJndiName()
    {
        return utJndiName;
    }

    Provider<UserTransaction> getUtProvider()
    {
        return utProvider;
    }

    Key<? extends Provider<UserTransaction>> getUtProviderKey()
    {
        return utProviderKey;
    }

    Properties getProperties()
    {
        return properties;
    }

    String getPuName()
    {
        return puName;
    }

    EntityManagerFactory getEmf()
    {
        return emf;
    }

    String getEmfJndiName()
    {
        return emfJndiName;
    }

    Provider<EntityManagerFactory> getEmfProvider()
    {
        return emfProvider;
    }

    Key<? extends Provider<EntityManagerFactory>> getEmfProviderKey()
    {
        return emfProviderKey;
    }

    boolean isEmfProvidedByJndiLookup()
    {
        return emfJndiName != null;
    }

    boolean isEmfProvidedByInstance()
    {
        return emf != null;
    }

    boolean isEmfProvidedByProvider()
    {
        return emfProvider != null;
    }

    boolean isEmfProvidedByProviderKey()
    {
        return emfProviderKey != null;
    }

    boolean isJta()
    {
        return isJta;
    }

    boolean isUserTransactionProvidedByJndiLookup()
    {
        return utJndiName != null;
    }


    boolean isUserTransactionProvidedByInstance()
    {
        return userTransaction != null;
    }

    boolean isUserTransactionProvidedByProvider()
    {
        return utProvider != null;
    }

    boolean isUserTransactionProvidedByProviderKey()
    {
        return utProviderKey != null;
    }

    boolean isAnnotated()
    {
        return annotation != null;
    }

    AnnotationHolder getAnnotationHolder()
    {
        return new AnnotationHolder( annotation );
    }

    Class<? extends Annotation> getAnnotation()
    {
        return annotation;
    }
}
