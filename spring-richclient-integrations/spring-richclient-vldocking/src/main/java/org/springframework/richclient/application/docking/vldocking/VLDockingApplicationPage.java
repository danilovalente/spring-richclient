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
package org.springframework.richclient.application.docking.vldocking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JComponent;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.io.Resource;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.support.AbstractApplicationPage;
import org.xml.sax.SAXException;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableResolver;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockingContext;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.event.DockableSelectionEvent;
import com.vlsolutions.swing.docking.event.DockableSelectionListener;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;
import com.vlsolutions.swing.docking.event.DockableStateWillChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateWillChangeListener;

/**
 * @author Rogan Dawes
 */
public class VLDockingApplicationPage extends AbstractApplicationPage implements PageLayoutBuilder {

    private DockingDesktop desktop;

    private DockingContext dockingContext;

    private VLDockingLayoutManager layoutManager = null;

    private boolean resolving = false;

    private Resource initialLayout = null;

    public VLDockingApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {
        super(window, pageDescriptor);
        if (pageDescriptor instanceof VLDockingPageDescriptor) {
            VLDockingPageDescriptor descriptor = (VLDockingPageDescriptor) pageDescriptor;
            setLayoutManager(descriptor.getLayoutManager());
            setInitialLayout(descriptor.getInitialLayout());
        }
    }

    protected PageComponent getPageComponent(Dockable dockable) {
        if (dockable instanceof ViewDescriptorDockable)
            return ((ViewDescriptorDockable) dockable).getPageComponent();
        return null;
    }

    protected Dockable getDockable(PageComponent pageComponent) {
        DockableState[] states = desktop.getDockables();
        for (int i = 0; i < states.length; i++) {
            Dockable dockable = states[i].getDockable();
            PageComponent pc = getPageComponent(dockable);
            if (pc == pageComponent)
                return dockable;
        }
        return null;
    }

    protected boolean giveFocusTo(PageComponent pageComponent) {
        Dockable dockable = getDockable(pageComponent);
        if (dockable == null) {
            return false;
        }
        // Don't request focus here, the DockingDesktop already shifts focus. If requesting focus at this point, 
        //  the DockingDesktop catches this event and fires another focus event. This might cause loops when 
        // maximizing/minimizing/restoring because at that point a remove of the component is done which shifts
        // focus and after setting the correct docking state, a focus request is done. 
        // see RCP-558
        return true;
    }

    public void addView(String viewDescriptorId) {
        showView(viewDescriptorId);
    }

    protected void doAddPageComponent(PageComponent pageComponent) {
        if (resolving)
            return;
        pageComponent.getControl();
        Dockable dockable = getDockable(pageComponent);
        if (dockable != null)
            return;
        dockable = createDockable(pageComponent);
        getLayoutManager().addDockable(desktop, dockable);
    }

    protected Dockable createDockable(PageComponent pageComponent) {
        return createDockable(getViewDescriptor(pageComponent.getId()), pageComponent);
    }

    protected Dockable createDockable(ViewDescriptor descriptor, PageComponent pageComponent) {
        return new ViewDescriptorDockable(descriptor, pageComponent);
    }

    protected void doRemovePageComponent(PageComponent pageComponent) {
        Dockable dockable = getDockable(pageComponent);
        if (dockable != null) {
            getLayoutManager().removeDockable(desktop, dockable);
        }
    }

    protected JComponent createControl() {
        String name = getPageDescriptor().getId();
        desktop = new DockingDesktop(name, getDockingContext());
        desktop.setName(name);
        DockableListener listener = new DockableListener();
        desktop.addDockableStateChangeListener(listener);
        desktop.addDockableStateWillChangeListener(listener);
        desktop.addDockableSelectionListener(listener);

        if (initialLayout != null) {
            try {
                InputStream in = initialLayout.getInputStream();
                desktop.getContext().readXML(in);
                in.close();
            } catch (IOException ioe) {
                logger.warn("Error reading workspace layout " + initialLayout + ", using defaults", ioe);
                getPageDescriptor().buildInitialLayout(this);
            } catch (SAXException saxe) {
                logger.warn("Error parsing workspace layout " + initialLayout + ", using defaults", saxe);
                getPageDescriptor().buildInitialLayout(this);
            } catch (ParserConfigurationException pce) {
                logger.warn("Error parsing workspace layout " + initialLayout + ", using defaults", pce);
                getPageDescriptor().buildInitialLayout(this);
            }
            if (desktop.getDockables().length == 0) {
                getPageDescriptor().buildInitialLayout(this);
            }
        } else {
            getPageDescriptor().buildInitialLayout(this);
        }
        return desktop;
    }

