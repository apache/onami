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

import javax.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Interceptor for methods and classes annotated with {@link Transactional @Transactional} annotation.
 */
class TxnInterceptor
    implements MethodInterceptor
{

    /**
     * Unit of work.
     */
    private UnitOfWork unitOfWork;

    /**
     * Factory for {@link TransactionFacade}.
     */
    private TransactionFacadeFactory tfProvider;

    /**
     * Helper for working with the concrete transactional annotations on methods and classes.
     */
    private TransactionalAnnotationHelper txnAnnotationHelper;

    @Inject
    @VisibleForTesting
    void init( UnitOfWork unitOfWork, TransactionFacadeFactory tfProvider,
               TransactionalAnnotationHelper txnAnnotationHelper )
    {
        this.unitOfWork = unitOfWork;
        this.tfProvider = tfProvider;
        this.txnAnnotationHelper = txnAnnotationHelper;
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public final Object invoke( MethodInvocation methodInvocation )
        throws Throwable
    {
        if ( persistenceUnitParticipatesInTransactionFor( methodInvocation ) )
        {
            return invokeInTransactionAndUnitOfWork( methodInvocation );
        }
        else
        {
            return methodInvocation.proceed();
        }

    }

    /**
     * Decides if the current persistence unit participates in a transaction for the given method invocation.
     * For a detailed description of when a persistence unit participates see the documentation at the
     * {@link Transactional @Transactional} annotation.
     *
     * @param methodInvocation the method invocation which may be wrapped in a transaction.
     * @return {@code true} if the current persistence unit participates in a transaction for the given method.
     */
    private boolean persistenceUnitParticipatesInTransactionFor( MethodInvocation methodInvocation )
    {
        return txnAnnotationHelper.persistenceUnitParticipatesInTransactionFor( methodInvocation );
    }

    /**
     * Invokes the original method within a unit of work and a transaction.
     *
     * @param methodInvocation the method to be executed within the transaction
     * @return the result of the invocation of the original method.
     * @throws Throwable if an exception occurs during the call to the original method.
     */
    private Object invokeInTransactionAndUnitOfWork( MethodInvocation methodInvocation )
        throws Throwable
    {
        final boolean weStartedTheUnitOfWork = !unitOfWork.isActive();
        if ( weStartedTheUnitOfWork )
        {
            unitOfWork.begin();
        }

        Throwable originalException = null;
        try
        {
            return invokeInTransaction( methodInvocation );
        }
        catch ( Throwable exc )
        {
            originalException = exc;
            throw exc;
        }
        finally
        {
            if ( weStartedTheUnitOfWork )
            {
                endUnitOfWork( originalException );
            }
        }
    }

    /**
     * Ends the unit of work. If an exception occurs while ending the unit of work it is neglected in preference of an
     * original exception.
     *
     * @param originalException the original exception. will be thrown in preference to an exception occurring during
     *                          execution of this method.
     * @throws Throwable if an exception happened while ending the unit of work.
     */
    private void endUnitOfWork( Throwable originalException )
        throws Throwable
    {
        try
        {
            unitOfWork.end();
        }
        catch ( Throwable exc )
        {
            if ( originalException != null )
            {
                throw originalException;
            }
            else
            {
                throw exc;
            }
        }
    }

    /**
     * Invoke the original method within a transaction.
     *
     * @param methodInvocation the original method invocation.
     * @return the result of the invocation of the original method.
     * @throws Throwable if an exception occurs during the call to the original method.
     */
    private Object invokeInTransaction( MethodInvocation methodInvocation )
        throws Throwable
    {
        final TransactionFacade transactionFacade = tfProvider.createTransactionFacade();
        transactionFacade.begin();
        final Object result = invokeAndHandleException( methodInvocation, transactionFacade );
        transactionFacade.commit();

        return result;
    }

    /**
     * Invoke the original method assuming a transaction has already been started.
     * This method is responsible of calling rollback if necessary.
     *
     * @param methodInvocation  the original method invocation.
     * @param transactionFacade the facade to the underlying resource local or jta transaction.
     * @return the result of the invocation of the original method.
     * @throws Throwable if an exception occurs during the call to the original method.
     */
    private Object invokeAndHandleException( MethodInvocation methodInvocation, TransactionFacade transactionFacade )
        throws Throwable
    {
        try
        {
            return methodInvocation.proceed();
        }
        catch ( Throwable exc )
        {
            handleException( methodInvocation, transactionFacade, exc );
            throw exc;
        }
    }

    /**
     * Handles the case that an exception was thrown by the original method.
     *
     * @param methodInvocation  the original method invocation.
     * @param transactionFacade the facade to the underlying resource local or jta transaction.
     * @param exc               the exception thrown by the original method.
     */
    private void handleException( MethodInvocation methodInvocation, TransactionFacade transactionFacade,
                                  Throwable exc )
        throws Throwable
    {
        try
        {
            if ( isRollbackNecessaryFor( methodInvocation, exc ) )
            {
                transactionFacade.rollback();
            }
            else
            {
                transactionFacade.commit();
            }
        }
        catch ( Exception swallowedException )
        {
            // swallow exception from transaction facade in favor of th exception thrown by the original method.
            throw exc;
        }
    }

    /**
     * Decides if a rollback is necessary for the given method invocation and a thrown exception.
     *
     * @param methodInvocation the method invocation during which an exception was thrown.
     * @param exc              the exception which was thrown
     * @return {@code true} if the transaction needs to be rolled back.
     */
    private boolean isRollbackNecessaryFor( MethodInvocation methodInvocation, Throwable exc )
    {
        return txnAnnotationHelper.isRollbackNecessaryFor( methodInvocation, exc );
    }

}
