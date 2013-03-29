package org.apache.onami.converters.system;

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

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.apache.onami.converters.core.AbstractConverter;
import org.kohsuke.MetaInfServices;

import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;

/**
 * Converter implementation for {@code java.util.Properties}.
 */
@MetaInfServices( Module.class )
public final class PropertiesConverter
    extends AbstractConverter<Properties>
{

    /**
     * {@inheritDoc}
     */
    public Object convert( String value, TypeLiteral<?> toType )
    {
        Properties properties = new Properties();

        try
        {
            properties.load( new StringReader( value ) );
        }
        catch ( IOException e )
        {
            // Should never happen.
            throw new ProvisionException( "Failed to parse '" + value + "' into Properties", e );
        }

        return properties;
    }

}
