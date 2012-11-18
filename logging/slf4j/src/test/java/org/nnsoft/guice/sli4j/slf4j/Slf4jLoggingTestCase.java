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

import org.nnsoft.guice.sli4j.core.InjectLogger;
import org.nnsoft.guice.sli4j.testfw.AbstractLoggerInectionTestCase;
import org.slf4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

/**
 *
 */
public final class Slf4jLoggingTestCase
    extends AbstractLoggerInectionTestCase<Logger>
{

    @InjectLogger
    private Logger logger;

    @BeforeTest
    public void setUp()
    {
        super.setUp( new Slf4jLoggingModule( Matchers.only( TypeLiteral.get( this.getClass() ) ) ) );
    }

    @Test
    public void injectAndVerify()
    {
        this.injectAndVerify( this.logger );
    }

}
