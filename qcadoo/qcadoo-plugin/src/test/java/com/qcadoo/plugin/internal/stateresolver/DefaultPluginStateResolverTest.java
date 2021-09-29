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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.Before;
import org.junit.Test;

import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginAccessor;
import com.qcadoo.plugin.api.PluginState;

public class DefaultPluginStateResolverTest {

    private static final String PLUGIN_IDENTIFIER = "somePlugin";

    private DefaultPluginStateResolver stateResolverImpl;

    private Plugin plugin;

    private PluginAccessor pluginAccessor;

    @Before
    public final void init() {
        stateResolverImpl = new DefaultPluginStateResolver();

        pluginAccessor = mock(PluginAccessor.class);
        setField(stateResolverImpl, "pluginAccessor", pluginAccessor);

        plugin = mock(Plugin.class);
        given(plugin.getIdentifier()).willReturn(PLUGIN_IDENTIFIER);
        given(pluginAccessor.getPlugin(PLUGIN_IDENTIFIER)).willReturn(plugin);
    }

    @Test
    public final void shouldIsEnabledReturnFalseIfPluginDoesNotExist() throws Exception {
        // when
        boolean isEnabled = stateResolverImpl.isEnabled("phantomPlugin");

        // then
        assertFalse(isEnabled);
    }

    @Test
    public final void shouldIsEnabledReturnTrue() throws Exception {
        // given
        setMockPluginEnabled();

        // when
        boolean isEnabled = stateResolverImpl.isEnabled(PLUGIN_IDENTIFIER);

        // then
        assertTrue(isEnabled);
    }

    @Test
    public final void shouldIsEnabledReturnFalse() throws Exception {
        // given
        setMockPluginDisabled();

        // when
        boolean isEnabled = stateResolverImpl.isEnabled(PLUGIN_IDENTIFIER);

        // then
        assertFalse(isEnabled);
    }

    private void setMockPluginEnabled() {
        given(plugin.getState()).willReturn(PluginState.ENABLED);
    }

    private void setMockPluginDisabled() {
        given(plugin.getState()).willReturn(PluginState.DISABLED);
    }
}
