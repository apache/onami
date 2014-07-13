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
import javax.inject.Provider;
import com.google.inject.TypeLiteral;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import java.lang.annotation.Annotation;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link PersistenceUnitModuleConfiguration}.
 */
public class PersistenceUnitModuleConfigurationTest
{

    private PersistenceUnitModuleConfiguration sut;

    @Before
    public void setUp()
        throws Exception
    {
        sut = new PersistenceUnitModuleConfiguration();
    }

    @Test
    public void shouldHandleAnnotation()
    {
        // given
        Class<? extends Annotation> annotation = Annotation.class;
        // when
        sut.annotatedWith( annotation );
        // then
        assertThat( sut.getAnnotation(), sameInstance( (Class) annotation ) );
    }

    @Test
    public void shouldHandleResourceLocale()
    {
        // when
        sut.useLocalTransaction();
        // then
        assertThat( sut.isJta(), is( false ) );
    }

    @Test
    public void shouldHandleUserTransaction()
    {
        // given
        final UserTransaction userTransaction = mock( UserTransaction.class );
        // when
        sut.useGlobalTransaction( userTransaction );
        // then
        assertThat( sut.isJta(), is( true ) );
        assertThat( sut.getUserTransaction(), sameInstance( userTransaction ) );
    }

    @Test
    public void shouldHandleUserTransactionInJndi()
    {
        // given
        final String utJndiName = "ut";
        // when
        sut.useGlobalTransactionWithJndiName( utJndiName );
        // then
        assertThat( sut.isJta(), is( true ) );
        assertThat( sut.getUtJndiName(), is( utJndiName ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void shouldHandleUserTransactionProvider()
    {
        // given
        final Provider<UserTransaction> utProvider = new MockUserTransactionProvider();
        // when
        sut.useGlobalTransactionProvidedBy( utProvider );
        // then
        assertThat( sut.isJta(), is( true ) );
        assertThat( sut.getUtProvider(), sameInstance( utProvider ) );
    }

    @Test
    public void shouldHandleUserTransactionProviderClass()
    {
        // given
        final Class<? extends Provider<UserTransaction>> utProviderClass = MockUserTransactionProvider.class;
        // when
        sut.useGlobalTransactionProvidedBy( utProviderClass );
        // then
        assertThat( sut.isJta(), is( true ) );
        assertThat( (Key) sut.getUtProviderKey(), is( (Key) Key.get( utProviderClass ) ) );
    }

    @Test
    public void shouldHandleUserTransactionProviderType()
    {
        // given
        final TypeLiteral<? extends Provider<UserTransaction>> utProviderType =
            TypeLiteral.get( MockUserTransactionProvider.class );
        // when
        sut.useGlobalTransactionProvidedBy( utProviderType );
        // then
        assertThat( sut.isJta(), is( true ) );
        assertThat( (Key) sut.getUtProviderKey(), is( (Key) Key.get( utProviderType ) ) );
    }

    @Test
    public void shouldHandleUserTransactionProviderKey()
    {
        // given
        final Key<? extends Provider<UserTransaction>> utProviderKey = Key.get( MockUserTransactionProvider.class );
        // when
        sut.useGlobalTransactionProvidedBy( utProviderKey );
        // then
        assertThat( sut.isJta(), is( true ) );
        assertThat( (Key) sut.getUtProviderKey(), is( (Key) utProviderKey ) );
    }

    @Test
    public void shouldHandleProperties()
    {
        // given
        Properties properties = mock( Properties.class );
        // when
        sut.setProperties( properties );
        // then
        assertThat( sut.getProperties(), sameInstance( properties ) );
    }

    @Test
    public void shouldHandlePuName()
    {
        // given
        final String puName = "puName";
        // when
        sut.setPuName( puName );
        // then
        assertThat( sut.isApplicationManagedPersistenceUnit(), is( true ) );
        assertThat( sut.getPuName(), sameInstance( puName ) );
    }

    @Test
    public void shouldHandleEmf()
    {
        // given
        final EntityManagerFactory emf = mock( EntityManagerFactory.class );
        // when
        sut.setEmf( emf );
        // then
        assertThat( sut.isApplicationManagedPersistenceUnit(), is( false ) );
        assertThat( sut.getEmf(), sameInstance( emf ) );
        assertThat( sut.isEmfProvidedByInstance(), is( true ) );
        assertThat( sut.isEmfProvidedByJndiLookup(), is( false ) );
        assertThat( sut.isEmfProvidedByProvider(), is( false ) );
        assertThat( sut.isEmfProvidedByProviderKey(), is( false ) );
    }

    @Test
    public void shouldHandleEmfJndiName()
    {
        // given
        final String emfJndiName = "emfJndiName";
        // when
        sut.setEmfJndiName( emfJndiName );
        // then
        assertThat( sut.isApplicationManagedPersistenceUnit(), is( false ) );
        assertThat( sut.getEmfJndiName(), is( emfJndiName ) );
        assertThat( sut.isEmfProvidedByInstance(), is( false ) );
        assertThat( sut.isEmfProvidedByJndiLookup(), is( true ) );
        assertThat( sut.isEmfProvidedByProvider(), is( false ) );
        assertThat( sut.isEmfProvidedByProviderKey(), is( false ) );
    }

    @Test
    public void shouldHandleEmfProvider()
    {
        // given
        final Provider<EntityManagerFactory> emfProvider = new MockEmfProvider();
        // when
        sut.setEmfProvider( emfProvider );
        // then
        assertThat( sut.isApplicationManagedPersistenceUnit(), is( false ) );
        assertThat( sut.getEmfProvider(), sameInstance( emfProvider ) );
        assertThat( sut.isEmfProvidedByInstance(), is( false ) );
        assertThat( sut.isEmfProvidedByJndiLookup(), is( false ) );
        assertThat( sut.isEmfProvidedByProvider(), is( true ) );
        assertThat( sut.isEmfProvidedByProviderKey(), is( false ) );
    }

    @Test
    public void shouldHandleEmfProviderClass()
    {
        // given
        final Class<? extends Provider<EntityManagerFactory>> emfProviderClass = MockEmfProvider.class;
        // when
        sut.setEmfProviderClass( emfProviderClass );
        // then
        assertThat( sut.isApplicationManagedPersistenceUnit(), is( false ) );
        assertThat( (Key) sut.getEmfProviderKey(), is( (Key) Key.get( emfProviderClass ) ) );
        assertThat( sut.isEmfProvidedByInstance(), is( false ) );
        assertThat( sut.isEmfProvidedByJndiLookup(), is( false ) );
        assertThat( sut.isEmfProvidedByProvider(), is( false ) );
        assertThat( sut.isEmfProvidedByProviderKey(), is( true ) );
    }

    @Test
    public void shouldHandleEmfProviderType()
    {
        // given
        final TypeLiteral<? extends Provider<EntityManagerFactory>> emfProviderType =
            TypeLiteral.get( MockEmfProvider.class );
        // when
        sut.setEmfProviderType( emfProviderType );
        // then
        assertThat( sut.isApplicationManagedPersistenceUnit(), is( false ) );
        assertThat( (Key) sut.getEmfProviderKey(), is( (Key) Key.get( emfProviderType ) ) );
        assertThat( sut.isEmfProvidedByInstance(), is( false ) );
        assertThat( sut.isEmfProvidedByJndiLookup(), is( false ) );
        assertThat( sut.isEmfProvidedByProvider(), is( false ) );
        assertThat( sut.isEmfProvidedByProviderKey(), is( true ) );
    }

    @Test
    public void shouldHandleEmfProviderKey()
    {
        // given
        final Key<? extends Provider<EntityManagerFactory>> emfProviderKey = Key.get( MockEmfProvider.class );
        // when
        sut.setEmfProviderKey( emfProviderKey );
        // then
        assertThat( sut.isApplicationManagedPersistenceUnit(), is( false ) );
        assertThat( (Key) sut.getEmfProviderKey(), is( (Key) emfProviderKey ) );
        assertThat( sut.isEmfProvidedByInstance(), is( false ) );
        assertThat( sut.isEmfProvidedByJndiLookup(), is( false ) );
        assertThat( sut.isEmfProvidedByProvider(), is( false ) );
        assertThat( sut.isEmfProvidedByProviderKey(), is( true ) );
    }

    // helpers

    private static class MockUserTransactionProvider
        implements Provider<UserTransaction>
    {
        public UserTransaction get()
        {
            return mock( UserTransaction.class );
        }
    }

    private static class MockEmfProvider
        implements Provider<EntityManagerFactory>
    {
        public EntityManagerFactory get()
        {
            return mock( EntityManagerFactory.class );
        }
    }

}
