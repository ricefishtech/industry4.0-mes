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
package com.qcadoo.plugins.dictionaries.internal.hooks;

import static com.qcadoo.model.constants.DictionaryItemFields.TECHNICAL_CODE;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.constants.DictionaryFields;
import com.qcadoo.model.constants.DictionaryItemFields;
import com.qcadoo.model.constants.QcadooModelConstants;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;
import com.qcadoo.view.api.components.WindowComponent;
import com.qcadoo.view.api.ribbon.RibbonActionItem;

@Service
public class DictionaryItemDetailsHooks {

    private static final String L_FORM = "form";

    private static final String L_WINDOW = "window";

    private static final String L_STATES = "states";

    private static final String L_DEACTIVATE = "deactivate";

    private static final String L_ACTIVATE = "activate";

    private static final String L_IS_INTEGER = "isInteger";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void blockedActivationOptionWhenDictionaryWasAddFromSystem(final ViewDefinitionState view) {
        FormComponent dictionaryItemForm = (FormComponent) view.getComponentByReference(L_FORM);

        Long dictionaryItemId = dictionaryItemForm.getEntityId();

        if (dictionaryItemId == null) {
            return;
        }

        Entity dictionaryItem = getDictionaryItem(dictionaryItemId);

        if (StringUtils.isNotEmpty(dictionaryItem.getStringField(TECHNICAL_CODE))) {
            changedEnabledButton(view, false);
        }
    }

    protected void changedEnabledButton(final ViewDefinitionState view, final boolean enabled) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(L_WINDOW);
        RibbonActionItem deactivateButton = window.getRibbon().getGroupByName(L_STATES).getItemByName(L_DEACTIVATE);
        RibbonActionItem activateButton = window.getRibbon().getGroupByName(L_STATES).getItemByName(L_ACTIVATE);

        deactivateButton.setEnabled(enabled);
        deactivateButton.requestUpdate(true);
        activateButton.setEnabled(enabled);
        activateButton.requestUpdate(true);
    }

    public void disableNameEdit(final ViewDefinitionState view) {
        FormComponent dictionaryItemForm = (FormComponent) view.getComponentByReference(L_FORM);
        FieldComponent nameFieldComponent = (FieldComponent) view.getComponentByReference(DictionaryItemFields.NAME);

        if (dictionaryItemForm.getEntityId() == null) {
            return;
        } else {
            nameFieldComponent.setEnabled(false);
        }
    }

    public void disableDictionaryItemFormForExternalItems(final ViewDefinitionState state) {
        FormComponent dictionaryItemForm = (FormComponent) state.getComponentByReference(L_FORM);

        Long dictionaryItemId = dictionaryItemForm.getEntityId();

        if (dictionaryItemId == null) {
            dictionaryItemForm.setFormEnabled(true);

            return;
        }

        Entity dictionaryItem = getDictionaryItem(dictionaryItemId);

        if (Objects.isNull(dictionaryItem)) {
            return;
        }

        String externalNumber = dictionaryItem.getStringField(DictionaryItemFields.EXTERNAL_NUMBER);

        dictionaryItemForm.setFormEnabled(Objects.isNull(externalNumber));
    }

    public void showIntegerCheckbox(final ViewDefinitionState state) {
        FormComponent form = (FormComponent) state.getComponentByReference(L_FORM);

        String dictionaryName = form.getEntity().getBelongsToField(DictionaryItemFields.DICTIONARY)
                .getStringField(DictionaryFields.NAME);

        if (QcadooModelConstants.DICTIONARY_UNITS.equals(dictionaryName)) {
            state.getComponentByReference(L_IS_INTEGER).setVisible(true);
        }
    }

    private Entity getDictionaryItem(final Long dictionaryItemId) {
        return dataDefinitionService
                .get(QcadooModelConstants.PLUGIN_IDENTIFIER, QcadooModelConstants.MODEL_DICTIONARY_ITEM)
                .get(dictionaryItemId);
    }

}
