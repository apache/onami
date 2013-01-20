package org.apache.onami.logging.core;

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

import static java.lang.String.format;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.google.inject.Binder;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Abstract module implementation of Logging module that simplifies Logger
 * building and injection.
 *
 * Subclasses have to specify the Logger and the relative
 * {@link AbstractLoggerInjector} types.
 *
 * @param <L> the Logger type has to be injected.
 */
public class AbstractLoggingModule<L>
    extends TypeLiteral<L>
    implements Module, TypeListener
{

    /**
     * The types matcher for whom the Logger injection has to be performed.
     */
    private final Matcher<? super TypeLiteral<?>> matcher;

    /**
     * The concrete Logger type.
     */
    private final Class<?> loggerClass;

    /**
     * The {@link AbstractLoggerInjector} constructor, instances will be created
     * at runtime.
     */
    private final Constructor<? extends MembersInjector<L>> logInjectorConstructor;

    /**
     * Creates a new Logger injection module.
     *
     * @param <LI> the concrete {@link AbstractLoggerInjector}
     * @param matcher types matcher for whom the Logger injection has to be
     *        performed.
     * @param loggerInjectorClass the {@link AbstractLoggerInjector} constructor.
     */
    public <LI extends AbstractLoggerInjector<L>> AbstractLoggingModule( Matcher<? super TypeLiteral<?>> matcher,
                                                                         Class<LI> loggerInjectorClass )
    {
        if ( matcher == null )
        {
            throw new IllegalArgumentException( "Parameter 'matcher' must not be null" );
        }
        if ( loggerInjectorClass == null )
        {
            throw new IllegalArgumentException( "Parameter 'loggerInjectorClass' must not be null" );
        }

        this.matcher = matcher;
        loggerClass = getRawType( getType() );
        try
        {
            logInjectorConstructor = loggerInjectorClass.getConstructor( Field.class );
        }
        catch ( SecurityException e )
        {
            throw new ProvisionException( format( "Impossible to access to '%s(%s)' public constructor due to security violation: %s",
                                                  loggerInjectorClass.getName(), Field.class.getName(), e.getMessage() ) );
        }
        catch ( NoSuchMethodException e )
        {
            throw new ProvisionException( format( "Class '%s' doesn't have a public construcor with <%s> parameter type: %s",
                                                  loggerInjectorClass.getName(), Field.class.getName(), e.getMessage() ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void configure( Binder binder )
    {
        binder.bindListener( matcher, this );
    }

    /**
     * {@inheritDoc}
     */
    public final <I> void hear( TypeLiteral<I> type, TypeEncounter<I> encounter )
    {
        hear( type.getRawType(), encounter );
    }

    @SuppressWarnings("unchecked")
    private <I> void hear( Class<?> klass, TypeEncounter<I> encounter )
    {
        if ( Object.class == klass )
        {
            return;
        }

        for ( Field field : klass.getDeclaredFields() )
        {
            if ( loggerClass == field.getType() && field.isAnnotationPresent( InjectLogger.class ) )
            {
                try
                {
                    encounter.register( (MembersInjector<? super I>) logInjectorConstructor.newInstance( field ) );
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( format( "Impossible to register '%s' for field '%s', see nested exception",
                                                        logInjectorConstructor.getName(),
                                                        field ), e );
                }
            }
        }

        hear( klass.getSuperclass(), encounter );
    }

    private static Class<?> getRawType( Type type )
    {
        if ( type instanceof Class<?> )
        {
            // type is a normal class.
            return (Class<?>) type;
        }
        else if ( type instanceof ParameterizedType )
        {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class.
            // Neal isn't either but suspects some pathological case related
            // to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            if ( !(rawType instanceof Class) )
            {
                throw new IllegalArgumentException( format( "Expected a Class, but <%s> is of type %s",
                                                            type,
                                                            type.getClass().getName() ) );
            }
            return (Class<?>) rawType;
        }
        else if ( type instanceof GenericArrayType )
        {
            Type componentType = ( (GenericArrayType) type ).getGenericComponentType();
            return Array.newInstance( getRawType( componentType ), 0 ).getClass();
        }
        else if ( type instanceof TypeVariable )
        {
            // we could use the variable's bounds, but that'll won't work if there are multiple.
            // having a raw type that's more general than necessary is okay
            return Object.class;
        }
        else
        {
            throw new IllegalArgumentException( format( "Expected a Class, ParameterizedType, or GenericArrayType, but <%s> is of type %s",
                                                        type,
                                                        type.getClass().getName() ) );
        }
    }

}
