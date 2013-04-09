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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.rules.constraint.Constraint;

public abstract class AbstractConstraint extends ConstraintsAccessor implements Constraint, Serializable {

	public boolean allTrue(Collection collection) {
		return allTrue(collection, this);
	}

	public boolean allTrue(Iterator it) {
		return allTrue(it, this);
	}

	public boolean anyTrue(Collection collection) {
		return anyTrue(collection, this);
	}

	public boolean anyTrue(Iterator it) {
		return anyTrue(it, this);
	}

	public Collection findAll(Collection collection) {
		return findAll(collection, this);
	}

	public Object findAll(Iterator it) {
		return findAll(it, this);
	}

	public Object findFirst(Collection collection) {
		return findFirst(collection, this);
	}

	public Object findFirst(Iterator it) {
		return findFirst(it, this);
	}
}