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
 * Status returned by {@link PluginManager} methods.
 * 
 * @since 0.4.0
 */
public enum PluginOperationStatus {

    /**
     * Plugin cannot be enabled because its dependencies are not enabled. User decision is required.
     */
    DEPENDENCIES_TO_ENABLE,

    /**
     * Operation was successful.
     */
    SUCCESS,

    /**
     * Operation was successful, but restart is necessary.
     */
    SUCCESS_WITH_RESTART,

    /**
     * Plugin cannot be installed or enabled because there are missing dependencies.
     */
    UNSATISFIED_DEPENDENCIES,

    /**
     * Plugin cannot be disabled because it's a dependency for enabled plugins. User decision is required.
     */
    DEPENDENCIES_TO_DISABLE,

    /**
     * System plugin cannot be disabled.
     */
    SYSTEM_PLUGIN_DISABLING,

    /**
     * Plugin was successfully installed, but there are missing dependencies required to enable plugin.
     */
    SUCCESS_WITH_MISSING_DEPENDENCIES,

    /**
     * Plugin's file was successfully uploaded but it is not a JAR file or the descriptor hasn't been found.
     */
    CORRUPTED_PLUGIN,

    /**
     * Plugin's file cannot be uploaded.
     */
    CANNOT_UPLOAD_PLUGIN,

    /**
     * Plugin cannot be installed - plugin's file doesn't exist, cannot be read or move to target directory.
     */
    CANNOT_INSTALL_PLUGIN_FILE,

    /**
     * System plugin cannot be unistalled.
     */
    SYSTEM_PLUGIN_UNINSTALLING,

    /**
     * System plugin cannot be updated.
     */
    SYSTEM_PLUGIN_UPDATING,

    /**
     * Plugin cannot be downgraded.
     */
    CANNOT_DOWNGRADE_PLUGIN,

    /**
     * Plugin cannot be installed - dependencies cycles exists.
     */
    DEPENDENCIES_CYCLES_EXISTS,

    /**
     * Plugin cannot be uninstalled because it's a dependency for other plugins. User decision is required.
     */
    DEPENDENCIES_TO_UNINSTALL,

    /**
     * Plugin cannot be updated because it's a dependency for other plugins, and these plugins have to been disabled.
     */
    UNSATISFIED_DEPENDENCIES_AFTER_UPDATE,

    /**
     * Plugin cannot be enabled - was disabled by system admin or shop service.
     */
    PLUGIN_ENABLING_IS_NOT_ALLOWED,

    /**
     * Plugin cannot be enabled because of encountered errors
     */
    PLUGIN_ENABLING_ENCOUNTERED_ERRORS,

    /**
     * Plugin not exist.
     */
    PLUGIN_NOT_EXIST;

}
