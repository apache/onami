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

import java.util.concurrent.TimeUnit;

import org.apache.onami.lifecycle.core.LifeCycleStageModule;
import org.apache.onami.lifecycle.core.Stager;

/**
 * The module for preparing for warm ups.
 */
public class WarmUpModule
    extends LifeCycleStageModule
{

    private static final long DEFAULT_WAIT_MS = TimeUnit.DAYS.toMillis( Integer.MAX_VALUE );    // essentially forever

    private final WarmUper<WarmUp> stager = new WarmUper<WarmUp>( WarmUp.class, DEFAULT_WAIT_MS );

    @Override
    protected void configureBindings()
    {
        bindStager( stager ).mappingWith( stager );
    }

    public Stager<WarmUp> getStager()
    {
        return stager;
    }
}
