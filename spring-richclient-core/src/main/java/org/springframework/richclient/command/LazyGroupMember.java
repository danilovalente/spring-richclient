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

import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.util.Assert;

/**
 * A {@link GroupMember} implementation that can be used as a placeholder for lazily initialized
 * commands.
 *
 */
public class LazyGroupMember extends GroupMember {
    
    private final CommandGroup parentGroup;

    private final String lazyCommandId;

    private boolean addedLazily;

    private GroupMember loadedMember;
    
    /**
     * Creates a new {@code LazyGroupMember} belonging to the given command group and managing
     * a lazily initialized command with the given id.
     *
     * @param parentGroup The command group that this member belongs to.
     * @param lazyCommandId The id of the command that this group member represents.
     * 
     * @throws IllegalArgumentException if either argument is null.
     */
    public LazyGroupMember(CommandGroup parentGroup, String lazyCommandId) {
        
        Assert.required(parentGroup, "parentGroup");
        Assert.required(lazyCommandId, "lazyCommandId");
        
        if (logger.isDebugEnabled()) {
            logger.debug("Lazy group member '" 
                         + lazyCommandId 
                         + "' instantiated for group '" 
                         + parentGroup.getId()
                         + "'");
        }
        
        this.parentGroup = parentGroup;
        this.lazyCommandId = lazyCommandId;
    }

    /**
     * Delegates this call to the lazily loaded member, but only if it has already been loaded.
     * Calling this method before the underlying member has beeen loaded will have no effect.
     */
    public void setEnabled(boolean enabled) {
        if (loadedMember != null) {
            loadedMember.setEnabled(enabled);
        }
    }

    protected void fill(GroupContainerPopulator parentContainerPopulator, 
                        Object controlFactory,
                        CommandButtonConfigurer buttonConfigurer, 
                        List previousButtons) {
        
        loadIfNecessary();
        
        if (loadedMember != null) {
            loadedMember.fill(parentContainerPopulator, controlFactory, buttonConfigurer, previousButtons);
        }
        
    }

    /**
     * Attempts to load the lazy command from the command registry of the parent command group, but
     * only if it hasn't already been loaded.
     */
    private void loadIfNecessary() {
        
        if (loadedMember != null) {
            return;
        }
        
        CommandRegistry commandRegistry = parentGroup.getCommandRegistry();

        Assert.isTrue(parentGroup.getCommandRegistry() != null, "Command registry must be set for group '"
                + parentGroup.getId() + "' in order to load lazy command '" + lazyCommandId + "'.");

        if (commandRegistry.containsCommandGroup(lazyCommandId)) {
            CommandGroup group = commandRegistry.getCommandGroup(lazyCommandId);
            loadedMember = new SimpleGroupMember(parentGroup, group);
        }
        else if (commandRegistry.containsActionCommand(lazyCommandId)) {
            ActionCommand command = commandRegistry.getActionCommand(lazyCommandId);
            loadedMember = new SimpleGroupMember(parentGroup, command);
        }
        else {
            
            if (logger.isWarnEnabled()) {
                logger.warn("Lazy command '" 
                            + lazyCommandId
                            + "' was asked to display; however, no backing command instance exists in registry.");
            }
            
        }

        if (addedLazily && loadedMember != null) {
            loadedMember.onAdded();
        }
        
    }

    /**
     * {@inheritDoc}
     */
    public boolean managesCommand(String commandId) {
        //FIXME isn't this supposed to recurse if command is a command group?
        return this.lazyCommandId.equals(commandId);
    }

    /**
     * {@inheritDoc}
     */
    protected void onAdded() {
        if (loadedMember != null) {
            loadedMember.onAdded();
        }
        else {
            addedLazily = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void onRemoved() {
        if (loadedMember != null) {
            loadedMember.onRemoved();
        }
        else {
            addedLazily = false;
        }
    }

}