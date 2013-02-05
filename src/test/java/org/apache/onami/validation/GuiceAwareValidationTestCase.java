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

import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith( OnamiRunner.class )
@GuiceModules( ValidationModule.class )
public final class GuiceAwareValidationTestCase
{

    @Inject
    private Validator validator;

    @Inject
    private DummyCountryDao dummyCountryDao;

    public void setValidator( Validator validator )
    {
        this.validator = validator;
    }

    public void setDummyCountryDao( DummyCountryDao dummyCountryDao )
    {
        this.dummyCountryDao = dummyCountryDao;
    }

    @Test
    public void testInjectedValidation()
    {
        Country country = new Country();
        country.setName( "Italy" );
        country.setIso2Code( "it" );
        country.setIso3Code( "ita" );

        Set<ConstraintViolation<Country>> violations = validator.validate( country );
        assertTrue( violations.isEmpty() );
    }

    @Test
    public void testAOPInjectedValidation()
    {
        dummyCountryDao.insertCountry( "Italy", "it", "ita" );
    }

    @Test( expected = ConstraintViolationException.class )
    public void testAOPInjectedFailedValidation()
        throws Exception
    {
        dummyCountryDao.insertCountry( "Italy", "ita", "ita" );
    }

    @Test( expected = DummyException.class )
    public void testRethrowWrappedException()
    {
        dummyCountryDao.updateCountry( new Country() );
    }

}
