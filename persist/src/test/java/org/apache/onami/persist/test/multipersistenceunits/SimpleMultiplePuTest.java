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

import org.apache.onami.persist.test.TestEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SimpleMultiplePuTest
    extends BaseMultiplePuTest
{

    @Override
    @Before
    public void setUp()
    {
        super.setUp();
        beginUnitOfWork();
    }

    @Override
    @After
    public void tearDown()
        throws Exception
    {
        endUnitOfWork();
        super.tearDown();
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
