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

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.mockito.Mockito.*;

/**
 * Test for {@link ResourceLocalTransactionFacadeFactory}.
 */
@RunWith( HierarchicalContextRunner.class )
public class ResourceLocalTransactionFacadeProviderTest
{
    private ResourceLocalTransactionFacadeFactory sut;

    private EntityManagerProvider emProvider;

    private EntityManager em;

    private EntityTransaction txn;

    @Before
    public void setUp()
    {
        // input
        emProvider = mock( EntityManagerProvider.class );

        // subject under test
        sut = new ResourceLocalTransactionFacadeFactory( emProvider );

        // environment
        em = mock( EntityManager.class );
        doReturn( em ).when( emProvider ).get();

        txn = mock( EntityTransaction.class );
        doReturn( txn ).when( em ).getTransaction();
    }

    public class InnerTransactionTest
    {

        private TransactionFacade sut;

        @Before
        public void setUp()
        {
            doReturn( true ).when( txn ).isActive();
            sut = ResourceLocalTransactionFacadeProviderTest.this.sut.createTransactionFacade();
        }

        @Test
        public void beginShouldDoNothing()
        {
            sut.begin();

            verify( txn, never() ).begin();
        }

        @Test
        public void commitShouldDoNothing()
        {
            sut.commit();

            verify( txn, never() ).commit();
        }

        @Test
        public void rollbackShouldSetRollbackOnlyFlag()
        {
            sut.rollback();

            verify( txn ).setRollbackOnly();
        }
    }

    public class OuterTransactionTest
    {

        private TransactionFacade sut;

        @Before
        public void setUp()
        {
            doReturn( false ).when( txn ).isActive();
            sut = ResourceLocalTransactionFacadeProviderTest.this.sut.createTransactionFacade();
        }

        @Test
        public void beginShouldBeginTransaction()
        {
            sut.begin();

            verify( txn ).begin();
        }

        @Test
        public void commitShouldCommitTransaction()
        {
            sut.commit();

            verify( txn ).commit();
        }

        @Test
        public void commitShouldRollbackTransactionIfMarkedAsRollbackOnly()
        {
            doReturn( true ).when( txn ).getRollbackOnly();

            sut.commit();

            verify( txn ).rollback();
        }

        @Test
        public void rollbackShouldRollbackTransaction()
        {
            sut.rollback();

            verify( txn ).rollback();
        }
    }
}
