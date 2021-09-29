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
package com.qcadoo.view.internal.ribbon.model;

import com.qcadoo.plugin.api.PluginUtils;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import com.qcadoo.view.api.ribbon.RibbonGroup;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class RibbonImpl implements InternalRibbon {

    private String name;

    private String alignment;

    private final List<RibbonGroupsPack> groupPacks = new LinkedList<RibbonGroupsPack>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public List<RibbonGroup> getGroups() {
        return getAllCastedGroups();
    }

    @Override
    public RibbonGroup getGroupByName(final String groupName) {
        for (RibbonGroupsPack groupPack : groupPacks) {
            RibbonGroup group = groupPack.getGroupByName(groupName);
            if (group != null) {
                return group;
            }
        }
        return null;
    }

    @Override
    public void addGroupsPack(final RibbonGroupsPack groupPack) {
        groupPacks.add(groupPack);
    }

    @Override
    public void addGroupPackAsFirst(final RibbonGroupsPack groupPack) {
        groupPacks.add(0, groupPack);
    }

    @Override
    public void removeGroupsPack(final RibbonGroupsPack groupPack) {
        groupPacks.remove(groupPack);
    }

    @Override
    public JSONObject getAsJson(final SecurityRolesService securityRolesService) {
        JSONObject ribbonJson = new JSONObject();
        try {
            ribbonJson.put("name", name);
            ribbonJson.put("alignment", alignment);
            JSONArray groupsArray = new JSONArray();
            for (InternalRibbonGroup group : getAllGroups()) {
                if ((group.getExtensionPluginIdentifier() == null || PluginUtils.isEnabled(group.getExtensionPluginIdentifier()))
                        && securityRolesService.canAccess(group.getAuthorizationRole())) {
                    groupsArray.put(group.getAsJson());
                }
            }
            ribbonJson.put("groups", groupsArray);
            return ribbonJson;
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public InternalRibbon getCopy() {
        InternalRibbon copy = new RibbonImpl();
        copy.setName(name);
        for (RibbonGroupsPack groupPack : groupPacks) {
            copy.addGroupsPack(groupPack.getCopy());
        }
        return copy;
    }

    @Override
    public InternalRibbon getUpdate() {
        InternalRibbon diff = new RibbonImpl();
        boolean isDiffrence = false;
        diff.setName(name);

        for (RibbonGroupsPack groupPack : groupPacks) {
            RibbonGroupsPack diffGroupPack = groupPack.getUpdate();
            if (diffGroupPack != null) {
                diff.addGroupsPack(diffGroupPack);
                isDiffrence = true;
            }

        }
        if (isDiffrence) {
            return diff;
        }
        return null;
    }

    private List<InternalRibbonGroup> getAllGroups() {
        List<InternalRibbonGroup> allGroups = new LinkedList<InternalRibbonGroup>();
        for (RibbonGroupsPack groupPack : groupPacks) {
            allGroups.addAll(groupPack.getGroups());
        }
        return allGroups;
    }

    private List<RibbonGroup> getAllCastedGroups() {
        List<RibbonGroup> allGroups = new LinkedList<RibbonGroup>();
        for (RibbonGroupsPack groupPack : groupPacks) {
            allGroups.addAll(groupPack.getGroups());
        }
        return allGroups;
    }

    @Override
    public void setPermanentlyDisabled(final boolean permanentlyDisabled) {
        for (RibbonGroup group : getGroups()) {
            for (RibbonActionItem item : group.getItems()) {
                boolean disabled = permanentlyDisabled && ribbonItemShouldBeDisabled(group, item);
                ((InternalRibbonActionItem) item).setPermanentlyDisabled(disabled);
                item.requestUpdate(true);
            }
        }
    }

    private boolean ribbonItemShouldBeDisabled(final RibbonGroup group, final RibbonActionItem item) {
        for (String excludedItemPattern : InternalRibbon.EXCLUDE_FROM_DISABLING) {
            String[] itemPath = StringUtils.split(excludedItemPattern, '.');
            if (itemPath[0].equals(group.getName()) && itemPath[1].equals(item.getName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setAlignment(final String alignment) {
        if (!StringUtils.isBlank(alignment)) {
            this.alignment = alignment;
        }
    }
}
