/*
 * $Header: /usr/local/cvs/java-tools/environment/eclipse/code-templates.xml,v
 * 1.2 2004/03/31 18:20:53 keith Exp $ $Revision: 1749 $ $Date: 2004/03/31
 * 18:20:53 $
 * 
 * Copyright Computer Science Innovations (CSI), 2003. All rights reserved.
 */
package org.springframework.richclient.application.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.style.ToStringCreator;

/**
 * Application event that communicates lifecycle changes in application objects.
 * 
 * @author Keith Donald
 */
public class LifecycleApplicationEvent extends ApplicationEvent {
    private String eventType;

    public static final String CREATED = "lifecycleEvent.created";

    public static final String MODIFIED = "lifecycleEvent.modified";

    public static final String DELETED = "lifecycleEvent.deleted";

    public LifecycleApplicationEvent(String eventType, Object source) {
        super(source);
        this.eventType = eventType;
    }

    public Object getObject() {
        return getSource();
    }

    public boolean objectIs(Class clazz) {
        if (clazz.isAssignableFrom(getSource().getClass())) 
            return true;

        return false;
    }

    public String getEventType() {
        return eventType;
    }

    public String toString() {
        return new ToStringCreator(this).toString();
    }
}