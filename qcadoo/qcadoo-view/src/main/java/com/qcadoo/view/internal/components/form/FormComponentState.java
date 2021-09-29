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
package com.qcadoo.view.internal.components.form;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityMessagesHolder;
import com.qcadoo.model.api.EntityOpResult;
import com.qcadoo.model.api.EntityTree;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.expression.ExpressionUtils;
import com.qcadoo.model.api.validators.ErrorMessage;
import com.qcadoo.model.internal.DetachedEntityTreeImpl;
import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;
import com.qcadoo.view.internal.FieldEntityIdChangeListener;
import com.qcadoo.view.internal.ScopeEntityIdChangeListener;
import com.qcadoo.view.internal.components.FieldComponentState;
import com.qcadoo.view.internal.components.lookup.LookupComponentState;
import com.qcadoo.view.internal.components.tree.TreeComponentState;
import com.qcadoo.view.internal.states.AbstractContainerState;

public class FormComponentState extends AbstractContainerState implements FormComponent {

    public static final String JSON_ENTITY_ID = "entityId";

    public static final String JSON_IS_ACTIVE = "isActive";

    public static final String JSON_VALID = "valid";

    public static final String JSON_BACK_REQUIRED = "performBackRequired";

    public static final String JSON_HEADER = "header";

    public static final String JSON_HEADER_ENTITY_IDENTIFIER = "headerEntityIdentifier";

    public static final String INITIALIZE_EVENT_NAME = "initialize";

    private Long entityId;

    private Long contextEntityId;

    private boolean active;

    private boolean valid = true;

    private final Map<String, Object> context = new HashMap<String, Object>();

    private final FormEventPerformer eventPerformer = new FormEventPerformer();

    private final String expressionEdit;

    private Map<String, FieldComponentState> fieldComponents;

    private final String expressionNew;

    private boolean performBackRequired = false;

    private final SecurityRole authorizationRole;

    private final SecurityRolesService securityRolesService;

    private final FormComponentPattern pattern;

    public FormComponentState(final FormComponentPattern pattern) {
        super(pattern);
        this.pattern = pattern;
        this.expressionNew = pattern.getExpressionNew();
        this.expressionEdit = pattern.getExpressionEdit();
        this.authorizationRole = pattern.getAuthorizationRole();
        this.securityRolesService = pattern.getApplicationContext().getBean(SecurityRolesService.class);
        registerEvent("clear", eventPerformer, "clear");
        registerEvent("save", eventPerformer, "save");
        registerEvent("saveAndClear", eventPerformer, "saveAndClear");
        registerEvent(INITIALIZE_EVENT_NAME, eventPerformer, INITIALIZE_EVENT_NAME);
        registerEvent("initializeAfterBack", eventPerformer, INITIALIZE_EVENT_NAME);
        registerEvent("reset", eventPerformer, INITIALIZE_EVENT_NAME);
        registerEvent("delete", eventPerformer, "delete");
        registerEvent("copy", eventPerformer, "copy");
        registerEvent("activate", eventPerformer, "activate");
        registerEvent("deactivate", eventPerformer, "deactivate");
    }

