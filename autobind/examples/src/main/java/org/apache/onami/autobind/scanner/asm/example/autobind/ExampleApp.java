package org.apache.onami.autobind.scanner.asm.example.autobind;

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

import java.io.IOException;

import org.apache.onami.autobind.annotations.Bind;
import org.apache.onami.autobind.annotations.GuiceModule;
import org.apache.onami.autobind.configuration.StartupModule;
import org.apache.onami.autobind.example.starter.ExampleApplication;
import org.apache.onami.autobind.scanner.ClasspathScanner;
import org.apache.onami.autobind.scanner.PackageFilter;
import org.apache.onami.autobind.scanner.ScannerModule;
import org.apache.onami.autobind.scanner.asm.ASMClasspathScanner;

import com.google.inject.Guice;
import com.google.inject.Injector;


/**
 * Example Application, which creates a new Injector with the help of the
 * provided {@link StartupModule}. It passes the {@link ASMClasspathScanner}
 * class for the {@link ClasspathScanner} and the packages (de.devsurf) which
 * should be scanned. The {@link StartupModule} binds these parameter, so we are
 * able to create and inject our {@link ScannerModule}. This Module uses the
 * {@link ClasspathScanner} to explore the Classpath and scans for Annotations.
 * 
 * All recognized Classes annotated with {@link GuiceModule} are installed in
 * the child injector and with {@link Bind} are automatically bound.
 * 
 * @author Daniel Manzke
 * 
 */
@Bind(multiple = true)
public class ExampleApp implements ExampleApplication {
	@Override
	public void run() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(ExampleApp.class)));

		System.out.println(injector.getInstance(Example.class).sayHello());
	}

	public static void main(String[] args) throws IOException {
		new ExampleApp().run();
	}
}
