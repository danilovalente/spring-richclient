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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.config.CommandButtonConfigurer;

/**
 * A member of a {@link CommandGroup}. 
 * 
 * <p>
 * A command group will generally consist of command objects but may also contain other objects 
 * such as separators or glue components used for spacing. This class is a simple wrapper around 
 * these various types of command group members. 
 * </p>
 *
 */
public abstract class GroupMember {

    /** Class logger, available to subclasses. */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Subclasses must implement this method to use the given container populator to add a 
     * GUI control component to a GUI container. The actual type of the GUI control will be 
     * determined by the type of the {@code controlFactory} provided, but it will generally be
     * a control that a command can be associated with, such as a button or menu item.
     *
     * @param containerPopulator The object responsible for populating a GUI container with 
     * an appropriate control component based on this instance. Must not be null.
     * 
     * @param controlFactory The factory for creating an appropriate GUI control that the underlying
     * command will be associated with.
     * 
     * @param buttonConfigurer The object that is to configure the newly created control component. 
     * 
     * @param previousButtons A list of {@link AbstractButton} instances that have already been
     * added to the container. May be null or empty.
     * 
     * @throws IllegalArgumentException if {@code containerPopulator}, {@code controlFactory}
     * or {@code buttonConfigurer} are null.
     */
    protected abstract void fill(GroupContainerPopulator containerPopulator,
                                 Object controlFactory,
                                 CommandButtonConfigurer buttonConfigurer, 
                                 List previousButtons);

    /**
     * Subclasses may override this method to allow their underlying command to be enabled or disabled.
     * @param enabled The enabled flag.
     */
    public abstract void setEnabled(boolean enabled);

    /**
     * Subclasses must implement this method to indicate whether or not they manage a command
     * with the given id. This method should also traverse nested commands if the command managed 
     * by this member is a {@link CommandGroup}.
     * 
     * @param commandId The id of the command to be checked for. May be null.
     * @return true if the command, or any of its nested commands, managed by this group member
     * has the given command id.
     */
    public abstract boolean managesCommand(String commandId);

    /**
     * Subclasses may override to return the command that they wrap.
     *
     * @return The wrapped command, possibly null.
     */
    public AbstractCommand getCommand() {
        return null;
    }

    /**
     * Subclasses may override to be notified when they are added to a command group.
     */
    protected void onAdded() {
        //do nothing
    }

    /**
     * Subclasses may override to be notified when they are removed from the group they belong to.
     */
    protected void onRemoved() {
        //do nothing
    }
    
}
