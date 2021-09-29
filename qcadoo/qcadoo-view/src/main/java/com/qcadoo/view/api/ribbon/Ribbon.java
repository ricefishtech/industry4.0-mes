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

/**
 * Represents ribbon in application
 * 
 * @since 0.4.0
 */
public interface Ribbon {

    /**
     * Get identifier of this ribbon
     * 
     * @return identifier of this ribbon
     */
    String getName();

    /**
     * Get groups of this ribbon
     * 
     * @return groups of this ribbon
     */
    List<RibbonGroup> getGroups();

    /**
     * Get group by name of this ribbon
     * 
     * @return group or null when no group witch such name
     */

    RibbonGroup getGroupByName(String groupName);

}
