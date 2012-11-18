package org.nnsoft.guice.junice.reflection;

/*
 *    Copyright 2010-2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * Exception thrown by a {@link ClassVisitor} when a error occurs.
 */
public final class HandleException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    public HandleException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public HandleException( String message )
    {
        super( message );
    }

    public HandleException( Throwable cause )
    {
        super( cause );
    }

}
