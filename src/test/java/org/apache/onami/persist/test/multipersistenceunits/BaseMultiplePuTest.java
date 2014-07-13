package org.apache.onami.persist.test.multipersistenceunits;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceModule;
import org.apache.onami.persist.PersistenceService;
import org.apache.onami.persist.UnitOfWork;
import org.junit.After;
import org.junit.Before;

import java.lang.annotation.Annotation;

public abstract class BaseMultiplePuTest
{
    protected EntityManagerProvider firstEmp;

    protected EntityManagerProvider secondEmp;

    private Injector injector;

    @Before
    public void setUp()
    {
        final PersistenceModule pm = createPersistenceModuleForTest();
        injector = Guice.createInjector( pm );

        //startup persistence
        injector.getInstance( Key.get( PersistenceService.class, FirstPU.class ) ).start();
        injector.getInstance( Key.get( PersistenceService.class, SecondPU.class ) ).start();

        firstEmp = injector.getInstance( Key.get( EntityManagerProvider.class, FirstPU.class ) );
        secondEmp = injector.getInstance( Key.get( EntityManagerProvider.class, SecondPU.class ) );
    }

    private PersistenceModule createPersistenceModuleForTest()
    {
        return new PersistenceModule()
        {

            @Override
            protected void configurePersistence()
            {
                bindApplicationManagedPersistenceUnit( "firstUnit" ).annotatedWith( FirstPU.class );
                bindApplicationManagedPersistenceUnit( "secondUnit" ).annotatedWith( SecondPU.class );
            }
        };
    }

    @After
    public void tearDown()
        throws Exception
    {
        injector.getInstance( Key.get( PersistenceService.class, FirstPU.class ) ).stop();
        injector.getInstance( Key.get( PersistenceService.class, SecondPU.class ) ).stop();
    }

    protected void beginUnitOfWork()
    {
        getInstance( UnitOfWork.class, FirstPU.class ).begin();
        getInstance( UnitOfWork.class, SecondPU.class ).begin();
    }

    protected void endUnitOfWork()
    {
        getInstance( UnitOfWork.class, FirstPU.class ).end();
        getInstance( UnitOfWork.class, SecondPU.class ).end();
    }

    protected <T> T getInstance(Class<T> type)
    {
        return injector.getInstance( type );
    }

    protected <T> T getInstance(Class<T> type, Class<? extends Annotation> anno)
    {
        return injector.getInstance( Key.get( type, anno ) );
    }
}
