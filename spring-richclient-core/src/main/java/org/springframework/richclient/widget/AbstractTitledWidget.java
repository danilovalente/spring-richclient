package org.springframework.richclient.widget;

import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.util.RcpSupport;
import org.springframework.richclient.dialog.TitlePane;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.form.ValidationResultsReporter;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

public abstract class AbstractTitledWidget extends AbstractWidget implements TitledWidget
{

    private Message description = new DefaultMessage(RcpSupport.getMessage(
            "titledWidget", "defaultMessage", RcpSupport.TEXT), Severity.INFO);
    private TitlePane titlePane = new TitlePane(1);

    private JComponent component;

    private String id;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    public void setBeanName(String beanName)
    {
        setId(beanName);
    }

    public boolean isEnabled()
    {
        return false;
    }

    public void setEnabled(boolean enabled)
    {
    }

    public void setTitle(String title)
    {
        this.titlePane.setTitle(title);
    }

    public void setImage(Image image)
    {
        this.titlePane.setImage(image);
    }

    public void setMessage(Message message)
    {
        if (message != null)
            titlePane.setMessage(message);
        else
            titlePane.setMessage(getDescription());
    }

    public ValidationResultsReporter newSingleLineResultsReporter(Messagable messagable)
    {
        return null;
    }

    protected Message getDescription()
    {
        return description;
    }

    public void setDescription(String longDescription)
    {
        this.description = new DefaultMessage(longDescription);
        setMessage(this.description);
    }

    public void setCaption(String shortDescription)
    {
        // TODO needed to comply to interface DescriptionConfigurable where will this end up?
    }

    /**
     * Lazy creation of component
     * <p/>
     * {@inheritDoc}
     */
    public final JComponent getComponent()
    {
        if (component == null)
            component = createComponent();

        return component;
    }

    /**
     * @return JComponent with titlePane, widgetContent and border.
     */
    private JComponent createComponent()
    {
        JPanel titlePaneContainer = new JPanel(new BorderLayout());
        titlePaneContainer.add(titlePane.getControl());
        titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);

        JPanel pageControl = new JPanel(new BorderLayout());
        pageControl.add(titlePaneContainer, BorderLayout.NORTH);
        JComponent content = createWidgetContent();
        GuiStandardUtils.attachDialogBorder(content);
        pageControl.add(content);

        return pageControl;
    }

    public abstract JComponent createWidgetContent();

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        this.titlePane.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String txt, PropertyChangeListener listener)
    {
        this.titlePane.addPropertyChangeListener(txt, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        this.titlePane.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String txt, PropertyChangeListener listener)
    {
        this.titlePane.removePropertyChangeListener(txt, listener);
    }
}
