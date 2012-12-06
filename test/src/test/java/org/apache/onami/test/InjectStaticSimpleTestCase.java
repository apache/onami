package org.apache.onami.test;

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

import org.apache.onami.test.JUniceRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.apache.onami.test.data.ComplexModule;
import org.apache.onami.test.data.HelloWorld;
import org.apache.onami.test.data.SimpleModule;
import org.apache.onami.test.data.WhoIm;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Module;

@RunWith( JUniceRunner.class )
@GuiceModules( SimpleModule.class )
public class InjectStaticSimpleTestCase
{

    /*
     * Any static filed will be injecteded once before creation of SimpleTest Class
     */
    @Inject
    public static HelloWorld helloWorld;

    @Inject
    public static WhoIm whoIm;

    @GuiceProvidedModules
    public static Module createComplexModule()
    {
        return new ComplexModule( "Marco Speranza" );
    }

    @Test
    public void testHelloWorld()
    {
        Assert.assertNotNull( helloWorld );
        Assert.assertEquals( "Hello World!!!!", helloWorld.sayHallo() );
    }

    @Test
    public void testWhoIm()
    {
        Assert.assertNotNull( whoIm );
        Assert.assertEquals( "Marco Speranza", whoIm.sayWhoIm() );
    }

}
