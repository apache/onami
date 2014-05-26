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

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link EntityManagerFactorySourceByJndiLookup}.
 */
public class EntityManagerFactorySourceByJndiLookupTest
{

    public static final String JNDI_NAME = "jndiName";

    private EntityManagerFactorySourceByJndiLookup sut;

    private JndiLookupHelper jndiLookupHelper;

    @Before
    public void setUp()
        throws Exception
    {
        jndiLookupHelper = mock( JndiLookupHelper.class );
        sut = new EntityManagerFactorySourceByJndiLookup( JNDI_NAME, jndiLookupHelper );
    }


    @Test
    public void shouldLookupEmfByJndiName()
        throws Exception
    {
        // given
        final EntityManagerFactory emf = mock( EntityManagerFactory.class );
        doReturn( emf ).when( jndiLookupHelper ).doJndiLookup( EntityManagerFactory.class, JNDI_NAME );
        // when
        final EntityManagerFactory result = sut.getEntityManagerFactory();
        // then
        assertThat( result, sameInstance( emf ) );
    }

    @Test(expected = NullPointerException.class)
    public void jndiNameIsMandatory()
    {
        new EntityManagerFactorySourceByJndiLookup( null, jndiLookupHelper );
    }

    @Test(expected = NullPointerException.class)
    public void jndiLookupHelperIsMandatory()
    {
        new EntityManagerFactorySourceByJndiLookup( JNDI_NAME, null );
    }

}
