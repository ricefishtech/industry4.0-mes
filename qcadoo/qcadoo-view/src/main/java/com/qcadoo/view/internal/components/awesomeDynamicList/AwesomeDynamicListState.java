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
package com.qcadoo.view.internal.components.awesomeDynamicList;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.components.AwesomeDynamicListComponent;
import com.qcadoo.view.api.components.FormComponent;
import com.qcadoo.view.internal.api.ContainerState;
import com.qcadoo.view.internal.api.InternalComponentState;
import com.qcadoo.view.internal.api.InternalViewDefinitionState;
import com.qcadoo.view.internal.components.FieldComponentState;
import com.qcadoo.view.internal.components.form.FormComponentPattern;
import com.qcadoo.view.internal.components.form.FormComponentState;
import com.qcadoo.view.internal.internal.ViewDefinitionStateImpl;

public class AwesomeDynamicListState extends FieldComponentState implements AwesomeDynamicListComponent, ContainerState {

    private static final String JSON_FORM_VALUES = "forms";

    private final AwesomeDynamicListModelUtils awesomeDynamicListModelUtils;

    private final FormComponentPattern innerFormPattern;

    private List<FormComponentState> forms;

    AwesomeDynamicListState(final AwesomeDynamicListPattern pattern, final FormComponentPattern innerFormPattern) {
        super(pattern);
        this.innerFormPattern = innerFormPattern;
        this.awesomeDynamicListModelUtils = new AwesomeDynamicListModelUtilsImpl();
    }

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        if (json.has(JSON_FORM_VALUES)) {
            forms = new LinkedList<>();
            JSONArray formValues = json.getJSONArray(JSON_FORM_VALUES);
            for (int i = 0; i < formValues.length(); i++) {
                JSONObject value = formValues.getJSONObject(i);
                String formName = value.getString("name");
                JSONObject formValue = value.getJSONObject("value");
                InternalViewDefinitionState innerFormState = new ViewDefinitionStateImpl();
                FormComponentState formState = (FormComponentState) innerFormPattern.createComponentState(innerFormState);
                formState.setName(formName);
                innerFormPattern.updateComponentStateListeners(innerFormState);
                formState.initialize(formValue, getLocale());
                forms.add(formState);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setFieldValue(final Object value) {
        requestRender();
        forms = new LinkedList<>();
        if (value instanceof List) {
            List<Entity> entities = (List<Entity>) value;
            for (Entity entity : entities) {
                InternalViewDefinitionState innerFormState = new ViewDefinitionStateImpl();
                FormComponentState formState = (FormComponentState) innerFormPattern.createComponentState(innerFormState);
                innerFormPattern.updateComponentStateListeners(innerFormState);
                try {
                    formState.initialize(new JSONObject(), getLocale());
                } catch (JSONException e) {
                    throw new IllegalStateException(e);
                }
                formState.setEntity(entity);
                forms.add(formState);
            }
        }
    }

    @Override
    public Object getFieldValue() {
        return getEntities();
    }

    @Override
    public List<Entity> getEntities() {
        List<Entity> entities = new LinkedList<>();
        for (FormComponent form : forms) {
            Entity e = form.getEntity();
            awesomeDynamicListModelUtils.proxyBelongsToFields(e);
            entities.add(e);
        }
        return entities;
    }

    @Override
    public JSONObject render() throws JSONException {
        JSONObject json = super.render();
        if (!json.has(JSON_CONTENT)) {
            JSONObject childerJson = new JSONObject();
            for (FormComponentState form : forms) {
                childerJson.put(form.getName(), form.render());
            }
            JSONObject content = new JSONObject();
            content.put("innerFormChanges", childerJson);
            json.put(JSON_CONTENT, content);
        }
        return json;
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = new JSONObject();

        JSONArray formValues = new JSONArray();
        for (FormComponentState formState : forms) {
            formValues.put(formState.render());
        }
        json.put(JSON_FORM_VALUES, formValues);

        json.put(JSON_REQUIRED, isRequired());

        return json;
    }

    @Override
    public Map<String, InternalComponentState> getChildren() {
        Map<String, InternalComponentState> children = new HashMap<>();
        for (FormComponentState form : forms) {
            children.put(form.getName(), form);
        }
        return children;
    }

    @Override
    public InternalComponentState getChild(final String name) {
        for (FormComponentState form : forms) {
            if (name.equals(form.getName())) {
                return form;
            }
        }
        return null;
    }

    @Override
    public void addChild(final InternalComponentState state) {
    }

    @Override
    public boolean isHasError() {
        for (FormComponent form : forms) {
            if (form.isHasError()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<FormComponent> getFormComponents() {
        List<FormComponent> formComponents = Lists.newArrayList();
        formComponents.addAll(forms);
        return formComponents;
    }

    @Override
    public FormComponent getFormComponent(final Long id) {
        checkNotNull(id, "id must be given");
        for (FormComponent form : forms) {
            if (id.equals(form.getEntityId())) {
                return form;
            }
        }
        return null;
    }

    @Override
    public InternalComponentState findChild(final String reference) {
        InternalComponentState component = null;
        for (FormComponent form : getFormComponents()) {
            component = (InternalComponentState) form.findFieldComponentByName(reference);
        }
        return component;
    }
}
