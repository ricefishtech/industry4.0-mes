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
package com.qcadoo.view.internal.components.grid;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityOpResult;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.CustomRestriction;
import com.qcadoo.model.api.search.JoinType;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchOrders;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.api.types.JoinFieldHolder;
import com.qcadoo.model.api.types.ManyToManyType;
import com.qcadoo.model.api.validators.ErrorMessage;
import com.qcadoo.model.internal.ProxyEntity;
import com.qcadoo.model.internal.types.EnumType;
import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.components.GridComponent;
import com.qcadoo.view.api.components.grid.GridComponentMultiSearchFilter;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;
import com.qcadoo.view.internal.CriteriaModifier;
import com.qcadoo.view.internal.FilterValueHolderImpl;
import com.qcadoo.view.internal.RowStyleResolver;
import com.qcadoo.view.internal.states.AbstractComponentState;

public final class GridComponentState extends AbstractComponentState implements GridComponent {

    enum ExportMode {
        ALL, SELECTED
    };

    public static final String JSON_SELECTED_ENTITY_ID = "selectedEntityId";

    public static final String JSON_HEADER_VALUE = "headerValue";

    public static final String JSON_BELONGS_TO_ENTITY_ID = "belongsToEntityId";

    public static final String JSON_FIRST_ENTITY = "firstEntity";

    public static final String JSON_MAX_ENTITIES = "maxEntities";

    private static final String JSON_TOTAL_ENTITIES = "totalEntities";

    public static final String JSON_ORDER = "order";

    private static final String JSON_ONLY_ACTIVE = "onlyActive";

    private static final String JSON_ONLY_INACTIVE = "onlyInactive";

    private static final String JSON_ORDER_COLUMN = "column";

    private static final String JSON_ORDER_DIRECTION = "direction";

    public static final String JSON_FILTERS = "filters";

    public static final String JSON_FILTERS_ENABLED = "filtersEnabled";

    private static final String JSON_MULTI_SEARCH_FILTER = "multiSearchFilter";

    private static final String JSON_MULTI_SEARCH_ENABLED = "multiSearchEnabled";

    private static final String JSON_ENTITIES = "entities";

    private static final String JSON_EDITABLE = "isEditable";

    public static final String JSON_MULTISELECT_MODE = "multiselectMode";

    public static final String JSON_SELECTED_ENTITIES = "selectedEntities";

    private static final String JSON_ENTITIES_TO_MARK_AS_NEW = "entitiesToMarkAsNew";

    private static final String JSON_ENTITIES_TO_MARK_WITH_CSS_CLASS = "entitiesToMarkWithCssClass";

    private static final String JSON_CRITERIA_MODIFIER_PARAMETER = "criteriaModifierParameter";

    private static final String JSON_DELETE_ENABLED = "deleteEnabled";

    private static final String JSON_AUTOMATIC_REFRESH = "autoRefresh";

	private static final String JSON_USER_HIDDEN_COLUMNS = "userHiddenColumns";

    private final GridEventPerformer eventPerformer = new GridEventPerformer();

    private final Map<String, GridComponentColumn> columns;

    private final FieldDefinition belongsToFieldDefinition;

    private Long selectedEntityId;

    private Long belongsToEntityId;

    private List<Entity> entities;

    private int totalEntities;

    private int firstResult;

    private int maxResults = Integer.MAX_VALUE;

    private boolean filtersEnabled = true;

    private boolean multiSearchEnabled = false;

    private final List<GridComponentOrderColumn> orderColumns = Lists.newLinkedList();

    private boolean multiselectMode = false;

    private Boolean isEditable = null;

    private Set<Long> selectedEntities = Sets.newHashSet();

    private Set<Long> entitiesToMarkAsNew = Sets.newHashSet();

    private CustomRestriction customRestriction;

    private final Map<String, String> filters = Maps.newHashMap();

    private final GridComponentMultiSearchFilter multiSearchFilter = new GridComponentMultiSearchFilter();

    private final PredefinedFilter defaultPredefinedFilter;

    private boolean onlyActive = true;

    private boolean onlyInactive = false;

    private final boolean activable;

    private boolean deletable = false;

    private final boolean weakRelation;

    private final DataDefinition scopeFieldDataDefinition;

    private final RowStyleResolver rowStyleResolver;

    private final CriteriaModifier criteriaModifier;

    private final FilterValueHolder criteriaModifierParameter;

    private final SecurityRole authorizationRole;

    private String deletableAuthorizationRole = "";

    private boolean deleteEnabled = false;

    private final SecurityRolesService securityRolesService;

    private boolean useDto = false;

    private boolean footerRow = false;

    private String columnsToSummary = "";

    private String columnsToSummaryTime = "";

    private boolean autoRefresh = false;

    private final GridComponentPattern pattern;

    private Set<String> userHiddenColumns = Sets.newHashSet();

