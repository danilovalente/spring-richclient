/*
 * Copyright 2002-2007 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;

import org.springframework.binding.value.IndexAdapter;
import org.springframework.util.ObjectUtils;

/**
 * @author Keith Donald
 */
public class ListListModel extends AbstractListModel implements ObservableList {
    private List items;

    private Comparator sorter;

    private IndexAdapter indexAdapter;

    public ListListModel() {
        this(null);
    }

    public ListListModel(List items) {
        this(items, null);
    }

    public ListListModel(List items, Comparator sorter) {
        if (items != null) {
            this.items = new ArrayList(items);
        }
        else {
            this.items = new ArrayList();
        }
        setComparator(sorter);
        sort();
    }

    public void setComparator(Comparator sorter) {
        this.sorter = sorter;
    }

    public void sort() {
        if (sorter != null) {
            Collections.sort(items, sorter);
            fireContentsChanged(items, -1, -1);
        }
    }

    protected List getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }

    public Object getElementAt(int index) {
        return items.get(index);
    }

    public void add(int index, Object o) {
        items.add(index, o);
        fireIntervalAdded(this, index, index);
    }

    public IndexAdapter getIndexAdapter(int index) {
        if (indexAdapter == null) {
            this.indexAdapter = new ThisIndexAdapter();
        }
        indexAdapter.setIndex(index);
        return indexAdapter;
    }

    private class ThisIndexAdapter extends AbstractIndexAdapter {
        private static final int NULL_INDEX = -1;

        public Object getValue() {
            if (getIndex() == NULL_INDEX) {
                return null;
            }
            return get(getIndex());
        }

        public void setValue(Object value) {
            if (getIndex() == NULL_INDEX) {
                throw new IllegalStateException("Attempt to set value at null index; operation not allowed");
            }
            Object oldValue = items.set(getIndex(), value);
            if (hasValueChanged(oldValue, value)) {
                fireContentsChanged(getIndex());
                fireValueChange(oldValue, value);
            }
        }

        public void fireIndexedObjectChanged() {
            fireContentsChanged(getIndex());
        }
    }

    public boolean add(Object o) {
        boolean result = items.add(o);
        if (result) {
            int end = items.size() - 1;
            fireIntervalAdded(this, end, end);
        }
        return result;

    }

    public boolean addAll(Collection c) {
        int firstIndex = items.size();
        boolean result = items.addAll(c);
        if (result) {
            int lastIndex = items.size() - 1;
            fireIntervalAdded(this, firstIndex, lastIndex);
        }
        return result;
    }

    public boolean addAll(int index, Collection c) {
        boolean result = items.addAll(index, c);
        if (result) {
            fireIntervalAdded(this, index, index + c.size() - 1);
        }
        return result;
    }

    public void clear() {
        if (items.size() > 0) {
            int firstIndex = 0;
            int lastIndex = items.size() - 1;
            items.clear();
            fireIntervalRemoved(this, firstIndex, lastIndex);
        }
    }

    public boolean contains(Object o) {
        return items.contains(o);
    }

    public boolean containsAll(Collection c) {
        return items.containsAll(c);
    }

    public Object get(int index) {
        return items.get(index);
    }

    public int indexOf(Object o) {
        return items.indexOf(o);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Iterator iterator() {
        return items.iterator();
    }

    public int lastIndexOf(Object o) {
        return items.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return items.listIterator();
    }

    public ListIterator listIterator(int index) {
        return items.listIterator(index);
    }

    public Object remove(int index) {
        Object o = items.remove(index);
        fireIntervalRemoved(this, index, index);
        return o;
    }

    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

    public boolean removeAll(Collection c) {
        boolean b = items.removeAll(c);
        if (b) {
            fireContentsChanged(this, -1, -1);
        }
        return b;
    }

    public boolean retainAll(Collection c) {
        boolean b = items.retainAll(c);
		if (b) {
			fireContentsChanged(this, -1, -1);
		}
		return b;
    }

    /**
	 * Set the value of a list element at the specified index.
	 * @param index of element to set
	 * @param element New element value
	 * @return old element value
	 */
    public Object set(int index, Object element) {
        Object oldObject = items.set(index, element);
        if (hasChanged(oldObject, element)) {
            fireContentsChanged(index);
        }
        return oldObject;
    }

    /**
     * Determine if the provided objects are different (have changed).  This method essentially
     * embodies the "change semantics" for elements in this list.  If list elements have an
     * altered "equals" implementation, it may not be sufficient to detect changes in a pair of
     * objects.  In that case, you can override this method and implement whatever change detection
     * mechanism is appropriate.
     * 
     * @param oldElement Old (original) value to compare
     * @param newElement New (updated) value to compare
     * @return true if objects are different (have changed)
     */
    protected boolean hasChanged(Object oldElement, Object newElement) {
        return !ObjectUtils.nullSafeEquals( oldElement, newElement );
    }

    public int size() {
        return items.size();
    }

    public List subList(int fromIndex, int toIndex) {
        return items.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return items.toArray();
    }

    public Object[] toArray(Object[] a) {
        return items.toArray(a);
    }

    /**
     * Notifies the list model that one of the list elements has changed.
     */
    protected void fireContentsChanged(int index) {
        fireContentsChanged(index, index);
    }

    /**
     * Notifies the list model that one or more of the list elements have
     * changed. The changed elements are specified by the range startIndex to
     * endIndex inclusive.
     */
    protected void fireContentsChanged(int startIndex, int endIndex) {
        fireContentsChanged(this, startIndex, endIndex);
    }

    /**
     * Replace this list model's items with the contents of the provided
     * collection.
     * 
     * @param collection
     *            The collection to replace with
     */
    public boolean replaceWith(Collection collection) {
        boolean changed = false;
        if (items.size() > 0) {
            items.clear();
            changed = true;
        }
        if (items.addAll(0, collection) && !changed) {
            changed = true;
        }
        if (changed) {
            fireContentsChanged(-1, -1);
        }
        return changed;
    }
}