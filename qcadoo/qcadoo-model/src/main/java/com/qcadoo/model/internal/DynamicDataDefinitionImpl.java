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

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityOpResult;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchQueryBuilder;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.internal.api.EntityHookDefinition;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.search.SearchCriteria;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DynamicDataDefinitionImpl implements InternalDataDefinition {

    private static final String L_CANNOT_FIND_ENTITY_FOR_DYNAMIC_DATA_DEFINITION = "Cannot find entity for dynamic data definition";

    private final String name;

    private final Map<String, FieldDefinition> fields = new LinkedHashMap<String, FieldDefinition>();

    public DynamicDataDefinitionImpl() {
        name = "dynamic" + Long.valueOf(System.nanoTime());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPluginIdentifier() {
        return "qcadooModel";
    }

    @Override
    public Entity get(final Long id) {
        throw new UnsupportedOperationException("Cannot get entity for dynamic data definition");
    }

    @Override
    public List<Entity> copy(final Long... id) {
        throw new UnsupportedOperationException("Cannot copy entity for dynamic data definition");
    }

    @Override
    public List<Entity> activate(final Long... id) {
        throw new UnsupportedOperationException("Cannot activate entity for dynamic data definition");
    }

    @Override
    public List<Entity> deactivate(final Long... id) {
        throw new UnsupportedOperationException("Cannot deactivate entity for dynamic data definition");
    }

    @Override
    public EntityOpResult delete(final Long... id) {
        throw new UnsupportedOperationException("Cannot delete entity for dynamic data definition");

    }

    @Override
    public Entity save(final Entity entity) {
        throw new UnsupportedOperationException("Cannot save entity for dynamic data definition");
    }

    @Override public Entity validate(Entity entity) {
        throw new UnsupportedOperationException("Cannot validate entity for dynamic data definition");
    }

    @Override
    public Entity fastSave(final Entity entity) {
        throw new UnsupportedOperationException("Cannot save entity for dynamic data definition");
    }

    @Override
    public SearchCriteriaBuilder find() {
        throw new UnsupportedOperationException(L_CANNOT_FIND_ENTITY_FOR_DYNAMIC_DATA_DEFINITION);
    }

    @Override
    public long count(){
        throw new UnsupportedOperationException("Cannot count all entities for dynamic data definition");
    }

    @Override
    public long count(final SearchCriterion criterion){
        throw new UnsupportedOperationException("Cannot count entities for dynamic data definition");
    }

    @Override
    public SearchCriteriaBuilder findWithAlias(final String alias) {
        throw new UnsupportedOperationException(L_CANNOT_FIND_ENTITY_FOR_DYNAMIC_DATA_DEFINITION);
    }

    @Override
    public SearchQueryBuilder find(final String queryString) {
        throw new UnsupportedOperationException(L_CANNOT_FIND_ENTITY_FOR_DYNAMIC_DATA_DEFINITION);
    }

    @Override
    public void move(final Long id, final int offset) {
        throw new UnsupportedOperationException("Cannot move entity for dynamic data definition");
    }

    @Override
    public void moveTo(final Long id, final int position) {
        throw new UnsupportedOperationException("Cannot move entity for dynamic data definition");
    }

    @Override
    public Map<String, FieldDefinition> getFields() {
        return fields;
    }

    public void addField(final String fieldName, final FieldType type) {
        FieldDefinitionImpl fieldDefinition = new FieldDefinitionImpl(this, fieldName);
        fieldDefinition.enable();
        fieldDefinition.setPersistent(false);
        fieldDefinition.withReadOnly(true);
        fieldDefinition.withType(type);
        fields.put(fieldName, fieldDefinition);
    }

    @Override
    public FieldDefinition getField(final String fieldName) {
        return fields.get(fieldName);
    }

    @Override
    public FieldDefinition getPriorityField() {
        return null;
    }

    @Override
    public boolean isPrioritizable() {
        return false;
    }

    @Override
    public Entity create(final Long id) {
        throw new UnsupportedOperationException("Cannot create entity with id for dynamic data definition");
    }

    @Override
    public Entity create() {
        return new DefaultEntity(this);
    }

    @Override
    public SearchResult find(final SearchCriteria searchCriteria) {
        throw new UnsupportedOperationException(L_CANNOT_FIND_ENTITY_FOR_DYNAMIC_DATA_DEFINITION);
    }

    @Override
    public String getFullyQualifiedClassName() {
        throw new UnsupportedOperationException("Cannot get class for dynamic data definition");
    }

    @Override
    public boolean callViewHook(final Entity entity) {
        return true;
    }

    @Override
    public boolean callCreateHook(final Entity entity) {
        return true;
    }

    @Override
    public boolean callUpdateHook(final Entity entity) {
        return true;
    }

    @Override
    public boolean callSaveHook(final Entity entity) {
        return true;
    }

    @Override
    public boolean callDeleteHook(final Entity entity) {
        return true;
    }

    @Override
    public Class<?> getClassForEntity() {
        throw new UnsupportedOperationException("Cannot get class for dynamic data definition");
    }

    @Override
    public Object getInstanceForEntity() {
        throw new UnsupportedOperationException("Cannot get instance for dynamic data definition");
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public boolean isInstertable() {
        return false;
    }

    @Override
    public boolean isUpdatable() {
        return false;
    }

    @Override
    public boolean isAuditable() {
        return false;
    }

    @Override
    public boolean isVersionable() {
        return false;
    }

    @Override
    public MasterModel getMasterModel() {
        return null;
    }

    @Override
    public void setMasterModel(MasterModel masterModel) {
    }

    @Override
    public boolean callCopyHook(final Entity targetEntity) {
        return true;
    }

    @Override
    public boolean callValidators(final Entity targetEntity) {
        return true;
    }

    @Override
    public EntityHookDefinition getHook(final String type, final String className, final String methodName) {
        throw new UnsupportedOperationException("Cannot get hook for dynamic data definition");
    }

    @Override
    public String getIdentifierExpression() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void enable() {
        // empty
    }

    @Override
    public void disable() {
        // empty
    }

    @Override
    public boolean isActivable() {
        return false;
    }

    @Override
    public Entity getMasterModelEntity(Long id) {
        return null;
    }

    @Override
    public Entity tryGetMasterModelEntity(Long id) {
        return null;
    }

}
