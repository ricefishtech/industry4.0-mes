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

import java.util.Set;

/**
 * Plugin represents information from plugin's descriptor and holds all its modules.
 * 
 * Plugin, {@link Module} and {@link ModuleFactory} are strongly connected. Below shows licecycle methods in the particular
 * situations.
 * 
 * <h3>Starting application</h3>
 * 
 * For every plugin's description, for every module:
 * 
 * <ul>
 * <li>{@link ModuleFactory#parse(String, Element)}</li>
 * </ul>
 * 
 * For every module factory, in proper order:
 * 
 * <ul>
 * <li>{@link ModuleFactory#preInit()}</li>
 * <li>{@link Module#init()} for every module belongs to this module factory, in plugin dependency order</li>
 * <li>{@link ModuleFactory#postInit()}</li>
 * </ul>
 * 
 * Again for every module factory, in proper order:
 * 
 * <ul>
 * <li>{@link Module#enableOnStartup()} or {@link Module#disableOnStartup()} for every module belongs to this module factory, in
 * plugin dependency order</li>
 * <li>{@link Module#multiTenantEnableOnStartup()} or {@link Module#multiTenantDisableOnStartup()} for every module belongs to
 * this module factory, for every tenant, in plugin dependency order</li>
 * </ul>
 * 
 * For every module factory with state {@link PluginState#ENABLING}:
 * 
 * <ul>
 * <li>{@link Module#enable()}</li>
 * <li>{@link Module#multiTenantEnable()} for every tenant</li>
 * </ul>
 * 
 * <h3>Enabling plugin</h3>
 * 
 * For plugin in {@link PluginState#DISABLED} state.
 * 
 * <ul>
 * <li>{@link Module#enable()}</li>
 * <li>{@link Module#multiTenantEnable()} for every tenant</li>
 * </ul>
 * 
 * For plugin in {@link PluginState#TEMPORARY} state, the state is changed to {@link PluginState#ENABLING} and the system is
 * restarted.
 * 
 * <h3>Disabling plugin</h3>
 * 
 * <ul>
 * <li>{@link Module#disable()}</li>
 * <li>{@link Module#multiTenantDisable()} for every tenant</li>
 * </ul>
 * 
 * <h3>Installing plugin</h3>
 * 
 * No additional method is called.
 * 
 * <h3>Uninstalling plugin</h3>
 * 
 * If the plugin is enabled, it will be disabled first. The system will be restarted.
 * 
 * <h3>Updating plugin</h3>
 * 
 * If the plugin is enabled, it will be disabled first. The state is set to {@link PluginState#ENABLING} and the system is
 * restarted.
 * 
 * @since 0.4.0
 */
public interface Plugin {

    /**
     * Returns plugin's identifier, it is unique in the whole system.
     * 
     * @return identifier
     */
    String getIdentifier();

    /**
     * Returns version of the plugin.
     * 
     * @return version
     */
    Version getVersion();

    /**
     * Returns state of the plugin, only {@link PluginState#ENABLED} plugins are usable in system.
     * 
     * @return state
     */
    PluginState getState();

    /**
     * Returns additional information.
     * 
     * @return additional information
     */
    PluginInformation getPluginInformation();

    /**
     * Returns requirements information.
     * 
     * @return requirements information
     */
    Set<PluginDependencyInformation> getRequiredPlugins();

    /**
     * Returns true for system plugin, it means that plugin cannot be disabled and removed.
     * 
     * @return true for system plugin
     */
    boolean isSystemPlugin();

    /**
     * Returns name the file with plugin.
     * 
     * @return filename
     */
    String getFilename();

    /**
     * Compares version of the plugin with given version.
     * 
     * @see Version#compareTo(Version)
     * @return comparison result
     */
    int compareVersion(Version version);

    /**
     * Checks if plugin has expected state.
     * 
     * @param expectedState
     *            expected state
     * @return true if plugin is in expected state
     */
    boolean hasState(PluginState expectedState);

}