    public GridComponentState(final DataDefinition dataDefinition, final GridComponentPattern pattern) {
        super(pattern);

        this.pattern = pattern;
        this.belongsToFieldDefinition = pattern.getBelongsToFieldDefinition();

        if (pattern.getDefaultOrderColumn() != null) {
            this.orderColumns.add(new GridComponentOrderColumn(pattern.getDefaultOrderColumn(), pattern
                    .getDefaultOrderDirection()));
        }

        this.activable = pattern.isActivable();
        this.onlyActive = pattern.isOnlyActive();
        this.weakRelation = pattern.isWeakRelation();
        this.scopeFieldDataDefinition = dataDefinition;
        this.rowStyleResolver = pattern.getRowStyleResolver();
        this.criteriaModifier = pattern.getCriteriaModifier();
        this.criteriaModifierParameter = new FilterValueHolderImpl();
        this.defaultPredefinedFilter = pattern.getDefaultPredefinedFilter();
        this.authorizationRole = pattern.getAuthorizationRole();
        this.securityRolesService = pattern.getApplicationContext().getBean(SecurityRolesService.class);
        this.deletable = pattern.isDeletable();
        this.deletableAuthorizationRole = pattern.getDeletableAuthorizationRole();
        this.autoRefresh = pattern.isautoRefresh();
        this.footerRow = pattern.isFooterRow();
        this.columnsToSummary = pattern.getColumnsToSummary();
        this.columnsToSummaryTime = pattern.getColumnsToSummaryTime();
        this.useDto = pattern.isUseDto();
        this.columns = pattern.getColumns();

        registerEvent("refresh", eventPerformer, "refresh");
        registerEvent("select", eventPerformer, "selectEntity");
        registerEvent("addExistingEntity", eventPerformer, "addExistingEntity");
        registerEvent("remove", eventPerformer, "removeSelectedEntity");
        registerEvent("moveUp", eventPerformer, "moveUpSelectedEntity");
        registerEvent("moveDown", eventPerformer, "moveDownSelectedEntity");
        registerEvent("copy", eventPerformer, "copySelectedEntity");
        registerEvent("activate", eventPerformer, "activateSelectedEntity");
        registerEvent("deactivate", eventPerformer, "deactivateSelectedEntity");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initializeContext(final JSONObject json) throws JSONException {
        super.initializeContext(json);

        Iterator<String> iterator = json.keys();

        while (iterator.hasNext()) {
            String field = iterator.next();
            if (JSON_BELONGS_TO_ENTITY_ID.equals(field)) {
                onScopeEntityIdChange(json.getLong(field));
            } else if (JSON_COMPONENT_OPTIONS.equals(field)) {
                JSONObject jsonOptions = json.getJSONObject(JSON_COMPONENT_OPTIONS);

                passFiltersFromJson(jsonOptions);
                passMultiSearchFilterFromJson(jsonOptions);
                passSelectedEntityIdFromJson(jsonOptions);
                passSelectedEntitiesFromJson(jsonOptions);
                passEntitiesFromJson(jsonOptions);
                passCriteriaModifierParameterFromJson(jsonOptions);
            }
        }
    }

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        if (json.has(JSON_SELECTED_ENTITY_ID) && !json.isNull(JSON_SELECTED_ENTITY_ID)) {
            selectedEntityId = json.getLong(JSON_SELECTED_ENTITY_ID);
        }
        if (json.has(JSON_MULTISELECT_MODE) && !json.isNull(JSON_MULTISELECT_MODE)) {
            multiselectMode = json.getBoolean(JSON_MULTISELECT_MODE);
        }
        if (json.has(JSON_SELECTED_ENTITIES) && !json.isNull(JSON_SELECTED_ENTITIES)) {
            JSONObject selectedEntitiesObj = json.getJSONObject(JSON_SELECTED_ENTITIES);

            JSONArray selectedEntitiesIds = selectedEntitiesObj.names();

            for (int i = 0; selectedEntitiesIds != null && i < selectedEntitiesIds.length(); i++) {
                String key = selectedEntitiesIds.getString(i);

                boolean isSelected = false;

                if (selectedEntitiesObj.has(key) && !selectedEntitiesObj.isNull(key)) {
                    isSelected = selectedEntitiesObj.getBoolean(key);
                }

                if (isSelected) {
                    selectedEntities.add(Long.parseLong(key));
                }
            }
        }
        if (json.has(JSON_BELONGS_TO_ENTITY_ID) && !json.isNull(JSON_BELONGS_TO_ENTITY_ID)) {
            belongsToEntityId = json.getLong(JSON_BELONGS_TO_ENTITY_ID);
        }
        if (json.has(JSON_FIRST_ENTITY) && !json.isNull(JSON_FIRST_ENTITY)) {
            firstResult = json.getInt(JSON_FIRST_ENTITY);
        }
        if (json.has(JSON_MAX_ENTITIES) && !json.isNull(JSON_MAX_ENTITIES)) {
            maxResults = json.getInt(JSON_MAX_ENTITIES);
        }
        if (json.has(JSON_ONLY_ACTIVE) && !json.isNull(JSON_ONLY_ACTIVE) && activable) {
            onlyActive = json.getBoolean(JSON_ONLY_ACTIVE);
        }
        if (json.has(JSON_ONLY_INACTIVE) && !json.isNull(JSON_ONLY_INACTIVE) && activable) {
            onlyInactive = json.getBoolean(JSON_ONLY_INACTIVE);
        }
        if (json.has(JSON_ORDER)) {
            JSONArray orderJson = json.getJSONArray(JSON_ORDER);

            orderColumns.clear();

            for (int i = 0; i < orderJson.length(); i++) {
                JSONObject orderColumn = orderJson.getJSONObject(i);

                if (orderColumn.has(JSON_ORDER_COLUMN) && orderColumn.has(JSON_ORDER_DIRECTION)) {
                    orderColumns.add(new GridComponentOrderColumn(orderColumn.getString(JSON_ORDER_COLUMN), orderColumn
                            .getString(JSON_ORDER_DIRECTION)));
                }
            }
        }
        if (json.has(JSON_AUTOMATIC_REFRESH)) {
            autoRefresh = json.getBoolean(JSON_AUTOMATIC_REFRESH);
        }
        if ((belongsToFieldDefinition != null && belongsToEntityId == null) || !securityRolesService.canAccess(authorizationRole)) {
            setEnabled(false);
        }
		if (json.has(JSON_USER_HIDDEN_COLUMNS)) {
			JSONArray hiddenColumnsJson = json.getJSONArray(JSON_USER_HIDDEN_COLUMNS);

			for (int i = 0; i < hiddenColumnsJson.length(); i++) {
				String hiddenColumnName = hiddenColumnsJson.getString(i);

				userHiddenColumns.add(hiddenColumnName);
			}
		}
        if (deletable && StringUtils.isNotEmpty(deletableAuthorizationRole)
                && securityRolesService.canAccess(deletableAuthorizationRole) || deletable
                && StringUtils.isEmpty(deletableAuthorizationRole)) {
            this.deleteEnabled = true;
        }

        passFiltersFromJson(json);
        passMultiSearchFilterFromJson(json);

        requestRender();
        requestUpdateState();
    }

