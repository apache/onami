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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;

import static java.util.Arrays.asList;
import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Helper class for working with {@link Transactional @Transactional} annotations.
 */
@Singleton
class TransactionalAnnotationHelper
{

    /**
     * Annotation of the persistence unit.
     */
    private final Class<? extends Annotation> puAnntoation;

    /**
     * Reader for {@link Transactional @Transactional} annotations.
     */
    private final TransactionalAnnotationReader txnAnnoReader;

    /**
     * Constructor.
     *
     * @param annotationHolder Holder of teh annotation of the persistence unit.
     * @param txnAnnoReader    reader for {@link Transactional @Transactional} annotations.
     */
    @Inject
    TransactionalAnnotationHelper( AnnotationHolder annotationHolder, TransactionalAnnotationReader txnAnnoReader )
    {
        this.puAnntoation = annotationHolder.getAnnotation();
        this.txnAnnoReader = checkNotNull( txnAnnoReader, "txnAnnoReader is mandatory!" );
    }

    /**
     * Decides if the current persistence unit participates in a transaction for the given method invocation.
     * For a detailed description of when a persistence unit participates see the documentation at the
     * {@link Transactional @Transactional} annotation.
     *
     * @param methodInvocation the method invocation which may be wrapped in a transaction.
     * @return {@code true} if the current persistence unit participates in a transaction for the given method.
     */
    public boolean persistenceUnitParticipatesInTransactionFor( MethodInvocation methodInvocation )
    {
        return puAnntoation == null || participates( methodInvocation );
    }

    /**
     * Decides if the current persistence unit participates in a transaction for the given method invocation.
     * The persistence unit has is annotated.
     *
     * @param methodInvocation the method invocation which may be wrapped in a transaction.
     * @return {@code true} if the current persistence unit participates in a transaction for the given method.
     */
    private boolean participates( MethodInvocation methodInvocation )
    {
        final Transactional transactional = txnAnnoReader.readAnnotationFrom( methodInvocation );
        final Class<? extends Annotation>[] onUnits = transactional.onUnits();
        return isEmpty( onUnits ) || contains( onUnits, puAnntoation );
    }

    /**
     * Returns {@code true} if the given array is empty.
     *
     * @param array the array to test for emptiness.
     * @return {@code true} if the the given array has is {@code null} or has length 0.
     */
    private boolean isEmpty( Object[] array )
    {
        return array == null || array.length == 0;
    }

    /**
     * Returns {@code true} if the given array contains the specified element.
     *
     * @param array the array in which to search for the specified element.
     * @param key   the element to look for.
     * @return {@code true} if the given array contains the specified element.
     */
    private boolean contains( Object[] array, Object key )
    {
        return asList( array ).contains( key );
    }

    /**
     * Decides if a rollback is necessary for the given method invocation and a thrown exception.
     *
     * @param methodInvocation the method invocation during which an exception was thrown.
     * @param exc              the exception which was thrown
     * @return {@code true} if the transaction needs to be rolled back.
     */
    public boolean isRollbackNecessaryFor( MethodInvocation methodInvocation, Throwable exc )
    {
        final Transactional transactional = txnAnnoReader.readAnnotationFrom( methodInvocation );
        return isRollbackNecessaryFor( transactional, exc );
    }

    /**
     * Decides if a rollback is necessary for the given transactional annotation and a thrown exception.
     *
     * @param transactional the transactional annotation of the method during which an exception was thrown.
     * @param exc           the exception which was thrown
     * @return {@code true} if the transaction needs to be rolled back.
     */
    private boolean isRollbackNecessaryFor( Transactional transactional, Throwable exc )
    {
        for ( Class<? extends Exception> rollbackOn : transactional.rollbackOn() )
        {
            if ( rollbackOn.isInstance( exc ) )
            {
                return isNotIgnoredForRollback( transactional, exc );
            }
        }
        return false;
    }

    /**
     * Decides if a rollback is not performed for the given transactional annotation and a thrown exception.
     *
     * @param transactional the transactional annotation of the method during which an exception was thrown.
     * @param exc           the exception which was thrown
     * @return {@code true} if the transaction needs to be rolled back.
     */
    private boolean isNotIgnoredForRollback( Transactional transactional, Throwable exc )
    {
        for ( Class<? extends Exception> ignore : transactional.ignore() )
        {
            if ( ignore.isInstance( exc ) )
            {
                return false;
            }
        }
        return true;
    }
}
