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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ContainerManagedEntityManagerFactoryProvider}.
 */
public class ContainerManagedEntityManagerFactoryProviderTest
{
    private ContainerManagedEntityManagerFactoryProvider sut;

    private EntityManagerFactory emf;

    private EntityManagerFactorySource emfSource;

    @Before
    public void setup()
    {
        // input
        emfSource = mock( EntityManagerFactorySource.class );

        // subject under test
        sut = new ContainerManagedEntityManagerFactoryProvider( emfSource );

        // helpers
        emf = mock( EntityManagerFactory.class );
        doReturn( emf ).when( emfSource ).getEntityManagerFactory();
    }

    @Test
    public void isRunningShouldReturnFalseBeforeStarting()
    {
        assertThat( sut.isRunning(), is( false ) );
    }

    @Test
    public void stoppingWhenNotRunningShouldDoNothing()
    {
        sut.stop();

        assertThat( sut.isRunning(), is( false ) );
    }

    @Test
    public void isRunningShouldReturnTrueAfterStarting()
    {
        sut.start();

        assertThat( sut.isRunning(), is( true ) );
        verify( emfSource ).getEntityManagerFactory();
    }

    @Test( expected = IllegalStateException.class )
    public void startingAfterAlreadyStartedShouldThrowException()
    {
        sut.start();
        sut.start();
    }

    @Test
    public void isRunningShouldReturnFalseAfterStartingAndStopping()
    {
        sut.start();
        sut.stop();

        assertThat( sut.isRunning(), is( false ) );
        verify( emf, never() ).close();
    }

    @Test
    public void restartingShouldWork()
    {
        sut.start();
        sut.stop();
        sut.start();

        assertThat( sut.isRunning(), is( true ) );
        verify( emfSource, times( 2 ) ).getEntityManagerFactory();
        verify( emf, never() ).close();
    }

    @Test( expected = IllegalStateException.class )
    public void getShouldThrowExceptionWhenNotStarted()
    {
        sut.get();
    }

    @Test
    public void getShouldReturnEmf()
    {
        sut.start();

        assertThat( sut.get(), sameInstance( emf ) );
    }

    @Test( expected = NullPointerException.class )
    public void emfSourceIsMandatory()
    {
        new ContainerManagedEntityManagerFactoryProvider( null );
    }
}
