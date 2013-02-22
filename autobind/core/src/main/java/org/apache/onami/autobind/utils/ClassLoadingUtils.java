/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.onami.autobind.utils;

import com.google.common.base.Optional;

public final class ClassLoadingUtils {

    private ClassLoadingUtils() {
        //Utility Class
    }

    /**
     * Load a class from the current class loader or the thread context class loader.
     * The default behavior is to load the class using the class loader of ClassLoadingUtils and falls back
     * to the thread context class loader if the class was not found.
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static Class loadClass(String className) throws ClassNotFoundException {
        Optional<ClassLoader> currentCloassLoader = Optional.of(ClassLoadingUtils.class.getClassLoader());
        Optional<ClassLoader> threadContextClassLoader = Optional.<ClassLoader>fromNullable(Thread.currentThread().getContextClassLoader());
        Optional<Class> clazz = tryLoad(className, currentCloassLoader).or(tryLoad(className, threadContextClassLoader));
        if (!clazz.isPresent()) {
            throw new ClassNotFoundException("Could not load class:" + className);
        } else {
            return clazz.get();
        }
    }

    /**
     * Try loading a {@link Class} with specified name from the specified {@link ClassLoader}.
     *
     * @param className   The name of the class.
     * @param classLoader The class loader.
     * @return Returns an Optional class.
     */
    private static Optional<Class> tryLoad(String className, Optional<ClassLoader> classLoader) {
        if (!classLoader.isPresent()) {
            return Optional.absent();
        } else {
            try {
                return Optional.<Class>of(classLoader.get().loadClass(className));
            } catch (ClassNotFoundException ex) {
                return Optional.absent();
            }
        }
    }
}