    @Override
    public void onFieldEntityIdChange(final Long entityId) {
        setFieldValue(entityId);
        eventPerformer.initialize(new String[0]);
    }

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        if (!json.has(JSON_ENTITY_ID)) {
            return;
        }
        if (json.isNull(JSON_ENTITY_ID)) {
            entityId = null;
        } else {
            entityId = json.getLong(JSON_ENTITY_ID);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initializeContext(final JSONObject json) throws JSONException {
        super.initializeContext(json);

        Iterator<String> iterator = json.keys();
        while (iterator.hasNext()) {
            String field = iterator.next();
            if ("id".equals(field)) {
                if (entityId == null) {
                    contextEntityId = json.getLong(field);
                }
            } else if (!json.isNull(field)) {
                context.put(field, json.get(field));
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.qcadoo.view.components.form.EntityComponentState#getEntityId()
     */
    @Override
    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(final Long entityId) {
        this.entityId = entityId;
        requestRender();
        requestUpdateState();
        notifyEntityIdChangeListeners(entityId);
    }

    @Override
    public void setFieldValue(final Object value) {
        setEntityId((Long) value);
    }

    /*
     * (non-Javadoc)
     * @see com.qcadoo.view.components.form.EntityComponentState#getEntity()
     */
    @Override
    public Entity getEntity() {
        Entity entity = getDataDefinition().create(entityId);
        copyFieldsAndContextTo(entity);
        return entity;
    }

    @Override
    public Entity getPersistedEntityWithIncludedFormValues() {
        if (entityId == null) {
            return getEntity();
        }
        Entity entity = getDataDefinition().get(entityId);
        if (entity == null) {
            return getEntity();
        }
        copyFieldsAndContextTo(entity);
        return entity;
    }

    private void copyFieldsAndContextTo(final Entity entity) {
        copyFieldsToEntity(entity);
        copyContextToEntity(entity);
    }

    @Override
    public void setEntity(final Entity entity) {
        if (entity.isValid()) {
            active = entity.isActive();
        } else {
            valid = false;
            requestRender();
            copyMessages(entity.getGlobalErrors());
        }

        copyEntityToFields(entity, entity.isValid());
        setEntityId(entity.getId());
        setFieldsRequiredAndDisables();
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean isHasError() { // form never has error - its field can have
        for (ComponentState child : getChildren().values()) {
            if (child.isHasError()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getFieldValue() {
        return getEntityId();
    }

    @Override
    public JSONObject render() throws JSONException {
        JSONObject json = super.render();
        json.put(JSON_BACK_REQUIRED, performBackRequired);
        return json;
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_VALID, isValid());
        json.put(JSON_IS_ACTIVE, active);
        if (entityId == null) {
            json.put(JSON_ENTITY_ID, JSONObject.NULL);
            json.put(JSON_HEADER, getTranslationService().translate(getTranslationPath() + ".headerNew", getLocale()));
            json.put(JSON_HEADER_ENTITY_IDENTIFIER, getHeaderNew());
        } else {
            json.put(JSON_ENTITY_ID, entityId);
            json.put(JSON_HEADER, getTranslationService().translate(getTranslationPath() + ".headerEdit", getLocale()));
            json.put(JSON_HEADER_ENTITY_IDENTIFIER, getHeaderEdit());
        }
        return json;
    }

    private String getHeaderEdit() {
        Entity entity = getDataDefinition().get(entityId);
        return ExpressionUtils.getValue(entity, expressionEdit, getLocale());
    }

    private Object getHeaderNew() {
        if (expressionNew == null) {
            return JSONObject.NULL;
        } else {
            return ExpressionUtils.getValue(getEntity(), expressionNew, getLocale());
        }
    }

    private Map<String, FieldComponentState> getFieldComponents() {
        if (fieldComponents != null) {
            return fieldComponents;
        }

        fieldComponents = new HashMap<String, FieldComponentState>();

        for (Map.Entry<String, FieldEntityIdChangeListener> field : getFieldEntityIdChangeListeners().entrySet()) {
            if (isValidFormField(field.getKey(), field.getValue())) {
                fieldComponents.put(field.getKey(), (FieldComponentState) field.getValue());
            }
        }

        for (Map.Entry<String, ScopeEntityIdChangeListener> field : getScopeEntityIdChangeListeners().entrySet()) {
            if (!(field.getValue() instanceof FieldComponent)) {
                continue;
            }
            fieldComponents.put(field.getKey(), (FieldComponentState) field.getValue());
        }

        return fieldComponents;
    }

    private boolean isValidFormField(final String fieldName, final FieldEntityIdChangeListener component) {
        if (!(component instanceof FieldComponent)) {
            return false;
        }

        FieldDefinition field = getDataDefinition().getField(fieldName);

        if (field == null) {
            return false;
        }

        return true;
    }

    private void copyFieldsToEntity(final Entity entity) {
        FieldComponentState fieldComponentState = null;
        for (Map.Entry<String, FieldComponentState> field : getFieldComponents().entrySet()) {
            fieldComponentState = field.getValue();
            if (fieldComponentState.isPersistent()) {
                entity.setField(field.getKey(), convertFieldFromString(fieldComponentState.getFieldValue(), field.getKey()));
            }

            if (fieldComponentState.isHasError()) {
                entity.setNotValid();
            }
        }
    }

    private void copyContextToEntity(final Entity entity) {
        for (String field : getDataDefinition().getFields().keySet()) {
            if (context.containsKey(field)) {
                entity.setField(field, convertFieldFromString(context.get(field), field));
            }
        }
    }

    private void setFieldsRequiredAndDisables() {
        for (Map.Entry<String, FieldComponentState> field : getFieldComponents().entrySet()) {
            FieldDefinition fieldDefinition = getDataDefinition().getField(field.getKey());

            if (fieldDefinition.isRequired()) {
                field.getValue().setRequired(true);
            }

            if (fieldDefinition.isReadOnly()) {
                field.getValue().setEnabled(false);
            }
        }
    }

    private Object convertFieldFromString(final Object value, final String field) {
        if (value instanceof String) {
            return getDataDefinition().getField(field).getType().fromString((String) value, getLocale());
        } else {
            return value;
        }
    }

    private void copyFieldMessages(final EntityMessagesHolder messagesHolder) {
        for (Map.Entry<String, FieldComponentState> fieldComponentEntry : getFieldComponents().entrySet()) {
            ErrorMessage message = messagesHolder.getError(fieldComponentEntry.getKey());
            copyMessage(fieldComponentEntry.getValue(), message);
        }
    }

    private void copyEntityToFields(final Entity entity, final boolean requestUpdateState) {
        for (Map.Entry<String, FieldComponentState> field : getFieldComponents().entrySet()) {
            ErrorMessage message = entity.getError(field.getKey());
            copyMessage(field.getValue(), message);
            if (fieldIsGridCorrespondingLookup(field.getValue(), field.getKey(), entity)) {
                continue;
            }
            if (fieldIsDetachedEntityTree(field.getValue(), field.getKey(), entity)) {
                EntityTree tree = entity.getTreeField(field.getKey());
                if (tree != null) {
                    ((TreeComponentState) field.getValue()).setRootNode(tree.getRoot());
                }
            }
            field.getValue().setFieldValue(convertFieldToString(entity.getField(field.getKey()), field.getKey()));
            if (requestUpdateState) {
                field.getValue().requestComponentUpdateState();
            }
        }
    }

    private boolean fieldIsGridCorrespondingLookup(final FieldComponentState field, final String databaseFieldName,
            final Entity entity) {
        return (field instanceof LookupComponentState) && (entity.getField(databaseFieldName) instanceof Collection);
    }

    private boolean fieldIsDetachedEntityTree(final Object fieldValue, final String databaseFieldName, final Entity entity) {
        return fieldValue instanceof TreeComponentState && entity.getField(databaseFieldName) instanceof DetachedEntityTreeImpl;
    }

    private Object convertFieldToString(final Object value, final String field) {
        if (value instanceof String) {
            return value;
        }

        if (value == null) {
            return "";
        }

        if (value instanceof Collection) {
            return value;
        }

        return getDataDefinition().getField(field).getType().toString(value, getLocale());
    }

    @Override
    public void setFormEnabled(final boolean enabled) {
        for (Map.Entry<String, FieldComponentState> field : getFieldComponents().entrySet()) {
            FieldDefinition fieldDefinition = getDataDefinition().getField(field.getKey());

            if (!(fieldDefinition.isReadOnly())) {
                field.getValue().setEnabled(enabled);
                field.getValue().requestComponentUpdateState();
            }
        }
        setEnabled(enabled);
    }

    private void setPerformBackRequired(final boolean backRequired) {
        this.performBackRequired = backRequired;
    }

    protected final class FormEventPerformer {

        public void saveAndClear(final String[] args) {
            save(args);
            if (isValid()) {
                clear(args);
            }
        }

        public void save(final String[] args) {
            Entity databaseEntity = getDatabaseEntity();

            if (databaseEntity == null && entityId != null) {
                throw new IllegalStateException("Entity cannot be found");
            }

            Entity entity = getEntity();

            if (entity.isValid()) {
                try {
                    entity = getDataDefinition().save(entity);
                } catch (org.hibernate.exception.ConstraintViolationException ex) {
                    entity.addGlobalError("qcadooView.errorPage.error.constraintViolationException.explanation");
                    entity.setNotValid();
                }
                setEntity(entity);
            }

            copyGlobalMessages(entity.getGlobalMessages());

            if (entity.isValid()) {
                setFieldValue(entity.getId());
                addTranslatedMessage(translateMessage("saveMessage"), MessageType.SUCCESS);
            } else {
                if (entity.getGlobalErrors().size() == 0) {
                    addTranslatedMessage(translateMessage("saveFailedMessage"), MessageType.FAILURE);
                }
                valid = false;
            }

            setFieldsRequiredAndDisables();
        }

        public void copy(final String[] args) {
            if (entityId == null) {
                addTranslatedMessage(translateMessage("copyFailedMessage"), MessageType.FAILURE);
                return;
            }

            List<Entity> copiedEntities = getDataDefinition().copy(entityId);

            if (!copiedEntities.isEmpty() && copiedEntities.get(0).getId() != null) {
                clear(args);
                setEntityId(copiedEntities.get(0).getId());
                initialize(args);
                addTranslatedMessage(translateMessage("copyMessage"), MessageType.SUCCESS);
            } else {
                addTranslatedMessage(translateMessage("copyFailedMessage"), MessageType.FAILURE);
            }
        }

        public void activate(final String[] args) {
            try {

                if (entityId == null) {
                    addTranslatedMessage(translateMessage("activateFailedMessage"), MessageType.FAILURE);
                    return;
                }

                List<Entity> activatedEntities = getDataDefinition().activate(entityId);

                if (!activatedEntities.isEmpty()) {
                    active = true;
                    addTranslatedMessage(translateMessage("activateMessage"), MessageType.SUCCESS);
                    setEntity(activatedEntities.get(0));
                }
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("validation")) {
                    addTranslatedMessage(translateMessage("activateFailedValidationMessage"), MessageType.FAILURE);
                } else {
                    addTranslatedMessage(translateMessage("activateFailedMessage"), MessageType.FAILURE);
                }
            }
        }

        public void deactivate(final String[] args) {
            if (entityId == null) {
                addTranslatedMessage(translateMessage("deactivateFailedMessage"), MessageType.FAILURE);
                return;
            }

            try {
                List<Entity> deactivatedEntities = getDataDefinition().deactivate(entityId);

                if (!deactivatedEntities.isEmpty()) {
                    active = false;
                    addTranslatedMessage(translateMessage("deactivateMessage"), MessageType.SUCCESS);
                    setEntity(deactivatedEntities.get(0));
                }
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("validation")) {
                    addTranslatedMessage(translateMessage("deactivateFailedValidationMessage"), MessageType.FAILURE);
                } else {
                    addTranslatedMessage(translateMessage("deactivateFailedMessage"), MessageType.FAILURE);
                }
            }
        }

        public void delete(final String[] args) {
            Entity entity = getDatabaseEntity();
            if (entity == null) {
                throw new IllegalStateException("Entity cannot be found");
            } else if (entityId != null) {
                EntityOpResult result = getDataDefinition().delete(entityId);
                copyMessages(result.getMessagesHolder().getGlobalErrors());
                copyFieldMessages(result.getMessagesHolder());
                if (result.isSuccessfull()) {
                    addTranslatedMessage(translateMessage("deleteMessage"), MessageType.SUCCESS);
                    clear(args);
                    setPerformBackRequired(true);
                } else {
                    setPerformBackRequired(false);
                }
            }
        }

        public void initialize(final String[] args) {
            if (contextEntityId != null) {
                entityId = contextEntityId;
            }

            Entity entity = getDatabaseEntity();
            if (entity != null) {
                active = entity.isActive();
                copyEntityToFields(entity, true);
                setFieldValue(entity.getId());
                setFieldsRequiredAndDisables();
                if (!securityRolesService.canAccess(authorizationRole)) {
                    setFormEnabled(false);
                }
                return;
            }

            if (entityId != null) {
                setFormEnabled(false);
                active = false;
                valid = false;
                addTranslatedMessage(translateMessage("entityNotFound"), MessageType.FAILURE);
                return;
            }

            clear(args);
            if (!securityRolesService.canAccess(authorizationRole)) {
                setFormEnabled(false);
            }
        }

        public void clear(final String[] args) {
            active = false;
            clearFields();
            setFieldValue(null);
            copyDefaultValuesToFields();
            setFieldsRequiredAndDisables();
        }

        private Entity getDatabaseEntity() {
            if (entityId == null) {
                return null;
            }
            return getDataDefinition().get(entityId);
        }

        private void copyDefaultValuesToFields() {
            for (Map.Entry<String, FieldComponentState> field : getFieldComponents().entrySet()) {
                FieldDefinition fieldDefinition = getDataDefinition().getField(field.getKey());
                if (fieldDefinition.getDefaultValue() != null) {
                    field.getValue().setFieldValue(convertFieldToString(fieldDefinition.getDefaultValue(), field.getKey()));
                    field.getValue().requestComponentUpdateState();
                }
            }
        }

        private void clearFields() {
            for (Map.Entry<String, FieldComponentState> field : getFieldComponents().entrySet()) {
                field.getValue().setFieldValue(null);
                field.getValue().requestComponentUpdateState();
            }
        }

    }

    @Override
    public FieldComponent findFieldComponentByName(final String name) {
        return (FieldComponent) findChild(name);
    }

}
