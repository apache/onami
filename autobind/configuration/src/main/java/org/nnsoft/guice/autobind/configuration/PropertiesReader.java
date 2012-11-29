package org.nnsoft.guice.autobind.configuration;

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

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.onami.configuration.configuration.PropertiesURLReader;

public class PropertiesReader
    implements ConfigurationReader
{

    private final PropertiesURLReader reader;

    public PropertiesReader( URL url )
    {
        reader = new PropertiesURLReader( url );
    }

    public Properties readNative()
        throws Exception
    {
        Properties properties = new Properties();

        return  reader.readConfiguration();
    }

    @Override
    public Iterator<Entry<String, String>> readConfiguration()
        throws Exception
    {
        Properties properties = reader.readConfiguration();
        Map<String, String> result = new HashMap<String, String>();
        properties.putAll(result);
        return result.entrySet().iterator();
    }

}
