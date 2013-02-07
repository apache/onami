package org.apache.onami.validation;

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

import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.bval.jsr303.ApacheValidationProvider;
import org.apache.bval.jsr303.DefaultMessageInterpolator;
import org.apache.bval.jsr303.resolver.DefaultTraversableResolver;
import org.kohsuke.MetaInfServices;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * The Google-Guice Validation module.
 */
@MetaInfServices( Module.class )
public final class ValidationModule
    extends AbstractModule
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure()
    {
        // apache bval bootstrap
        bind( MessageInterpolator.class ).to( DefaultMessageInterpolator.class ).in( SINGLETON );
        bind( TraversableResolver.class ).to( DefaultTraversableResolver.class ).in( SINGLETON );
        bind( ConstraintValidatorFactory.class ).to( GuiceAwareConstraintValidatorFactory.class );
        bind( new TypeLiteral<ValidationProvider<?>>(){} ).to( ApacheValidationProvider.class ).in( SINGLETON );
        bind( ConfigurationState.class ).toProvider( ConfigurationStateProvider.class ).in( SINGLETON );
        bind( ValidatorFactory.class ).toProvider( ValidatorFactoryProvider.class ).in( SINGLETON );
        bind( Validator.class ).toProvider( ValidatorProvider.class );

        // AOP stuff
        MethodInterceptor validateMethodInterceptor = new ValidateMethodInterceptor();
        binder().requestInjection( validateMethodInterceptor );
        bindInterceptor( any(), annotatedWith( Validate.class ), validateMethodInterceptor );
    }

}
