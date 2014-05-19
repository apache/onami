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

import javax.persistence.EntityManager;

/**
 * The Unit of work correlates with the life cycle of the {@link EntityManager}.
 * According to JPA every thread should use its own {@link EntityManager}. Therefore the unit of
 * work will control the life cycle of the {@link EntityManager} on a per thread basis. This means
 * the UnitOfWork is thread safe.
 * <p/>
 * Most of the time it is not recommended to manual control the unit of work.
 * <p/>
 * For applications running in a container the {@link PersistenceFilter} is recommended.
 * It will start a unit of work for every incoming request and properly close it at the end.
 * <p/>
 * For stand alone application it is recommended to relay on the {@link Transactional @Transactional} annotation.
 * The transaction handler will automatically span a unit of work around a transaction.
 * <p/>
 * The most likely scenario in which one would want to take manual control over the unit of work
 * is in a background thread within a container (i.e. timer triggered jobs).
 * <p/>
 * Recommended pattern:
 * <pre>
 * public void someMethod() {
 *   final boolean unitOfWorkWasInactive = ! unitOfWork.isActive();
 *   if (unitOfWorkWasInactive) {
 *     unitOfWork.begin();
 *   }
 *   try {
 *     // do work
 *   }
 *   finally {
 *     if (unitOfWorkWasInactive) {
 *       unitOfWork.end();
 *     }
 *   }
 * }
 * </pre>
 */
public interface UnitOfWork
{

    /**
     * Begins the unit of work.
     * When a unit of work has already been started for the current thread an {@link IllegalStateException} is thrown.
     *
     * @throws IllegalStateException if a unit of work is already active for this thread.
     */
    void begin();

    /**
     * @return {@code true} if the unit of work is active for the current thread
     *         {@code false} otherwise.
     */
    boolean isActive();

    /**
     * Ends the unit of work.
     * When the unit of work is not active this method will do nothing.
     */
    void end();

}
