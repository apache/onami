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

import static org.mockito.Mockito.*;

/**
 * Test for {@link JtaTransactionFacadeFactory}.
 */
@RunWith( HierarchicalContextRunner.class )
public class JtaTransactionFacadeProviderTest
{

    private JtaTransactionFacadeFactory sut;

    private UserTransactionFacade utFacade;

    private EntityManagerProvider emProvider;

    private EntityManager em;

    @Before
    public void setUp()
    {
        // input
        utFacade = mock( UserTransactionFacade.class );
        emProvider = mock( EntityManagerProvider.class );

        // subject under test
        sut = new JtaTransactionFacadeFactory( utFacade, emProvider );

        // environment
        em = mock( EntityManager.class );
        doReturn( em ).when( emProvider ).get();
    }

    public class InnerTransactionTest
    {

        private TransactionFacade sut;

        @Before
        public void setUp()
        {
            doReturn( true ).when( utFacade ).isActive();
            sut = JtaTransactionFacadeProviderTest.this.sut.createTransactionFacade();
        }

        @Test
        public void beginShouldDoNothing()
        {
            sut.begin();

            verify( utFacade, never() ).begin();
            verify( em ).joinTransaction();
        }

        @Test
        public void commitShouldDoNothing()
        {
            sut.commit();

            verify( utFacade, never() ).commit();
        }

        @Test
        public void rollbackShouldSetRollbackOnlyFlag()
        {
            sut.rollback();

            verify( utFacade ).setRollbackOnly();
        }
    }

    public class OuterTransactionTest
    {

        private TransactionFacade sut;

        @Before
        public void setUp()
        {
            doReturn( false ).when( utFacade ).isActive();
            sut = JtaTransactionFacadeProviderTest.this.sut.createTransactionFacade();
        }

        @Test
        public void beginShouldBeginTransaction()
        {
            sut.begin();

            verify( utFacade ).begin();
            verify( em ).joinTransaction();
        }

        @Test
        public void commitShouldCommitTransaction()
        {
            sut.commit();

            verify( utFacade ).commit();
        }

        @Test
        public void commitShouldRollbackTransactionIfMarkedAsRollbackOnly()
        {
            doReturn( true ).when( utFacade ).getRollbackOnly();

            sut.commit();

            verify( utFacade ).rollback();
        }

        @Test
        public void rollbackShouldRollbackTransaction()
        {
            sut.rollback();

            verify( utFacade ).rollback();
        }
    }

}
