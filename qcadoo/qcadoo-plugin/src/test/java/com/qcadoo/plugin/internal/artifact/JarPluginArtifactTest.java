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
package com.qcadoo.plugin.internal.artifact;

import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.io.IOUtils.contentEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.qcadoo.plugin.api.artifact.JarPluginArtifact;
import com.qcadoo.plugin.api.artifact.PluginArtifact;

public class JarPluginArtifactTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldHaveFileName() throws Exception {
        // given
        File file = folder.newFile("plugin.jar");

        // when
        PluginArtifact pluginArtifact = new JarPluginArtifact(file);

        // then
        assertEquals("plugin.jar", pluginArtifact.getName());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowAnExceptionIfFileNotExist() throws Exception {
        // given
        File file = new File("xxxx");

        // when
        new JarPluginArtifact(file);
    }

    @Test
    public void shouldHaveFileInputStream() throws Exception {
        // given
        File file = folder.newFile("plugin.jar");
        writeStringToFile(file, "content");

        // when
        PluginArtifact pluginArtifact = new JarPluginArtifact(file);

        // then
        Assert.assertTrue(contentEquals(new FileInputStream(file), pluginArtifact.getInputStream()));
    }

}
