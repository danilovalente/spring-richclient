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
package org.springframework.binding.value.support;

import org.springframework.binding.value.IndexAdapter;

/**
 * Base implementation of an {@link IndexAdapter} which provides basic index
 * storing.
 */
public abstract class AbstractIndexAdapter extends AbstractValueModel implements IndexAdapter {

	private int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		if (hasChanged(this.index, index)) {
			int oldValue = this.index;
			this.index = index;
			firePropertyChange("index", oldValue, this.index);
		}
	}

}