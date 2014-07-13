package org.apachi.onami.lifecycle.warmup;

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

import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.onami.lifecycle.warmup.WarmUp;

public class Dag1
{
    /*
        3 Classes all with warmups

            B
          <
        A
          <
            C
     */

    @SuppressWarnings( "UnusedParameters" )
    @Singleton
    public static class A
    {
        private final Recorder recorder;
        private final CountDownLatch latch;

        @Inject
        public A( Recorder recorder, B b, C c, CountDownLatch latch )
        {
            this.recorder = recorder;
            this.latch = latch;
        }

        @WarmUp
        public void warmUp()
            throws InterruptedException
        {
            try
            {
                recorder.record( "A" );
            }
            finally
            {
                latch.countDown();
            }
        }
    }

    @Singleton
    public static class B
    {
        private final Recorder recorder;
        private final CountDownLatch latch;

        @Inject
        public B( Recorder recorder, CountDownLatch latch )
        {
            this.recorder = recorder;
            this.latch = latch;
        }

        @WarmUp
        public void warmUp()
            throws InterruptedException
        {
            try
            {
                recorder.record( "B" );
            }
            finally
            {
                latch.countDown();
            }
        }
    }

    @Singleton
    public static class C
    {
        private final Recorder recorder;
        private final CountDownLatch latch;

        @Inject
        public C( Recorder recorder, CountDownLatch latch )
        {
            this.recorder = recorder;
            this.latch = latch;
        }

        @WarmUp
        public void warmUp()
            throws InterruptedException
        {
            try
            {
                recorder.record( "C" );
            }
            finally
            {
                latch.countDown();
            }
        }
    }
}
