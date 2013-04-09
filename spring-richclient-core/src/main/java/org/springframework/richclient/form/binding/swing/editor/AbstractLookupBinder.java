package org.springframework.richclient.form.binding.swing.editor;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.util.RcpSupport;
import org.springframework.richclient.widget.editor.DefaultDataEditorWidget;
import org.springframework.util.Assert;

import javax.swing.*;
import java.util.Map;

public abstract class AbstractLookupBinder implements Binder
{
    private int autoPopupDialog = AbstractLookupBinding.AUTOPOPUPDIALOG_NO_UNIQUE_MATCH;
    private boolean revertValueOnFocusLost = true;
    private String selectDialogId = AbstractLookupBinding.DEFAULT_SELECTDIALOG_ID;
    private String selectDialogCommandId = AbstractLookupBinding.DEFAULT_SELECTDIALOG_COMMAND_ID;
    private final String dataEditorId;
    private String dataEditorViewCommandId;
    private Object filter;
    private boolean enableViewCommand;
    private boolean loadDetailedObject = false;


    public boolean isLoadDetailedObject()
    {
        return loadDetailedObject;
    }


    public void setLoadDetailedObject(boolean loadDetailedObject)
    {
        this.loadDetailedObject = loadDetailedObject;
    }

    public AbstractLookupBinder(String dataEditorId)
    {
        this.dataEditorId = dataEditorId;
        enableViewCommand = false;
    }

    public void setAutoPopupDialog(int autoPopupDialog)
    {
        this.autoPopupDialog = autoPopupDialog;
    }

    public void setRevertValueOnFocusLost(boolean revertValueOnFocusLost)
    {
        this.revertValueOnFocusLost = revertValueOnFocusLost;
    }

    public void setSelectDialogId(String selectDialogId)
    {
        this.selectDialogId = selectDialogId;
    }

    public void setSelectDialogCommandId(String selectDialogCommandId)
    {
        this.selectDialogCommandId = selectDialogCommandId;
    }

    public Binding bind(FormModel formModel, String formPropertyPath, Map context)
    {
        AbstractLookupBinding referableBinding = getLookupBinding(formModel, formPropertyPath, context);
        referableBinding.setAutoPopupdialog(getAutoPopupDialog());
        referableBinding.setRevertValueOnFocusLost(isRevertValueOnFocusLost());
        referableBinding.setSelectDialogCommandId(getSelectDialogCommandId());
        referableBinding.setSelectDialogId(getSelectDialogId());
        referableBinding.setDataEditorViewCommandId(dataEditorViewCommandId);
        referableBinding.setEnableViewCommand(enableViewCommand);
        referableBinding.setFilter(filter);
        referableBinding.setLoadDetailedObject(loadDetailedObject);
        return referableBinding;
    }

    protected abstract AbstractLookupBinding getLookupBinding(FormModel formModel, String formPropertyPath, Map context);

    public Binding bind(JComponent control, FormModel formModel, String formPropertyPath, Map context)
    {
        throw new UnsupportedOperationException("This binder needs a special component that cannot be given");
    }

    protected int getAutoPopupDialog()
    {
        return autoPopupDialog;
    }

    protected DefaultDataEditorWidget getDataEditor()
    {
        Object dataEditor = RcpSupport.getBean(dataEditorId);
        Assert.isInstanceOf(DefaultDataEditorWidget.class, dataEditor);
        return (DefaultDataEditorWidget) dataEditor;
    }

    protected boolean isRevertValueOnFocusLost()
    {
        return revertValueOnFocusLost;
    }

    protected String getSelectDialogCommandId()
    {
        return selectDialogCommandId;
    }

    protected String getSelectDialogId()
    {
        return selectDialogId;
    }

    public void setDataEditorViewCommandId(String dataEditorViewCommandId)
    {
        this.dataEditorViewCommandId = dataEditorViewCommandId;
    }

    public void setEnableViewCommand(boolean enableViewCommand)
    {
        this.enableViewCommand = enableViewCommand;
    }

    public void setFilter(Object filter)
    {
        this.filter = filter;
    }

    public String getDataEditorViewCommandId()
    {
        return dataEditorViewCommandId;
    }

    public Object getFilter()
    {
        return filter;
    }

    public boolean isEnableViewCommand()
    {
        return enableViewCommand;
    }
}
