package org.nnsoft.guice.sli4j.log4j;

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

import static com.google.inject.TypeLiteral.get;
import static com.google.inject.matcher.Matchers.only;

import org.apache.log4j.Logger;
import org.nnsoft.guice.sli4j.core.InjectLogger;
import org.nnsoft.guice.sli4j.testfw.AbstractLoggerInectionTestCase;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 */
public final class Log4jLoggingTestCase
    extends AbstractLoggerInectionTestCase<Logger>
{

    @InjectLogger
    private Logger logger;

    @BeforeTest
    public void setUp()
    {
        super.setUp( new Log4jLoggingModule( only( get( this.getClass() ) ) ) );
    }

    @Test
    public void injectAndVerify()
    {
        this.injectAndVerify( this.logger );
    }

}
