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

import com.google.common.collect.Lists;
import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.model.api.*;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.api.validators.ErrorMessage;
import com.qcadoo.model.internal.api.EntityAwareCopyPerformers;
import com.qcadoo.model.internal.api.EntityAwareEqualsPerformers;
import com.qcadoo.model.internal.api.PerformerEntitiesChain;
import com.qcadoo.model.internal.api.ValueAndError;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import com.qcadoo.model.api.validators.GlobalMessage;

public final class DefaultEntity implements Entity, EntityAwareCopyPerformers, EntityAwareEqualsPerformers {

    private Long id;

    private final DataDefinition dataDefinition;

    private final Map<String, Object> fields;

    private final EntityMessagesHolder messagesHolder = new EntityMessagesHolderImpl();

    private boolean notValidFlag = false;

    private boolean active = true;

    public DefaultEntity(final DataDefinition dataDefinition, final Long id, final Map<String, Object> fields) {
        this.dataDefinition = dataDefinition;
        this.id = id;
        this.fields = fields;
    }

    public DefaultEntity(final DataDefinition dataDefinition, final Long id) {
        this(dataDefinition, id, new HashMap<>());
    }

    public DefaultEntity(final DataDefinition dataDefinition) {
        this(dataDefinition, null, new HashMap<>());
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public void setField(final String fieldName, final Object fieldValue) {
        fields.put(fieldName, fieldValue);
    }

    @Override
    public Map<String, Object> getFields() {
        return fields;
    }

    @Override
    public void addGlobalError(final String message, final String... vars) {
        messagesHolder.addGlobalError(message, vars);
    }

    @Override
    public void addGlobalMessage(final String message, final String... vars) {
        messagesHolder.addGlobalMessage(message, vars);
    }

    @Override
    public void addGlobalMessage(String message, boolean autoClose, final boolean extraLarge, String... vars) {
        messagesHolder.addGlobalMessage(message, autoClose, extraLarge, vars);
    }

    @Override
    public void addGlobalError(String message, boolean autoClose, String... vars) {
        messagesHolder.addGlobalError(message, autoClose, vars);
    }

    @Override
    public void addGlobalError(String message, boolean autoClose, final boolean extraLarge, String... vars) {
        messagesHolder.addGlobalError(message, autoClose, extraLarge, vars);
    }

    @Override
    public void addError(final FieldDefinition fieldDefinition, final String message, final String... vars) {
        messagesHolder.addError(fieldDefinition, message, vars);
    }

    @Override
    public List<ErrorMessage> getGlobalErrors() {
        return messagesHolder.getGlobalErrors();
    }

    @Override
    public List<GlobalMessage> getGlobalMessages() {
        return messagesHolder.getGlobalMessages();
    }

    @Override
    public Map<String, ErrorMessage> getErrors() {
        return messagesHolder.getErrors();
    }

    @Override
    public ErrorMessage getError(final String fieldName) {
        return messagesHolder.getError(fieldName);
    }

    @Override
    public boolean isValid() {
        return !notValidFlag && getErrors().isEmpty() && getGlobalErrors().isEmpty();
    }

    @Override
    public boolean isFieldValid(final String fieldName) {
        return messagesHolder.getError(fieldName) == null;
    }

    @Override
    public void setNotValid() {
        notValidFlag = true;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(23, 41).append(id).append(dataDefinition);

        for (Map.Entry<String, Object> field : fields.entrySet()) {
            if (field.getValue() instanceof Collection) {
                continue;
            }
            if (field.getValue() instanceof Entity) {
                Entity entity = (Entity) field.getValue();
                hcb.append(field.getKey()).append(entity.getDataDefinition().getPluginIdentifier())
                        .append(entity.getDataDefinition().getName()).append(entity.getId());
            } else {
                hcb.append(field.getKey()).append(field.getValue());
            }
        }

        return hcb.toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) obj;
        return equals(other, new PerformerEntitiesChainImpl(this));
    }

    @Override
    public boolean flatEquals(final Entity otherEntity) {
        return otherEntity != null && definitionsAndIdsAreEqual(otherEntity) && fieldsAreEquals(otherEntity, null, true);
    }

    @Override
    public boolean equals(final Entity otherEntity, final PerformerEntitiesChain performersChain) {
        return otherEntity != null
                && (otherEntity == performersChain.getLast() || definitionsAndIdsAreEqual(otherEntity)
                        && fieldsAreEquals(otherEntity, performersChain, false));
    }

    private boolean definitionsAndIdsAreEqual(final Entity otherEntity) {
        return new EqualsBuilder().append(id, otherEntity.getId()).append(dataDefinition, otherEntity.getDataDefinition())
                .isEquals();
    }

