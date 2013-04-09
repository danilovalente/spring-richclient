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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ReflectiveVisitorHelper;
import org.springframework.rules.closure.Closure;
import org.springframework.rules.constraint.Constraint;
import org.springframework.core.style.ToStringCreator;
import org.springframework.rules.constraint.And;
import org.springframework.rules.constraint.ClosureResultConstraint;
import org.springframework.rules.constraint.Not;
import org.springframework.rules.constraint.Or;

/**
 * @author Keith Donald
 */
public class ValidationResultsCollector {
    protected static final Log logger = LogFactory
            .getLog(ValidationResultsCollector.class);

    protected ReflectiveVisitorHelper visitorSupport = new ReflectiveVisitorHelper();

    private ValidationResultsBuilder resultsBuilder;

    private ValidationResults results;

    private boolean collectAllErrors;

    private Object argument;

    public ValidationResultsCollector() {
    }

    public ValidationResults collect(final Object argument,
            final Constraint constraint) {
        this.resultsBuilder = new ValidationResultsBuilder() {
            public void constraintSatisfied() {
            }

            public void constraintViolated(Constraint constraint) {
                results = new ValueValidationResults(argument, constraint);
            }
        };
        if (results == null) {
            results = new ValueValidationResults(argument);
        }
        this.argument = argument;
        visitorSupport.invokeVisit(this, constraint);
        return results;
    }

    public void setCollectAllErrors(boolean collectAllErrors) {
        this.collectAllErrors = collectAllErrors;
    }

    protected ValidationResultsBuilder getResultsBuilder() {
        return resultsBuilder;
    }
    
    protected void setResultsBuilder(ValidationResultsBuilder resultsBuilder) {
        this.resultsBuilder = resultsBuilder;
    }

    protected void setArgument(Object argument) {
        this.argument = argument;
    }

    boolean visit(And and) {
        resultsBuilder.pushAnd();
        if (logger.isDebugEnabled()) {
            logger.debug("Starting [and]...");
        }
        boolean result = true;
        Iterator it = and.iterator();
        while (it.hasNext()) {
            boolean test = ((Boolean)visitorSupport.invokeVisit(
                    ValidationResultsCollector.this, it.next())).booleanValue();
            if (!test) {
                if (!collectAllErrors) {
                    resultsBuilder.pop(false);
                    return false;
                }

                if (result) {
                    result = false;
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Finished [and]...");
        }
        resultsBuilder.pop(result);
        return result;
    }

    boolean visit(Or or) {
        resultsBuilder.pushOr();
        if (logger.isDebugEnabled()) {
            logger.debug("Starting [or]...");
        }
        Iterator it = or.iterator();
        while (it.hasNext()) {
            boolean result = ((Boolean)visitorSupport.invokeVisit(
                    ValidationResultsCollector.this, it.next())).booleanValue();
            if (result) {
                resultsBuilder.pop(result);
                return true;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Finished [or]...");
        }
        resultsBuilder.pop(false);
        return false;
    }

    Boolean visit(Not not) {
        resultsBuilder.pushNot();
        if (logger.isDebugEnabled()) {
            logger.debug("Starting [not]...");
        }
        Boolean result = (Boolean)visitorSupport.invokeVisit(this, not
                .getConstraint());
        if (logger.isDebugEnabled()) {
            logger.debug("Finished [not]...");
        }
        resultsBuilder.pop(result.booleanValue());
        return result;
    }

    Boolean visit(ClosureResultConstraint ofConstraint) {
        Closure f = ofConstraint.getFunction();
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking function with argument " + argument);
        }
        setArgument(f.call(argument));
        return (Boolean)visitorSupport.invokeVisit(this, ofConstraint
                .getPredicate());
    }

    boolean visit(Constraint constraint) {
        if (logger.isDebugEnabled()) {
            logger.debug("Testing constraint [" + constraint + "] with argument '"
                    + argument + "']");
        }
        boolean result = constraint.test(argument);
        result = applyAnyNegation(result);
        if (!result) {
            resultsBuilder.push(constraint);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Constraint [" + constraint + "] "
                    + (result ? "passed" : "failed"));
        }
        return result;
    }

    protected boolean applyAnyNegation(boolean result) {
        boolean negated = resultsBuilder.negated();
        if (logger.isDebugEnabled()) {
            if (negated) {
                logger.debug("[negate result]");
            }
            else {
                logger.debug("[no negation]");
            }
        }
        return negated ? !result : result;
    }

    public String toString() {
        return new ToStringCreator(this).append("collectAllErrors",
                collectAllErrors).append("validationResultsBuilder",
                resultsBuilder).toString();
    }

}