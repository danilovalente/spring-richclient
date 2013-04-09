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
package org.springframework.richclient.form.binding.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.BinderSelectionStrategy;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.util.Assert;

/**
 * Abstract base implementation of <code>BindingFactory</code>. 
 * 
 * @author Oliver Hutchison
 */
public abstract class AbstractBindingFactory implements BindingFactory {

    private BinderSelectionStrategy binderSelectionStrategy;
    
    private FormModel formModel;
    
    private FormComponentInterceptor interceptor;
    
    protected AbstractBindingFactory(FormModel formModel) {
        Assert.notNull(formModel, "formModel can not be null.");
        this.formModel = formModel;
        FormComponentInterceptorFactory factory = (FormComponentInterceptorFactory)ApplicationServicesLocator.services().getService(FormComponentInterceptorFactory.class);
        interceptor = factory.getInterceptor(formModel);
    }
    
    public Binding createBinding(String formPropertyPath) {
        return createBinding(formPropertyPath, Collections.EMPTY_MAP);
    }

    public Binding createBinding(Class controlType, String formPropertyPath) {
        return createBinding(controlType, formPropertyPath, Collections.EMPTY_MAP);
    }

    public Binding bindControl(JComponent control, String formPropertyPath) {
        return bindControl(control, formPropertyPath, Collections.EMPTY_MAP);
    }
    
    public Binding createBinding(String formPropertyPath, Map context) {
        Assert.notNull(context, "Context must not be null");
        Binder binder = getBinderSelectionStrategy().selectBinder(formModel, formPropertyPath);
        Binding binding = binder.bind(formModel, formPropertyPath, context); 
        interceptBinding(binding);
        return binding;
    }

    public Binding createBinding(Class controlType, String formPropertyPath, Map context) {
        Assert.notNull(context, "Context must not be null");
        Binder binder = getBinderSelectionStrategy().selectBinder(controlType, formModel, formPropertyPath);
        Binding binding =  binder.bind(formModel, formPropertyPath, context);
        interceptBinding(binding);
        return binding;
    }

    public Binding bindControl(JComponent control, String formPropertyPath, Map context) {
        Assert.notNull(context, "Context must not be null");
        Binder binder = getBinderSelectionStrategy().selectBinder(control.getClass(), formModel, formPropertyPath);
        Binding binding =  binder.bind(control, formModel, formPropertyPath, context);
        interceptBinding(binding);
        return binding;
    }
    
    public void interceptBinding(Binding binding) {
        if (interceptor != null) {
            interceptor.processComponent(binding.getProperty(), binding.getControl());
        }
    }
    
    public FormModel getFormModel() {
        return formModel;
    }
        
    protected BinderSelectionStrategy getBinderSelectionStrategy() {
        if (binderSelectionStrategy == null) {
            binderSelectionStrategy = (BinderSelectionStrategy)ApplicationServicesLocator.services().getService(BinderSelectionStrategy.class);
        }
        return binderSelectionStrategy;
    }
    
    public void setBinderSelectionStrategy(BinderSelectionStrategy binderSelectionStrategy) {
        this.binderSelectionStrategy = binderSelectionStrategy;
    }
    
    protected Map createContext(String key, Object value) {
        Map context = new HashMap(4);
        context.put(key, value);
        return context;
    }
}