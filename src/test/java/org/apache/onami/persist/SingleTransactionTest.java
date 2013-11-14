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

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.TestCase;
import org.apache.onami.persist.testframework.TransactionalWorker;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnAnyThrowingNone;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnAnyThrowingRuntimeTestException;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnAnyThrowingTestException;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnNoneThrowingNone;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnNoneThrowingRuntimeTestException;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnNoneThrowingTestException;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnRuntimeTestExceptionThrowingNone;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnRuntimeTestExceptionThrowingTestException;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnTestExceptionThrowingNone;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnTestExceptionThrowingRuntimeTestException;
import org.apache.onami.persist.testframework.tasks.TaskRollingBackOnTestExceptionThrowingTestException;

/**
 * Tests running a single non nested transaction.
 * The test make us of the testframework. For every test a new Injector is created.
 */
public class SingleTransactionTest
    extends TestCase
{

    private Injector injector;

    @Override
    public void setUp()
    {
        final PersistenceModule pm = new PersistenceModule();
        pm.addApplicationManagedPersistenceUnit( "testUnit" );
        injector = Guice.createInjector( pm );

        //startup persistence
        injector.getInstance( PersistenceService.class ).start();
    }

    @Override
    public void tearDown()
    {
        injector.getInstance( PersistenceService.class ).stop();
        injector = null;
    }


    public void testTaskRollingBackOnAnyThrowingNone()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnAnyThrowingNone.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitesHaveBeenPersisted();
    }

    public void testTaskRollingBackOnAnyThrowingRuntimeTestException()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnAnyThrowingRuntimeTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertNoEntityHasBeenPersisted();
    }

    public void testTaskRollingBackOnAnyThrowingTestException()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnAnyThrowingTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertNoEntityHasBeenPersisted();
    }

    public void testTaskRollingBackOnNoneThrowingNone()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnNoneThrowingNone.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitesHaveBeenPersisted();
    }

    public void testTaskRollingBackOnNoneThrowingRuntimeTestException()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnNoneThrowingRuntimeTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitesHaveBeenPersisted();
    }

    public void testTaskRollingBackOnNoneThrowingTestException()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnNoneThrowingTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitesHaveBeenPersisted();
    }

    public void testTaskRollingBackOnRuntimeTestExceptionThrowingNone()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitesHaveBeenPersisted();
    }

    public void testTaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertNoEntityHasBeenPersisted();
    }

    public void testTaskRollingBackOnRuntimeTestExceptionThrowingTestException()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitesHaveBeenPersisted();
    }

    public void testTaskRollingBackOnTestExceptionThrowingNone()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnTestExceptionThrowingNone.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitesHaveBeenPersisted();
    }

    public void testTaskRollingBackOnTestExceptionThrowingRuntimeTestException()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertAllEntitesHaveBeenPersisted();
    }

    public void testTaskRollingBackOnTestExceptionThrowingTestException()
    {
        // given
        final TransactionalWorker worker = injector.getInstance( TransactionalWorker.class );
        worker.scheduleTask( TaskRollingBackOnTestExceptionThrowingTestException.class );

        // when
        worker.doTasks();

        // then
        worker.assertNoEntityHasBeenPersisted();
    }

}
