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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleFactory;
import com.qcadoo.plugin.api.PluginState;
import com.qcadoo.plugin.api.PluginStateResolver;
import com.qcadoo.plugin.internal.api.InternalPlugin;
import com.qcadoo.tenant.api.MultiTenantUtil;
import com.qcadoo.tenant.internal.DefaultMultiTenantService;

public class PluginChangeStateToTest {

    private static final Logger LOG = LoggerFactory.getLogger(PluginChangeStateToTest.class);

    private PluginStateResolver mockPluginStateResolver;

    @Test
    public void shouldChangeState() throws Exception {
        MultiTenantUtil multiTenantUtil = new MultiTenantUtil();
        ReflectionTestUtils.setField(multiTenantUtil, "multiTenantService", new DefaultMultiTenantService());
        multiTenantUtil.init();

        mockPluginStateResolver = mock(PluginStateResolver.class);

        PluginUtilsService pluginUtil = new PluginUtilsService(mockPluginStateResolver);
        pluginUtil.init();

        assertOperationNotSupported(null, PluginState.UNKNOWN);
        assertOperationSupported(null, PluginState.TEMPORARY, false, false);
        assertOperationSupported(null, PluginState.ENABLING, false, false);
        assertOperationSupported(null, PluginState.ENABLED, false, false);
        assertOperationSupported(null, PluginState.DISABLED, false, false);

        assertOperationNotSupported(PluginState.TEMPORARY, PluginState.UNKNOWN);
        assertOperationNotSupported(PluginState.TEMPORARY, PluginState.TEMPORARY);
        assertOperationSupported(PluginState.TEMPORARY, PluginState.ENABLING, false, false);
        assertOperationNotSupported(PluginState.TEMPORARY, PluginState.ENABLED);
        assertOperationNotSupported(PluginState.TEMPORARY, PluginState.DISABLED);

        assertOperationNotSupported(PluginState.ENABLING, PluginState.UNKNOWN);
        assertOperationNotSupported(PluginState.ENABLING, PluginState.TEMPORARY);
        assertOperationNotSupported(PluginState.ENABLING, PluginState.ENABLING);
        assertOperationSupported(PluginState.ENABLING, PluginState.ENABLED, true, false);
        assertOperationNotSupported(PluginState.ENABLING, PluginState.DISABLED);

        assertOperationNotSupported(PluginState.ENABLED, PluginState.UNKNOWN);
        assertOperationNotSupported(PluginState.ENABLED, PluginState.TEMPORARY);
        assertOperationNotSupported(PluginState.ENABLED, PluginState.ENABLING);
        assertOperationNotSupported(PluginState.ENABLED, PluginState.ENABLED);
        assertOperationSupported(PluginState.ENABLED, PluginState.DISABLED, false, true);

        assertOperationNotSupported(PluginState.DISABLED, PluginState.UNKNOWN);
        assertOperationNotSupported(PluginState.DISABLED, PluginState.TEMPORARY);
        assertOperationSupported(PluginState.DISABLED, PluginState.ENABLING, false, false);
        assertOperationSupported(PluginState.DISABLED, PluginState.ENABLED, true, false);
        assertOperationNotSupported(PluginState.DISABLED, PluginState.DISABLED);
    }

    private void assertOperationNotSupported(final PluginState from, final PluginState to) throws Exception {
        // given
        InternalPlugin plugin = DefaultPlugin.Builder.identifier("identifier", Collections.<ModuleFactory<?>> emptyList())
                .build();

        if (from != null) {
            plugin.changeStateTo(from);
        }

        // when
        try {
            plugin.changeStateTo(to);
            Assert.fail();
        } catch (IllegalStateException e) {
            LOG.info("ignore");
        }
    }

    private void assertOperationSupported(final PluginState from, final PluginState to, final boolean callEnable,
            final boolean callDisable) throws Exception {
        // given
        ModuleFactory<?> moduleFactory = mock(ModuleFactory.class);
        Module module1 = mock(Module.class);
        Module module2 = mock(Module.class);

        InternalPlugin plugin = DefaultPlugin.Builder
                .identifier("identifier", Lists.<ModuleFactory<?>> newArrayList(moduleFactory))
                .withModule(moduleFactory, module1).withModule(moduleFactory, module2).build();

        if (from != null) {
            plugin.changeStateTo(from);
        }

        given(mockPluginStateResolver.isEnabled("identifier")).willReturn(PluginState.ENABLED.equals(to));

        // when
        plugin.changeStateTo(to);

        // then
        assertEquals(to, plugin.getState());
        assertTrue(plugin.hasState(to));

        if (callEnable) {
            verify(module1).enable();
            // verify(module1).multiTenantEnable();
            verify(module2).enable();
        } else {
            verify(module1, never()).enable();
            verify(module2, never()).multiTenantEnable();
        }
        if (callDisable) {
            verify(module1).disable();
            // verify(module2).multiTenantDisable();
        } else {
            verify(module1, never()).disable();
            verify(module2, never()).multiTenantDisable();
        }
    }

}
