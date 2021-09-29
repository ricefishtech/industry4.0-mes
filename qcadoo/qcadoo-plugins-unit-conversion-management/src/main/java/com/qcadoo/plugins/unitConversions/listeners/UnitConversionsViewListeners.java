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
package com.qcadoo.plugins.unitConversions.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.constants.DictionaryFields;
import com.qcadoo.model.constants.QcadooModelConstants;
import com.qcadoo.plugins.dictionaries.constants.QcadooDictionariesContants;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;

@Service
public class UnitConversionsViewListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void redirectToDictionary(final ViewDefinitionState viewDefinitionState, final ComponentState componentState,
            final String[] args) {
        final Entity dictionary = getDictionary("units");
        if (dictionary != null) {
            viewDefinitionState.redirectTo(getUrl(dictionary.getId()), false, true);
        }
    }

    private Entity getDictionary(final String name) {
        return dataDefinitionService.get(QcadooModelConstants.PLUGIN_IDENTIFIER, QcadooModelConstants.MODEL_DICTIONARY).find()
                .add(SearchRestrictions.eq(DictionaryFields.NAME, name)).setMaxResults(1).uniqueResult();
    }

    private String getUrl(final Long dictionaryId) {
        final StringBuilder sb = new StringBuilder("../page/");
        sb.append(QcadooDictionariesContants.PLUGIN_IDENTIFIER).append('/');
        sb.append(QcadooDictionariesContants.VIEW_DICTIONARY_DETAILS);
        sb.append(".html?context={\"form.id\":\"").append(dictionaryId).append("\"}");
        return sb.toString();
    }
}
