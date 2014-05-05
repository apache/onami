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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Test for {@link AllPersistenceUnits}.
 */
public class AllPersistenceUnitsTest
{
    private AllPersistenceUnits sut;

    private PersistenceService ps1;

    private PersistenceService ps2;

    private UnitOfWork uow1;

    private UnitOfWork uow2;

    @Before
    public void setUp()
        throws Exception
    {
        sut = new AllPersistenceUnits();

        ps1 = mock( PersistenceService.class );
        ps2 = mock( PersistenceService.class );

        uow1 = mock( UnitOfWork.class );
        uow2 = mock( UnitOfWork.class );

        sut.add( ps1, uow1 );
        sut.add( ps2, uow2 );
    }

    @Test
    public void shouldStartOnAllPersistenceServices()
        throws Exception
    {
        // when
        sut.startAllStoppedPersistenceServices();

        // then
        verify( ps1 ).start();
        verify( ps2 ).start();
    }

    @Test
    public void shouldStartOnRunningPersistenceServices()
        throws Exception
    {
        // given
        doReturn( true ).when( ps1 ).isRunning();
        doReturn( true ).when( ps2 ).isRunning();

        // when
        sut.startAllStoppedPersistenceServices();

        // then
        verify( ps1, never() ).start();
        verify( ps2, never() ).start();
    }

    @Test
    public void shouldStartOnAllPersistenceServicesEvenInCaseOfException()
        throws Exception
    {
        // given
        doThrow( new RuntimeException() ).when( ps1 ).start();
        doThrow( new RuntimeException() ).when( ps2 ).start();

        // when
        try
        {
            sut.startAllStoppedPersistenceServices();
        }

        // then
        catch ( AggregatedException e )
        {
            verify( ps1 ).start();
            verify( ps2 ).start();
            assertThat( e.getNumCauses(), is( 2 ) );
            return;
        }

        fail( "must throw AggregatedException" );
    }

    @Test
    public void shouldStopOnAllPersistenceServices()
        throws Exception
    {
        // when
        sut.stopAllPersistenceServices();

        // then
        verify( ps1 ).stop();
        verify( ps2 ).stop();
    }

    @Test
    public void shouldStopOnAllPersistenceServicesEvenInCaseOfException()
        throws Exception
    {
        // given
        doThrow( new RuntimeException() ).when( ps1 ).stop();
        doThrow( new RuntimeException() ).when( ps2 ).stop();

        // when
        try
        {
            sut.stopAllPersistenceServices();
        }

        // then
        catch ( AggregatedException e )
        {
            verify( ps1 ).stop();
            verify( ps2 ).stop();
            assertThat( e.getNumCauses(), is( 2 ) );
            return;
        }

        fail( "must throw AggregatedException" );
    }

    @Test
    public void shouldBeginOnAllUnitsOfWork()
        throws Exception
    {
        // when
        sut.beginAllInactiveUnitsOfWork();

        // then
        verify( uow1 ).begin();
        verify( uow2 ).begin();
    }

    @Test
    public void shouldNotBeginOnActiveUnitsOfWork()
        throws Exception
    {
        // given
        doReturn( true ).when( uow1 ).isActive();
        doReturn( true ).when( uow2 ).isActive();

        // when
        sut.beginAllInactiveUnitsOfWork();

        // then
        verify( uow1, never() ).begin();
        verify( uow2, never() ).begin();
    }

    @Test
    public void shouldBeginOnAllUnitsOfWorkEvenInCaseOfException()
        throws Exception
    {
        // given
        doThrow( new RuntimeException() ).when( uow1 ).begin();
        doThrow( new RuntimeException() ).when( uow2 ).begin();

        // when
        try
        {
            sut.beginAllInactiveUnitsOfWork();
        }

        // then
        catch ( AggregatedException e )
        {
            verify( uow1 ).begin();
            verify( uow2 ).begin();
            assertThat( e.getNumCauses(), is( 2 ) );
            return;
        }

        fail( "must throw AggregatedException" );
    }

    @Test
    public void shouldEndOnAllUnitsOfWork()
        throws Exception
    {
        // when
        sut.endAllUnitsOfWork();

        // then
        verify( uow1 ).end();
        verify( uow2 ).end();
    }

    @Test
    public void shouldEndOnAllUnitsOfWorkEvenInCaseOfException()
        throws Exception
    {
        // given
        doThrow( new RuntimeException() ).when( uow1 ).end();
        doThrow( new RuntimeException() ).when( uow2 ).end();

        // when
        try
        {
            sut.endAllUnitsOfWork();
        }

        // then
        catch ( AggregatedException e )
        {
            verify( uow1 ).end();
            verify( uow2 ).end();
            assertThat( e.getNumCauses(), is( 2 ) );
            return;
        }

        fail( "must throw AggregatedException" );
    }
}
