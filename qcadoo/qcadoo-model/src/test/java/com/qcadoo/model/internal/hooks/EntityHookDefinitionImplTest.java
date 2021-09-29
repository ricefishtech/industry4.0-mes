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
package com.qcadoo.model.internal.hooks;

import static org.mockito.BDDMockito.given;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import com.qcadoo.model.beans.sample.CustomEntityService;
import com.qcadoo.plugin.api.PluginStateResolver;
import com.qcadoo.plugin.internal.PluginUtilsService;

public class EntityHookDefinitionImplTest {

    private static final String SOME_PLUGIN_IDENTIFIER = "somePlugin";

    private EntityHookDefinitionImpl entityHookDefinitionImpl;

    @Mock
    private PluginStateResolver pluginStateResolver;

    @Before
    public final void init() throws HookInitializationException {
        MockitoAnnotations.initMocks(this);

        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        BDDMockito.given(applicationContext.getBean(CustomEntityService.class)).willReturn(new CustomEntityService());
        entityHookDefinitionImpl = new EntityHookDefinitionImpl("com.qcadoo.model.beans.sample.CustomEntityService", "onSave",
                SOME_PLUGIN_IDENTIFIER, applicationContext);

        PluginUtilsService pluginUtil = new PluginUtilsService(pluginStateResolver);
        pluginUtil.init();
    }

    private void stubPluginIsEnabled(final boolean isEnabled) {
        given(pluginStateResolver.isEnabled(SOME_PLUGIN_IDENTIFIER)).willReturn(isEnabled);
        given(pluginStateResolver.isEnabledOrEnabling(SOME_PLUGIN_IDENTIFIER)).willReturn(isEnabled);
    }

    @Test
    public final void shouldIsEnabledReturnTrueIfSourcePluginIsEnabledForCurrentUser() throws Exception {
        // given
        stubPluginIsEnabled(true);

        entityHookDefinitionImpl.enable();

        // when
        boolean isEnabled = entityHookDefinitionImpl.isEnabled();

        // then
        Assert.assertTrue(isEnabled);
    }

    @Test
    public final void shouldIsEnabledReturnFalseIfSourcePluginIsEnabledForCurrentUserButDisableInSystem() throws Exception {
        // given
        stubPluginIsEnabled(true);

        entityHookDefinitionImpl.disable();

        // when
        boolean isEnabled = entityHookDefinitionImpl.isEnabled();

        // then
        Assert.assertFalse(isEnabled);
    }

    @Test
    public final void shouldIsEnabledReturnFalseIfSourcePluginIsDisabled() throws Exception {
        // given
        stubPluginIsEnabled(false);

        entityHookDefinitionImpl.disable();

        // when
        boolean isEnabled = entityHookDefinitionImpl.isEnabled();

        // then
        Assert.assertFalse(isEnabled);
    }

    @Test
    public final void shouldIsEnabledReturnFalseIfSourcePluginIsDisabledOnlyForCurrentUser() throws Exception {
        // given
        stubPluginIsEnabled(false);

        entityHookDefinitionImpl.enable();

        // when
        boolean isEnabled = entityHookDefinitionImpl.isEnabled();

        // then
        Assert.assertFalse(isEnabled);
    }
}
