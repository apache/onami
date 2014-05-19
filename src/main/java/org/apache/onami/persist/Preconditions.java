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
 * Checks to ensure arguments are in a valid state.
 */
class Preconditions
{

    /**
     * Check that a reference is not null.
     *
     * @param <T>       the type of the reference
     * @param reference the reference to check.
     * @return the reference itself.
     * @throws NullPointerException if the reference is null.
     */
    static <T> T checkNotNull( T reference )
    {
        if ( reference == null )
        {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Check that a reference is not null.
     *
     * @param <T>       the type of the reference
     * @param reference the reference to check.
     * @param message   the message of the NullPointerException if one is thrown.
     * @return the reference itself.
     * @throws NullPointerException if the reference is null.
     */
    static <T> T checkNotNull( T reference, String message )
    {
        if ( reference == null )
        {
            throw new NullPointerException( message );
        }
        return reference;
    }

}