    private void applyPredefinedFilter(final PredefinedFilter predefinedFilter) {
        if (predefinedFilter == null) {
            return;
        }

        orderColumns.clear();

        if (predefinedFilter.getOrderColumn() != null) {
            orderColumns
                    .add(new GridComponentOrderColumn(predefinedFilter.getOrderColumn(), predefinedFilter.getOrderDirection()));
        }

        filters.putAll(predefinedFilter.getParsedFilterRestrictions());
    }

    @SuppressWarnings("unchecked")
    private void passFiltersFromJson(final JSONObject json) throws JSONException {
        if (json.has(JSON_FILTERS_ENABLED) && !json.isNull(JSON_FILTERS_ENABLED)) {
            filtersEnabled = json.getBoolean(JSON_FILTERS_ENABLED);
        }

        if (json.has(JSON_FILTERS) && !json.isNull(JSON_FILTERS)) {
            filtersEnabled = true;

            JSONObject filtersJson = json.getJSONObject(JSON_FILTERS);

            Iterator<String> filtersKeys = filtersJson.keys();

            while (filtersKeys.hasNext()) {
                String column = filtersKeys.next();

                filters.put(column, filtersJson.getString(column).trim());
            }
        } else {
            applyPredefinedFilter(defaultPredefinedFilter);
        }
    }

    private void passMultiSearchFilterFromJson(final JSONObject json) throws JSONException {
        if (json.has(JSON_MULTI_SEARCH_ENABLED) && !json.isNull(JSON_MULTI_SEARCH_ENABLED)) {
            multiSearchEnabled = json.getBoolean(JSON_MULTI_SEARCH_ENABLED);
        }

        if (json.has(JSON_MULTI_SEARCH_FILTER) && !json.isNull(JSON_MULTI_SEARCH_FILTER)) {
            multiSearchFilter.clear();

            JSONObject jsonMultiSearchFilter = json.getJSONObject(JSON_MULTI_SEARCH_FILTER);

            if (jsonMultiSearchFilter.has(GridComponentMultiSearchFilter.JSON_GROUP_OPERATOR_FIELD)
                    && !jsonMultiSearchFilter.isNull(GridComponentMultiSearchFilter.JSON_GROUP_OPERATOR_FIELD)) {
                multiSearchFilter.setGroupOperator(jsonMultiSearchFilter
                        .getString(GridComponentMultiSearchFilter.JSON_GROUP_OPERATOR_FIELD));
            }
            if (jsonMultiSearchFilter.has(GridComponentMultiSearchFilter.JSON_RULES_FIELD)
                    && !jsonMultiSearchFilter.isNull(GridComponentMultiSearchFilter.JSON_RULES_FIELD)) {
                JSONArray jsonMultiSearchFilterRules = jsonMultiSearchFilter
                        .getJSONArray(GridComponentMultiSearchFilter.JSON_RULES_FIELD);

                for (int i = 0; i < jsonMultiSearchFilterRules.length(); ++i) {
                    JSONObject jsonRule = jsonMultiSearchFilterRules.getJSONObject(i);

                    multiSearchFilter.addRule(
                            jsonRule.getString(GridComponentMultiSearchFilterRule.JSON_FIELD_FIELD),
                            jsonRule.getString(GridComponentMultiSearchFilterRule.JSON_OPERATOR_FIELD),
                            jsonRule.isNull(GridComponentMultiSearchFilterRule.JSON_DATA_FIELD) ? null : jsonRule
                                    .getString(GridComponentMultiSearchFilterRule.JSON_DATA_FIELD));
                }
            }
        }
    }

