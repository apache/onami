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

import com.google.inject.Inject;
import org.apache.onami.persist.EntityManagerProvider;
import org.apache.onami.persist.Transactional;
import org.apache.onami.persist.test.TestEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TransactionalMultiplePuTest
    extends BaseMultiplePuTest
{

    private TestEntity firstEntity;

    private TestEntity secondEntity;

    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        firstEntity = new TestEntity();
        secondEntity = new TestEntity();
    }

    @Test
    public void storeUnitsInTwoPersistenceUnits()
        throws Exception
    {
        // when
        runServices( FirstServiceNotRollingBack.class, SecondServiceNotRollingBack.class );

        // then
        beginUnitOfWork();
        assertNotNull( firstEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        assertNotNull( secondEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( firstEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( secondEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        endUnitOfWork();
    }

    @Test
    public void storeUnitsInTwoPersistenceUnitsAndRollBackBoth()
        throws Exception
    {
        // when
        runServices( FirstServiceRollingBack.class, SecondServiceRollingBack.class );

        // then
        beginUnitOfWork();
        assertNull( firstEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        assertNull( secondEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( firstEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( secondEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        endUnitOfWork();
    }

    @Test
    public void storeUnitsInTwoPersistenceUnitsAndRollBackOnlyFirst()
        throws Exception
    {
        // when
        runServices( FirstServiceRollingBack.class, SecondServiceNotRollingBack.class );

        // then
        beginUnitOfWork();
        assertNull( firstEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        assertNotNull( secondEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( firstEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( secondEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        endUnitOfWork();
    }

    @Test
    public void storeUnitsInTwoPersistenceUnitsAndRollBackOnlySecond()
        throws Exception
    {
        // when
        runServices( FirstServiceNotRollingBack.class, SecondServiceRollingBack.class );

        // then
        beginUnitOfWork();
        assertNotNull( firstEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        assertNull( secondEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( firstEmp.get().find( TestEntity.class, secondEntity.getId() ) );
        assertNull( secondEmp.get().find( TestEntity.class, firstEntity.getId() ) );
        endUnitOfWork();
    }

    private void runServices( Class<? extends FirstService> firstServiceClass,
                              Class<? extends SecondService> secondServiceClass )
    {
        final FirstService fistService = getInstance( firstServiceClass );
        final SecondService secondService = getInstance( secondServiceClass );

        try {
            fistService.setSecondService( secondService );
            secondService.setException( new RuntimeException() );
            fistService.run( firstEntity, secondEntity );
        }
        catch ( RuntimeException e ) {
            // ignore
        }
    }

    interface FirstService
    {
        void setSecondService(SecondService secondService);

        void run(TestEntity firstEntity, TestEntity secondEntity);
    }

    static class FirstServiceRollingBack
        implements FirstService
    {

        private final EntityManagerProvider emp;

        private SecondService secondService;

        @Inject
        public FirstServiceRollingBack( @FirstPU EntityManagerProvider emp )
        {
            this.emp = emp;
        }

        // @Override
        public void setSecondService( SecondService secondService )
        {
            this.secondService = secondService;
        }

        // @Override
        @Transactional( onUnits = FirstPU.class )
        public void run(TestEntity firstEntity, TestEntity secondEntity)
        {
            emp.get().persist( firstEntity );
            secondService.run(secondEntity);
        }
    }

    static class FirstServiceNotRollingBack
        implements FirstService
    {

        private final EntityManagerProvider emp;

        private SecondService secondService;

        @Inject
        public FirstServiceNotRollingBack( @FirstPU EntityManagerProvider emp )
        {
            this.emp = emp;
        }

        // @Override
        public void setSecondService( SecondService secondService )
        {
            this.secondService = secondService;
        }

        // @Override
        @Transactional( onUnits = FirstPU.class, ignore = RuntimeException.class)
        public void run(TestEntity firstEntity, TestEntity secondEntity)
        {
            emp.get().persist( firstEntity );
            secondService.run(secondEntity);
        }
    }

    interface SecondService
    {
        void setException(RuntimeException exception);

        void run(TestEntity secondEntity);
    }

    static class SecondServiceRollingBack
        implements SecondService
    {

        private final EntityManagerProvider emp;

        private RuntimeException ex;

        @Inject
        public SecondServiceRollingBack( @SecondPU EntityManagerProvider emp )
        {
            this.emp = emp;
        }

        // @Override
        public void setException(RuntimeException ex)
        {
            this.ex = ex;
        }

        // @Override
        @Transactional( onUnits = SecondPU.class )
        public void run( TestEntity secondEntity )
        {
            emp.get().persist( secondEntity );
            if (ex != null) {
                throw ex;
            }
        }
    }

    static class SecondServiceNotRollingBack
        implements SecondService
    {

        private final EntityManagerProvider emp;

        private RuntimeException ex;

        @Inject
        public SecondServiceNotRollingBack( @SecondPU EntityManagerProvider emp )
        {
            this.emp = emp;
        }

        // @Override
        public void setException(RuntimeException ex)
        {
            this.ex = ex;
        }

        // @Override
        @Transactional( onUnits = SecondPU.class, ignore = RuntimeException.class )
        public void run( TestEntity secondEntity )
        {
            emp.get().persist( secondEntity );
            if (ex != null) {
                throw ex;
            }
        }
    }

}
