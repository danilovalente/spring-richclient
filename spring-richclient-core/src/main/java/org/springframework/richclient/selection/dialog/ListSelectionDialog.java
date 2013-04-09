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
package org.springframework.richclient.selection.dialog;

import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.util.Assert;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.EventListModel;

/**
 * A <code>ListSelectionDialog</code> can be used to select an item from a
 * list.
 * 
 * @author Peter De Bruycker
 */
public class ListSelectionDialog extends AbstractSelectionDialog {

	private ListCellRenderer renderer;

	private JList list;

	private EventList items;

	public ListSelectionDialog(String title, List items) {
		this(title, null, GlazedLists.eventList(items));
	}
	
	public ListSelectionDialog(String title, Window parent, List items) {
		this(title, parent, GlazedLists.eventList(items));
	}

	public ListSelectionDialog(String title, Window parent, EventList items) {
		super(title, parent);
		this.items = items;
	}
	
	public void setRenderer(ListCellRenderer renderer) {
		Assert.notNull(renderer, "Renderer cannot be null.");
		Assert.isTrue(!isControlCreated(), "Install the renderer before the control is created.");

		this.renderer = renderer;
	}

	protected JComponent createSelectionComponent() {
		list = getComponentFactory().createList();
		list.setModel(new EventListModel(items));

		list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addListSelectionListener(new ListSelectionListener() {

			private int lastIndex = -1;

			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}

				if (list.getSelectionModel().isSelectionEmpty() && lastIndex > -1) {
					if (list.getModel().getSize() > 0) {
						list.setSelectedIndex(lastIndex);
						return;
					}
				}

				setFinishEnabled(!list.getSelectionModel().isSelectionEmpty());
				lastIndex = list.getSelectedIndex();
			}
		});

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					getFinishCommand().execute();
				}
			}
		});

		if (renderer != null) {
			list.setCellRenderer(renderer);
		}

		setFinishEnabled(false);

		if (!items.isEmpty()) {
			list.setSelectedIndex(0);
		}

		return new JScrollPane(list);
	}

	protected Object getSelectedObject() {
		return items.get(list.getSelectedIndex());
	}

	protected final JList getList() {
		return list;
	}
}
