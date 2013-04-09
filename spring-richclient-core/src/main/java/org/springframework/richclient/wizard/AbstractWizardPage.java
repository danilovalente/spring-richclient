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
package org.springframework.richclient.wizard;

import java.awt.Image;

import javax.swing.JComponent;

import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.dialog.AbstractDialogPage;

public abstract class AbstractWizardPage extends AbstractDialogPage implements WizardPage {
    private Wizard wizard;

    private WizardPage previousPage;

    /**
     * Creates a wizard page. This titles of this dialog page will be configured
     * using the default ObjectConfigurer.
     * 
     * @param pageId
     *            the id of this wizard page. This will be used to configure the
     *            page.
     */
    protected AbstractWizardPage(String pageId) {
        this(pageId, false);
    }

    /**
     * Creates a new wizard page.
     * 
     * @param pageId
     *            the id of this wizard page
     * @param autoConfigure
     *            whether or not to use an ObjectConfigurer to configure the
     *            titles of this dialog page using the given pageId
     */
    protected AbstractWizardPage(String pageId, boolean autoConfigure) {
        super(pageId, autoConfigure);
    }

    /**
     * Creates a new wizard page with the given title.
     * 
     * @param pageId
     *            the id of this wizard page
     * @param autoConfigure
     *            whether or not to use an ObjectConfigurer to configure the
     *            titles of this dialog page using the given pageId
     * @param title
     *            the title of this wizard page, or <code>null</code> if none
     */
    protected AbstractWizardPage(String pageId, boolean autoConfigure, String title) {
        super(pageId, autoConfigure, title);
    }

    /**
     * Creates a new wizard page with the given title and image.
     * 
     * @param pageId
     *            the id of this wizard page
     * @param autoConfigure
     *            whether or not to use an ObjectConfigurer to configure the
     *            titles of this wizard page using the given pageId
     * @param title
     *            the title of this wizard page, or <code>null</code> if none
     * @param image
     *            the image for this wizard page, or <code>null</code> if none
     */
    protected AbstractWizardPage(String pageId, boolean autoConfigure, String title, Image icon) {
        super(pageId, autoConfigure, title, icon);
    }

    public String getKey() {
        return getWizard().getId() + "." + getId();
    }

    public Image getImage() {
        Image image = super.getImage();
        if (image != null) {
            return image;
        }
        return wizard.getDefaultPageImage();
    }

    public WizardPage getNextPage() {
        if (wizard == null) {
            return null;
        }
        return wizard.getNextPage(this);
    }

    public WizardPage getPreviousPage() {
        if (previousPage != null) {
            return previousPage;
        }
        if (wizard == null) {
            return null;
        }
        return wizard.getPreviousPage(this);
    }

    public boolean canFlipToNextPage() {
        return isPageComplete() && getNextPage() != null;
    }

    protected boolean isCurrentPage() {
        return (getContainer() != null && this == getContainer().getCurrentPage());
    }

    protected WizardContainer getContainer() {
        if (wizard == null) {
            return null;
        }
        return wizard.getContainer();
    }

    public Wizard getWizard() {
        return wizard;
    }

    public void setPreviousPage(WizardPage page) {
        previousPage = page;
    }

    public void setVisible(boolean visible) {
        JComponent control = getControl();
        if (control != null) {
            super.setVisible(visible);
            control.requestFocusInWindow();
        }
    }

    public void setEnabled(boolean enabled) {
        setPageComplete(enabled);
    }

    public void setWizard(Wizard newWizard) {
        Wizard oldValue = this.wizard;
        this.wizard = newWizard;
        firePropertyChange("wizard", oldValue, newWizard);
    }

    public void onAboutToShow() {
    }

    public String toString() {
        return new ToStringCreator(this).append("id", getId()).toString();
    }
}