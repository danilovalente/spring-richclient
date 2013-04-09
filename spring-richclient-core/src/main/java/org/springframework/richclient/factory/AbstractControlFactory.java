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
package org.springframework.richclient.factory;

import javax.swing.JComponent;

import org.springframework.richclient.application.support.ApplicationServicesAccessor;

/**
 * A skeleton implementation of the {@link ControlFactory} interface that only
 * creates it's control when requested.
 *
 * <p>
 * The factory may operate in singleton mode, which is the default. In this
 * case, the control will be created the first time it is requested and the same
 * instance will be returned for each subsequent request. When operating in
 * non-singleton mode, a new control instance is created each time it is
 * requested.
 * </p>
 *
 * @author Keith Donald
 */
public abstract class AbstractControlFactory extends ApplicationServicesAccessor implements ControlFactory {

	private boolean singleton = true;

	private JComponent control;

	/**
	 * Creates a new uninitialized {@code AbstractControlFactory}.
	 */
	protected AbstractControlFactory() {
		// do nothing
	}

	/**
	 * Returns true (the default) if this factory is to create a single instance
	 * of its control.
	 *
	 * @return <code>true</code> if this factory returns a singleton instance
	 * of its control.
	 */
	protected final boolean isSingleton() {
		return singleton;
	}

	/**
	 * Sets the flag that determines if this factory is to create a single
	 * instance of its control. By default, this flag is true.
	 *
	 * @param singleton The singleton flag.
	 */
	protected final void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	/**
	 * Returns an instance of the control that this factory produces.
	 *
	 * <p>
	 * This implementation is a template method, calling the abstract
	 * {@link #createControl()} method if operating in non-singleton mode or if
	 * the control has not yet been created when operating in singleton mode.
	 * </p>
	 *
	 */
	public final JComponent getControl() {
		if (isSingleton()) {
			if (this.control == null) {
				this.control = createControl();
			}
			return this.control;
		}

		return createControl();
	}

	/**
	 * Returns true if the control for this factory has been created. If this
	 * factory is set to non-singleton mode, this method will always return
	 * false even if an instance of the control has previously been created.
	 *
	 * @return <code>true</code> if operating in singleton mode and an instance of the
	 * control has already been created, false otherwise.
	 */
	public final boolean isControlCreated() {
		if (isSingleton()) {
			return this.control != null;
		}

		return false;
	}

	/**
	 * Creates an instance of the control produced by this factory if operating
	 * in singleton mode and the control instance has not already been created.
	 */
	protected void createControlIfNecessary() {
		if (isSingleton() && this.control == null) {
			getControl();
		}
	}

	/**
	 * Subclasses must override this method to create a new instance of the
	 * control that this factory produces.
	 *
	 * @return The newly created control, never null.
	 */
	protected abstract JComponent createControl();

}