    private void passSelectedEntitiesFromJson(final JSONObject json) throws JSONException {
        if (json.has(JSON_SELECTED_ENTITIES) && !json.isNull(JSON_SELECTED_ENTITIES)) {
            selectedEntities = Sets.newHashSet();

            JSONArray entitiesToSelect = json.getJSONArray(JSON_SELECTED_ENTITIES);

            for (int i = 0; i < entitiesToSelect.length(); i++) {
                selectedEntities.add(Long.valueOf(entitiesToSelect.get(i).toString()));
            }
        }
    }

    private void passSelectedEntityIdFromJson(final JSONObject json) throws JSONException {
        if (json.has(JSON_SELECTED_ENTITY_ID) && !json.isNull(JSON_SELECTED_ENTITY_ID)) {
            selectedEntityId = json.getLong(JSON_SELECTED_ENTITY_ID);
        }
    }

    private void passEntitiesFromJson(final JSONObject json) throws JSONException {
        if (gridIsEmpty() && json.has(JSON_ENTITIES) && !json.isNull(JSON_ENTITIES)) {
            entities = Lists.newArrayList();

            JSONArray givenEntities = json.getJSONArray(JSON_ENTITIES);

            Long entityId = null;

            for (int i = 0; i < givenEntities.length(); i++) {
                entityId = Long.valueOf(givenEntities.get(i).toString());

                entities.add(getDataDefinition().get(entityId));
            }
        }
    }

    private void passCriteriaModifierParameterFromJson(final JSONObject json) throws JSONException {
        if (json.has(JSON_CRITERIA_MODIFIER_PARAMETER) && !json.isNull(JSON_CRITERIA_MODIFIER_PARAMETER)) {
            criteriaModifierParameter.initialize(json.getJSONObject(JSON_CRITERIA_MODIFIER_PARAMETER));
        }
    }

    private boolean gridIsEmpty() {
        return belongsToEntityId == null;
    }

    @Override
    public void onFieldEntityIdChange(final Long entityId) {
        setSelectedEntityId(entityId);
    }

    @Override
    public void onScopeEntityIdChange(final Long scopeEntityId) {
        if (belongsToFieldDefinition == null) {
            throw new IllegalStateException("Grid doesn't have scopeField, it cannot set scopeEntityId");
        }

        if (belongsToEntityId != null && !belongsToEntityId.equals(scopeEntityId)) {
            setSelectedEntityId(null);
            selectedEntities = Sets.newHashSet();
            multiselectMode = false;
        }

        belongsToEntityId = scopeEntityId;

        setEnabled(scopeEntityId != null && securityRolesService.canAccess(authorizationRole));
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        if (entities == null) {
            eventPerformer.reload();
        }

        if (entities == null) {
            throw new IllegalStateException("Cannot load entities for grid component");
        }

        JSONObject json = new JSONObject();

        json.put(JSON_SELECTED_ENTITY_ID, selectedEntityId);
        json.put(JSON_BELONGS_TO_ENTITY_ID, belongsToEntityId);
        json.put(JSON_FIRST_ENTITY, firstResult);
        json.put(JSON_MAX_ENTITIES, maxResults);
        json.put(JSON_FILTERS_ENABLED, filtersEnabled);
        json.put(JSON_MULTI_SEARCH_ENABLED, multiSearchEnabled);
        json.put(JSON_TOTAL_ENTITIES, totalEntities);
        json.put(JSON_ONLY_ACTIVE, onlyActive);
        json.put(JSON_ONLY_INACTIVE, onlyInactive);
        json.put(JSON_MULTISELECT_MODE, multiselectMode);

        JSONObject selectedEntitiesJson = new JSONObject();

        for (Long entityId : selectedEntities) {
            selectedEntitiesJson.put(entityId.toString(), true);
        }

        json.put(JSON_SELECTED_ENTITIES, selectedEntitiesJson);

        if (isEditable != null) {
            json.put(JSON_EDITABLE, isEditable);
        }

        if (!entitiesToMarkAsNew.isEmpty()) {
            JSONObject entitiesToMarkAsNewJson = new JSONObject();

            for (Long entityId : entitiesToMarkAsNew) {
                entitiesToMarkAsNewJson.put(entityId.toString(), true);
            }

            json.put(JSON_ENTITIES_TO_MARK_AS_NEW, entitiesToMarkAsNewJson);
        }

        if (rowStyleResolver != null) {
            json.put(JSON_ENTITIES_TO_MARK_WITH_CSS_CLASS, getRowStyles());
        }

        JSONArray jsonOrderList = new JSONArray();

        for (GridComponentOrderColumn orderColumn : orderColumns) {
            JSONObject jsonOrder = new JSONObject();

            jsonOrder.put(JSON_ORDER_COLUMN, orderColumn.getName());
            jsonOrder.put(JSON_ORDER_DIRECTION, orderColumn.getDirection());
            jsonOrderList.put(jsonOrder);
        }
        if (!orderColumns.isEmpty()) {
            json.put(JSON_ORDER, jsonOrderList);
        }

        json.put(JSON_FILTERS, new JSONObject(filters));
        json.put(JSON_MULTI_SEARCH_FILTER, multiSearchFilter.toJson());

        JSONArray jsonEntities = new JSONArray();

        for (Entity entity : entities) {
            jsonEntities.put(convertEntityToJson(entity));
        }

        json.put(JSON_ENTITIES, jsonEntities);

        if (criteriaModifierParameter != null) {
            json.put(JSON_CRITERIA_MODIFIER_PARAMETER, criteriaModifierParameter);

            if (criteriaModifierParameter.has(JSON_HEADER_VALUE)) {
                json.put(JSON_HEADER_VALUE, criteriaModifierParameter.getString(JSON_HEADER_VALUE));
            }
        }

        json.put(JSON_DELETE_ENABLED, this.deleteEnabled);
        json.put(JSON_AUTOMATIC_REFRESH, this.autoRefresh);

        return json;
    }

