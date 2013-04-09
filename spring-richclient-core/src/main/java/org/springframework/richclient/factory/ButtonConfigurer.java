/*
 * $Header$
 * $Revision: 1998 $
 * $Date: 2008-03-06 09:18:03 -0300 (Qui, 06 Mar 2008) $
 *
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.factory;

import javax.swing.AbstractButton;

/**
 * Marker for button configurers.
 *
 * @author Keith Donald
 */
public interface ButtonConfigurer {

	/**
	 * Configure the given button.
	 *
	 * @param button The button that needs to be configured.
	 * @return the configured button.
	 */
    public AbstractButton configure(AbstractButton button);
}