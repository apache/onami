package org.nnsoft.guice.gache;

/*
 *  Copyright 2012 The 99 Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.cache.annotation.CacheInvocationParameter;

final class DefaultCacheInvocationParameter
    implements CacheInvocationParameter
{

    private final Class<?> rawType;

    private final Object value;

    private final Set<Annotation> annotations;

    private final int parameterPosition;

    public DefaultCacheInvocationParameter( Class<?> rawType,
                                            Object value,
                                            Set<Annotation> annotations,
                                            int parameterPosition )
    {
        this.rawType = rawType;
        this.value = value;
        this.annotations = annotations;
        this.parameterPosition = parameterPosition;
    }

    public Class<?> getRawType()
    {
        return rawType;
    }

    public Object getValue()
    {
        return value;
    }

    public Set<Annotation> getAnnotations()
    {
        return annotations;
    }

    public int getParameterPosition()
    {
        return parameterPosition;
    }

}
