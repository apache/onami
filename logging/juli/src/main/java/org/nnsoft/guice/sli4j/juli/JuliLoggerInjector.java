package org.nnsoft.guice.sli4j.juli;

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

import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.nnsoft.guice.sli4j.core.AbstractLoggerInjector;

/**
 * {@code java.util.logging.Logger} logger injector implementation.
 */
public final class JuliLoggerInjector extends AbstractLoggerInjector<Logger> {

    /**
     * Creates a new {@code java.util.logging.Logger} Logger injector.
     *
     * @param field the logger field has to be injected.
     */
    public JuliLoggerInjector( Field field )
    {
        super( field );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger createLogger( Class<?> klass )
    {
        return Logger.getLogger( klass.getName() );
    }

}
