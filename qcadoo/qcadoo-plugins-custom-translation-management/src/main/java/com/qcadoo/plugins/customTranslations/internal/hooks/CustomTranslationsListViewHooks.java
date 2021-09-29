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
package com.qcadoo.plugins.customTranslations.internal.hooks;

import com.qcadoo.model.api.search.CustomRestriction;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.GridComponent;
import com.qcadoo.view.api.components.WindowComponent;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import com.qcadoo.view.api.ribbon.RibbonGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import static com.qcadoo.customTranslation.constants.CustomTranslationFields.LOCALE;

@Service
public class CustomTranslationsListViewHooks {

    private static final String L_CLEAN_CUSTOM_TRANSLATIONS = "cleanCustomTranslations";

    private static final String L_CLEAN = "clean";

    private static final String L_WINDOW = "window";

    private static final String L_GRID = "grid";

    public void updateRibbonState(final ViewDefinitionState view) {
        GridComponent customTranslationsGrid = (GridComponent) view.getComponentByReference(L_GRID);

        WindowComponent window = (WindowComponent) view.getComponentByReference(L_WINDOW);
        RibbonGroup clean = (RibbonGroup) window.getRibbon().getGroupByName(L_CLEAN);

        RibbonActionItem cleanCustomTranslations = (RibbonActionItem) clean.getItemByName(L_CLEAN_CUSTOM_TRANSLATIONS);

        boolean isAnyTranslationSelected = customTranslationsGrid.getSelectedEntities().isEmpty();

        updateButtonState(cleanCustomTranslations, !isAnyTranslationSelected);
    }

    private void updateButtonState(final RibbonActionItem ribbonActionItem, final boolean isEnabled) {
        ribbonActionItem.setEnabled(isEnabled);
        ribbonActionItem.requestUpdate(true);
    }

    public void setLocaleToDefault(final ViewDefinitionState view) {
        FieldComponent localeField = (FieldComponent) view.getComponentByReference(LOCALE);

        if (localeField.getFieldValue() == null) {
            localeField.setFieldValue(LocaleContextHolder.getLocale().getLanguage());
            localeField.requestComponentUpdateState();
        }
    }

    public void addDiscriminatorRestrictionToGrid(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        addDiscriminatorRestrictionToGrid(view);
    }

    public void addDiscriminatorRestrictionToGrid(final ViewDefinitionState view) {
        GridComponent customTranslationsGrid = (GridComponent) view.getComponentByReference(L_GRID);

        FieldComponent localeField = (FieldComponent) view.getComponentByReference(LOCALE);

        final String locale = (String) localeField.getFieldValue();

        if (StringUtils.isEmpty(locale)) {
            customTranslationsGrid.setCustomRestriction(null);
        } else {
            customTranslationsGrid.setCustomRestriction(new CustomRestriction() {

                @Override
                public void addRestriction(final SearchCriteriaBuilder searchCriteriaBuilder) {
                    searchCriteriaBuilder.add(SearchRestrictions.eq(LOCALE, locale));
                }

            });
        }
    }

}
