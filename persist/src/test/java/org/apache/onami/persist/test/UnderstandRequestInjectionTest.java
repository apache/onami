package org.apache.onami.persist.test;

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

import com.google.inject.Guice;
import javax.inject.Inject;
import com.google.inject.PrivateModule;
import org.junit.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for understanding how guice handles the requestInjection() method.
 */
public class UnderstandRequestInjectionTest
{
    @Test
    public void requestInjectionInOnePrivateModule()
        throws Exception
    {
        final ObjectWithSetterInjection obj = new ObjectWithSetterInjection();
        Guice.createInjector( new PrivateModule()
        {
            @Override
            protected void configure()
            {
                bind( Foo.class ).to( Foo1.class );
                requestInjection( obj );
            }
        } );

        obj.assertAddedTypes( 1 );
    }

    @Test
    public void requestInjectionInTwoPrivateModule()
        throws Exception
    {
        final ObjectWithSetterInjection obj = new ObjectWithSetterInjection();
        Guice.createInjector( new PrivateModule()
                              {
                                  @Override
                                  protected void configure()
                                  {
                                      bind( Foo.class ).to( Foo1.class );
                                      requestInjection( obj );
                                  }
                              }, //                                                                 //
                              new PrivateModule()
                              {
                                  @Override
                                  protected void configure()
                                  {
                                      bind( Foo.class ).to( Foo2.class );
                                  }
                              }
        );

        obj.assertAddedTypes( 1 );
    }

    private static class ObjectWithSetterInjection
    {
        private final Set<Integer> actuals = newHashSet();

        @Inject
        public void addFoo( Foo foo )
        {
            actuals.add( foo.type() );
        }

        void assertAddedTypes( Integer... types )
        {
            Set<Integer> expected = newHashSet( types );
            assertThat( actuals, is( expected ) );
        }
    }

    private interface Foo
    {
        int type();
    }

    private static class Foo1
        implements Foo
    {
        public int type()
        {
            return 1;
        }
    }

    private static class Foo2
        implements Foo
    {
        public int type()
        {
            return 2;
        }
    }
}
