package org.apache.onami.persist;

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

import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link EntityManagerFactoryFactory}.
 */
public class EntityManagerFactoryFactoryTest
{

    private static final String TEST_KEY = "testKey";

    private static final String TEST_VALUE = "testValue";

    private static final String PU_NAME = "testUnit";

    private static final String PU_KEY = "hibernate.ejb.persistenceUnitName";

    private EntityManagerFactoryFactory sut;

    private Properties properties;

    @Before
    public void setUp()
        throws Exception
    {
        properties = new Properties(  );
        sut = new EntityManagerFactoryFactory( PU_NAME, properties );
    }

    @Test
    public void shouldCreateAnInstanceWithThePassedValues()
    {
        // given
        properties.setProperty( TEST_KEY, TEST_VALUE );
        // when
        final EntityManagerFactory result = sut.createApplicationManagedEntityManagerFactory();
        // then
        assertThat( result.getProperties().get( PU_KEY ), is( (Object) PU_NAME ) );
        assertThat( result.getProperties().get( TEST_KEY ), is( (Object) TEST_VALUE ) );
    }

}
