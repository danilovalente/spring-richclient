/*
 * Copyright 2002-2005 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;

import org.springframework.binding.form.FieldMetadata;
import org.springframework.binding.support.TestPropertyChangeListener;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * 
 * @author  Oliver Hutchison
 */
public class DefaultFieldMetadataTests extends SpringRichTestCase {

    public void testDirtyChangeTrackingOnValueChange() {
        FormModelMediatingValueModel vm = new FormModelMediatingValueModel(new ValueHolder("v1"));
        DefaultFormModel fm = new DefaultFormModel(new Object());
        DefaultFieldMetadata m = new DefaultFieldMetadata(fm, vm, Object.class, false, null);
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(FieldMetadata.DIRTY_PROPERTY);
        m.addPropertyChangeListener(FieldMetadata.DIRTY_PROPERTY, pcl);        
        assertTrue(!m.isDirty());
        assertEquals(0, pcl.eventCount());
        
        vm.setValue("v1");
        assertEquals(0, pcl.eventCount());
        assertTrue(!m.isDirty());
        
        vm.setValue("v2");
        assertEquals(1, pcl.eventCount());
        assertTrue(m.isDirty());
        
        vm.setValue("v3");
        assertEquals(1, pcl.eventCount());
        assertTrue(m.isDirty());
        
        vm.setValue("v1");
        assertEquals(2, pcl.eventCount());
        assertTrue(!m.isDirty());
        
        vm.setValue(null);
        assertEquals(3, pcl.eventCount());
        assertTrue(m.isDirty());        
        
        vm.clearDirty();
        assertEquals(4, pcl.eventCount());
        assertTrue(!m.isDirty());
        
        vm.setValue(null);
        assertEquals(4, pcl.eventCount());
        assertTrue(!m.isDirty());    
    }
    
    public void testEnabledChange() {
        FormModelMediatingValueModel vm = new FormModelMediatingValueModel(new ValueHolder("v1"));
        DefaultFormModel fm = new DefaultFormModel(new Object());
        DefaultFieldMetadata m = new DefaultFieldMetadata(fm, vm, Object.class, false, null);
        TestPropertyChangeListener pcl = new TestPropertyChangeListener(FieldMetadata.ENABLED_PROPERTY);
        m.addPropertyChangeListener(FieldMetadata.ENABLED_PROPERTY, pcl);
        assertEquals(0, pcl.eventCount());
        assertTrue(m.isEnabled());        
        
        m.setEnabled(true);
        assertEquals(0, pcl.eventCount());
        assertTrue(m.isEnabled());
        
        m.setEnabled(false);
        assertEquals(1, pcl.eventCount());
        assertTrue(!m.isEnabled());
        
        fm.setEnabled(false);
        assertEquals(1, pcl.eventCount());
        assertTrue(!m.isEnabled());
        
        m.setEnabled(true);
        assertEquals(1, pcl.eventCount());
        assertTrue(!m.isEnabled());
        
        fm.setEnabled(true);
        assertEquals(2, pcl.eventCount());
        assertTrue(m.isEnabled());        
    }
  
  
    public void testCustomMetaData() {
        final FormModelMediatingValueModel vm = new FormModelMediatingValueModel(new ValueHolder("v1"));
        final DefaultFormModel fm = new DefaultFormModel(new Object());
        final Map customMeta = new HashMap();
        customMeta.put("custom1", "a");
        customMeta.put("custom2", "b");
        final DefaultFieldMetadata m = new DefaultFieldMetadata(fm, vm, Object.class, false, customMeta);
  
        assertNull(m.getUserMetadata("custom3"));
        assertEquals("a", m.getUserMetadata("custom1"));
        assertEquals("b", m.getUserMetadata("custom2"));
    }
  
    public void testMutableCustomMetaData() {
        final FormModelMediatingValueModel vm = new FormModelMediatingValueModel(new ValueHolder("v1"));
        final DefaultFormModel fm = new DefaultFormModel(new Object());
        final DefaultFieldMetadata m = new DefaultFieldMetadata(fm, vm, Object.class, false, null);
        final String name = "customProperty";
        final String othername = "othername";
        final TestPropertyChangeListener pcl = new TestPropertyChangeListener(name);
        final TestPropertyChangeListener pclOther = new TestPropertyChangeListener(othername);

        m.addPropertyChangeListener(name, pcl);
        m.addPropertyChangeListener(othername, pclOther);
        
        assertNull(m.getUserMetadata(name));
        pcl.assertEventCount(0);
        
        m.setUserMetadata(name, "a");
        assertEquals("a", m.getUserMetadata(name));
        pcl.assertEventCount(1);
        assertNull(pcl.lastEvent().getOldValue());
        assertEquals("a", pcl.lastEvent().getNewValue());
      
        m.setUserMetadata(name, "b");
        assertEquals("b", m.getUserMetadata(name));
        pcl.assertEventCount(2);
        assertEquals("a", pcl.lastEvent().getOldValue());
        assertEquals("b", pcl.lastEvent().getNewValue());
      
        m.setUserMetadata(name, "b");
        pcl.assertEventCount(2);
      
        m.setUserMetadata(name, null);
        assertNull(m.getUserMetadata(name));
        pcl.assertEventCount(3);
        assertEquals("b", pcl.lastEvent().getOldValue());
        assertNull(pcl.lastEvent().getNewValue());

        pclOther.assertEventCount(0);
        m.setUserMetadata(othername, "1");
        pcl.assertEventCount(3);
        pclOther.assertEventCount(1);
      
        m.clearUserMetadata();
      
        assertNull(m.getUserMetadata(name));
        assertNull(m.getUserMetadata(othername));
        pcl.assertEventCount(3);
        pclOther.assertEventCount(2);
        assertEquals("1", pclOther.lastEvent().getOldValue());
        assertEquals(null, pclOther.lastEvent().getNewValue());
    }
}