    private boolean fieldsAreEquals(final Entity otherEntity, final PerformerEntitiesChain performersChain, final boolean flat) {
        for (String fieldName : dataDefinition.getFields().keySet()) {
            final Object fieldValue = fields.get(fieldName);
            final Object otherFieldValue = otherEntity.getField(fieldName);
            if (fieldValue == null) {
                if (otherFieldValue != null) {
                    return false;
                }
            } else if (fieldValue instanceof Collection) {
                continue;
            } else if (fieldValue instanceof Entity) {
                if (!flat && !belongsToAreEquals((Entity) fieldValue, (Entity) otherFieldValue, performersChain)) {
                    return false;
                }
            } else if (!fieldValue.equals(otherFieldValue)) {
                return false;
            }
        }
        return true;
    }

    private boolean belongsToAreEquals(final Entity fieldValue, final Entity otherFieldValue,
            final PerformerEntitiesChain performersChain) {
        boolean btResult;
        if (fieldValue instanceof EntityAwareEqualsPerformers) {
            final EntityAwareEqualsPerformers fieldEntityValue = (EntityAwareEqualsPerformers) fieldValue;
            if (performersChain.find(fieldValue) == null) {
                performersChain.append(this);
                btResult = fieldEntityValue.equals(otherFieldValue, performersChain);
            } else {
                btResult = fieldEntityValue.flatEquals(otherFieldValue);
            }
        } else {
            btResult = fieldValue.equals(otherFieldValue);
        }
        return btResult;
    }

    @Override
    public DefaultEntity copy() {
        return copy(new PerformerEntitiesChainImpl(this));
    }

    @Override
    public DefaultEntity copy(final PerformerEntitiesChain performersChain) {
        DefaultEntity entity = new DefaultEntity(dataDefinition, id);
        for (Map.Entry<String, Object> field : fields.entrySet()) {
            Object fieldValueCopy = null;
            if (field.getValue() instanceof Entity) {
                fieldValueCopy = copyFieldEntityValue(performersChain, (Entity) field.getValue());
            } else {
                fieldValueCopy = field.getValue();
            }
            entity.setField(field.getKey(), fieldValueCopy);
        }
        return entity;
    }

    private Entity copyFieldEntityValue(final PerformerEntitiesChain performersChain, final Entity fieldEntity) {
        Entity fieldEntityCopy = null;
        final Entity existingPerformer = performersChain.find(fieldEntity);
        if (existingPerformer != null) {
            fieldEntityCopy = existingPerformer;
        } else if (fieldEntity instanceof EntityAwareCopyPerformers) {
            performersChain.append(this);
            fieldEntityCopy = ((EntityAwareCopyPerformers) fieldEntity).copy(performersChain);
        } else {
            fieldEntityCopy = fieldEntity.copy();
        }
        return fieldEntityCopy;
    }

    @Override
    public Object getField(final String fieldName) {
        return fields.get(fieldName);
    }

    @Override
    public String getStringField(final String fieldName) {
        return (String) getField(fieldName);
    }

    @Override
    public boolean getBooleanField(final String fieldName) {
        Object fieldValue = getField(fieldName);
        if (fieldValue instanceof Boolean) {
            return ((Boolean) fieldValue).booleanValue();
        }
        if (fieldValue instanceof String) {
            return "1".equals(fieldValue) || Boolean.parseBoolean((String) fieldValue);
        }
        return false;
    }

    @Override
    public BigDecimal getDecimalField(final String fieldName) {
        final Object fieldValue = getField(fieldName);
        if (fieldValue == null) {
            return null;
        }
        if (fieldValue instanceof BigDecimal) {
            return (BigDecimal) fieldValue;
        }
        final FieldDefinition fieldDefinition = dataDefinition.getField(fieldName);
        if (fieldValue instanceof String && BigDecimal.class.equals(fieldDefinition.getType().getType())) {
            if (StringUtils.isBlank((String) fieldValue)) {
                return null;
            }
            final ValueAndError valueAndError = fieldDefinition.getType().toObject(fieldDefinition, fieldValue);
            if (valueAndError.isValid()) {
                return (BigDecimal) valueAndError.getValue();
            }
        }
        throw new IllegalArgumentException("Field " + fieldName + " in " + dataDefinition.getPluginIdentifier() + '.'
                + dataDefinition.getName() + " does not contain correct BigDecimal value (current field value: " + fieldValue
                + ")");
    }

