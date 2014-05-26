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
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test for {@link AggregatedException}.
 */
public class AggregatedExceptionTest
{
    private AggregatedException.Builder sut;

    @Before
    public void setUp()
        throws Exception
    {
        sut = new AggregatedException.Builder();
    }

    @Test
    public void shouldNotThrowAnythingWhenEmpty()
    {
        sut.throwRuntimeExceptionIfHasCauses( "test msg" );
    }

    @Test
    public void shouldThrowAggregatedExceptionWithAllCollectedExceptions()
    {
        final Exception e0 = new Exception();
        final Exception e1 = new Exception();

        try
        {
            sut.add( e0 );
            sut.add( e1 );
            sut.throwRuntimeExceptionIfHasCauses( "test msg" );
        }

        catch ( AggregatedException e )
        {
            assertThat( e.getNumCauses(), is( 2 ) );
            assertThat( e.getCauses()[0], sameInstance( (Throwable) e0 ) );
            assertThat( e.getCauses()[1], sameInstance( (Throwable) e1 ) );
            return;
        }

        fail( "must throw AggregatedException" );
    }

    @Test
    public void shouldThrowOriginalExceptionWhenOnlyOne()
    {
        Exception e0 = new RuntimeException(  );

        try
        {
            sut.add( e0 );
            sut.throwRuntimeExceptionIfHasCauses( "test msg" );
        }

        catch ( RuntimeException e )
        {
            assertThat( e, sameInstance( (Throwable) e0 ) );
            return;
        }

        fail( "must throw RuntimeException" );
    }

}
