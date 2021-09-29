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
package com.qcadoo.model.internal;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.*;
import com.qcadoo.model.constants.VersionableConstants;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.api.InternalFieldDefinition;
import com.qcadoo.model.internal.api.ValidationService;
import com.qcadoo.model.internal.api.ValueAndError;
import com.qcadoo.model.internal.types.PasswordType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Map.Entry;

@Service
public final class ValidationServiceImpl implements ValidationService {

    @Override
    public void validateGenericEntity(final InternalDataDefinition dataDefinition, final Entity genericEntity,
            final Entity existingGenericEntity) {

        validateEntityAgainstVersion(existingGenericEntity, genericEntity, dataDefinition);
        copyReadOnlyAndMissingFields(dataDefinition, genericEntity, existingGenericEntity);
        parseFields(dataDefinition, genericEntity);

        if (genericEntity.getId() == null) {
            dataDefinition.callCreateHook(genericEntity);
            parseAndValidateEntity(dataDefinition, genericEntity, existingGenericEntity);
        } else {
            parseAndValidateEntity(dataDefinition, genericEntity, existingGenericEntity);
            dataDefinition.callUpdateHook(genericEntity);
        }
        dataDefinition.callSaveHook(genericEntity);
    }

    private void copyReadOnlyAndMissingFields(final InternalDataDefinition dataDefinition, final Entity genericEntity,
            final Entity existingGenericEntity) {
        for (Map.Entry<String, FieldDefinition> field : dataDefinition.getFields().entrySet()) {
            Object value = null;
            if (existingGenericEntity != null) {
                value = existingGenericEntity.getField(field.getKey());
            }
            if (field.getValue().getType() instanceof PasswordType) {
                continue;
            }
            if (field.getValue().isReadOnly()) {
                genericEntity.setField(field.getKey(), value);
            }
            if (!genericEntity.getFields().containsKey(field.getKey()) && genericEntity.getId() != null) {
                genericEntity.setField(field.getKey(), value);
            }
        }
    }

    private void parseFields(final InternalDataDefinition dataDefinition, final Entity genericEntity) {
        for (Entry<String, FieldDefinition> fieldDefinitionEntry : dataDefinition.getFields().entrySet()) {
            final InternalFieldDefinition fieldDefinition = (InternalFieldDefinition) fieldDefinitionEntry.getValue();
            final FieldType fieldType = fieldDefinition.getType();
            final Object fieldValue = genericEntity.getField(fieldDefinitionEntry.getKey());
            Object parsedValue = null;

            if (fieldType instanceof BelongsToType) {
                parsedValue = parseBelongsToField(fieldDefinition, trimAndNullIfEmpty(fieldValue), genericEntity);
            } else {
                parsedValue = fieldValue;
            }

            genericEntity.setField(fieldDefinitionEntry.getKey(), parsedValue);
        }
    }

    private Object parseBelongsToField(final InternalFieldDefinition fieldDefinition, final Object value,
            final Entity validatedEntity) {
        Entity referencedEntity = null;

        if (value != null) {
            Long referencedEntityId = null;
            if (value instanceof String) {
                try {
                    referencedEntityId = Long.valueOf((String) value);
                } catch (NumberFormatException e) {
                    validatedEntity.addError(fieldDefinition, "qcadooView.validate.field.error.wrongType", value.getClass()
                            .getSimpleName(), fieldDefinition.getType().getType().getSimpleName());
                }
            } else if (value instanceof Long) {
                referencedEntityId = (Long) value;
            } else if (value instanceof Integer) {
                referencedEntityId = Long.valueOf((Integer) value);
            } else if (value instanceof Entity) {
                referencedEntityId = ((Entity) value).getId();
            } else {
                validatedEntity.addError(fieldDefinition, "qcadooView.validate.field.error.wrongType", value.getClass()
                        .getSimpleName(), fieldDefinition.getType().getType().getSimpleName());
            }

            if (referencedEntityId != null) {
                BelongsToType belongsToFieldType = (BelongsToType) fieldDefinition.getType();
                referencedEntity = belongsToFieldType.getDataDefinition().get(referencedEntityId);
            }
        }
        return referencedEntity;
    }

    private void parseAndValidateEntity(final InternalDataDefinition dataDefinition, final Entity genericEntity,
            final Entity existingGenericEntity) {
        for (Entry<String, FieldDefinition> fieldDefinitionEntry : dataDefinition.getFields().entrySet()) {
            final String fieldName = fieldDefinitionEntry.getKey();
            final Object newValue = genericEntity.getField(fieldName);
            final Object oldValue = getOldFieldValue(existingGenericEntity, fieldName);
            final InternalFieldDefinition fieldDefinition = (InternalFieldDefinition) fieldDefinitionEntry.getValue();

            final Object validatedFieldValue = parseAndValidateField(fieldDefinition, oldValue, newValue, genericEntity);
            genericEntity.setField(fieldName, validatedFieldValue);
        }

        if (genericEntity.isValid()) {
            dataDefinition.callValidators(genericEntity);
        }
    }

    private Object getOldFieldValue(final Entity existingEntityOrNull, final String fieldName) {
        if (existingEntityOrNull == null) {
            return null;
        } else {
            return existingEntityOrNull.getField(fieldName);
        }
    }

    private Object parseFieldValue(final InternalFieldDefinition fieldDefinition, final Object value, final Entity validatedEntity) {
        ValueAndError valueAndError = ValueAndError.empty();
        if (value != null) {
            valueAndError = fieldDefinition.getType().toObject(fieldDefinition, value);
            if (!valueAndError.isValid()) {
                validatedEntity.addError(fieldDefinition, valueAndError.getMessage(), valueAndError.getArgs());
                return null;
            }
        }
        return valueAndError.getValue();
    }

    private Object parseAndValidateField(final InternalFieldDefinition fieldDefinition, final Object oldValue,
            final Object newValue, final Entity validatedEntity) {
        FieldType fieldType = fieldDefinition.getType();
        Object parsedValue;
        if (fieldType instanceof HasManyType || fieldType instanceof TreeType || fieldType instanceof ManyToManyType) {
            parsedValue = newValue;
        } else {
            parsedValue = parseFieldValue(fieldDefinition, trimAndNullIfEmpty(newValue), validatedEntity);
        }

        if (validatedEntity.isFieldValid(fieldDefinition.getName())
                && fieldDefinition.callValidators(validatedEntity, oldValue, parsedValue)) {
            return parsedValue;
        } else {
            return null;
        }
    }

    private Object trimAndNullIfEmpty(final Object value) {
        if (value instanceof String && !StringUtils.hasText((String) value)) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).trim();
        }
        return value;
    }

    private void validateEntityAgainstVersion(final Entity databaseEntity, final Entity entity, final DataDefinition dataDefinition) {
        if(databaseEntity != null && dataDefinition.isVersionable()){
            Long savedVersion = (Long) entity.getField(VersionableConstants.VERSION_FIELD_NAME);
            Long currentDbVersion = (Long)databaseEntity.getField(VersionableConstants.VERSION_FIELD_NAME);

            if(savedVersion.compareTo(currentDbVersion) != 0){
                entity.addGlobalError("qcadooView.validate.global.optimisticLock",false, true);
            }
        }
    }
}