    @Override
    public Integer getIntegerField(final String fieldName) {
        final Object fieldValue = getField(fieldName);
        if (fieldValue == null) {
            return null;
        }
        if (fieldValue instanceof Integer) {
            return (Integer) fieldValue;
        }
        final FieldDefinition fieldDefinition = dataDefinition.getField(fieldName);
        if (fieldValue instanceof String && Integer.class.equals(fieldDefinition.getType().getType())) {
            if (StringUtils.isBlank((String) fieldValue)) {
                return null;
            }
            final ValueAndError valueAndError = fieldDefinition.getType().toObject(fieldDefinition, fieldValue);
            if (valueAndError.isValid()) {
                return (Integer) valueAndError.getValue();
            }
        }
        throw new IllegalArgumentException("Field " + fieldName + " in " + dataDefinition.getPluginIdentifier() + '.'
                + dataDefinition.getName() + " does not contain correct Integer value (current field value: " + fieldValue + ")");
    }

    @Override
    public Long getLongField(final String fieldName) {
        final Object fieldValue = getField(fieldName);
        if (fieldValue == null) {
            return null;
        }
        if (fieldValue instanceof Long) {
            return (Long)fieldValue;
        }
        final FieldDefinition fieldDefinition = dataDefinition.getField(fieldName);
        if (fieldValue instanceof String && Long.class.equals(fieldDefinition.getType().getType())) {
            if (StringUtils.isBlank((String) fieldValue)) {
                return null;
            }
            final ValueAndError valueAndError = fieldDefinition.getType().toObject(fieldDefinition, fieldValue);
            if (valueAndError.isValid()) {
                return (Long) valueAndError.getValue();
            }
        }
        throw new IllegalArgumentException("Field " + fieldName + " in " + dataDefinition.getPluginIdentifier() + '.'
                + dataDefinition.getName() + " does not contain correct Long value (current field value: " + fieldValue + ")");
    }

    @SuppressWarnings("unchecked")
    @Override
    public EntityList getHasManyField(final String fieldName) {
        Object fieldValue = getField(fieldName);
        if (fieldValue == null) {
            return new DetachedEntityListImpl(dataDefinition, null);
        }
        if (fieldValue instanceof EntityList) {
            return (EntityList) fieldValue;
        }
        if (fieldValue instanceof List<?>) {
            return new DetachedEntityListImpl(dataDefinition, (List<Entity>) fieldValue);
        }
        throw new IllegalArgumentException("Field " + fieldName + " in " + dataDefinition.getPluginIdentifier() + '.'
                + dataDefinition.getName() + " does not contain value of type List<Entity> or EntityList");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Entity> getManyToManyField(final String fieldName) {
        if (getField(fieldName) == null) {
            return Lists.newArrayList();
        }
        return (List<Entity>) getField(fieldName);
    }

    @Override
    public EntityTree getTreeField(final String fieldName) {
        return (EntityTree) getField(fieldName);
    }

    @Override
    public Entity getBelongsToField(final String fieldName) {
        if (getField(fieldName) == null) {
            return null;
        }

        checkArgument(dataDefinition.getField(fieldName).getType() instanceof BelongsToType, "Field should be belongsTo type");
        if (getField(fieldName) instanceof Number) {
            return getProxyForBelongsToField(fieldName);
        }
        return (Entity) getField(fieldName);
    }

    private Entity getProxyForBelongsToField(final String fieldName) {
        BelongsToType belongsToType = (BelongsToType) dataDefinition.getField(fieldName).getType();
        Long belongsToEntityId = ((Number) getField(fieldName)).longValue();
        return new ProxyEntity(belongsToType.getDataDefinition(), belongsToEntityId);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinition;
    }

    @Override
    public String toString() {
        StringBuilder entity = new StringBuilder("Entity[" + dataDefinition + "][id=" + id + ",active=" + active);
        for (Map.Entry<String, Object> field : fields.entrySet()) {

            entity.append(",").append(field.getKey()).append("=");
            if (field.getValue() instanceof Collection) {
                entity.append("#collection");
                continue;
            }

            if (field.getValue() instanceof Entity) {
                Entity belongsToEntity = (Entity) field.getValue();
                entity.append("Entity[" + belongsToEntity.getDataDefinition() + "][id=" + belongsToEntity.getId() + "]");
            } else {
                entity.append(field.getValue());
            }
        }
        return entity.append("]").toString();
    }

    @Override
    public Date getDateField(final String fieldName) {
        final Object fieldValue = getField(fieldName);
        if (fieldValue == null) {
            return null;
        }
        if (fieldValue instanceof Long) {
            return new Date((Long) fieldValue);
        }
        if (fieldValue instanceof Date) {
            Date fieldDateValue = (Date) fieldValue;
            return new Date(fieldDateValue.getTime());
        }
        if (fieldValue instanceof String) {
            return DateUtils.parseDate(fieldValue);
        }
        throw new IllegalArgumentException("Field " + fieldName + " in " + dataDefinition.getPluginIdentifier() + '.'
                + dataDefinition.getName() + " does not contain correct Date value");
    }

}
