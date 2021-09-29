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
package com.qcadoo.customTranslation.internal.hooks;

import static com.qcadoo.customTranslation.constants.CustomTranslationFields.ACTIVE;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.CUSTOM_TRANSLATION;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.KEY;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.LOCALE;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.PLUGIN_IDENTIFIER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.customTranslation.api.CustomTranslationCacheService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.plugin.api.PluginStateResolver;

@Service
public final class CustomTranslationModelHooks {

    @Autowired
    private CustomTranslationCacheService customTranslationCacheService;

    @Autowired
    private PluginStateResolver pluginStateResolver;

    public boolean checkIfCustomTranslationIsUnique(final DataDefinition customTranslationDD, final Entity customTranslation) {
        String pluginIdentifier = customTranslation.getStringField(PLUGIN_IDENTIFIER);
        String locale = customTranslation.getStringField(LOCALE);
        String key = customTranslation.getStringField(KEY);

        SearchCriteriaBuilder searchCriteriaBuilder = customTranslationDD.find()
                .add(SearchRestrictions.eq(PLUGIN_IDENTIFIER, pluginIdentifier)).add(SearchRestrictions.eq(LOCALE, locale))
                .add(SearchRestrictions.eq(KEY, key));

        if (customTranslation.getId() != null) {
            searchCriteriaBuilder.add(SearchRestrictions.ne("id", customTranslation.getId()));
        }

        SearchResult searchResult = searchCriteriaBuilder.list();

        if (!searchResult.getEntities().isEmpty()) {
            customTranslation.addError(customTranslationDD.getField(KEY),
                    "customTranslation.customTranslation.error.customTranslationIsntUnique");

            return false;
        }

        return true;
    }

    public void changeActiveState(final DataDefinition customTranslationDD, final Entity customTranslation) {
        String pluginIdentifier = customTranslation.getStringField(PLUGIN_IDENTIFIER);
        String translation = customTranslation.getStringField(CUSTOM_TRANSLATION);

        if (pluginStateResolver.isEnabled(pluginIdentifier)) {
            customTranslation.setField(ACTIVE, (translation != null));
        }
    }

    public void updateCache(final DataDefinition customTranslationDD, final Entity customTranslation) {
        boolean active = customTranslation.getBooleanField(ACTIVE);

        String key = customTranslation.getStringField(KEY);
        String locale = customTranslation.getStringField(LOCALE);
        String translation = (active) ? customTranslation.getStringField(CUSTOM_TRANSLATION) : null;

        customTranslationCacheService.manageCustomTranslation(key, locale, translation);
    }

}
