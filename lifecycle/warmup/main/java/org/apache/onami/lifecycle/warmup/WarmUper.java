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
import org.apache.onami.lifecycle.core.NoOpStageHandler;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stageable;
import org.apache.onami.lifecycle.core.StageableTypeMapper;
import org.apache.onami.lifecycle.core.Stager;
import sun.java2d.Disposer;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Default {@link Disposer} implementation.
 *
 * @since 0.2.0
 */
public class WarmUper<A extends Annotation>
    implements Stager<A>, StageableTypeMapper<A>
{
    private final Queue<Stageable> stageables = new ConcurrentLinkedQueue<Stageable>();
    private final Map<Stageable, TypeLiteral<?>> types = new ConcurrentHashMap<Stageable, TypeLiteral<?>>();

    private final Class<A> stage;

    public WarmUper( Class<A> stage )
    {
        this.stage = stage;
    }

    public <I> void registerType( Stageable stageable, TypeLiteral<I> parentType )
    {
        types.put( stageable, parentType );
    }

    public void register( Stageable stageable )
    {
        stageables.add( stageable );
    }

    public void stage()
    {
        stage( new NoOpStageHandler() );
    }

    public void stage( StageHandler stageHandler )
    {
    }

    public Class<A> getStage()
    {
        return stage;
    }
}