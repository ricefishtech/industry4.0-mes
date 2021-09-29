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
package com.qcadoo.localization.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class TranslationModuleService {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationModuleService.class);

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    private ApplicationContext applicationContext;

    private final Set<String> basenames = new LinkedHashSet<>();

    public void addTranslationModule(final Collection<? extends String> moduleBasenames) {
        basenames.addAll(moduleBasenames);
        messageSource.clearCache();
        String[] basenamesArray = basenames.toArray(new String[basenames.size()]);
        ArrayUtils.reverse(basenamesArray);
        messageSource.setBasenames(basenamesArray);
    }

    public void removeTranslationModule(final Collection<? extends String> moduleBasenames) {
        basenames.removeAll(moduleBasenames);
        messageSource.clearCache();
        messageSource.setBasenames(basenames.toArray(new String[basenames.size()]));
    }

    public List<Resource> getLocalizationResources() {
        List<Resource> resources = new LinkedList<>();
        for (String basename : basenames) {
            String searchName = basename + "*.properties";
            try {
                resources.addAll(Arrays.asList(applicationContext.getResources(searchName)));
            } catch (IOException e) {
                LOG.error("Can not read messages file", e);
            }
        }
        return resources;
    }
}
