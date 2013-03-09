package org.apache.onami.lifecycle.standard;

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
 * A Disposer is a mini-container that releases resources
 * invoking {@link Disposable#dispose(DisposeHandler)}.
 * <p>
 * Order of disposal is reverse to the order of registration. Each disposable
 * may register zero or more objects for disposal too.
 * <p>
 * Implementations must be threadsafe because registration can be done from
 * any thread.
 *
 * @since 0.2.0
 */
public interface Disposer
{

    /**
     * Register a {@link Disposable} to release resources.
     *
     * @param disposable object to be invoked to release resources.
     * @since 0.2.0
     */
    void register( Disposable disposable );

    /**
     * Releases resources invoking {@link Disposable#dispose(DisposeHandler)}.
     *
     * @since 0.2.0
     */
    void dispose();

    /**
     * Releases resources invoking {@link Disposable#dispose(DisposeHandler)}.
     *
     * @param disposeHandler the {@link DisposeHandler} instance that tracks dispose progresses.
     * @since 0.2.0
     */
    void dispose( DisposeHandler disposeHandler );

}