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
package org.springframework.rules.reporting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.ReflectiveVisitorHelper;
import org.springframework.rules.constraint.Constraint;
import org.springframework.core.style.StylerUtils;
import org.springframework.rules.constraint.*;
import org.springframework.rules.constraint.property.CompoundPropertyConstraint;
import org.springframework.rules.constraint.property.ParameterizedPropertyConstraint;
import org.springframework.rules.constraint.property.PropertiesConstraint;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.constraint.property.PropertyValueConstraint;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @author Keith Donald
 */
public class DefaultMessageTranslator implements MessageTranslator,
		ObjectNameResolver {

	protected static final Log logger = LogFactory
			.getLog(DefaultMessageTranslator.class);

	private ReflectiveVisitorHelper visitorSupport = new ReflectiveVisitorHelper();

	private List args = new ArrayList();

	private MessageSource messages;

	private ObjectNameResolver objectNameResolver;

	private Locale locale;

	public DefaultMessageTranslator(MessageSource messages) {
		this(messages, null);
	}

	public DefaultMessageTranslator(MessageSource messages, ObjectNameResolver objectNameResolver) {
		this(messages, objectNameResolver, null);
	}

	public DefaultMessageTranslator(MessageSource messages,
			ObjectNameResolver objectNameResolver, Locale locale) {
		setMessageSource(messages);
		this.objectNameResolver = objectNameResolver;
		this.locale = locale;
	}

	public void setMessageSource(MessageSource messageSource) {
		Assert.notNull(messageSource, "messageSource is required");
		this.messages = messageSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.rules.reporting.MessageTranslator#getMessage(org.springframework.rules.constraint.Constraint)
	 */
	public String getMessage(Constraint constraint) {
		String objectName = null;
		if (constraint instanceof PropertyConstraint) {
			objectName = ((PropertyConstraint) constraint).getPropertyName();
		}
		String message = buildMessage(objectName, null, constraint);
		return message;
	}

	public String getMessage(String objectName, Constraint constraint) {
		return buildMessage(objectName, null, constraint);
	}

	public String getMessage(String objectName, Object rejectedValue,
			Constraint constraint) {
		return buildMessage(objectName, rejectedValue, constraint);
	}

	public String getMessage(String objectName, ValidationResults results) {
		return buildMessage(objectName, results.getRejectedValue(), results
				.getViolatedConstraint());
	}

	public String getMessage(PropertyResults results) {
		Assert.notNull(results, "No property results specified");
		return buildMessage(results.getPropertyName(), results
				.getRejectedValue(), results.getViolatedConstraint());
	}

	private String buildMessage(String objectName, Object rejectedValue,
			Constraint constraint) {
		StringBuffer buf = new StringBuffer(255);
		MessageSourceResolvable[] args = resolveArguments(constraint);
		if (logger.isDebugEnabled()) {
			logger.debug(StylerUtils.style(args));
		}
		if (objectName != null) {
			buf.append(resolveObjectName(objectName));
			buf.append(' ');
		}
		for (int i = 0; i < args.length - 1; i++) {
			MessageSourceResolvable arg = args[i];
			buf.append(messages.getMessage(arg, locale));
			buf.append(' ');
		}
		buf.append(messages.getMessage(args[args.length - 1], locale));
		buf.append(".");
		return buf.toString();
	}

	private MessageSourceResolvable[] resolveArguments(Constraint constraint) {
		args.clear();
		visitorSupport.invokeVisit(this, constraint);
		return (MessageSourceResolvable[]) args
				.toArray(new MessageSourceResolvable[0]);
	}

	void visit(CompoundPropertyConstraint rule) {
		visitorSupport.invokeVisit(this, rule.getPredicate());
	}

	void visit(PropertiesConstraint e) {
		add(
				getMessageCode(e.getConstraint()),
				new Object[] { resolveObjectName(e.getOtherPropertyName()) },
				e.toString());
	}

	void visit(ParameterizedPropertyConstraint e) {
		add(getMessageCode(e.getConstraint()),
				new Object[] { e.getParameter() }, e.toString());
	}

	public void add(String code, Object[] args, String defaultMessage) {
		MessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(
				new String[] { code }, args, defaultMessage);
		if (logger.isDebugEnabled()) {
			logger.debug("Adding resolvable: " + resolvable);
		}
		this.args.add(resolvable);
	}

	public String resolveObjectName(String objectName) {
		if(objectNameResolver != null)
			return objectNameResolver.resolveObjectName(objectName);
		return messages.getMessage(objectName, null,
				new DefaultBeanPropertyNameRenderer()
						.renderShortName(objectName), locale);
	}

	void visit(PropertyValueConstraint valueConstraint) {
		visitorSupport.invokeVisit(this, valueConstraint.getConstraint());
	}

    /**
     * Visit function for compound constraints like And/Or/XOr.
     * 
     * <p>syntax: <code>CONSTRAINT MESSAGE{code} CONSTRAINT</code></p>
     * 
     * @param compoundConstraint
     */
	void visit(CompoundConstraint compoundConstraint) {
		Iterator it = compoundConstraint.iterator();
        String compoundMessage = getMessageCode(compoundConstraint);
		while (it.hasNext()) {
			Constraint p = (Constraint) it.next();
			visitorSupport.invokeVisit(this, p);
			if (it.hasNext()) {
				add(compoundMessage, null, compoundMessage);
			}
		}
	}

	void visit(Not not) {
		add("not", null, "not");
		visitorSupport.invokeVisit(this, not.getConstraint());
	}

	// @TODO - consider standard visitor here...
	void visit(StringLengthConstraint constraint) {
		ClosureResultConstraint c = (ClosureResultConstraint) constraint
				.getPredicate();
		Object p = c.getPredicate();
		MessageSourceResolvable resolvable;
		if (p instanceof ParameterizedBinaryConstraint) {
			resolvable = handleParameterizedBinaryPredicate((ParameterizedBinaryConstraint) p);
		} else {
			resolvable = handleRange((Range) p);
		}
		Object[] args = new Object[] { resolvable };
		add(getMessageCode(constraint), args, constraint.toString());
	}

	void visit(ClosureResultConstraint c) {
		visitorSupport.invokeVisit(this, c.getPredicate());
	}

	private MessageSourceResolvable handleParameterizedBinaryPredicate(
			ParameterizedBinaryConstraint p) {
		MessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(
				new String[] { getMessageCode(p.getConstraint()) },
				new Object[] { p.getParameter() }, p.toString());
		return resolvable;
	}

	private MessageSourceResolvable handleRange(Range r) {
		MessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(
				new String[] { getMessageCode(r) }, new Object[] { r.getMin(),
						r.getMax() }, r.toString());
		return resolvable;
	}

	void visit(Constraint constraint) {
		if (constraint instanceof Range) {
			this.args.add(handleRange((Range) constraint));
		} else if (constraint instanceof ParameterizedBinaryConstraint) {
			this.args.add(handleParameterizedBinaryPredicate((ParameterizedBinaryConstraint)constraint));
		} else {
			add(getMessageCode(constraint), null, constraint.toString());
		}
	}

    /**
     * Determines the messageCode (key in messageSource) to look up.
     * If <code>TypeResolvable</code> is implemented, user can give a custom code,
     * otherwise the short className is used.
     * 
     * @param o
     * @return
     */
	protected String getMessageCode(Object o) {
		if (o instanceof TypeResolvable) {
			String type = ((TypeResolvable) o).getType();
			if (type != null) {
				return type;
			}
		}
		return ClassUtils.getShortNameAsProperty(o.getClass());
	}
}
