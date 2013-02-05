package org.apache.onami.scheduler;

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

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;

import com.google.inject.Module;

@RunWith( OnamiRunner.class )
public class ManualStartTestCase
{

    @GuiceProvidedModules
    public static Module createTestModule()
    {
        return new QuartzModule()
        {

            @Override
            protected void schedule()
            {
                configureScheduler().withManualStart();
                scheduleJob( TimedTask.class );
            }

        };
    }

    @Inject
    private Scheduler scheduler;

    @Inject
    private TimedTask timedTask;

    @After
    public void tearDown()
        throws Exception
    {
        scheduler.shutdown();
    }

    @Test
    public void testManualStart()
        throws Exception
    {
        Thread.sleep( 5000L );
        assertTrue( timedTask.getInvocationsTimedTaskA() == 0 );
        scheduler.start();
        Thread.sleep( 5000L );
        assertTrue( timedTask.getInvocationsTimedTaskA() > 0 );
    }

}
