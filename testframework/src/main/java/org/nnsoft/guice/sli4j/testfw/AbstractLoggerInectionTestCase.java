package org.nnsoft.guice.sli4j.testfw;

/*
 *    Copyright 2010-2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import static com.google.inject.Guice.createInjector;

import org.nnsoft.guice.sli4j.core.AbstractLoggingModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

/**
 *
 */
public abstract class AbstractLoggerInectionTestCase<L>
{

    @Inject
    private Service service;

    public void setService( Service service )
    {
        this.service = service;
    }

    public <LM extends AbstractLoggingModule<L>> void setUp( LM logginModule )
    {
        createInjector( logginModule, new AbstractModule()
        {

            @Override
            protected void configure()
            {
                bind( Service.class ).to( ServiceImpl.class ).asEagerSingleton();
            }

        } ).injectMembers( this );
    }

    public void injectAndVerify( L logger )
    {
        assert logger != null;
        assert this.service != null;
    }

}
