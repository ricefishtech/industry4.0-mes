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
package com.qcadoo.model.internal.dictionaries;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.aop.Monitorable;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchOrders;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.constants.DictionaryFields;
import com.qcadoo.model.constants.DictionaryItemFields;
import com.qcadoo.model.constants.QcadooModelConstants;
import com.qcadoo.model.internal.api.InternalDictionaryService;

@Service
public final class DictionaryServiceImpl implements InternalDictionaryService {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    public List<String> getKeys(final String dictionary) {
        checkArgument(hasText(dictionary), "dictionary name must be given");

        List<Entity> items = createCriteriaForItemsFrom(dictionary).addOrder(SearchOrders.asc(DictionaryItemFields.NAME)).list()
                .getEntities();
        List<String> keys = new ArrayList<String>();

        for (Entity item : items) {
            keys.add(item.getStringField(DictionaryItemFields.NAME));
        }

        return keys;
    }

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    public List<String> getActiveKeys(final String dictionary) {
        checkArgument(hasText(dictionary), "dictionary name must be given");

        List<Entity> items = createCriteriaForItemsFrom(dictionary).add(SearchRestrictions.eq("active", true))
                .addOrder(SearchOrders.asc(DictionaryItemFields.NAME)).list().getEntities();
        List<String> keys = new ArrayList<String>();

        for (Entity item : items) {
            keys.add(item.getStringField(DictionaryItemFields.NAME));
        }

        return keys;
    }

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    public Map<String, String> getValues(final String dictionary, final Locale locale) {
        checkArgument(hasText(dictionary), "dictionary name must be given");

        List<Entity> items = createCriteriaForActiveItemsFrom(dictionary).addOrder(SearchOrders.asc(DictionaryItemFields.NAME))
                .list().getEntities();

        Map<String, String> values = new LinkedHashMap<String, String>();

        // TODO MAKU translate dictionary values
        for (Entity item : items) {
            values.put(item.getStringField(DictionaryItemFields.NAME), item.getStringField(DictionaryItemFields.NAME));
        }

        return values;
    }

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    public Map<String, String> getKeyValues(final String dictionary, final Locale locale) {
        checkArgument(hasText(dictionary), "dictionary name must be given");

        List<Entity> items = createCriteriaForActiveItemsFrom(dictionary).addOrder(SearchOrders.asc(DictionaryItemFields.NAME))
                .list().getEntities();

        Map<String, String> values = new LinkedHashMap<>();

        for (Entity item : items) {
            values.put(item.getStringField(DictionaryItemFields.TECHNICAL_CODE), item.getStringField(DictionaryItemFields.NAME));
        }

        return values;
    }

    @Override
    @Transactional(readOnly = true)
    @Monitorable
    public Set<String> getDictionaries() {
        List<Entity> dictionaries = getDictionaryDataDefinition().find().addOrder(SearchOrders.asc(DictionaryFields.NAME)).list()
                .getEntities();

        Set<String> names = new HashSet<String>();

        for (Entity dictionary : dictionaries) {
            if ((Boolean) dictionary.getField(DictionaryFields.ACTIVE)) {
                names.add(dictionary.getStringField(DictionaryFields.NAME));
            }
        }

        return names;
    }

    @Override
    @Transactional
    @Monitorable
    public void createIfNotExists(final String pluginIdentifier, final String name, final String... values) {
        SearchResult serachResult = getDictionaryDataDefinition().find().add(SearchRestrictions.eq(DictionaryFields.NAME, name))
                .list();
        if (serachResult.getTotalNumberOfEntities() > 0) {
            Entity dictionaryEntity = serachResult.getEntities().get(0);
            dictionaryEntity.setField(DictionaryFields.ACTIVE, true);
            getDictionaryDataDefinition().save(dictionaryEntity);
            return;
        }

        Entity dictionary = getDictionaryDataDefinition().create();
        dictionary.setField(DictionaryFields.PLUGIN_IDENTIFIER, pluginIdentifier);
        dictionary.setField(DictionaryFields.NAME, name);
        dictionary.setField(DictionaryFields.ACTIVE, true);
        dictionary = getDictionaryDataDefinition().save(dictionary);

        for (String value : values) {
            Entity item = getItemDataDefinition().create();
            item.setField(DictionaryItemFields.DICTIONARY, dictionary);
            item.setField(DictionaryItemFields.DESCRIPTION, "");
            item.setField(DictionaryItemFields.NAME, value);
            getItemDataDefinition().save(item);
        }
    }

    @Override
    public String getName(final String dictionaryName, final Locale locale) {
        Entity dictionary = getDictionaryDataDefinition().find()
                .add(SearchRestrictions.eq(DictionaryFields.NAME, dictionaryName)).setMaxResults(1).uniqueResult();
        return translationService.translate(dictionary.getStringField(DictionaryFields.PLUGIN_IDENTIFIER) + "." + dictionaryName
                + ".dictionary", locale);
    }

    @Override
    public Entity getItemEntity(final String dictionaryName, final String itemName) {
        return createCriteriaForActiveItemsFrom(dictionaryName).add(SearchRestrictions.eq(DictionaryItemFields.NAME, itemName))
                .setMaxResults(1).uniqueResult();
    }

    private static final String ITEM_DICTIONARY_NAME_PATH = DictionaryItemFields.DICTIONARY + '.' + DictionaryFields.NAME;

    private SearchCriteriaBuilder createCriteriaForActiveItemsFrom(final String dictionaryName) {
        return createCriteriaForItemsFrom(dictionaryName).add(SearchRestrictions.eq(DictionaryItemFields.ACTIVE, true));
    }

    private SearchCriteriaBuilder createCriteriaForItemsFrom(final String dictionaryName) {
        return getItemDataDefinition().find().createAlias(DictionaryItemFields.DICTIONARY, DictionaryItemFields.DICTIONARY)
                .add(SearchRestrictions.eq(ITEM_DICTIONARY_NAME_PATH, dictionaryName));
    }

    private DataDefinition getDictionaryDataDefinition() {
        return dataDefinitionService.get(QcadooModelConstants.PLUGIN_IDENTIFIER, QcadooModelConstants.MODEL_DICTIONARY);
    }

    private DataDefinition getItemDataDefinition() {
        return dataDefinitionService.get(QcadooModelConstants.PLUGIN_IDENTIFIER, QcadooModelConstants.MODEL_DICTIONARY_ITEM);
    }

    @Override
    @Transactional
    @Monitorable
    public void disable(final String pluginIdentifier, final String name) {
        final DataDefinition dictionaryDataDefinition = getDictionaryDataDefinition();
        final SearchCriteriaBuilder searchCriteriaBuilder = dictionaryDataDefinition.find();
        searchCriteriaBuilder.add(SearchRestrictions.eq(DictionaryFields.NAME, name));
        searchCriteriaBuilder.add(SearchRestrictions.eq(DictionaryFields.ACTIVE, true));
        searchCriteriaBuilder.setMaxResults(1);
        final Entity dictionaryEntity = searchCriteriaBuilder.uniqueResult();
        if (dictionaryEntity != null) {
            dictionaryEntity.setField(DictionaryFields.ACTIVE, false);
            dictionaryDataDefinition.save(dictionaryEntity);
        }
    }

    public Boolean checkIfUnitIsInteger(String unit) {
        return getItemEntity(QcadooModelConstants.DICTIONARY_UNITS, unit).getBooleanField(DictionaryItemFields.IS_INTEGER);
    }

}
