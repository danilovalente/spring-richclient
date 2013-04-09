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
package org.springframework.richclient.core;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.application.support.ApplicationServicesAccessor;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.config.CommandLabelConfigurable;
import org.springframework.richclient.image.config.ImageConfigurable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * A convenient super class for objects that can be labeled for display in a
 * GUI.
 * 
 * @author Keith Donald
 */
public class LabeledObjectSupport extends ApplicationServicesAccessor implements DescribedElement, VisualizedElement,
		CommandLabelConfigurable, ImageConfigurable, DescriptionConfigurable, TitleConfigurable {
	protected final Log logger = LogFactory.getLog(getClass());

	private CommandButtonLabelInfo label;

	private String title;

	private String caption;

	private String description;

	private Image image;

	private PropertyChangeSupport propertyChangeSupport;

	public void setLabelInfo(CommandButtonLabelInfo label) {
		String oldDisplayName = null;
		if (this.title != null || this.label != null) {
			oldDisplayName = getDisplayName();
		}
		
		int oldMnemonic = getMnemonic();
		int oldMnemonicIndex = getMnemonicIndex();
		KeyStroke oldAccelerator = getAccelerator();
		this.label = label;
		firePropertyChange(DISPLAY_NAME_PROPERTY, oldDisplayName, getDisplayName());
		firePropertyChange("mnemonic", oldMnemonic, getMnemonic());
		firePropertyChange("mnemonicIndex", oldMnemonicIndex, getMnemonicIndex());
		firePropertyChange("accelerator", oldAccelerator, getAccelerator());
	}

	public void setCaption(String caption) {
		String oldValue = caption;
		this.caption = caption;
		firePropertyChange(CAPTION_PROPERTY, oldValue, caption);
	}

	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		firePropertyChange(DESCRIPTION_PROPERTY, oldValue, description);
	}

	public void setTitle(String title) {
		String oldValue = null;
		if (this.title != null || this.label != null) {
			oldValue = getDisplayName();
		}

		this.title = title;
		firePropertyChange(DISPLAY_NAME_PROPERTY, oldValue, getDisplayName());
	}

	public void setImage(Image image) {
		Image oldValue = image;
		this.image = image;
		firePropertyChange("image", oldValue, image);
	}

	public String getDisplayName() {
		if (title != null) {
			return title;
		}

		if (label == null) {
			if (logger.isInfoEnabled()) {
				logger.info("This labeled object's display name is not configured; returning 'displayName'");
			}
			return "displayName";
		}
		return label.getText();
	}

	public String getCaption() {
		return caption;
	}

	public String getDescription() {
		return description;
	}

	public Image getImage() {
		return image;
	}

	public Icon getIcon() {
		if (image != null)
			return new ImageIcon(image);

		return null;
	}

	public int getMnemonic() {
		if (label != null)
			return label.getMnemonic();

		return 0;
	}

	public int getMnemonicIndex() {
		if (label != null)
			return label.getMnemonicIndex();

		return 0;
	}

	public KeyStroke getAccelerator() {
		if (label != null)
			return label.getAccelerator();

		return null;
	}

	public CommandButtonLabelInfo getLabel() {
		return label;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		getOrCreatePropertyChangeSupport().addPropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
		getOrCreatePropertyChangeSupport().addPropertyChangeListener(propertyName, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		getPropertyChangeSupport().removePropertyChangeListener(l);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
		getPropertyChangeSupport().removePropertyChangeListener(propertyName, l);
	}

	private PropertyChangeSupport getPropertyChangeSupport() {
		Assert.notNull(propertyChangeSupport,
				"Property change support has not yet been initialized; add a listener first!");
		return propertyChangeSupport;
	}

	private PropertyChangeSupport getOrCreatePropertyChangeSupport() {
		if (propertyChangeSupport == null) {
			propertyChangeSupport = new SwingPropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}

	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		if (propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		if (propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	protected boolean hasChanged(Object currentValue, Object proposedValue) {
		return !ObjectUtils.nullSafeEquals(currentValue, proposedValue);
	}

	protected boolean hasChanged(boolean currentValue, boolean proposedValue) {
		return currentValue != proposedValue;
	}

	protected boolean hasChanged(int currentValue, int proposedValue) {
		return currentValue != proposedValue;
	}

	public String toString() {
		return new ToStringCreator(this).toString();
	}

}