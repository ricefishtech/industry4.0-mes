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
package com.qcadoo.plugin.internal.stateresolver;

import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginAccessor;
import com.qcadoo.plugin.api.PluginState;

@Service
public class DefaultPluginStateResolver implements InternalPluginStateResolver {

    private PluginAccessor pluginAccessor;

    @Override
    public void setPluginAccessor(final PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public boolean isEnabled(final String pluginIdentifier) {
        return isEnabled(getPlugin(pluginIdentifier));
    }

    @Override
    public boolean isEnabled(final Plugin plugin) {
        return plugin != null && PluginState.ENABLED.equals(plugin.getState());
    }

    // TODO maku test
    /**
     * @deprecated internal use only!
     * */
    @Deprecated
    @Override
    public boolean isEnabledOrEnabling(final String pluginIdentifier) {
        return isEnabledOrEnabling(getPlugin(pluginIdentifier));
    }

    // TODO maku test
    /**
     * @deprecated internal use only!
     * */
    @Deprecated
    @Override
    public boolean isEnabledOrEnabling(final Plugin plugin) {
        return isEnabled(plugin) || isEnabling(plugin);
    }

    private boolean isEnabling(final Plugin plugin) {
        return plugin != null && PluginState.ENABLING.equals(plugin.getState());
    }

    protected Plugin getPlugin(final String pluginIdentifier) {
        Preconditions.checkState(pluginAccessor != null, "No PluginAccessor defined");
        return pluginAccessor.getPlugin(pluginIdentifier);
    }

}
