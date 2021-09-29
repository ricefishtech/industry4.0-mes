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
package com.qcadoo.plugin.internal.accessor;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qcadoo.model.beans.qcadooPlugin.QcadooPluginPlugin;
import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginState;
import com.qcadoo.plugin.api.Version;
import com.qcadoo.plugin.internal.api.InternalPlugin;
import com.qcadoo.plugin.internal.api.ModuleFactoryAccessor;
import com.qcadoo.plugin.internal.api.PluginDao;
import com.qcadoo.plugin.internal.api.PluginDependencyManager;
import com.qcadoo.plugin.internal.api.PluginDescriptorParser;
import com.qcadoo.plugin.internal.stateresolver.InternalPluginStateResolver;

public class PluginAccessorTest {

    private final PluginDescriptorParser pluginDescriptorParser = mock(PluginDescriptorParser.class);

    private final PluginDao pluginDao = Mockito.mock(PluginDao.class);

    private final PluginDependencyManager pluginDependencyManager = mock(PluginDependencyManager.class);

    private final ModuleFactoryAccessor moduleFactoryAccessor = mock(ModuleFactoryAccessor.class);

    private final InternalPluginStateResolver pluginStateResolver = mock(InternalPluginStateResolver.class);

    private DefaultPluginAccessor pluginAccessor;

    @Before
    public void init() {
        pluginAccessor = new DefaultPluginAccessor();
        pluginAccessor.setPluginDescriptorParser(pluginDescriptorParser);
        pluginAccessor.setPluginDao(pluginDao);
        pluginAccessor.setPluginDependencyManager(pluginDependencyManager);
        pluginAccessor.setModuleFactoryAccessor(moduleFactoryAccessor);
        pluginAccessor.setInternalPluginStateResolver(pluginStateResolver);
    }

    @Test
    public void shouldname() throws Exception {
        Assert.assertTrue(true);
    }

    @Test
    public void shouldSynchronizePluginsFromClasspathAndDatabase() throws Exception {
        // given
        InternalPlugin plugin1 = mock(InternalPlugin.class);
        QcadooPluginPlugin pluginsPlugin1 = mock(QcadooPluginPlugin.class);
        given(plugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getState()).willReturn("ENABLED");
        given(pluginsPlugin1.getVersion()).willReturn("0.0.0");
        given(plugin1.compareVersion(new Version(pluginsPlugin1.getVersion()))).willReturn(0);

        QcadooPluginPlugin pluginsPlugin21 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin21.getIdentifier()).willReturn("identifier21");
        given(pluginsPlugin21.getIdentifier()).willReturn("identifier21");
        given(pluginsPlugin21.getState()).willReturn("ENABLED");
        InternalPlugin plugin22 = mock(InternalPlugin.class);
        given(plugin22.getIdentifier()).willReturn("identifier21");
        given(pluginsPlugin21.getVersion()).willReturn("0.0.0");
        given(plugin22.compareVersion(new Version(pluginsPlugin21.getVersion()))).willReturn(1);

