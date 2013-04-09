/*
 * Copyright 2002-2006 the original author or authors.
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

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageListener;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.WindowManager;
import org.springframework.richclient.application.config.ApplicationLifecycleAdvisor;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.statusbar.StatusBar;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.util.EventListenerListHelper;
import org.springframework.richclient.util.WindowUtils;
import org.springframework.util.Assert;

/**
 * Abstract helper implementation for <code>ApplicationWindow</code>.
 */
public abstract class AbstractApplicationWindow implements ApplicationWindow, WindowFocusListener {
    protected Log logger = LogFactory.getLog( getClass() );

    private final EventListenerListHelper pageListeners = new EventListenerListHelper( PageListener.class );

    private int number;

    private ApplicationWindowCommandManager commandManager;

    private CommandGroup menuBarCommandGroup;

    private CommandGroup toolBarCommandGroup;

    private StatusBar statusBar;

    private ApplicationWindowConfigurer windowConfigurer;

    private JFrame control;

    private ApplicationPage currentPage;

    private WindowManager windowManager;

    public AbstractApplicationWindow() {
        this( Application.instance().getWindowManager().size() );
    }

    public AbstractApplicationWindow( int number ) {
        this.number = number;
        getAdvisor().setOpeningWindow( this );
        init();
        getAdvisor().onCommandsCreated( this );
    }

    protected void init() {
        this.commandManager = getAdvisor().createWindowCommandManager();
        this.menuBarCommandGroup = getAdvisor().getMenuBarCommandGroup();
        this.toolBarCommandGroup = getAdvisor().getToolBarCommandGroup();
        this.statusBar = getAdvisor().getStatusBar();
    }

    public int getNumber() {
        return number;
    }

    public ApplicationPage getPage() {
        return currentPage;
    }

    protected ApplicationLifecycleAdvisor getAdvisor() {
        return Application.instance().getLifecycleAdvisor();
    }

    protected ApplicationServices getServices() {
        return ApplicationServicesLocator.services();
    }

    protected ApplicationWindowConfigurer getWindowConfigurer() {
        if( windowConfigurer == null ) {
            this.windowConfigurer = initWindowConfigurer();
        }
        return windowConfigurer;
    }

