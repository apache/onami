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

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * <p>
 * Class visitor engine.
 * </p>
 * <p>
 * Visit the input class and all super classes and invokes handler to register annotations.
 * </p>
 */
public final class ClassVisitor
{

    private static final String JAVA_PACKAGE = "java";

    private static final Logger logger = Logger.getLogger( ClassVisitor.class.getName() );

    private final Multimap<Class<? extends Annotation>, AnnotationHandler<? extends Annotation, ? extends AnnotatedElement>> handlers =
        ArrayListMultimap.create();

    public <A extends Annotation> ClassVisitor registerHandler( Class<A> annotationType,
                                                        AnnotationHandler<A, ? extends AnnotatedElement> handler )
    {
        handlers.put( annotationType, handler );
        return this;
    }

    public <T> void visit( final Class<? super T> type )
        throws HandleException
    {
        checkArgument( type != null, "Type to be visited cannot be null" );

        if ( logger.isLoggable( Level.FINER ) )
        {
            logger.finer( "  Visit class: " + type );
        }

        if ( type.getPackage() != null && type.getPackage().getName().startsWith( JAVA_PACKAGE ) )
        {
            return;
        }

        handle( type );
        handle( type.getDeclaredFields() );
        handle( type.getDeclaredMethods() );

        visit( type.getSuperclass() );
    }

    @SuppressWarnings( "unchecked" )
    private void handle( AnnotatedElement... elements )
        throws HandleException
    {
        for ( AnnotatedElement element : elements )
        {
            for ( Annotation annotation : element.getAnnotations() )
            {
                for ( AnnotationHandler<? extends Annotation, ? extends AnnotatedElement> handler : handlers.get( annotation.annotationType() ) )
                {
                    ( (AnnotationHandler<Annotation, AnnotatedElement>) handler ).handle( annotation, element );
                }
            }
        }
    }

}
