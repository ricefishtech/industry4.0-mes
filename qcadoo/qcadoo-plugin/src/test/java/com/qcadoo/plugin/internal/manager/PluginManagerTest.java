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
package com.qcadoo.plugin.internal.manager;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginDependencyInformation;
import com.qcadoo.plugin.api.PluginDependencyResult;
import com.qcadoo.plugin.api.PluginOperationResult;
import com.qcadoo.plugin.api.PluginOperationStatus;
import com.qcadoo.plugin.api.PluginState;
import com.qcadoo.plugin.api.VersionOfDependency;
import com.qcadoo.plugin.internal.api.InternalPlugin;
import com.qcadoo.plugin.internal.api.InternalPluginAccessor;
import com.qcadoo.plugin.internal.api.PluginDao;
import com.qcadoo.plugin.internal.api.PluginDependencyManager;
import com.qcadoo.plugin.internal.api.PluginDependencyResultImpl;
import com.qcadoo.plugin.internal.api.PluginDescriptorParser;
import com.qcadoo.plugin.internal.api.PluginFileManager;
import com.qcadoo.plugin.internal.dependencymanager.SimplePluginStatusResolver;

public class PluginManagerTest {

    private final InternalPlugin plugin = mock(InternalPlugin.class);

    private final InternalPlugin anotherPlugin = mock(InternalPlugin.class);

    private final InternalPluginAccessor pluginAccessor = mock(InternalPluginAccessor.class);

    private final PluginDao pluginDao = mock(PluginDao.class);

    private final PluginDependencyManager pluginDependencyManager = mock(PluginDependencyManager.class);

    private final PluginFileManager pluginFileManager = mock(PluginFileManager.class);

    private final PluginDescriptorParser pluginDescriptorParser = mock(PluginDescriptorParser.class);

    private DefaultPluginManager pluginManager;

    @Before
    public void init() {
        given(pluginAccessor.getPlugin("pluginname")).willReturn(plugin);
        given(pluginAccessor.getPlugin("anotherPluginname")).willReturn(anotherPlugin);

        pluginManager = new DefaultPluginManager();
        pluginManager.setPluginAccessor(pluginAccessor);
        pluginManager.setPluginDao(pluginDao);
        pluginManager.setPluginDependencyManager(pluginDependencyManager);
        pluginManager.setPluginFileManager(pluginFileManager);
        pluginManager.setPluginDescriptorParser(pluginDescriptorParser);
    }

