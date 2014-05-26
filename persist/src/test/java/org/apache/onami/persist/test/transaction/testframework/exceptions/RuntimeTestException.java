package org.apache.onami.persist.test.transaction.testframework.exceptions;

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
 * Exception which can be thrown by a {@link org.apache.onami.persist.test.transaction.testframework.TransactionalTask}.
 */
public class RuntimeTestException
    extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    private final String message;

    /**
     * Constructor.
     */
    public RuntimeTestException()
    {
        this.message = RuntimeTestException.class.getSimpleName();
    }

    /**
     * Constructor.
     *
     * @param message the message of the exception.
     */
    public RuntimeTestException( String message )
    {
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
