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
package com.qcadoo.plugin.internal.dependencymanager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginAccessor;
import com.qcadoo.plugin.api.PluginDependencyInformation;
import com.qcadoo.plugin.api.PluginDependencyResult;
import com.qcadoo.plugin.internal.api.PluginDependencyManager;
import com.qcadoo.plugin.internal.api.PluginDependencyResultImpl;

@Service
public final class DefaultPluginDependencyManager implements PluginDependencyManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPluginDependencyManager.class);

    @Autowired
    private PluginAccessor pluginAccessor;

    @Override
    public PluginDependencyResult getDependenciesToEnable(final List<Plugin> plugins,
            final PluginStatusResolver pluginStatusResolver) {

        Set<String> marked = new HashSet<String>();
        DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        PluginDependencyResult pluginDependencyResult = getDependenciesToEnable(plugins, marked, graph, pluginStatusResolver);

        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(graph);

        if (cycleDetector.detectCycles()) {
            LOG.warn("Dependency cycle detected: " + cycleDetector.findCycles());
            return PluginDependencyResultImpl.cyclicDependencies();
        } else {
            return pluginDependencyResult;
        }
    }

    private PluginDependencyResult getDependenciesToEnable(final List<Plugin> plugins, final Set<String> marked,
            final DirectedGraph<String, DefaultEdge> graph, final PluginStatusResolver pluginStatusResolver) {

        Set<PluginDependencyInformation> disabledDependencies = new HashSet<PluginDependencyInformation>();
        Set<PluginDependencyInformation> unsatisfiedDependencies = new HashSet<PluginDependencyInformation>();

        Set<String> argumentPluginIdentifiersSet = getArgumentIdentifiersSet(plugins);

        for (Plugin plugin : plugins) {

            if (marked.contains(plugin.getIdentifier())) {
                continue;
            }

            marked.add(plugin.getIdentifier());
            graph.addVertex(plugin.getIdentifier());

            for (PluginDependencyInformation dependencyInfo : plugin.getRequiredPlugins()) {
                Plugin requiredPlugin = pluginAccessor.getPlugin(dependencyInfo.getIdentifier());
                if (requiredPlugin == null) {
                    if (!argumentPluginIdentifiersSet.contains(dependencyInfo.getIdentifier())) {
                        unsatisfiedDependencies.add(dependencyInfo);
                    }
                    continue;
                }

                graph.addVertex(dependencyInfo.getIdentifier());
                graph.addEdge(plugin.getIdentifier(), dependencyInfo.getIdentifier());

                if (!isPluginDisabled(requiredPlugin, pluginStatusResolver)) {
                    continue;
                }

                if (dependencyInfo.contains(requiredPlugin.getVersion())) {
                    disabledDependencies.add(dependencyInfo);

                    PluginDependencyResult nextLevelDependencioesResult = getDependenciesToEnable(
                            Collections.singletonList(requiredPlugin), marked, graph, pluginStatusResolver);

                    if (!nextLevelDependencioesResult.getUnsatisfiedDependencies().isEmpty()
                            || nextLevelDependencioesResult.isCyclic()) {
                        return nextLevelDependencioesResult;
                    }

                    disabledDependencies.addAll(nextLevelDependencioesResult.getDependenciesToEnable());
                } else {
                    unsatisfiedDependencies.add(dependencyInfo);
                }

            }
        }

        if (!unsatisfiedDependencies.isEmpty()) {
            return PluginDependencyResultImpl.unsatisfiedDependencies(unsatisfiedDependencies);
        }

        if (disabledDependencies.isEmpty()) {
            return PluginDependencyResultImpl.satisfiedDependencies();
        }

        Iterator<PluginDependencyInformation> dependencyInfoIterator = disabledDependencies.iterator();
        while (dependencyInfoIterator.hasNext()) {
            if (argumentPluginIdentifiersSet.contains(dependencyInfoIterator.next().getIdentifier())) {
                dependencyInfoIterator.remove();
            }
        }

        return PluginDependencyResultImpl.dependenciesToEnable(disabledDependencies);
    }

    @Override
    public PluginDependencyResult getDependenciesToDisable(final List<Plugin> plugins,
            final PluginStatusResolver pluginStatusResolver) {
        return PluginDependencyResultImpl.dependenciesToDisable(getDependentPlugins(plugins, false, pluginStatusResolver));
    }

    @Override
    public PluginDependencyResult getDependenciesToUninstall(final List<Plugin> plugins,
            final PluginStatusResolver pluginStatusResolver) {
        return PluginDependencyResultImpl.dependenciesToUninstall(getDependentPlugins(plugins, true, pluginStatusResolver));
    }

    @Override
    public PluginDependencyResult getDependenciesToUpdate(final Plugin existingPlugin, final Plugin newPlugin,
            final PluginStatusResolver pluginStatusResolver) {
        Set<PluginDependencyInformation> dependentPlugins = getDependentPlugins(Collections.singletonList(existingPlugin), false,
                pluginStatusResolver);

        Set<PluginDependencyInformation> dependenciesToDisableUnsatisfiedAfterUpdate = new HashSet<PluginDependencyInformation>();
        for (Plugin plugin : pluginAccessor.getPlugins()) {
            if (pluginStatusResolver.isPluginNotInstalled(plugin)) {
                continue;
            }
            for (PluginDependencyInformation dependencyInfo : plugin.getRequiredPlugins()) {
                if (dependencyInfo.getIdentifier().equals(existingPlugin.getIdentifier())) {
                    if (!dependencyInfo.contains(newPlugin.getVersion())) {
                        dependenciesToDisableUnsatisfiedAfterUpdate.add(new PluginDependencyInformation(plugin.getIdentifier()));
                    }
                    break;
                }
            }
        }

        return PluginDependencyResultImpl.dependenciesToUpdate(dependentPlugins, dependenciesToDisableUnsatisfiedAfterUpdate);
    }

    @Override
    public List<Plugin> sortPluginsInDependencyOrder(final Collection<Plugin> plugins, final Map<String, Plugin> allPlugins) {
        // Map: not initialized pugins -> its dependencies
        Map<String, Set<String>> notInitializedPlugins = createPluginsMapWithDependencies(plugins);

        List<String> initializedPlugins = new LinkedList<String>();

        // While not all plugins are initialized...
        while (!notInitializedPlugins.isEmpty()) {

            Iterator<Map.Entry<String, Set<String>>> pluginsToInitializedIt = notInitializedPlugins.entrySet().iterator();

            // ..for all not initialized plugins...
            while (pluginsToInitializedIt.hasNext()) {

                Map.Entry<String, Set<String>> pluginToInitializeEntry = pluginsToInitializedIt.next();

                // ...if plugin has no dependencies...
                if (pluginToInitializeEntry.getValue().isEmpty()) {

                    // ...set this plugin as initialized...
                    initializedPlugins.add(pluginToInitializeEntry.getKey());
                    pluginsToInitializedIt.remove();

                    // ...and remove it from all dependencies
                    for (Set<String> dependencies : notInitializedPlugins.values()) {
                        dependencies.remove(pluginToInitializeEntry.getKey());
                    }
                }
            }
        }

        List<Plugin> sortedPlugins = convertIdentifiersToPlugins(initializedPlugins, allPlugins);

        Collections.sort(sortedPlugins, new DependencyComparator());

        return sortedPlugins;
    }

    @Override
    public List<Plugin> sortPluginsInDependencyOrder(final Collection<Plugin> plugins) {
        return sortPluginsInDependencyOrder(plugins, null);
    }

    private Set<PluginDependencyInformation> getDependentPlugins(final List<Plugin> plugins, final boolean includeDisabled,
            final PluginStatusResolver pluginStatusResolver) {

        List<Plugin> enabledDependencyPlugins = new LinkedList<Plugin>();

        Set<String> argumentPluginIdentifiersSet = getArgumentIdentifiersSet(plugins);

        for (Plugin plugin : pluginAccessor.getPlugins()) {
            if (includeDisabled) {
                if (pluginStatusResolver.isPluginNotInstalled(plugin)) {
                    continue;
                }
            } else {
                if (!pluginStatusResolver.isPluginEnabled(plugin)) {
                    continue;
                }
            }
            for (Plugin pluginToDisable : plugins) {
                for (PluginDependencyInformation dependencyInfo : plugin.getRequiredPlugins()) {
                    if (dependencyInfo.getIdentifier().equals(pluginToDisable.getIdentifier())) {
                        enabledDependencyPlugins.add(plugin);
                        break;
                    }
                }
            }
        }

        Set<PluginDependencyInformation> enabledDependencies = new HashSet<PluginDependencyInformation>();
        for (Plugin plugin : enabledDependencyPlugins) {
            enabledDependencies.add(new PluginDependencyInformation(plugin.getIdentifier()));
        }

        if (!enabledDependencyPlugins.isEmpty()) {
            PluginDependencyResult nextLevelDependencioesResult = getDependenciesToDisable(enabledDependencyPlugins,
                    pluginStatusResolver);
            enabledDependencies.addAll(nextLevelDependencioesResult.getDependenciesToDisable());
        }

        Iterator<PluginDependencyInformation> dependencyInfoIterator = enabledDependencies.iterator();
        while (dependencyInfoIterator.hasNext()) {
            if (argumentPluginIdentifiersSet.contains(dependencyInfoIterator.next().getIdentifier())) {
                dependencyInfoIterator.remove();
            }
        }

        return enabledDependencies;
    }

    private Map<String, Set<String>> createPluginsMapWithDependencies(final Collection<Plugin> plugins) {
        Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
        for (Plugin plugin : plugins) {
            resultMap.put(plugin.getIdentifier(), null);
        }
        for (Plugin plugin : plugins) {
            Set<String> dependencyIdentifiers = new HashSet<String>();
            for (PluginDependencyInformation dependency : plugin.getRequiredPlugins()) {
                if (resultMap.containsKey(dependency.getIdentifier())) {
                    dependencyIdentifiers.add(dependency.getIdentifier());
                }
            }
            resultMap.put(plugin.getIdentifier(), dependencyIdentifiers);
        }
        return resultMap;
    }

    private List<Plugin> convertIdentifiersToPlugins(final Collection<String> pluginIdetifiers,
            final Map<String, Plugin> allPlugins) {
        List<Plugin> resultList = new LinkedList<Plugin>();
        for (String pluginIdentifier : pluginIdetifiers) {
            if (allPlugins == null) {
                resultList.add(pluginAccessor.getPlugin(pluginIdentifier));
            } else {
                resultList.add(allPlugins.get(pluginIdentifier));
            }
        }
        return resultList;
    }

    private Set<String> getArgumentIdentifiersSet(final List<Plugin> plugins) {
        Set<String> argumentPluginInformationsSet = new HashSet<String>();
        for (Plugin plugin : plugins) {
            argumentPluginInformationsSet.add(plugin.getIdentifier());
        }
        return argumentPluginInformationsSet;
    }

    private boolean isPluginDisabled(final Plugin plugin, final PluginStatusResolver pluginStatusResolver) {
        return pluginStatusResolver.isPluginDisabled(plugin) || pluginStatusResolver.isPluginNotInstalled(plugin);
    }

    void setPluginAccessor(final PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

}
