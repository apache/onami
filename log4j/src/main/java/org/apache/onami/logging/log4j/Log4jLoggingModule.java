package org.apache.onami.logging.log4j;

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

import static com.google.inject.matcher.Matchers.any;

import org.apache.log4j.Logger;
import org.apache.onami.logging.core.AbstractLoggingModule;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

/**
 * {@code Apache Log4j} logger module implementation.
 */
public final class Log4jLoggingModule extends AbstractLoggingModule<Logger> {

    /**
     * Creates a new {@code Apache Log4j} injection module that matches any class.
     *
     * @since 3.1
     */
    public Log4jLoggingModule()
    {
        super( any(), Log4JLoggerInjector.class );
    }

    /**
     * Creates a new {@code Apache Log4j} injection module.
     *
     * @param matcher types matcher for whom the Logger injection has to be
     *        performed.
     */
    public Log4jLoggingModule( Matcher<? super TypeLiteral<?>> matcher )
    {
        super( matcher, Log4JLoggerInjector.class );
    }

}
