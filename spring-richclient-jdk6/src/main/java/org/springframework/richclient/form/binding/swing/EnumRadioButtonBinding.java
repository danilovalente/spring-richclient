package org.springframework.richclient.form.binding.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.springframework.binding.form.FormModel;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.form.binding.support.CustomBinding;
import org.springframework.util.Assert;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Radiobutton binding for Enum values.
 *
 * Labels for the radio buttons can be configured in the messages:
 *
 * <pre>
 * my.package.MyClass.MY_ENUM = My Enum Label
 * my.package.MyClass.null = None
 * </pre>
 *
 * @author Lieven Doclo
 *
 */
public class EnumRadioButtonBinding extends CustomBinding {

    private JPanel contentPanel;

    private List<Enum> selectableEnumsList;

    private Map<String, ContainerJRadioButton<Enum>> radioButtons;

    private Class propertyType;

    private ButtonGroup group;

    private MessageSourceAccessor messageSourceAccessor;

    public EnumRadioButtonBinding(JPanel contentPanel, FormModel formModel, String formPropertyPath,
            Class<?> propertyType, List<Enum> selectableEnumsList) {
        super(formModel, formPropertyPath, propertyType);
        this.contentPanel = contentPanel;
        this.propertyType = propertyType;
        radioButtons = new LinkedHashMap<String, ContainerJRadioButton<Enum>>();
        messageSourceAccessor = getMessages();
        this.selectableEnumsList = selectableEnumsList;
        createRadioButtons();
    }

    @Override
    protected void valueModelChanged(Object newValue) {
        deselectAll();
        Enum enumValue = (Enum) newValue;
        ContainerJRadioButton<Enum> button;
        if (enumValue == null) {
            button = radioButtons.get(null);
        }
        else {
            button = radioButtons.get(enumValue.name());
        }
        if (button != null) {
            button.setSelected(true);
        }
    }

    private void deselectAll() {
        if (group != null)
            group.clearSelection();
    }

    @Override
    protected JComponent doBindControl() {
        Assert.notNull(selectableEnumsList);
        return createBindingControl();
    }

    private JPanel createBindingControl() {
        group = new ButtonGroup();
        FormLayout layout = new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC },
                getDefaultRowsWithGap(radioButtons.keySet().size()));
        int count = 1;
        contentPanel.setLayout(layout);
        for (ContainerJRadioButton<Enum> button : radioButtons.values()) {
            group.add(button);
            contentPanel.add(button, new CellConstraints(1, count));
            count += 2;
        }
        valueModelChanged(getValue());
        return contentPanel;
    }

    private static RowSpec[] getDefaultRowsWithGap(int rowCount) {
        List<RowSpec> rows = new ArrayList<RowSpec>();
        for (int i = 0; i < rowCount; i++) {
            rows.add(new RowSpec("fill:pref:nogrow"));
            rows.add(FormFactory.LINE_GAP_ROWSPEC);
        }
        rows.remove(rows.size() - 1);
        return rows.toArray(new RowSpec[] {});
    }

    @Override
    protected void enabledChanged() {
        for (ContainerJRadioButton<Enum> button : radioButtons.values()) {
            button.setEnabled(isEnabled());
        }

    }

    @Override
    protected void readOnlyChanged() {
        for (ContainerJRadioButton<Enum> button : radioButtons.values()) {
            button.setEnabled(isReadOnly());
        }
    }

    private void createRadioButtons() {
        for (Enum enumValue : selectableEnumsList) {
            ContainerJRadioButton<Enum> button = new ContainerJRadioButton<Enum>();
            String text;
            if (enumValue == null) {
                text = messageSourceAccessor.getMessage(propertyType.getName() + ".null", "null");
                button.setText(text == null ? propertyType.getName() + ".null" : text);
            }
            else {
                text = messageSourceAccessor.getMessage(propertyType.getName() + "." + enumValue.name(), enumValue
                        .name());
                button.setText(text == null ? propertyType.getName() + "." + enumValue.name() : text);
            }
            button.setContainedObject(enumValue);
            button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ContainerJRadioButton<Enum> button = (ContainerJRadioButton<Enum>) e.getSource();
                    controlValueChanged(button.getContainedObject());
                }

            });
            if (enumValue == null)
                radioButtons.put(null, button);
            else
                radioButtons.put(enumValue.name(), button);

        }
    }

    private class ContainerJRadioButton<T> extends JRadioButton {

        private T containedObject;

        public T getContainedObject() {
            return containedObject;
        }

        public void setContainedObject(T containedObject) {
            this.containedObject = containedObject;
        }

    }

}