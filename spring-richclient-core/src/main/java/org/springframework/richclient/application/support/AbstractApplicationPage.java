/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.application.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageComponentListener;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.application.PageComponentPaneFactory;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.util.EventListenerListHelper;
import org.springframework.util.Assert;

/**
 * Abstract "convenience" implementation of <code>ApplicationPage</code>.
 * 
 * @author Peter De Bruycker
 */
public abstract class AbstractApplicationPage extends AbstractControlFactory implements ApplicationPage {

    private final EventListenerListHelper pageComponentListeners = new EventListenerListHelper(
            PageComponentListener.class);

    private ViewDescriptorRegistry viewDescriptorRegistry;

    private PageComponentPaneFactory pageComponentPaneFactory;

    private final List<PageComponent> pageComponents = new ArrayList<PageComponent>();

    private PageComponent activeComponent;

    private SharedCommandTargeter sharedCommandTargeter;

    private PageDescriptor descriptor;

    private ApplicationWindow window;

    private boolean settingActiveComponent;

    private ApplicationEventMulticaster applicationEventMulticaster;

    private PropertyChangeListener pageComponentUpdater = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() instanceof PageComponent) {
                updatePageComponentProperties((PageComponent) evt.getSource());
            }
        }
    };

    public AbstractApplicationPage() {

    }

    public AbstractApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {
        setApplicationWindow(window);
        setDescriptor(pageDescriptor);
    }

    /**
     * Called when the <code>PageComponent</code> changes any of its properties (display name, caption, icon, ...).
     * <p>
     * This method should be overridden when these changes must be reflected in the ui.
     * 
     * @param pageComponent
     *            the <code>PageComponent</code> that has changed
     */
    protected void updatePageComponentProperties(PageComponent pageComponent) {
        // do nothing by default
    }

    protected PageComponent findPageComponent(final String viewDescriptorId) {
        for (PageComponent component : pageComponents) {
            if (component.getId().equals(viewDescriptorId)) {
                return component;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(String id) {
        return (T) findPageComponent(id);
    }

    public void addPageComponentListener(PageComponentListener listener) {
        pageComponentListeners.add(listener);
    }

    public void removePageComponentListener(PageComponentListener listener) {
        pageComponentListeners.remove(listener);
    }

    protected void fireOpened(PageComponent component) {
        component.componentOpened();
        pageComponentListeners.fire("componentOpened", component);
    }

    protected void fireFocusGained(PageComponent component) {
        component.componentFocusGained();
        pageComponentListeners.fire("componentFocusGained", component);
    }

    /**
     * Sets the first {@link PageComponent} as the active one.
     */
    protected void setActiveComponent() {
        if (pageComponents.size() > 0) {
            setActiveComponent((PageComponent) pageComponents.get(0));
        }
    }

    protected ViewDescriptor getViewDescriptor(String viewDescriptorId) {
        return getViewDescriptorRegistry().getViewDescriptor(viewDescriptorId);
    }

    /**
     * Returns the active <code>PageComponent</code>, or <code>null</code> if none.
     * 
     * @return the active <code>PageComponent</code>
     */
    public PageComponent getActiveComponent() {
        return activeComponent;
    }

    /**
     * Activates the given <code>PageComponent</code>. Does nothing if it is already the active one.
     * <p>
     * Does nothing if this <code>ApplicationPage</code> doesn't contain the given <code>PageComponent</code>.
     * 
     * @param pageComponent
     *            the <code>PageComponent</code>
     */
    public void setActiveComponent(PageComponent pageComponent) {
        if (!pageComponents.contains(pageComponent)) {
            return;
        }

        // if pageComponent is already active, don't do anything
        if (this.activeComponent == pageComponent || settingActiveComponent) {
            return;
        }

        settingActiveComponent = true;
        try {
            if (this.activeComponent != null) {
                fireFocusLost(this.activeComponent);
            }
            giveFocusTo(pageComponent);
            this.activeComponent = pageComponent;
            fireFocusGained(this.activeComponent);
        } finally {
            // If this is not done in a finally, any exception thrown in fireFocusGained
            // will prevent the user from leaving the screen
            settingActiveComponent = false;
        }
    }

    protected void fireFocusLost(PageComponent component) {
        component.componentFocusLost();
        pageComponentListeners.fire("componentFocusLost", component);
    }

    /**
     * This method must add the given <code>PageComponent</code> in the ui.
     * <p>
     * Implementors may choose to add the <code>PageComponent</code>'s control directly, or add the
     * <code>PageComponentPane</code>'s control.
     * 
     * @param pageComponent
     *            the <code>PageComponent</code> to add
     */
    protected abstract void doAddPageComponent(PageComponent pageComponent);

    /**
     * This method must remove the given <code>PageComponent</code> from the ui.
     * 
     * @param pageComponent
     *            the <code>PageComponent</code> to remove
     */
    protected abstract void doRemovePageComponent(PageComponent pageComponent);

    /**
     * This method must transfer the focus to the given <code>PageComponent</code>. This could involve making an
     * internal frame visible, selecting a tab in a tabbed pane, ...
     * 
     * @param pageComponent
     *            the <code>PageComponent</code>
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise
     */
    protected abstract boolean giveFocusTo(PageComponent pageComponent);

    protected PageComponentPane createPageComponentPane(PageComponent pageComponent) {
        return getPageComponentPaneFactory().createPageComponentPane(pageComponent);
    }

    protected void fireClosed(PageComponent component) {
        component.componentClosed();
        pageComponentListeners.fire("componentClosed", component);
    }

    public String getId() {
        return descriptor.getId();
    }

    public ApplicationWindow getWindow() {
        return window;
    }

    /**
     * Closes the given <code>PageComponent</code>. This method disposes the <code>PageComponent</code>, triggers all
     * necessary events ("focus lost" and "closed"), and will activate another <code>PageComponent</code> (if there is
     * one).
     * <p>
     * Returns <code>false</code> if this <code>ApplicationPage</code> doesn't contain the given
     * <code>PageComponent</code>.
     * 
     * @param pageComponent
     *            the <code>PageComponent</code>
     * @return boolean <code>true</code> if pageComponent was successfully closed.
     */
    public boolean close(PageComponent pageComponent) {
        if (!pageComponent.canClose()) {
            return false;
        }

        if (!pageComponents.contains(pageComponent)) {
            return false;
        }

        if (pageComponent == activeComponent) {
            fireFocusLost(pageComponent);
            activeComponent = null;
        }

        doRemovePageComponent(pageComponent);
        pageComponents.remove(pageComponent);
        pageComponent.removePropertyChangeListener(pageComponentUpdater);
        if (pageComponent instanceof ApplicationListener && getApplicationEventMulticaster() != null) {
            getApplicationEventMulticaster().removeApplicationListener((ApplicationListener) pageComponent);
        }

        pageComponent.dispose();
        fireClosed(pageComponent);
        if (activeComponent == null) {
            setActiveComponent();
        }
        return true;
    }

    /**
     * Closes this <code>ApplicationPage</code>. This method calls {@link #close(PageComponent)} for each open
     * <code>PageComponent</code>.
     * 
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise.
     */
    public boolean close() {
        for (Iterator<PageComponent> iter = new HashSet<PageComponent>(pageComponents).iterator(); iter.hasNext();) {
            PageComponent component = iter.next();
            if (!close(component))
                return false;
        }
        return true;
    }

    public View showView(String id) {
        Assert.hasText(id, "id cannot be empty");

        return showView(getViewDescriptor(id), false, null);
    }

    public View showView(String id, Object input) {
        Assert.hasText(id, "id cannot be empty");

        return showView(getViewDescriptor(id), true, input);
    }

    private View showView(ViewDescriptor viewDescriptor, boolean setInput, Object input) {
        Assert.notNull(viewDescriptor, "viewDescriptor cannot be null");

        View view = (View) findPageComponent(viewDescriptor.getId());
        if (view == null) {
            view = (View) createPageComponent(viewDescriptor);

            if (setInput) {
                // trigger control creation before input is set to avoid npe
                view.getControl();
                
                view.setInput(input);
            }

            addPageComponent(view);
        } else {
            if (setInput) {
                view.setInput(input);
            }
        }
        setActiveComponent(view);
        
        return view;
    }

    public void openEditor(Object editorInput) {
        // TODO implement editors
    }

    public boolean closeAllEditors() {
        // TODO implement editors
        return true;
    }

    /**
     * Adds the pageComponent to the components list while registering listeners and firing appropriate events. (not yet
     * setting the component as the active one)
     * 
     * @param pageComponent
     *            the pageComponent to add.
     */
    protected void addPageComponent(PageComponent pageComponent) {
        pageComponents.add(pageComponent);
        doAddPageComponent(pageComponent);
        pageComponent.addPropertyChangeListener(pageComponentUpdater);

        fireOpened(pageComponent);
    }

    /**
     * Creates a PageComponent for the given PageComponentDescriptor.
     * 
     * @param descriptor
     *            the descriptor
     * @return the created PageComponent
     */
    protected PageComponent createPageComponent(PageComponentDescriptor descriptor) {
        PageComponent pageComponent = descriptor.createPageComponent();
        pageComponent.setContext(new DefaultViewContext(this, createPageComponentPane(pageComponent)));
        if (pageComponent instanceof ApplicationListener && getApplicationEventMulticaster() != null) {
            getApplicationEventMulticaster().addApplicationListener((ApplicationListener) pageComponent);
        }

        return pageComponent;
    }

    public List<PageComponent> getPageComponents() {
        return Collections.unmodifiableList(pageComponents);
    }

    public final void setApplicationWindow(ApplicationWindow window) {
        Assert.notNull(window, "The containing window is required");
        Assert.state(this.window == null, "Page window already set: it should only be set once, during initialization");
        this.window = window;
        sharedCommandTargeter = new SharedCommandTargeter(window);
        addPageComponentListener(sharedCommandTargeter);
    }

    public final void setDescriptor(PageDescriptor descriptor) {
        Assert.notNull(descriptor, "The page's descriptor is required");
        Assert.state(this.descriptor == null,
                "Page descriptor already set: it should only be set once, during initialization");
        this.descriptor = descriptor;
    }

    protected PageDescriptor getPageDescriptor() {
        return descriptor;
    }

    public ApplicationEventMulticaster getApplicationEventMulticaster() {
        if ((applicationEventMulticaster == null) && (getApplicationContext() != null)) {
            final String beanName = AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;
            if (getApplicationContext().containsBean(beanName)) {
                applicationEventMulticaster = (ApplicationEventMulticaster) getApplicationContext().getBean(beanName);
            }
        }
        return applicationEventMulticaster;
    }

    public void setViewDescriptorRegistry(ViewDescriptorRegistry viewDescriptorRegistry) {
        this.viewDescriptorRegistry = viewDescriptorRegistry;
    }

    public ViewDescriptorRegistry getViewDescriptorRegistry() {
        if (viewDescriptorRegistry == null) {
            viewDescriptorRegistry = (ViewDescriptorRegistry) ApplicationServicesLocator.services().getService(
                    ViewDescriptorRegistry.class);
        }

        return viewDescriptorRegistry;
    }

    public void setPageComponentPaneFactory(PageComponentPaneFactory pageComponentPaneFactory) {
        this.pageComponentPaneFactory = pageComponentPaneFactory;
    }

    public PageComponentPaneFactory getPageComponentPaneFactory() {
        if (pageComponentPaneFactory == null) {
            pageComponentPaneFactory = (PageComponentPaneFactory) ApplicationServicesLocator.services().getService(
                    PageComponentPaneFactory.class);
        }

        return pageComponentPaneFactory;
    }
}
