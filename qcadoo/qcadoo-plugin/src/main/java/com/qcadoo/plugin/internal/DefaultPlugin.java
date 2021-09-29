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
package com.qcadoo.plugin.internal;

import com.qcadoo.plugin.api.*;
import com.qcadoo.plugin.internal.api.InternalPlugin;
import com.qcadoo.tenant.api.MultiTenantUtil;

import java.util.*;

import static com.qcadoo.plugin.api.PluginState.*;
import static java.util.Collections.*;

public final class DefaultPlugin implements InternalPlugin {

    private final Map<ModuleFactory<?>, List<Module>> modulesByFactories;

    private final List<ModuleFactory<?>> factories;

    private final List<ModuleFactory<?>> reversedFactories;

    private final PluginInformation information;

    private final Set<PluginDependencyInformation> dependencies;

    private final boolean system;

    private final String identifier;

    private final Version version;

    private final String fileName;

    private PluginState state;

    private DefaultPlugin(final String identifier, final String fileName, final boolean system, final Version version,
            final List<ModuleFactory<?>> factories, final Map<ModuleFactory<?>, List<Module>> modulesByFactories,
            final PluginInformation information, final Set<PluginDependencyInformation> dependencies) {
        this.state = UNKNOWN;
        this.identifier = identifier;
        this.fileName = fileName;
        this.version = version;
        this.modulesByFactories = modulesByFactories;
        this.information = information;
        this.dependencies = dependencies;
        this.system = system;
        this.factories = factories;
        this.reversedFactories = new ArrayList<>(factories);
        Collections.reverse(reversedFactories);
    }

    @Override
    public PluginInformation getPluginInformation() {
        return information;
    }

    @Override
    public Set<PluginDependencyInformation> getRequiredPlugins() {
        return dependencies;
    }

    @Override
    public boolean isSystemPlugin() {
        return system;
    }

    @Override
    public void changeStateTo(final PluginState targetState) {
        if (!isTransitionPossible(getState(), targetState)) {
            throw new IllegalStateException("Cannot change state of plugin " + this + " from " + getState() + " to "
                    + targetState);
        }

        if (hasState(UNKNOWN)) {
            state = targetState;

            return;
        }

        Plugin thisPlugin = this;

        if (ENABLED.equals(targetState)) {
            state = targetState;

            for (final ModuleFactory<?> factory : factories) {
                List<Module> modules = modulesByFactories.get(factory);

                for (final Module module : modules) {
                    module.enable();

                    MultiTenantUtil.doInMultiTenantContext(() -> {
                        if (PluginUtils.isEnabled(thisPlugin)) {
                            module.multiTenantEnable();
                        }
                    });
                }
            }
        } else if (DISABLED.equals(targetState)) {
            for (final ModuleFactory<?> factory : reversedFactories) {
                List<Module> modules = modulesByFactories.get(factory);
                Collections.reverse(modules);

                for (final Module module : modules) {
                    module.disable();

                    MultiTenantUtil.doInMultiTenantContext(() -> {
                        if (PluginUtils.isEnabled(thisPlugin)) {
                            module.multiTenantDisable();
                        }
                    });
                }
            }

            state = targetState;
        } else {
            state = targetState;
        }

    }

    private boolean isTransitionPossible(final PluginState from, final PluginState to) {
        if (from == null || to == null || to.equals(UNKNOWN) || to.equals(from)) {
            return false;
        }

        if (from.equals(UNKNOWN)) {
            return true;
        }

        if (to.equals(TEMPORARY)) {
            return false;
        }

        if (from.equals(ENABLING) && to.equals(DISABLED)) {
            return false;
        }

        if (from.equals(ENABLED) && to.equals(ENABLING)) {
            return false;
        }

        if (from.equals(TEMPORARY) && !to.equals(ENABLING)) {
            return false;
        }

        return true;
    }

    @Override
    public String getFilename() {
        return fileName;
    }

    @Override
    public int compareVersion(final Version version) {
        return this.version.compareTo(version);
    }

    @Override
    public List<Module> getModules(final ModuleFactory<?> moduleFactory) {
        if (modulesByFactories.containsKey(moduleFactory)) {
            return modulesByFactories.get(moduleFactory);
        } else {
            return Collections.emptyList();
        }
    }

    public static class Builder {

        private final String identifier;

        private String fileName;

        private Version version;

        private String description;

        private String vendor;

        private String vendorUrl;

        private String name;

        private String license;

        private boolean system;

        private String group;

        private final Map<ModuleFactory<?>, List<Module>> modulesByFactories = new LinkedHashMap<>();

        private final Set<PluginDependencyInformation> dependencyInformations = new HashSet<>();

        private final List<ModuleFactory<?>> factories;

        public Builder(final String identifier, final List<ModuleFactory<?>> factories) {
            this.identifier = identifier;
            this.factories = factories;
            for (ModuleFactory<?> factory : factories) {
                modulesByFactories.put(factory, new ArrayList<>());
            }
        }

        public static Builder identifier(final String identifier, final List<ModuleFactory<?>> factories) {
            return new Builder(identifier, factories);
        }

        public Builder withFileName(final String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withModule(final ModuleFactory<?> moduleFactory, final Module module) {
            modulesByFactories.get(moduleFactory).add(module);
            return this;
        }

        public Builder withVersion(final String version) {
            this.version = new Version(version);
            return this;
        }

        public Builder withDependency(final String identifier, final String version) {
            dependencyInformations.add(new PluginDependencyInformation(identifier, new VersionOfDependency(version)));
            return this;
        }

        public Builder withFeature(final String featureName, final String systemName) {
            // TODO KRNA add features
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder withVendor(final String vendor) {
            this.vendor = vendor;
            return this;
        }

        public Builder withVendorUrl(final String vendorUrl) {
            this.vendorUrl = vendorUrl;
            return this;
        }

        public Builder withLicense(final String license) {
            this.license = license;
            return this;
        }

        public Builder asSystem() {
            this.system = true;
            return this;
        }

        public Builder withGroup(final String group){
            this.group = group;
            return this;
        }

        public InternalPlugin build() {
            PluginInformation pluginInformation = new PluginInformation(name, description, vendor, vendorUrl, license, group);
            return new DefaultPlugin(identifier, fileName, system, version, unmodifiableList(factories),
                    unmodifiableMap(modulesByFactories), pluginInformation, unmodifiableSet(dependencyInformations));
        }

    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public PluginState getState() {
        return state;
    }

    @Override
    public boolean hasState(final PluginState expectedState) {
        return state.equals(expectedState);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultPlugin)) {
            return false;
        }
        DefaultPlugin other = (DefaultPlugin) obj;
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (fileName == null) {
            if (other.fileName != null) {
                return false;
            }
        } else if (!fileName.equals(other.fileName)) {
            return false;
        }
        if (state != other.state) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultPlugin [identifier=" + identifier + ", version=" + version + ", state=" + state + ", system=" + system
                + "]";
    }

}
