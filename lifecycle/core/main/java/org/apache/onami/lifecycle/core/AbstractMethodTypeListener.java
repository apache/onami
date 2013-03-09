package org.apache.onami.lifecycle.core;

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

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * A Guice {@code TypeListener} to hear annotated methods with lifecycle annotations.
 */
abstract class AbstractMethodTypeListener
    implements TypeListener
{

    /**
     * The {@code java} package constants.
     */
    private static final String JAVA_PACKAGE = "java";

    /**
     * The lifecycle annotations to search on methods in the order to be searched.
     */
    private final List<Class<? extends Annotation>> annotationTypes;

    /**
     * Creates a new methods listener instance.
     *
     * @param annotationTypes the lifecycle annotations to search on methods in the order to be searched.
     */
    public AbstractMethodTypeListener( List<Class<? extends Annotation>> annotationTypes )
    {
        this.annotationTypes = annotationTypes;
    }

    /**
     * {@inheritDoc}
     */
    public final <I> void hear( TypeLiteral<I> type, TypeEncounter<I> encounter )
    {
        hear( type, type.getRawType(), encounter );
    }

    /**
     * Allows traverse the input klass hierarchy.
     *
     * @param parentType the owning type being heard
     * @param klass      encountered by Guice.
     * @param encounter  the injection context.
     */
    private <I> void hear( final TypeLiteral<I> parentType, Class<? super I> klass, TypeEncounter<I> encounter )
    {
        if ( klass == null || klass.getPackage().getName().startsWith( JAVA_PACKAGE ) )
        {
            return;
        }

        for ( Class<? extends Annotation> annotationType : annotationTypes )
        {
            for ( Method method : klass.getDeclaredMethods() )
            {
                if ( method.isAnnotationPresent( annotationType ) )
                {
                    if ( method.getParameterTypes().length != 0 )
                    {
                        encounter.addError( "Annotated methods with @%s must not accept any argument, found %s",
                                            annotationType.getName(), method );
                    }

                    hear( method, parentType, encounter, annotationType );
                }
            }
        }

        hear( parentType, klass.getSuperclass(), encounter );
    }

    /**
     * Returns the lifecycle annotation to search on methods.
     *
     * @return the lifecycle annotation to search on methods.
     */
    protected final List<Class<? extends Annotation>> getAnnotationTypes()
    {
        return annotationTypes;
    }

    /**
     * Allows implementations to define the behavior when lifecycle annotation is found on the method.
     *
     * @param method         encountered by this type handler.
     * @param parentType     the owning type being heard
     * @param encounter      the injection context.
     * @param annotationType the annotation type that was specified.
     */
    protected abstract <I> void hear( Method method, TypeLiteral<I> parentType, TypeEncounter<I> encounter,
                                      Class<? extends Annotation> annotationType );

}
