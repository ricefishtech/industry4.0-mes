/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.view.internal.ribbon.templates.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonGroup;
import com.qcadoo.view.internal.ribbon.templates.RibbonTemplateParameters;

public class RibbonTemplate {

    private final String name;

    private final String plugin;

    private final List<TemplateRibbonGroup> groups = new ArrayList<TemplateRibbonGroup>();

    public RibbonTemplate(final String plugin, final String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public void addTemplateGroup(final TemplateRibbonGroup group) {
        if (groups.contains(group)) {
            throw new IllegalStateException("group '" + group.getName() + "' already exists in template '" + name + "'");
        }
        groups.add(group);
    }

    public void removeTemplateGroup(final TemplateRibbonGroup group) {
        groups.remove(group);
    }

    public List<InternalRibbonGroup> getRibbonGroups(final RibbonTemplateParameters parameters,
            final ViewDefinition viewDefinition) {
        List<InternalRibbonGroup> groups = new LinkedList<InternalRibbonGroup>();
        for (TemplateRibbonGroup templateGroup : getFilteredList(parameters)) {
            InternalRibbonGroup groupInstance = templateGroup.getRibbonGroup(parameters, viewDefinition);
            if (groupInstance != null) {
                groups.add(groupInstance);
            }
        }
        return groups;
    }

    private List<TemplateRibbonGroup> getFilteredList(final RibbonTemplateParameters parameters) {
        List<TemplateRibbonGroup> filteredList = new LinkedList<TemplateRibbonGroup>();
        if (parameters.getExcludeGroups() != null) {
            for (TemplateRibbonGroup group : groups) {
                if (!parameters.getExcludeGroups().contains(group.getName())) {
                    filteredList.add(group);
                }
            }
            return filteredList;
        }
        if (parameters.getIncludeGroups() != null) {
            for (TemplateRibbonGroup group : groups) {
                if (parameters.getIncludeGroups().contains(group.getName())) {
                    filteredList.add(group);
                }
            }
            return filteredList;
        }
        return groups;
    }

    public void parseParameters(final RibbonTemplateParameters parameters) {
        parseGroupNames(parameters.getIncludeGroups());
        parseGroupNames(parameters.getExcludeGroups());
        parseItemNames(parameters.getIncludeItems());
        parseItemNames(parameters.getExcludeItems());
    }

    private void parseGroupNames(final Set<String> groupNames) {
        if (groupNames == null) {
            return;
        }
        for (String groupName : groupNames) {
            boolean groupFound = false;
            for (TemplateRibbonGroup group : groups) {
                if (groupName.equals(group.getName())) {
                    groupFound = true;
                }
            }
            if (!groupFound) {
                throw new IllegalStateException("group '" + groupName + "' not found in template '" + name + "'");
            }
        }
    }

    private void parseItemNames(final Set<String> itemNames) {
        if (itemNames == null) {
            return;
        }
        for (String itemName : itemNames) {
            String[] itemNameParts = itemName.split("\\.");
            if (itemNameParts.length != 2) {
                throw new IllegalStateException("item name '" + itemName + "' is not correct");
            }
            TemplateRibbonGroup group = getGroupByName(itemNameParts[0]);
            if (group == null) {
                throw new IllegalStateException("group '" + itemNameParts[0] + "' of item '" + itemName + "' not found");
            }
            if (!group.containItem(itemName)) {
                throw new IllegalStateException("item element '" + itemNameParts[1] + "' of item '" + itemName
                        + "' not found in template group '" + itemNameParts[0] + "'");
            }
        }
    }

    private TemplateRibbonGroup getGroupByName(final String groupName) {
        for (TemplateRibbonGroup group : groups) {
            if (groupName.equals(group.getName())) {
                return group;
            }
        }
        return null;
    }
}
