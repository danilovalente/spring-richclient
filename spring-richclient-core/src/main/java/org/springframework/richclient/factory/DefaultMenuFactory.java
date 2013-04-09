/*
 * $Header$
 * $Revision: 1242 $
 * $Date: 2006-07-26 13:44:04 -0300 (Qua, 26 Jul 2006) $
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.factory;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * @author Keith Donald
 */
public class DefaultMenuFactory implements MenuFactory {

    public JMenu createMenu() {
        return new JMenu();
    }

    public JMenuItem createMenuItem() {
        return new JMenuItem();
    }

    public JCheckBoxMenuItem createCheckBoxMenuItem() {
        return new JCheckBoxMenuItem();
    }

    public JRadioButtonMenuItem createRadioButtonMenuItem() {
        return new JRadioButtonMenuItem();
    }

    public JPopupMenu createPopupMenu() {
        return new JPopupMenu();
    }

    public JMenuBar createMenuBar() {
        return new JMenuBar();
    }

}