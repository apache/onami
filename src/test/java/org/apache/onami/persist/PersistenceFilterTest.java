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
import org.mockito.InOrder;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import static org.mockito.Mockito.*;

/**
 * Test for {@link PersistenceFilter}.
 */
public class PersistenceFilterTest
{
    private PersistenceFilter sut;

    private AllPersistenceUnits persistenceUnitsContainer;

    @Before
    public void setUp()
        throws Exception
    {
        persistenceUnitsContainer = mock( AllPersistenceUnits.class );
        sut = new PersistenceFilter( persistenceUnitsContainer );
    }

    @Test
    public void initShouldStartService()
        throws Exception
    {
        sut.init( mock( FilterConfig.class ) );
        verify( persistenceUnitsContainer ).startAllStoppedPersistenceServices();
    }

    @Test
    public void destroyShouldStopService()
    {
        sut.destroy();
        verify( persistenceUnitsContainer ).stopAllPersistenceServices();
    }

    @Test
    public void doFilterShouldSpanUnitOfWork()
        throws Exception
    {
        // given
        final FilterChain chain = mock( FilterChain.class );
        final InOrder inOrder = inOrder( persistenceUnitsContainer, chain );

        final ServletRequest request = mock( ServletRequest.class );
        final ServletResponse response = mock( ServletResponse.class );

        // when
        sut.doFilter( request, response, chain );

        // then
        inOrder.verify( persistenceUnitsContainer ).beginAllInactiveUnitsOfWork();
        inOrder.verify( chain ).doFilter( request, response );
        inOrder.verify( persistenceUnitsContainer ).endAllUnitsOfWork();
    }

    @Test(expected = RuntimeException.class)
    public void doFilterShouldEndUnitOfWorkInCaseOfException()
        throws Exception
    {
        // given
        final FilterChain chain = mock( FilterChain.class );
        final InOrder inOrder = inOrder( persistenceUnitsContainer, chain );

        final ServletRequest request = mock( ServletRequest.class );
        final ServletResponse response = mock( ServletResponse.class );

        doThrow( new RuntimeException() ).when( chain ).doFilter( request, response );

        // when
        try
        {
            sut.doFilter( request, response, chain );
        }
        // then
        finally
        {
            inOrder.verify( persistenceUnitsContainer ).beginAllInactiveUnitsOfWork();
            inOrder.verify( chain ).doFilter( request, response );
            inOrder.verify( persistenceUnitsContainer ).endAllUnitsOfWork();
        }
    }
}
