/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.value;

import java.beans.PropertyChangeListener;

/**
 * Describes models with a generic access to a single value that allow
 * to observe value changes. The value can be accessed using the
 * <code>#getValue()</code>/<code>#setValue(Object)</code>/
 * <code>#setValueSilently(Object, PropertyChangeListener)</code>
 * methods. Observers can register instances of <code>PropertyChangeListener</code>
 * to be notified if the value changes.
 *
 * <p>The listeners registered with this ValueModel using #addValueChangeListener
 * will be invoked only with PropertyChangeEvents that have the name set to
 * "value".
 *
 * <p>AbstractValueModel minimizes the effort required to implement this interface.
 * It uses the PropertyChangeSupport to fire PropertyChangeEvents, and it adds
 * PropertyChangeListeners for the specific property name "value". This ensures
 * that the constraint mentioned above is met.
 *
 * @see org.springframework.binding.value.support.AbstractValueModel
 *
 * @author Karsten Lentzsch
 * @author Keith Donald
 * @author Oliver Hutchison
 */
public interface ValueModel {

    /**
     * The name of the bound property <em>value</em>.
     */
    String VALUE_PROPERTY = "value";

    /**
     * Returns this model's value. In case of a write-only value,
     * implementers may choose to either reject this operation or
     * or return <code>null</code> or any other appropriate value.
     *
     * @return this model's value
     */
    Object getValue();

    /**
     * Sets a new value and if the value has changed notifies any registered
     * value change listeners.
     *
     * @param newValue  the value to be set
     */
    void setValue(Object newValue);

    /**
     * Sets a new value and if the value has changed notifies all registered
     * value change listeners except for the specified listener to skip.
     *
     * @param newValue  the value to be set
     * @param listenerToSkip the <code>PropertyChangeListener</code> that should
     * not be notified of this change (may be <code>null</code>).
     */
    void setValueSilently(Object newValue, PropertyChangeListener listenerToSkip);

    /**
     * Registers the given <code>PropertyChangeListener</code> with this
     * ValueModel. The listener will be notified if the value has changed.
     * The PropertyChangeEvents delivered to the listener must have the name
     * set to "value". The latter ensures that all ValueModel implementers
     * behave like the AbstractValueModel subclasses.<p>
     *
     * To comply with the above specification implementers can use
     * the PropertyChangeSupport's #addPropertyChangeListener method
     * that accepts a property name, so that listeners will be invoked only
     * if that specific property has changed.
     *
     * @param listener the listener to be added
     *
     * @see org.springframework.binding.value.support.AbstractValueModel#addValueChangeListener(PropertyChangeListener)
     */
    void addValueChangeListener(PropertyChangeListener listener);

    /**
     * Deregisters the given <code>PropertyChangeListener</code> from this
     * ValueModel.
     *
     * @param listener the listener to be removed
     */
    void removeValueChangeListener(PropertyChangeListener listener);

}