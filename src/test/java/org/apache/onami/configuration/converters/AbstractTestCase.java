package org.apache.onami.configuration.converters;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertEquals;

import org.junit.Before;

import com.google.inject.Module;

/**
 *
 */
public abstract class AbstractTestCase<T>
{

    private T convertedField;

    protected void setConvertedField( T convertedField )
    {
        this.convertedField = convertedField;
    }

    @Before
    public final void init()
    {
        createInjector( this.getModules() ).injectMembers( this );
    }

    protected abstract Module[] getModules();

    protected final void verifyConversion( T expected )
    {
        assertEquals( expected, this.convertedField );
    }

}
