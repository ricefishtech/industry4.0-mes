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
package com.qcadoo.plugin.internal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginStateResolver;

/**
 * Utils to checking plugin's state.
 * 
 * @since 0.4.0
 */
@Service
public class PluginUtilsService {

    private final PluginStateResolver pluginStateResolver;

    private static PluginUtilsService instance;

    @Autowired
    public PluginUtilsService(final PluginStateResolver pluginStateResolver) {
        this.pluginStateResolver = pluginStateResolver;
    }

    @PostConstruct
    public void init() {
        initialise(this);
    }

    private static void initialise(final PluginUtilsService pluginUtil) {
        PluginUtilsService.instance = pluginUtil;
    }

    /**
     * Returns true if plugin is enabled.
     * 
     * @param plugin
     *            plugin
     * @return true if enabled
     * @see PluginStateResolver#isEnabled(Plugin)
     */
    public static boolean isEnabled(final Plugin plugin) {
        return instance.pluginStateResolver.isEnabled(plugin);
    }

    /**
     * Returns true if plugin is enabled.
     * 
     * @param pluginIdentifier
     *            plugin's identifier
     * @return true if enabled
     * @see PluginStateResolver#isEnabled(String)
     */
    public static boolean isEnabled(final String pluginIdentifier) {
        return instance.pluginStateResolver.isEnabled(pluginIdentifier);
    }

    /**
     * Returns true if plugin is enabled or enabling.
     * 
     * @deprecated for internal use only
     * 
     * @param plugin
     *            plugin
     * @return true if enabled or enabling
     * @see PluginStateResolver#isEnabledOrEnabling(Plugin)
     */
    @Deprecated
    public static boolean isEnabledOrEnabling(final Plugin plugin) {
        return instance.pluginStateResolver.isEnabledOrEnabling(plugin);
    }

    /**
     * Returns true if plugin is enabled or enabling.
     * 
     * @deprecated for internal use only
     * 
     * @param pluginIdentifier
     *            plugin's identifier
     * @return true if enabled or enabling
     * @see PluginStateResolver#isEnabledOrEnabling(Plugin)
     */
    @Deprecated
    public static boolean isEnabledOrEnabling(final String pluginIdentifier) {
        return instance.pluginStateResolver.isEnabledOrEnabling(pluginIdentifier);
    }

}
