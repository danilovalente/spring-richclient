/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.rules.constraint;

import java.lang.reflect.Method;

import org.springframework.rules.constraint.Constraint;
import org.springframework.rules.reporting.TypeResolvable;
import org.springframework.util.Assert;

/**
 * A adapter that can adapt a method on an object that accepts a single argument
 * and returns a boolean result a UnaryPredicate. For example, a DAO might have
 * the method <code>isUnique(String objectName)<code> that
 * tests whether not a name parameter is unique.  To adapt that method as a
 * UnaryPredicate, use this class.
 *
 * @author Keith Donald
 */
public class MethodInvokingConstraint implements Constraint, TypeResolvable {

	private Object targetObject;

	private Method testMethod;

	private String type;

	/**
	 * Creates a MethodInvokingConstraint for the provided target object - the
	 * constraint logic is encapsulated within the specified method name.
	 *
	 * Note: this constructor will attempt to guess the parameter type for the
	 * method as it accept a single unary argument and return a boolean result.
	 *
	 * @param targetObject
	 *            The target object
	 * @param methodName
	 *            The method name
	 */
	public MethodInvokingConstraint(Object targetObject, String methodName) {
		this(targetObject, methodName, null, null);
	}

	public MethodInvokingConstraint(Object targetObject, String methodName,
			String constraintType) {
		this(targetObject, methodName, null, constraintType);
	}

	private Class guessParameterType(Object object, String methodName) {
		Method[] methods = targetObject.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			if (m.getName().equals(methodName)) {
				Class[] types = m.getParameterTypes();
				if (types.length == 1) {
					return types[0];
				}
			}
		}
		throw new IllegalArgumentException(
				"No single argument, boolean method found with name '"
				+ methodName + "'");
	}

	public MethodInvokingConstraint(Object targetObject, String methodName,
			Class parameterType) {
		this(targetObject, methodName, parameterType, null);
	}

	public MethodInvokingConstraint(Object targetObject, String methodName,
			Class parameterType, String constraintType) {
		Assert.notNull(targetObject, "targetObject is required");
		this.targetObject = targetObject;
		if (parameterType == null) {
			parameterType = guessParameterType(targetObject, methodName);
		}
		setType(constraintType);
		try {
			this.testMethod = targetObject.getClass().getMethod(methodName,
					new Class[]{parameterType});
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		Class returnType = testMethod.getReturnType();
		Assert.isTrue(returnType == Boolean.class
				|| returnType == boolean.class, "Return type must be a boolean type");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean test(Object argument) {
		try {
			return ((Boolean) testMethod.invoke(targetObject,
					new Object[]{argument})).booleanValue();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}