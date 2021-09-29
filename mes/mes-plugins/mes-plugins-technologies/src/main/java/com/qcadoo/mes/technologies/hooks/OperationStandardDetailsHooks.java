/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
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
package com.qcadoo.mes.technologies.hooks;

import com.qcadoo.mes.basic.constants.SkillFields;
import com.qcadoo.mes.technologies.constants.OperationSkillFields;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;
import com.qcadoo.view.api.components.LookupComponent;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OperationStandardDetailsHooks {

    private static final String L_FORM = "form";

    private static final String L_OPERATION_ID = "operationId";

    private static final String ORIGINAL_STANDARD_VALUE = "originalStandardValue";

    private static final String ORIGINAL_STANDARD_DESCRIPTION = "originalStandardDescription";

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent operationStandardForm = (FormComponent) view.getComponentByReference(L_FORM);
        LookupComponent standardLookup = (LookupComponent) view.getComponentByReference("standard");

        Entity operationStandard = operationStandardForm.getEntity();
        Entity operation = operationStandard.getBelongsToField("operation");

        filterStandardLookup(standardLookup, operation);
        fillOriginalStandard(view, standardLookup);
    }

    private void fillOriginalStandard(final ViewDefinitionState view, final LookupComponent standardLookup) {
        Entity standard = standardLookup.getEntity();

        if(Objects.nonNull(standard)) {
            FieldComponent originalStandardValueField = (FieldComponent) view.getComponentByReference(ORIGINAL_STANDARD_VALUE);
            FieldComponent originalStandardDescriptionField = (FieldComponent) view.getComponentByReference(ORIGINAL_STANDARD_DESCRIPTION);
            originalStandardValueField.setFieldValue(standard.getStringField("value"));
            originalStandardDescriptionField.setFieldValue(standard.getStringField("description"));
        }
    }

    private void filterStandardLookup(final LookupComponent standardLookup, final Entity operation) {
        FilterValueHolder filterValueHolder = standardLookup.getFilterValue();

        Long operationId = operation.getId();

        if (Objects.isNull(operationId)) {
            filterValueHolder.remove(L_OPERATION_ID);
        } else {
            filterValueHolder.put(L_OPERATION_ID, operationId);
        }

        standardLookup.setFilterValue(filterValueHolder);
    }

}