        InternalPlugin plugin3 = mock(InternalPlugin.class);
        given(plugin3.getIdentifier()).willReturn("identifier3");
        QcadooPluginPlugin pluginsPlugin4 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin4.getIdentifier()).willReturn("identifier4");
        given(pluginsPlugin4.getState()).willReturn("ENABLED");

        Set<InternalPlugin> pluginsFromDescriptor = Sets.newHashSet(plugin1, plugin22, plugin3);
        Set<QcadooPluginPlugin> pluginsFromDatabase = Sets.newHashSet(pluginsPlugin1, pluginsPlugin21, pluginsPlugin4);

        given(pluginDescriptorParser.loadPlugins()).willReturn(pluginsFromDescriptor);

        given(pluginDao.list()).willReturn(pluginsFromDatabase);

        // when
        pluginAccessor.init();

        // then
        verify(pluginDao, never()).save(plugin1);
        verify(plugin22).changeStateTo(PluginState.ENABLED);
        verify(pluginDao).save(plugin22);
        verify(pluginDao).save(plugin3);
        verify(pluginDao).delete(pluginsPlugin4);
    }

    @Test
    public void shouldListAllPlugins() throws Exception {
        // given
        InternalPlugin plugin1 = mock(InternalPlugin.class, "plugin1");
        QcadooPluginPlugin pluginsPlugin1 = mock(QcadooPluginPlugin.class, "plugin1");
        given(plugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getVersion()).willReturn("0.0.0");
        given(plugin1.compareVersion(new Version(pluginsPlugin1.getVersion()))).willReturn(0);
        given(plugin1.hasState(PluginState.ENABLED)).willReturn(false);
        given(pluginsPlugin1.getState()).willReturn("DISABLED");

        QcadooPluginPlugin pluginsPlugin21 = mock(QcadooPluginPlugin.class, "plugin21");
        given(pluginsPlugin21.getIdentifier()).willReturn("identifier21");
        given(pluginsPlugin21.getState()).willReturn("ENABLED");
        InternalPlugin plugin22 = mock(InternalPlugin.class, "plugin22");
        given(plugin22.getIdentifier()).willReturn("identifier21");
        given(plugin22.hasState(PluginState.ENABLED)).willReturn(true);
        given(pluginsPlugin21.getVersion()).willReturn("0.0.0");
        given(plugin22.compareVersion(new Version(pluginsPlugin21.getVersion()))).willReturn(1);

        InternalPlugin plugin3 = mock(InternalPlugin.class, "plugin3");
        QcadooPluginPlugin pluginsPlugin3 = mock(QcadooPluginPlugin.class, "plugin3");
        given(plugin3.getIdentifier()).willReturn("identifier3");
        given(pluginsPlugin3.getIdentifier()).willReturn("identifier3");
        given(pluginsPlugin3.getVersion()).willReturn("0.0.0");
        given(plugin3.hasState(PluginState.ENABLED)).willReturn(true);
        given(plugin3.isSystemPlugin()).willReturn(true);
        given(pluginsPlugin3.getState()).willReturn("ENABLED");

        InternalPlugin plugin4 = mock(InternalPlugin.class, "plugin4");
        given(plugin4.getIdentifier()).willReturn("identifier4");
        given(plugin4.hasState(PluginState.ENABLED)).willReturn(false);

        Set<InternalPlugin> pluginsFromDescriptor = Sets.newHashSet(plugin1, plugin22, plugin3, plugin4);
        Set<QcadooPluginPlugin> pluginsFromDatabase = Sets.newHashSet(pluginsPlugin1, pluginsPlugin21, pluginsPlugin3);

        given(pluginDescriptorParser.loadPlugins()).willReturn(pluginsFromDescriptor);

        given(pluginDao.list()).willReturn(pluginsFromDatabase);

        // when
        pluginAccessor.init();

        // then
        verify(plugin1).changeStateTo(PluginState.DISABLED);
        verify(plugin22).changeStateTo(PluginState.ENABLED);
        verify(plugin3).changeStateTo(PluginState.ENABLED);
        verify(plugin4).changeStateTo(PluginState.ENABLING);

        assertThat(pluginAccessor.getPlugins(), hasItems((Plugin) plugin1, (Plugin) plugin22, (Plugin) plugin3, (Plugin) plugin4));

        assertThat(pluginAccessor.getEnabledPlugins(), hasItems((Plugin) plugin22, (Plugin) plugin3));
        assertThat(pluginAccessor.getEnabledPlugins(), not(hasItem((Plugin) plugin1)));
        assertThat(pluginAccessor.getEnabledPlugins(), not(hasItem((Plugin) plugin4)));
        assertThat(pluginAccessor.getSystemPlugins(), hasItem((Plugin) plugin3));

        assertEquals(plugin1, pluginAccessor.getPlugin("identifier1"));
        assertEquals(plugin22, pluginAccessor.getPlugin("identifier21"));
        assertEquals(plugin3, pluginAccessor.getPlugin("identifier3"));
        assertEquals(plugin4, pluginAccessor.getPlugin("identifier4"));
        assertNull(pluginAccessor.getEnabledPlugin("identifier1"));
        assertEquals(plugin22, pluginAccessor.getEnabledPlugin("identifier21"));
        assertEquals(plugin3, pluginAccessor.getEnabledPlugin("identifier3"));
        assertNull(pluginAccessor.getEnabledPlugin("identifier4"));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnPluginDowngrade() throws Exception {
        // given
        InternalPlugin plugin11 = mock(InternalPlugin.class);
        given(plugin11.getIdentifier()).willReturn("identifier11");
        QcadooPluginPlugin pluginsPlugin12 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin12.getIdentifier()).willReturn("identifier11");
        given(pluginsPlugin12.getVersion()).willReturn("0.0.0");
        given(pluginsPlugin12.getState()).willReturn("ENABLED");
        given(plugin11.compareVersion(new Version(pluginsPlugin12.getVersion()))).willReturn(-1);

        Set<InternalPlugin> pluginsFromDescriptor = Sets.newHashSet(plugin11);
        Set<QcadooPluginPlugin> pluginsFromDatabase = Sets.newHashSet(pluginsPlugin12);

        given(pluginDescriptorParser.loadPlugins()).willReturn(pluginsFromDescriptor);

        given(pluginDao.list()).willReturn(pluginsFromDatabase);

        // when
        pluginAccessor.init();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformInit() throws Exception {
        // given
        InternalPlugin plugin1 = mock(InternalPlugin.class, "plugin1");
        QcadooPluginPlugin pluginsPlugin1 = mock(QcadooPluginPlugin.class, "plugin1");
        given(plugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getState()).willReturn("ENABLED");
        given(pluginsPlugin1.getVersion()).willReturn("0.0.0");
        InternalPlugin plugin2 = mock(InternalPlugin.class, "plugin2");
        QcadooPluginPlugin pluginsPlugin2 = mock(QcadooPluginPlugin.class, "plugin2");
        given(plugin2.getIdentifier()).willReturn("identifier2");
        given(pluginsPlugin2.getIdentifier()).willReturn("identifier2");
        given(pluginsPlugin2.getVersion()).willReturn("0.0.0");
        given(pluginsPlugin2.getState()).willReturn("ENABLED");

        Set<InternalPlugin> pluginsFromDescriptor = Sets.newHashSet(plugin1, plugin2);
        Set<QcadooPluginPlugin> pluginsFromDatabase = Sets.newHashSet(pluginsPlugin1, pluginsPlugin2);
        List<Plugin> sortedPluginsToInitialize = Lists.newArrayList((Plugin) plugin2, (Plugin) plugin1);

        given(pluginDependencyManager.sortPluginsInDependencyOrder(Mockito.anyCollectionOf(Plugin.class), Mockito.anyMap()))
                .willReturn(sortedPluginsToInitialize);

        given(pluginDescriptorParser.loadPlugins()).willReturn(pluginsFromDescriptor);

        given(pluginDao.list()).willReturn(pluginsFromDatabase);

        // when
        pluginAccessor.init();
        pluginAccessor.onApplicationEvent(null);

        // then
        InOrder inOrder = inOrder(plugin2, plugin1, moduleFactoryAccessor);
        inOrder.verify(moduleFactoryAccessor).init(sortedPluginsToInitialize);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformEnableOnEnablingPlugins() throws Exception {
        // given
        InternalPlugin plugin1 = mock(InternalPlugin.class);
        QcadooPluginPlugin pluginsPlugin1 = mock(QcadooPluginPlugin.class);
        given(plugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getState()).willReturn("ENABLING");
        given(pluginsPlugin1.getVersion()).willReturn("0.0.0");
        given(plugin1.hasState(PluginState.ENABLING)).willReturn(true);
        InternalPlugin plugin2 = mock(InternalPlugin.class);
        QcadooPluginPlugin pluginsPlugin2 = mock(QcadooPluginPlugin.class);
        given(plugin2.getIdentifier()).willReturn("identifier2");
        given(pluginsPlugin2.getIdentifier()).willReturn("identifier2");
        given(pluginsPlugin2.getState()).willReturn("ENABLING");
        given(pluginsPlugin2.getVersion()).willReturn("0.0.0");
        given(plugin2.hasState(PluginState.ENABLING)).willReturn(true);
        InternalPlugin plugin3 = mock(InternalPlugin.class);
        QcadooPluginPlugin pluginsPlugin3 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin3.getIdentifier()).willReturn("identifier3");
        given(pluginsPlugin3.getVersion()).willReturn("0.0.0");
        given(pluginsPlugin3.getState()).willReturn("DISABLED");
        given(plugin3.getIdentifier()).willReturn("identifier3");

        Set<InternalPlugin> pluginsFromDescriptor = Sets.newHashSet(plugin1, plugin2, plugin3);
        Set<QcadooPluginPlugin> pluginsFromDatabase = Sets.newHashSet(pluginsPlugin1, pluginsPlugin2, pluginsPlugin3);
        List<Plugin> sortedPluginsToInitialize = Lists.newArrayList((Plugin) plugin2, (Plugin) plugin1);

        given(pluginDependencyManager.sortPluginsInDependencyOrder(Mockito.anyCollectionOf(Plugin.class), Mockito.anyMap()))
                .willReturn(sortedPluginsToInitialize);

        given(pluginDescriptorParser.loadPlugins()).willReturn(pluginsFromDescriptor);

        given(pluginDao.list()).willReturn(pluginsFromDatabase);

        // when
        pluginAccessor.init();
        pluginAccessor.onApplicationEvent(null);

        // then
        InOrder inOrder = inOrder(plugin2, plugin1, moduleFactoryAccessor);
        inOrder.verify(moduleFactoryAccessor).init(sortedPluginsToInitialize);
        inOrder.verify(plugin2).changeStateTo(PluginState.ENABLED);
        inOrder.verify(plugin1).changeStateTo(PluginState.ENABLED);
        verify(plugin3, never()).changeStateTo(PluginState.ENABLED);
        verify(pluginDao).save(plugin1);
        verify(pluginDao).save(plugin2);
        verify(pluginDao, never()).save(plugin3);
    }

    @Test
    public void shouldNotDeleteTemporaryPlugins() throws Exception {
        // given
        QcadooPluginPlugin pluginsPlugin1 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin1.getState()).willReturn(PluginState.TEMPORARY.toString());
        QcadooPluginPlugin pluginsPlugin2 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin2.getState()).willReturn(PluginState.ENABLED.toString());

        Set<InternalPlugin> pluginsFromDescriptor = Sets.newHashSet();
        Set<QcadooPluginPlugin> pluginsFromDatabase = Sets.newHashSet(pluginsPlugin1, pluginsPlugin2);

        given(pluginDescriptorParser.loadPlugins()).willReturn(pluginsFromDescriptor);
        given(pluginDao.list()).willReturn(pluginsFromDatabase);

        // when
        pluginAccessor.init();

        verify(pluginDao, never()).delete(pluginsPlugin1);
        verify(pluginDao).delete(pluginsPlugin2);
    }

    @Test
    public void shouldRemovePlugin() throws Exception {
        // given
        InternalPlugin plugin1 = mock(InternalPlugin.class, "plugin1");

        given(plugin1.getIdentifier()).willReturn("identifier1");

        QcadooPluginPlugin pluginsPlugin1 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getState()).willReturn("ENABLED");
        given(pluginsPlugin1.getVersion()).willReturn("0.0.0");

        InternalPlugin plugin2 = mock(InternalPlugin.class);
        given(plugin2.getIdentifier()).willReturn("identifier2");

        QcadooPluginPlugin pluginsPlugin2 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin2.getIdentifier()).willReturn("identifier2");
        given(pluginsPlugin2.getState()).willReturn("ENABLED");
        given(pluginsPlugin2.getVersion()).willReturn("0.0.0");

        given(pluginDescriptorParser.loadPlugins()).willReturn(Sets.newHashSet(plugin1, plugin2));

        given(pluginDao.list()).willReturn(Sets.<QcadooPluginPlugin> newHashSet(pluginsPlugin1, pluginsPlugin2));

        pluginAccessor.init();

        // when
        pluginAccessor.removePlugin(plugin1);

        // then
        assertEquals(1, pluginAccessor.getPlugins().size());
        assertThat(pluginAccessor.getPlugins(), hasItems((Plugin) plugin2));
    }

    @Test
    public void shouldSaveNewPlugin() throws Exception {
        // given
        InternalPlugin plugin1 = mock(InternalPlugin.class, "plugin1");

        given(plugin1.getIdentifier()).willReturn("identifier1");

        QcadooPluginPlugin pluginsPlugin1 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin1.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin1.getState()).willReturn("ENABLED");
        given(pluginsPlugin1.getVersion()).willReturn("0.0.0");

        InternalPlugin plugin2 = mock(InternalPlugin.class);
        given(plugin2.getIdentifier()).willReturn("identifier2");

        given(pluginDescriptorParser.loadPlugins()).willReturn(Sets.newHashSet(plugin1));

        given(pluginDao.list()).willReturn(Sets.<QcadooPluginPlugin> newHashSet(pluginsPlugin1));

        pluginAccessor.init();

        // when
        pluginAccessor.savePlugin(plugin2);

        // then
        assertThat(pluginAccessor.getPlugins(), hasItems((Plugin) plugin1, (Plugin) plugin2));
    }

    @Test
    public void shouldSaveExistingPlugin() throws Exception {
        // given
        InternalPlugin plugin1 = mock(InternalPlugin.class, "plugin1");

        given(plugin1.getIdentifier()).willReturn("identifier1");

        InternalPlugin plugin2 = mock(InternalPlugin.class);
        given(plugin2.getIdentifier()).willReturn("identifier1");

        QcadooPluginPlugin pluginsPlugin2 = mock(QcadooPluginPlugin.class);
        given(pluginsPlugin2.getIdentifier()).willReturn("identifier1");
        given(pluginsPlugin2.getState()).willReturn("ENABLED");
        given(pluginsPlugin2.getVersion()).willReturn("0.0.0");

        given(pluginDescriptorParser.loadPlugins()).willReturn(Sets.newHashSet(plugin1));

        given(pluginDao.list()).willReturn(Sets.<QcadooPluginPlugin> newHashSet(pluginsPlugin2));

        pluginAccessor.init();

        // when
        pluginAccessor.savePlugin(plugin2);

        // then
        assertEquals(1, pluginAccessor.getPlugins().size());
        assertThat(pluginAccessor.getPlugins(), hasItems((Plugin) plugin2));
    }

}
