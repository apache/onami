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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test for {@link org.apache.onami.persist.EntityManagerProviderImpl}.
 * This class tests the behavior of the impl in the context of multiple threads.
 */
public class EntityManagerProviderImplThreadingTest
{
    private EntityManagerProviderImpl sut;

    private EntityManagerFactory emf;

    @Before
    public void setUp()
    {
        // input
        final EntityManagerFactoryProvider emfProvider = mock( EntityManagerFactoryProvider.class );

        // subject under test
        sut = new EntityManagerProviderImpl( emfProvider, null );

        // helpers
        emf = mock( EntityManagerFactory.class );
        doReturn( emf ).when( emfProvider ).get();

        doAnswer( new Answer()
        {
            public Object answer( InvocationOnMock invocation )
                throws Throwable
            {
                return mock( EntityManager.class );
            }
        } ).when( emf ).createEntityManager();
    }

    @Test
    public void beginShouldBeCallableFromMultipleThreads()
    {
        for ( int i = 0; i < 5; i++ )
        {
            new Thread( new Runnable()
            {
                public void run()
                {
                    assertThat( sut.isActive(), is( false ) );

                    sut.begin();

                    assertThat( sut.isActive(), is( true ) );
                }
            } ).start();
        }
    }

    @Test
    public void getShouldReturnTheSameInstanceInTheSameThread()
        throws Exception
    {
        final int numThreads = 5;
        final CountDownLatch latch = new CountDownLatch( numThreads );
        for ( int i = 0; i < numThreads; i++ )
        {
            new Thread( new Runnable()
            {
                public void run()
                {
                    sut.begin();

                    final EntityManager em1 = sut.get();
                    final EntityManager em2 = sut.get();
                    final EntityManager em3 = sut.get();
                    final EntityManager em4 = sut.get();

                    latch.countDown();

                    assertTrue( em1 == em2 );
                    assertTrue( em1 == em3 );
                    assertTrue( em1 == em4 );
                }
            } ).start();
        }

        latch.await();
        verify( emf, times( numThreads ) ).createEntityManager();
    }
}
