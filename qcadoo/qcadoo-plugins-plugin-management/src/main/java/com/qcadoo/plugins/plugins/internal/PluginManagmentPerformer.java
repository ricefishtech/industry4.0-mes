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
package com.qcadoo.plugins.plugins.internal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.plugin.api.PluginDependencyInformation;
import com.qcadoo.plugin.api.PluginOperationResult;
import com.qcadoo.plugin.api.artifact.PluginArtifact;

@Service
public class PluginManagmentPerformer {

    private static final String L_WRONG_STATUS = "Wrong status";
    @Autowired
    private PluginManagmentConnector pluginManagmentConnector;

    public String performInstall(final PluginArtifact artifact) {

        PluginOperationResult result = pluginManagmentConnector.performInstall(artifact);

        String url = null;

        switch (result.getStatus()) {
            case SUCCESS:
                url = createSuccessPageUrl("install.success");
                break;
            case SUCCESS_WITH_RESTART:
                url = createRestartPageUrl(createSuccessPageUrl("install.success"));
                break;
            case SUCCESS_WITH_MISSING_DEPENDENCIES:
                url = createSuccessPageUrl("install.successWithMissingDependencies", result.getPluginDependencyResult()
                        .getUnsatisfiedDependencies());
                break;
            case DEPENDENCIES_CYCLES_EXISTS:
                url = createErrorPageUrl("install.cyclingDependency");
                break;
            case CANNOT_UPLOAD_PLUGIN:
                url = createErrorPageUrl("install.cannotUploadPlugin");
                break;
            case CORRUPTED_PLUGIN:
                url = createErrorPageUrl("install.corruptedPlugin");
                break;
            case SYSTEM_PLUGIN_UPDATING:
                url = createErrorPageUrl("install.systemPlugin");
                break;
            case CANNOT_DOWNGRADE_PLUGIN:
                url = createErrorPageUrl("install.incorrectVersion");
                break;
            case CANNOT_INSTALL_PLUGIN_FILE:
                url = createErrorPageUrl("install.cannotInstallPlugin");
                break;
            case DEPENDENCIES_TO_ENABLE:
                url = createErrorPageUrl("install.dependenciesToEnable", result.getPluginDependencyResult()
                        .getDependenciesToEnable());
                break;
            case UNSATISFIED_DEPENDENCIES:
                url = createErrorPageUrl("install.unsatisfiedDependencies", result.getPluginDependencyResult()
                        .getUnsatisfiedDependencies());
                break;
            case UNSATISFIED_DEPENDENCIES_AFTER_UPDATE:
                url = createErrorPageUrl("install.unsatisfiedDependenciesAfterUpdate", result.getPluginDependencyResult()
                        .getDependenciesToDisableUnsatisfiedAfterUpdate());
                break;
            default:
                throw new IllegalStateException(L_WRONG_STATUS);
        }

        return url;
    }

    public String performEnable(final List<String> pluginIdentifiers) {

        PluginOperationResult result = pluginManagmentConnector.performEnable(pluginIdentifiers);

        String url = null;

        switch (result.getStatus()) {
            case SUCCESS:
                url = createSuccessPageUrl("enable.success");
                break;
            case SUCCESS_WITH_RESTART:
                url = createRestartPageUrl(createSuccessPageUrl("enable.success"));
                break;
            case UNSATISFIED_DEPENDENCIES:
                url = createErrorPageUrl("enable.unsatisfiedDependencies", result.getPluginDependencyResult()
                        .getUnsatisfiedDependencies());
                break;
            case DEPENDENCIES_TO_ENABLE:
                url = createConfirmPageUrl("enable.dependenciesToEnable", "enable.dependenciesToEnableCancelLabel",
                        "enable.dependenciesToEnableAcceptLabel", "performEnablingMultiplePlugins", result
                                .getPluginDependencyResult().getDependenciesToEnable(), pluginIdentifiers);
                break;
            case CANNOT_INSTALL_PLUGIN_FILE:
                url = createErrorPageUrl("enable.cannotInstall");
                break;
            case PLUGIN_ENABLING_ENCOUNTERED_ERRORS:
                url = createErrorPageUrl("enable.encounteredErrors");
                break;

            default:
                throw new IllegalStateException(L_WRONG_STATUS);
        }

        return url;
    }

