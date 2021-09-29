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
package com.qcadoo.plugin.internal.filemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.qcadoo.plugin.api.artifact.PluginArtifact;
import com.qcadoo.plugin.internal.PluginException;

public class PluginFileManagerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File pluginTmpFile;

    private File source;

    private File destination;

    private DefaultPluginFileManager defaultPluginFileManager;

    @Before
    public void init() throws IOException {
        source = folder.newFolder("source");
        destination = folder.newFolder("destination");
        pluginTmpFile = new File(source, "pluginname.jar");
        FileUtils.touch(pluginTmpFile);
        defaultPluginFileManager = new DefaultPluginFileManager();
        defaultPluginFileManager.setPluginsPath(destination.getAbsolutePath());
        defaultPluginFileManager.setPluginsTmpPath(source.getAbsolutePath());
    }

    @Test
    public void shouldFailureInstallPluginFileWhenDestinationNotExist() throws Exception {
        // given
        defaultPluginFileManager.setPluginsPath("notExistingFolder");

        // when
        boolean result = defaultPluginFileManager.installPlugin("pluginname.jar");

        // then
        assertFalse(result);
    }

    @Test(expected = PluginException.class)
    public void shouldThrowExceptionOnInstallingPluginFileWhenDestinationFileExist() throws Exception {
        // given
        defaultPluginFileManager.setPluginsPath(source.getAbsolutePath());

        // when
        defaultPluginFileManager.installPlugin("pluginname.jar");

        // then
    }

    @Test
    public void shouldInstallPluginFile() throws Exception {
        // given
        File pluginFile = new File(destination, "pluginname.jar");

        // when
        boolean result = defaultPluginFileManager.installPlugin("pluginname.jar");

        // then
        assertTrue(result);
        assertFalse(pluginTmpFile.exists());
        assertTrue(pluginFile.exists());
    }

    @Test
    public void shouldFailureInstallPluginFileWhenSourceNotExist() throws Exception {
        // given

        // when
        boolean result = defaultPluginFileManager.installPlugin("notExistingPluginname.jar");

        // then
        assertFalse(result);
    }

    @Test
    public void shouldUninstallTemporaryPluginFile() throws Exception {
        // given

        // when
        defaultPluginFileManager.uninstallPlugin("pluginname.jar");

        // then
        assertFalse(pluginTmpFile.exists());
    }

    @Test
    public void shouldUninstallNotTemporaryPluginFile() throws Exception {
        // given
        File pluginFile = new File(destination, "uninstallpluginname.jar");
        FileUtils.touch(pluginFile);

        // when
        defaultPluginFileManager.uninstallPlugin("uninstallpluginname.jar");

        // then
        assertFalse(pluginFile.exists());
    }

    @Test
    public void shouldUploadPluginFile() throws Exception {
        // given
        File newPluginFile = new File(source, "newpluginname.jar");
        FileUtils.touch(newPluginFile);

        PluginArtifact pluginArtifact = mock(PluginArtifact.class);
        given(pluginArtifact.getInputStream()).willReturn(new FileInputStream(newPluginFile));
        given(pluginArtifact.getName()).willReturn("uploadpluginname.jar");

        // when
        File pluginFile = defaultPluginFileManager.uploadPlugin(pluginArtifact);

        // then
        assertTrue(pluginFile.exists());
    }

    @Test(expected = PluginException.class)
    public void shouldThrowExceptionOnUploadingPluginFileWhenOperationFail() throws Exception {
        // given
        File newPluginFile = new File(source, "newpluginname.jar");
        FileUtils.touch(newPluginFile);

        PluginArtifact pluginArtifact = mock(PluginArtifact.class);
        given(pluginArtifact.getInputStream()).willReturn(new FileInputStream(newPluginFile));
        given(pluginArtifact.getName()).willReturn("");

        // when
        defaultPluginFileManager.uploadPlugin(pluginArtifact);

    }

    @After
    public void cleanUp() {
        folder.delete();
    }
}
