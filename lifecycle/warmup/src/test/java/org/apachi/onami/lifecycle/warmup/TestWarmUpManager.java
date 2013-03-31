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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.onami.lifecycle.core.LifeCycleStageModule;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stager;
import org.apache.onami.lifecycle.warmup.WarmUp;
import org.apache.onami.lifecycle.warmup.WarmUpModule;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class TestWarmUpManager
{

    @SuppressWarnings( "ThrowableResultOfMethodCallIgnored" )
    @Test
    public void testErrors()
        throws Exception
    {
        AbstractModule module = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( WarmUpWithException.class ).asEagerSingleton();
            }
        };
        Injector injector = Guice.createInjector( WarmUpModule.newWarmUpModule(), module );

        final AtomicInteger errorCount = new AtomicInteger( 0 );
        StageHandler stageHandler = new StageHandler()
        {
            public <I> void onSuccess( I injectee )
            {
            }

            public <I, E extends Throwable> void onError( I injectee, E error )
            {
                errorCount.incrementAndGet();
            }
        };
        injector.getInstance( LifeCycleStageModule.key( WarmUp.class ) ).stage( stageHandler );
        assertEquals( errorCount.get(), 1 );
    }

    @Test
    public void testDag1()
        throws Exception
    {
        Injector injector = Guice.createInjector( WarmUpModule.newWarmUpModule() );
        injector.getInstance( Dag1.A.class );
        injector.getInstance( LifeCycleStageModule.key( WarmUp.class ) ).stage();
        Recorder recorder = injector.getInstance( Recorder.class );

        System.out.println( recorder.getRecordings() );
        System.out.println( recorder.getConcurrents() );

        assertSingleExecution( recorder );
        assertNotConcurrent( recorder, "A", "B" );
        assertNotConcurrent( recorder, "A", "C" );

        assertEquals( recorder.getInterruptions().size(), 0 );
        assertOrdering( recorder, "A", "B" );
        assertOrdering( recorder, "A", "C" );
    }

    @Test
    public void testDag2()
        throws Exception
    {
        Injector injector = Guice.createInjector( WarmUpModule.newWarmUpModule() );
        injector.getInstance( Dag2.A1.class );
        injector.getInstance( Dag2.A2.class );
        injector.getInstance( Dag2.A3.class );
        injector.getInstance( LifeCycleStageModule.key( WarmUp.class ) ).stage();
        Recorder recorder = injector.getInstance( Recorder.class );

        System.out.println( recorder.getRecordings() );
        System.out.println( recorder.getConcurrents() );

        assertSingleExecution( recorder );

        assertNotConcurrent( recorder, "A1", "B1" );
        assertNotConcurrent( recorder, "A1", "B2" );
        assertNotConcurrent( recorder, "B1", "C1" );
        assertNotConcurrent( recorder, "B2", "C1" );
        assertNotConcurrent( recorder, "A2", "B2" );
        assertNotConcurrent( recorder, "A2", "B3" );
        assertNotConcurrent( recorder, "B2", "C2" );
        assertNotConcurrent( recorder, "B3", "C2" );
        assertNotConcurrent( recorder, "A3", "B3" );
        assertNotConcurrent( recorder, "A3", "B4" );
        assertNotConcurrent( recorder, "B3", "C3" );
        assertNotConcurrent( recorder, "B4", "C3" );

        assertEquals( recorder.getInterruptions().size(), 0 );
        assertOrdering( recorder, "A1", "B1" );
        assertOrdering( recorder, "B1", "C1" );
        assertOrdering( recorder, "A1", "B2" );
        assertOrdering( recorder, "B2", "C1" );
        assertOrdering( recorder, "A2", "B2" );
        assertOrdering( recorder, "B2", "C2" );
        assertOrdering( recorder, "A2", "B3" );
        assertOrdering( recorder, "B3", "C2" );
        assertOrdering( recorder, "A3", "B3" );
        assertOrdering( recorder, "B3", "C3" );
        assertOrdering( recorder, "A3", "B4" );
        assertOrdering( recorder, "B4", "C3" );
    }

    @Test
    public void testDag3()
        throws Exception
    {
        Injector injector = Guice.createInjector( WarmUpModule.newWarmUpModule() );
        injector.getInstance( Dag3.A.class );
        injector.getInstance( LifeCycleStageModule.key( WarmUp.class ) ).stage();
        Recorder recorder = injector.getInstance( Recorder.class );

        System.out.println( recorder.getRecordings() );
        System.out.println( recorder.getConcurrents() );

        assertSingleExecution( recorder );

        assertNotConcurrent( recorder, "C", "D" );
        assertNotConcurrent( recorder, "B", "D" );
        assertNotConcurrent( recorder, "A", "B" );
        assertNotConcurrent( recorder, "A", "C" );

        assertEquals( recorder.getInterruptions().size(), 0 );
        assertOrdering( recorder, "A", "C" );
        assertOrdering( recorder, "C", "D" );
        assertOrdering( recorder, "A", "D" );
        assertOrdering( recorder, "B", "D" );
    }

    @Test
    public void testDag4()
        throws Exception
    {
        Module module = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                RecorderSleepSettings recorderSleepSettings = new RecorderSleepSettings();
                recorderSleepSettings.setBaseSleepFor( "E", 1, TimeUnit.MILLISECONDS );
                recorderSleepSettings.setRandomize( false );
                bind( RecorderSleepSettings.class ).toInstance( recorderSleepSettings );
            }
        };
        Injector injector = Guice.createInjector( WarmUpModule.newWarmUpModule(), module );
        injector.getInstance( Dag4.A.class );
        injector.getInstance( LifeCycleStageModule.key( WarmUp.class ) ).stage();
        Recorder recorder = injector.getInstance( Recorder.class );

        System.out.println( recorder.getRecordings() );
        System.out.println( recorder.getConcurrents() );

        assertSingleExecution( recorder );
        assertEquals( recorder.getInterruptions().size(), 0 );
        assertOrdering( recorder, "D", "E" );
        assertOrdering( recorder, "C", "E" );
        assertOrdering( recorder, "B", "D" );
        assertOrdering( recorder, "A", "B" );
    }

    @Test
    public void testFlat()
        throws Exception
    {
        Injector injector = Guice.createInjector( WarmUpModule.newWarmUpModule() );
        Recorder recorder = injector.getInstance( Recorder.class );
        injector.getInstance( Flat.A.class ).recorder = recorder;
        injector.getInstance( Flat.B.class ).recorder = recorder;
        injector.getInstance( LifeCycleStageModule.key( WarmUp.class ) ).stage();

        System.out.println( recorder.getRecordings() );
        System.out.println( recorder.getConcurrents() );

        assertSingleExecution( recorder );
        assertEquals( recorder.getInterruptions().size(), 0 );
        assertTrue( recorder.getRecordings().indexOf( "A" ) >= 0 );
        assertTrue( recorder.getRecordings().indexOf( "B" ) >= 0 );
    }

    @Test
    public void testStuck()
        throws Exception
    {
        Module module = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                RecorderSleepSettings recorderSleepSettings = new RecorderSleepSettings();
                recorderSleepSettings.setBaseSleepFor( "C", 1, TimeUnit.DAYS );
                bind( RecorderSleepSettings.class ).toInstance( recorderSleepSettings );
            }
        };
        LifeCycleStageModule<WarmUp> warmUpModule = WarmUpModule.builder().withMaxWait( 1, TimeUnit.SECONDS ).build();
        Injector injector = Guice.createInjector( warmUpModule, module );
        injector.getInstance( Dag1.A.class );
        Stager<WarmUp> stager = injector.getInstance( LifeCycleStageModule.key( WarmUp.class ) );

        boolean succeeded;
        try
        {
            stager.stage();
            succeeded = true;
        }
        catch ( RuntimeException e )
        {
            succeeded = false;
            assertTrue( e.getCause() instanceof TimeoutException );
        }

        // Wait for all interrupted warmup tasks to finish
        // and add themselfs to recorder.
        // This fixes race between test thread and interrupted tasks
        // threads. This workaround is good enough for test.
        Thread.sleep( 1000 );

        Recorder recorder = injector.getInstance( Recorder.class );

        System.out.println( recorder.getRecordings() );
        System.out.println( recorder.getConcurrents() );

        assertSingleExecution( recorder );
        assertFalse( succeeded );
        assertTrue( recorder.getRecordings().contains( "B" ) );
        assertEquals( recorder.getInterruptions(), Arrays.asList( "C" ) );
    }

    private void assertSingleExecution( Recorder recorder )
    {
        Set<String> duplicateCheck = new HashSet<String>();
        for ( String s : recorder.getRecordings() )
        {
            assertFalse( s + " ran more than once: " + recorder.getRecordings(), duplicateCheck.contains( s ) );
            duplicateCheck.add( s );
        }
    }

    private void assertOrdering( Recorder recorder, String base, String dependency )
    {
        int baseIndex = recorder.getRecordings().indexOf( base );
        int dependencyIndex = recorder.getRecordings().indexOf( dependency );

        assertTrue( baseIndex >= 0 );
        assertTrue( dependencyIndex >= 0 );
        assertTrue( "baseIndex: " + baseIndex + " - dependencyIndex: " + dependencyIndex,
                           baseIndex > dependencyIndex );
    }

    private void assertNotConcurrent( Recorder recorder, String task1, String task2 )
    {
        for ( Set<String> s : recorder.getConcurrents() )
        {
            assertTrue( String.format( "Incorrect concurrency for %s and %s: %s", task1, task2, s ),
                               !s.contains( task1 ) || !s.contains( task2 ) );
        }
    }

}
