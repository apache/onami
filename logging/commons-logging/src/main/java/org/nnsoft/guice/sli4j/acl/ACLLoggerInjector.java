package org.nnsoft.guice.sli4j.acl;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nnsoft.guice.sli4j.core.AbstractLoggerInjector;

/**
 * {@code Apache Commons Logging} logger injector implementation.
 */
public final class ACLLoggerInjector
    extends AbstractLoggerInjector<Log>
{

    /**
     * Creates a new {@code Apache Commons Logging} Logger injector.
     *
     * @param field the logger field has to be injected.
     */
    public ACLLoggerInjector( Field field )
    {
        super( field );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Log createLogger( Class<?> klass )
    {
        return LogFactory.getLog( klass );
    }

}
