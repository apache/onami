package org.nnsoft.guice.junice;

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

import org.nnsoft.guice.junice.annotation.MockFramework;
import org.nnsoft.guice.junice.annotation.MockType;
import org.nnsoft.guice.junice.mock.MockEngine;
import org.nnsoft.guice.junice.mock.framework.EasyMockFramework;
import org.nnsoft.guice.junice.mock.framework.MockitoFramework;

/**
 * Factory class to create the mock framework.
 *
 * @see MockFramework
 */
final class MockEngineFactory
{

    /**
     * Mock factory constructor. <br>
     * Supported framewors: <li> {@link MockType}.EASY_MOCK <li> {@link MockType}.MOCKITO <br>
     *
     * @see MockType
     * @param type of mock framework to create.
     * @return An instance of mock framework.
     */
    public static MockEngine getMockEngine( MockType type )
    {
        switch ( type )
        {
            case EASY_MOCK:
                return new EasyMockFramework();

            case MOCKITO:
                return new MockitoFramework();

            default:
                throw new IllegalArgumentException( "Unrecognized MockeType '" + type.name() + "'" );
        }
    }

}
