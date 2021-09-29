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

import java.util.List;

import com.qcadoo.plugin.api.artifact.PluginArtifact;

/**
 * Service to managing plugins.
 * 
 * @since 0.4.0
 */
public interface PluginManager {

    /**
     * Enables plugins with given identifiers.
     * 
     * @param identifiers
     *            identifiers to enable
     * @return status of the operation
     */
    PluginOperationResult enablePlugin(final String... identifiers);

    /**
     * Disables plugins with given identifiers.
     * 
     * @param identifiers
     *            identifiers to disable
     * @return status of the operation
     */
    PluginOperationResult disablePlugin(final String... identifiers);

    /**
     * Uninstalls plugins with given identifiers.
     * 
     * @param identifiers
     *            identifiers to uninstall
     * @return status of the operation
     */
    PluginOperationResult uninstallPlugin(final String... identifiers);

    /**
     * Installs plugin from given artifact.
     * 
     * @param pluginArtifact
     *            plugin's artifact
     * @return status of the operation
     */
    PluginOperationResult installPlugin(final PluginArtifact pluginArtifact);

    /**
     * Gets list of enabled plugins
     * 
     * @return list identifier of enabled plugins.
     */
    List<Plugin> getEnabledPluginsList();

    /**
     * Checks if plugin with specified identifier is enabled
     * 
     * @param pluginIdentifier
     *            plugin identifier
     * @return true if plugin is enabled, otherwise returns false
     */
    boolean isPluginEnabled(final String pluginIdentifier);
}
