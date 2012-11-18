package org.nnsoft.guice.junice.mock.framework;

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

import org.easymock.classextension.EasyMock;
import org.nnsoft.guice.junice.annotation.MockObjType;
import org.nnsoft.guice.junice.mock.MockEngine;

/**
 * Specifies the Easy-Mock Framework.
 *
 * @see MockEngine
 */
public class EasyMockFramework
    implements MockEngine
{

    /**
     * {@inheritDoc}
     */
    public void resetMock( Object... objects )
    {
        EasyMock.reset( objects );
    }

    /**
     * {@inheritDoc}
     */
    public <T> T createMock( Class<T> cls, MockObjType type )
    {
        switch ( type )
        {
            case EASY_MOCK_NICE:
                return EasyMock.createNiceMock( cls );

            case EASY_MOCK_STRICT:
                return EasyMock.createStrictMock( cls );

            case EASY_MOCK_NORMAL:
            case DEFAULT:
                return EasyMock.createMock( cls );

            default:
                throw new IllegalArgumentException( "Unsupported mock type '" + type + "' for Easy-Mock Framework." );
        }
    }

}
