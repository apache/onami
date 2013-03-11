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

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static com.google.inject.matcher.Matchers.any;

/**
 * Abstract implementation of a module that requires an ordered list
 * of annotation types and a type matcher.
 */
abstract class AbstractLifeCycleModule
    extends AbstractModule
{

    /**
     * The annotation types the scanner will look for in the types methods
     * in the order that they will be matched.
     */
    private final List<Class<? extends Annotation>> annotationTypes;

    /**
     * The type matcher to filter classes where looking for lifecycle annotations.
     */
    private final Matcher<? super TypeLiteral<?>> typeMatcher;

    /**
     * Creates a new module which looks for the input lifecycle annotation on methods in any type.
     *
     * @param annotationType the lifecycle annotation to be searched.
     */
    public <A extends Annotation> AbstractLifeCycleModule( Class<A> annotationType )
    {
        this( ListBuilder.builder( annotationType ).build(), any() );
    }

    /**
     * Creates a new module which looks for the input lifecycle annotation on methods
     * in types filtered by the input matcher.
     *
     * @param annotationType the lifecycle annotation to be searched.
     * @param typeMatcher    the filter for injectee types.
     */
    public <A extends Annotation> AbstractLifeCycleModule( Class<A> annotationType,
                                                           Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        this( ListBuilder.builder( annotationType ).build(), typeMatcher );
    }

    /**
     * Creates a new module which looks for the input lifecycle annotations on methods in any type.
     *
     * @param annotationTypes the lifecycle annotations to be searched in the order to be searched.
     */
    public AbstractLifeCycleModule( List<Class<? extends Annotation>> annotationTypes )
    {
        this( annotationTypes, any() );
    }

    /**
     * Creates a new module which looks for the input lifecycle annotations on methods
     * in types filtered by the input matcher.
     *
     * @param annotationTypes the lifecycle annotations to be searched in the order to be searched.
     * @param typeMatcher     the filter for injectee types.
     */
    public AbstractLifeCycleModule( List<Class<? extends Annotation>> annotationTypes,
                                    Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        if ( annotationTypes == null )
        {
            throw new IllegalArgumentException( "annotationType must be specified" );
        }
        if ( typeMatcher == null )
        {
            throw new IllegalArgumentException( "typeMatcher must be specified" );
        }
        this.annotationTypes = new ArrayList<Class<? extends Annotation>>( annotationTypes );
        this.typeMatcher = typeMatcher;
    }

    /**
     * Returns the ordered annotation types the scanner will look for in the types methods.
     *
     * @return The ordered annotation types the scanner will look for in the types methods.
     */
    protected final List<Class<? extends Annotation>> getAnnotationTypes()
    {
        return annotationTypes;
    }

    /**
     * Returns the type matcher to filter classes where looking for lifecycle annotations.
     *
     * @return the type matcher to filter classes where looking for lifecycle annotations.
     */
    protected final Matcher<? super TypeLiteral<?>> getTypeMatcher()
    {
        return typeMatcher;
    }

}
