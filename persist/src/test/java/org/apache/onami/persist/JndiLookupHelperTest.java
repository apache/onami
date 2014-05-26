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

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link JndiLookupHelper}.
 */
public class JndiLookupHelperTest
{

    public static final String JNDI_NAME = "jndiName";

    private JndiLookupHelper sut;

    @Before
    public void setUp()
        throws Exception
    {
        sut = new JndiLookupHelper();
    }


    @Test
    public void shouldLookupEmfByJndiName()
        throws Exception
    {
        // given
        final Context context = mock( Context.class );
        final EntityManagerFactory emf = mock( EntityManagerFactory.class );
        doReturn( emf ).when( context ).lookup( JNDI_NAME );
        InitialContextFactoryStub.registerContext( context );
        // when
        final EntityManagerFactory result = sut.doJndiLookup( EntityManagerFactory.class, JNDI_NAME );
        // then
        assertThat( result, sameInstance( emf ) );
    }

    @Test( expected = NullPointerException.class )
    public void shouldThrowExceptionIfContextReturnsNull()
        throws Exception
    {
        // given
        final Context context = mock( Context.class );
        doReturn( null ).when( context ).lookup( JNDI_NAME );
        InitialContextFactoryStub.registerContext( context );
        // when
        sut.doJndiLookup( EntityManagerFactory.class, JNDI_NAME );
    }

    @Test( expected = RuntimeException.class )
    public void shouldWrapNamingException()
        throws Exception
    {
        // given
        final Context context = mock( Context.class );
        doThrow( new NamingException() ).when( context ).lookup( JNDI_NAME );
        InitialContextFactoryStub.registerContext( context );
        // when
        sut.doJndiLookup( EntityManagerFactory.class, JNDI_NAME );
    }

}
