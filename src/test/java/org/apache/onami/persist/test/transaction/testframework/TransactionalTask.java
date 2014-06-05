package org.apache.onami.persist.test.transaction.testframework;

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

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.test.TestEntity;
import org.apache.onami.persist.test.transaction.testframework.exceptions.RuntimeTestException;
import org.apache.onami.persist.test.transaction.testframework.exceptions.TestException;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link TransactionalTask} is a task which is executed during a transaction
 * test. {@link TransactionalTask}s are passed to a {@link TransactionalWorker} which will call
 * them one after another.
 * The sub classes of {@link TransactionalTask} should create a {@link org.apache.onami.persist.test.TestEntity} and
 * use {@link #storeEntity(org.apache.onami.persist.test.TestEntity)} to persist entities in the DB. They also must
 * call {@link #doOtherTasks()} to allow the {@link TransactionalWorker} to call the other scheduled
 * tasks.
 */
public abstract class TransactionalTask
{

    @Inject
    private EntityManagerProvider emProvider;

    private TransactionalWorker worker;

    private final List<TestEntity> persistedEntities = new ArrayList<TestEntity>();

    /**
     * Should 'try to' create entities in the persistent storage (i.e. DB).
     * Use {@link #storeEntity(org.apache.onami.persist.test.TestEntity)} to persist entities.
     *
     * @throws TestException        may be thrown to test rollback.
     * @throws RuntimeTestException may be thrown to test rollback.
     */
    public abstract void doTransactional()
        throws TestException, RuntimeTestException;

    /**
     * Does other tasks.
     *
     * @throws TestException        may be thrown to test rollback.
     * @throws RuntimeTestException may be thrown to test rollback.
     */
    protected final void doOtherTasks()
        throws TestException, RuntimeTestException
    {
        worker.doNextTask();
    }

    /**
     * Stores an entity.
     *
     * @param entity the entity to store.
     */
    protected final void storeEntity( TestEntity entity )
    {
        final EntityManager entityManager = emProvider.get();
        entityManager.persist( entity );
        entityManager.flush();
        persistedEntities.add( entity );
    }

    @VisibleForTesting
    void setWorker( TransactionalWorker transactionalWorker )
    {
        worker = transactionalWorker;
    }

    @VisibleForTesting
    List<TestEntity> getPersistedEntities()
    {
        return persistedEntities;
    }

}
