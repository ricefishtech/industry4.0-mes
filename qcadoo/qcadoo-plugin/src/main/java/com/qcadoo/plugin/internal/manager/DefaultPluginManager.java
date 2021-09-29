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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.model.beans.qcadooPlugin.QcadooPluginPlugin;
import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginDependencyInformation;
import com.qcadoo.plugin.api.PluginDependencyResult;
import com.qcadoo.plugin.api.PluginManager;
import com.qcadoo.plugin.api.PluginOperationResult;
import com.qcadoo.plugin.api.PluginState;
import com.qcadoo.plugin.api.artifact.PluginArtifact;
import com.qcadoo.plugin.internal.PluginException;
import com.qcadoo.plugin.internal.api.InternalPlugin;
import com.qcadoo.plugin.internal.api.InternalPluginAccessor;
import com.qcadoo.plugin.internal.api.PluginDao;
import com.qcadoo.plugin.internal.api.PluginDependencyManager;
import com.qcadoo.plugin.internal.api.PluginDescriptorParser;
import com.qcadoo.plugin.internal.api.PluginFileManager;
import com.qcadoo.plugin.internal.api.PluginOperationResultImpl;
import com.qcadoo.plugin.internal.dependencymanager.PluginStatusResolver;
import com.qcadoo.plugin.internal.dependencymanager.SimplePluginStatusResolver;

