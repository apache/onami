package org.apache.onami.lifecycle.warmup;

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
import org.apache.onami.lifecycle.core.StageableTypeMapper;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import static com.google.inject.matcher.Matchers.any;

/**
 * The module for preparing for warm ups.
 */
public class WarmUpModule<A extends Annotation>
    extends AbstractModule
{
    private final LifeCycleStageModule<A> lifeCycleStageModule;

    private static final long DEFAULT_WAIT_MS = TimeUnit.DAYS.toMillis( Integer.MAX_VALUE );    // essentially forever

    /**
     * Creates a new module which register methods annotated with input annotation on methods
     * in types filtered by the input matcher.
     *
     * @param stage       the annotation to be searched.
     */
    public WarmUpModule( Class<A> stage )
    {
        this( stage, any() );
    }

    /**
     * Creates a new module which register methods annotated with input annotation on methods
     * in types filtered by the input matcher.
     *
     * @param stage       the annotation to be searched.
     * @param typeMatcher the filter for injectee types.
     */
    public WarmUpModule( Class<A> stage, Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        WarmUper<A> stager = new WarmUper<A>( stage, DEFAULT_WAIT_MS );
        lifeCycleStageModule =
            LifeCycleStageModule.builder( stage ).withTypeMatcher( typeMatcher ).withStager( stager ).withTypeMapper(
                stager ).build();
    }

    /**
     * Return a new standard warm up module
     *
     * @return warm up module
     */
    public static WarmUpModule<WarmUp> newWarmUpModule()
    {
        return new WarmUpModule<WarmUp>( WarmUp.class );
    }

    /**
     * Return a new standard warm up module
     *
     * @param typeMatcher     the filter for injectee types.
     * @return warm up module
     */
    public static WarmUpModule<WarmUp> newWarmUpModule( Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        return new WarmUpModule<WarmUp>( WarmUp.class, typeMatcher );
    }

    /**
     * Allows one to create WarmUpModule with builder pattern.
     *
     * @return builder for WarmUpModule.
     */
    public static Builder<WarmUp> builder()
    {
        WarmUper<WarmUp> stager = new WarmUper<WarmUp>( WarmUp.class, DEFAULT_WAIT_MS );
        return new Builder<WarmUp>( WarmUp.class, stager ).withTypeMapper( stager );
    }

    /**
     * Allows one to create WarmUpModule with builder pattern.
     *
     * @param stage       the annotation to be searched.
     * @return builder for WarmUpModule.
     */
    public static <A extends Annotation> Builder<A> builder( Class<A> stage )
    {
        WarmUper<A> stager = new WarmUper<A>( stage, DEFAULT_WAIT_MS );
        return new Builder<A>( stage, stager ).withTypeMapper( stager );
    }

    @Override
    protected void configure()
    {
        binder().install( lifeCycleStageModule );
    }

    public static class Builder<A extends Annotation>
    {
        private final WarmUper<A> stager;

        private LifeCycleStageModule.Builder<A> internalBuilder;

        Builder( Class<A> annotationClass, WarmUper<A> stager )
        {
            this.stager = stager;
            internalBuilder = LifeCycleStageModule.builder( annotationClass ).withStager( stager );
        }

        /**
         * Builds {@link LifeCycleStageModule} with given settings.
         *
         * @return {@link LifeCycleStageModule} with given settings.
         */
        public LifeCycleStageModule<A> build()
        {
            return internalBuilder.build();
        }

        /**
         * Sets the filter for injectee types.
         *
         * @param typeMatcher the filter for injectee types.
         * @return self
         */
        public Builder<A> withTypeMatcher( Matcher<? super TypeLiteral<?>> typeMatcher )
        {
            internalBuilder.withTypeMatcher( typeMatcher );
            return this;
        }

        /**
         * Sets the container to register mappings from {@link org.apache.onami.lifecycle.core.Stageable}s to the types that created them.
         *
         * @param typeMapper container to map {@link org.apache.onami.lifecycle.core.Stageable}s to types
         * @return self
         */
        public Builder<A> withTypeMapper( StageableTypeMapper<A> typeMapper )
        {
            internalBuilder.withTypeMapper( typeMapper );
            return this;
        }

        /**
         * When the warm up is staged, it will wait until this maximum time for warm ups to finish.
         * The default is to wait forever
         *
         * @param maxWait max time to wait
         * @param unit    time unit
         * @return self
         */
        public Builder<A> withMaxWait( long maxWait, TimeUnit unit )
        {
            stager.setMaxWait( maxWait, unit );
            return this;
        }
    }
}
