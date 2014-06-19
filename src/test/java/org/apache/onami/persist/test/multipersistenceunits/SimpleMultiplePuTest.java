package org.apache.onami.persist.test.multipersistenceunits;

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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.PersistenceModule;
import org.apache.onami.persist.PersistenceService;
import org.apache.onami.persist.UnitOfWork;
import org.apache.onami.persist.test.TestEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SimpleMultiplePuTest
{

    private Injector injector;

    private EntityManagerProvider firstEmp;

    private EntityManagerProvider secondEmp;

    @Before
    public void setUp()
    {
        final PersistenceModule pm = createPersistenceModuleForTest();
        injector = Guice.createInjector( pm );

        //startup persistence
        injector.getInstance( Key.get( PersistenceService.class, FirstPU.class ) ).start();
        injector.getInstance( Key.get( PersistenceService.class, SecondPU.class ) ).start();

        injector.getInstance( Key.get( UnitOfWork.class, FirstPU.class ) ).begin();
        injector.getInstance( Key.get( UnitOfWork.class, SecondPU.class ) ).begin();

        firstEmp = injector.getInstance( Key.get( EntityManagerProvider.class, FirstPU.class ) );
        secondEmp = injector.getInstance( Key.get( EntityManagerProvider.class, SecondPU.class ) );
    }

    @After
    public void tearDown()
        throws Exception
    {
        injector.getInstance( Key.get( UnitOfWork.class, FirstPU.class ) ).end();
        injector.getInstance( Key.get( UnitOfWork.class, SecondPU.class ) ).end();

        injector.getInstance( Key.get( PersistenceService.class, FirstPU.class ) ).stop();
        injector.getInstance( Key.get( PersistenceService.class, SecondPU.class ) ).stop();
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


    @Test
    public void storeUnitsInTwoPersistenceUnits()
        throws Exception
    {
        // given
        final TestEntity firstEntity = new TestEntity();
        final TestEntity secondEntity = new TestEntity();

        // when
        firstEmp.get().persist( firstEntity );
        secondEmp.get().persist( secondEntity );

        // then
        assertNotNull( firstEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        assertNotNull( secondEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( firstEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( secondEmp.get().find( TestEntity.class, firstEntity.getId() ) );
    }

}