    protected void updatePageComponentProperties(PageComponent pageComponent) {
        Dockable dockable = getDockable(pageComponent);
        DockKey dockKey = dockable.getDockKey();

        if (pageComponent.getIcon() != null) {
            dockKey.setIcon(pageComponent.getIcon());
        }
        dockKey.setName(pageComponent.getDisplayName());
        dockKey.setTooltip(pageComponent.getCaption());
    }

    /**
     * @return the dockingContext
     */
    public DockingContext getDockingContext() {
        if (this.dockingContext == null) {
            this.dockingContext = new DockingContext();
            this.dockingContext.setDockableResolver(new ViewDescriptorResolver());
        }
        return this.dockingContext;
    }

    /**
     * @return the layoutManager
     */
    private VLDockingLayoutManager getLayoutManager() {
        if (this.layoutManager == null) {
            layoutManager = new DefaultLayoutManager();
        }
        return this.layoutManager;
    }

    /**
     * @param layoutManager
     *            the layoutManager to set
     */
    public void setLayoutManager(VLDockingLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /**
     * @param initialLayout
     *            the initialLayout to set
     */
    public void setInitialLayout(Resource initialLayout) {
        this.initialLayout = initialLayout;
    }

    public boolean close() {
        // try to save the layout if it came from a file we can write to
        if (initialLayout != null) {
            File file = null;
            try {
                file = initialLayout.getFile();
                if (file.canWrite()) {
                    try {
                        OutputStream out = new FileOutputStream(file);
                        desktop.getContext().writeXML(out);
                        out.close();
                    } catch (IOException ioe) {
                        logger.warn("Error saving desktop layout to " + file, ioe);
                    }
                }
            } catch (IOException ioe) {
            }
        }
        return super.close();
    }

    private class DockableListener implements DockableStateChangeListener, DockableStateWillChangeListener,
            DockableSelectionListener {

        /*
         * (non-Javadoc)
         * 
         * @see com.vlsolutions.swing.docking.event.DockableStateWillChangeListener#dockableStateWillChange(com.vlsolutions.swing.docking.event.DockableStateWillChangeEvent)
         */
        public void dockableStateWillChange(DockableStateWillChangeEvent event) {
            DockableState futureState = event.getFutureState();
            if (futureState.isClosed()) {
                Dockable dockable = futureState.getDockable();
                if (dockable instanceof ViewDescriptorDockable) {
                    ViewDescriptorDockable vdd = (ViewDescriptorDockable) dockable;
                    PageComponent pc = vdd.getPageComponent();
                    if (!pc.canClose())
                        event.cancel();
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vlsolutions.swing.docking.event.DockableStateChangeListener#dockableStateChanged(com.vlsolutions.swing.docking.event.DockableStateChangeEvent)
         */
        public void dockableStateChanged(DockableStateChangeEvent event) {
            DockableState previousState = event.getPreviousState();
            DockableState newState = event.getNewState();
            Dockable dockable = newState.getDockable();
            PageComponent pc = getPageComponent(dockable);
            if (pc == null)
                return;
            if (previousState != null && !previousState.isClosed() && newState.isClosed()) {
                pc.getContext().getPage().close(pc);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vlsolutions.swing.docking.event.DockableSelectionListener#selectionChanged(com.vlsolutions.swing.docking.event.DockableSelectionEvent)
         */
        public void selectionChanged(DockableSelectionEvent e) {
            Dockable dockable = e.getSelectedDockable();
            if (dockable != null) {
                PageComponent pc = getPageComponent(dockable);
                if (pc != null)
                    setActiveComponent(pc);
            }
        }

    }

    private class ViewDescriptorResolver implements DockableResolver {

        public Dockable resolveDockable(String keyName) {
            ViewDescriptor descriptor = getViewDescriptor(keyName);
            if (descriptor == null)
                return null;
            PageComponent pageComponent = createPageComponent(descriptor);
            resolving = true;
            addPageComponent(pageComponent);
            resolving = false;
            Dockable dockable = createDockable(descriptor, pageComponent);
            return dockable;
        }

    }

    private class DefaultLayoutManager implements VLDockingLayoutManager {

        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.richclient.application.vldocking.VLDockingLayoutManager#addDockable(com.vlsolutions.swing.docking.DockingDesktop,
         *      com.vlsolutions.swing.docking.Dockable)
         */
        public void addDockable(DockingDesktop desktop, Dockable dockable) {
            desktop.addDockable(dockable);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.richclient.application.vldocking.VLDockingLayoutManager#removeDockable(com.vlsolutions.swing.docking.DockingDesktop,
         *      com.vlsolutions.swing.docking.Dockable)
         */
        public void removeDockable(DockingDesktop desktop, Dockable dockable) {
            desktop.remove(dockable);
        }

    }

}
