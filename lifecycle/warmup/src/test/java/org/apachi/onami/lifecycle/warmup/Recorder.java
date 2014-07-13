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

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class Recorder
{

    private final List<String> recordings = new ArrayList<String>();

    private final List<String> interruptions = new ArrayList<String>();

    private final RecorderSleepSettings recorderSleepSettings;

    private final Set<Set<String>> concurrents = new HashSet<Set<String>>();

    private final Set<String> activeConcurrents = new HashSet<String>();

    @Inject
    public Recorder( RecorderSleepSettings recorderSleepSettings )
    {
        this.recorderSleepSettings = recorderSleepSettings;
    }

    public synchronized void record( String s )
        throws InterruptedException
    {

        recordings.add( s );

        long sleepMs = recorderSleepSettings.getSleepMsFor( s );

        activeConcurrents.add( s );
        try
        {
            concurrents.add( new HashSet<String>( activeConcurrents ) );
            Thread.sleep( sleepMs );
        }
        catch ( InterruptedException e )
        {
            interruptions.add( s );
            throw e;
        }
        finally
        {
            activeConcurrents.remove( s );
        }
    }

    public synchronized List<String> getRecordings()
    {
        return new ArrayList<String>( recordings );
    }

    public synchronized List<String> getInterruptions()
    {
        return new ArrayList<String>( interruptions );
    }

    public synchronized Set<Set<String>> getConcurrents()
    {
        return new HashSet<Set<String>>( concurrents );
    }

}
