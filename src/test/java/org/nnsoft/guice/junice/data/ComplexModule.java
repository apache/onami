package org.nnsoft.guice.junice.data;

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

import com.google.inject.AbstractModule;

public class ComplexModule
    extends AbstractModule
{

    private String name;

    public ComplexModule( String name )
    {
        this.name = name;
    }

    @Override
    protected void configure()
    {
        bind( WhoIm.class ).toInstance( new WhoIm( name ) );
    }

}
