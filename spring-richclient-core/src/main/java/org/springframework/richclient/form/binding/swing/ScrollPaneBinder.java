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
package org.springframework.richclient.form.binding.swing;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.BinderSelectionStrategy;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.util.Assert;

/**
 * A binder that binds a scroll pane and the scroll pane's view. If the 
 * scroll pane does not have a view a default binding will be created and
 * set as the scroll pane's view.
 * 
 * @author Oliver Hutchison
 */
public class ScrollPaneBinder extends AbstractBinder {

    private final BinderSelectionStrategy viewBinderSelectionStrategy;

    private final Class defaultViewType;

    /**
     * Constructs a new ScrollPaneBinder
     * 
     * @param viewBinderSelectionStrategy the {@link BinderSelectionStrategy} which will be used 
     * to select a Binder for the scrollpane's view component.
     * @param defaultViewType the type of the component that will be created and bound if the 
     * scroll pane does not already have a view    
     */
    public ScrollPaneBinder(BinderSelectionStrategy viewBinderSelectionStrategy, Class defaultViewType) {
        super(null);
        this.viewBinderSelectionStrategy = viewBinderSelectionStrategy;
        this.defaultViewType = defaultViewType;
    }

    protected JComponent createControl(Map context) {
        return getComponentFactory().createScrollPane();
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof JScrollPane, "Control must be an instance of JScrollPane.");
        JScrollPane scrollPane = (JScrollPane)control;
        Binding viewBinding = getViewBinding(scrollPane, formModel, formPropertyPath, context);
        return new ScrollPaneDecoratedBinding(viewBinding, scrollPane);
    }

    protected Binding getViewBinding(JScrollPane scrollPane, FormModel formModel, String formPropertyPath, Map context) {
        JComponent view = (JComponent)scrollPane.getViewport().getView();
        if (view == null) {
            Binding viewBinding = viewBinderSelectionStrategy.selectBinder(defaultViewType, formModel, formPropertyPath)
                    .bind(formModel, formPropertyPath, context);
            scrollPane.setViewportView(viewBinding.getControl());
            return viewBinding;
        }
        Binding existingBinding = (Binding)view.getClientProperty(BINDING_CLIENT_PROPERTY_KEY);
        if (existingBinding != null) {
            return existingBinding;
        }
        return viewBinderSelectionStrategy.selectBinder(view.getClass(), formModel, formPropertyPath).bind(
                view, formModel, formPropertyPath, context);
    }

    protected void validateContextKeys(Map context) {
        // do nothing as we pass the context on to
        // the scroll pane's view binder
    }
}
