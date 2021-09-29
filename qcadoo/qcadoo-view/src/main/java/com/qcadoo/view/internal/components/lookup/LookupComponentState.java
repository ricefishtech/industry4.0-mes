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
package com.qcadoo.view.internal.components.lookup;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.expression.ExpressionUtils;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchOrders;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchRestrictions.SearchMatchMode;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.view.api.components.LookupComponent;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;
import com.qcadoo.view.internal.CriteriaModifier;
import com.qcadoo.view.internal.FilterValueHolderImpl;
import com.qcadoo.view.internal.components.FieldComponentState;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

public final class LookupComponentState extends FieldComponentState implements LookupComponent {

    public static final String JSON_REQUIRED = "required";

    public static final String JSON_OLD_SELECTED_ENTITY_ID = "oldValue";

    public static final String JSON_TEXT = "selectedEntityValue";

    public static final String JSON_CODE = "selectedEntityCode";

    public static final String JSON_ACTIVE = "selectedEntityActive";

    public static final String JSON_CURRENT_CODE = "currentCode";

    public static final String JSON_CLEAR_CURRENT_CODE = "clearCurrentCodeCode";

    public static final String JSON_BELONGS_TO_ENTITY_ID = "contextEntityId";

    public static final String JSON_AUTOCOMPLETE_MATCHES = "autocompleteMatches";

    public static final String JSON_AUTOCOMPLETE_CODE = "autocompleteCode";

    public static final String JSON_AUTOCOMPLETE_ENTITIES_NUMBER = "autocompleteEntitiesNumber";

    public static final String JSON_CRITERIA_MODIFIER_PARAMETER = "criteriaModifierParameter";

    private static final String CRITERIA_MODIFIER_NOT_PRESENT = "There is no critieria modifier. Filter value is not present.";

    private final LookupEventPerformer eventPerformer = new LookupEventPerformer();

    private final FieldDefinition belongsToFieldDefinition;

    private Long belongsToEntityId;

    private Long oldSelectedEntityId;

    private String currentCode;

    private boolean clearCurrentCodeCode = false;

    private boolean selectedEntityActive = true;

    private boolean onlyActive = false;

    private String selectedEntityCode;

    private String selectedEntityValue;

    private final String fieldCode;

    private final String expression;

    private String autocompleteCode;

    private List<Entity> autocompleteMatches;

    private int autocompleteEntitiesNumber;

    private final CriteriaModifier criteriaModifier;

    private final FilterValueHolder criteriaModifierParameter;
    
    public LookupComponentState(final FieldDefinition scopeField, final String fieldCode, final String expression,
            final LookupComponentPattern pattern) {
        super(pattern);
        this.belongsToFieldDefinition = scopeField;
        this.fieldCode = fieldCode;
        this.expression = expression;
        this.criteriaModifier = pattern.getCriteriaModifier();
        this.criteriaModifierParameter = this.criteriaModifier != null ? new FilterValueHolderImpl() : null;
        this.onlyActive = pattern.isOnlyActive();
        registerEvent("initialize", eventPerformer, "initialize");
        registerEvent("autompleteSearch", eventPerformer, "autompleteSearch");
        registerEvent("onSelectedEntityChange", eventPerformer, "onSelectedEntityChange");
    }

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        super.initializeContent(json);

        if (json.has(JSON_TEXT) && !json.isNull(JSON_TEXT)) {
            selectedEntityValue = json.getString(JSON_TEXT);
        }
        if (json.has(JSON_CODE) && !json.isNull(JSON_CODE)) {
            selectedEntityCode = json.getString(JSON_CODE);
        }
        if (json.has(JSON_BELONGS_TO_ENTITY_ID) && !json.isNull(JSON_BELONGS_TO_ENTITY_ID)) {
            belongsToEntityId = json.getLong(JSON_BELONGS_TO_ENTITY_ID);
        }
        if (json.has(JSON_OLD_SELECTED_ENTITY_ID) && !json.isNull(JSON_OLD_SELECTED_ENTITY_ID)) {
            oldSelectedEntityId = json.getLong(JSON_OLD_SELECTED_ENTITY_ID);
        }

