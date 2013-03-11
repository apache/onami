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

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.MoreTypes;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.google.inject.matcher.Matchers.any;

/**
 * Guice module to register methods to be invoked when {@link Stager#stage()} is invoked.
 * <p/>
 * Module instance have has so it must not be used to construct more than one {@link com.google.inject.Injector}.
 */
public final class LifeCycleStageModule<A extends Annotation>
    extends AbstractLifeCycleModule
{

    private final Stager<A> stager;

    private final StageableTypeMapper<A> typeMapper;

    /**
     * Creates a new module which register methods annotated with input annotation on methods in any type.
     *
     * @param stager the annotation that represents this stage and the methods with this annotation
     */
    public LifeCycleStageModule( Stager<A> stager )
    {
        this( stager, any() );
    }

    /**
     * Creates a new module which register methods annotated with input annotation on methods
     * in types filtered by the input matcher.
     *
     * @param stager      the annotation that represents this stage and the methods with this annotation
     * @param typeMatcher the filter for injectee types.
     */
    public LifeCycleStageModule( Stager<A> stager, Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        super( stager.getStage(), typeMatcher );
        this.stager = stager;
        typeMapper = new NoOpStageableTypeMapper<A>();
    }

    /**
     * Creates a new module from the supplied {@link Builder}.
     *
     * @param builder settings container.
     */
    LifeCycleStageModule( Builder<A> builder )
    {
        super( builder.stager.getStage(), builder.typeMatcher );
        this.stager = builder.stager;
        this.typeMapper = builder.typeMapper;
    }

    /**
     * Allows one to create {@link LifeCycleStageModule} with builder pattern.
     *
     * @param stage the annotation that represents this stage and the methods with this annotation
     * @return builder for {@link LifeCycleStageModule}.
     */
    public static <A extends Annotation> Builder<A> builder( Class<A> stage )
    {
        return new Builder<A>( stage );
    }

    /**
     * Convenience to generate the correct key for retrieving stagers from an injector.
     * E.g.
     * <p/>
     * <code><pre>
     * Stager&lt;MyAnnotation&gt; stager = injector.getInstance( LifeCycleStageModule.key( MyAnnotation.class ) );
     * </pre></code>
     *
     * @param stage the annotation that represents this stage and the methods with this annotation
     * @param <A>   the Annotation type
     * @return the Guice key to use for accessing the stager for the input stage
     */
    public static <A extends Annotation> Key<Stager<A>> key( Class<A> stage )
    {
        return Key.get( type( stage ) );
    }

    private static <A extends Annotation> TypeLiteral<Stager<A>> type( Class<A> stage )
    {
        MoreTypes.ParameterizedTypeImpl parameterizedType =
            new MoreTypes.ParameterizedTypeImpl( null, Stager.class, stage );
        //noinspection unchecked
        @SuppressWarnings( "unchecked" ) // TODO
        TypeLiteral<Stager<A>> stagerType = (TypeLiteral<Stager<A>>) TypeLiteral.get( parameterizedType );
        return stagerType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure()
    {
        binder().bind( type( stager.getStage() ) ).toInstance( stager );

        bindListener( getTypeMatcher(), new AbstractMethodTypeListener( getAnnotationTypes() )
        {

            @Override
            protected <I> void hear( final Method stageMethod, final TypeLiteral<I> parentType,
                                     final TypeEncounter<I> encounter,
                                     final Class<? extends Annotation> annotationType )
            {
                encounter.register( new InjectionListener<I>()
                {

                    public void afterInjection( I injectee )
                    {
                        Stageable stageable = new StageableMethod( stageMethod, injectee );
                        stager.register( stageable );
                        typeMapper.registerType( stageable, parentType );
                    }

                } );
            }

        } );
    }

    /**
     * Builder pattern helper.
     */
    public static final class Builder<A extends Annotation>
    {

        private Matcher<? super TypeLiteral<?>> typeMatcher = any();

        private Stager<A> stager;

        private StageableTypeMapper<A> typeMapper = new NoOpStageableTypeMapper<A>();

        Builder( Class<A> annotationClass )
        {
            stager = new DefaultStager<A>( annotationClass, DefaultStager.Order.FIRST_IN_FIRST_OUT );
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
         * Builds {@link LifeCycleStageModule} with given settings.
         *
         * @return {@link LifeCycleStageModule} with given settings.
         */
        public LifeCycleStageModule<A> build()
        {
            return new LifeCycleStageModule<A>( this );
        }

        /**
         * Sets the filter for injectee types.
         *
         * @param typeMatcher the filter for injectee types.
         * @return self
         */
        public Builder<A> withTypeMatcher( Matcher<? super TypeLiteral<?>> typeMatcher )
        {
            this.typeMatcher = checkNotNull( typeMatcher, "Argument 'typeMatcher' must be not null." );
            return this;
        }

        /**
         * Sets the container to register disposable objects.
         *
         * @param stager container to register disposable objects.
         * @return self
         */
        public Builder<A> withStager( Stager<A> stager )
        {
            this.stager = checkNotNull( stager, "Argument 'stager' must be not null." );
            return this;
        }

        /**
         * Sets the container to register mappings from {@link Stageable}s to the types that created them.
         *
         * @param typeMapper container to map {@link Stageable}s to types
         * @return self
         */
        public Builder<A> withTypeMapper( StageableTypeMapper<A> typeMapper )
        {
            this.typeMapper = checkNotNull( typeMapper, "Argument 'typeMapper' must be not null." );
            return this;
        }

    }

}
