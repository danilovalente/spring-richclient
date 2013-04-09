package org.springframework.richclient.widget;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.ValidationResultsReporter;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.command.AbstractCommand;

import javax.swing.*;
import java.util.List;
import java.util.Collections;

/**
 * A decorator to add a {@link org.springframework.richclient.dialog.TitlePane} to a {@link org.springframework.richclient.form.Form}. Adds the commit command as a default widget
 * command to show.
 *
 * TODO check all widget functionality
 *
 * @author Jan Hoskens
 *
 */
public class TitledWidgetForm extends AbstractTitledWidget
{

    private AbstractForm form;

    /**
     * Set the inner form that needs decorating.
     */
    public void setForm(AbstractForm form)
    {
        this.form = form;
    }

    /**
     * Returns the form.
     */
    public AbstractForm getForm()
    {
        return form;
    }

    @Override
    public JComponent createWidgetContent()
    {
        newSingleLineResultsReporter(this);
        return getForm().getControl();
    }

    @Override
    public List<? extends AbstractCommand> getCommands()
    {
        return Collections.emptyList();
    }

    @Override
    public ValidationResultsReporter newSingleLineResultsReporter(Messagable messagable)
    {
        return getForm().newSingleLineResultsReporter(this);
    }

    @Override
    public void onAboutToHide()
    {
        super.onAboutToHide();
        // NOTE in future form should be a widget
        if (form instanceof Widget)
            ((Widget) form).onAboutToHide();
    }

    @Override
    public void onAboutToShow()
    {
        super.onAboutToShow();
        // NOTE in future form should be a widget
        if (form instanceof Widget)
            ((Widget) form).onAboutToShow();
    }
}

