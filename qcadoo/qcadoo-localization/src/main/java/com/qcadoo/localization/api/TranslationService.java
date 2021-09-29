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
package com.qcadoo.localization.api;

import java.util.Locale;
import java.util.Map;

import com.qcadoo.localization.internal.module.LocaleModuleFactory;

/**
 * Service for getting translations.
 * 
 * @since 0.4.0
 */
public interface TranslationService {

    /**
     * Default message, used when ignoreMissingTranslations is set to true and mesage translation was not found
     */
    String DEFAULT_MISSING_MESSAGE = "-";

    /**
     * Returns all messages (key and translation) for given group name.
     * 
     * @param group
     *            group
     * @param locale
     *            prefix
     * @return messages
     */
    Map<String, String> getMessagesGroup(String group, Locale locale);

    /**
     * Translates given code into the locale using the args.
     * 
     * @param code
     *            message's code
     * @param locale
     *            locale
     * @param args
     *            message's args
     * @return the translation
     */
    String translate(String code, Locale locale, String... args);

    /**
     * Translates given codes into the locale using the args. First translated code will be returned.
     * 
     * @param code
     *            message's code
     * @param secondCode
     *            message's code
     * @param locale
     *            locale
     * @param args
     *            message's args
     * @return the translation
     */
    String translate(String code, String secondCode, Locale locale, String... args);

    /**
     * Translates given codes into the locale using the args. First translated code will be returned.
     * 
     * @param code
     *            message's code
     * @param secondCode
     *            message's code
     * @param thirdCode
     *            message's code
     * @param locale
     *            locale
     * @param args
     *            message's args
     * @return the translation
     */
    String translate(String code, String secondCode, String thirdCode, Locale locale, String... args);

    /**
     * Returns a map of available locales.
     * 
     * @return a map with a locale value and its name
     * @see LocaleModuleFactory
     */
    Map<String, String> getLocales();

    /**
     * Returns a max upload size.
     *
     * @return a max upload size
     * @see int
     */
    int getMaxUploadSize();
}
