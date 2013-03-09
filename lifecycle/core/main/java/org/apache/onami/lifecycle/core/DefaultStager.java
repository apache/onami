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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Default {@link Stager} implementation.
 */
public class DefaultStager<A extends Annotation>
    implements Stager<A>
{
    private final Class<A> stage;

    /**
     * Stack of elements have to be disposed.
     */
    private final Queue<Stageable> stageables;

    /**
     * @param stage the annotation that specifies this stage
     * @param mode  execution order
     */
    protected DefaultStager( Class<A> stage, Order mode )
    {
        this.stage = stage;

        Queue<Stageable> localStageables;
        switch ( mode )
        {
            case FIRST_IN_FIRST_OUT:
            {
                localStageables = new LinkedList<Stageable>();
                break;
            }

            case FIRST_IN_LAST_OUT:
            {
                localStageables = Collections.asLifoQueue( new LinkedList<Stageable>() );
                break;
            }

            default:
            {
                throw new IllegalArgumentException( "Unknown mode: " + mode );
            }
        }
        stageables = localStageables;
    }

    /**
     * Allocate a new stager
     *
     * @param stage the stage annotation
     * @return stager
     */
    public static <A extends Annotation> Stager<A> newStager( Class<A> stage )
    {
        return newStager( stage, Order.FIRST_IN_FIRST_OUT );
    }

    /**
     * Allocate a new stager
     *
     * @param stage the stage annotation
     * @param mode  execution order
     * @return stager
     */
    public static <A extends Annotation> Stager<A> newStager( Class<A> stage, Order mode )
    {
        return new DefaultStager<A>( stage, mode );
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void register( Stageable stageable )
    {
        stageables.add( stageable );
    }

    /**
     * {@inheritDoc}
     */
    public void stage()
    {
        stage( null );
    }

    /**
     * {@inheritDoc}
     */
    public void stage( StageHandler stageHandler )
    {
        if ( stageHandler == null )
        {
            stageHandler = new NoOpStageHandler();
        }

        List<Stageable> localStageables;
        synchronized ( this )
        {
            localStageables = new ArrayList<Stageable>(stageables);
            stageables.clear();
        }

        for ( Stageable stageable : localStageables )
        {
            stageable.stage( stageHandler );
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class<A> getStage()
    {
        return stage;
    }

    /**
     * specifies ordering for a {@link DefaultStager}
     */
    public static enum Order
    {
        /**
         * FIFO
         */
        FIRST_IN_FIRST_OUT,

        /**
         * FILO/LIFO
         */
        FIRST_IN_LAST_OUT
    }

}
