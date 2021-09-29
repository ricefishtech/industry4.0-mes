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
package com.qcadoo.view.api.ribbon;

import java.util.List;

import com.qcadoo.security.api.SecurityRole;

/**
 * Represents ribbon items group
 * 
 * @since 0.4.0
 */
public interface RibbonGroup {

    /**
     * Get identifier of this ribbon group
     * 
     * @return identifier of this ribbon group
     */
    String getName();

    /**
     * Returns default authorization role necessary to be able to see this ribbon group or null if no such role defined
     * 
     * @return default authorization role
     */
    SecurityRole getAuthorizationRole();

    /**
     * Get items of this group
     * 
     * @return items of this group
     */
    List<RibbonActionItem> getItems();

    /**
     * Get item by name
     * 
     * @return item or null when no item witch such name
     */
    RibbonActionItem getItemByName(final String itemName);

}
