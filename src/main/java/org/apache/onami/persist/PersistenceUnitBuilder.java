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

import java.lang.annotation.Annotation;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Builder class for configurating a guice-jpa persistence unit.
 */
public final class PersistenceUnitBuilder
{

    // ---- Members

    /**
     * The module which is built by this instance.
     */
    private AbstractPersistenceUnitModule module;

    // ---- Constructors

    /**
     * Constructor.
     *
     * @param module the module which is built by this instance.
     */
    PersistenceUnitBuilder( AbstractPersistenceUnitModule module )
    {
        this.module = module;
    }

    // ---- Methods

    /**
     * Add an annotation to the module. The annotation is used to bind the {@link UnitOfWork} and
     * the {@link EntityManagerProvider} in guice.
     *
     * @param annotation the annotation. May be {@code null}.
     * @return the builder for method chaining.
     */
    public PersistenceUnitBuilder annotatedWith( Class<? extends Annotation> annotation )
    {
        checkNotNull( module, "cannot change a module after creating the injector." );
        module.annotatedWith( annotation );
        return this;
    }

    /**
     * Configure the persistence unit to use local transactions. This means even if the data source
     * is managed by the container its transaction will not participate in a global container managed
     * transaction (CMT).
     */
    public void useResourceLocalTransaction()
    {
        checkNotNull( module, "cannot change a module after creating the injector." );
        module.setTransactionType( TransactionType.RESOURCE_LOCAL );
    }

    /**
     * Configure the persistence unit to use global transactions. This means all transactions on this
     * data source will participate in a global container managed transaction (CMT)
     */
    public void useJtaTransaction()
    {
        checkNotNull( module, "cannot change a module after creating the injector." );
        module.setTransactionType( TransactionType.JTA );
    }

    /**
     * Builds the module and also changes the state of the builder.
     * After calling this method all calls to the builder will result in an exception.
     *
     * @return the persistence module.
     */
    AbstractPersistenceUnitModule build()
    {
        checkNotNull( module, "build() can only be called once." );
        final AbstractPersistenceUnitModule m = module;
        module = null;
        return m;
    }

}
