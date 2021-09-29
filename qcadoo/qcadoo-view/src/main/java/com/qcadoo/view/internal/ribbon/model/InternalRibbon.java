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

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.ribbon.Ribbon;

public interface InternalRibbon extends Ribbon {

    /**
     * List of items which should not be permanently disabled.
     */
    List<String> EXCLUDE_FROM_DISABLING = Collections.unmodifiableList(Lists.newArrayList("navigation.back", "actions.refresh"));

    /**
     * Set identifier of this ribbon
     * 
     * @param name
     *            identifier of this ribbon
     */
    void setName(final String name);

    /**
     * Add group to this ribbon
     * 
     * @param group
     *            group to add
     */
    void addGroupsPack(final RibbonGroupsPack groupPack);

    /**
     * Add group to this ribbon as first
     * 
     * @param group
     *            group to add
     */
    void addGroupPackAsFirst(final RibbonGroupsPack groupPack);

    /**
     * Removes group from this ribbon
     * 
     * @param group
     *            group to remove
     */
    void removeGroupsPack(final RibbonGroupsPack groupPack);

    /**
     * generates JSON string that contains all ribbon definition
     * 
     * @param securityRolesService
     * 
     * @return JSON ribbon definition
     */
    JSONObject getAsJson(final SecurityRolesService securityRolesService);

    /**
     * Gets copy of this robbon - internal usage only
     * 
     * @return copy of this ribbon
     */
    InternalRibbon getCopy();

    /**
     * Gets ribbon with only updated fields - internal usage only
     * 
     * @return ribon with only updated fields
     */
    InternalRibbon getUpdate();

    /**
     * Set this ribbon items state to permanently disabled. Permanently disabled means that you can not enable them using {@link
     * RibbonActionItem.setEnabled()}
     * 
     * @param permanentlyDisabled
     *            true if items in this ribbon should be permanently disabled
     */
    void setPermanentlyDisabled(final boolean permanentlyDisabled);

    /**
     * Set horizontal alignment.
     * 
     * @param alignment
     *            alignment value
     */
    void setAlignment(final String alignment);

}
