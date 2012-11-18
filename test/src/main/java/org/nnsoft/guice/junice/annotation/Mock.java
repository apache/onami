package org.nnsoft.guice.junice.annotation;

/*
 *    Copyright 2010-2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nnsoft.guice.junice.GuiceMockModule;

import com.google.inject.name.Named;

/**
 * Annotate your filed into which {@link GuiceMockModule} will create and inject the mock object.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
@Inherited
public @interface Mock
{

    public class NoAnnotation
    {
    }

    /**
     * Indicates if this mock object has to be resetted after each test method Default: true
     *
     * @return the value
     */
    public boolean resetAfter() default true;

    /**
     * The name of the method that provides to mock creation.
     *
     * @return
     */
    public String providedBy() default "";

    /**
     * The {@link Class} that contains the method {@link Mock#providedBy()}. By default: the filed declaring class.
     *
     * @return
     */
    public Class<?> providerClass() default Object.class;

    /**
     * Specifies an annotaion {@link Class} that will be used in the <em>Google Guice</em> binder to execute the literal
     * annotating binding.
     *
     * @return
     */
    public Class<?> annotatedWith() default NoAnnotation.class;

    /**
     * Specifies an {@link String} annotation that will be used in the <em>Google Guice</em> binder to execute the
     * literal annotating binding via {@link Named} class.
     *
     * @return
     */
    public String namedWith() default "";

    /**
     * Specifies
     *
     * @return
     */
    public MockObjType type() default MockObjType.DEFAULT;

}
