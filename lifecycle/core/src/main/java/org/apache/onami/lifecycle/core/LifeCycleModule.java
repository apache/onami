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

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static java.lang.String.format;

/**
 * Guice module to register methods to be invoked after injection is complete.
 */
public final class LifeCycleModule
    extends AbstractLifeCycleModule
{

    /**
     * Creates a new module which looks for the input lifecycle annotation on methods in any type.
     *
     * @param annotationType the lifecycle annotation to be searched.
     */
    public <A extends Annotation> LifeCycleModule( Class<A> annotationType )
    {
        super( annotationType );
    }

    /**
     * Creates a new module which looks for the input lifecycle annotation on methods
     * in types filtered by the input matcher.
     *
     * @param annotationType the lifecycle annotation to be searched.
     * @param typeMatcher    the filter for injectee types.
     */
    public <A extends Annotation> LifeCycleModule( Class<A> annotationType,
                                                   Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        super( annotationType, typeMatcher );
    }

    /**
     * Creates a new module which looks for the input lifecycle annotations on methods in any type.
     *
     * @param annotationTypes the lifecycle annotations to be searched in the order to be searched.
     */
    public LifeCycleModule( List<Class<? extends Annotation>> annotationTypes )
    {
        super( annotationTypes );
    }

    /**
     * Creates a new module which looks for the input lifecycle annotations on methods
     * in types filtered by the input matcher.
     *
     * @param annotationTypes the lifecycle annotations to be searched in the order to be searched.
     * @param typeMatcher     the filter for injectee types.
     */
    public LifeCycleModule( List<Class<? extends Annotation>> annotationTypes,
                            Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        super( annotationTypes, typeMatcher );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure()
    {
        bindListener( getTypeMatcher(), new AbstractMethodTypeListener( getAnnotationTypes() )
        {

            @Override
            protected <I> void hear( final Method method, TypeLiteral<I> parentType, TypeEncounter<I> encounter,
                                     final Class<? extends Annotation> annotationType )
            {
                encounter.register( new InjectionListener<I>()
                {

                    public void afterInjection( I injectee )
                    {
                        try
                        {
                            method.invoke( injectee );
                        }
                        catch ( IllegalArgumentException e )
                        {
                            // should not happen, anyway...
                            throw new ProvisionException(
                                format( "Method @%s %s requires arguments", annotationType.getName(), method ), e );
                        }
                        catch ( IllegalAccessException e )
                        {
                            throw new ProvisionException(
                                format( "Impossible to access to @%s %s on %s", annotationType.getName(), method,
                                        injectee ), e );
                        }
                        catch ( InvocationTargetException e )
                        {
                            throw new ProvisionException(
                                format( "An error occurred while invoking @%s %s on %s", annotationType.getName(),
                                        method, injectee ), e.getTargetException() );
                        }
                    }

                } );
            }

        } );
    }

}
