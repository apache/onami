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

/**
 * This is the main control to the entire persistence engine. Before calling any other method
 * of either {@link UnitOfWork}, {@link EntityManagerProvider}, or any method annotated with
 * {@link Transactional @Transactional} the persistence service must be started.
 */
public interface PersistenceService
{

    /**
     * Starts the underlying persistence engine and makes onami-persist ready for use.
     * This method must be called by your code prior to using any other onami-persist artifacts.
     * If you are using onami-persist in a web container {@link PersistenceFilter} will call this
     * method upon initialization of the web application.
     *
     * @throws IllegalStateException if the service is already running.
     */
    void start();

    /**
     * @return {@code true} if the underlying persistence engine is running.
     *         {@code false} otherwise.
     */
    boolean isRunning();

    /**
     * Stops the underlying persistence engine.
     * <ul>
     * <li>If already stopped, calling this method does nothing.</li>
     * <li>If not yet started, it also does nothing.</li>
     * </ul>
     */
    void stop();

}
