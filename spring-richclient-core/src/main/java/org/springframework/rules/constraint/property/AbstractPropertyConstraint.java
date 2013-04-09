/*
 * The Spring Framework is published under the terms of the Apache Software
 * License.
 */
package org.springframework.rules.constraint.property;

import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.util.Assert;

/**
 * Convenience superclass for bean property expressions.
 *
 * @author Keith Donald
 */
public abstract class AbstractPropertyConstraint implements PropertyConstraint {

	private String propertyName;

	protected AbstractPropertyConstraint() {
	}

	protected AbstractPropertyConstraint(String propertyName) {
		setPropertyName(propertyName);
	}

	public String getPropertyName() {
		return propertyName;
	}

	public boolean isDependentOn(String propertyName) {
		return getPropertyName().equals(propertyName);
	}
	
	public boolean isCompoundRule() {
		return false;
	}

	protected void setPropertyName(String propertyName) {
		Assert.notNull(propertyName, "The propertyName to constrain is required");
		this.propertyName = propertyName;
	}

	public boolean test(Object o) {
		if (o instanceof PropertyAccessStrategy)
			return test((PropertyAccessStrategy)o);

        return test(new BeanPropertyAccessStrategy(o));
	}

	protected abstract boolean test(PropertyAccessStrategy domainObjectAccessStrategy);

	public String toString() {
		return getPropertyName();
	}

}