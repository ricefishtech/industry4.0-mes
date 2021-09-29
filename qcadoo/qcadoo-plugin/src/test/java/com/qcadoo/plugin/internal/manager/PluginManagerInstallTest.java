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

import com.qcadoo.plugin.api.*;
import com.qcadoo.plugin.api.artifact.PluginArtifact;
import com.qcadoo.plugin.internal.PluginException;
import com.qcadoo.plugin.internal.api.*;
import com.qcadoo.plugin.internal.dependencymanager.SimplePluginStatusResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.File;
import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class PluginManagerInstallTest {

    private final InternalPlugin plugin = mock(InternalPlugin.class);

    private final InternalPlugin anotherPlugin = mock(InternalPlugin.class);

    private final InternalPluginAccessor pluginAccessor = mock(InternalPluginAccessor.class);

    private final PluginDao pluginDao = mock(PluginDao.class);

    private final PluginDependencyManager pluginDependencyManager = mock(PluginDependencyManager.class);

    private final PluginFileManager pluginFileManager = mock(PluginFileManager.class);

    private final PluginDescriptorParser pluginDescriptorParser = mock(PluginDescriptorParser.class);

    private final PluginArtifact pluginArtifact = mock(PluginArtifact.class);

    private DefaultPluginManager pluginManager;

    private final File file = mock(File.class, RETURNS_DEEP_STUBS);

    @Before
    public void init() {
        given(pluginAccessor.getPlugin("pluginname")).willReturn(plugin);

        given(anotherPlugin.getIdentifier()).willReturn("pluginname");
        given(anotherPlugin.getVersion()).willReturn(new Version("1.2.5"));
        given(plugin.getVersion()).willReturn(new Version("1.2.4"));

        pluginManager = new DefaultPluginManager();
        pluginManager.setPluginAccessor(pluginAccessor);
        pluginManager.setPluginDao(pluginDao);
        pluginManager.setPluginDependencyManager(pluginDependencyManager);
        pluginManager.setPluginFileManager(pluginFileManager);
        pluginManager.setPluginDescriptorParser(pluginDescriptorParser);
    }

    @Test
    public void shouldInstallTemporaryPlugin() throws Exception {
        // given

        given(plugin.hasState(PluginState.TEMPORARY)).willReturn(true);
        given(plugin.getState()).willReturn(PluginState.TEMPORARY);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(plugin.getFilename()).willReturn("filename");
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(file.getName()).willReturn("tempFileName");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao).save(anotherPlugin);
        verify(pluginAccessor).savePlugin(anotherPlugin);
        verify(anotherPlugin).changeStateTo(plugin.getState());
        verify(pluginFileManager).uninstallPlugin("filename");
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldFailureWithCorruptedPluginOnInstall() throws Exception {
        // given
        given(pluginDescriptorParser.parse(file)).willThrow(new PluginException(""));
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);
        given(file.getName()).willReturn("filename");

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(Mockito.any(InternalPlugin.class));
        verify(pluginAccessor, never()).savePlugin(Mockito.any(InternalPlugin.class));
        verify(pluginFileManager).uninstallPlugin("filename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.CORRUPTED_PLUGIN, pluginOperationResult.getStatus());
    }

    @Test
    public void shouldFailureOnUploadingPluginOnInstall() throws Exception {
        // given
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willThrow(new PluginException(""));

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(Mockito.any(InternalPlugin.class));
        verify(pluginAccessor, never()).savePlugin(Mockito.any(InternalPlugin.class));
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.CANNOT_UPLOAD_PLUGIN, pluginOperationResult.getStatus());
    }

    @Test
    public void shouldInstallTemporaryPluginAndNotifyAboutMissingDependencies() throws Exception {
        // given

        given(plugin.hasState(PluginState.TEMPORARY)).willReturn(true);
        given(plugin.getState()).willReturn(PluginState.TEMPORARY);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(plugin.getFilename()).willReturn("filename");

        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(file.getName()).willReturn("tempFileName");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.unsatisfiedDependencies(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(null))));
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao).save(anotherPlugin);
        verify(pluginAccessor).savePlugin(anotherPlugin);
        verify(anotherPlugin).changeStateTo(plugin.getState());
        verify(pluginFileManager).uninstallPlugin("filename");
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS_WITH_MISSING_DEPENDENCIES, pluginOperationResult.getStatus());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies()
                .contains(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldInstallDisabledPlugin() throws Exception {
        // given

        given(plugin.hasState(PluginState.DISABLED)).willReturn(true);
        given(plugin.getState()).willReturn(PluginState.DISABLED);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(plugin.getFilename()).willReturn("filename");
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(file.getName()).willReturn("tempFileName");

        given(pluginFileManager.installPlugin("anotherFilename")).willReturn(true);

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao).save(anotherPlugin);
        verify(pluginAccessor).savePlugin(anotherPlugin);
        verify(anotherPlugin).changeStateTo(plugin.getState());
        verify(pluginFileManager).uninstallPlugin("filename");
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS_WITH_RESTART, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldFailureInstallDisabledPluginWithMissingDependencies() throws Exception {
        // given

        given(plugin.hasState(PluginState.DISABLED)).willReturn(true);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(anotherPlugin.getFilename()).willReturn("filename");
        given(file.getName()).willReturn("tempFileName");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.unsatisfiedDependencies(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(anotherPlugin, never()).changeStateTo(plugin.getState());
        verify(pluginFileManager).uninstallPlugin("filename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.UNSATISFIED_DEPENDENCIES, pluginOperationResult.getStatus());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies()
                .contains(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldInstallEnabledPlugin() throws Exception {
        // given
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(plugin.getState()).willReturn(PluginState.ENABLED);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(pluginFileManager.installPlugin("anotherFilename")).willReturn(true);
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(file.getName()).willReturn("tempFileName");
        given(plugin.getFilename()).willReturn("filename");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        InternalPlugin dependencyPlugin = mock(InternalPlugin.class);
        given(pluginAccessor.getPlugin("dependencyplugin")).willReturn(dependencyPlugin);
        given(dependencyPlugin.getIdentifier()).willReturn("dependencyplugin");

        InternalPlugin dependencyPlugin2 = mock(InternalPlugin.class);
        given(pluginAccessor.getPlugin("dependencyplugin2")).willReturn(dependencyPlugin2);
        given(dependencyPlugin2.getIdentifier()).willReturn("dependencyplugin2");

        PluginDependencyResult installPluginDependencyResult = PluginDependencyResultImpl.dependenciesToDisable(newHashSet(
                new PluginDependencyInformation("dependencyplugin", new VersionOfDependency("")),
                new PluginDependencyInformation("dependencyplugin2", new VersionOfDependency(""))));

        given(
                pluginDependencyManager.getDependenciesToUpdate(Mockito.eq(plugin), Mockito.eq(anotherPlugin),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(installPluginDependencyResult);
        given(
                pluginDependencyManager.sortPluginsInDependencyOrder(newArrayList((Plugin) dependencyPlugin2,
                        (Plugin) dependencyPlugin))).willReturn(
                newArrayList((Plugin) dependencyPlugin, (Plugin) dependencyPlugin2));

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        InOrder inOrderDisable = inOrder(dependencyPlugin2, dependencyPlugin, plugin);
        inOrderDisable.verify(dependencyPlugin2).changeStateTo(PluginState.DISABLED);
        inOrderDisable.verify(dependencyPlugin).changeStateTo(PluginState.DISABLED);
        inOrderDisable.verify(plugin).changeStateTo(PluginState.DISABLED);
        InOrder inOrderEnabling = inOrder(anotherPlugin, dependencyPlugin, dependencyPlugin2);
        inOrderEnabling.verify(anotherPlugin).changeStateTo(PluginState.ENABLING);
        inOrderEnabling.verify(dependencyPlugin).changeStateTo(PluginState.ENABLING);
        inOrderEnabling.verify(dependencyPlugin2).changeStateTo(PluginState.ENABLING);
        verify(pluginDao).save(anotherPlugin);
        verify(pluginAccessor).savePlugin(anotherPlugin);
        verify(pluginDao).save(dependencyPlugin);
        verify(pluginDao).save(dependencyPlugin2);
        verify(pluginFileManager).uninstallPlugin("filename");
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS_WITH_RESTART, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldFailureInstallEnabledPluginWithUnsatisfiedDependenciesAfterUpdate() throws Exception {
        // given
        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(plugin.getState()).willReturn(PluginState.ENABLED);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(pluginFileManager.installPlugin("anotherFilename")).willReturn(true);
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(file.getName()).willReturn("tempFileName");
        given(plugin.getFilename()).willReturn("filename");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        PluginDependencyResult installPluginDependencyResult = PluginDependencyResultImpl.dependenciesToUpdate(Collections
                .<PluginDependencyInformation> emptySet(), newHashSet(new PluginDependencyInformation("dependencyplugin",
                new VersionOfDependency(""))));

        given(
                pluginDependencyManager.getDependenciesToUpdate(Mockito.eq(plugin), Mockito.eq(anotherPlugin),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(installPluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(anotherPlugin, never()).changeStateTo(plugin.getState());
        verify(pluginFileManager).uninstallPlugin("anotherFilename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.UNSATISFIED_DEPENDENCIES_AFTER_UPDATE, pluginOperationResult.getStatus());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getDependenciesToDisableUnsatisfiedAfterUpdate().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getDependenciesToDisableUnsatisfiedAfterUpdate()
                .contains(new PluginDependencyInformation("dependencyplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldFailureInstallEnabledPluginWithUnsitisfiedDependencies() throws Exception {
        // given

        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(anotherPlugin.getFilename()).willReturn("filename");
        given(file.getName()).willReturn("tempFileName");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.unsatisfiedDependencies(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(anotherPlugin, never()).changeStateTo(plugin.getState());
        verify(pluginFileManager).uninstallPlugin("filename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.UNSATISFIED_DEPENDENCIES, pluginOperationResult.getStatus());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies()
                .contains(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldFailureInstallEnabledPluginWithDisabledDependencies() throws Exception {
        // given

        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(anotherPlugin.getFilename()).willReturn("filename");
        given(file.getName()).willReturn("tempFileName");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.dependenciesToEnable(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(anotherPlugin, never()).changeStateTo(plugin.getState());
        verify(pluginFileManager).uninstallPlugin("filename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.DEPENDENCIES_TO_ENABLE, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable()
                .contains(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
    }

    @Test
    public void shouldNotInstallDisabledPluginIfCannotInstall() throws Exception {
        // given

        given(plugin.hasState(PluginState.DISABLED)).willReturn(true);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(plugin.getFilename()).willReturn("filename");
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(pluginFileManager.installPlugin("anotherFilename")).willReturn(false);
        given(file.getName()).willReturn("tempFileName");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(anotherPlugin, never()).changeStateTo(plugin.getState());
        verify(pluginFileManager, never()).uninstallPlugin("filename");
        verify(pluginFileManager).uninstallPlugin("anotherFilename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.CANNOT_INSTALL_PLUGIN_FILE, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldNotInstallEnabledPluginIfCannotInstall() throws Exception {
        // given

        given(plugin.hasState(PluginState.ENABLED)).willReturn(true);
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);

        given(plugin.getFilename()).willReturn("filename");
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(pluginFileManager.installPlugin("anotherFilename")).willReturn(false);
        given(file.getName()).willReturn("tempFileName");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(anotherPlugin, never()).changeStateTo(plugin.getState());
        verify(pluginFileManager, never()).uninstallPlugin("filename");
        verify(pluginFileManager).uninstallPlugin("anotherFilename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.CANNOT_INSTALL_PLUGIN_FILE, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldNotInstallSystemPlugin() throws Exception {
        // given
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);
        given(anotherPlugin.isSystemPlugin()).willReturn(true);
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(file.getName()).willReturn("tempFileName");

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(pluginFileManager).uninstallPlugin("anotherFilename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SYSTEM_PLUGIN_UPDATING, pluginOperationResult.getStatus());
    }

    @Test
    public void shouldInstallNotExistingPlugin() throws Exception {
        // given
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);
        given(anotherPlugin.getIdentifier()).willReturn("notExistingPluginname");
        given(file.getName()).willReturn("tempFileName");
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao).save(anotherPlugin);
        verify(pluginAccessor).savePlugin(anotherPlugin);
        verify(anotherPlugin).changeStateTo(PluginState.TEMPORARY);
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
    }

    @Test
    public void shouldInstallNotExistingPluginAndNotifyAboutMissingDependencies() throws Exception {
        // given
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);
        given(anotherPlugin.getIdentifier()).willReturn("notExistingPluginname");
        given(file.getName()).willReturn("tempFileName");
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");

        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.unsatisfiedDependencies(Collections
                .singleton(new PluginDependencyInformation("unknownplugin", new VersionOfDependency(""))));
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao).save(anotherPlugin);
        verify(pluginAccessor).savePlugin(anotherPlugin);
        assertTrue(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS_WITH_MISSING_DEPENDENCIES, pluginOperationResult.getStatus());
        assertEquals(0, pluginOperationResult.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals(1, pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies().size());
        assertTrue(pluginOperationResult.getPluginDependencyResult().getUnsatisfiedDependencies()
                .contains(new PluginDependencyInformation("unknownplugin")));
    }

    @Test
    public void shouldNotInstallPluginWithIncorrectVersion() throws Exception {
        // given
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);
        given(anotherPlugin.getVersion()).willReturn(new Version("1.2.0"));
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(file.getName()).willReturn("tempFileName");
        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.satisfiedDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(pluginFileManager).uninstallPlugin("anotherFilename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.CANNOT_DOWNGRADE_PLUGIN, pluginOperationResult.getStatus());
    }

    @Test
    public void shouldNotInstallPluginWithCyclicDependencies() throws Exception {
        // given
        given(pluginDescriptorParser.parse(file)).willReturn(anotherPlugin);
        given(pluginFileManager.uploadPlugin(pluginArtifact)).willReturn(file);
        given(anotherPlugin.getFilename()).willReturn("anotherFilename");
        given(file.getName()).willReturn("tempFileName");
        PluginDependencyResult pluginDependencyResult = PluginDependencyResultImpl.cyclicDependencies();
        given(
                pluginDependencyManager.getDependenciesToEnable(Mockito.eq(newArrayList((Plugin) anotherPlugin)),
                        Mockito.any(SimplePluginStatusResolver.class))).willReturn(pluginDependencyResult);

        // when
        PluginOperationResult pluginOperationResult = pluginManager.installPlugin(pluginArtifact);

        // then
        verify(pluginDao, never()).save(anotherPlugin);
        verify(pluginAccessor, never()).savePlugin(anotherPlugin);
        verify(pluginFileManager).uninstallPlugin("anotherFilename");
        assertFalse(pluginOperationResult.isSuccess());
        assertEquals(PluginOperationStatus.DEPENDENCIES_CYCLES_EXISTS, pluginOperationResult.getStatus());
    }

}