    public String performDisable(final List<String> pluginIdentifiers) {

        PluginOperationResult result = pluginManagmentConnector.performDisable(pluginIdentifiers);

        String url = null;

        switch (result.getStatus()) {
            case SUCCESS:
                url = createSuccessPageUrl("disable.success");
                break;
            case SYSTEM_PLUGIN_DISABLING:
                url = createErrorPageUrl("disable.systemPlugin");
                break;
            case DEPENDENCIES_TO_DISABLE:
                url = createConfirmPageUrl("disable.dependenciesToDisable", "disable.dependenciesToDisableCancelLabel",
                        "disable.dependenciesToDisableAcceptLabel", "performDisablingMultiplePlugins", result
                                .getPluginDependencyResult().getDependenciesToDisable(), pluginIdentifiers);
                break;
            default:
                throw new IllegalStateException(L_WRONG_STATUS);
        }

        return url;
    }

    public String performRemove(final List<String> pluginIdentifiers) {

        PluginOperationResult result = pluginManagmentConnector.performRemove(pluginIdentifiers);

        String url = null;

        switch (result.getStatus()) {
            case SUCCESS:
                url = createSuccessPageUrl("uninstall.success");
                break;
            case SUCCESS_WITH_RESTART:
                url = createRestartPageUrl(createSuccessPageUrl("uninstall.success"));
                break;
            case SYSTEM_PLUGIN_UNINSTALLING:
                url = createErrorPageUrl("uninstall.systemPlugin");
                break;
            case DEPENDENCIES_TO_UNINSTALL:
                url = createConfirmPageUrl("uninstall.dependenciesToUninstall", "uninstall.dependenciesToUninstallCancelLabel",
                        "uninstall.dependenciesToUninstallAcceptLabel", "performUninstallingMultiplePlugins", result
                                .getPluginDependencyResult().getDependenciesToUninstall(), pluginIdentifiers);
                break;
            default:
                throw new IllegalStateException(L_WRONG_STATUS);
        }

        return url;
    }

    public void performRestart() {
        pluginManagmentConnector.performRestart();
    }

    private String createSuccessPageUrl(final String statusKey) {
        return createSuccessPageUrl(statusKey, null);
    }

    private String createSuccessPageUrl(final String statusKey, final Set<PluginDependencyInformation> dependencies) {
        StringBuilder url = new StringBuilder("../pluginPages/infoPage.html?type=success&status=");
        url.append(statusKey);
        addDependenciesToUrl(url, dependencies);
        return url.toString();
    }

    private String createErrorPageUrl(final String statusKey) {
        return createErrorPageUrl(statusKey, null);
    }

    private String createErrorPageUrl(final String statusKey, final Set<PluginDependencyInformation> dependencies) {
        StringBuilder url = new StringBuilder("../pluginPages/infoPage.html?type=error&status=");
        url.append(statusKey);
        addDependenciesToUrl(url, dependencies);
        return url.toString();
    }

    private String createConfirmPageUrl(final String statusKey, final String cancelLabel, final String acceptLabel,
            final String acceptRedirect, final Set<PluginDependencyInformation> dependencies, final List<String> pluginIdentifiers) {

        StringBuilder redirectUrl = new StringBuilder(acceptRedirect);
        redirectUrl.append(".html?");
        for (String pluginIdentifier : pluginIdentifiers) {
            if (redirectUrl.charAt(redirectUrl.length() - 1) != '?') {
                redirectUrl.append("&");
            }
            redirectUrl.append("plugin=");
            redirectUrl.append(pluginIdentifier);
        }
        for (PluginDependencyInformation dependency : dependencies) {
            redirectUrl.append("&plugin=");
            redirectUrl.append(dependency.getIdentifier());
        }

        StringBuilder url = new StringBuilder("../pluginPages/infoPage.html?type=confirm&status=");
        url.append(statusKey);
        url.append("&cancelLabel=");
        url.append(cancelLabel);
        url.append("&acceptLabel=");
        url.append(acceptLabel);
        url.append("&acceptRedirect=");
        try {
            url.append(URLEncoder.encode(redirectUrl.toString(), "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Error while generating redirect url", e);
        }

        addDependenciesToUrl(url, dependencies);
        return url.toString();
    }

    private String createRestartPageUrl(final String redirectAfterSuccessPage) {
        StringBuilder url = new StringBuilder("../pluginPages/restartPage.html?redirect=");
        try {
            url.append(URLEncoder.encode(redirectAfterSuccessPage, "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Error while generating redirect url", e);
        }
        return url.toString();
    }

    private void addDependenciesToUrl(final StringBuilder url, final Set<PluginDependencyInformation> dependencies) {
        if (dependencies != null) {
            for (PluginDependencyInformation dependencyInfo : dependencies) {
                url.append("&dep_");
                url.append(dependencyInfo.getIdentifier());
                url.append("=");
                if (dependencyInfo.getVersionOfDependency() == null
                        || "0.0.0".equals(dependencyInfo.getVersionOfDependency().toString())) {
                    url.append("none");
                } else {
                    url.append(dependencyInfo.getVersionOfDependency().toString());
                }
            }
        }
    }
}
