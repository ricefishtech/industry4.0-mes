/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.view.internal;

import com.google.common.base.Preconditions;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class CustomMethodHolder {

    private static final String CLASS_ATTRIBUTE = "class";

    private static final String METHOD_ATTRIBUTE = "method";

    private static final String INVOKE_FAIL_MSG = "Failed to invoke custom method";

    private static final String BEAN_NOT_FOUND_MSG = "Failed to find bean for class '%s', "
            + "in application context. Please make sure that there is no typo, class have @Service or @Component "
            + "annotation and its package is registered in the Spring component-scan feature "
            + "(check plugin's src/main/resources/root-context.xml)";

    private static final String CLASS_NOT_FOUND_MSG = "Failed to find class '%s', please make sure that there is no typo";

    private static final String WRONG_VISIBILITY_MSG = "Given method '%s.%s' has invalid visibility, must be public";

    private static final String WRONG_RETURN_TYPE_MSG = "Given method '%s.%s' has invalid return type, must be %s";

    private static final String NO_SUCH_METHOD_MSG = "Failed to find method '%s.%s', "
            + "please make sure that there is no typo, method returns %s and parameters' types are valid (%s)";

    private static final String SECURITY_EXCEPTION_MSG = "Failed to access method '%s.%s'";

    private final Object bean;

    private final Method method;

    private final Class<?> expectedReturnType;

    private final Class<?>[] expectedParameterTypes;

    /**
     * @param holderNode
     *            custom method holder node
     * @param parser
     *            view definition parser
     * @param applicationContext
     *            spring container's application context
     * @param expectedReturnType
     *            return type
     * @param expectedParameterTypes
     *            parameter types
     */
    public CustomMethodHolder(final Node holderNode, final ViewDefinitionParser parser,
            final ApplicationContext applicationContext, final Class<?> expectedReturnType,
            final Class<?>[] expectedParameterTypes) {
        this(parser.getStringAttribute(holderNode, CLASS_ATTRIBUTE), parser.getStringAttribute(holderNode, METHOD_ATTRIBUTE),
                applicationContext, expectedReturnType, expectedParameterTypes);
    }

    /**
     * @param className
     *            binary name (http://docs.oracle.com/javase/6/docs/api/java/lang/ClassLoader.html#name) of class containing
     *            resolver method
     * @param methodName
     *            method name
     * @param applicationContext
     * @param expectedReturnType
     *            return type
     * @param expectedParameterTypes
     *            parameter types
     */
    private CustomMethodHolder(final String className, final String methodName, final ApplicationContext applicationContext,
            final Class<?> expectedReturnType, final Class<?>[] expectedParameterTypes) {
        Preconditions.checkArgument(!StringUtils.isBlank(className), "class name attribute is not specified!");
        Preconditions.checkArgument(!StringUtils.isBlank(methodName), "method name attribute is not specified!");
        Preconditions.checkArgument(expectedReturnType != null, "expected return type is not specified!");
        Preconditions.checkArgument(expectedParameterTypes != null, "expected parameter types are not specified!");

        this.expectedReturnType = expectedReturnType;
        this.expectedParameterTypes = Arrays.copyOf(expectedParameterTypes, expectedParameterTypes.length);

        final Class<?> clazz = getCustomMethodClass(className);

        bean = getCustomMethodBean(clazz, applicationContext);
        method = getMethod(clazz, methodName);

        checkMethodSignature();
    }

    public Object invoke(final Object... args) {
        try {
            return method.invoke(bean, args);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(INVOKE_FAIL_MSG, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(INVOKE_FAIL_MSG, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(INVOKE_FAIL_MSG, e);
        }
    }

    private Class<?> getCustomMethodClass(final String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            final String msg = String.format(CLASS_NOT_FOUND_MSG, className);
            throw new IllegalStateException(msg, e);
        }
    }

    private Object getCustomMethodBean(final Class<?> clazz, final ApplicationContext applicationContext) {
        Object hookBean = applicationContext.getBean(clazz);
        if (hookBean == null) {
            final String msg = String.format(BEAN_NOT_FOUND_MSG, clazz.getCanonicalName());
            throw new IllegalStateException(msg);
        }
        return hookBean;
    }

    private void checkMethodSignature() {
        if (!Modifier.isPublic(method.getModifiers())) {
            final String msg = String.format(WRONG_VISIBILITY_MSG, method.getDeclaringClass().getCanonicalName(),
                    method.getName());
            throw new IllegalStateException(msg);
        }
        if (!expectedReturnType.equals(method.getReturnType())) {
            final String msg = String.format(WRONG_RETURN_TYPE_MSG, method.getDeclaringClass().getCanonicalName(),
                    method.getName(), expectedReturnType);
            throw new IllegalStateException(msg);
        }
    }

    private Method getMethod(final Class<?> clazz, final String methodName) {
        try {
            return clazz.getMethod(methodName, expectedParameterTypes);
        } catch (SecurityException e) {
            final String msg = String.format(SECURITY_EXCEPTION_MSG, clazz.getCanonicalName(), methodName);
            throw new IllegalStateException(msg, e);
        } catch (NoSuchMethodException e) {
            final String msg = String.format(NO_SUCH_METHOD_MSG, clazz.getCanonicalName(), methodName, expectedReturnType,
                    Arrays.toString(expectedParameterTypes));
            throw new IllegalStateException(msg, e);
        }
    }

    public static boolean methodExists(final String className, final String methodName,
            final ApplicationContext applicationContext, final Class<?>[] expectedParameterTypes) {
        Preconditions.checkArgument(!StringUtils.isBlank(className), "class name attribute is not specified!");
        Preconditions.checkArgument(!StringUtils.isBlank(methodName), "method name attribute is not specified!");
        Preconditions.checkArgument(expectedParameterTypes != null, "expected parameter types are not specified!");

        try {
            final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);

            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && Arrays.deepEquals(method.getParameterTypes(), expectedParameterTypes)) {
                    return true;
                }
            }
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
