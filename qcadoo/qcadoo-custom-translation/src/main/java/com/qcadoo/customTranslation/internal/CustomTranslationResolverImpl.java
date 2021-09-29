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

import com.qcadoo.customTranslation.api.CustomTranslationCacheService;
import com.qcadoo.customTranslation.api.CustomTranslationResolver;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.Locale;

@Service
public class CustomTranslationResolverImpl implements CustomTranslationResolver {

    @Autowired
    private CustomTranslationCacheService customTranslationCacheService;

    @Override
    public boolean isCustomTranslationActive(final String key, final Locale locale) {
        return customTranslationCacheService.isCustomTranslationActive(key, locale.getLanguage());
    }

    @Override
    public String getCustomTranslation(final String key, final Locale locale, final String[] args) {
        String translation = customTranslationCacheService.getCustomTranslation(key, locale.getLanguage());

        if (translation == null) {
            return null;
        } else {
            translation = translation.replace("'", "''");

            Object[] argsToUse = args;

            if (!ObjectUtils.isEmpty(argsToUse)) {
                argsToUse = ArrayUtils.EMPTY_OBJECT_ARRAY;
            }

            MessageFormat messageFormat = new MessageFormat(translation);

            return messageFormat.format(argsToUse);
        }
    }

}