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
package org.springframework.binding.form.support;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.Converter;
import org.springframework.binding.form.BindingErrorMessageProvider;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.support.TestBean;
import org.springframework.binding.support.TestPropertyChangeListener;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.binding.validation.ValidationResultsModel;
import org.springframework.binding.validation.Validator;
import org.springframework.binding.validation.support.DefaultValidationMessage;
import org.springframework.binding.validation.support.DefaultValidationResults;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.core.Severity;

import java.util.Set;

/**
 * Tests for @link DefaultFormModel
 *
 * @author Oliver Hutchison
 */
public class DefaultFormModelTests extends AbstractFormModelTests {

    protected AbstractFormModel getFormModel(Object formObject) {
        return new TestDefaultFormModel(formObject);
    }

    protected AbstractFormModel getFormModel(BeanPropertyAccessStrategy pas, boolean buffering) {
        return new TestDefaultFormModel(pas, buffering);
    }

    public void testPropertyChangeCausesValidation() {
        DefaultFormModel fm = (DefaultFormModel) getFormModel(new TestBean());
        TestValidator v = new TestValidator();
        fm.setValidator(v);
        TestConversionService cs = new TestConversionService();
        cs.executer = new ConversionExecutor(String.class, String.class, new CopiedPublicNoOpConverter(String.class, String.class));
        fm.setConversionService(cs);
        ValueModel vm = fm.getValueModel("simpleProperty");
        // starting at 2: constructing a formmodel + creating valueModel
        int expectedCount = 2;
        assertEquals(expectedCount++, v.count);

        vm.setValue("1");
        assertEquals(expectedCount, v.count);

        // no change in value, no validation triggered.
        vm.setValue("1");
        assertEquals(expectedCount++, v.count);

        vm.setValue(null);
        assertEquals(expectedCount++, v.count);

        vm = fm.getValueModel("simpleProperty", Integer.class);
        vm.setValue("1");
        assertEquals(expectedCount++, v.count);

        vm.setValue("2");
        assertEquals(expectedCount++, v.count);
    }

    public void testValidationMessages() {
    	DefaultFormModel fm = (DefaultFormModel) getFormModel(new TestBean());
        ValidationResultsModel r = fm.getValidationResults();
        TestValidator v = new TestValidator();
        fm.setValidator(v);
        ValueModel vm = fm.getValueModel("simpleProperty");
        // starting at 2: constructing a formmodel + creating valueModel
        int expectedCount = 2;

        assertEquals(expectedCount++, v.count);
        assertEquals(0, r.getMessageCount());

        v.results = getValidationResults("message1");
        vm.setValue("1");
        assertEquals(expectedCount++, v.count);
        assertEquals(1, r.getMessageCount());
        assertContainsMessage("message1", r.getMessages());

        v.results = getValidationResults("message2");
        vm.setValue("2");
        assertEquals(expectedCount, v.count);
        assertEquals(1, r.getMessageCount());
        assertContainsMessage("message2", r.getMessages());
        // this will cause a binding exception
        vm.setValue(new Object());
        assertEquals(expectedCount++, v.count);
        assertEquals(2, r.getMessageCount());
        assertContainsMessage("message2", r.getMessages());

        // this will clear the binding exception
        vm.setValue("3");
        assertEquals(expectedCount++, v.count);
        assertEquals(1, r.getMessageCount());
        assertContainsMessage("message2", r.getMessages());

        fm.validate();
        assertEquals(expectedCount++, v.count);
        assertEquals(1, r.getMessageCount());
        assertContainsMessage("message2", r.getMessages());
    }

    public void testRaiseClearValidationMessage() {
    	DefaultFormModel fm = (DefaultFormModel) getFormModel(new TestBean());
        ValidationResultsModel r = fm.getValidationResults();
        TestValidator v = new TestValidator();
        fm.setValidator(v);
        ValueModel vm = fm.getValueModel("simpleProperty");
        // starting at 2: constructing a formmodel + creating valueModel
        int expectedCount = 2;

        final DefaultValidationMessage message1 = new DefaultValidationMessage("simpleProperty", Severity.ERROR, "1");

        fm.raiseValidationMessage(message1);
        assertEquals(expectedCount++, v.count);
        assertEquals(1, r.getMessageCount());
        assertContainsMessage("1", r.getMessages());

        fm.clearValidationMessage(message1);
        assertEquals(0, r.getMessageCount());

        fm.raiseValidationMessage(message1);
        fm.setValidating(false);
        assertEquals(0, r.getMessageCount());

        fm.setValidating(true);
        assertEquals(expectedCount++, v.count);
        assertEquals(1, r.getMessageCount());

        v.results = getValidationResults("2");
        vm.setValue("3");
        assertEquals(expectedCount++, v.count);
        assertEquals(2, r.getMessageCount());

        fm.clearValidationMessage(message1);
        assertEquals(1, r.getMessageCount());
    }

