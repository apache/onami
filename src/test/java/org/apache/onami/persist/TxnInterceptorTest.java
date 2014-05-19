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
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Test for {@link TxnInterceptor}
 */
@RunWith( HierarchicalContextRunner.class )
public class TxnInterceptorTest
{

    private UnitOfWork unitOfWork;

    private TransactionalAnnotationHelper txnAnnotationHelper;

    private TransactionFacadeFactory tfProvider;

    private TransactionFacade txnFacade;

    private TxnInterceptor sut;

    private MethodInvocation invocation;

    @Before
    public void setUp()
        throws Exception
    {
        unitOfWork = mock( UnitOfWork.class );
        tfProvider = mock( TransactionFacadeFactory.class );
        txnAnnotationHelper = mock( TransactionalAnnotationHelper.class );

        sut = new TxnInterceptor();
        sut.unitOfWork = unitOfWork;
        sut.tfProvider = tfProvider;
        sut.txnAnnotationHelper = txnAnnotationHelper;

        invocation = mock( MethodInvocation.class );
    }

    public class NotParticipatingInTransaction
    {

        @Before
        public void setUp()
            throws Exception
        {
            doReturn( false ).when( txnAnnotationHelper ).persistenceUnitParticipatesInTransactionFor( invocation );
        }

        @Test
        public void invokesOriginalIfNotParticipatingInTransaction()
            throws Throwable
        {
            sut.invoke( invocation );

            verify( invocation ).proceed();
        }

    }

    public class ParticipatingInTransaction
    {

        @Before
        public void setUp()
            throws Exception
        {
            doReturn( true ).when( txnAnnotationHelper ).persistenceUnitParticipatesInTransactionFor( invocation );

            txnFacade = mock( TransactionFacade.class );
            doReturn( txnFacade ).when( tfProvider ).createTransactionFacade();
        }

        public class UnitOfWorkInactive
        {

            private InOrder inOrder;

            @Before
            public void setup()
            {
                doReturn( false ).when( unitOfWork ).isActive();
                inOrder = inOrder( unitOfWork, invocation );
            }

            @Test
            public void processWithoutException()
                throws Throwable
            {
                sut.invoke( invocation );

                inOrder.verify( unitOfWork ).begin();
                inOrder.verify( invocation ).proceed();
                inOrder.verify( unitOfWork ).end();
            }

            @Test
            public void processWithException()
                throws Throwable
            {
                // given
                final RuntimeException originalException = new RuntimeException();
                doThrow( originalException ).when( invocation ).proceed();

                // when
                try
                {
                    sut.invoke( invocation );
                }

                // then
                catch ( RuntimeException e )
                {
                    inOrder.verify( unitOfWork ).begin();
                    inOrder.verify( invocation ).proceed();
                    inOrder.verify( unitOfWork ).end();
                    assertThat( e, sameInstance( originalException ) );
                    return;
                }
                fail( "expected RuntimeException to be thrown" );
            }

            @Test
            public void throwExceptionWhichOccurredInUnitOfWork()
                throws Throwable
            {
                // given
                final RuntimeException uowExec = new RuntimeException();
                doThrow( uowExec ).when( unitOfWork ).end();

                // when
                try
                {
                    sut.invoke( invocation );
                }

                // then
                catch ( RuntimeException e )
                {
                    inOrder.verify( unitOfWork ).begin();
                    inOrder.verify( invocation ).proceed();
                    inOrder.verify( unitOfWork ).end();
                    assertThat( e, sameInstance( uowExec ) );
                    return;
                }
                fail( "expected RuntimeException to be thrown" );
            }

            @Test
            public void throwExceptionOfOriginalMethodIfExceptionOccurredInUnitOfWork()
                throws Throwable
            {
                // given
                final RuntimeException originalExc = new RuntimeException();
                doThrow( originalExc ).when( invocation ).proceed();
                doThrow( new RuntimeException() ).when( unitOfWork ).end();

                // when
                try
                {
                    sut.invoke( invocation );
                }

                // then
                catch ( RuntimeException e )
                {
                    inOrder.verify( unitOfWork ).begin();
                    inOrder.verify( invocation ).proceed();
                    inOrder.verify( unitOfWork ).end();
                    assertThat( e, sameInstance( originalExc ) );
                    return;
                }
                fail( "expected RuntimeException to be thrown" );
            }

        }

        public class UnitOfWorkActive
        {

            private InOrder inOrder;

