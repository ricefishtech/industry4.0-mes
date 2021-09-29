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
package com.qcadoo.customTranslation.internal.aop;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qcadoo.customTranslation.api.CustomTranslationResolver;
import com.qcadoo.customTranslation.constants.CustomTranslationContants;
import com.qcadoo.plugin.api.PluginStateResolver;

@Service
public class TranslationServiceOverrideUtil {

    @Value("${useCustomTranslations}")
    private boolean useCustomTranslations;

    @Autowired
    private PluginStateResolver pluginStateResolver;

    @Autowired
    private CustomTranslationResolver customTranslationResolver;

    public boolean shouldOverrideTranslation(final String key, final Locale locale) {
        return useCustomTranslations && pluginStateResolver.isEnabled(CustomTranslationContants.PLUGIN_IDENTIFIER)
                && customTranslationResolver.isCustomTranslationActive(key, locale);
    }

    public String getCustomTranslation(final String key, final Locale locale, final String[] args) {
        return customTranslationResolver.getCustomTranslation(key, locale, args);
    }

}
