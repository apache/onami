package org.apache.onami.persist.test.transaction.testframework.tasks;

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

import org.apache.onami.persist.Transactional;
import org.apache.onami.persist.test.TestEntity;
import org.apache.onami.persist.test.transaction.testframework.TransactionalTask;
import org.apache.onami.persist.test.transaction.testframework.exceptions.RuntimeTestException;
import org.apache.onami.persist.test.transaction.testframework.exceptions.TestException;

/**
 * Task which stores an entity and will:
 * - roll backy if an exception happened.
 * - throw a new {@link TestException}.
 */
public class TaskRollingBackOnAnyThrowingTestException
    extends TransactionalTask
{

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( rollbackOn = Exception.class )
    public void doTransactional()
        throws TestException, RuntimeTestException
    {
        storeEntity( new TestEntity() );
        doOtherTasks();
        throw new TestException( getClass().getSimpleName() );
    }

}
