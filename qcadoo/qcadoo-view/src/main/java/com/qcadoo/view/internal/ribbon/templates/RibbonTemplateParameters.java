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
package com.qcadoo.view.internal.ribbon.templates;

import java.util.HashSet;
import java.util.Set;

public final class RibbonTemplateParameters {

    public static final String DEFAULT_TEMPLATE_PLUGIN = "qcadooView";

    private final String templatePlugin;

    private final String templateName;

    private final Set<String> includeGroups;

    private final Set<String> includeItems;

    private final Set<String> excludeGroups;

    private final Set<String> excludeItems;

    private RibbonTemplateParameters(final String templatePlugin, final String templateName, final Set<String> includeGroups,
            final Set<String> includeItems, final Set<String> excludeGroups, final Set<String> excludeItems) {
        if (includeGroups != null && excludeGroups != null) {
            throw new IllegalStateException("template usage error: cannot define both includeGroups and excludeGroups");
        }
        if (includeItems != null && excludeItems != null) {
            throw new IllegalStateException("template usage error: cannot define both includeItems and excludeItems");
        }
        if (templatePlugin == null) {
            this.templatePlugin = DEFAULT_TEMPLATE_PLUGIN;
        } else {
            this.templatePlugin = templatePlugin;
        }
        this.templateName = templateName;
        this.includeGroups = includeGroups;
        this.includeItems = includeItems;
        this.excludeGroups = excludeGroups;
        this.excludeItems = excludeItems;
    }

    public static final class RibbonTemplateParametersBuilder {

        private final String templatePlugin;

        private final String templateName;

        private Set<String> includeGroups;

        private Set<String> includeItems;

        private Set<String> excludeGroups;

        private Set<String> excludeItems;

        private RibbonTemplateParametersBuilder(final String templatePlugin, final String templateName) {
            this.templatePlugin = templatePlugin;
            this.templateName = templateName;
        }

        public RibbonTemplateParametersBuilder usingOnlyGroups(final String groups) {
            includeGroups = parseNames(groups);
            return this;
        }

        public RibbonTemplateParametersBuilder withoutGroups(final String groups) {
            excludeGroups = parseNames(groups);
            return this;
        }

        public RibbonTemplateParametersBuilder usingOnlyItems(final String items) {
            includeItems = parseNames(items);
            return this;
        }

        public RibbonTemplateParametersBuilder withoutItems(final String items) {
            excludeItems = parseNames(items);
            return this;
        }

        public RibbonTemplateParameters build() {
            return new RibbonTemplateParameters(templatePlugin, templateName, includeGroups, includeItems, excludeGroups,
                    excludeItems);
        }

        private Set<String> parseNames(final String names) {
            if (names == null) {
                return null;
            }
            Set<String> parsedNames = new HashSet<String>();
            for (String name : names.split(",")) {
                parsedNames.add(name.trim());
            }
            return parsedNames;
        }

    }

    public static RibbonTemplateParametersBuilder getBuilder(final String templatePlugin, final String templateName) {
        return new RibbonTemplateParametersBuilder(templatePlugin, templateName);
    }

    public static RibbonTemplateParametersBuilder getBuilder(final String templateName) {
        return new RibbonTemplateParametersBuilder(null, templateName);
    }

    public String getTemplatePlugin() {
        return templatePlugin;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Set<String> getIncludeGroups() {
        return includeGroups;
    }

    public Set<String> getIncludeItems() {
        return includeItems;
    }

    public Set<String> getExcludeGroups() {
        return excludeGroups;
    }

    public Set<String> getExcludeItems() {
        return excludeItems;
    }

}
