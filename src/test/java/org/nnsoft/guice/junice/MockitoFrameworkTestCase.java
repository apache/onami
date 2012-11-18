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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nnsoft.guice.junice.JUniceRunner;
import org.nnsoft.guice.junice.annotation.Mock;

import com.google.inject.Inject;
import org.nnsoft.guice.junice.data.HelloWorld;
import org.nnsoft.guice.junice.data.TelephonService;

@RunWith( JUniceRunner.class )
public class MockitoFrameworkTestCase
    extends AbstractMockitoTestCase
{

    /*
     * Any NON-static filed will be injecteded before run each tests.
     */
    @Inject
    private HelloWorld helloWorldNotStatic;

    @Mock
    private TelephonService service;

    @BeforeClass
    public static void setUpClass()
    {
    }

    @Test
    public void testInjectNotStatic()
    {
        Assert.assertNotNull( helloWorldNotStatic );
        Assert.assertEquals( "Hello World!!!!", helloWorldNotStatic.sayHallo() );
        Assert.assertNotNull( service );
        Assert.assertNotNull( providedMock );
    }

}
