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

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterThan;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import static java.lang.System.currentTimeMillis;
import static javax.transaction.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for {@link UserTransactionFacade}.
 */
public class UserTransactionFacadeTest
{

    private UserTransactionFacade sut;

    private UserTransaction txn;

    @Before
    public void setup()
        throws Exception
    {
        txn = mock( UserTransaction.class );
        sut = new UserTransactionFacade( txn );
    }

    @Test
    public void beginOnTxn()
        throws Exception
    {
        sut.begin();
        verify( txn ).begin();
    }

    @Test( expected = RuntimeException.class )
    public void beginWithNotSupportedException()
        throws Exception
    {
        doThrow( new NotSupportedException() ).when( txn ).begin();
        sut.begin();
    }

    @Test( expected = RuntimeException.class )
    public void beginWithSystemException()
        throws Exception
    {
        doThrow( new SystemException() ).when( txn ).begin();
        sut.begin();
    }

    @Test
    public void commitOnTxn()
        throws Exception
    {
        sut.commit();
        verify( txn ).commit();
    }

    @Test( expected = RuntimeException.class )
    public void commitWithSecurityException()
        throws Exception
    {
        doThrow( new SecurityException() ).when( txn ).commit();
        sut.commit();
    }

    @Test( expected = RuntimeException.class )
    public void commitWithIllegalStateException()
        throws Exception
    {
        doThrow( new IllegalStateException() ).when( txn ).commit();
        sut.commit();
    }

    @Test( expected = RuntimeException.class )
    public void commitWithRollbackException()
        throws Exception
    {
        doThrow( new RollbackException() ).when( txn ).commit();
        sut.commit();
    }

    @Test( expected = RuntimeException.class )
    public void commitWithHeuristicMixedException()
        throws Exception
    {
        doThrow( new HeuristicMixedException() ).when( txn ).commit();
        sut.commit();
    }

    @Test( expected = RuntimeException.class )
    public void commitWithHeuristicRollbackException()
        throws Exception
    {
        doThrow( new HeuristicRollbackException() ).when( txn ).commit();
        sut.commit();
    }

    @Test( expected = RuntimeException.class )
    public void commitWithSystemException()
        throws Exception
    {
        doThrow( new SystemException() ).when( txn ).commit();
        sut.commit();
    }

    @Test
    public void rollbackOnTxn()
        throws Exception
    {
        sut.rollback();
        verify( txn ).rollback();
    }

    @Test( expected = RuntimeException.class )
    public void rollbackWithIllegalStateException()
        throws Exception
    {
        doThrow( new IllegalStateException() ).when( txn ).rollback();
        sut.rollback();
    }

    @Test( expected = RuntimeException.class )
    public void rollbackWithSecurityException()
        throws Exception
    {
        doThrow( new SecurityException() ).when( txn ).rollback();
        sut.rollback();
    }

    @Test( expected = RuntimeException.class )
    public void rollbackWithSystemException()
        throws Exception
    {
        doThrow( new SystemException() ).when( txn ).rollback();
        sut.rollback();
    }

    @Test
    public void setRollbackOnlyOnTxn()
        throws Exception
    {
        sut.setRollbackOnly();
        verify( txn ).setRollbackOnly();
    }

    @Test( expected = RuntimeException.class )
    public void setRollbackOnlyWithIllegalStateException()
        throws Exception
    {
        doThrow( new IllegalStateException() ).when( txn ).setRollbackOnly();
        sut.setRollbackOnly();
    }

    @Test( expected = RuntimeException.class )
    public void setRollbackOnlyWithSystemException()
        throws Exception
    {
        doThrow( new SystemException() ).when( txn ).setRollbackOnly();
        sut.setRollbackOnly();
    }

    @Test
    public void getRollbackOnlyUsesStatusOfTransaction()
        throws Exception
    {
        assertThatRollbackOnlyOf( STATUS_ACTIVE, is( false ) );
        assertThatRollbackOnlyOf( STATUS_MARKED_ROLLBACK, is( true ) );
        assertThatRollbackOnlyOf( STATUS_PREPARED, is( false ) );
        assertThatRollbackOnlyOf( STATUS_COMMITTED, is( false ) );
        assertThatRollbackOnlyOf( STATUS_ROLLEDBACK, is( true ) );
        assertThatRollbackOnlyOf( STATUS_UNKNOWN, is( false ) );
        assertThatRollbackOnlyOf( STATUS_NO_TRANSACTION, is( false ) );
        assertThatRollbackOnlyOf( STATUS_PREPARING, is( false ) );
        assertThatRollbackOnlyOf( STATUS_COMMITTING, is( false ) );
        assertThatRollbackOnlyOf( STATUS_ROLLING_BACK, is( true ) );
    }

    @Test( expected = RuntimeException.class )
    public void getRollbackOnlyWithSystemException()
        throws Exception
    {
        doThrow( new SystemException() ).when( txn ).getStatus();
        sut.getRollbackOnly();
    }

    @Test
    public void getRollbackOnlyRetriesWhenStatusUnknown()
        throws Exception
    {
        final long start = currentTimeMillis();
        doReturn( STATUS_UNKNOWN ).when( txn ).getStatus();
        sut.getRollbackOnly();
        final long duration = currentTimeMillis() - start;

        verify( txn, times( 9 ) ).getStatus();
        assertThat( duration, is( greaterThan( 1000L ) ) );
    }

    @Test
    public void isActiveUsesStatusOfTransaction()
        throws Exception
    {
        assertThatIsActiveOf( STATUS_ACTIVE, is( true ) );
        assertThatIsActiveOf( STATUS_MARKED_ROLLBACK, is( true ) );
        assertThatIsActiveOf( STATUS_PREPARED, is( true ) );
        assertThatIsActiveOf( STATUS_COMMITTED, is( true ) );
        assertThatIsActiveOf( STATUS_ROLLEDBACK, is( true ) );
        assertThatIsActiveOf( STATUS_UNKNOWN, is( true ) );
        assertThatIsActiveOf( STATUS_NO_TRANSACTION, is( false ) );
        assertThatIsActiveOf( STATUS_PREPARING, is( true ) );
        assertThatIsActiveOf( STATUS_COMMITTING, is( true ) );
        assertThatIsActiveOf( STATUS_ROLLING_BACK, is( true ) );
    }

    @Test( expected = RuntimeException.class )
    public void isActiveWithSystemException()
        throws Exception
    {
        doThrow( new SystemException() ).when( txn ).getStatus();
        sut.isActive();
    }

    @Test
    public void isActiveOnlyRetriesWhenStatusUnknown()
        throws Exception
    {
        final long start = currentTimeMillis();
        doReturn( STATUS_UNKNOWN ).when( txn ).getStatus();
        sut.isActive();
        final long duration = currentTimeMillis() - start;

        verify( txn, times( 9 ) ).getStatus();
        assertThat( duration, is( greaterThan( 1000L ) ) );
    }

    private void assertThatRollbackOnlyOf( int status, Matcher<Boolean> expected )
        throws Exception
    {
        doReturn( status ).when( txn ).getStatus();
        final boolean result = sut.getRollbackOnly();
        assertThat( result, is( expected ) );
    }

    private void assertThatIsActiveOf( int status, Matcher<Boolean> expected )
        throws Exception
    {
        doReturn( status ).when( txn ).getStatus();
        final boolean result = sut.isActive();
        assertThat( result, is( expected ) );
    }

    private Matcher<Long> greaterThan( long expected )
    {
        return new GreaterThan<Long>( expected );
    }

}
