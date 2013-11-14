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

import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

/**
 * Interface which hides away the details of inner (nested) and outer transactions as well
 * as the details between {@link EntityTransaction} and {@link UserTransaction}.
 */
interface TransactionFacade
{

    /**
     * Starts a transaction.
     * <p/>
     * The first call to begin will start the actual transaction. Subsequent calls will start a
     * 'nested' transaction.
     */
    void begin();

    /**
     * Commits a transaction.
     * <p/>
     * Only the actual transaction can be committed. Calls to commit on nested transactions have
     * no effect.
     */
    void commit();

    /**
     * Rolls a transaction back.
     * <p/>
     * Only the actual transaction can be rolled back. Calls to rollback on nested transactions will
     * set the onlyRollBack flag on the actual transaction. Setting this flag wil cause an actual
     * transaction to be rolled back in any case.
     */
    void rollback();

}