        if (json.has(JSON_CURRENT_CODE) && !json.isNull(JSON_CURRENT_CODE)) {
            currentCode = json.getString(JSON_CURRENT_CODE);
        }

        if (json.has(JSON_AUTOCOMPLETE_CODE) && !json.isNull(JSON_AUTOCOMPLETE_CODE)) {
            autocompleteCode = json.getString(JSON_AUTOCOMPLETE_CODE);
        }

        if (json.has(JSON_CRITERIA_MODIFIER_PARAMETER) && !json.isNull(JSON_CRITERIA_MODIFIER_PARAMETER)) {
            criteriaModifierParameter.initialize(json.getJSONObject(JSON_CRITERIA_MODIFIER_PARAMETER));
        }

        if (belongsToFieldDefinition != null && belongsToEntityId == null) {
            setEnabled(false);
        }
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = super.renderContent();
        json.put(JSON_TEXT, selectedEntityValue);
        json.put(JSON_CODE, selectedEntityCode);
        json.put(JSON_ACTIVE, selectedEntityActive);
        json.put(JSON_BELONGS_TO_ENTITY_ID, belongsToEntityId);

        if (clearCurrentCodeCode) {
            json.put(JSON_CLEAR_CURRENT_CODE, clearCurrentCodeCode);
        }

        if (autocompleteMatches != null) {
            JSONArray matches = new JSONArray();
            for (Entity entity : autocompleteMatches) {
                JSONObject matchEntity = new JSONObject();
                matchEntity.put("id", entity.getId());
                matchEntity.put("value", ExpressionUtils.getValue(entity, expression, getLocale()));
                matchEntity.put("code", String.valueOf(entity.getField(fieldCode)));
                matchEntity.put("active", entity.isActive());
                matches.put(matchEntity);
            }
            json.put(JSON_AUTOCOMPLETE_MATCHES, matches);
            json.put(JSON_AUTOCOMPLETE_CODE, autocompleteCode);
            json.put(JSON_AUTOCOMPLETE_ENTITIES_NUMBER, autocompleteEntitiesNumber);

        }

        if (criteriaModifierParameter != null && !criteriaModifierParameter.isEmpty()) {
            json.put(JSON_CRITERIA_MODIFIER_PARAMETER, criteriaModifierParameter.toJSON());
        }

