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
package com.qcadoo.plugin.internal.api;

import java.util.Set;

import com.google.common.collect.Sets;
import com.qcadoo.plugin.api.PluginDependencyInformation;
import com.qcadoo.plugin.api.PluginDependencyResult;
import com.qcadoo.plugin.api.PluginManager;
import com.qcadoo.plugin.api.PluginOperationResult;

/**
 * Holder for plugin's dependencies information.
 * 
 * @see PluginManager
 * @see PluginOperationResult
 * @since 0.4.0
 */
public final class PluginDependencyResultImpl implements PluginDependencyResult {

    private Set<PluginDependencyInformation> dependenciesToEnable = Sets.newHashSet();

    private Set<PluginDependencyInformation> unsatisfiedDependencies = Sets.newHashSet();

    private Set<PluginDependencyInformation> dependenciesToDisable = Sets.newHashSet();

    private Set<PluginDependencyInformation> dependenciesToDisableUnsatisfiedAfterUpdate = Sets.newHashSet();

    private Set<PluginDependencyInformation> dependenciesToUninstall = Sets.newHashSet();

    private boolean cycleExists;

    private PluginDependencyResultImpl() {
    }

    @Override
    public Set<PluginDependencyInformation> getDependenciesToEnable() {
        return dependenciesToEnable;
    }

    @Override
    public Set<PluginDependencyInformation> getUnsatisfiedDependencies() {
        return unsatisfiedDependencies;
    }

    @Override
    public Set<PluginDependencyInformation> getDependenciesToDisable() {
        return dependenciesToDisable;
    }

    @Override
    public Set<PluginDependencyInformation> getDependenciesToUninstall() {
        return dependenciesToUninstall;
    }

    @Override
    public Set<PluginDependencyInformation> getDependenciesToDisableUnsatisfiedAfterUpdate() {
        return dependenciesToDisableUnsatisfiedAfterUpdate;
    }

    @Override
    public boolean isDependenciesSatisfied() {
        return dependenciesToEnable.isEmpty() && unsatisfiedDependencies.isEmpty() && dependenciesToDisable.isEmpty()
                && dependenciesToUninstall.isEmpty() && dependenciesToDisableUnsatisfiedAfterUpdate.isEmpty() && !isCyclic();
    }

    @Override
    public boolean isCyclic() {
        return cycleExists;
    }

    /**
     * Creates holder with plugins to enable.
     * 
     * @param dependenciesToEnable
     *            plugins to enable
     * @return holder
     */
    public static PluginDependencyResult dependenciesToEnable(final Set<PluginDependencyInformation> dependenciesToEnable) {
        PluginDependencyResultImpl result = new PluginDependencyResultImpl();
        result.setDependenciesToEnable(dependenciesToEnable);
        return result;
    }

    /**
     * Creates holder with all plugins satisfied.
     * 
     * @return holder
     */
    public static PluginDependencyResult satisfiedDependencies() {
        return new PluginDependencyResultImpl();
    }

    /**
     * Creates holder with missing plugins.
     * 
     * @param unsatisfiedDependencies
     *            missing plugins
     * @return holder
     */
    public static PluginDependencyResult unsatisfiedDependencies(final Set<PluginDependencyInformation> unsatisfiedDependencies) {
        PluginDependencyResultImpl result = new PluginDependencyResultImpl();
        result.setUnsatisfiedDependencies(unsatisfiedDependencies);
        return result;
    }

    /**
     * Creates holder with plugins to disable.
     * 
     * @param dependenciesToDisable
     *            plugins to disable
     * @return holder
     */
    public static PluginDependencyResult dependenciesToDisable(final Set<PluginDependencyInformation> dependenciesToDisable) {
        PluginDependencyResultImpl result = new PluginDependencyResultImpl();
        result.setDependenciesToDisable(dependenciesToDisable);
        return result;
    }

    /**
     * Creates holder with plugins to uninstall.
     * 
     * @param dependenciesToUninstall
     *            plugins to uninstall
     * @return holder
     */
    public static PluginDependencyResult dependenciesToUninstall(final Set<PluginDependencyInformation> dependenciesToUninstall) {
        PluginDependencyResultImpl result = new PluginDependencyResultImpl();
        result.setDependenciesToUninstall(dependenciesToUninstall);
        return result;
    }

    /**
     * Creates holder with plugins to disable, after update.
     * 
     * @param dependenciesToDisable
     *            plugins to disable
     * @param dependenciesToDisableUnsatisfiedAfterUpdate
     *            plugins which will not be able to enable after updating
     * @return holder
     */
    public static PluginDependencyResult dependenciesToUpdate(final Set<PluginDependencyInformation> dependenciesToDisable,
            final Set<PluginDependencyInformation> dependenciesToDisableUnsatisfiedAfterUpdate) {
        PluginDependencyResultImpl result = new PluginDependencyResultImpl();
        result.setDependenciesToDisable(dependenciesToDisable);
        result.setDependenciesToDisableUnsatisfiedAfterUpdate(dependenciesToDisableUnsatisfiedAfterUpdate);
        return result;
    }

    /**
     * Creates holder with cyclic dependencies.
     * 
     * @return holder
     */
    public static PluginDependencyResult cyclicDependencies() {
        PluginDependencyResultImpl result = new PluginDependencyResultImpl();
        result.setCycleExists(true);
        return result;
    }

    private void setDependenciesToUninstall(final Set<PluginDependencyInformation> dependenciesToUninstall) {
        this.dependenciesToUninstall = dependenciesToUninstall;
    }

    private void setCycleExists(final boolean cycleExists) {
        this.cycleExists = cycleExists;
    }

    private void setDependenciesToEnable(final Set<PluginDependencyInformation> dependenciesToEnable) {
        this.dependenciesToEnable = dependenciesToEnable;
    }

    private void setUnsatisfiedDependencies(final Set<PluginDependencyInformation> unsatisfiedDependencies) {
        this.unsatisfiedDependencies = unsatisfiedDependencies;
    }

    private void setDependenciesToDisable(final Set<PluginDependencyInformation> dependenciesToDisable) {
        this.dependenciesToDisable = dependenciesToDisable;
    }

    private void setDependenciesToDisableUnsatisfiedAfterUpdate(
            final Set<PluginDependencyInformation> dependenciesToDisableUnsatisfiedAfterUpdate) {
        this.dependenciesToDisableUnsatisfiedAfterUpdate = dependenciesToDisableUnsatisfiedAfterUpdate;
    }
}
