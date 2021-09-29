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

public interface PluginDependencyResult {

    /**
     * Set of plugins which have to been enabled.
     * 
     * @return plugins to enable
     */
    Set<PluginDependencyInformation> getDependenciesToEnable();

    /**
     * Set of plugins which are missing.
     * 
     * @return missing plugins
     */
    Set<PluginDependencyInformation> getUnsatisfiedDependencies();

    /**
     * Set of plugins which have to been disabled.
     * 
     * @return plugins to disable
     */
    Set<PluginDependencyInformation> getDependenciesToDisable();

    /**
     * Set of plugins which have to been uninstalled.
     * 
     * @return plugins to uninstall
     */
    Set<PluginDependencyInformation> getDependenciesToUninstall();

    /**
     * Set of plugins which have to disabled after plugin update.
     * 
     * @return plugins to disable
     */
    Set<PluginDependencyInformation> getDependenciesToDisableUnsatisfiedAfterUpdate();

    /**
     * Returns true if there is no cycle, missing and unsatisfied plugins.
     * 
     * @return true if there is no problems with dependencies between plugins
     */
    boolean isDependenciesSatisfied();

    /**
     * Returns true if there is a cycle between plugins.
     * 
     * @return true if there is a cycle
     */
    boolean isCyclic();

}