    protected ApplicationWindowConfigurer initWindowConfigurer() {
        return new DefaultApplicationWindowConfigurer( this );
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Iterator getSharedCommands() {
        return commandManager.getSharedCommands();
    }

    public CommandGroup getMenuBar() {
        return menuBarCommandGroup;
    }

    public CommandGroup getToolBar() {
        return toolBarCommandGroup;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void setWindowManager( WindowManager windowManager ) {
        this.windowManager = windowManager;
    }

    /**
     * Show the given page in this window.
     *
     * @param pageId the page to show, identified by id
     *
     * @throws IllegalArgumentException if pageId == null
     */
    public void showPage( String pageId ) {
        if( pageId == null )
            throw new IllegalArgumentException( "pageId == null" );

        if( getPage() == null || !getPage().getId().equals( pageId ) ) {
            showPage( createPage( this, pageId ) );
        } else {
            // asking for the same page, so ignore
        }
    }

    public void showPage( PageDescriptor pageDescriptor ) {
        Assert.notNull( pageDescriptor, "pageDescriptor == null" );

        if( getPage() == null || !getPage().getId().equals( pageDescriptor.getId() ) ) {
            showPage( createPage( pageDescriptor ) );
        } else {
            // asking for the same page, so ignore
        }
    }

    /**
     * Show the given page in this window.
     *
     * @param page the page to show
     *
     * @throws IllegalArgumentException if page == null
     */
    public void showPage( ApplicationPage page ) {
        if( page == null )
            throw new IllegalArgumentException( "page == null" );

        if( this.currentPage == null ) {
            this.currentPage = page;
            getAdvisor().onPreWindowOpen( getWindowConfigurer() );
            this.control = createNewWindowControl();
            this.control.addWindowFocusListener( this );
            initWindowControl( this.control );
            getAdvisor().onWindowCreated( this );
            setActivePage( page );
            this.control.setVisible( true );
            getAdvisor().onWindowOpened( this );
        } else {
            if( !currentPage.getId().equals( page.getId() ) ) {
                final ApplicationPage oldPage = this.currentPage;
                this.currentPage = page;
                setActivePage( page );
                pageListeners.fire( "pageClosed", oldPage );
            } else {
                // asking for the same page, so ignore
            }
        }
        pageListeners.fire( "pageOpened", this.currentPage );
    }

    protected final ApplicationPage createPage( ApplicationWindow window, String pageDescriptorId ) {
        PageDescriptor descriptor = getPageDescriptor( pageDescriptorId );
        return createPage( descriptor );
    }

    /**
     * Factory method for creating the page area managed by this window. Subclasses may
     * override to return a custom page implementation.
     *
     * @param descriptor The page descriptor
     *
     * @return The window's page
     */
    protected ApplicationPage createPage( PageDescriptor descriptor ) {
        ApplicationPageFactory windowFactory = (ApplicationPageFactory) getServices().getService(
                ApplicationPageFactory.class );
        return windowFactory.createApplicationPage( this, descriptor );
    }

    protected PageDescriptor getPageDescriptor( String pageDescriptorId ) {
        ApplicationContext ctx = Application.instance().getApplicationContext();
        Assert.state( ctx.containsBean( pageDescriptorId ), "Do not know about page or view descriptor with name '"
                + pageDescriptorId + "' - check your context config" );
        Object desc = ctx.getBean( pageDescriptorId );
        if( desc instanceof PageDescriptor ) {
            return (PageDescriptor) desc;
        } else if( desc instanceof ViewDescriptor ) {
            return new SingleViewPageDescriptor( (ViewDescriptor) desc );
        } else {
            throw new IllegalArgumentException( "Page id '" + pageDescriptorId
                    + "' is not backed by an ApplicationPageDescriptor" );
        }
    }

    protected void initWindowControl( JFrame windowControl ) {
        ApplicationWindowConfigurer configurer = getWindowConfigurer();
        applyStandardLayout( windowControl, configurer );
        prepareWindowForView( windowControl, configurer );
    }

    protected void applyStandardLayout( JFrame windowControl, ApplicationWindowConfigurer configurer ) {
        windowControl.setTitle( configurer.getTitle() );
        windowControl.setIconImage( configurer.getImage() );
        windowControl.setJMenuBar( createMenuBarControl() );
        windowControl.getContentPane().setLayout( new BorderLayout() );
        windowControl.getContentPane().add( createToolBarControl(), BorderLayout.NORTH );
        windowControl.getContentPane().add( createWindowContentPane() );
        windowControl.getContentPane().add( createStatusBarControl(), BorderLayout.SOUTH );
    }

    /**
     * Set the given <code>ApplicationPage</code> active (visible + selected if
     * applicable)
     *
     * @param page the <code>ApplicationPage</code>
     */
    protected abstract void setActivePage( ApplicationPage page );

    protected void prepareWindowForView( JFrame windowControl, ApplicationWindowConfigurer configurer ) {
        windowControl.pack();
        windowControl.setSize( configurer.getInitialSize() );

        WindowUtils.centerOnScreen(windowControl);
    }

    protected JFrame createNewWindowControl() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        WindowAdapter windowCloseHandler = new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                close();
            }
        };
        frame.addWindowListener( windowCloseHandler );
        return frame;
    }

    public JFrame getControl() {
        return control;
    }

    public boolean isControlCreated() {
        return control != null;
    }

    protected JMenuBar createMenuBarControl() {
        JMenuBar menuBar = menuBarCommandGroup.createMenuBar();
        menuBarCommandGroup.setVisible( getWindowConfigurer().getShowMenuBar() );
        return menuBar;
    }

    protected JComponent createToolBarControl() {
        JComponent toolBar = toolBarCommandGroup.createToolBar();
        toolBarCommandGroup.setVisible( getWindowConfigurer().getShowToolBar() );
        return toolBar;
    }

    protected JComponent createStatusBarControl() {
        JComponent statusBarControl = statusBar.getControl();
        statusBarControl.setVisible( getWindowConfigurer().getShowStatusBar() );
        return statusBarControl;
    }

    public void addPageListener( PageListener listener ) {
        this.pageListeners.add( listener );
    }

    public void removePageListener( PageListener listener ) {
        this.pageListeners.remove( listener );
    }

    /**
	 * Close this window. First checks with the advisor by calling the
	 * {@link ApplicationLifecycleAdvisor#onPreWindowClose(ApplicationWindow)}
	 * method. Then tries to close it's currentPage. If both are successfull,
	 * the window will be disposed and removed from the {@link WindowManager}.
	 *
	 * @return boolean <code>true</code> if both, the advisor and the
	 * currentPage allow the closing action.
	 */
    public boolean close() {
        boolean canClose = getAdvisor().onPreWindowClose( this );
        if( canClose ) {
        	// check if page can be closed
            if( currentPage != null ) {
                canClose = currentPage.close();
                // page cannot be closed, exit method and do not dispose
                if (!canClose)
                	return canClose;
            }

            if( control != null ) {
                control.dispose();
                control = null;
            }

            if( windowManager != null ) {
                windowManager.remove( this );
            }
            windowManager = null;
        }
        return canClose;
    }

    /**
     * When gaining focus, set this window as the active one on it's manager.
     */
    public void windowGainedFocus( WindowEvent e ) {
        if( this.windowManager != null )
            this.windowManager.setActiveWindow( this );
    }

    /**
     * When losing focus no action is done. This way the last focussed window will stay
     * listed as the activeWindow.
     */
    public void windowLostFocus( WindowEvent e ) {
    }

    /**
     * Implementors create the component that contains the contents of this window.
     *
     * @return the content pane
     */
    protected abstract JComponent createWindowContentPane();
}
