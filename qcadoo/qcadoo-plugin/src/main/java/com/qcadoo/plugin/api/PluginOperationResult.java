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
package com.qcadoo.plugin.api;


/**
 * Holder for the status and dependencies information returned by {@link PluginManager} methods.
 * 
 * @since 0.4.0
 */
public interface PluginOperationResult {

    /**
     * Returns true if operation was successful: {@link PluginOperationStatus#SUCCESS},
     * {@link PluginOperationStatus#SUCCESS_WITH_MISSING_DEPENDENCIES} and {@link PluginOperationStatus#SUCCESS_WITH_RESTART}.
     * 
     * @return true if success
     */
    boolean isSuccess();

    /**
     * Returns true if operation requires restart: {@link PluginOperationStatus#SUCCESS_WITH_RESTART}.
     * 
     * @return true if success
     */
    boolean isRestartNeccessary();

    /**
     * Returns status.
     * 
     * @return status
     */
    PluginOperationStatus getStatus();

    /**
     * Returns dependencies information.
     * 
     * @return dependencies information
     */
    PluginDependencyResult getPluginDependencyResult();

}
