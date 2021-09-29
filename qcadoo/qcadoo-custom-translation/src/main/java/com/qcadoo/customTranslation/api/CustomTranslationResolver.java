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

import java.util.Locale;

/**
 * Service for resolving custom translation.
 * 
 * @since 1.1.8
 */
public interface CustomTranslationResolver {

    /**
     * Checks if there is custom translation with given locale and is active
     * 
     * @param key
     *            translation key
     * @param locale
     *            locale
     * 
     * @return boolean
     */
    boolean isCustomTranslationActive(final String key, final Locale locale);

    /**
     * Gets custom translation with given locale and replaces place holders using args
     * 
     * @param key
     *            translation key
     * @param locale
     *            locale
     * @param args
     *            translation args
     * 
     * @return the custom translation
     */
    String getCustomTranslation(final String key, final Locale locale, final String[] args);

}