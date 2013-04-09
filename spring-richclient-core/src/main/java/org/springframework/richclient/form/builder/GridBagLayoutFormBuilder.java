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
package org.springframework.richclient.form.builder;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.LabelOrientation;
import org.springframework.richclient.layout.LayoutBuilder;

/**
 * @see GridBagLayoutBuilder
 */
public class GridBagLayoutFormBuilder extends AbstractFormBuilder implements LayoutBuilder {

    private final GridBagLayoutBuilder builder;

    public GridBagLayoutFormBuilder(BindingFactory bindingFactory) {
        super(bindingFactory);
        this.builder = new FormModelAwareGridBagLayoutBuilder();
    }

    /**
     * Returns the underlying {@link GridBagLayoutBuilder}. Should be used with
     * caution.
     *
     * @return never null
     */
    public final GridBagLayoutBuilder getBuilder() {
        return builder;
    }

    public void setComponentFactory(ComponentFactory componentFactory) {
        super.setComponentFactory(componentFactory);
        builder.setComponentFactory(componentFactory);
    }

    /**
     * Appends a label and field to the end of the current line.
     * <p />
     *
     * The label will be to the left of the field, and be right-justified.
     * <br />
     * The field will "grow" horizontally as space allows.
     * <p />
     *
     * @param propertyName the name of the property to create the controls for
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see FormModelHelper#createLabel(String)
     * @see FormModelHelper#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName) {
        return appendLabeledField(propertyName, LabelOrientation.LEFT);
    }

    /**
     * Appends a label and field to the end of the current line.
     * <p />
     *
     * The label will be to the left of the field, and be right-justified.
     * <br />
     * The field will "grow" horizontally as space allows.
     * <p />
     *
     * @param propertyName the name of the property to create the controls for
     * @param colSpan      the number of columns the field should span
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see FormModelHelper#createLabel(String)
     * @see FormModelHelper#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName, int colSpan) {
        return appendLabeledField(propertyName, LabelOrientation.LEFT, colSpan);
    }

    /**
     * Appends a label and field to the end of the current line.
     * <p />
     *
     * The label will be to the left of the field, and be right-justified.
     * <br />
     * The field will "grow" horizontally as space allows.
     * <p />
     *
     * @param propertyName the name of the property to create the controls for
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see FormModelHelper#createLabel(String)
     * @see FormModelHelper#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName, LabelOrientation labelOrientation) {
        return appendLabeledField(propertyName, labelOrientation, 1);
    }

    /**
     * Appends a label and field to the end of the current line.
     * <p />
     *
     * The label will be to the left of the field, and be right-justified.
     * <br />
     * The field will "grow" horizontally as space allows.
     * <p />
     *
     * @param propertyName the name of the property to create the controls for
     * @param colSpan      the number of columns the field should span
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see FormModelHelper#createLabel(String)
     * @see FormModelHelper#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName, LabelOrientation labelOrientation,
            int colSpan) {
        final JComponent field = createDefaultBinding(propertyName).getControl();

        return appendLabeledField(propertyName, field, labelOrientation, colSpan);
    }

    /**
     * Appends a label and field to the end of the current line.
     * <p />
     *
     * The label will be to the left of the field, and be right-justified.
     * <br />
     * The field will "grow" horizontally as space allows.
     * <p />
     *
     * @param propertyName the name of the property to create the controls for
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see FormModelHelper#createLabel(String)
     * @see FormModelHelper#createBoundControl(String)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName, final JComponent field,
            LabelOrientation labelOrientation) {
        return appendLabeledField(propertyName, field, labelOrientation, 1);
    }

    /**
     * Appends a label and field to the end of the current line.
     * <p />
     *
     * The label will be to the left of the field, and be right-justified.
     * <br />
     * The field will "grow" horizontally as space allows.
     * <p />
     *
     * @param propertyName the name of the property to create the controls for
     * @param colSpan      the number of columns the field should span
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see FormModelHelper#createLabel(String)
     * @see FormComponentInterceptor#processLabel(String, JComponent)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName, final JComponent field,
            LabelOrientation labelOrientation, int colSpan) {
        return appendLabeledField(propertyName, field, labelOrientation, colSpan, 1, true, false);
    }

    /**
     * Appends a label and field to the end of the current line.
     * <p />
     *
     * The label will be to the left of the field, and be right-justified.
     * <br />
     * The field will "grow" horizontally as space allows.
     * <p />
     *
     * @param propertyName the name of the property to create the controls for
     * @param colSpan      the number of columns the field should span
     *
     * @return "this" to make it easier to string together append calls
     *
     * @see FormModelHelper#createLabel(String)
     * @see FormComponentInterceptor#processLabel(String, JComponent)
     */
    public GridBagLayoutFormBuilder appendLabeledField(String propertyName, final JComponent field,
            LabelOrientation labelOrientation, int colSpan, int rowSpan, boolean expandX, boolean expandY) {
        builder.appendLabeledField(propertyName, field, labelOrientation, colSpan, rowSpan, expandX, expandY);
        return this;
    }

    /**
     * Appends a separator (usually a horizontal line). Has an implicit
     * {@link #nextLine()}before and after it.
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutFormBuilder appendSeparator() {
        return appendSeparator(null);
    }

    /**
     * Appends a separator (usually a horizontal line) using the provided string
     * as the key to look in the
     * {@link #setComponentFactory(ComponentFactory) ComponentFactory's}message
     * bundle for the text to put along with the separator. Has an implicit
     * {@link #nextLine()}before and after it.
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutFormBuilder appendSeparator(String labelKey) {
        builder.appendSeparator(labelKey);
        return this;
    }

    /**
     * Ends the current line and starts a new one
     *
     * @return "this" to make it easier to string together append calls
     */
    public GridBagLayoutFormBuilder nextLine() {
        builder.nextLine();
        return this;
    }

    /**
     * Should this show "guidelines"? Useful for debugging layouts.
     */
    public void setShowGuidelines(boolean showGuidelines) {
        builder.setShowGuidelines(showGuidelines);
    }

    /**
     * Creates and returns a JPanel with all the given components in it, using
     * the "hints" that were provided to the builder.
     *
     * @return a new JPanel with the components laid-out in it
     */
    public JPanel getPanel() {
        return builder.getPanel();
    }

    /**
     * @see GridBagLayoutBuilder#setAutoSpanLastComponent(boolean)
     */
    public void setAutoSpanLastComponent(boolean autoSpanLastComponent) {
        builder.setAutoSpanLastComponent(autoSpanLastComponent);
    }

    protected final class FormModelAwareGridBagLayoutBuilder extends GridBagLayoutBuilder {
        protected JLabel createLabel(String propertyName) {
            JLabel label = getComponentFactory().createLabel("");
            getFormModel().getFieldFace(propertyName).configure(label);
            return label;
        }
    }
}