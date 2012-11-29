package org.apache.onami.logging.log4j2;

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

import org.apache.logging.log4j.Logger;
import org.apache.onami.logging.core.InjectLogger;
import org.apache.onami.logging.testfw.AbstractLoggerInectionTestCase;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.google.inject.TypeLiteral.get;
import static com.google.inject.matcher.Matchers.only;

/**
 *
 */
public final class Log4j2LoggingTestCase
    extends AbstractLoggerInectionTestCase<Logger>
{
    @InjectLogger
    private Logger logger;

    @BeforeTest
    public void setUp()
    {
        super.setUp( new Log4j2LoggingModule( only( get( this.getClass() ) ) ) );
    }

    @Test
    public void injectAndVerify()
    {
        this.injectAndVerify( this.logger );
    }

}