        return json;
    }

    @Override
    public Long getFieldValue() {
        return getFieldValueWithoutSearching();
    }

    @Override
    public boolean isEmpty() {
        return org.apache.commons.lang3.StringUtils.isEmpty(autocompleteCode) && !hasSelectedEntity();
    }

    @Override
    public String getCurrentCode() {
        return currentCode;
    }

    private boolean hasSelectedEntity() {
        return getFieldValue() != null;
    }

    public Long getFieldValueWithoutSearching() {
        return convertToLong(super.getFieldValue());
    }

    @Override
    public void setFieldValue(final Object value) {
        setFieldValueWithoutRefreshing(convertToLong(value));
        if (!this.isHasError()) {
            clearCurrentCodeCode = true;
        }
        eventPerformer.refresh();
    }

    private void setFieldValueWithoutRefreshing(final Long value) {
        super.setFieldValue(value);
        notifyEntityIdChangeListeners(convertToLong(value));
    }

    private Long convertToLong(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (StringUtils.hasText(value.toString()) && !"null".equals(value.toString())) {
            return Long.parseLong(value.toString());
        } else {
            return null;
        }
    }

    @Override
    public void onScopeEntityIdChange(final Long scopeEntityId) {
        if (belongsToFieldDefinition == null) {
            throw new IllegalStateException("Lookup doesn't have scopeField, it cannot set scopeEntityId");
        }
        belongsToEntityId = scopeEntityId;
        setEnabled(scopeEntityId != null);
        requestRender();
    }

    @Override
    public Entity getEntity() {
        Long entityId = getFieldValueWithoutSearching();
        if (entityId == null) {
            return null;
        }
        return getDataDefinition().get(entityId);
    }

    protected class LookupEventPerformer {

        public void initialize(final String[] args) {
            refresh();
            requestRender();
        }

        public void autompleteSearch(final String[] args) {
            if ((belongsToFieldDefinition == null || belongsToEntityId != null)) {
                SearchCriteriaBuilder searchCriteriaBuilder = getDataDefinition().find();

                if(StringUtils.hasText(currentCode)){
                    searchCriteriaBuilder.add(SearchRestrictions.ilike(fieldCode, currentCode, SearchMatchMode.ANYWHERE));
                }
                
                if (belongsToFieldDefinition != null && belongsToEntityId != null
                        && belongsToFieldDefinition.getType() instanceof BelongsToType) {
                    BelongsToType type = (BelongsToType) belongsToFieldDefinition.getType();
                    searchCriteriaBuilder.add(SearchRestrictions.belongsTo(belongsToFieldDefinition.getName(), type
                            .getDataDefinition().get(belongsToEntityId)));
                }

                if (getDataDefinition().isActivable() && onlyActive) {
                    if (oldSelectedEntityId == null) {
                        searchCriteriaBuilder.add(SearchRestrictions.eq("active", true));
                    } else {
                        searchCriteriaBuilder.add(SearchRestrictions.or(SearchRestrictions.eq("active", true),
                                SearchRestrictions.idEq(oldSelectedEntityId)));
                    }
                }

                searchCriteriaBuilder.addOrder(SearchOrders.asc(fieldCode));

                if (criteriaModifier != null) {
                    criteriaModifier.modifyCriteria(searchCriteriaBuilder, criteriaModifierParameter);
                }

                searchCriteriaBuilder.setMaxResults(25);
                
                SearchResult results = searchCriteriaBuilder.list();

                autocompleteEntitiesNumber = results.getTotalNumberOfEntities();

                if (results.getTotalNumberOfEntities() > 25) {
                    autocompleteMatches = new LinkedList<>();
                    
                } else {
                    autocompleteMatches = results.getEntities();
                }
                
            } else {
                autocompleteMatches = new LinkedList<>();
            }

            if(!StringUtils.hasText(currentCode)){
                setFieldValue("");
            }            
            
            autocompleteCode = currentCode;
            requestRender();
        }

        public void onSelectedEntityChange(final String[] args) {
            notifyEntityIdChangeListeners(getFieldValue());
        }

        private void refresh() {
            Long entityId = getFieldValueWithoutSearching();
            if (entityId == null) {
                selectedEntityCode = "";
                selectedEntityValue = "";
                selectedEntityActive = true;
                return;
            }
            Entity entity = getDataDefinition().get(entityId);
            if (entity == null) {
                setFieldValueWithoutRefreshing(null);
                selectedEntityCode = "";
                selectedEntityValue = "";
                selectedEntityActive = true;
            } else {
                selectedEntityCode = String.valueOf(entity.getField(fieldCode));
                selectedEntityValue = ExpressionUtils.getValue(entity, expression, getLocale());
                selectedEntityActive = entity.isActive();
            }
        }

    }

    @Override
    public FilterValueHolder getFilterValue() {
        if (criteriaModifier == null) {
            throw new IllegalStateException(CRITERIA_MODIFIER_NOT_PRESENT);
        }

        FilterValueHolder holder = new FilterValueHolderImpl(criteriaModifierParameter);
        return holder;
    }

    @Override
    public void setFilterValue(FilterValueHolder value) {
        if (criteriaModifier == null) {
            throw new IllegalStateException(CRITERIA_MODIFIER_NOT_PRESENT);
        }
        criteriaModifierParameter.initialize(value.toJSON());
        requestRender();
    }
}
