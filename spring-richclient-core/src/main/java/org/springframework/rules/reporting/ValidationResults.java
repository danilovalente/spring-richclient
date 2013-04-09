/*
 * $Header$
 * $Revision: 2095 $
 * $Date: 2008-10-31 12:47:23 -0200 (Sex, 31 Out 2008) $
 *
 * Copyright Computer Science Innovations (CSI), 2003. All rights reserved.
 */
package org.springframework.rules.reporting;

import org.springframework.rules.constraint.Constraint;
import org.springframework.richclient.core.Severity;

/**
 * @author  Keith Donald
 */
public interface ValidationResults {

	/**
	 * @return Returns the rejectedValue.
	 */
	public Object getRejectedValue();

	/**
	 * @return Returns the violatedConstraint.
	 */
	public Constraint getViolatedConstraint();

	/**
	 * @return Returns the violatedCount.
	 */
	public int getViolatedCount();

	/**
	 * @return Returns the severity.
	 */
	public Severity getSeverity();
}