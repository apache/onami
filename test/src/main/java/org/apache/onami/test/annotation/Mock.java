package org.apache.onami.test.annotation;

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate your filed into which {@link org.apache.onami.test.GuiceMockModule} will create and inject the mock object.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
@Inherited
public @interface Mock
{

    public static class NoAnnotation
    {
    }

    /**
     * Indicates if this mock object has to be resetted after each test method Default: true
     *
     * @return the value
     */
    boolean resetAfter() default true;

    /**
     * The name of the method that provides to mock creation.
     *
     * @return
     */
    String providedBy() default "";

    /**
     * The {@link Class} that contains the method {@link Mock#providedBy()}. By default: the filed declaring class.
     *
     * @return
     */
    Class<?> providerClass() default Object.class;

    /**
     * Specifies an annotaion {@link Class} that will be used in the <em>Google Guice</em> binder to execute the literal
     * annotating binding.
     *
     * @return
     */
    Class<?> annotatedWith() default NoAnnotation.class;

    /**
     * Specifies an {@link String} annotation that will be used in the <em>Google Guice</em> binder to execute the
     * literal annotating binding via {@link com.google.inject.name.Named} class.
     *
     * @return
     */
    String namedWith() default "";

    /**
     * Specifies
     *
     * @return
     */
    MockObjType type() default MockObjType.DEFAULT;

}
