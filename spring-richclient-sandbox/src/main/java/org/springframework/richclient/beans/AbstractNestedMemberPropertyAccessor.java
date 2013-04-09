/*
 * Copyright 2007 the original author or authors.
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
package org.springframework.richclient.beans;

import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.CachingMapDecorator;

/**
 * This implementation extends {@link AbstractMemberPropertyAccessor} with the
 * functionality of nested property handling.
 *
 * @author Arne Limburg
 *
 */
public abstract class AbstractNestedMemberPropertyAccessor extends AbstractMemberPropertyAccessor {

	private AbstractNestedMemberPropertyAccessor parentPropertyAccessor;

	private String basePropertyName;

	private final ChildPropertyAccessorCache childPropertyAccessors = new ChildPropertyAccessorCache();

	private final boolean strictNullHandlingEnabled;

	protected AbstractNestedMemberPropertyAccessor(Class targetClass, boolean fieldAccessEnabled,
			boolean strictNullHandlingEnabled) {
		super(targetClass, fieldAccessEnabled);
		this.strictNullHandlingEnabled = strictNullHandlingEnabled;
	}

	public AbstractNestedMemberPropertyAccessor(AbstractNestedMemberPropertyAccessor parent, String baseProperty) {
		super(parent.getPropertyType(baseProperty), parent.isFieldAccessEnabled());
		parentPropertyAccessor = parent;
		basePropertyName = baseProperty;
		strictNullHandlingEnabled = parent.strictNullHandlingEnabled;
	}

	public boolean isStrictNullHandlingEnabled() {
		return strictNullHandlingEnabled;
	}

	protected AbstractNestedMemberPropertyAccessor getParentPropertyAccessor() {
		return parentPropertyAccessor;
	}

	protected String getBasePropertyName() {
		return basePropertyName;
	}

	public Object getTarget() {
		if (parentPropertyAccessor != null && basePropertyName != null) {
			return parentPropertyAccessor.getPropertyValue(basePropertyName);
		}
		else {
			return null;
		}
	}

	public Class getTargetClass() {
		if (parentPropertyAccessor != null) {
			return parentPropertyAccessor.getPropertyType(basePropertyName);
		}
		else {
			return super.getTargetClass();
		}
	}

	public boolean isReadableProperty(String propertyPath) {
		if (PropertyAccessorUtils.isNestedProperty(propertyPath)) {
			String baseProperty = getBasePropertyName(propertyPath);
			String childPropertyPath = getChildPropertyPath(propertyPath);
			if (!super.isReadableProperty(baseProperty)) {
				return false;
			}
			else {
				return ((PropertyAccessor) childPropertyAccessors.get(baseProperty))
						.isReadableProperty(childPropertyPath);
			}
		}
		else {
			return super.isReadableProperty(propertyPath);
		}
	}

	public boolean isWritableProperty(String propertyPath) {
		if (PropertyAccessorUtils.isNestedProperty(propertyPath)) {
			String baseProperty = getBasePropertyName(propertyPath);
			String childPropertyPath = getChildPropertyPath(propertyPath);
			return super.isReadableProperty(baseProperty)
					&& ((PropertyAccessor) childPropertyAccessors.get(baseProperty))
							.isWritableProperty(childPropertyPath);
		}
		else {
			return super.isWritableProperty(propertyPath);
		}
	}

	public Class getPropertyType(String propertyPath) {
		if (PropertyAccessorUtils.isNestedProperty(propertyPath)) {
			String baseProperty = getBasePropertyName(propertyPath);
			String childPropertyPath = getChildPropertyPath(propertyPath);
			return ((PropertyAccessor) childPropertyAccessors.get(baseProperty)).getPropertyType(childPropertyPath);
		}
		else {
			return super.getPropertyType(propertyPath);
		}
	}

	public Object getPropertyValue(String propertyPath) {
		if (PropertyAccessorUtils.isNestedProperty(propertyPath)) {
			String baseProperty = getBasePropertyName(propertyPath);
			String childPropertyPath = getChildPropertyPath(propertyPath);
			return ((PropertyAccessor) childPropertyAccessors.get(baseProperty)).getPropertyValue(childPropertyPath);
		}
		else if (isStrictNullHandlingEnabled() && getTarget() == null) {
			throw new NullValueInNestedPathException(getTargetClass(), propertyPath);
		}
		else {
			return super.getPropertyValue(propertyPath);
		}
	}

	public void setPropertyValue(String propertyPath, Object value) {
		if (PropertyAccessorUtils.isNestedProperty(propertyPath)) {
			String baseProperty = getBasePropertyName(propertyPath);
			String childPropertyPath = getChildPropertyPath(propertyPath);
			((PropertyAccessor) childPropertyAccessors.get(baseProperty)).setPropertyValue(childPropertyPath, value);
		}
		else if (isStrictNullHandlingEnabled() && getTarget() == null) {
			throw new NullValueInNestedPathException(getTargetClass(), propertyPath);
		}
		else {
			super.setPropertyValue(propertyPath, value);
		}
	}

	protected String getBasePropertyName(String propertyPath) {
		int index = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
		return index == -1 ? propertyPath : propertyPath.substring(0, index);
	}

	protected String getChildPropertyPath(String propertyPath) {
		int index = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
		if (index == -1) {
			return "";
		}
		return propertyPath.substring(index + 1);
	}

	protected PropertyAccessor getChildPropertyAccessor(String propertyName) {
		return (PropertyAccessor) childPropertyAccessors.get(propertyName);
	}

	protected abstract AbstractNestedMemberPropertyAccessor createChildPropertyAccessor(String propertyName);

	protected void clearChildPropertyAccessorCache() {
		childPropertyAccessors.clear();
	}

	private class ChildPropertyAccessorCache extends CachingMapDecorator {

		protected Object create(Object propertyName) {
			return createChildPropertyAccessor((String) propertyName);
		}
	}
}