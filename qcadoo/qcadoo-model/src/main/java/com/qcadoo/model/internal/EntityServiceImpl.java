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
import com.google.common.collect.Sets;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.ExpressionService;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.api.types.HasManyType;
import com.qcadoo.model.api.types.ManyToManyType;
import com.qcadoo.model.api.types.TreeType;
import com.qcadoo.model.constants.VersionableConstants;
import com.qcadoo.model.internal.api.EntityService;
import com.qcadoo.model.internal.api.HibernateService;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.api.InternalFieldDefinition;
import com.qcadoo.model.internal.types.PasswordType;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

@Service
public final class EntityServiceImpl implements EntityService {

    @Autowired
    private HibernateService hibernateService;

    @Autowired
    private ExpressionService expressionService;

    @Override
    public Long getId(final Object databaseEntity) {
        return (Long) getField(databaseEntity, FIELD_ID);
    }

    public Boolean getActive(final Object databaseEntity) {
        return (Boolean) getField(databaseEntity, FIELD_ACTIVE);
    }

    public void setActive(final Object databaseEntity, final boolean active) {
        setField(databaseEntity, FIELD_ACTIVE, active);
    }

    @Override
    public void setId(final Object databaseEntity, final Long id) {
        setField(databaseEntity, FIELD_ID, id);
    }

    @Override
    public void setField(final Object databaseEntity, final FieldDefinition fieldDefinition, final Object value) {
        if (!shouldAllowToSetField(fieldDefinition, value)) {
            return;
        }

        if (fieldDefinition.getType() instanceof BelongsToType && value != null) {
            Object belongsToValue = getBelongsToFieldValue(
                    (InternalDataDefinition) ((BelongsToType) fieldDefinition.getType()).getDataDefinition(), value);
            setField(databaseEntity, fieldDefinition.getName(), belongsToValue);
        } else if (fieldDefinition.getType() instanceof HasManyType) {
            setField(databaseEntity, fieldDefinition.getName(), null);
        } else if (fieldDefinition.getType() instanceof TreeType) {
            setField(databaseEntity, fieldDefinition.getName(), null);
        } else {
            setField(databaseEntity, fieldDefinition.getName(), value);
        }
    }

    private boolean shouldAllowToSetField(final FieldDefinition fieldDefinition, final Object value) {
        return ((InternalFieldDefinition) fieldDefinition).isEnabled()
                && !(fieldDefinition.getType() instanceof PasswordType && value == null);
    }

    private Object getBelongsToFieldValue(final InternalDataDefinition dataDefinition, final Object value) {
        Long id = null;

        if (value instanceof Long) {
            id = (Long) value;
        } else if (value instanceof Entity) {
            id = ((Entity) value).getId();
        } else {
            id = Long.parseLong(value.toString());
        }

        Class<?> referencedClass = dataDefinition.getClassForEntity();
        return hibernateService.getCurrentSession().load(referencedClass, id);
    }

    @Override
    public Object getField(final Object databaseEntity, final FieldDefinition fieldDefinition) {
        return getField(databaseEntity, fieldDefinition, null);
    }

    public Object getField(final Object databaseEntity, final FieldDefinition fieldDefinition, final Entity performer) {
        if (!((InternalFieldDefinition) fieldDefinition).isEnabled()) {
            return null;
        }
        if (fieldDefinition.getType() instanceof BelongsToType) {
            return getBelongsToField(databaseEntity, fieldDefinition, performer);
        }
        if (fieldDefinition.getType() instanceof HasManyType) {
            return getHasManyField(databaseEntity, fieldDefinition);
        }
        if (fieldDefinition.getType() instanceof ManyToManyType) {
            return getManyToManyField(databaseEntity, fieldDefinition, performer);
        }
        if (fieldDefinition.getType() instanceof TreeType) {
            return getTreeField(databaseEntity, fieldDefinition);
        }

        return getPrimitiveField(databaseEntity, fieldDefinition);
    }

    @Override
    public Entity convertToGenericEntity(final InternalDataDefinition dataDefinition, final Object databaseEntity) {
        return convertToGenericEntity(dataDefinition, databaseEntity, null);
    }

