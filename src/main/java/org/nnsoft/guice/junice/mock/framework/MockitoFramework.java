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

import static com.google.common.base.Preconditions.checkArgument;
import static org.nnsoft.guice.junice.annotation.MockObjType.DEFAULT;

import org.mockito.Mockito;
import org.nnsoft.guice.junice.annotation.MockObjType;
import org.nnsoft.guice.junice.mock.MockEngine;

/**
 * Specifies the Mockito Framework.
 *
 * @see MockEngine
 */
public class MockitoFramework
    implements MockEngine
{

    /**
     * {@inheritDoc}
     */
    public void resetMock( Object... objects )
    {
        Mockito.reset( objects );
    }

    /**
     * {@inheritDoc}
     */
    public <T> T createMock( Class<T> cls, MockObjType type )
    {
        checkArgument( DEFAULT == type, "Unsupported mock type '%s' for Mockito Framework.", type );
        return Mockito.mock( cls );
    }

}
