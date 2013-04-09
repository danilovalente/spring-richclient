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
package org.springframework.richclient.command.support;

import javax.swing.JFrame;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.ActionCommand;

/**
 * A skeleton implementation of an action command that needs to be aware of the
 * {@link ApplicationWindow} in which it resides. 
 * 
 * @author Keith Donald
 */
public abstract class ApplicationWindowAwareCommand extends ActionCommand implements ApplicationWindowAware {

    private ApplicationWindow window;

    /**
     * Creates a new uninitialized {@code ApplicationWindowAwareCommand}.
     *
     */
    protected ApplicationWindowAwareCommand() {
        //do nothing
    }

    /**
     * Creates a new {@code ApplicationWindowAwareCommand} with the given command identifier.
     *
     * @param commandId The identifier of this command instance. This should be unique amongst
     * all comands within the application.
     */
    protected ApplicationWindowAwareCommand(String commandId) {
        super(commandId);
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationWindow(ApplicationWindow window) {
        this.window = window;
    }

    /**
     * Returns the application window that this component was created within.
     * @return The application window, or null if this property has not yet been initialized.
     */
    protected ApplicationWindow getApplicationWindow() {
        return window;
    }

    /**
     * Returns the {@link JFrame} of the application window that this command belongs to.
     *
     * @return The control component of the application window, never null.
     */
    protected JFrame getParentWindowControl() {
        // allow subclasses to derive where the application window comes from
        final ApplicationWindow applicationWindow = getApplicationWindow();
        if (applicationWindow == null) {
            return Application.instance().getActiveWindow().getControl();
        }
        return applicationWindow.getControl();
    }
    
}
