package org.springframework.rules.constraint;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.rules.constraint.property.AbstractPropertyConstraint;
import org.springframework.rules.reporting.TypeResolvable;

public abstract class AbstractTypeResolvablePropertyConstraint extends AbstractPropertyConstraint
        implements
        TypeResolvable,
        MessageSourceResolvable
{

    private String type;

    public AbstractTypeResolvablePropertyConstraint()
    {
    }

    public AbstractTypeResolvablePropertyConstraint(String propertyName)
    {
        super(propertyName);
    }

    public AbstractTypeResolvablePropertyConstraint(String propertyName, String type)
    {
        super(propertyName);
        setType(type);
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Object[] getArguments()
    {
        return null;
    }

    public String[] getCodes()
    {
        return new String[]{type};
    }

    public String getDefaultMessage()
    {
        return type;
    }
}

