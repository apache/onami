package org.apache.onami.guava.eventbus;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Module;

@RunWith( OnamiRunner.class )
public final class EventBusModuleTestCase
{

    @GuiceProvidedModules
    public static Module createTestModule()
    {
        return new EventBusModule()
        {

            @Override
            protected void configure()
            {
                bindBus( "eventbus.test" ).toAnyBoundClass();
            }

        };
    }

    @Inject
    @Named( "eventbus.test" )
    private EventBus eventBus;

    private boolean eventNotified = false;

    public void setEventBus( EventBus eventBus )
    {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void when( ApplicationEvent applicationEvent )
    {
        eventNotified = true;
    }

    @After
    public void tearDown()
    {
        eventBus = null;
    }

    @Test
    public void eventBusNotNull()
    {
        assertNotNull( eventBus );
    }

    @Test
    public void eventNotified()
    {
        eventBus.post( new ApplicationEvent() );
        assertTrue( eventNotified );
    }

}
