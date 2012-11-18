package org.nnsoft.guice.junice.handler;

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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nnsoft.guice.junice.annotation.Mock;
import org.nnsoft.guice.junice.mock.MockEngine;
import org.nnsoft.guice.junice.reflection.ClassVisitor;
import org.nnsoft.guice.junice.reflection.FieldHandler;
import org.nnsoft.guice.junice.reflection.HandleException;

/**
 * Handler class to handle all {@link Mock} annotations.
 *
 * @see ClassVisitor
 * @see Mock
 */
public final class MockHandler
    implements FieldHandler<Mock>
{

    private static final Logger logger = Logger.getLogger( MockHandler.class.getName() );

    final private HashMap<Field, Object> mockedObjects = new HashMap<Field, Object>( 1 );

    /**
     * @param engine
     * @return
     */
    public HashMap<Field, Object> getMockedObject( MockEngine engine )
    {
        createMockedObjectBymockFramekork( engine );
        return mockedObjects;
    }

    private void createMockedObjectBymockFramekork( MockEngine engine )
    {
        for ( Entry<Field, Object> entry : mockedObjects.entrySet() )
        {
            if ( entry.getValue() instanceof Class<?> )
            {
                Field field = entry.getKey();
                Mock mock = field.getAnnotation( Mock.class );
                mockedObjects.put( entry.getKey(), engine.createMock( (Class<?>) entry.getValue(), mock.type() ) );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public void handle( final Mock annotation, final Field element )
        throws HandleException
    {
        final Class<? super Object> type = (Class<? super Object>) element.getDeclaringClass();

        if ( logger.isLoggable( Level.FINER ) )
        {
            logger.finer( "      Found annotated field: " + element );
        }
        if ( annotation.providedBy().length() > 0 )
        {
            Class<?> providedClass = type;
            if ( annotation.providerClass() != Object.class )
            {
                providedClass = annotation.providerClass();
            }
            try
            {
                Method method = providedClass.getMethod( annotation.providedBy() );

                if ( !element.getType().isAssignableFrom( method.getReturnType() ) )
                {
                    throw new HandleException( "Impossible to mock " + element.getDeclaringClass().getName()
                        + " due to compatibility type, method provider " + providedClass.getName() + "#"
                        + annotation.providedBy() + " returns " + method.getReturnType().getName() );
                }
                try
                {
                    Object mocked = getMockProviderForType( element.getType(), method, type );
                    mockedObjects.put( element, mocked );
                }
                catch ( Throwable t )
                {
                    throw new HandleException( "Impossible to mock " + element.getDeclaringClass().getName()
                        + ", method provider " + providedClass.getName() + "#" + annotation.providedBy()
                        + " raised an error", t );
                }
            }
            catch ( SecurityException e )
            {
                throw new HandleException( "Impossible to mock " + element.getDeclaringClass().getName()
                    + ", impossible to access to method provider " + providedClass.getName() + "#"
                    + annotation.providedBy(), e );
            }
            catch ( NoSuchMethodException e )
            {
                throw new HandleException( "Impossible to mock " + element.getDeclaringClass().getName()
                    + ", the method provider " + providedClass.getName() + "#" + annotation.providedBy()
                    + " doesn't exist." );
            }
        }
        else
        {
            mockedObjects.put( element, element.getType() );
        }
    }

    @SuppressWarnings( "unchecked" )
    private <T> T getMockProviderForType( T t, Method method, Class<?> cls )
        throws HandleException
    {
        if ( method.getReturnType() == t )
        {
            try
            {
                if ( logger.isLoggable( Level.FINER ) )
                {
                    logger.finer( "        ...invoke Provider method for Mock: " + method.getName() );
                }
                if ( !Modifier.isPublic( method.getModifiers() ) || !Modifier.isStatic( method.getModifiers() ) )
                {
                    throw new HandleException( "Impossible to invoke method " + cls + "#" + method.getName()
                        + ". The method shuld be 'static public " + method.getReturnType().getName() + " "
                        + method.getName() + "()'" );
                }

                return (T) method.invoke( cls );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }
        throw new HandleException( "The method: " + method + " shuld be return a type " + t );
    }

}
