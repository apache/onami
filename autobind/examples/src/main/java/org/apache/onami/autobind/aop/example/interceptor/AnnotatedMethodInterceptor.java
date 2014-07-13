package org.apache.onami.autobind.aop.example.interceptor;

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

import java.lang.reflect.Method;

import javax.interceptor.Interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.onami.autobind.aop.ClassMatcher;
import org.apache.onami.autobind.aop.Intercept;
import org.apache.onami.autobind.aop.Invoke;
import org.apache.onami.autobind.aop.MethodMatcher;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;


@Interceptor
public class AnnotatedMethodInterceptor {

	@Invoke
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println(AnnotatedMethodInterceptor.class.getSimpleName()
				+ " - Trying to invoke: " + invocation.getMethod().getName());
		return invocation.proceed();
	}

	@ClassMatcher
	public Matcher<? super Class<?>> getClassMatcher() {
		return Matchers.any();
	}

	@MethodMatcher
	public Matcher<? super Method> getMethodMatcher() {
		return Matchers.annotatedWith(Intercept.class);
	}

}
