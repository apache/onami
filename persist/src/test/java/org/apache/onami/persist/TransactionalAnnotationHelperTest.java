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

import java.lang.annotation.Annotation;
import java.security.InvalidParameterException;
import java.util.IllegalFormatException;
import java.util.MissingFormatArgumentException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link TransactionalAnnotationHelper}.
 */
@RunWith( HierarchicalContextRunner.class )
public class TransactionalAnnotationHelperTest
{

    private TransactionalAnnotationHelper sut;

    private TransactionalAnnotationReader txnAnnoReader;

    Class<? extends Annotation> puAnntoation;

    private MethodInvocation invocation;

    private Transactional txnal;

    @Before
    public void setUp()
        throws Exception
    {
        // input
        txnAnnoReader = mock( TransactionalAnnotationReader.class );

        // environment
        invocation = mock( MethodInvocation.class );
        txnal = mock( Transactional.class );

        doReturn( txnal ).when( txnAnnoReader ).readAnnotationFrom( invocation );
    }

    public class WithoutPuAnnotation
    {
        @Before
        public void setUp()
            throws Exception
        {
            // input
            puAnntoation = null;

            // subject under test
            sut = new TransactionalAnnotationHelper( new AnnotationHolder( puAnntoation ), txnAnnoReader );
        }

        @Test
        public void participatesInTxnWhenUnitsIsNull()
        {
            doReturn( null ).when( txnal ).onUnits();
            final boolean result = sut.persistenceUnitParticipatesInTransactionFor( invocation );
            assertThat( result, is( true ) );
        }

        @Test
        public void participatesInTxnWhenUnitsIsEmpty()
        {
            doReturn( new Class[]{ } ).when( txnal ).onUnits();
            final boolean result = sut.persistenceUnitParticipatesInTransactionFor( invocation );
            assertThat( result, is( true ) );
        }

        @Test
        public void participatesInTxnWhenUnitsContainsPuAnnotation()
        {
            doReturn( new Class[]{ TestPersistenceUnit.class } ).when( txnal ).onUnits();
            final boolean result = sut.persistenceUnitParticipatesInTransactionFor( invocation );
            assertThat( result, is( true ) );
        }

        @Test
        public void participatesInTxnWhenUnitsContainsNotPuAnnotation()
        {
            doReturn( new Class[]{ OtherPersistenceUnit.class } ).when( txnal ).onUnits();
            final boolean result = sut.persistenceUnitParticipatesInTransactionFor( invocation );
            assertThat( result, is( true ) );
        }

    }

    public class WithPuAnnotation
    {
        @Before
        public void setUp()
            throws Exception
        {
            // input
            puAnntoation = TestPersistenceUnit.class;

            // subject under test
            sut = new TransactionalAnnotationHelper( new AnnotationHolder( puAnntoation ), txnAnnoReader );
        }

        @Test
        public void participatesInTxnWhenUnitsIsNull()
        {
            doReturn( null ).when( txnal ).onUnits();
            final boolean result = sut.persistenceUnitParticipatesInTransactionFor( invocation );
            assertThat( result, is( true ) );
        }

        @Test
        public void participatesInTxnWhenUnitsIsEmpty()
        {
            doReturn( new Class[]{ } ).when( txnal ).onUnits();
            final boolean result = sut.persistenceUnitParticipatesInTransactionFor( invocation );
            assertThat( result, is( true ) );
        }

        @Test
        public void participatesInTxnWhenUnitsContainsPuAnnotation()
        {
            doReturn( new Class[]{ TestPersistenceUnit.class } ).when( txnal ).onUnits();
            final boolean result = sut.persistenceUnitParticipatesInTransactionFor( invocation );
            assertThat( result, is( true ) );
        }

        @Test
        public void participatesNotInTxnWhenUnitsContainsNotPuAnnotation()
        {
            doReturn( new Class[]{ OtherPersistenceUnit.class } ).when( txnal ).onUnits();
            final boolean result = sut.persistenceUnitParticipatesInTransactionFor( invocation );
            assertThat( result, is( false ) );
        }

    }

    public class RollbackOnIllegalArgumentExceptionIgnoreIllegalFormatException
    {
        @Before
        public void setUp()
            throws Exception
        {
            // input
            puAnntoation = null;

            // subject under test
            sut = new TransactionalAnnotationHelper( new AnnotationHolder( puAnntoation ), txnAnnoReader );

            doReturn( new Class[]{ IllegalArgumentException.class, IllegalStateException.class } )
                .when( txnal ).rollbackOn();
            doReturn( new Class[]{ IllegalFormatException.class, NumberFormatException.class } ).when( txnal ).ignore();
        }

        @Test
        public void shouldRollbackOnIllegalArgumentException()
        {
            Throwable exc = new IllegalArgumentException();
            final boolean result = sut.isRollbackNecessaryFor( invocation, exc );
            assertThat( result, is( true ) );
        }

        @Test
        public void shouldRollbackOnIllegalStateException()
        {
            Throwable exc = new IllegalStateException();
            final boolean result = sut.isRollbackNecessaryFor( invocation, exc );
            assertThat( result, is( true ) );
        }

        @Test
        public void shouldRollbackOnInvalidParameterException()
        {
            Throwable exc = new InvalidParameterException();
            final boolean result = sut.isRollbackNecessaryFor( invocation, exc );
            assertThat( result, is( true ) );
        }

        @Test
        public void shouldNotRollbackOnNumberFormatException()
        {
            Throwable exc = new NumberFormatException();
            final boolean result = sut.isRollbackNecessaryFor( invocation, exc );
            assertThat( result, is( false ) );
        }

        @Test
        public void shouldNotRollbackOnMissingFormatArgumentException()
        {
            Throwable exc = new MissingFormatArgumentException( "" );
            final boolean result = sut.isRollbackNecessaryFor( invocation, exc );
            assertThat( result, is( false ) );
        }

        @Test
        public void shouldNotRollbackOnRuntimeException()
        {
            Throwable exc = new RuntimeException();
            final boolean result = sut.isRollbackNecessaryFor( invocation, exc );
            assertThat( result, is( false ) );
        }

    }

}