    private JSONObject getRowStyles() throws JSONException {
        final JSONObject stylesForEntities = new JSONObject();

        for (Map.Entry<Long, Set<String>> entityToStyles : rowStyleResolver.resolve(entities).entrySet()) {
            stylesForEntities.put(entityToStyles.getKey().toString(), new JSONArray(entityToStyles.getValue()));
        }

        return stylesForEntities;
    }

    private JSONObject convertEntityToJson(final Entity entity) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id", entity.getId());

        if (activable) {
            json.put("active", entity.isActive());
        } else {
            json.put("active", true);
        }

        JSONObject fields = new JSONObject();

        for (GridComponentColumn column : pattern.filterColumnsWithAccess(columns.values())) {
            fields.put(column.getName(), column.getValue(entity, getLocale()));
        }

        json.put("fields", fields);

        return json;
    }

    @Override
    public Set<Long> getSelectedEntitiesIds() {
        return selectedEntities;
    }

    @Override
    public List<Entity> getSelectedEntities() {
        if (selectedEntities == null || selectedEntities.isEmpty()) {
            return Lists.newArrayList();
        }

        final SearchCriteriaBuilder searchCriteria = getDataDefinition().find();

        searchCriteria.add(SearchRestrictions.in("id", selectedEntities));

        return searchCriteria.list().getEntities();
    }

    @Override
    public void setEditable(final boolean isEditable) {
        this.isEditable = isEditable;
    }

    @Override
    public void setSelectedEntitiesIds(final Set<Long> selectedEntities) {
        this.selectedEntities = selectedEntities;

        if (selectedEntities == null || selectedEntities.size() < 2) {
            multiselectMode = false;
        } else {
            multiselectMode = true;
        }
    }

    @Override
    public void setEntities(final List<Entity> entities) {
        this.entities = entities;

        totalEntities = entities.size();
    }

    @Override
    public List<Entity> getEntities() {
        if (entities == null) {
            eventPerformer.reload();
        }

        return entities;
    }

    @Override
    public void reloadEntities() {
        eventPerformer.reload();
    }

    private void setSelectedEntityId(final Long selectedEntityId) {
        this.selectedEntityId = selectedEntityId;

        notifyEntityIdChangeListeners(selectedEntityId);
    }

    private Long getSelectedEntityId() {
        return selectedEntityId;
    }

    @Override
    public Object getFieldValue() {
        return getSelectedEntityId();
    }

    @Override
    public void setFieldValue(final Object value) {
        setSelectedEntityId((Long) value);
    }

    @Override
    public void setCustomRestriction(final CustomRestriction customRestriction) {
        this.customRestriction = customRestriction;
    }

    protected class GridEventPerformer {

        public void refresh(final String[] args) {
            // nothing interesting here
        }

        public void selectEntity(final String[] args) {
            notifyEntityIdChangeListeners(getSelectedEntityId());
        }

        public void addExistingEntity(final String[] selectedEntities) throws JSONException {
            if (!weakRelation || selectedEntities.length == 0) {
                return;
            }

            JSONArray selectedEntitiesArray = null;

            if (selectedEntities[0].contains("[")) {
                selectedEntitiesArray = new JSONArray(selectedEntities[0]);
            } else {
                selectedEntitiesArray = new JSONArray(selectedEntities);
            }

            List<Long> selectedEntitiesId = Lists.newArrayList();

            for (int i = 0; i < selectedEntitiesArray.length(); i++) {
                selectedEntitiesId.add(Long.parseLong(selectedEntitiesArray.getString(i)));
            }

            List<Entity> existingEntities = getEntities();
            List<Entity> newlyAddedEntities = getDataDefinition().find().add(SearchRestrictions.in("id", selectedEntitiesId))
                    .list().getEntities();

            entitiesToMarkAsNew = Sets.newHashSet(selectedEntitiesId);

            for (Entity existingEntity : existingEntities) {
                entitiesToMarkAsNew.remove(existingEntity.getId());
            }

            existingEntities.addAll(newlyAddedEntities);

            FieldType belongsToFieldType = belongsToFieldDefinition.getType();

            if (belongsToFieldType instanceof JoinFieldHolder) {
                Entity gridOwnerEntity = scopeFieldDataDefinition.get(belongsToEntityId);
                List<Entity> entities = gridOwnerEntity.getManyToManyField(((JoinFieldHolder) belongsToFieldType)
                        .getJoinFieldName());
                entities.addAll(newlyAddedEntities);
                gridOwnerEntity.setField(((JoinFieldHolder) belongsToFieldType).getJoinFieldName(), entities);
                gridOwnerEntity.getDataDefinition().save(gridOwnerEntity);
                copyFieldValidationMessages(gridOwnerEntity);
            } else if (belongsToFieldType instanceof BelongsToType) {
                for (Entity entity : newlyAddedEntities) {
                    Entity newEntity = entity.getDataDefinition().getMasterModelEntity(entity.getId());
                    newEntity.setField(belongsToFieldDefinition.getName(), belongsToEntityId);
                    newEntity.getDataDefinition().save(newEntity);
                    copyFieldValidationMessages(newEntity);
                }
            } else {
                throw new IllegalArgumentException("Unsupported relation type - " + belongsToFieldDefinition.getType().toString());
            }

            reload();
        }

        public void removeSelectedEntity(final String[] args) {
            EntityOpResult result = EntityOpResult.successfull();

            if (weakRelation) {
                Entity entity = null;
                boolean isManyToManyRelationType = belongsToFieldDefinition.getType() instanceof JoinFieldHolder;
                Long[] selectedEntitiesIds = selectedEntities.toArray(new Long[selectedEntities.size()]);

                if (isManyToManyRelationType) {
                    String gridFieldName = ((JoinFieldHolder) belongsToFieldDefinition.getType()).getJoinFieldName();
                    Entity gridOwnerEntity = scopeFieldDataDefinition.get(belongsToEntityId);

                    List<Entity> relatedEntities = gridOwnerEntity.getManyToManyField(gridFieldName);

                    for (Long selectedId : selectedEntitiesIds) {
                        relatedEntities.remove(new ProxyEntity(getDataDefinition(), selectedId));
                    }

                    gridOwnerEntity.setField(gridFieldName, relatedEntities);
                    scopeFieldDataDefinition.save(gridOwnerEntity);
                    copyFieldValidationMessages(gridOwnerEntity);
                } else {
                    for (Long selectedId : selectedEntitiesIds) {
                        entity = getDataDefinition().getMasterModelEntity(selectedId);
                        entity.setField(belongsToFieldDefinition.getName(), null);
                        entity.getDataDefinition().save(entity);
                        copyFieldValidationMessages(entity);
                    }
                }
            } else {
                result = getDataDefinition().delete(selectedEntities.toArray(new Long[selectedEntities.size()]));
            }

            if (result.isSuccessfull()) {
                if (selectedEntities.size() == 1) {
                    addTranslatedMessage(translateMessage("deleteMessage"), MessageType.SUCCESS);
                } else {
                    addTranslatedMessage(translateMessage("deleteMessages", String.valueOf(selectedEntities.size())),
                            MessageType.SUCCESS);
                }

                setSelectedEntityId(null);
                multiselectMode = false;
                selectedEntities = Sets.newHashSet();
            } else {
                copyMessages(result.getMessagesHolder().getGlobalErrors());
            }

            copyGlobalMessages(result.getMessagesHolder().getGlobalMessages());
        }

        public void moveUpSelectedEntity(final String[] args) {
            getDataDefinition().move(selectedEntityId, -1);
            addTranslatedMessage(translateMessage("moveMessage"), MessageType.SUCCESS);
        }

        public void deactivateSelectedEntity(final String[] args) {
            try {
                List<Entity> deactivatedEntities = getDataDefinition().deactivate(
                        selectedEntities.toArray(new Long[selectedEntities.size()]));

                entitiesToMarkAsNew = Sets.newHashSet();

                for (Entity entity : deactivatedEntities) {
                    entitiesToMarkAsNew.add(entity.getId());
                }

                if (selectedEntities.size() == 1) {
                    addTranslatedMessage(translateMessage("deactivateMessage"), MessageType.SUCCESS);
                } else {
                    addTranslatedMessage(translateMessage("deactivateMessages", String.valueOf(selectedEntities.size())),
                            MessageType.SUCCESS);
                }
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("validation")) {
                    addTranslatedMessage(translateMessage("deactivateFailedValidationMessage"), MessageType.FAILURE);
                } else {
                    addTranslatedMessage(translateMessage("deactivateFailedMessage"), MessageType.FAILURE);
                }
            }
        }

        public void activateSelectedEntity(final String[] args) {
            try {
                List<Entity> activatedEntities = getDataDefinition().activate(
                        selectedEntities.toArray(new Long[selectedEntities.size()]));

                entitiesToMarkAsNew = Sets.newHashSet();

                for (Entity entity : activatedEntities) {
                    entitiesToMarkAsNew.add(entity.getId());
                }

                if (selectedEntities.size() == 1) {
                    addTranslatedMessage(translateMessage("activateMessage"), MessageType.SUCCESS);
                } else {
                    addTranslatedMessage(translateMessage("activateMessages", String.valueOf(selectedEntities.size())),
                            MessageType.SUCCESS);
                }
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("validation")) {
                    addTranslatedMessage(translateMessage("activateFailedValidationMessage"), MessageType.FAILURE);
                } else {
                    addTranslatedMessage(translateMessage("activateFailedMessage"), MessageType.FAILURE);
                }
            }
        }

        public void copySelectedEntity(final String[] args) {
            List<Entity> copiedEntities = getDataDefinition().copy(selectedEntities.toArray(new Long[selectedEntities.size()]));

            entitiesToMarkAsNew = Sets.newHashSet();

            for (Entity copiedEntity : copiedEntities) {
                entitiesToMarkAsNew.add(copiedEntity.getId());
            }

            if (selectedEntities.size() == 1) {
                addTranslatedMessage(translateMessage("copyMessage"), MessageType.SUCCESS);
            } else {
                addTranslatedMessage(translateMessage("copyMessages", String.valueOf(selectedEntities.size())),
                        MessageType.SUCCESS);
            }
        }

        public void moveDownSelectedEntity(final String[] args) {
            getDataDefinition().move(selectedEntityId, 1);
            addTranslatedMessage(translateMessage("moveMessage"), MessageType.SUCCESS);
        }

        private void reload() {
            if (belongsToFieldDefinition == null || belongsToEntityId != null) {
                SearchCriteriaBuilder criteria = getDataDefinition().find();

                if (belongsToFieldDefinition != null && !useDto) {
                    if (belongsToFieldDefinition.getType() instanceof ManyToManyType) {
                        String belongsToFieldName = belongsToFieldDefinition.getName();
                        criteria.createAlias(belongsToFieldName, belongsToFieldName).add(
                                SearchRestrictions.eq(belongsToFieldName + ".id", belongsToEntityId));
                    } else {
                        // criteria.add(SearchRestrictions.belongsTo(belongsToFieldDefinition.getName(), ((DataDefinitionHolder)
                        // belongsToFieldDefinition.getType()).getDataDefinition(), belongsToEntityId));
                        criteria.add(SearchRestrictions.eq(belongsToFieldDefinition.getName() + ".id", belongsToEntityId));
                    }
                }
                if (useDto) {
                    criteria.add(SearchRestrictions.eq(buildDtoIdFieldName(belongsToFieldDefinition.getName()),
                            belongsToEntityId.intValue()));
                }

                try {
                    if (filtersEnabled) {
                        GridComponentFilterUtils.addFilters(filters, columns, getDataDefinition(), criteria);
                    }

                    if (multiSearchEnabled) {
                        GridComponentFilterUtils.addMultiSearchFilter(multiSearchFilter, columns, getDataDefinition(), criteria);
                    }

                    if (customRestriction != null) {
                        customRestriction.addRestriction(criteria);
                    }

                    if (activable && onlyActive) {
                        criteria.add(SearchRestrictions.eq("active", true));
                    }
                    if (activable && onlyInactive) {
                        criteria.add(SearchRestrictions.eq("active", false));
                    }

                    addOrder(criteria);
                    addPaging(criteria);

                    if (criteriaModifier != null) {
                        criteriaModifier.modifyCriteria(criteria, criteriaModifierParameter);
                    }

                    SearchResult result = criteria.list();

                    if (repeatWithFixedFirstResult(result)) {
                        addPaging(criteria);
                        result = criteria.list();
                    }

                    entities = result.getEntities();
                    totalEntities = result.getTotalNumberOfEntities();
                } catch (GridComponentFilterException gcfe) {
                    addMessage("qcadooView.grid.filter.incorrectValue", MessageType.FAILURE, gcfe.getFilterValue());
                    clear();
                }
            } else {
                clear();
            }
        }

        private void clear() {
            entities = Lists.newArrayList();
            totalEntities = 0;
        }

        private void addPaging(final SearchCriteriaBuilder criteria) {
            criteria.setFirstResult(firstResult);
            criteria.setMaxResults(maxResults);
        }

        private void addOrder(final SearchCriteriaBuilder criteria) {
            for (GridComponentOrderColumn orderColumn : orderColumns) {
                String field = GridComponentFilterUtils.getFieldNameByColumnName(columns, orderColumn.getName());

                field = GridComponentFilterUtils.addAliases(criteria, field, JoinType.LEFT);

                if (field != null) {
                    if ("asc".equals(orderColumn.getDirection())) {
                        criteria.addOrder(SearchOrders.asc(field));
                    } else {
                        criteria.addOrder(SearchOrders.desc(field));
                    }
                }
            }
        }

        private boolean repeatWithFixedFirstResult(final SearchResult result) {
            if (result.getEntities().isEmpty() && result.getTotalNumberOfEntities() > 0) {
                while (firstResult >= result.getTotalNumberOfEntities()) {
                    firstResult -= maxResults;
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private String buildDtoIdFieldName(final String name) {
        StringBuffer dtoKey = new StringBuffer();

        dtoKey.append(name);
        dtoKey.append("Id");

        return dtoKey.toString();
    }

    @Override
    public Map<String, String> getColumnNames() {
        Map<String, String> names = Maps.newLinkedHashMap();

        for (GridComponentColumn column : pattern.filterColumnsWithAccess(columns.values())) {
            if (column.isHidden()) {
                continue;
            }

            if (column.getFields().size() == 1) {
                String fieldCode = getDataDefinition().getPluginIdentifier() + "." + getDataDefinition().getName() + "."
                        + column.getFields().get(0).getName();
                names.put(
                        column.getName(),
                        getTranslationService().translate(getTranslationPath() + ".column." + column.getName(),
                                fieldCode + ".label", getLocale()));
            } else {
                names.put(column.getName(),
                        getTranslationService().translate(getTranslationPath() + ".column." + column.getName(), getLocale()));
            }
        }

        return names;
    }

    @Override
    public List<Map<String, String>> getColumnValuesOfAllRecords() {
        return getColumnValues(ExportMode.ALL);
    }

    @Override
    public List<Map<String, String>> getColumnValuesOfSelectedRecords() {
        return getColumnValues(ExportMode.SELECTED);
    }

    private List<Map<String, String>> getColumnValues(final ExportMode mode) {
        if (entities == null) {
            eventPerformer.reload();
        }

        if (entities == null) {
            throw new IllegalStateException("Cannot load entities for grid component");
        }

        List<Map<String, String>> values = Lists.newArrayList();

        for (Entity entity : entities) {
            if (mode == ExportMode.ALL || (mode == ExportMode.SELECTED && getSelectedEntitiesIds().contains(entity.getId()))) {
                values.add(convertEntityToMap(entity));
            }
        }

        return values;
    }

    private Map<String, String> convertEntityToMap(final Entity entity) {
        Map<String, String> values = Maps.newLinkedHashMap();

        for (GridComponentColumn column : pattern.filterColumnsWithAccess(columns.values())) {
            if (column.isHidden()) {
                continue;
            }

			String fieldValue = column.getValue(entity, getLocale());

            if (column.getFields().get(0).getType() instanceof EnumType) {
                if (fieldValue != null) {
					StringBuffer localeString = new StringBuffer();

					localeString.append(getDataDefinition().getPluginIdentifier());
					localeString.append('.');
					localeString.append(getDataDefinition().getName());
					localeString.append('.');
					localeString.append(column.getName());
					localeString.append(".value.");
					localeString.append(fieldValue);

					values.put(column.getName(), getTranslationService().translate(localeString.toString(), getLocale()));
				} else {
					values.put(column.getName(), "");
				}
            } else if (column.getFields().get(0).getType().getType().equals(Boolean.class)) {
                if (fieldValue != null) {
                    if (fieldValue.equals("1")) {
                        values.put(column.getName(), getTranslationService().translate("qcadooView.true", getLocale()));
                    } else {
                        values.put(column.getName(), getTranslationService().translate("qcadooView.false", getLocale()));
                    }
                } else {
                    values.put(column.getName(), "");
                }
            } else {
                values.put(column.getName(), fieldValue);
            }
        }

        return values;
    }

    private void copyFieldValidationMessages(final Entity messagesSource) {
        for (ErrorMessage message : messagesSource.getErrors().values()) {
            addMessage(message);
        }
    }

    @Override
    public FilterValueHolder getFilterValue() {
        if (criteriaModifier == null) {
            throw new IllegalStateException("There is no critieria modifier. Filter value is not present.");
        }

        FilterValueHolder holder = new FilterValueHolderImpl(criteriaModifierParameter);

        return holder;
    }

    @Override
    public void setFilterValue(final FilterValueHolder value) {
        if (criteriaModifier == null) {
            throw new IllegalStateException("There is no critieria modifier, can't set filter value.");
        }

        criteriaModifierParameter.initialize(value.toJSON());

        requestRender();
    }

    @Override
    public Map<String, String> getFilters() {
        return filters;
    }

    @Override
    public void setFilters(Map<String, String> newFilters) {
        filters.putAll(newFilters);
    }

    @Override
    public Map<String, GridComponentColumn> getColumns() {
        return columns;
    }

	@Override
	public Set<String> getUserHiddenColumns() {
		return userHiddenColumns;
	}

    @Override
    public GridComponentMultiSearchFilter getMultiSearchFilter() {
        return multiSearchFilter;
    }
}
