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
package com.qcadoo.customTranslation.internal;

import static com.qcadoo.customTranslation.constants.CustomTranslationFields.ACTIVE;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.KEY;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.LOCALE;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.qcadoo.customTranslation.api.CustomTranslationCacheService;
import com.qcadoo.customTranslation.constants.CustomTranslationFields;
import com.qcadoo.model.api.Entity;
import com.qcadoo.tenant.api.MultiTenantService;

@Service
public class CustomTranslationCacheServiceImpl implements CustomTranslationCacheService {

    @Autowired
    private MultiTenantService multiTenantService;

    private final Map<Integer, Map<String, Map<String, String>>> customTranslationsCache;

    public CustomTranslationCacheServiceImpl() {
        this.customTranslationsCache = Maps.newHashMap();
    }

    @Override
    public void addCustomTranslation(final String key, final String locale, final String customTranslation) {
        Map<String, String> localeAndCustomTranslation = Maps.newHashMap();
        localeAndCustomTranslation.put(locale, customTranslation);

        getTenantCustomTranslationsCache().put(key, localeAndCustomTranslation);
    }

    @Override
    public void updateCustomTranslation(final String key, final String locale, final String customTranslation) {
        if (isCustomTranslationAdded(key)) {
            getTenantCustomTranslationsCache().get(key).put(locale, customTranslation);
        }
    }

    @Override
    public void manageCustomTranslation(final String key, final String locale, final String customTranslation) {
        if (isCustomTranslationAdded(key)) {
            updateCustomTranslation(key, locale, customTranslation);
        } else {
            addCustomTranslation(key, locale, customTranslation);
        }
    }

    @Override
    public void removeCustomTranslations(final List<String> keys) {
        if (keys != null) {
            for (String key : keys) {
                if (isCustomTranslationAdded(key)) {
                    for (String locale : getTenantCustomTranslationsCache().get(key).keySet()) {
                        updateCustomTranslation(key, locale, null);
                    }
                }
            }
        }
    }

    @Override
    public String getCustomTranslation(final String key, final String locale) {
        if (getTenantCustomTranslationsCache().containsKey(key)
                && getTenantCustomTranslationsCache().get(key).containsKey(locale)) {
            return getTenantCustomTranslationsCache().get(key).get(locale);
        }
        return null;
    }

    @Override
    public Map<String, Map<String, String>> getCustomTranslations() {
        return Collections.unmodifiableMap(getTenantCustomTranslationsCache());
    }

    @Override
    public boolean isCustomTranslationAdded(final String key) {
        return getTenantCustomTranslationsCache().containsKey(key);
    }

    @Override
    public boolean isCustomTranslationActive(final String key, final String locale) {
        if (getTenantCustomTranslationsCache().containsKey(key)
                && getTenantCustomTranslationsCache().get(key).containsKey(locale)) {
            return getTenantCustomTranslationsCache().get(key).get(locale) != null;
        }

        return false;
    }

    @Override
    public void loadCustomTranslations(final List<Entity> customTranslations) {
        for (Entity customTranslation : customTranslations) {
            boolean active = customTranslation.getBooleanField(ACTIVE);

            String key = customTranslation.getStringField(KEY);
            String translation = customTranslation.getStringField(CustomTranslationFields.CUSTOM_TRANSLATION);
            String locale = (active) ? customTranslation.getStringField(LOCALE) : null;

            manageCustomTranslation(key, locale, translation);
        }
    }

    private Map<String, Map<String, String>> getTenantCustomTranslationsCache() {
        final int tenantId = multiTenantService.getCurrentTenantId();

        Map<String, Map<String, String>> tenantCustomTranslationsCache = customTranslationsCache.get(tenantId);

        if (tenantCustomTranslationsCache == null) {
            tenantCustomTranslationsCache = Maps.newHashMap();

            customTranslationsCache.put(tenantId, tenantCustomTranslationsCache);
        }

        return tenantCustomTranslationsCache;
    }

}