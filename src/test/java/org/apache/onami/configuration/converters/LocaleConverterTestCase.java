package org.apache.onami.configuration.converters;

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

import static com.google.inject.name.Names.named;
import static com.google.inject.util.Modules.combine;

import java.util.Locale;

import org.apache.onami.configuration.converters.LocaleConverter;
import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;

/**
 *
 */
@RunWith( OnamiRunner.class )
public final class LocaleConverterTestCase
    extends AbstractTestCase<Locale>
{

    @GuiceProvidedModules
    public static Module createTestModule()
    {
        return combine( new LocaleConverter(), new AbstractModule()
        {
            protected void configure()
            {
                bindConstant().annotatedWith( named( "locale" ) ).to( "en_US" );
            };
        } );
    }

    @Override
    @Inject
    public void setConvertedField( @Named( "locale" ) Locale convertedField )
    {
        super.setConvertedField( convertedField );
    }

    @Test
    public void locale()
    {
        verifyConversion( new Locale( "en", "US" ) );
    }

}
