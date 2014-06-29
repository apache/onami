package org.apache.onami.lifecycle.warmup;

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

import com.google.inject.TypeLiteral;
import jsr166y.ForkJoinPool;
import org.apache.onami.lifecycle.core.NoOpStageHandler;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stageable;
import org.apache.onami.lifecycle.core.StageableTypeMapper;
import org.apache.onami.lifecycle.core.Stager;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A {@link Stager} that handles the warm up process. For Warm Up, you
 * <strong>must</strong> use WarmUper.
 */
public class WarmUper<A extends Annotation>
    implements Stager<A>, StageableTypeMapper
{
    private final ConcurrentMap<TypeLiteral<?>, Set<Stageable>> reverseLookup =
        new ConcurrentHashMap<TypeLiteral<?>, Set<Stageable>>();

    private final Class<A> stage;

    private volatile long maxMs;

    /**
     * @param stage the annotation to mark this stage
     * @param maxMs when {@link #stage()} is called, this is the maximum time
     *              to wait for warmups to complete. If time expires, a
     *              {@link TimeoutException} (wrapped in a {@link RuntimeException}
     *              is thrown.
     */
    public WarmUper( Class<A> stage, long maxMs )
    {
        this.stage = stage;
        this.maxMs = maxMs;
    }

    /**
     * When the warm up is staged, it will wait until this maximum time for warm ups to finish.
     * The default is to wait forever. If time expires, a
     * {@link TimeoutException} (wrapped in a {@link RuntimeException}
     * is thrown.
     *
     * @param maxWait max time to wait
     * @param unit    time unit
     */
    public void setMaxWait( long maxWait, TimeUnit unit )
    {
        this.maxMs = unit.toMillis( maxWait );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <I> void registerType( Stageable stageable, TypeLiteral<I> parentType )
    {
        Set<Stageable> newList = Collections.newSetFromMap( new ConcurrentHashMap<Stageable, Boolean>() );
        Set<Stageable> oldList = reverseLookup.putIfAbsent( parentType, newList );
        Set<Stageable> useList = ( oldList != null ) ? oldList : newList;
        useList.add( stageable );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( Stageable stageable )
    {
        // this is a NOP for warm up. Use registerType instead
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stage()
    {
        stage( new NoOpStageHandler() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stage( StageHandler stageHandler )
    {
        Map<TypeLiteral<?>, Set<Stageable>> localCopy = new HashMap<TypeLiteral<?>, Set<Stageable>>();
        localCopy.putAll( reverseLookup );
        reverseLookup.clear();

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try
        {
            ConcurrentMap<TypeLiteral<?>, WarmUpTask> inProgress = new ConcurrentHashMap<TypeLiteral<?>, WarmUpTask>();
            forkJoinPool.submit( new WarmUpTask( stageHandler, WarmUpTask.ROOT, localCopy, inProgress ) );
        }
        finally
        {
            forkJoinPool.shutdown();
        }
        try
        {
            boolean success = forkJoinPool.awaitTermination( maxMs, TimeUnit.MILLISECONDS );
            if ( !success )
            {
                forkJoinPool.shutdownNow();
                throw new RuntimeException( new TimeoutException( "Warm up stager timed out" ) );
            }
        }
        catch ( InterruptedException e )
        {
            forkJoinPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<A> getStage()
    {
        return stage;
    }
}