package org.apache.onami.autobind.integrations.test.commons.configuration.url;

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

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.configuration.plist.PropertyListConfiguration;
import org.apache.onami.autobind.annotations.Bind;
import org.apache.onami.autobind.configuration.Configuration;
import org.apache.onami.autobind.configuration.PathConfig;
import org.apache.onami.autobind.configuration.PathConfig.PathType;
import org.apache.onami.autobind.configuration.StartupModule;
import org.apache.onami.autobind.integrations.commons.configuration.CommonsConfigurationFeature;
import org.apache.onami.autobind.scanner.PackageFilter;
import org.apache.onami.autobind.scanner.asm.ASMClasspathScanner;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class URLConfigTests {
	@Test
	public void createDynamicModule() {
		StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(URLConfigTests.class));
		startup.addFeature(CommonsConfigurationFeature.class);

		Injector injector = Guice.createInjector(startup);
		assertNotNull(injector);
	}

	@Test
	public void createPListConfiguration() {
		StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(URLConfigTests.class));
		startup.addFeature(CommonsConfigurationFeature.class);

		Injector injector = Guice.createInjector(startup);
		assertNotNull(injector);

		TestInterface instance = injector.getInstance(TestInterface.class);
		Assert.assertTrue("sayHello() - yeahhh out of the package :)".equals(instance.sayHello()));
	}

	@Configuration(name = @Named("config"), location = @PathConfig(value = "http://devsurf.de/guice/configuration.plist", type = PathType.URL), to = PropertyListConfiguration.class)
	public interface TestConfiguration {
	}

	public static interface TestInterface {
		String sayHello();
	}

	@Bind
	public static class TestImplementations implements TestInterface {
		@Inject
		@Named("config")
		private org.apache.commons.configuration.Configuration config;

		@Override
		public String sayHello() {
			return "sayHello() - " + config.getString("de.devsurf.configuration.message");
		}
	}
}
