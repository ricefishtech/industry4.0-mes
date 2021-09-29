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
package com.qcadoo.customTranslation.api;

import java.util.List;
import java.util.Map;

import com.qcadoo.model.api.Entity;

/**
 * Service for managing custom translations cache.
 * 
 * @since 1.1.8
 */
public interface CustomTranslationCacheService {

    /**
     * Adds custom translation with given key, locale and customTranslation
     * 
     * @param key
     *            translation key
     * @param locale
     *            locale
     * @param customTranslation
     *            custom translation
     */
    void addCustomTranslation(final String key, final String locale, final String customTranslation);

    /**
     * Updates custom translation with given key, locale and customTranslation
     * 
     * @param key
     *            translation key
     * @param locale
     *            locale
     * @param customTranslation
     *            custom translation
     * 
     */
    void updateCustomTranslation(final String key, final String locale, final String customTranslation);

    /**
     * Manages custom translation with given key, locale and customTranslation
     * 
     * @param key
     *            translation key
     * @param locale
     *            locale
     * @param customTranslation
     *            custom translation
     * 
     */
    void manageCustomTranslation(final String key, final String locale, final String customTranslation);

    /**
     * Removes custom translations with given keys
     * 
     * @param keys
     *            translation keys
     * 
     */
    void removeCustomTranslations(final List<String> keys);

    /**
     * Gets custom translation with given key and locale
     * 
     * @param key
     *            translation key
     * @param locale
     *            locale
     * 
     * @return the custom translation
     */
    String getCustomTranslation(final String key, final String locale);

    /**
     * Gets map of custom translations
     * 
     * @return the custom translations
     */
    Map<String, Map<String, String>> getCustomTranslations();

    /**
     * Checks if there is custom translation with given key
     * 
     * @param key
     *            translation key
     * 
     * @return boolean
     */
    boolean isCustomTranslationAdded(final String key);

    /**
     * Checks if there is custom translation with given key and locale
     * 
     * @param key
     *            translation key
     * @param locale
     *            locale
     * 
     * @return boolean
     */
    boolean isCustomTranslationActive(final String key, final String locale);

    /**
     * Loads custom translations
     * 
     * @param customTranslations
     */
    void loadCustomTranslations(final List<Entity> customTranslations);

}