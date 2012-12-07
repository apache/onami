package org.apache.onami.autobind.annotations;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.apache.onami.autobind.annotations.To.Type.INTERFACES;
import static org.apache.onami.autobind.jsr330.Names.named;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

import javax.inject.Named;

import org.apache.onami.autobind.annotations.To.Type;

public class Annotations
{

    /**
     * Hidden constructor, this class cannot be instantiated.
     */
    protected Annotations()
    {
        // do nothing
    }

    public static Bind createBind()
    {
        return createBind( INTERFACES );
    }

    public static Bind createBind( final Type type )
    {
        return new Bind()
        {

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return Bind.class;
            }

            @Override
            public Named value()
            {
                return named( "" );
            }

            @Override
            public AnnotatedWith annotatedWith() {
                return null;
            }

            @Override
            public To to()
            {
                return createTo( type );
            }

            @Override
            public boolean multiple()
            {
                return false;
            }

        };
    }

    @SuppressWarnings("unchecked")
    public static AnnotatedWith createAnnotatedWith( )
    {
        return createAnnotatedWith( (Class<? super Annotation>[]) Array.newInstance(Annotation.class, 0) );
    }

    public static AnnotatedWith createAnnotatedWith( final Class<? super Annotation>... annotations )
    {
        return new AnnotatedWith()
        {

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return AnnotatedWith.class;
            }

            @Override
            public Class<? super Annotation>[] value()
            {
                return annotations;
            }

        };
    }

    public static To createTo()
    {
        return createTo( INTERFACES );
    }

    public static To createTo( final Type type )
    {
        return new To()
        {

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return To.class;
            }

            @Override
            public Type value()
            {
                return type;
            }

            @Override
            public Class<? extends Object>[] customs()
            {
                return new Class<?>[0];
            }

        };
    }

}
