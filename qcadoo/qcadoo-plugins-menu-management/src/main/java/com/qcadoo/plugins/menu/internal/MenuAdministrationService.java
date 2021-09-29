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
package com.qcadoo.plugins.menu.internal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.CustomRestriction;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchDisjunction;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;
import com.qcadoo.view.api.components.GridComponent;
import com.qcadoo.view.api.utils.TranslationUtilsService;
import com.qcadoo.view.constants.MenuCategoryFields;
import com.qcadoo.view.constants.MenuItemFields;
import com.qcadoo.view.constants.QcadooViewConstants;

@Service
public class MenuAdministrationService {

    private static final String L_FORM = "form";

    private static final String L_GRID = "grid";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private TranslationUtilsService translationUtilsService;

    private static final List<String[]> HIDDEN_CATEGORIES = new ArrayList<String[]>();

    static {
        HIDDEN_CATEGORIES.add(new String[] { "qcadooView", "home" });
        HIDDEN_CATEGORIES.add(new String[] { "qcadooView", "administration" });
    }

    public void addRestrictionToCategoriesGrid(final ViewDefinitionState viewDefinitionState) {
        GridComponent categoriesGrid = (GridComponent) viewDefinitionState.getComponentByReference(L_GRID);

        categoriesGrid.setCustomRestriction(new CustomRestriction() {

            @Override
            public void addRestriction(final SearchCriteriaBuilder searchCriteriaBuilder) {
                SearchDisjunction disjunction = SearchRestrictions.disjunction();

                for (String[] category : HIDDEN_CATEGORIES) {
                    disjunction.add(SearchRestrictions.and(
                            SearchRestrictions.eq(MenuCategoryFields.PLUGIN_IDENTIFIER, category[0]),
                            SearchRestrictions.eq(MenuCategoryFields.NAME, category[1])));
                }

                searchCriteriaBuilder.add(SearchRestrictions.not(disjunction));
            }

        });
    }

    public void translateCategoriesGrid(final ViewDefinitionState viewDefinitionState) {
        GridComponent categoriesGrid = (GridComponent) viewDefinitionState.getComponentByReference(L_GRID);

        for (Entity category : categoriesGrid.getEntities()) {
            if (category.getStringField(MenuCategoryFields.PLUGIN_IDENTIFIER) != null) {
                category.setField(MenuCategoryFields.NAME,
                        translationUtilsService.getCategoryTranslation(category, viewDefinitionState.getLocale()));
            }
        }
    }

    public void translateCategoryForm(final ViewDefinitionState viewDefinitionState) {
        FormComponent categoryForm = (FormComponent) viewDefinitionState.getComponentByReference(L_FORM);

        Long categoryId = categoryForm.getEntityId();

        if (categoryId != null) {
            Entity category = dataDefinitionService
                    .get(QcadooViewConstants.PLUGIN_IDENTIFIER, QcadooViewConstants.MODEL_CATEGORY).get(categoryId);

            if ((category != null) && (category.getStringField(MenuCategoryFields.PLUGIN_IDENTIFIER) != null)) {
                FieldComponent categoryNameField = (FieldComponent) viewDefinitionState.getComponentByReference("categoryName");

                categoryNameField.setEnabled(false);

                categoryNameField.setFieldValue(translationUtilsService.getCategoryTranslation(category,
                        viewDefinitionState.getLocale()));
            }

            GridComponent categoryItemsGrid = (GridComponent) viewDefinitionState.getComponentByReference("itemsGrid");

            for (Entity item : categoryItemsGrid.getEntities()) {
                if (item.getStringField(MenuItemFields.PLUGIN_IDENTIFIER) != null) {
                    item.setField(MenuItemFields.NAME,
                            translationUtilsService.getItemTranslation(item, viewDefinitionState.getLocale()));
                }
            }
        }
    }

    public void translateItemForm(final ViewDefinitionState viewDefinitionState) {
        FormComponent itemForm = (FormComponent) viewDefinitionState.getComponentByReference(L_FORM);

        Long itemId = itemForm.getEntityId();

        if (itemId != null) {
            Entity item = dataDefinitionService.get(QcadooViewConstants.PLUGIN_IDENTIFIER, QcadooViewConstants.MODEL_ITEM).get(
                    itemId);

            if ((item != null) && (item.getStringField(MenuItemFields.PLUGIN_IDENTIFIER) != null)) {
                FieldComponent itemNameField = (FieldComponent) viewDefinitionState.getComponentByReference("itemName");
                FieldComponent itemViewField = (FieldComponent) viewDefinitionState.getComponentByReference("itemView");

                itemNameField.setEnabled(false);
                itemViewField.setEnabled(false);
            }
        }
    }

}
