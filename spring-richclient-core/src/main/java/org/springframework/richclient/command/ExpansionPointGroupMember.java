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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.util.Assert;

/**
 * A collection of {@link GroupMember}s that represent a subsection of a {@link CommandGroup}. 
 *
 */
public class ExpansionPointGroupMember extends GroupMember {

    private static final String DEFAULT_EXPANSION_POINT_NAME = "default";

    private final HashSet members = new LinkedHashSet(5);

    private final String expansionPointName;

    private boolean leadingSeparator;

    private boolean endingSeparator;

    /**
     * Creates a new {@code ExpansionPointGroupMember} with a default name.
     */
    protected ExpansionPointGroupMember() {
        expansionPointName = DEFAULT_EXPANSION_POINT_NAME;
    }

    /**
     * Creates a new {@code ExpansionPointGroupMember} with the given name.
     *
     * @param expansionPointName The name of the expansion point. Must not be null.
     * 
     * @throws IllegalArgumentException if {@code expansionPointName} is null.
     */
    protected ExpansionPointGroupMember(String expansionPointName) {
        Assert.required(expansionPointName, "expansionPointName");
        this.expansionPointName = expansionPointName;
    }

    /**
     * Returns true if the visual representation of this expansion point will include a leading
     * separator.
     *
     * @return true for a leading separator.
     */
    public boolean isLeadingSeparator() {
        return leadingSeparator;
    }

    /**
     * Sets the flag that indicates whether or not the visual representation of this expansion
     * point will display a leading separator.
     *
     * @param leadingSeparator Set to true to display a leading separator.
     */
    public void setLeadingSeparator(boolean leadingSeparator) {
        this.leadingSeparator = leadingSeparator;
    }

    /**
     * Returns true if the visual representation of this expansion point will include a trailing
     * separator.
     *
     * @return true for a trailing separator.
     */
    public boolean isEndingSeparator() {
        return endingSeparator;
    }

    /**
     * Sets the flag that indicates whether or not the visual representation of this expansion
     * point will display a trailing separator.
     *
     * @param endingSeparator Set to true to display a trailing separator.
     */
    public void setEndingSeparator(boolean endingSeparator) {
        this.endingSeparator = endingSeparator;
    }

    /**
     * Returns the name of this expansion point.
     *
     * @return The expansion point name, never null.
     */
    public String getExpansionPointName() {
        return expansionPointName;
    }

    /**
     * Attempts to add the given member to this expansion point. The member will not be added if
     * an equivalent entry (according to its equals() method) already exists. If added, the member's
     * {@link GroupMember#onAdded()} method will be called.
     *
     * @param member The member to be added. Must not be null.
     * 
     * @throws IllegalArgumentException if {@code member} is null.
     */
    protected void add(GroupMember member) {
        
        Assert.required(member, "member");
        
        if (members.add(member)) {
            member.onAdded();
        }
        
    }

    /**
     * If the given member belongs to this exponsion point, it will be removed. Its 
     * {@link GroupMember#onRemoved()} method will be called.  
     *
     * @param member The member that is to be removed.
     */
    public void remove(GroupMember member) {
        if (members.remove(member)) {
            member.onRemoved();
        }
    }

    /**
     * Removes all the group members from this expansion point.
     */
    protected void clear() {
        members.clear();
    }

    /**
     * Adds each member of this expansion point to a GUI container using the given container 
     * populator. Leading and trailing separators will also be added as determined by the 
     * appropriate flags set on this instance.
     * 
     * {@inheritDoc}
     */
    protected void fill(GroupContainerPopulator containerPopulator, 
                        Object controlFactory, 
                        CommandButtonConfigurer configurer,
                        List previousButtons) {
        
        Assert.required(containerPopulator, "containerPopulator");
        Assert.required(controlFactory, "controlFactory");
        Assert.required(configurer, "configurer");
        
        if (members.size() > 0 && isLeadingSeparator()) {
            containerPopulator.addSeparator();
        }

        for (Iterator iterator = members.iterator(); iterator.hasNext();) {
            GroupMember member = (GroupMember)iterator.next();
            member.fill(containerPopulator, controlFactory, configurer, previousButtons);
        }

        if (members.size() > 0 && isEndingSeparator()) {
            containerPopulator.addSeparator();
        }
        
    }

    /**
     * Returns the group member that manages the command with the given id, or null if none of the
     * members in this expansion point manage a command with that id.
     * 
     * @param commandId The id of the command whose managing member is to be returned.
     * @return The group member that manages the command with the given id, or null.
     */
    public GroupMember getMemberFor(String commandId) {
        for (Iterator it = members.iterator(); it.hasNext();) {
            GroupMember member = (GroupMember)it.next();
            if (member.managesCommand(commandId)) {
                return member;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean managesCommand(String commandId) {
        for (Iterator iterator = members.iterator(); iterator.hasNext();) {
            GroupMember member = (GroupMember)iterator.next();
            if (member.managesCommand(commandId))
                return true;
        }

        return false;
    }

    /**
     * Returns true if this expansion point has no members.
     * @return true if this expansion point has no members.
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Default implementation, performs no operation.
     */
    public void setEnabled(boolean enabled) {
        //do nothing
    }

}