    @Test
    public void shouldNotEnableEnabledPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.enablePlugin("pluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.ENABLED);
        verify(pluginDao, never()).save(plugin);
        verify(pluginAccessor, never()).savePlugin(plugin);
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, pluginOperationResult.getStatus());
    }

    @Test
    public void shouldEnableDisabledPlugin() throws Exception {
        // given
        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(singletonList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);
        given(pluginDependencyManager.sortPluginsInDependencyOrder(singletonList((Plugin) plugin))).willReturn(
                singletonList((Plugin) plugin));

        // when
        PluginOperationResult pluginOperationResult = pluginManager.enablePlugin("pluginname");

        // then
        verify(plugin).changeStateTo(PluginState.ENABLED);
        verify(pluginDao).save(plugin);
        verify(pluginAccessor).savePlugin(plugin);
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldEnableUninstalledPlugin() throws Exception {
        // given
        given(anotherPlugin.hasState(PluginState.TEMPORARY)).willReturn(true);
        given(anotherPlugin.getFilename()).willReturn("filename");
        given(pluginFileManager.installPlugin("filename")).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(singletonList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);
        given(pluginDependencyManager.sortPluginsInDependencyOrder(singletonList((Plugin) anotherPlugin))).willReturn(
                singletonList((Plugin) anotherPlugin));

        // when
        PluginOperationResult pluginOperationResult = pluginManager.enablePlugin("anotherPluginname");

        // then
        verify(anotherPlugin).changeStateTo(PluginState.ENABLING);
        verify(pluginDao).save(anotherPlugin);
        verify(pluginAccessor).savePlugin(anotherPlugin);

        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS_WITH_RESTART, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldNotEnablePluginIfCannotInstall() throws Exception {
        // given
        given(anotherPlugin.hasState(PluginState.TEMPORARY)).willReturn(true);
        given(anotherPlugin.getFilename()).willReturn("filename");
        given(pluginFileManager.installPlugin("filename")).willReturn(false);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(singletonList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.enablePlugin("anotherPluginname");

        // then
        verify(anotherPlugin, never()).changeStateTo(PluginState.ENABLING);
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(plugin);

        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.CANNOT_INSTALL_PLUGIN_FILE, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldNotEnablePluginWithDisabledDependencies() throws Exception {
        // given
        given(plugin.hasState(PluginState.DISABLED)).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.dependenciesToEnable(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(singletonList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.enablePlugin("pluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.ENABLED);
        verify(pluginDao, never()).save(plugin);
        verify(pluginAccessor, never()).savePlugin(plugin);
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.DEPENDENCIES_TO_ENABLE, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable()
                .contains(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldNotEnablePluginWithUnsatisfiedDependencies() throws Exception {
        // given
        given(plugin.hasState(PluginState.DISABLED)).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.unsatisfiedDependencies(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(singletonList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.enablePlugin("pluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.ENABLED);
        verify(pluginDao, never()).save(plugin);
        verify(pluginAccessor, never()).savePlugin(plugin);
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.UNSATISFIED_DEPENDENCIES, pluginOperationResult.getStatus());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies()
                .contains(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldEnableMultiplePlugins() throws Exception {
        // given
        given(plugin.hasState(PluginState.DISABLED)).willReturn(true);

        InternalPlugin nextPlugin = mock(InternalPlugin.class, "nextPlugin");
        given(nextPlugin.hasState(PluginState.DISABLED)).willReturn(true);
        given(pluginAccessor.getPlugin("nextPluginname")).willReturn(nextPlugin);

        given(anotherPlugin.hasState(PluginState.TEMPORARY)).willReturn(true);
        given(anotherPlugin.getFilename()).willReturn("filename");
        given(pluginFileManager.installPlugin("filename")).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(
                        Mockito.eq(newArrayList((Plugin) plugin, (Plugin) anotherPlugin, (Plugin) nextPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);
        given(
                pluginDependencyManager.sortPluginsInDependencyOrder(newArrayList((Plugin) plugin, (Plugin) anotherPlugin,
                        (Plugin) nextPlugin))).willReturn(
                newArrayList((Plugin) plugin, (Plugin) anotherPlugin, (Plugin) nextPlugin));

        // when
        PluginOperationResult pluginOperationResult = pluginManager.enablePlugin("pluginname", "anotherPluginname",
                "nextPluginname");

        // then
        InOrder inOrder = inOrder(plugin, anotherPlugin, nextPlugin);
        inOrder.verify(plugin).changeStateTo(PluginState.ENABLED);
        inOrder.verify(anotherPlugin).changeStateTo(PluginState.ENABLING);
        inOrder.verify(nextPlugin).changeStateTo(PluginState.ENABLED);
        verify(pluginDao).save(plugin);
        verify(pluginAccessor).savePlugin(plugin);
        verify(pluginDao).save(nextPlugin);
        verify(pluginAccessor).savePlugin(nextPlugin);
        verify(pluginDao).save(anotherPlugin);
        verify(pluginAccessor).savePlugin(anotherPlugin);
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS_WITH_RESTART, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());

    }

    @Test
    public void shouldNotDisableNotEnabledPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.ENABLED)).willReturn(false);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.disablePlugin("pluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.DISABLED);
        verify(pluginDao, never()).save(plugin);
        verify(pluginAccessor, never()).savePlugin(plugin);
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, pluginOperationResult.getStatus());
    }

    @Test
    public void shouldDisableEnabledPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToDisable(Mockito.eq(singletonList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);
        given(pluginDependencyManager.sortPluginsInDependencyOrder(singletonList((Plugin) plugin))).willReturn(
                singletonList((Plugin) plugin));

        // when
        PluginOperationResult pluginOperationResult = pluginManager.disablePlugin("pluginname");

        // then
        verify(plugin).changeStateTo(PluginState.DISABLED);
        verify(pluginDao).save(plugin);
        verify(pluginAccessor).savePlugin(plugin);
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToDisable().size());
    }

    @Test
    public void shouldNotDisableSystemPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(plugin.isSystemPlugin()).willReturn(true);

        given(anotherPlugin.hasState(PluginState.ENABLED)).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToDisable(
                        Mockito.eq(newArrayList((Plugin) plugin, (Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.disablePlugin("pluginname", "anotherPluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.DISABLED);
        verify(anotherPlugin, never()).changeStateTo(PluginState.DISABLED);
        verify(pluginDao, never()).save(plugin);
        verify(pluginAccessor, never()).savePlugin(plugin);
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SYSTEM_PLUGIN_DISABLING, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToDisable().size());
    }

    @Test
    public void shouldNotDisablePluginWithEnabledDependencies() throws Exception {
        // given
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.dependenciesToDisable(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
        given(
                pluginDependencyManager.getDependenciesToDisable(Mockito.eq(singletonList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.disablePlugin("pluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.DISABLED);
        verify(pluginDao, never()).save(plugin);
        verify(pluginAccessor, never()).savePlugin(plugin);
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.DEPENDENCIES_TO_DISABLE, pluginOperationResult.getStatus());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getDependenciesToDisable().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getDependenciesToDisable().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getDependenciesToDisable()
                .contains(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldUninstallNotTemporaryPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.TEMPORARY)).willReturn(false);
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToUninstall(Mockito.eq(singletonList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);
        given(pluginDependencyManager.sortPluginsInDependencyOrder(singletonList((Plugin) plugin))).willReturn(
                singletonList((Plugin) plugin));

        given(plugin.getFilename()).willReturn("filename");

        // when
        PluginOperationResult pluginOperationResult = pluginManager.uninstallPlugin("pluginname");

        // then
        verify(plugin).changeStateTo(PluginState.DISABLED);
        verify(pluginDao).delete(plugin);
        verify(pluginAccessor).removePlugin(plugin);

        verify(pluginFileManager).uninstallPlugin("filename");
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS_WITH_RESTART, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToUninstall().size());
    }

    @Test
    public void shouldUninstallTemporaryPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.TEMPORARY)).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToUninstall(Mockito.eq(singletonList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);
        given(pluginDependencyManager.sortPluginsInDependencyOrder(singletonList((Plugin) plugin))).willReturn(
                singletonList((Plugin) plugin));

        given(plugin.getFilename()).willReturn("filename");

        // when
        PluginOperationResult pluginOperationResult = pluginManager.uninstallPlugin("pluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.DISABLED);
        verify(pluginDao).delete(plugin);
        verify(pluginAccessor).removePlugin(plugin);

        verify(pluginFileManager).uninstallPlugin("filename");
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToUninstall().size());
    }

    @Test
    public void shouldNotUninstallPluginWithEnabledDependencies() throws Exception {
        // given
        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.dependenciesToUninstall(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
        given(
                pluginDependencyManager.getDependenciesToUninstall(Mockito.eq(singletonList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        given(plugin.getFilename()).willReturn("filename");

        // when
        PluginOperationResult pluginOperationResult = pluginManager.uninstallPlugin("pluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.DISABLED);
        verify(pluginDao, never()).delete(plugin);
        verify(pluginAccessor, never()).removePlugin(plugin);

        verify(pluginFileManager, never()).uninstallPlugin("filename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.DEPENDENCIES_TO_UNINSTALL, pluginOperationResult.getStatus());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getDependenciesToUninstall().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getDependenciesToUninstall()
                .contains(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldNotUninstallSystemPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.TEMPORARY)).willReturn(false);
        given(plugin.isSystemPlugin()).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToDisable(Mockito.eq(newArrayList((Plugin) plugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.uninstallPlugin("pluginname");

        // then
        verify(plugin, never()).changeStateTo(PluginState.DISABLED);
        verify(pluginDao, never()).delete(plugin);
        verify(pluginAccessor, never()).removePlugin(plugin);

        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SYSTEM_PLUGIN_UNINSTALLING, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToUninstall().size());
    }

    @Test
    public void shouldDisableMultipleEnabledPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);

        InternalPlugin nextPlugin = mock(InternalPlugin.class, "nextPlugin");
        given(nextPlugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(pluginAccessor.getPlugin("nextPluginname")).willReturn(nextPlugin);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToDisable(Mockito.eq(newArrayList((Plugin) plugin, (Plugin) nextPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);
        given(pluginDependencyManager.sortPluginsInDependencyOrder(newArrayList((Plugin) plugin, (Plugin) nextPlugin)))
                .willReturn(newArrayList((Plugin) plugin, (Plugin) nextPlugin));

        // when
        PluginOperationResult pluginOperationResult = pluginManager.disablePlugin("pluginname", "nextPluginname");

        // then
        InOrder inOrder = inOrder(nextPlugin, plugin);
        inOrder.verify(nextPlugin).changeStateTo(PluginState.DISABLED);
        inOrder.verify(plugin).changeStateTo(PluginState.DISABLED);
        verify(pluginDao).save(nextPlugin);
        verify(pluginAccessor).savePlugin(nextPlugin);
        verify(pluginDao).save(plugin);
        verify(pluginAccessor).savePlugin(plugin);
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToDisable().size());
    }

    @Test
    public void shouldUninstallMultiplePlugins() throws Exception {
        // given
        given(plugin.hasState(PluginState.TEMPORARY)).willReturn(false);
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(plugin.getFilename()).willReturn("filename");

        given(anotherPlugin.hasState(PluginState.TEMPORARY)).willReturn(true);
        given(anotherPlugin.hasState(PluginState.ENABLED)).willReturn(false);
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");

        InternalPlugin nextPlugin = mock(InternalPlugin.class, "nextPlugin");
        given(nextPlugin.hasState(PluginState.TEMPORARY)).willReturn(false);
        given(nextPlugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(nextPlugin.getFilename()).willReturn("nextPluginFilename");
        given(pluginAccessor.getPlugin("nextPluginname")).willReturn(nextPlugin);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToUninstall(
                        Mockito.eq(newArrayList((Plugin) plugin, (Plugin) anotherPlugin, (Plugin) nextPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);
        given(
                pluginDependencyManager.sortPluginsInDependencyOrder(newArrayList((Plugin) plugin, (Plugin) anotherPlugin,
                        (Plugin) nextPlugin))).willReturn(
                newArrayList((Plugin) nextPlugin, (Plugin) plugin, (Plugin) anotherPlugin));

        // when
        PluginOperationResult pluginOperationResult = pluginManager.uninstallPlugin("pluginname", "anotherPluginname",
                "nextPluginname");

        // then
        InOrder inOrder = inOrder(plugin, nextPlugin);
        inOrder.verify(plugin).changeStateTo(PluginState.DISABLED);
        inOrder.verify(nextPlugin).changeStateTo(PluginState.DISABLED);
        verify(anotherPlugin, never()).changeStateTo(PluginState.DISABLED);
        verify(pluginDao).delete(plugin);
        verify(pluginAccessor).removePlugin(plugin);
        verify(pluginDao).delete(nextPlugin);
        verify(pluginAccessor).removePlugin(nextPlugin);
        verify(pluginDao).delete(anotherPlugin);
        verify(pluginAccessor).removePlugin(anotherPlugin);
        verify(pluginFileManager).uninstallPlugin("filename", "anotherFilename", "nextPluginFilename");

        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS_WITH_RESTART, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToUninstall().size());
    }

}
