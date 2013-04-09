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
package org.springframework.binding.support;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;

/**
 * An implementation of <code>MutablePropertyAccessStrategy</code> that provides access 
 * to the properties of a JavaBean.
 * 
 * <p>As this class delegates to a <code>BeanWrapper</code> for property access, there is 
 * full support for <b>nested properties</b>, enabling the setting/getting
 * of properties on subproperties to an unlimited depth.
 *   
 * @author Oliver Hutchison
 * @author Arne Limburg
 * @see org.springframework.beans.BeanWrapper
 */
public class BeanPropertyAccessStrategy extends AbstractPropertyAccessStrategy {

    private final BeanWrapper beanWrapper;

    /**
     * Creates a new instance of BeanPropertyAccessStrategy that will provide access
     * to the properties of the provided JavaBean.
     * 
     * @param bean JavaBean to be accessed through this class. 
     */
    public BeanPropertyAccessStrategy(Object bean) {
        this(new ValueHolder(bean));
    }

    /**
     * Creates a new instance of BeanPropertyAccessStrategy that will provide access
     * to the JavaBean contained by the provided value model.  
     * 
     * @param domainObjectHolder value model that holds the JavaBean to 
     * be accessed through this class
     */
    public BeanPropertyAccessStrategy(final ValueModel domainObjectHolder) {
    	super(domainObjectHolder);
        this.beanWrapper = new BeanWrapperImpl(false);
        this.beanWrapper.setWrappedInstance(domainObjectHolder.getValue());
    }

    /**
     * Creates a child instance of BeanPropertyAccessStrategy that will delegate to its 
     * parent for property access.
     * 
     * @param parent BeanPropertyAccessStrategy which will be used to provide property access
     * @param basePropertyPath property path that will as a base when accessing the parent   
     * BeanPropertyAccessStrategy
     */
    protected BeanPropertyAccessStrategy(BeanPropertyAccessStrategy parent, String basePropertyPath) {
    	super(parent, basePropertyPath);
        this.beanWrapper = parent.beanWrapper;
    }

    /**
     * Provides <code>BeanWrapper</code> access to subclasses. 
     * @return Spring <code>BeanWrapper</code> used to access the bean.
     */
    protected BeanWrapper getBeanWrapper() {
        return beanWrapper;
    }
    
    /**
     * Provides <code>BeanWrapper</code> access to subclasses. 
     * @return Spring <code>BeanWrapper</code> used to access the bean.
     */
    protected PropertyAccessor getPropertyAccessor() {
    	return beanWrapper;
    }
  
    public MutablePropertyAccessStrategy getPropertyAccessStrategyForPath(String propertyPath) throws BeansException {
        return new BeanPropertyAccessStrategy(this, getFullPropertyPath(propertyPath));
    }

    public MutablePropertyAccessStrategy newPropertyAccessStrategy(ValueModel domainObjectHolder) {
        return new BeanPropertyAccessStrategy(domainObjectHolder);
    }
    
    protected void domainObjectChanged() {
    	beanWrapper.setWrappedInstance(getDomainObject());
    }
}