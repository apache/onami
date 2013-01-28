package org.apache.onami.lifecycle;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A {@link MethodDisposable} is a reference to a disposable injectee
 * and related method to release resources.
 *
 * @since 0.2.0
 */
public final class MethodDisposable implements Disposable
{

    /**
     * The method to be invoked to release resources.
     */
    private final Method disposeMethod;

    /**
     * The target injectee has to release the resources.
     */
    private final Object injectee;

    /**
     * Creates a new {@link MethodDisposable} reference.
     *
     * @param disposeMethod the method to be invoked to release resources.
     * @param injectee the target injectee has to release the resources.
     * @since 0.2.0
     */
    public MethodDisposable( Method disposeMethod, Object injectee )
    {
        this.disposeMethod = disposeMethod;
        this.injectee = injectee;
    }

    /**
     * Disposes allocated resources by invoking the injectee method,
     * tracking progresses in the input {@link DisposeHandler}.
     *
     * @param disposeHandler the handler to track dispose progresses.
     * @since 0.2.0
     */
    public void dispose( DisposeHandler disposeHandler )
    {
        try
        {
            disposeMethod.invoke( injectee );
            disposeHandler.onSuccess( injectee );
        }
        catch ( IllegalArgumentException e )
        {
            disposeHandler.onError( injectee, e );
        }
        catch ( IllegalAccessException e )
        {
            disposeHandler.onError( injectee, e );
        }
        catch ( InvocationTargetException e )
        {
            disposeHandler.onError( injectee, e.getCause() );
        }
    }

}