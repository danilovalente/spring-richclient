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
package org.springframework.richclient.factory;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.table.TableModel;

import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.util.Alignment;

/**
 * A factory interface for encapsulating logic to create well-formed, configured
 * GUI controls.
 *
 * @author Keith Donald
 */
public interface ComponentFactory {

	/**
	 * Create and configure a label with the specified label key. For example:
	 * "&My Control Label:", where the '&' marks a positional mnemonic.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @return The configured label.
	 */
	public JLabel createLabel(String labelKey);

	/**
	 * Create and configure a label with the specified label key. For example:
	 * "&My Control Label:", where the '&' marks a positional mnemonic.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @return The configured label.
	 */
	public JLabel createLabel(String[] labelKeys);

	/**
	 * Creates and configure a label with the specified label key and
	 * parameterized arguments. Argument values are resolved to {digit
	 * placeholder} characters in the resolved message string.
	 *
	 * @param labelKey
	 * @param arguments
	 * @return The configured label.
	 */
	public JLabel createLabel(String labelKey, Object[] arguments);

	/**
	 * Creates and configure a label with the specified label key and
	 * parameterized arguments. Argument values are resolved to {digit
	 * placeholder} characters in the resolved message string. Argument values
	 * are pulled from the provided value model, and this component factory will
	 * auto-subscribe for changes, dynamically updating the label when
	 * underlying arguments change.
	 *
	 * @param labelKey
	 * @param argumentValueHolders The value model of the arguments;
	 * @return The configured label.
	 */
	public JLabel createLabel(String labelKey, ValueModel[] argumentValueHolders);

	/**
	 * Create and configure a title label with the specified label key. A title
	 * label's text matches that of a titled border title (bold, highlighted.)
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 *
	 * @return The configured label.
	 */
	public JLabel createTitleLabel(String labelKey);

	/**
	 * Creates a titled border for the specified component.
	 *
	 * @param labelKey the title label message code.
	 * @param comp the component to attach a titled border to.
	 * @return the configured component.
	 */
	public JComponent createTitledBorderFor(String labelKey, JComponent comp);

	/**
	 * Create and configure a label for the provided component. Associating a
	 * label with a component ensures when the mnemonic is selected the
	 * component is given focus.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @param comp the labeled component
	 * @return The configured label.
	 */
	public JLabel createLabelFor(String labelKey, JComponent comp);

	/**
	 * Create and configure a label for the provided component. Associating a
	 * label with a component ensures when the mnemonic is selected the
	 * component is given focus.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @param comp the labeled component
	 * @return The configured label.
	 */
	public JLabel createLabelFor(String[] labelKeys, JComponent comp);

	/**
	 * Create and configure a button with the specified label key. The button
	 * will be configured with the appropriate mnemonic and accelerator. Note:
	 * if you find yourself duplicating the same handler logic accross different
	 * buttons, maybe its time to use a Command.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @return The configured button.
	 */
	public JButton createButton(String labelKey);

	/**
	 * Create and configure an left-aligned label acting as a form dividing
	 * separator; that is, a control that displays a label and a separator
	 * immediately underneath it.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @return The configured labeled separator.
	 */
	public JComponent createLabeledSeparator(String labelKey);

	/**
	 * Create and configure an aligned label acting as a form dividing
	 * separator; that is, a control that displays a label and a separator
	 * immediately underneath it.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @param alignment The label's alignment.
	 * @return The configured labeled separator.
	 */
	public JComponent createLabeledSeparator(String labelKey, Alignment alignment);

	/**
	 * Create a list using this component factory.
	 *
	 * @return The new list.
	 */
	public JList createList();

	/**
	 * Create a combo box using this component factory.
	 *
	 * @return The new combo box.
	 */
	public JComboBox createComboBox();

	/**
	 * Create a combo box using this component factory. The list of selectable
	 * items is populated from the provided value model, which provides access
	 * to a list. When an item is selected in the list, the
	 * selectedItemValueModel is set.
	 *
	 * @return The new combo box.
	 * @param the value model for the list of selectable items
	 * @param the property to render each item in the list.
	 */
	public JComboBox createListValueModelComboBox(ValueModel selectedItemValueModel,
			ValueModel selectableItemsListHolder, String renderedPropertyPath);

	/**
	 * Create a combo box using this component factory, to be populated by the
	 * list of all enums of the specified type, resolved using this factory's
	 * enum resolver.
	 *
	 * @return The new combo box.
	 */
	public JComboBox createComboBox(Class enumType);

	/**
	 * Configure a combo box to be populated with all enums of the specified
	 * enumeration type. The type must be resolvable by this factory's enum
	 * resolver.
	 *
	 * @param enumType The enumeration type.
	 */
	public void configureForEnum(JComboBox comboBox, Class enumType);

	/**
	 * Create a configured menu item.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @return The menu item.
	 */
	public JMenuItem createMenuItem(String labelKey);

