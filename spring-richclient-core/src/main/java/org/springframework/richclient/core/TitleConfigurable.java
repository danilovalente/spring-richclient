/*
 * $Header$
 * $Revision: 2092 $
 * $Date: 2008-10-31 12:17:48 -0200 (Sex, 31 Out 2008) $
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.core;

/**
 * Implementing by application objects whose titles are configurable; for
 * example, dialogs or wizard pages.
 * 
 * @author Keith Donald
 */
public interface TitleConfigurable {

    /**
     * Sets the title.
     * 
     * @param title
     *            the title
     */
    public void setTitle(String title);
}