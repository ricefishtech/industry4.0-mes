package com.qcadoo.mes.technologies.hooks;


import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;
import com.qcadoo.view.api.components.LookupComponent;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class StandardOperationDetailsHooks {

    private static final String L_FORM = "form";

    private static final String ORIGINAL_STANDARD_VALUE = "originalStandardValue";

    private static final String ORIGINAL_STANDARD_DESCRIPTION = "originalStandardDescription";

    private static final String L_STANDARD_ID = "standardId";

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(L_FORM);
        Entity opStandard = form.getEntity();
        FieldComponent originalStandardValueField = (FieldComponent) view.getComponentByReference(ORIGINAL_STANDARD_VALUE);
        FieldComponent originalStandardDescriptionField = (FieldComponent) view.getComponentByReference(ORIGINAL_STANDARD_DESCRIPTION);
        originalStandardValueField.setFieldValue(opStandard.getBelongsToField("standard").getStringField(
                "value"));
        originalStandardDescriptionField.setFieldValue(opStandard.getBelongsToField("standard").getStringField(
                "description"));

        LookupComponent operationLookup = (LookupComponent) view.getComponentByReference("operation");

        FilterValueHolder filterValueHolder = operationLookup.getFilterValue();

        Long standardId = opStandard.getBelongsToField("standard").getId();

        if (Objects.isNull(standardId)) {
            filterValueHolder.remove(L_STANDARD_ID);
        } else {
            filterValueHolder.put(L_STANDARD_ID, standardId);
        }

        operationLookup.setFilterValue(filterValueHolder);
    }
}
