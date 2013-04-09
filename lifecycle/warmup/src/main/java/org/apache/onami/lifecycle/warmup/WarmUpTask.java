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

import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import jsr166y.RecursiveAction;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Fork-join task that performs the warm ups
 */
class WarmUpTask
    extends RecursiveAction
{
    private final StageHandler stageHandler;

    private final TypeLiteral<?> typeLiteral;

    private final Map<TypeLiteral<?>, Set<Stageable>> reverseLookup;

    private final ConcurrentMap<TypeLiteral<?>, WarmUpTask> inProgress;

    static final TypeLiteral<?> ROOT = new TypeLiteral<Object>(){};

    /**
     * @param stageHandler the stage handler passed to {@link org.apache.onami.lifecycle.core.Stager#stage(org.apache.onami.lifecycle.core.StageHandler)}
     * @param typeLiteral the type associated with the object being warmed up
     * @param reverseLookup the full list of types-to-stagers that were registered
     * @param inProgress which tasks are already warming up (to avoid duplicates)
     */
    WarmUpTask( StageHandler stageHandler, TypeLiteral<?> typeLiteral,
                Map<TypeLiteral<?>, Set<Stageable>> reverseLookup, ConcurrentMap<TypeLiteral<?>, WarmUpTask> inProgress )
    {
        this.stageHandler = stageHandler;
        this.typeLiteral = typeLiteral;
        this.reverseLookup = reverseLookup;
        this.inProgress = inProgress;
    }

    @Override
    protected void compute()
    {
        List<WarmUpTask> tasksToJoin = new ArrayList<WarmUpTask>();
        if ( typeLiteral == ROOT )
        {
            // this is the root task - just start all the known tasks
            computeRoot( tasksToJoin );
        }
        else
        {
            internalCompute( tasksToJoin );
        }

        // wait for dependent tasks to finish
        for ( WarmUpTask task : tasksToJoin )
        {
            task.join();
        }

        // finally do the execution

        Set<Stageable> stageables = reverseLookup.get( typeLiteral );
        if ( stageables != null )
        {
            for ( Stageable stageable : stageables )
            {
                if ( Thread.currentThread().isInterrupted() )
                {
                    // Warmup is taking too long - thread was interrupted.
                    // Skip other stageables.
                    break;
                }
                stageable.stage( stageHandler );
            }
        }
    }

    private void computeRoot( List<WarmUpTask> tasksToJoin )
    {
        for ( TypeLiteral<?> typeLiteral : reverseLookup.keySet() )
        {
            WarmUpTask warmUpTask = new WarmUpTask( stageHandler, typeLiteral, reverseLookup, inProgress );
            startTask( tasksToJoin, warmUpTask );
        }
    }

    private void internalCompute( List<WarmUpTask> tasksToJoin )
    {
        List<WarmUpTask> childTasks = new ArrayList<WarmUpTask>();
        addDependency( childTasks, getConstructorInjectionPoint( typeLiteral ) );
        for ( InjectionPoint injectionPoint : getMethodInjectionPoints( typeLiteral ) )
        {
            addDependency( childTasks, injectionPoint );
        }

        for ( WarmUpTask childTask : childTasks )
        {
            startTask( tasksToJoin, childTask );
        }
    }

    private void startTask( List<WarmUpTask> tasksToJoin, WarmUpTask childTask )
    {
        WarmUpTask existingTask = inProgress.putIfAbsent( childTask.typeLiteral, childTask );
        if ( existingTask == null )
        {
            childTask.fork();
            tasksToJoin.add( childTask );
        }
        else
        {
            tasksToJoin.add( existingTask );
        }
    }

    private void addDependency( List<WarmUpTask> childTasks, InjectionPoint injectionPoint )
    {
        if ( injectionPoint != null )
        {
            List<Dependency<?>> dependencies = injectionPoint.getDependencies();
            for ( Dependency<?> dependency : dependencies )
            {
                // create a task for any dependencies. Note: even if the dependency isn't
                // a registered stager it must be created as a task as its dependencies
                // may be stagers
                TypeLiteral<?> dependencyTypeLiteral = dependency.getKey().getTypeLiteral();
                childTasks.add(
                    new WarmUpTask( stageHandler, dependencyTypeLiteral, reverseLookup, inProgress ) );
            }
        }
    }

    private Set<InjectionPoint> getMethodInjectionPoints( TypeLiteral<?> type )
    {
        try
        {
            return InjectionPoint.forInstanceMethodsAndFields( type );
        }
        catch ( ConfigurationException e )
        {
            // ignore
        }
        return new HashSet<InjectionPoint>();
    }

    private InjectionPoint getConstructorInjectionPoint( TypeLiteral<?> type )
    {
        try
        {
            return InjectionPoint.forConstructorOf( type );
        }
        catch ( ConfigurationException e )
        {
            // ignore
        }
        return null;
    }
}
