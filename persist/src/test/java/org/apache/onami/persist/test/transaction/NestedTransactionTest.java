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
import org.apache.onami.persist.test.transaction.testframework.TransactionalTask;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests running nested transactions.
 * The test makes use of the test framework.
 * Since the test is running a loop the injector is created directly in the test to ensure
 * that for every {@link TestVector} a new injector instance is used.
 */
public class NestedTransactionTest
{

    /**
     * All possible combination of {@link org.apache.onami.persist.test.transaction.testframework.TransactionalTask}s
     * and if they should have been rolled back.
     */
    private static final Collection<TestVector> TEST_VECTORS = buildTestVectors(
        whenFirstTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //                                                  //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //                                  //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //                                         //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //                                                 //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //                                 //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //                                        //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //                                 //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class ) //                 //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //                        //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //                                        //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //                        //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ), //

        whenFirstTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ) //                               //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnAnyThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnNoneThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnNoneThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingNone.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingRuntimeTestException.class )//
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnRuntimeTestExceptionThrowingTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingNone.class ) //
            .expectCommitWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingRuntimeTestException.class ) //
            .expectRollbackWhenSecondTaskIs( TaskRollingBackOnTestExceptionThrowingTestException.class ) //
    );


    /**
     * Test which ensures that all combinations of tasks are present.
     */
    @Test
    public void testVectorShouldContainAllCombinations()
    {
        Set<TestVector> s = new HashSet<TestVector>();

        s.addAll( TEST_VECTORS );

        assertThat( s.size(), is( 12 * 12 ) );
    }

    /**
     * Test which iterates over ALL possible combinations of inner and outer tasks.
     */
    @Test
    public void testNestedTransactions()
    {
        final StringBuilder msg = new StringBuilder();
        for ( TestVector v : TEST_VECTORS )
        {
            try
            {
                doTestNestedTransaction( v );
            }
            catch ( AssertionError e )
            {
                msg.append( "\n" );
                msg.append( e.getMessage() );
            }
        }
        if ( msg.length() > 0 )
        {
            fail( msg.toString() );
        }
    }

    private void doTestNestedTransaction( TestVector testVector )
    {
        final PersistenceModule pm = createPersistenceModuleForTest();
        final Injector injector = Guice.createInjector( pm );
        final PersistenceService persistService = injector.getInstance( PersistenceService.class );
        persistService.start();
        try
        {
            doTestNestedTransaction( testVector, injector.getInstance( TransactionalWorker.class ) );
        }
        finally
        {
            persistService.stop();
        }

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

    private void doTestNestedTransaction( TestVector testVector, TransactionalWorker worker )
    {
        // given
        testVector.scheduleOuterTaskOn( worker );
        testVector.scheduleInnerTaskOn( worker );

        // when
        worker.doTasks();

        // then
        testVector.assertExpectedOutcomeFor( worker );
    }


    private static Collection<TestVector> buildTestVectors( TestVectorsBuilder... factories )
    {
        final List<TestVector> result = new ArrayList<TestVector>();
        for ( TestVectorsBuilder factory : factories )
        {
            result.addAll( factory.buildTestVectors() );
        }
        return result;
    }

    private static TestVectorsBuilder whenFirstTaskIs( Class<? extends TransactionalTask> firstTask )
    {
        return new TestVectorsBuilder( firstTask );
    }

    private abstract static class TestVector
    {
        private final Class<? extends TransactionalTask> outerTask;

        private final Class<? extends TransactionalTask> innerTask;

        public TestVector( Class<? extends TransactionalTask> outerTask, Class<? extends TransactionalTask> innerTask )
        {
            this.outerTask = outerTask;
            this.innerTask = innerTask;
        }

        public void scheduleOuterTaskOn( TransactionalWorker worker )
        {
            worker.scheduleTask( outerTask );
        }

        public void scheduleInnerTaskOn( TransactionalWorker worker )
        {
            worker.scheduleTask( innerTask );
        }

        public abstract void assertExpectedOutcomeFor( TransactionalWorker worker );

        @Override
        public boolean equals( Object obj )
        {
            return obj instanceof TestVector && equalsTestVector( (TestVector) obj );
        }

        private boolean equalsTestVector( TestVector other )
        {
            return innerTask.equals( other.innerTask ) && outerTask.equals( other.outerTask );
        }

        @Override
        public int hashCode()
        {
            return 31 * outerTask.hashCode() + innerTask.hashCode();
        }
    }

    private static class RollingBackTestVector
        extends TestVector
    {

        public RollingBackTestVector( Class<? extends TransactionalTask> outerTask,
                                      Class<? extends TransactionalTask> innerTask )
        {
            super( outerTask, innerTask );
        }

        @Override
        public void assertExpectedOutcomeFor( TransactionalWorker worker )
        {
            worker.assertNoEntityHasBeenPersisted();
        }
    }

    private static class CommittingTestVector
        extends TestVector
    {

        public CommittingTestVector( Class<? extends TransactionalTask> outerTask,
                                     Class<? extends TransactionalTask> innerTask )
        {
            super( outerTask, innerTask );
        }

        @Override
        public void assertExpectedOutcomeFor( TransactionalWorker worker )
        {
            worker.assertAllEntitiesHaveBeenPersisted();
        }
    }

    private static class TestVectorsBuilder
    {

        private final Class<? extends TransactionalTask> firstTask;

        private final List<TestVector> result;

        public TestVectorsBuilder( Class<? extends TransactionalTask> firstTask )
        {
            this.firstTask = firstTask;
            this.result = new ArrayList<TestVector>();
        }

        public TestVectorsBuilder expectCommitWhenSecondTaskIs( Class<? extends TransactionalTask> secondTask )
        {
            result.add( new CommittingTestVector( firstTask, secondTask ) );
            return this;
        }

        public TestVectorsBuilder expectRollbackWhenSecondTaskIs( Class<? extends TransactionalTask> secondTask )
        {
            result.add( new RollingBackTestVector( firstTask, secondTask ) );
            return this;
        }

        public Collection<TestVector> buildTestVectors()
        {
            return result;
        }
    }
}
