package org.springframework.richclient.form.binding.swing.date;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXDatePicker;
import org.springframework.binding.form.FormModel;

/**
 * Binds a <cod>Date</code> to a <code>JXDatePicker</code>
 * 
 * @author Peter De Bruycker
 */
public class JXDatePickerDateFieldBinding extends AbstractDateFieldBinding {

	private JXDatePicker datePicker;

	public JXDatePickerDateFieldBinding(JXDatePicker datePicker, FormModel formModel, String formPropertyPath) {
		super(formModel, formPropertyPath);
		this.datePicker = datePicker;
	}

	protected void valueModelChanged(Object newValue) {
		datePicker.setDate((Date) newValue);
	}

	protected JComponent doBindControl() {
		datePicker.setDate((Date) getValue());

		if (getDateFormat() != null) {
			datePicker.setFormats(new String[] {getDateFormat()});
		}

		datePicker.addPropertyChangeListener("date", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				controlValueChanged(datePicker.getDate());
			}
		});
		
		return datePicker;
	}

	protected void enabledChanged() {
		datePicker.setEnabled(isEnabled());
	}

	protected void readOnlyChanged() {
		datePicker.setEditable(!isReadOnly());
	}

}