    public void testChangingValidatingClearsMessagesOrValidates() {
        DefaultFormModel fm = (DefaultFormModel) getFormModel(new TestBean());
        ValidationResultsModel r = fm.getValidationResults();
        TestValidator v = new TestValidator();
        // starting at 2: constructing a formmodel + creating valueModel
        int expectedCount = 2;
        v.results = getValidationResults("message1");
        fm.setValidator(v);

        ValueModel vm = fm.getValueModel("simpleProperty");
        assertEquals(expectedCount, v.count);
        assertEquals(1, r.getMessageCount());

        fm.setValidating(false);
        assertEquals(expectedCount++, v.count);
        assertEquals(0, r.getMessageCount());

        fm.setValidating(true);
        assertEquals(expectedCount, v.count);
        assertEquals(1, r.getMessageCount());

        // this will cause a binding exception
        vm.setValue(new Object());
        assertEquals(expectedCount, v.count);
        assertEquals(2, r.getMessageCount());

        fm.setValidating(false);
        assertEquals(expectedCount, v.count);
        assertEquals(0, r.getMessageCount());

        // this will cause a another binding exception
        fm.getValueModel("listProperty").setValue(new Object());
        assertEquals(expectedCount, v.count);
        assertEquals(0, r.getMessageCount());

        vm.setValue("test");
        assertEquals(expectedCount++, v.count);
        assertEquals(0, r.getMessageCount());

        fm.setValidating(true);
        assertEquals(expectedCount++, v.count);
        assertEquals(2, r.getMessageCount());
    }

    public void testSetThrowsExceptionRaisesValidationMessage() {
        final ErrorBean errorBean = new ErrorBean();
        DefaultFormModel fm = (DefaultFormModel) getFormModel(errorBean);
        final ValueModel vm = fm.getValueModel("error");

        vm.setValue("test");
        assertEquals(1, fm.getValidationResults().getMessageCount());

        errorBean.errorToThrow = null;
        vm.setValue("test");
        assertEquals(0, fm.getValidationResults().getMessageCount());
    }

    public void testTypeConversionThrowsExceptionRaisesValidationMessage() {
        DefaultFormModel fm = (DefaultFormModel) getFormModel(new TestBean());
        TestConversionService cs = new TestConversionService();
        cs.executer = new ConversionExecutor(String.class, Integer.class, new ExceptionConverter(String.class,
                Integer.class));
        fm.setConversionService(cs);
        final ValueModel vm = fm.getValueModel("simpleProperty", Integer.class);

        vm.setValue("test");
        assertEquals(1, fm.getValidationResults().getMessageCount());
    }

    public void testValidatingEvents() {
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(ValidatingFormModel.VALIDATING_PROPERTY);
        DefaultFormModel fm = (DefaultFormModel) getFormModel(new TestBean());
        fm.addPropertyChangeListener(ValidatingFormModel.VALIDATING_PROPERTY, pcl);
        assertTrue(fm.isEnabled());

        fm.setValidating(false);
        assertTrue(!fm.isValidating());
        assertEquals(1, pcl.eventCount());

        fm.setValidating(false);
        assertTrue(!fm.isValidating());
        assertEquals(1, pcl.eventCount());

        fm.setValidating(true);
        assertTrue(fm.isValidating());
        assertEquals(2, pcl.eventCount());

        fm.setEnabled(true);
        assertTrue(fm.isValidating());
        assertEquals(2, pcl.eventCount());
    }

    public void testReadOnlyRevert() {
        FormModel fm = getFormModel(new TestBean());
        fm.getValueModel("readOnly");
        fm.revert();

        // no additional asserts, this test should just not throw an exception!
    }

    public void testDefaultFormModelFromValueModel() throws Exception {
        TestBean testBean = new TestBean();
        ValueModel valueModel = new ValueHolder(testBean);
        DefaultFormModel model = new DefaultFormModel(valueModel);
        assertEquals(testBean, model.getFormObject());
    }

    private DefaultValidationResults getValidationResults(String message) {
        DefaultValidationResults res = new DefaultValidationResults();
        res.addMessage("simpleProperty", Severity.ERROR, message);
        return res;
    }

    private void assertContainsMessage(String message, Set messages) {
        assertTrue("Set of messages does not contain expected message '" + message + "'", messages
                .contains(new DefaultValidationMessage("simpleProperty", Severity.ERROR, message)));
    }

    public static class TestValidator implements Validator {

        public ValidationResults results = new DefaultValidationResults();

        public int count;

        public ValidationResults validate(Object object) {
            count++;
            return results;
        }
    }

    public class ErrorBean {
        public RuntimeException errorToThrow = new UnsupportedOperationException();

        public Object getError() {
            return null;
        }

        public void setError(Object error) {
            if (errorToThrow != null) {
                throw errorToThrow;
            }
        }
    }

    private static class TestDefaultFormModel extends DefaultFormModel {
        public TestDefaultFormModel(Object bean) {
            super(bean, false);
        }

        public TestDefaultFormModel(BeanPropertyAccessStrategy pas, boolean buffering) {
            super(pas, buffering);
        }

        public void init() {
            super.init();
            setValidator(new TestValidator());
            setBindingErrorMessageProvider(new BindingErrorMessageProvider() {

                public ValidationMessage getErrorMessage(FormModel formModel, String propertyName,
                        Object valueBeingSet, Exception e) {
                    return new DefaultValidationMessage(propertyName, Severity.ERROR, "");
                }

            });
        }
    }

    private static class ExceptionConverter implements Converter {

        private final Class sourceClass;

        private final Class targetClass;

        public ExceptionConverter(Class sourceClass, Class targetClass) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
        }

        public Object convert(Object source, Class targetClass, ConversionContext context) throws ConversionException {
            throw new ConversionException("test", targetClass);
        }

        public Class[] getSourceClasses() {
            return new Class[] { sourceClass };
        }

        public Class[] getTargetClasses() {
            return new Class[] { targetClass };
        }

    }
}
