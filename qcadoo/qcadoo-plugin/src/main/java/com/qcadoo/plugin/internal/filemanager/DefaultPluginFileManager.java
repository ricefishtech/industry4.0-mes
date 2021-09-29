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

import static java.lang.System.getProperty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qcadoo.plugin.api.artifact.PluginArtifact;
import com.qcadoo.plugin.internal.PluginException;
import com.qcadoo.plugin.internal.api.PluginFileManager;

@Service
public final class DefaultPluginFileManager implements PluginFileManager {

    private static final String L_FILE_SEPARATOR = "file.separator";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPluginFileManager.class);

    @Value("#{plugin.pluginsPath}")
    private String pluginsPath;

    @Value("#{plugin.pluginsTmpPath}")
    private String pluginsTmpPath;

    @Override
    public boolean installPlugin(final String... filenames) {
        if (!checkFileRightsToWrite(pluginsPath)) {
            return false;
        }
        for (String filename : filenames) {
            if (!checkFileExists(filename, pluginsTmpPath)) {
                return false;
            }
        }
        for (String filename : filenames) {
            try {
                FileUtils.moveToDirectory(new File(pluginsTmpPath + getProperty(L_FILE_SEPARATOR) + filename), new File(
                        pluginsPath), false);
            } catch (IOException e) {
                LOG.error("Problem with moving plugin file - " + e.getMessage());
                throw new PluginException(e.getMessage(), e);
            }
        }
        return true;
    }

    @Override
    public File uploadPlugin(final PluginArtifact pluginArtifact) {
        InputStream input = pluginArtifact.getInputStream();
        File pluginFile = new File(pluginsTmpPath + getProperty(L_FILE_SEPARATOR) + pluginArtifact.getName());
        try {
            FileUtils.copyInputStreamToFile(input, pluginFile);
        } catch (IOException e) {
            LOG.error("Problem with upload plugin file - " + e.getMessage());
            throw new PluginException(e.getMessage(), e);
        }
        return pluginFile;
    }

    @Override
    public void uninstallPlugin(final String... filenames) {
        for (String filename : filenames) {
            File file = new File((pluginsTmpPath + getProperty(L_FILE_SEPARATOR) + filename));
            if (!file.exists()) {
                file = new File(pluginsPath + getProperty(L_FILE_SEPARATOR) + filename);
            }
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                LOG.error("Problem with removing plugin file - " + e.getMessage());
                if (file.exists()) {
                    LOG.info("Trying delete file after JVM stop");
                    file.deleteOnExit();
                }
            }
        }
    }

    private boolean checkFileExists(final String key, final String path) {
        File file = new File(path + getProperty(L_FILE_SEPARATOR) + key);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    private boolean checkFileRightsToWrite(final String pluginsPath) {
        File file = new File(pluginsPath);
        if (!file.exists() || !file.canWrite()) {
            return false;
        }
        return true;
    }

    void setPluginsPath(final String pluginsPath) {
        this.pluginsPath = pluginsPath;
    }

    void setPluginsTmpPath(final String pluginsTmpPath) {
        this.pluginsTmpPath = pluginsTmpPath;
    }

}
