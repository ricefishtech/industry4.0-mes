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
package com.qcadoo.plugins.customTranslations.internal.listeners;

import static com.qcadoo.customTranslation.constants.CustomTranslationFields.CUSTOM_TRANSLATION;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ComponentState.MessageType;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.GridComponent;

@Service
public class CustomTranslationsListListeners {

    private static final String L_GRID = "grid";

    private static final String L_WINDOW_ACTIVE_MENU = "window.activeMenu";

    public void cleanCustomTranslations(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent customTranslationsGrid = (GridComponent) view.getComponentByReference(L_GRID);

        List<Entity> customTranslations = customTranslationsGrid.getSelectedEntities();

        if (!customTranslations.isEmpty()) {
            for (Entity customTranslation : customTranslations) {
                customTranslation.setField(CUSTOM_TRANSLATION, null);

                customTranslation.getDataDefinition().save(customTranslation);
            }

            customTranslationsGrid
                    .addMessage("qcadooCustomTranslations.customTranslationsList.message.cleanCustomTranslationsSuccess",
                            MessageType.SUCCESS);
        }
    }

    public void replaceCustomTranslations(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put(L_WINDOW_ACTIVE_MENU, "administration.customTranslations");

        String url = "../page/qcadooCustomTranslations/replaceCustomTranslations.html";

        view.redirectTo(url, false, true, parameters);
    }

}
