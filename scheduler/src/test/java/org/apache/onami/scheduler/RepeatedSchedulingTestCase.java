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

import static junit.framework.Assert.assertTrue;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;

import com.google.inject.Inject;
import com.google.inject.Module;

@RunWith( OnamiRunner.class )
public class RepeatedSchedulingTestCase
{

    @GuiceProvidedModules
    public static Module createTestModule()
    {
        return new QuartzModule()
        {

            @Override
            protected void schedule()
            {
                scheduleJob( TimedTask.class ).updateExistingTrigger();
                scheduleJob( TimedTask.class ).updateExistingTrigger();
            }

        };
    }

    @Inject
    private TimedTask timedTask;

    @Inject
    private Scheduler scheduler;

    @After
    public void tearDown()
        throws Exception
    {
        this.scheduler.shutdown();
    }

    @Test
    public void minimalTest()
        throws Exception
    {
        Thread.sleep( 5000 );
        assertTrue( this.timedTask.getInvocationsTimedTaskA() > 0 );
    }
}
