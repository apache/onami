package org.apache.onami.lifecycle.standard;

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
import org.apache.onami.lifecycle.core.LifeCycleStageModule;

import java.lang.annotation.Annotation;

import static com.google.inject.matcher.Matchers.any;

/**
 * Guice module to register methods to be invoked when {@link org.apache.onami.lifecycle.core.Stager#stage()} is invoked.
 * <p/>
 * Module instance have state so it must not be used to construct more than one {@link com.google.inject.Injector}.
 */
public class DisposeModule
    extends AbstractModule
{
    private final LifeCycleStageModule<?> lifeCycleStageModule;

    private final Disposer disposer;

    public DisposeModule()
    {
        disposer = new DefaultDisposer();
        lifeCycleStageModule = new LifeCycleStageModule<Dispose>( new StagerWrapper<Dispose>( disposer, Dispose.class ) );
    }

    /**
     * Creates a new module which register methods annotated with input annotation on methods
     * in types filtered by the input matcher.
     *
     * @param disposeAnnotationType the <i>Dispose</i> annotation to be searched.
     * @param typeMatcher           the filter for injectee types.
     */
    public <A extends Annotation> DisposeModule( Class<A> disposeAnnotationType,
                                                 Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        disposer = new DefaultDisposer();
        lifeCycleStageModule =
            LifeCycleStageModule.builder( disposeAnnotationType )
                .withTypeMatcher( typeMatcher )
                .withStager( new StagerWrapper<A>( disposer, disposeAnnotationType ) )
                .build();
    }

    @SuppressWarnings( "unchecked" )
    protected DisposeModule( Builder builder )
    {
        disposer = builder.disposer;
        StagerWrapper wrapper = new StagerWrapper( builder.disposer, builder.disposeAnnotationType );
        lifeCycleStageModule =
            LifeCycleStageModule.builder( builder.disposeAnnotationType ).withStager( wrapper ).withTypeMatcher(
                builder.typeMatcher ).build();
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @Override
    protected void configure()
    {
        bind(Disposer.class).toInstance( disposer );
        install( lifeCycleStageModule );
    }

    /**
     * Builder pattern helper.
     *
     * @since 0.2.0
     */
    public static final class Builder
    {

        private Class<? extends Annotation> disposeAnnotationType = Dispose.class;

        private Matcher<? super TypeLiteral<?>> typeMatcher = any();

        private Disposer disposer = new DefaultDisposer();

        /**
         * Hidden constructor.
         */
        Builder()
        {
        }

        private static <T> T checkNotNull( T object, String message )
        {
            if ( object == null )
            {
                throw new IllegalArgumentException( message );
            }
            return object;
        }

        /**
         * Builds {@link org.apache.onami.lifecycle.standard.DisposeModule} with given settings.
         *
         * @return {@link org.apache.onami.lifecycle.standard.DisposeModule} with given settings.
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
            this.disposeAnnotationType =
                checkNotNull( disposeAnnotationType, "Argument 'disposeAnnotationType' must be not null." );
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
    }
}
