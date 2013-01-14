package org.apache.onami.lifecycle;

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

import static com.google.inject.matcher.Matchers.any;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;

/**
 * Guice module to register methods to be invoked when {@link Disposer#dispose()} is invoked.
 * <p>
 * Module instance have state so it must not be used to construct more than one {@link Injector}. 
 */
public final class DisposeModule
    extends AbstractLifeCycleModule
{

    private final Disposer disposer;

    /**
     * Creates a new module which register methods annotated with {@link Dispose} on methods in any type.
     */
    public DisposeModule()
    {
        this( Dispose.class, any() );
    }

    /**
     * Creates a new module which register methods annotated with input annotation on methods
     * in types filtered by the input matcher.
     *
     * @param disposeAnnotationType the <i>Dispose</i> annotation to be searched.
     * @param typeMatcher the filter for injectee types.
     */
    public <A extends Annotation> DisposeModule( Class<A> disposeAnnotationType,
                                                 Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        super( disposeAnnotationType, typeMatcher );
        disposer = new Disposer();
    }

    /**
     * Creates a new module from the supplied {@link Builder}.
     *
     * @param builder settings container.
     * @since 0.2.0
     */
    DisposeModule( Builder builder )
    {
        super( builder.disposeAnnotationType, builder.typeMatcher );
        this.disposer = builder.disposer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure()
    {
        bind( Disposer.class ).toInstance( disposer );

        bindListener( getTypeMatcher(), new AbstractMethodTypeListener( getAnnotationType() )
        {

            @Override
            protected <I> void hear( final Method disposeMethod, TypeEncounter<I> encounter )
            {
                encounter.register( new InjectionListener<I>()
                {

                    public void afterInjection( I injectee )
                    {
                        disposer.register( disposeMethod, injectee );
                    }

                } );
            }

        } );
    }

    /**
     * Allows to create {@link DisposeModule} with builder pattern.
     *
     * @return builder for {@link DisposeModule}.
     * @since 0.2.0
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder pattern helper.
     *
     * @since 0.2.0
     */
    public static final class Builder
    {

        Class<? extends Annotation> disposeAnnotationType = Dispose.class;

        Matcher<? super TypeLiteral<?>> typeMatcher = any();

        Disposer disposer = new Disposer();

        /**
         * Hidden constructor.
         */
        Builder()
        {
        }

        /**
         * Builds {@link DisposeModule} with given settings.
         *
         * @return {@link DisposeModule} with given settings.
         * @since 0.2.0
         */
        public DisposeModule build()
        {
            return new DisposeModule( this );
        }

        /**
         * Sets <i>Dispose</i> annotation to be searched.
         *
         * @param disposeAnnotationType <i>Dispose</i> annotation to be searched.
         * @return self
         * @since 0.2.0
         */
        public Builder withDisposeAnnotationType( Class<? extends Annotation> disposeAnnotationType )
        {
            this.disposeAnnotationType = checkNotNull( disposeAnnotationType,
                                                       "Argument 'disposeAnnotationType' must be not null." );
            return this;
        }

        /**
         * Sets the filter for injectee types.
         *
         * @param typeMatcher the filter for injectee types.
         * @return self
         * @since 0.2.0
         */
        public Builder withTypeMatcher( Matcher<? super TypeLiteral<?>> typeMatcher )
        {
            this.typeMatcher = checkNotNull( typeMatcher, "Argument 'typeMatcher' must be not null." );
            return this;
        }

        /**
         * Sets the container to register disposable objects.
         *
         * @param disposer container to register disposable objects.
         * @return self
         * @since 0.2.0
         */
        public Builder withDisposer( Disposer disposer )
        {
            this.disposer = checkNotNull( disposer, "Argument 'disposer' must be not null." );
            return this;
        }

        private static <T> T checkNotNull( T object, String message )
        {
            if ( object == null )
            {
                throw new IllegalArgumentException( message );
            }
            return object;
        }

    }

}
