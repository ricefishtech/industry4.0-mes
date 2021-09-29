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
package com.qcadoo.plugin.internal.module;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleFactory;
import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginState;
import com.qcadoo.plugin.api.PluginStateResolver;
import com.qcadoo.plugin.internal.PluginUtilsService;
import com.qcadoo.plugin.internal.api.InternalPlugin;
import com.qcadoo.tenant.api.MultiTenantUtil;
import com.qcadoo.tenant.internal.DefaultMultiTenantService;

public class ModuleFactoryAccessorTest {

    @Test
    public void shouldCallInitOnAllModuleFactories() throws Exception {
        // given
        MultiTenantUtil multiTenantUtil = new MultiTenantUtil();
        ReflectionTestUtils.setField(multiTenantUtil, "multiTenantService", new DefaultMultiTenantService());
        multiTenantUtil.init();

        PluginStateResolver mockPluginStateResolver = mock(PluginStateResolver.class);
        given(mockPluginStateResolver.isEnabled("plugin1")).willReturn(false);
        given(mockPluginStateResolver.isEnabled("plugin2")).willReturn(true);

        PluginUtilsService pluginUtil = new PluginUtilsService(mockPluginStateResolver);
        pluginUtil.init();

        ModuleFactory<?> moduleFactory1 = mock(ModuleFactory.class);
        given(moduleFactory1.getIdentifier()).willReturn("module1");
        ModuleFactory<?> moduleFactory2 = mock(ModuleFactory.class);
        given(moduleFactory2.getIdentifier()).willReturn("module2");

        DefaultModuleFactoryAccessor moduleFactoryAccessor = new DefaultModuleFactoryAccessor();
        List<ModuleFactory<?>> factoriesList = new ArrayList<>();
        factoriesList.add(moduleFactory1);
        factoriesList.add(moduleFactory2);
        moduleFactoryAccessor.setModuleFactories(factoriesList);

        InternalPlugin plugin1 = mock(InternalPlugin.class);
        Module module111 = mock(Module.class);
        Module module112 = mock(Module.class);
        Module module12 = mock(Module.class);
        given(plugin1.getModules(moduleFactory1)).willReturn(newArrayList(module111, module112));
        given(plugin1.getModules(moduleFactory2)).willReturn(newArrayList(module12));
        given(plugin1.hasState(PluginState.ENABLED)).willReturn(false);
        given(plugin1.getIdentifier()).willReturn("plugin1");

        InternalPlugin plugin2 = mock(InternalPlugin.class);
        Module module21 = mock(Module.class);
        Module module22 = mock(Module.class);
        given(plugin2.getModules(moduleFactory1)).willReturn(newArrayList(module21));
        given(plugin2.getModules(moduleFactory2)).willReturn(newArrayList(module22));
        given(plugin2.hasState(PluginState.ENABLED)).willReturn(true);
        given(plugin2.getIdentifier()).willReturn("plugin2");

        List<Plugin> plugins = newArrayList(plugin1, plugin2);

        // when
        moduleFactoryAccessor.init(plugins);

        // then
        InOrder inOrder = inOrder(moduleFactory1, moduleFactory2, module111, module112, module12, module21, module22);

        inOrder.verify(moduleFactory1).preInit();
        inOrder.verify(module111).init();
        inOrder.verify(module112).init();
        inOrder.verify(module21).init();
        inOrder.verify(moduleFactory1).postInit();
        inOrder.verify(moduleFactory2).preInit();
        inOrder.verify(module12).init();
        inOrder.verify(module22).init();
        inOrder.verify(moduleFactory2).postInit();
        inOrder.verify(module21).enableOnStartup();
        inOrder.verify(module21).multiTenantEnableOnStartup();
        inOrder.verify(module22).enableOnStartup();
        inOrder.verify(module22).multiTenantEnableOnStartup();
        inOrder.verify(module12).disableOnStartup();
        inOrder.verify(module12).multiTenantDisableOnStartup();
        inOrder.verify(module112).disableOnStartup();
        inOrder.verify(module112).multiTenantDisableOnStartup();
        inOrder.verify(module111).disableOnStartup();
        inOrder.verify(module111).multiTenantDisableOnStartup();
    }

    @Test
    public void shouldReturnModuleFactory() throws Exception {
        // given
        ModuleFactory<?> moduleFactory = mock(ModuleFactory.class);
        given(moduleFactory.getIdentifier()).willReturn("module");

        DefaultModuleFactoryAccessor moduleFactoryAccessor = new DefaultModuleFactoryAccessor();
        moduleFactoryAccessor.setModuleFactories(Collections.<ModuleFactory<?>> singletonList(moduleFactory));

        // when
        ModuleFactory<? extends Module> mf = moduleFactoryAccessor.getModuleFactory("module");

        // then
        Assert.assertSame(moduleFactory, mf);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfModuleFactoryNotExists() throws Exception {
        // given
        DefaultModuleFactoryAccessor moduleFactoryAccessor = new DefaultModuleFactoryAccessor();
        moduleFactoryAccessor.setModuleFactories(Collections.<ModuleFactory<?>> emptyList());

        // when
        moduleFactoryAccessor.getModuleFactory("module");
    }
}
