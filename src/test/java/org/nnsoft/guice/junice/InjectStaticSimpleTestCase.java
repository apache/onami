package org.nnsoft.guice.junice;

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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nnsoft.guice.junice.JUniceRunner;
import org.nnsoft.guice.junice.annotation.GuiceModules;
import org.nnsoft.guice.junice.annotation.GuiceProvidedModules;

import com.google.inject.Inject;
import com.google.inject.Module;
import org.nnsoft.guice.junice.data.ComplexModule;
import org.nnsoft.guice.junice.data.HelloWorld;
import org.nnsoft.guice.junice.data.SimpleModule;
import org.nnsoft.guice.junice.data.WhoIm;

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
