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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Convenience for building lists of annotations
 */
public class ListBuilder
{
    private final List<Class<? extends Annotation>> list = new ArrayList<Class<? extends Annotation>>();

    /**
     * Return a new builder.
     *
     * @return new builder
     */
    public static ListBuilder builder()
    {
        return new ListBuilder();
    }

    /**
     * Return a new builder which has the input annotation as the first item.
     *
     * @param annotationClass annotation to initialize the list with.
     * @return new builder.
     */
    public static ListBuilder builder( Class<? extends Annotation> annotationClass )
    {
        return new ListBuilder().append( annotationClass );
    }

    /**
     * Append the input annotation to the list that is being built.
     *
     * @param annotationClass annotation to append.
     * @return self
     */
    public ListBuilder append( Class<? extends Annotation> annotationClass )
    {
        list.add( annotationClass );
        return this;
    }

    /**
     * Build and return the list.
     *
     * @return list with the built annotations.
     */
    public List<Class<? extends Annotation>> build()
    {
        return new ArrayList<Class<? extends Annotation>>( list );
    }

    /**
     * Hidden constructor, this class must be not instantiated directly.
     */
    private ListBuilder()
    {
        // do nothing
    }

}
