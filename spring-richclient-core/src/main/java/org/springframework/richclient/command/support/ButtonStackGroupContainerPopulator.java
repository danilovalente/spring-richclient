package org.springframework.richclient.command.support;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import org.springframework.richclient.command.CommandGroupFactoryBean;

import com.jgoodies.forms.builder.ButtonStackBuilder;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Size;

/**
 * Creates a buttonstack: a panel with buttons that are vertically positioned.
 * 
 * @see org.springframework.richclient.command.support.ButtonBarGroupContainerPopulator
 * @see com.jgoodies.forms.builder.ButtonStackBuilder
 * 
 * @author jh
 */
public class ButtonStackGroupContainerPopulator extends SimpleGroupContainerPopulator
{
    private RowSpec rowSpec;

    private ButtonStackBuilder builder;

    private List buttons = new ArrayList();

    /**
     * Constructor. 
     */
    public ButtonStackGroupContainerPopulator() {
        super(new JPanel());
        builder = new ButtonStackBuilder((JPanel)getContainer());
    }

    /**
     * Define the minimum buttonsize of the buttonStack. This will actually
     * replace the rowSpec with a new one. 
     * 
     * @param minimumSize
     * @see #setRowSpec(RowSpec)
     */
    public void setMinimumButtonSize(Size minimumSize) 
    {
        this.rowSpec = new RowSpec(minimumSize);
    }

    /**
     * This allows to completely customize the rowspec.
     * 
     * @param rowSpec
     */
    public void setRowSpec(RowSpec rowSpec)
    {
        this.rowSpec = rowSpec;
    }

    /**
     * Set a custom columnSpec for the buttonstack.
     * 
     * @param columnSpec
     */
    public void setColumnSpec(ColumnSpec columnSpec)
    {
        if (columnSpec != null)
            builder.getLayout().setColumnSpec(1, columnSpec);
    }    
    
    /**
     * @return the created ButtonStack panel
     */
    public JPanel getButtonStack() {
        return builder.getPanel();
    }

    /**
     * @see SimpleGroupContainerPopulator#add(Component)
     */
    public void add(Component c) {
        buttons.add(c);
    }

    /**
     * @see SimpleGroupContainerPopulator#addSeparator()
     */
    public void addSeparator() {
        buttons.add(CommandGroupFactoryBean.SEPARATOR_MEMBER_CODE);
    }

    /**
     * @see SimpleGroupContainerPopulator#onPopulated()
     */
    public void onPopulated() {
        int length = buttons.size();
        for (int i = 0; i < length; i++) {
            Object o = buttons.get(i);
            if (o instanceof String && o == CommandGroupFactoryBean.SEPARATOR_MEMBER_CODE) {
                builder.addUnrelatedGap();
            }
            else if (o instanceof AbstractButton) {
                AbstractButton button = (AbstractButton)o;
                if (this.rowSpec != null) {
                    addCustomGridded(button);
                }
                else {
                    builder.addGridded(button);
                }
                if (i < buttons.size() - 1) {
                    builder.addRelatedGap();
                }
            }
        }
        builder.addGlue();
    }

    /**
     * Handle the custom RowSpec.
     * 
     * @param button
     */
    private void addCustomGridded(AbstractButton button) {
        builder.getLayout().appendRow(this.rowSpec);
        builder.getLayout().addGroupedRow(builder.getRow());
        button.putClientProperty("jgoodies.isNarrow", Boolean.TRUE);
        builder.add(button);
        builder.nextRow();
    }

}
