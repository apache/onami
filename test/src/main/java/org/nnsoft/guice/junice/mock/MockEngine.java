package org.nnsoft.guice.junice.mock;

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

import org.nnsoft.guice.junice.annotation.MockObjType;
import org.nnsoft.guice.junice.mock.framework.EasyMockFramework;
import org.nnsoft.guice.junice.mock.framework.MockitoFramework;

/**
 * Interface to specify mock framework class engine.
 *
 * @see EasyMockFramework
 * @see MockitoFramework
 */
public interface MockEngine
{

    /**
     * Reset the mock objects
     *
     * @param objects to reset.
     */
    void resetMock( Object... objects );

    /**
     * Create a typed mock
     *
     * @param <T> Class to mock
     * @param cls Class to mock
     * @return the mock object
     */
    <T> T createMock( Class<T> cls, MockObjType type );

}
