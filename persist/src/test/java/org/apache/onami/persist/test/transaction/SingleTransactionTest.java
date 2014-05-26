package org.apache.onami.persist.test.transaction;

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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.onami.persist.PersistenceModule;
import org.apache.onami.persist.PersistenceService;
import org.apache.onami.persist.test.transaction.testframework.TransactionalWorker;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnAnyThrowingNone;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnAnyThrowingRuntimeTestException;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnAnyThrowingTestException;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnNoneThrowingNone;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnNoneThrowingRuntimeTestException;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnNoneThrowingTestException;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnRuntimeTestExceptionThrowingNone;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnRuntimeTestExceptionThrowingTestException;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnTestExceptionThrowingNone;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnTestExceptionThrowingRuntimeTestException;
import org.apache.onami.persist.test.transaction.testframework.tasks.TaskRollingBackOnTestExceptionThrowingTestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests running a single non nested transaction.
 * The test make us of the testframework. For every test a new Injector is created.
 */
public class SingleTransactionTest
{

    private Injector injector;

    private TransactionalWorker worker;

    @Before
    public void setUp()
    {
        final PersistenceModule pm = createPersistenceModuleForTest();
        injector = Guice.createInjector( pm );

        //startup persistence
        injector.getInstance( PersistenceService.class ).start();
        worker = injector.getInstance( TransactionalWorker.class );
    }

    private PersistenceModule createPersistenceModuleForTest()
    {
        return new PersistenceModule()
        {

            @Override
            protected void configurePersistence()
            {
                bindApplicationManagedPersistenceUnit( "testUnit" );
            }
        };
    }

    @After
    public void tearDown()
    {
        injector.getInstance( PersistenceService.class ).stop();
        injector = null;
    }

    @Test
    public void testTaskRollingBackOnAnyThrowingNone()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnAnyThrowingNone.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitiesHaveBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnAnyThrowingRuntimeTestException()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnAnyThrowingRuntimeTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertNoEntityHasBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnAnyThrowingTestException()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnAnyThrowingTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertNoEntityHasBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnNoneThrowingNone()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnNoneThrowingNone.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitiesHaveBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnNoneThrowingRuntimeTestException()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnNoneThrowingRuntimeTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitiesHaveBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnNoneThrowingTestException()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnNoneThrowingTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitiesHaveBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnRuntimeTestExceptionThrowingNone()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitiesHaveBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertNoEntityHasBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnRuntimeTestExceptionThrowingTestException()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitiesHaveBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnTestExceptionThrowingNone()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnTestExceptionThrowingNone.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitiesHaveBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnTestExceptionThrowingRuntimeTestException()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitiesHaveBeenPersisted();
    }

    @Test
    public void testTaskRollingBackOnTestExceptionThrowingTestException()
    {
        // given
        worker.scheduleTask( TaskRollingBackOnTestExceptionThrowingTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertNoEntityHasBeenPersisted();
    }

}
