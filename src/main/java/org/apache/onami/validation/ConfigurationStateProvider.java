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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

import org.apache.bval.jsr303.ConfigurationImpl;

/**
 * The {@code javax.validation.spi.ConfigurationState} provider implementation.
 */
@Singleton
final class ConfigurationStateProvider
    implements Provider<ConfigurationState>
{

    @com.google.inject.Inject( optional = true )
    private BootstrapState bootstrapState;

    private final ValidationProvider<?> validationProvider;

    private final TraversableResolver traversableResolver;

    private final MessageInterpolator messageInterpolator;

    private final ConstraintValidatorFactory constraintValidatorFactory;

    @Inject
    public ConfigurationStateProvider( ValidationProvider<?> validationProvider,
			TraversableResolver traversableResolver,
			MessageInterpolator messageInterpolator,
			ConstraintValidatorFactory constraintValidatorFactory )
    {
		this.validationProvider = validationProvider;
		this.traversableResolver = traversableResolver;
		this.messageInterpolator = messageInterpolator;
		this.constraintValidatorFactory = constraintValidatorFactory;
	}

    /**
     * {@inheritDoc}
     */
    public ConfigurationState get()
    {
        ConfigurationImpl configuration = new ConfigurationImpl( bootstrapState, validationProvider );
        configuration.traversableResolver( traversableResolver );
        configuration.messageInterpolator( messageInterpolator );
        configuration.constraintValidatorFactory( constraintValidatorFactory );
        return configuration;
    }

}
