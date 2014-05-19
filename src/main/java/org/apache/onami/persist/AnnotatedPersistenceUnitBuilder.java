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

import javax.transaction.UserTransaction;

/**
 * 3rd step of the persistence unit builder process.
 * Define the transaction type.
 */
public interface AnnotatedPersistenceUnitBuilder
    extends UnconfiguredPersistenceUnitBuilder
{

    /**
     * Mark the persistence unit to use resource local transactions.
     *
     * @return the next builder step.
     */
    UnconfiguredPersistenceUnitBuilder useLocalTransaction();

    /**
     * Mark the persistence unit to use JTA transactions.
     *
     * @param userTransaction the instance of the UserTransaction object to use.
     * @return the next builder step.
     */
    UnconfiguredPersistenceUnitBuilder useGlobalTransaction( UserTransaction userTransaction );

    /**
     * Mark the persistence unit to use JTA transactions.
     *
     * @param utJndiName the JNDI name to use for looking up the user transaction instance.
     * @return the next builder step.
     */
    UnconfiguredPersistenceUnitBuilder useGlobalTransactionWithJndiName( String utJndiName );

    /**
     * Mark the persistence unit to use JTA transactions.
     *
     * @param utProvider a provider to retrieve the user transaction instance.
     * @return the next builder step.
     */
    UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy( Provider<UserTransaction> utProvider );

    /**
     * Mark the persistence unit to use JTA transactions.
     *
     * @param utProviderClass a provider to retrieve the user transaction instance.
     * @return the next builder step.
     */
    UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        Class<? extends Provider<UserTransaction>> utProviderClass );

    /**
     * Mark the persistence unit to use JTA transactions.
     *
     * @param utProviderType a provider to retrieve the user transaction instance.
     * @return the next builder step.
     */
    UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        TypeLiteral<? extends Provider<UserTransaction>> utProviderType );

    /**
     * Mark the persistence unit to use JTA transactions.
     *
     * @param utProviderKey a provider to retrieve the user transaction instance.
     * @return the next builder step.
     */
    UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        Key<? extends Provider<UserTransaction>> utProviderKey );

}