	/**
	 * Create a configured checkbox.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @return The checkbox.
	 */
	public JCheckBox createCheckBox(String labelKey);

	/**
	 * Create a configured checkbox.
	 *
	 * @param labelKeys The label message codes; may also be the label text if
	 * no message source is configured.
	 * @return The checkbox.
	 */
	public JCheckBox createCheckBox(String[] labelKeys);

	/**
	 * Create a configured toggle button.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @return The toggle button.
	 */
	public JToggleButton createToggleButton(String labelKey);

	/**
	 * Create a configured toggle button.
	 *
	 * @param labelKeys The label message codes; may also be the label text if
	 * no message source is configured.
	 * @return The toggle button.
	 */
	public JToggleButton createToggleButton(String[] labelKeys);

	/**
	 * Create a configured radio button.
	 *
	 * @param labelKey The label message code; may also be the label text if no
	 * message source is configured.
	 * @return The radio button.
	 */
	public JRadioButton createRadioButton(String labelKey);

	/**
	 * Create a configured radio button.
	 *
	 * @param labelKeys The label message codes; may also be the label text if
	 * no message source is configured.
	 * @return The radio button.
	 */
	public JRadioButton createRadioButton(String[] labelKeys);

	/**
	 * Create a formatted text field using this component factory.
	 *
	 * @param formatterFactory AbstractFormatterFactory used for formatting.
	 * @return The new formatted text field
	 */
	public JFormattedTextField createFormattedTextField(AbstractFormatterFactory formatterFactory);

	/**
	 * Create a standard text field using this component factory.
	 *
	 * @return the new text field.
	 */
	public JTextField createTextField();

	/**
	 * Create a standard password field using this component factory.
	 *
	 * @return the new password field.
	 */
	public JPasswordField createPasswordField();

	/**
	 * Create a text area using this component factory.
	 *
	 * @return The new text area.
	 */
	public JTextArea createTextArea();

	/**
	 * Create a text area using this component factory.
	 *
	 * @return The new text area.
	 */
	public JTextArea createTextArea(int row, int columns);

	/**
	 * Create a text area that looks like a label (but with cut/copy/paste
	 * enabled!) using this component factory.
	 *
	 * @return The new text area.
	 */
	public JTextArea createTextAreaAsLabel();

	/**
	 * Create and return a new tabbed pane.
	 *
	 * @return a new tabbed pane.
	 */
	public JTabbedPane createTabbedPane();

	/**
	 * Adds a tab to the provided tabbed pane, configuring the tab's appearance
	 * from information retrieved using the <code>labelKey</code> property.
	 * The tab title text, icon, mnemonic, and mnemonic index are all
	 * configurable.
	 *
	 * @param tabbedPane
	 * @param labelKey
	 * @param tabComponent
	 */
	public void addConfiguredTab(JTabbedPane tabbedPane, String labelKey, JComponent tabComponent);

	/**
	 * Create a scroll pane using this component factory.
	 *
	 * @return empty scroll pane.
	 * @see javax.swing.JScrollPane#JScrollPane()
	 */
	public JScrollPane createScrollPane();

	/**
	 * Create a scroll pane using this component factory, with the specified
	 * component as the viewport view.
	 *
	 * @param view the component to display in the scrollpane's viewport
	 * @return scroll pane with specified view
	 * @see JScrollPane#JScrollPane(java.awt.Component)
	 */
	public JScrollPane createScrollPane(Component view);

	/**
	 * Create a scroll pane using this component factory, with the specified
	 * component as the viewport view and with the specified vertical and
	 * horizontal scrollbar policies.
	 *
	 * @param view the component to display in the scrollpane's viewport
	 * @param vsbPolicy set the vertical scrollbar policy.
	 * @param hsbPolicy set the horizontal scrollbar policy.
	 * @return scroll pane with specified view and scrolling policies
	 * @see JScrollPane#JScrollPane(java.awt.Component, int, int)
	 */
	public JScrollPane createScrollPane(Component view, int vsbPolicy, int hsbPolicy);

	/**
	 * Creates a panel using this component factory.
	 *
	 * @return the panel
	 * @see JPanel
	 */
	public JPanel createPanel();

	/**
	 * Creates a panel with the supplied LayoutManager using this component
	 * factory.
	 *
	 * @param layoutManager the LayoutManager that will be used by the returned
	 * panel
	 * @return a panel
	 * @see JPanel#JPanel(java.awt.LayoutManager)
	 */
	public JPanel createPanel(LayoutManager layoutManager);

	/**
	 * Construct a JTable with a default model.
	 * @return new table instance
	 */
	public JTable createTable();

	/**
	 * Construct a JTable with the specified table model.
	 * @param model TableModel to install into the new table
	 * @return new table instance
	 */
	public JTable createTable(TableModel model);

	/**
	 * Construct a JToolBar.
	 * @return new toolbar instance
	 */
	public JComponent createToolBar();

}