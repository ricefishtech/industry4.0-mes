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
package com.qcadoo.view.api.components;

import com.qcadoo.view.api.ribbon.Ribbon;

/**
 * Represents window component
 * 
 * @since 0.4.0
 */
public interface WindowComponent {

    /**
     * Returns ribbon of this window
     * 
     * @return ribbon of this window
     */
    Ribbon getRibbon();

    /**
     * Informs that this window's ribbon should be updated
     */
    void requestRibbonRender();

    /**
     * Set tab with given name as active (focused).
     * 
     * @param tabName
     *            name of the tab to be activated (focused). Note that this tab will be also mark as visible.
     * 
     * @throws IllegalArgumentException
     *             If tab with given name doesn't exist inside of this window.
     * @since 1.2.1
     */
    void setActiveTab(final String tabName) throws IllegalArgumentException;

}
