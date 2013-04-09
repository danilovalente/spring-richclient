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
package org.springframework.richclient.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.richclient.dialog.control.ExtTabbedPane;
import org.springframework.richclient.dialog.control.Tab;
import org.springframework.richclient.dialog.control.VetoableSingleSelectionModel;

/**
 * A concrete implementation of <code>CompositeDialogPage</code> that presents
 * the child pages in a <code>JTabbedPane</code>.
 * <p>
 * Each child page is placed into a separate tab of the <code>JTabbedPane</code>.
 * This class also decorates the tab titles to indicate the page completed
 * status.
 * <p>
 * Has support for hiding/showing <code>DialogPage</code>s; on
 * <code>DialogPage.setVisible(false)</code> the tab for the page is removed
 * from the ui, on <code>DialogPage.setVisible(true)</code> it is shown again.
 * 
 * @author Oliver Hutchison
 * @author Peter De Bruycker
 */
public class TabbedDialogPage extends CompositeDialogPage {
	private ExtTabbedPane tabbedPaneView;

	private Map page2tab = new HashMap();

	private Map tab2Page = new HashMap();

	private boolean settingSelection;

	public TabbedDialogPage(String pageId) {
		super(pageId);
	}

	public TabbedDialogPage(String pageId, boolean autoConfigure) {
		super(pageId, autoConfigure);
	}

	protected JComponent createControl() {
		createPageControls();
		final JTabbedPane tabbedPane = getComponentFactory().createTabbedPane();
		tabbedPaneView = new ExtTabbedPane(tabbedPane);

		List pages = getPages();
		for (int i = 0; i < pages.size(); i++) {
			final DialogPage page = (DialogPage) pages.get(i);
			final Tab tab = new Tab();

			JComponent control = page.getControl();
			control.setPreferredSize(getLargestPageSize());

			tab.setComponent(control);
			tab.setVisible(page.isVisible());
			tab.setEnabled(page.isEnabled());
			decorateTab(tab, page);

			page2tab.put(page, tab);
			tab2Page.put(tab, page);
			tabbedPaneView.addTab(tab);
		}
		
		tabbedPane.setModel(new VetoableSingleSelectionModel() {
			protected boolean selectionAllowed(int index) {
				return canChangeTabs();
			}
		});
		
		tabbedPaneView.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				
				if (index >= 0) {
					index = tabbedPaneView.convertUIIndexToModelIndex(index);
					Tab tab = tabbedPaneView.getTab(index);
					setActivePage((DialogPage) tab2Page.get(tab));
				}
				else {
					setActivePage(null);
				}
			}
		});
		
		// show first visible tab
		setActivePage((DialogPage) pages.get(tabbedPaneView.convertUIIndexToModelIndex(0)));
		return tabbedPane;
	}

	/**
	 * Sets the active page of this TabbedDialogPage. This method will also
	 * select the tab wich displays the new active page.
	 * 
	 * @param activePage the page to be made active. Must be one of the child
	 * pages.
	 */
	public void setActivePage(DialogPage page) {
		if (settingSelection) {
			return;
		}

		try {
			settingSelection = true;

			super.setActivePage(page);
			if (page != null) {
				Tab tab = (Tab) page2tab.get(page);
				tabbedPaneView.selectTab(tab);
			}
		}
		finally {
			settingSelection = false;
		}
	}

	protected boolean canChangeTabs() {
		return true;
	}

	protected void updatePageComplete(DialogPage page) {
		super.updatePageComplete(page);

		if (tabbedPaneView != null) {
			Tab tab = (Tab) page2tab.get(page);
			decorateTab(tab, page);
		}
	}

	protected void decorateTab(Tab tab, DialogPage page) {
		tab.setTitle(getDecoratedPageTitle(page));
		tab.setTooltip(page.getDescription());
		if (page instanceof LabeledObjectSupport) {
			tab.setMnemonic(((LabeledObjectSupport) page).getMnemonic());
		}
		tab.setIcon(page.getIcon());
	}

	protected void updatePageVisibility(DialogPage page) {
		Tab tab = getTab(page);
		tab.setVisible(page.isVisible());
	}
	
	protected void updatePageEnabled(DialogPage page) {
		Tab tab = getTab(page);
		tab.setEnabled(page.isEnabled());
	}

	protected void updatePageLabels(DialogPage page) {
		Tab tab = getTab(page);
		tab.setTitle(getDecoratedPageTitle(page));
	}

	protected final Tab getTab(final DialogPage page) {
		return (Tab) page2tab.get(page);
	}
}