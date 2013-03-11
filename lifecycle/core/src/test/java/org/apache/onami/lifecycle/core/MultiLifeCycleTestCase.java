package org.apache.onami.lifecycle.core;

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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.Assert;
import org.junit.Test;

public class MultiLifeCycleTestCase
{
    @Test
    public void testOrdering()
    {
        LifeCycleModule lifeCycleModule = new LifeCycleModule(
            ListBuilder.builder().append( TestAnnotationA.class ).append( TestAnnotationB.class ).append(
                TestAnnotationC.class ).build() );
        MultiLifeCycleObject obj = Guice.createInjector( lifeCycleModule ).getInstance( MultiLifeCycleObject.class );
        Assert.assertEquals( "aaabbbc", obj.toString() );
    }

    public static class Foo
    {
        @Inject
        public Foo( Stager<TestAnnotationA> stager )
        {
            System.out.println( stager.getStage() );
        }
    }

    @Test
    public void testStaging()
    {
        LifeCycleStageModule<TestAnnotationA> moduleA =
            new LifeCycleStageModule<TestAnnotationA>( DefaultStager.newStager( TestAnnotationA.class ) );
        LifeCycleStageModule<TestAnnotationB> moduleB =
            new LifeCycleStageModule<TestAnnotationB>( DefaultStager.newStager( TestAnnotationB.class ) );
        LifeCycleStageModule<TestAnnotationC> moduleC =
            new LifeCycleStageModule<TestAnnotationC>( DefaultStager.newStager( TestAnnotationC.class ) );

        Injector injector = Guice.createInjector( moduleA, moduleB, moduleC );
        MultiLifeCycleObject obj = injector.getInstance( MultiLifeCycleObject.class );

        Assert.assertEquals( obj.toString(), "" );

        injector.getInstance( LifeCycleStageModule.key( TestAnnotationA.class ) ).stage();
        Assert.assertEquals( "aaa", obj.toString() );
        injector.getInstance( LifeCycleStageModule.key( TestAnnotationB.class ) ).stage();
        Assert.assertEquals( "aaabbb", obj.toString() );
        injector.getInstance( LifeCycleStageModule.key( TestAnnotationC.class ) ).stage();
        Assert.assertEquals( "aaabbbc", obj.toString() );

        injector.getInstance( Foo.class );
    }

    @Test
    public void testStagingOrdering()
    {
        LifeCycleStageModule<TestAnnotationA> moduleA =
            new LifeCycleStageModule<TestAnnotationA>( DefaultStager.newStager( TestAnnotationA.class, DefaultStager.Order.FIRST_IN_FIRST_OUT ) );
        LifeCycleStageModule<TestAnnotationB> moduleB =
            new LifeCycleStageModule<TestAnnotationB>( DefaultStager.newStager( TestAnnotationB.class, DefaultStager.Order.FIRST_IN_LAST_OUT ) );

        final StringBuilder str = new StringBuilder();
        Module m = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                binder().bind( StringBuilder.class ).toInstance( str );
            }
        };

        Injector injector = Guice.createInjector( moduleA, moduleB, m );
        injector.getInstance( StageObject1.class );
        injector.getInstance( StageObject2.class );

        injector.getInstance( LifeCycleStageModule.key( TestAnnotationA.class ) ).stage();
        Assert.assertEquals( "1a2a", str.toString() );
        str.setLength( 0 );

        injector.getInstance( LifeCycleStageModule.key( TestAnnotationB.class ) ).stage();
        Assert.assertEquals( "2b1b", str.toString() );
    }
}
