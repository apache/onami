package org.apache.onami.lifecycle.standard;

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

import org.apache.onami.lifecycle.core.DefaultStager;
import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stageable;

/**
 * Default {@link Disposer} implementation.
 *
 * @since 0.2.0
 */
public class DefaultDisposer
    extends DefaultStager<Dispose>
    implements Disposer
{
    public DefaultDisposer()
    {
        super( Dispose.class, Order.FIRST_IN_LAST_OUT );
    }

    public void register( final Disposable disposable )
    {
        Stageable stageable = new Stageable()
        {
            public void stage( final StageHandler stageHandler )
            {
                DisposeHandler disposeHandler = new DisposeHandler()
                {
                    public <I> void onSuccess( I injectee )
                    {
                        stageHandler.onSuccess( injectee );
                    }

                    public <I, E extends Throwable> void onError( I injectee, E error )
                    {
                        stageHandler.onError( injectee, error );
                    }
                };
                disposable.dispose( disposeHandler );
            }
        };
        register( stageable );
    }

    public void dispose()
    {
        super.stage();
    }

    public void dispose( final DisposeHandler disposeHandler )
    {
        StageHandler stageHandler = new StageHandler()
        {
            public <I> void onSuccess( I injectee )
            {
                disposeHandler.onSuccess( injectee );
            }

            public <I, E extends Throwable> void onError( I injectee, E error )
            {
                disposeHandler.onError( injectee, error );
            }
        };
        super.stage( stageHandler );
    }
}