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
package org.springframework.richclient.command;

import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.richclient.util.Assert;

/**
 * A simple implementation of the {@link GroupMember} interface that manages normal commands that
 * can be associated with instances of {@link AbstractButton}s.
 *
 */
public class SimpleGroupMember extends GroupMember {

    private final CommandGroup parent;

    private final AbstractCommand command;

    /**
     * Creates a new {@code SimpleGroupMember} belonging to the given command group and wrapping
     * the given command.
     *
     * @param parentGroup The command group that this member belongs to.
     * @param command The command that this group member represents.
     * 
     * @throws IllegalArgumentException if either argument is null.
     * @throws InvalidGroupMemberException if the given command group does not support the type of
     * the given command.
     */
    public SimpleGroupMember(CommandGroup parentGroup, AbstractCommand command) {
        this.parent = parentGroup;
        this.command = command;
        
        if (!parentGroup.isAllowedMember(command)) {
            throw new InvalidGroupMemberException(command.getClass(), parentGroup.getClass());
        }
        
    }

    /**
     * Sets the enabled flag of the underlying command.
     */
    public void setEnabled(boolean enabled) {
        command.setEnabled(enabled);
    }

    
    protected void fill(GroupContainerPopulator containerPopulator, 
                        Object controlFactory,
                        CommandButtonConfigurer buttonConfigurer, 
                        List previousButtons) {
        
        Assert.required(containerPopulator, "containerPopulator");
        Assert.required(buttonConfigurer, "buttonConfigurer");
        
        if (controlFactory instanceof MenuFactory) {
            JMenuItem menu = findMenu(command, previousButtons);

            if (menu == null) {
                menu = command.createMenuItem(((MenuFactory)controlFactory), buttonConfigurer);
            }
            
            logger.debug("Adding menu item to container");
            containerPopulator.add(menu);
        }
        else if (controlFactory instanceof ButtonFactory) {
            AbstractButton button = findButton(command, previousButtons);
            
            if (button == null) {
                button = command.createButton(((ButtonFactory)controlFactory), buttonConfigurer);
            }
            
            logger.debug("Adding button to container");
            containerPopulator.add(button);
        }
        
    }

    /**
     * {@inheritDoc}
     */
    public boolean managesCommand(String commandId) {
        //FIXME isn't this supposed to recursively check subcommands if command is a commandgroup?
        
        if (commandId == null) {
            return false;
        }
        
        return commandId.equals(this.command.getId());
        
    }

    /**
     * Returns the underlying command, never null.
     * @return The underlying command.
     */
    public AbstractCommand getCommand() {
        return command;
    }

    /**
     * Searches the given list of {@link AbstractButton}s for one that is an instance of a 
     * {@link JMenuItem} and has the given command attached to it. If found, the menu item will be
     * removed from the list.
     *
     * @param attachedCommand The command that we are checking to see if it attached to any item in the list.
     * @param abstractButtons The collection of {@link AbstractButton}s that will be checked to
     * see if they have the given command attached to them. May be null or empty.
     *  
     * @return The element from the list that the given command is attached to, or null if no
     * such element could be found.
     * 
     */
    protected JMenuItem findMenu(AbstractCommand attachedCommand, List abstractButtons) {
        
        if (abstractButtons == null) {
            return null;
        }
        
        for (Iterator it = abstractButtons.iterator(); it.hasNext();) {
            AbstractButton button = (AbstractButton)it.next();
            if (button instanceof JMenuItem && attachedCommand.isAttached(button)) {
                it.remove();
                return (JMenuItem)button;
            }
        }
        return null;
    }

    /**
     * Searches the given list of {@link AbstractButton}s for one that is not an instance of a 
     * {@link JMenuItem} and has the given command attached to it. If found, the button will be
     * removed from the list.
     *
     * @param attachedCommand The command that we are checking to see if it attached to any item in the list.
     * @param abstractButtons The collection of {@link AbstractButton}s that will be checked to
     * see if they have the given command attached to them. May be null or empty.
     *  
     * @return The element from the list that the given command is attached to, or null if no
     * such element could be found.
     * 
     */
    protected AbstractButton findButton(AbstractCommand attachedCommand, List buttons) {
        
        if (buttons == null) {
            return null;
        }
        
        for (Iterator it = buttons.iterator(); it.hasNext();) {
            AbstractButton button = (AbstractButton)it.next();
            if (!(button instanceof JMenuItem) && attachedCommand.isAttached(button)) {
                it.remove();
                return button;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected void onAdded() {
        if (parent instanceof ExclusiveCommandGroup) {
            ((ExclusiveCommandGroup)parent).getSelectionController().add((ToggleCommand)command);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void onRemoved() {
        if (parent instanceof ExclusiveCommandGroup) {
            ((ExclusiveCommandGroup)parent).getSelectionController().remove((ToggleCommand)command);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return new ToStringCreator(this).append("command", command).toString();
    }
    
}
