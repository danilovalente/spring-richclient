/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.value.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.binding.value.DerivedValueModel;
import org.springframework.binding.value.ValueModel;

/**
 * Abstract base class for value models that derive their value from one or more
 * "source" value model. Provides a hook to notify when any of the "source"
 * value models change.
 *
 * @author Oliver Hutchison
 */
public abstract class AbstractDerivedValueModel extends AbstractValueModel implements DerivedValueModel {

	private final ValueModel[] sourceValueModels;

	private final PropertyChangeListener sourceChangeHandler;

	/**
	 * Create a derivedValueModel based on the given sourceValueModels. When any
	 * of these valueModels has their value changed, the derivedValueModel will
	 * update its value.
	 *
	 * @param sourceValueModels an <code>Array</code> of valueModels that
	 * influence the derived value.
	 */
	protected AbstractDerivedValueModel(ValueModel[] sourceValueModels) {
		this.sourceValueModels = sourceValueModels;
		this.sourceChangeHandler = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				sourceValuesChanged();
			}
		};
		for (int i = 0; i < sourceValueModels.length; i++) {
			sourceValueModels[i].addValueChangeListener(sourceChangeHandler);
		}
	}

	public ValueModel[] getSourceValueModels() {
		return sourceValueModels;
	}

	/**
	 * Convenience method to extract values from all sourceValueModels that
	 * influence the derived value.
	 *
	 * @return an <code>Array</code> containing the source values in the same
	 * order as the source valueModels were defined.
	 */
	protected Object[] getSourceValues() {
		Object[] values = new Object[sourceValueModels.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = sourceValueModels[i].getValue();
		}
		return values;
	}

	/**
	 * Derive the value from the source values and fire a valueChangeEvent to
	 * notify listeners.
	 */
	protected abstract void sourceValuesChanged();

	/**
	 * A derived valueModel is always readOnly.
	 *
	 * @see #setValue(Object)
	 */
	public boolean isReadOnly() {
		return true;
	}

	/**
	 * A {@link DerivedValueModel}'s value is based on other valueModels, it
	 * cannot be set. Will throw an {@link UnsupportedOperationException} when
	 * used.
	 */
	public void setValue(Object newValue) {
		throw new UnsupportedOperationException("This value model is read only");
	}
}