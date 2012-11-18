package org.nnsoft.guice.sli4j.slf4j;

/*
 *    Copyright 2010-2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import static com.google.inject.matcher.Matchers.any;

import org.nnsoft.guice.sli4j.core.AbstractLoggingModule;
import org.slf4j.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

/**
 * {@code SLF4J} logger module implementation.
 */
public final class Slf4jLoggingModule extends AbstractLoggingModule<Logger> {

    /**
     * Creates a new {@code SLF4J} injection module that matches any class.
     *
     * @since 3.1
     */
    public Slf4jLoggingModule()
    {
        super( any(), Slf4jLoggerInjector.class );
    }

    /**
     * Creates a new {@code SLF4J} injection module.
     *
     * @param matcher types matcher for whom the Logger injection has to be
     *        performed.
     */
    public Slf4jLoggingModule( Matcher<? super TypeLiteral<?>> matcher )
    {
        super( matcher, Slf4jLoggerInjector.class );
    }

}