            @Before
            public void setup()
            {
                doReturn( true ).when( unitOfWork ).isActive();
                inOrder = inOrder( txnFacade, invocation );
            }

            @Test
            public void processWithoutUnitOfWork()
                throws Throwable
            {
                sut.invoke( invocation );

                verify( unitOfWork, never() ).begin();
                verify( invocation ).proceed();
                verify( unitOfWork, never() ).end();
            }

            @Test
            public void invokeStartsTransactionIfParticipatingInTransaction()
                throws Throwable
            {
                // given
                doReturn( true ).when( txnAnnotationHelper ).persistenceUnitParticipatesInTransactionFor( invocation );
                // when
                sut.invoke( invocation );
                // then
                inOrder.verify( txnFacade ).begin();
                inOrder.verify( invocation ).proceed();
                inOrder.verify( txnFacade ).commit();
            }

            @Test
            public void rollbackIfExceptionThrownWhichRequiresRollback()
                throws Throwable
            {
                // given
                final RuntimeException exc = new RuntimeException();
                doReturn( true ).when( txnAnnotationHelper ).persistenceUnitParticipatesInTransactionFor( invocation );
                doThrow( exc ).when( invocation ).proceed();
                doReturn( true ).when( txnAnnotationHelper ).isRollbackNecessaryFor( invocation, exc );

                // when
                try
                {
                    sut.invoke( invocation );
                }

                // then
                catch ( RuntimeException e )
                {
                    inOrder.verify( txnFacade ).begin();
                    inOrder.verify( invocation ).proceed();
                    inOrder.verify( txnFacade ).rollback();
                    assertThat( e, sameInstance( exc ) );
                    return;
                }
                fail( "expected RuntimeException to be thrown" );
            }

            @Test
            public void commitIfExceptionThrownWhichRequiresNoRollback()
                throws Throwable
            {
                // given
                final RuntimeException exc = new RuntimeException();
                doReturn( true ).when( txnAnnotationHelper ).persistenceUnitParticipatesInTransactionFor( invocation );
                doThrow( exc ).when( invocation ).proceed();
                doReturn( false ).when( txnAnnotationHelper ).isRollbackNecessaryFor( invocation, exc );

                // when
                try
                {
                    sut.invoke( invocation );
                }

                // then
                catch ( RuntimeException e )
                {
                    inOrder.verify( txnFacade ).begin();
                    inOrder.verify( invocation ).proceed();
                    inOrder.verify( txnFacade ).commit();
                    assertThat( e, sameInstance( exc ) );
                    return;
                }
                fail( "expected RuntimeException to be thrown" );
            }

            @Test
            public void throwExceptionOfOriginalMethodIfExceptionOccurredInCommit()
                throws Throwable
            {
                // given
                final RuntimeException exc = new RuntimeException();
                doReturn( true ).when( txnAnnotationHelper ).persistenceUnitParticipatesInTransactionFor( invocation );
                doThrow( exc ).when( invocation ).proceed();
                doReturn( false ).when( txnAnnotationHelper ).isRollbackNecessaryFor( invocation, exc );
                doThrow( new RuntimeException() ).when( txnFacade ).commit();

                // when
                try
                {
                    sut.invoke( invocation );
                }

                // then
                catch ( RuntimeException e )
                {
                    inOrder.verify( txnFacade ).begin();
                    inOrder.verify( invocation ).proceed();
                    inOrder.verify( txnFacade ).commit();
                    assertThat( e, sameInstance( exc ) );
                    return;
                }
                fail( "expected RuntimeException to be thrown" );
            }

            @Test
            public void throwExceptionOfOriginalMethodIfExceptionOccurredInRollback()
                throws Throwable
            {
                // given
                final RuntimeException exc = new RuntimeException();
                doReturn( true ).when( txnAnnotationHelper ).persistenceUnitParticipatesInTransactionFor( invocation );
                doThrow( exc ).when( invocation ).proceed();
                doReturn( true ).when( txnAnnotationHelper ).isRollbackNecessaryFor( invocation, exc );
                doThrow( new RuntimeException() ).when( txnFacade ).rollback();

                // when
                try
                {
                    sut.invoke( invocation );
                }

                // then
                catch ( RuntimeException e )
                {
                    inOrder.verify( txnFacade ).begin();
                    inOrder.verify( invocation ).proceed();
                    inOrder.verify( txnFacade ).rollback();
                    assertThat( e, sameInstance( exc ) );
                    return;
                }
                fail( "expected RuntimeException to be thrown" );
            }

        }

    }

}
