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

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class InjectFromSuperClassTestCase
    extends AbstractTestCase
{

    @Inject
    @Named( "test.info.inject" )
    private String info;

    @Inject
    @Named( "test.info.inject2" )
    private String infoFromIterable;

    @Inject
    @Named( "test.info.inject3" )
    private String infoFromArray;

    @Test
    public void testInjectFromSuperClass()
    {
        Assert.assertNotNull( info );
        Assert.assertEquals( "JUnice = JUnit + Guice", info );

        Assert.assertNotNull( infoFromIterable );
        Assert.assertEquals( "JUnice = JUnit + Guice Iterable", infoFromIterable );

        Assert.assertNotNull( infoFromArray );
        Assert.assertEquals( "JUnice = JUnit + Guice Array", infoFromArray );
    }

}
