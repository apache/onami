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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Test for {@link EntityManagerProviderImpl}.
 */
public class EntityManagerProviderImplTest
{

    private EntityManagerProviderImpl sut;

    private EntityManagerFactoryProvider emfProvider;

    private EntityManager em;

    private Properties properties;

    private EntityManagerFactory emf;

    @Before
    public void setUp()
    {
        // input
        emfProvider = mock( EntityManagerFactoryProvider.class );
        properties = new Properties();

        // subject under test
        sut = new EntityManagerProviderImpl( emfProvider, properties );

        // helpers
        emf = mock( EntityManagerFactory.class );
        doReturn( emf ).when( emfProvider ).get();

        em = mock( EntityManager.class );
        doReturn( em ).when( emf ).createEntityManager( properties );
    }

    @Test
    public void newInstanceShouldNotBeActive()
    {
        assertThat( sut.isActive(), is( false ) );
    }

    @Test
    public void stoppingShouldDoNothingIfNotActive()
    {
        sut.end();

        assertThat( sut.isActive(), is( false ) );
    }

    @Test
    public void shouldBeActiveAfterStarting()
    {
        sut.begin();

        verify( emf ).createEntityManager( properties );
        assertThat( sut.isActive(), is( true ) );
    }

    @Test
    public void shouldNotBeActiveAfterStartingAndStopping()
    {
        sut.begin();
        sut.end();

        verify( emf ).createEntityManager( properties );
        verify( em ).close();
        assertThat( sut.isActive(), is( false ) );
    }

    @Test
    public void shouldNotBeActiveAfterStartingAndStoppingEvenWhenExceptionThrown()
    {
        doThrow( new RuntimeException() ).when( em ).close();

        try
        {
            sut.begin();
            sut.end();
        }

        catch ( RuntimeException e )
        {
            verify( emf ).createEntityManager( properties );
            verify( em ).close();
            assertThat( sut.isActive(), is( false ) );
            return;
        }
        fail( "expected RuntimeException to be thrown" );
    }

    @Test
    public void restartingShouldWork()
    {
        sut.begin();
        sut.end();
        sut.begin();

        verify( emf, times( 2 ) ).createEntityManager( properties );
        verify( em ).close();
        assertThat( sut.isActive(), is( true ) );
    }

    @Test( expected = IllegalStateException.class )
    public void startingWhenActiveShouldThrowException()
    {
        sut.begin();
        sut.begin();
    }

    @Test
    public void shouldReturnTheEntityManager()
    {
        sut.begin();
        final EntityManager result = sut.get();

        assertThat( result, sameInstance( em ) );
    }

    @Test( expected = IllegalStateException.class )
    public void shouldThrowExceptionWhenGettingEntityManagerAndUnitOfWorkIsNotActive()
    {
        sut.get();
    }

    @Test( expected = NullPointerException.class )
    public void entityManagerFactoryProviderIsMandatory()
    {
        new EntityManagerProviderImpl( null, properties );
    }

    @Test
    public void propertiesAreOptional()
    {
        new EntityManagerProviderImpl( emfProvider, null );
    }

    @Test
    public void shouldCreateEntityManagerWithoutPropertiesIfNull()
    {
        doReturn( em ).when( emf ).createEntityManager();
        sut = new EntityManagerProviderImpl( emfProvider, null );

        sut.begin();

        verify( emf ).createEntityManager();
    }

}
