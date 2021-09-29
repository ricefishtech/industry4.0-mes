package com.qcadoo.plugins.users.hooks;

import com.qcadoo.plugins.users.constants.GroupDetailsConstants;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FormComponent;
import com.qcadoo.view.api.components.LookupComponent;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;
import org.springframework.stereotype.Service;

@Service
public class GroupDetailsHooks {

    private static String L_FORM = "form";

    public void fillCriteriaModifiers(final ViewDefinitionState viewDefinitionState) {
        LookupComponent roles = (LookupComponent) viewDefinitionState.getComponentByReference("roleLookup");
        FormComponent form = (FormComponent) viewDefinitionState.getComponentByReference(L_FORM);
        if (form.getEntityId() != null) {
                FilterValueHolder filter = roles.getFilterValue();
                filter.put(GroupDetailsConstants.GROUP_ID, form.getEntityId());
                roles.setFilterValue(filter);
            }
        roles.requestComponentUpdateState();
    }
}