@Service
public class DefaultPluginManager implements PluginManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPluginManager.class);

    @Autowired
    private InternalPluginAccessor pluginAccessor;

    @Autowired
    private PluginDao pluginDao;

    @Autowired
    private PluginFileManager pluginFileManager;

    @Autowired
    private PluginDependencyManager pluginDependencyManager;

    @Autowired
    private PluginDescriptorParser pluginDescriptorParser;

    private final PluginStatusResolver pluginStatusResolver = new SimplePluginStatusResolver();

    @Override
    public PluginOperationResult enablePlugin(final String... keys) {
        List<Plugin> plugins = new ArrayList<Plugin>();

        for (String key : keys) {
            Plugin plugin = pluginAccessor.getPlugin(key);

            if (!plugin.hasState(PluginState.ENABLED)) {
                plugins.add(plugin);
            }
        }

        if (plugins.isEmpty()) {
            return PluginOperationResultImpl.success();
        }

        PluginDependencyResult pluginDependencyResult = pluginDependencyManager.getDependenciesToEnable(plugins,
                pluginStatusResolver);

        if (pluginDependencyResult.isCyclic()) {
            return PluginOperationResultImpl.dependenciesCyclesExists();
        }

        if (!pluginDependencyResult.isDependenciesSatisfied()) {
            if (!pluginDependencyResult.getUnsatisfiedDependencies().isEmpty()) {
                return PluginOperationResultImpl.unsatisfiedDependencies(pluginDependencyResult);
            }

            if (!pluginDependencyResult.getDependenciesToEnable().isEmpty()) {
                return PluginOperationResultImpl.dependenciesToEnable(pluginDependencyResult);
            }
        }

        boolean shouldRestart = false;

        List<String> fileNames = new ArrayList<String>();

        for (Plugin plugin : plugins) {
            if (plugin.hasState(PluginState.TEMPORARY)) {
                fileNames.add(plugin.getFilename());
            }
        }
        if (!fileNames.isEmpty()) {
            if (!pluginFileManager.installPlugin(fileNames.toArray(new String[fileNames.size()]))) {
                return PluginOperationResultImpl.cannotInstallPlugin();
            }
            shouldRestart = true;
        }

        plugins = pluginDependencyManager.sortPluginsInDependencyOrder(plugins);

        for (Plugin plugin : plugins) {
            if (plugin.hasState(PluginState.TEMPORARY)) {
                ((InternalPlugin) plugin).changeStateTo(PluginState.ENABLING);
            } else {
                try {
                    ((InternalPlugin) plugin).changeStateTo(PluginState.ENABLED);
                } catch (Exception e) {
                    LOG.error(e.getMessage());

                    ((InternalPlugin) plugin).changeStateTo(PluginState.DISABLED);

                    return PluginOperationResultImpl.pluginEnablingEncounteredErrors();
                }
            }

            pluginDao.save(plugin);
            pluginAccessor.savePlugin(plugin);
        }

        if (shouldRestart) {
            return PluginOperationResultImpl.successWithRestart();
        } else {
            return PluginOperationResultImpl.success();
        }

    }

    @Override
    public PluginOperationResult disablePlugin(final String... keys) {
        List<Plugin> plugins = new ArrayList<>();

        for (String key : keys) {
            Plugin plugin = pluginAccessor.getPlugin(key);

            if (plugin.isSystemPlugin()) {
                return PluginOperationResultImpl.systemPluginDisabling();
            }

            if (plugin.hasState(PluginState.ENABLED)) {
                plugins.add(plugin);
            }
        }

        if (plugins.isEmpty()) {
            return PluginOperationResultImpl.success();
        }

        PluginDependencyResult pluginDependencyResult = pluginDependencyManager.getDependenciesToDisable(plugins,
                pluginStatusResolver);

        if (!pluginDependencyResult.isDependenciesSatisfied() && !pluginDependencyResult.getDependenciesToDisable().isEmpty()) {
            return PluginOperationResultImpl.dependenciesToDisable(pluginDependencyResult);
        }

        plugins = pluginDependencyManager.sortPluginsInDependencyOrder(plugins);

        Collections.reverse(plugins);

        for (Plugin plugin : plugins) {
            ((InternalPlugin) plugin).changeStateTo(PluginState.DISABLED);

            pluginDao.save(plugin);
            pluginAccessor.savePlugin(plugin);
        }

        return PluginOperationResultImpl.success();
    }

    @Override
    public PluginOperationResult uninstallPlugin(final String... keys) {
        List<Plugin> plugins = new ArrayList<Plugin>();

        for (String key : keys) {
            Plugin plugin = pluginAccessor.getPlugin(key);

            if (plugin.isSystemPlugin()) {
                return PluginOperationResultImpl.systemPluginUninstalling();
            }

            plugins.add(plugin);
        }

        PluginDependencyResult pluginDependencyResult = pluginDependencyManager.getDependenciesToUninstall(plugins,
                pluginStatusResolver);

        if (!pluginDependencyResult.isDependenciesSatisfied() && !pluginDependencyResult.getDependenciesToUninstall().isEmpty()) {
            return PluginOperationResultImpl.dependenciesToUninstall(pluginDependencyResult);
        }

        boolean shouldRestart = false;

        List<String> fileNames = new ArrayList<String>();
        for (Plugin plugin : plugins) {
            if (!plugin.hasState(PluginState.TEMPORARY)) {
                shouldRestart = true;
            }
            fileNames.add(plugin.getFilename());
        }

        pluginFileManager.uninstallPlugin(fileNames.toArray(new String[fileNames.size()]));

        plugins = pluginDependencyManager.sortPluginsInDependencyOrder(plugins);
        Collections.reverse(plugins);
        for (Plugin plugin : plugins) {
            if (plugin.hasState(PluginState.ENABLED)) {
                ((InternalPlugin) plugin).changeStateTo(PluginState.DISABLED);
            }
            pluginDao.delete(plugin);
            pluginAccessor.removePlugin(plugin);
        }

        if (shouldRestart) {
            return PluginOperationResultImpl.successWithRestart();
        } else {
            return PluginOperationResultImpl.success();
        }
    }

    @Override
    public PluginOperationResult installPlugin(final PluginArtifact pluginArtifact) {
        File pluginFile = null;
        try {
            pluginFile = pluginFileManager.uploadPlugin(pluginArtifact);
        } catch (PluginException e) {
            return PluginOperationResultImpl.cannotUploadPlugin();
        }
        Plugin plugin = null;
        try {
            plugin = pluginDescriptorParser.parse(pluginFile);
        } catch (PluginException e) {
            LOG.error(e.getMessage());
            pluginFileManager.uninstallPlugin(pluginFile.getName());
            return PluginOperationResultImpl.corruptedPlugin();
        }

        if (plugin.isSystemPlugin()) {
            pluginFileManager.uninstallPlugin(plugin.getFilename());
            return PluginOperationResultImpl.systemPluginUpdating();
        }

        boolean shouldRestart = false;

        PluginDependencyResult pluginDependencyResult = pluginDependencyManager.getDependenciesToEnable(newArrayList(plugin),
                pluginStatusResolver);

        if (pluginDependencyResult.isCyclic()) {
            pluginFileManager.uninstallPlugin(plugin.getFilename());
            return PluginOperationResultImpl.dependenciesCyclesExists();
        }

        Plugin existingPlugin = pluginAccessor.getPlugin(plugin.getIdentifier());
        if (existingPlugin == null) {
            ((InternalPlugin) plugin).changeStateTo(PluginState.TEMPORARY);
            pluginDao.save(plugin);
            pluginAccessor.savePlugin(plugin);

            if (!pluginDependencyResult.isDependenciesSatisfied()
                    && !pluginDependencyResult.getUnsatisfiedDependencies().isEmpty()) {
                return PluginOperationResultImpl.successWithMissingDependencies(pluginDependencyResult);
            }

            return PluginOperationResultImpl.success();
        } else {
            if (existingPlugin.getVersion().compareTo(plugin.getVersion()) >= 0) {
                pluginFileManager.uninstallPlugin(plugin.getFilename());
                return PluginOperationResultImpl.cannotDowngradePlugin();
            }
            if (existingPlugin.hasState(PluginState.TEMPORARY)) {
                if (!pluginDependencyResult.isDependenciesSatisfied()
                        && !pluginDependencyResult.getUnsatisfiedDependencies().isEmpty()) {
                    pluginFileManager.uninstallPlugin(existingPlugin.getFilename());
                    ((InternalPlugin) plugin).changeStateTo(existingPlugin.getState());
                    pluginDao.save(plugin);
                    pluginAccessor.savePlugin(plugin);
                    return PluginOperationResultImpl.successWithMissingDependencies(pluginDependencyResult);
                }
                ((InternalPlugin) plugin).changeStateTo(existingPlugin.getState());
            } else if (existingPlugin.hasState(PluginState.DISABLED)) {
                if (!pluginDependencyResult.isDependenciesSatisfied()
                        && !pluginDependencyResult.getUnsatisfiedDependencies().isEmpty()) {
                    pluginFileManager.uninstallPlugin(plugin.getFilename());
                    return PluginOperationResultImpl.unsatisfiedDependencies(pluginDependencyResult);
                }
                if (!pluginFileManager.installPlugin(plugin.getFilename())) {
                    pluginFileManager.uninstallPlugin(plugin.getFilename());
                    return PluginOperationResultImpl.cannotInstallPlugin();
                }
                shouldRestart = true;
                ((InternalPlugin) plugin).changeStateTo(existingPlugin.getState());
            } else if (existingPlugin.hasState(PluginState.ENABLED)) {
                if (!pluginDependencyResult.isDependenciesSatisfied()) {
                    if (!pluginDependencyResult.getUnsatisfiedDependencies().isEmpty()) {
                        pluginFileManager.uninstallPlugin(plugin.getFilename());
                        return PluginOperationResultImpl.unsatisfiedDependencies(pluginDependencyResult);
                    }

                    if (!pluginDependencyResult.getDependenciesToEnable().isEmpty()) {
                        pluginFileManager.uninstallPlugin(plugin.getFilename());
                        return PluginOperationResultImpl.dependenciesToEnable(pluginDependencyResult);
                    }
                }
                if (!pluginFileManager.installPlugin(plugin.getFilename())) {
                    pluginFileManager.uninstallPlugin(plugin.getFilename());
                    return PluginOperationResultImpl.cannotInstallPlugin();
                }
                shouldRestart = true;
                PluginDependencyResult installPluginDependencyResult = pluginDependencyManager.getDependenciesToUpdate(
                        existingPlugin, plugin, pluginStatusResolver);

                if (!installPluginDependencyResult.getDependenciesToDisableUnsatisfiedAfterUpdate().isEmpty()) {
                    pluginFileManager.uninstallPlugin(plugin.getFilename());
                    return PluginOperationResultImpl.unsatisfiedDependenciesAfterUpdate(installPluginDependencyResult);
                }

                List<Plugin> dependencyPlugins = new ArrayList<Plugin>();
                for (PluginDependencyInformation pluginDependencyInformation : installPluginDependencyResult
                        .getDependenciesToDisable()) {
                    dependencyPlugins.add(pluginAccessor.getPlugin(pluginDependencyInformation.getIdentifier()));
                }
                dependencyPlugins = pluginDependencyManager.sortPluginsInDependencyOrder(dependencyPlugins);
                Collections.reverse(dependencyPlugins);
                for (Plugin dependencyPlugin : dependencyPlugins) {
                    ((InternalPlugin) dependencyPlugin).changeStateTo(PluginState.DISABLED);
                }

                ((InternalPlugin) existingPlugin).changeStateTo(PluginState.DISABLED);
                ((InternalPlugin) plugin).changeStateTo(PluginState.ENABLING);

                Collections.reverse(dependencyPlugins);
                for (Plugin dependencyPlugin : dependencyPlugins) {
                    ((InternalPlugin) dependencyPlugin).changeStateTo(PluginState.ENABLING);
                    pluginDao.save(dependencyPlugin);
                }
            }
            pluginFileManager.uninstallPlugin(existingPlugin.getFilename());
            pluginDao.save(plugin);
            pluginAccessor.savePlugin(plugin);
            if (shouldRestart) {
                return PluginOperationResultImpl.successWithRestart();
            } else {
                return PluginOperationResultImpl.success();
            }
        }
    }

    void setPluginAccessor(final InternalPluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;

    }

    void setPluginDao(final PluginDao pluginDao) {
        this.pluginDao = pluginDao;

    }

    void setPluginFileManager(final PluginFileManager pluginFileManager) {
        this.pluginFileManager = pluginFileManager;
    }

    void setPluginDependencyManager(final PluginDependencyManager pluginDependencyManager) {
        this.pluginDependencyManager = pluginDependencyManager;
    }

    void setPluginDescriptorParser(final PluginDescriptorParser pluginDescriptorParser) {
        this.pluginDescriptorParser = pluginDescriptorParser;
    }

    @Override
    public List<Plugin> getEnabledPluginsList() {
        List<Plugin> pluginIdentifierList = new ArrayList<Plugin>();
        Set<QcadooPluginPlugin> pluginList = pluginDao.list();
        for (QcadooPluginPlugin qcadooPlugin : pluginList) {
            Plugin plugin = pluginAccessor.getEnabledPlugin(qcadooPlugin.getIdentifier());
            if (plugin != null) {
                pluginIdentifierList.add(plugin);
            }
        }
        return pluginIdentifierList;
    }

    @Override
    public boolean isPluginEnabled(final String pluginIdentifier) {
        return pluginAccessor.getEnabledPlugin(pluginIdentifier) != null;
    }

}
