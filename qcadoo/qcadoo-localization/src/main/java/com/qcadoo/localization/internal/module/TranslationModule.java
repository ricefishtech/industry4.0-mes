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
package com.qcadoo.localization.internal.module;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.qcadoo.localization.api.TranslationPropertiesHolder;
import com.qcadoo.localization.internal.ConfigUtil;
import com.qcadoo.localization.internal.TranslationModuleService;
import com.qcadoo.plugin.api.Module;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TranslationModule extends Module implements TranslationPropertiesHolder {

    private final ApplicationContext applicationContext;

    private final TranslationModuleService translationModuleService;

    private final Set<String> basenames = new LinkedHashSet<>();

    private final String pluginIdentifier;

    private final String basename;

    private final String path;

    private final boolean hotDeploy;

    private final String sourceBasePath;

    public TranslationModule(final ApplicationContext applicationContext,
            final TranslationModuleService translationModuleService, final ConfigUtil configUtil, final String pluginIdentifier, final String basename,
            final String path) {
        super();
        this.applicationContext = applicationContext;
        this.translationModuleService = translationModuleService;
        this.pluginIdentifier = pluginIdentifier;
        this.basename = basename;
        this.path = path;
        this.sourceBasePath = configUtil.getSourceBasePath();
        this.hotDeploy = configUtil.isHotDeploy();
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        translationModuleService.addTranslationModule(parseBasenames());
    }

    @Override
    public void multiTenantEnable() {

    }

    @Override
    public void multiTenantDisable() {

    }

    @Override
    public void disable() {
        translationModuleService.removeTranslationModule(basenames);
    }

    private Set<String> parseBasenames() {
        if (basename == null || "*".equals(basename)) {
            basenames.addAll(getAllFilesFromPath());

        } else if (hotDeploy) {
            basenames.add(findPluginPath(pluginIdentifier) + "/" + path + "/" + basename);

        } else {
            basenames.add("classpath:" + pluginIdentifier + "/" + path + "/" + basename);
        }

        return basenames;
    }

    @Override
    public Set<String> getParsedBasenames() {
        return basenames;
    }

    @Override
    public String getPluginIdentifier() {
        return pluginIdentifier;
    }

    private Collection<? extends String> getAllFilesFromPath() {
        Set<String> basenamesInDirectory = new LinkedHashSet<>();

        try {
            Resource[] resources = applicationContext.getResources("classpath*:" + pluginIdentifier + "/" + path + "/*.properties");
            Pattern pattern = Pattern.compile("([a-z][a-zA-Z0-9]*)\\_\\w+\\.properties");

            for (Resource resource : resources) {
                Matcher matcher = pattern.matcher(resource.getFilename());

                if (matcher.matches()) {
                    if (hotDeploy) {
                        basenamesInDirectory.add(findPluginPath(pluginIdentifier) + "/" + path + "/" + matcher.group(1));

                    } else {
                        basenamesInDirectory.add("classpath:" + pluginIdentifier + "/" + path + "/" + matcher.group(1));
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can not find localization resources", e);
        }

        return basenamesInDirectory;
    }

    private String findPluginPath(String pluginIdentifier) {
        List<String> prefixes = Arrays.asList("/mes/mes-plugins/", "/mes-commercial/", "/qcadoo/");

        for (String prefix : prefixes) {
            String f = sourceBasePath + prefix;
            if (Files.isDirectory(Paths.get(f))) {
                Path dir = FileSystems.getDefault().getPath(f);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path pluginMainDir : stream) {
                        Path file = pluginMainDir.resolve("src/main/resources/").resolve(pluginIdentifier);
                        if (Files.exists(file)) {
                            String x = file.toUri().toURL().toString();
                            return x;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return "";
    }
}