    public Entity convertToGenericEntity(final InternalDataDefinition dataDefinition, final Object databaseEntity,
            final Entity performer) {
        Entity genericEntity = null;

        if (databaseEntity instanceof Object[]) {
            genericEntity = dataDefinition.create();
            Object[] databaseArray = (Object[]) databaseEntity;

            List<String> fields = new ArrayList<String>(dataDefinition.getFields().keySet());

            for (int i = 0; i < fields.size(); i++) {
                if (dataDefinition.getField(fields.get(i)).getType() instanceof BelongsToType) {
                    InternalDataDefinition referencedDataDefinition = (InternalDataDefinition) ((BelongsToType) dataDefinition
                            .getField(fields.get(i)).getType()).getDataDefinition();
                    genericEntity.setField(fields.get(i), convertToGenericEntity(referencedDataDefinition, databaseArray[i]));
                } else {
                    genericEntity.setField(fields.get(i), databaseArray[i]);
                }
            }
        } else if (databaseEntity.getClass().getName().startsWith("com.qcadoo.model.beans")) {
            genericEntity = dataDefinition.create(getId(databaseEntity));

            if (dataDefinition.isActivable()) {
                genericEntity.setActive(getActive(databaseEntity));
            }

            for (Entry<String, FieldDefinition> fieldDefinitionEntry : dataDefinition.getFields().entrySet()) {
                if (fieldDefinitionEntry.getValue().isPersistent()
                        && ((InternalFieldDefinition) fieldDefinitionEntry.getValue()).isEnabled()) {
                    Entity currentPerformer = performer;
                    if (currentPerformer == null) {
                        currentPerformer = genericEntity;
                    }
                    genericEntity.setField(fieldDefinitionEntry.getKey(),
                            getField(databaseEntity, fieldDefinitionEntry.getValue(), currentPerformer));
                }
            }

            if (dataDefinition.isPrioritizable()) {
                genericEntity.setField(dataDefinition.getPriorityField().getName(),
                        getField(databaseEntity, dataDefinition.getPriorityField()));
            }

            for (Entry<String, FieldDefinition> fieldDefinitionEntry : dataDefinition.getFields().entrySet()) {
                if (fieldDefinitionEntry.getValue().getExpression() != null
                        && ((InternalFieldDefinition) fieldDefinitionEntry.getValue()).isEnabled()) {
                    genericEntity.setField(fieldDefinitionEntry.getKey(), expressionService.getValue(genericEntity,
                            fieldDefinitionEntry.getValue().getExpression(), Locale.ENGLISH));
                }
            }

            dataDefinition.callViewHook(genericEntity);
        } else {
            genericEntity = new DefaultEntity(dataDefinition);
            genericEntity.setField(dataDefinition.getFields().keySet().iterator().next(), databaseEntity);
        }

        return genericEntity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertToDatabaseEntity(final InternalDataDefinition dataDefinition, final Entity genericEntity,
            final Object existingDatabaseEntity) {
        Object databaseEntity = getDatabaseEntity(dataDefinition, genericEntity, existingDatabaseEntity);

        for (Entry<String, FieldDefinition> fieldDefinitionEntry : dataDefinition.getFields().entrySet()) {
            FieldDefinition fieldDefinition = fieldDefinitionEntry.getValue();

            if (fieldDefinition.isPersistent() && ((InternalFieldDefinition) fieldDefinition).isEnabled()) {
                Object fieldValue = genericEntity.getField(fieldDefinitionEntry.getKey());

                if (fieldDefinition.getType() instanceof ManyToManyType && fieldValue instanceof Iterable) {
                    Set<Object> innerDatabaseEntities = Sets.newHashSet();
                    for (Entity innerGenericEntity : (Iterable<Entity>) fieldValue) {
                        innerDatabaseEntities.add(getDatabaseEntity(
                                (InternalDataDefinition) innerGenericEntity.getDataDefinition(), innerGenericEntity, null));
                    }
                    setField(databaseEntity, fieldDefinitionEntry.getValue(), innerDatabaseEntities);
                } else {
                    setField(databaseEntity, fieldDefinitionEntry.getValue(), fieldValue);
                }
            }
        }

        if (dataDefinition.isPrioritizable() && genericEntity.getField(dataDefinition.getPriorityField().getName()) != null) {
            setField(databaseEntity, dataDefinition.getPriorityField(),
                    genericEntity.getField(dataDefinition.getPriorityField().getName()));
        }

        if (dataDefinition.isActivable()) {
            setActive(databaseEntity, genericEntity.isActive());
        }

        return databaseEntity;
    }

    private Object getDatabaseEntity(final InternalDataDefinition dataDefinition, final Entity genericEntity,
            final Object existingDatabaseEntity) {
        Object databaseEntity = null;

        if (existingDatabaseEntity == null) {
            databaseEntity = dataDefinition.getInstanceForEntity();
            setId(databaseEntity, genericEntity.getId());

            if(dataDefinition.isVersionable()) {
                setField(databaseEntity, VersionableConstants.VERSION_FIELD_NAME, genericEntity.getLongField(VersionableConstants.VERSION_FIELD_NAME));
            }
        } else {
            databaseEntity = existingDatabaseEntity;
        }
        return databaseEntity;
    }

    private Object getPrimitiveField(final Object databaseEntity, final FieldDefinition fieldDefinition) {
        return getField(databaseEntity, fieldDefinition.getName());
    }

    private Object getHasManyField(final Object databaseEntity, final FieldDefinition fieldDefinition) {
        Long parentId = getId(databaseEntity);
        HasManyType hasManyFieldType = (HasManyType) fieldDefinition.getType();
        InternalDataDefinition referencedDataDefinition = (InternalDataDefinition) hasManyFieldType.getDataDefinition();

        return new EntityListImpl(referencedDataDefinition, hasManyFieldType.getJoinFieldName(), parentId);
    }

    private Object getManyToManyField(final Object databaseEntity, final FieldDefinition fieldDefinition, final Entity performer) {
        ManyToManyType manyToManyType = (ManyToManyType) fieldDefinition.getType();
        InternalDataDefinition referencedDataDefinition = (InternalDataDefinition) manyToManyType.getDataDefinition();

        if(manyToManyType.isLazyLoading()){
            return new ProxyList(fieldDefinition, getId(databaseEntity), performer);

        } else {
            @SuppressWarnings("unchecked")
            Set<Object> databaseEntities = (Set<Object>) getPrimitiveField(databaseEntity, fieldDefinition);
            if (databaseEntities == null) {
                return null;
            }
            List<Entity> genericEntities = Lists.newArrayList();

            @SuppressWarnings("unchecked")
            final Iterable<Object> fieldValues = (Iterable<Object>) getField(databaseEntity, fieldDefinition.getName());
            for (Object innerDatabaseEntity : fieldValues) {
                Entity innerEntity = null;
                Long id = getId(innerDatabaseEntity);
                if (id == null) {
                    innerEntity = convertToGenericEntity(referencedDataDefinition, innerDatabaseEntity);
                } else {
                    innerEntity = new ProxyEntity(referencedDataDefinition, id);
                }
                genericEntities.add(innerEntity);
            }
            return genericEntities;
        }
    }

    private Object getTreeField(final Object databaseEntity, final FieldDefinition fieldDefinition) {
        Long parentId = getId(databaseEntity);
        TreeType treeFieldType = (TreeType) fieldDefinition.getType();
        InternalDataDefinition referencedDataDefinition = (InternalDataDefinition) treeFieldType.getDataDefinition();

        return new EntityTreeImpl(referencedDataDefinition, treeFieldType.getJoinFieldName(), parentId);
    }

    private Object getBelongsToField(final Object databaseEntity, final FieldDefinition fieldDefinition, final Entity performer) {
        BelongsToType belongsToFieldType = (BelongsToType) fieldDefinition.getType();
        InternalDataDefinition referencedDataDefinition = (InternalDataDefinition) belongsToFieldType.getDataDefinition();

        Object value = getField(databaseEntity, fieldDefinition.getName());

        if (value == null) {
            return null;
        }

        if (performer != null && referencedDataDefinition.equals(performer.getDataDefinition()) && performer.getId() != null
                && performer.getId().equals(getId(value))) {
            return performer;
        }

        if (belongsToFieldType.isLazyLoading()) {
            Long id = null;

            if (value instanceof HibernateProxy) {
                id = (Long) ((HibernateProxy) value).getHibernateLazyInitializer().getIdentifier();
            } else {
                id = getId(getField(databaseEntity, fieldDefinition.getName()));
            }

            if (id == null) {
                return null;
            }

            return new ProxyEntity(referencedDataDefinition, id);
        } else {
            Entity currentPerformer = performer;
            if (performer == null || performer.getId() == null && referencedDataDefinition.equals(performer.getDataDefinition())) {
                currentPerformer = new ProxyEntity(referencedDataDefinition, getId(value));
            }
            return convertToGenericEntity(referencedDataDefinition, value, currentPerformer);
        }
    }

    private void setField(final Object databaseEntity, final String fieldName, final Object value) {
        try {
            PropertyUtils.setProperty(databaseEntity, fieldName, value);
        } catch (Exception e) {
            throw new IllegalStateException("cannot set value of the property: " + databaseEntity.getClass().getSimpleName()
                    + ", " + fieldName, e);
        }
    }

    private Object getField(final Object databaseEntity, final String fieldName) {
        try {
            return PropertyUtils.getProperty(databaseEntity, fieldName);
        } catch (Exception e) {
            throw new IllegalStateException("cannot get value of the property: " + databaseEntity.getClass().getSimpleName()
                    + ", " + fieldName, e);
        }
    }

}
