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
package org.springframework.richclient.dialog;

import java.awt.Image;

import javax.swing.Icon;

import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.factory.ControlFactory;

/**
 * DialogPages are used to combine several controls in one Dialog which acts as
 * a container. Different implementations are available to create tabs/trees or
 * wizard-like sequences.
 *
 * @author Keith Donald
 */
public interface DialogPage extends ControlFactory, Messagable, Guarded, PropertyChangePublisher {

	public static final String DESCRIPTION_PROPERTY = "description";

	public static final String PAGE_COMPLETE_PROPERTY = "pageComplete";

	/**
	 * Returns this page's name.
	 *
	 * @return the name of this page
	 */
	public String getId();

	/**
	 * Returns this dialog page's title.
	 *
	 * @return the title of this dialog page, or <code>null</code> if none
	 */
	public String getTitle();

	/**
	 * Returns this dialog page's description text.
	 *
	 * @return the description text for this dialog page, or <code>null</code>
	 * if none
	 */
	public String getDescription();

	/**
	 * Returns the current message for this dialog page.
	 *
	 * @return the message, or <code>null</code> if none
	 */
	public Message getMessage();

	/**
	 * Returns this dialog page's image.
	 *
	 * @return the image for this dialog page, or <code>null</code> if none
	 */
	public Image getImage();

	/**
	 * Returns this dialog page's icon.
	 *
	 * @return the icon for this dialog page, or <code>null</code> if none
	 */
	public Icon getIcon();

	/**
	 * Notifies that help has been requested for this dialog page.
	 */
	public void performHelp();

	/**
	 * Sets the visibility of this dialog page.
	 *
	 * @param visible <code>true</code> to make this page visible, and
	 * <code>false</code> to hide it
	 */
	public void setVisible(boolean visible);

	/**
	 * Returns the visibility of this dialog page.
	 *
	 * @return <code>true</code> this page is visible, or <code>false</code>
	 * if this page is hidden
	 */
	public boolean isVisible();

	/**
	 * Returns whether this page is complete or not.
	 * <p>
	 * This information is typically to decide when it is okay to submit a form.
	 * </p>
	 *
	 * @return <code>true</code> if this page is complete, and
	 * <code>false</code> otherwise
	 */
	public boolean isPageComplete();

}