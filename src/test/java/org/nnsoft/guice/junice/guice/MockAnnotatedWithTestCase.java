package org.nnsoft.guice.junice.guice;

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

import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nnsoft.guice.junice.JUniceRunner;
import org.nnsoft.guice.junice.annotation.Mock;

import com.google.inject.Inject;
import org.nnsoft.guice.junice.data.HelloWordAnnotated;
import org.nnsoft.guice.junice.data.Service;
import org.nnsoft.guice.junice.data.TestAnnotation;

@RunWith( JUniceRunner.class )
public class MockAnnotatedWithTestCase
{

    @Mock( annotatedWith = TestAnnotation.class )
    private Service service2;

    @Mock
    private Service service;

    @Mock( namedWith = "test.named" )
    private Service serviceNamed;

    @Inject
    private HelloWordAnnotated seviceImplAnnotated;

    @Test
    public void test()
        throws Exception
    {
        Assert.assertNotNull( service2 );
        Assert.assertNotNull( service );
        Assert.assertNotNull( serviceNamed );
    }

    @Test
    public void test3()
        throws Exception
    {
        Assert.assertNotNull( service2 );
        Assert.assertNotNull( serviceNamed );

        EasyMock.expect( service2.go() ).andReturn( "Mocked injected class annotated" ).anyTimes();
        EasyMock.expect( serviceNamed.go() ).andReturn( "Mocked injected class named" ).anyTimes();
        EasyMock.replay( service2, serviceNamed );

        Assert.assertEquals( "Mocked injected class annotated", service2.go() );
        Assert.assertEquals( "Mocked injected class annotated", seviceImplAnnotated.go() );
        Assert.assertEquals( "Mocked injected class named", seviceImplAnnotated.getNamed() );
        EasyMock.verify( service2 );
    }

}
