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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Exception holding an aggregation of multiple exceptions which were collected.
 */
class AggregatedException
    extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * Builder for AggregatedException
     */
    static class Builder
    {

        /**
         * list of causes for the aggregated exception.
         */
        private List<Throwable> causes = new ArrayList<Throwable>();

        /**
         * Adds an exception to the list of aggregated exceptions.
         *
         * @param cause the exception to add
         */
        void add( Throwable cause )
        {
            causes.add( checkNotNull( cause, "cause is mandatory!" ) );
        }

        /**
         * Throws a runtime exception if the builder has causes.
         *
         * @param msg the message of the aggregated exception.
         */
        void throwRuntimeExceptionIfHasCauses( String msg )
        {
            try
            {
                if ( !causes.isEmpty() )
                {
                    throw getRuntimeException( msg );
                }
            }
            finally
            {
                causes = null;
            }
        }

        /**
         * Converts the collected causes into a runtime exception
         *
         * @param msg the message of the aggregated exception.
         * @return the exception to throw
         */
        private RuntimeException getRuntimeException( String msg )
        {
            if ( causes.size() == 1 )
            {
                final Throwable cause = causes.get( 0 );
                if ( cause instanceof RuntimeException )
                {
                    return (RuntimeException) cause;
                }
            }
            return new AggregatedException( msg, causes.toArray( new Throwable[causes.size()] ) );
        }
    }

    /**
     * all the underlying causes for this aggregated exception.
     */
    private final Throwable[] causes;

    /**
     * number of causes for this aggregated exceptions.
     */
    private final int numCauses;

    /**
     * Constructor.
     *
     * @param message the message
     * @param causes  all the causes
     */
    private AggregatedException( String message, Throwable[] causes )
    {
        super( message );
        this.causes = causes;
        this.numCauses = this.causes.length;
    }

    /**
     * @return the causes which lead to this exception
     */
    public Throwable[] getCauses()
    {
        return causes.clone();
    }

    /**
     * @return the number of causes collected into this exception
     */
    public int getNumCauses()
    {
        return numCauses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStackTrace( PrintStream s )
    {
        synchronized ( s )
        {

            s.println( this );
            StackTraceElement[] trace = getStackTrace();
            for ( final StackTraceElement aTrace : trace )
            {
                s.println( "\tat " + aTrace );
            }

            for ( int i = 0; i < numCauses; i++ )
            {
                s.println( "Cause " + ( i + 1 ) + ":" );
                causes[i].printStackTrace( s );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStackTrace( PrintWriter s )
    {
        synchronized ( s )
        {

            s.println( this );
            StackTraceElement[] trace = getStackTrace();
            for ( final StackTraceElement aTrace : trace )
            {
                s.println( "\tat " + aTrace );
            }

            for ( int i = 0; i < numCauses; i++ )
            {
                s.println( "Cause " + ( i + 1 ) + ":" );
                causes[i].printStackTrace( s );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return super.toString() + " (caused by " + numCauses + " causes)";
    }

}
