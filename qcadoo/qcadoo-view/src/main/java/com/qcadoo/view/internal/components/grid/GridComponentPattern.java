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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.DataDefinitionHolder;
import com.qcadoo.model.api.types.EnumeratedType;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.api.types.JoinFieldHolder;
import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.constants.Alignment;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.CriteriaModifier;
import com.qcadoo.view.internal.RowStyleResolver;
import com.qcadoo.view.internal.module.gridColumn.ViewGridColumnModuleColumnModel;
import com.qcadoo.view.internal.patterns.AbstractComponentPattern;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;

public class GridComponentPattern extends AbstractComponentPattern {

    private static final String L_COLUMN = "column";

    private static final String L_WIDTH = "width";

    private static final String JSP_PATH = "elements/grid.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.Grid";

    private static final int DEFAULT_GRID_HEIGHT = 300;

    private static final int DEFAULT_GRID_WIDTH = 300;

    private static final Predicate<GridComponentColumn> COLUMNS_VISIBLE_FOR_TENANT_PREDICATE = new Predicate<GridComponentColumn>() {

        @Override
        public boolean apply(final GridComponentColumn column) {
            return column != null && column.isVisibleForCurrentTenant();
        }
    };

    private static final Function<Entry<String, GridComponentColumn>, GridComponentColumn> VALUE_FROM_MAP_ENTRY_FUNCTION = new Function<Entry<String, GridComponentColumn>, GridComponentColumn>() {

        @Override
        public GridComponentColumn apply(final Entry<String, GridComponentColumn> from) {
            if (from == null) {
                return null;
            } else {
                return from.getValue();
            }
        }
    };

    private final Set<String> searchableColumns = new HashSet<>();

    private final Set<String> multiSearchColumns = new HashSet<>();

    private final Set<String> orderableColumns = new HashSet<>();

    private final Map<String, GridComponentColumn> columns = new LinkedHashMap<>();

    private FieldDefinition belongsToFieldDefinition;

    private String correspondingView;

    private String correspondingComponent;

    private String correspondingLookup;

    private boolean correspondingViewInModal = false;

    private boolean paginable = true;

    private boolean deletable = false;

    private boolean creatable = false;

    private boolean multiselect = false;

    private boolean hasPredefinedFilters = false;

    private boolean filtersDefaultVisible = true;

    private boolean weakRelation = false;

    private String defaultPredefinedFilterName = "";

    private final Map<String, PredefinedFilter> predefinedFilters = Maps.newLinkedHashMap();

    private int height = DEFAULT_GRID_HEIGHT;

    private int width = DEFAULT_GRID_WIDTH;

    private String defaultOrderColumn;

    private String defaultOrderDirection;

    private boolean lookup = false;

    private boolean activable = false;

    private boolean prioritizable = true;

    private boolean onlyActive = true;

    private Boolean fixedHeight;

    private boolean shrinkToFit = true;

    private RowStyleResolver rowStyleResolver = null;

    private CriteriaModifier criteriaModifier = null;

    private SecurityRole authorizationRole;

    private String deletableAuthorizationRole = "";

    private String linkAuthorizationRole = "";

    private boolean footerRow = false;

    private String columnsToSummary = "";

    private String columnsToSummaryTime = "";

    private boolean autoRefresh = false;

    private final SecurityRolesService securityRolesService;

    private boolean suppressSelectEvent = false;

    public GridComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
        securityRolesService = getApplicationContext().getBean(SecurityRolesService.class);
    }

    @Override
    public ComponentState getComponentStateInstance() {
        DataDefinition scopeFieldDataDefinition = null;
        if (getScopeFieldDefinition() != null) {
            scopeFieldDataDefinition = getScopeFieldDefinition().getDataDefinition();
        }
        return new GridComponentState(scopeFieldDataDefinition, this);
    }

    @Override
    public String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    public String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    public String getJsObjectName() {
        return JS_OBJECT;
    }

    @Override
    protected void initializeComponent() throws JSONException {
        configureBelongsToFieldDefinition();
        parseOptions();

        activable = getDataDefinition().isActivable();

        if (creatable && weakRelation && getScopeFieldDefinition() == null) {
            throwIllegalStateException("Missing scope field for grid");
        }

        if (correspondingView != null && correspondingComponent == null) {
            throwIllegalStateException("Missing correspondingComponent for grid");
        }

        if (weakRelation && creatable && correspondingLookup == null) {
            throwIllegalStateException("Missing correspondingLookup for grid");
        }
    }

    private void configureBelongsToFieldDefinition() {
        if (getScopeFieldDefinition() != null) {
            FieldType fieldType = getScopeFieldDefinition().getType();
            if (fieldType instanceof JoinFieldHolder && fieldType instanceof DataDefinitionHolder) {
                belongsToFieldDefinition = ((DataDefinitionHolder) fieldType).getDataDefinition()
                        .getField(((JoinFieldHolder) fieldType).getJoinFieldName());
            } else {
                throwIllegalStateException("Scope field for grid should be one of: hasMany, tree or manyToMany");
            }
        }
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = super.getJsOptions(locale);
        json.put("paginable", paginable);
        json.put("deletable", deletable);
        json.put("creatable", creatable);
        json.put("multiselect", multiselect);
        json.put("activable", activable);
        json.put("weakRelation", weakRelation);

        json.put("hasPredefinedFilters", hasPredefinedFilters);
        json.put("filtersDefaultVisible", filtersDefaultVisible);

        JSONArray predefinedFiltersArray = new JSONArray();
        for (PredefinedFilter predefinedFilter : predefinedFilters.values()) {
            predefinedFiltersArray.put(predefinedFilter.toJson());
        }

        json.put("predefinedFilters", predefinedFiltersArray);

        json.put("height", height);
        json.put(L_WIDTH, width);
        json.put("fullscreen", width == 0 || height == 0);
        json.put("lookup", lookup);
        json.put("correspondingView", correspondingView);
        json.put("correspondingComponent", correspondingComponent);
        json.put("correspondingLookup", correspondingLookup);
        json.put("correspondingViewInModal", correspondingViewInModal);
        json.put("prioritizable", getDataDefinition().isPrioritizable() && prioritizable);
        json.put("onlyActive", getDataDefinition().isActivable() && onlyActive);
        json.put("searchableColumns", new JSONArray(searchableColumns));
        json.put("multiSearchColumns", new JSONArray(multiSearchColumns));
        json.put("orderableColumns", new JSONArray(orderableColumns));
        json.put("fixedHeight", fixedHeight);
        json.put("shrinkToFit", shrinkToFit);
        json.put("autoRefresh", autoRefresh);
        json.put("footerRow", footerRow);
        json.put("columnsToSummary", columnsToSummary);
        json.put("columnsToSummaryTime", columnsToSummaryTime);
        json.put("suppressSelectEvent", suppressSelectEvent);

        if (belongsToFieldDefinition != null) {
            json.put("belongsToFieldName", belongsToFieldDefinition.getName());
        }

        json.put("columns", getColumnsForJsOptions(locale));

        JSONObject translations = new JSONObject();

        addTranslation(translations, "unactiveVisibleButton", locale);
        addTranslation(translations, "onlyInactiveVisibleButton", locale);
        addTranslation(translations, "unactiveNotVisibleButton", locale);
        addTranslation(translations, "addFilterButton", locale);
        addTranslation(translations, "multiSearchButton", locale);
        addTranslation(translations, "clearFilterButton", locale);
        addTranslation(translations, "columnChooserButton", locale);
        addTranslation(translations, "columnChooserCaption", locale);
        addTranslation(translations, "columnChooserSubmit", locale);
        addTranslation(translations, "columnChooserCancel", locale);
        addTranslation(translations, "saveFilterButton", locale);
        addTranslation(translations, "saveColumnWidthButton", locale);
        addTranslation(translations, "resetFilterButton", locale);
        addTranslation(translations, "noResults", locale);
        addTranslation(translations, "removeFilterButton", locale);
        addTranslation(translations, "newButton", locale);
        addTranslation(translations, "addExistingButton", locale);
        addTranslation(translations, "deleteButton", locale);
        addTranslation(translations, "upButton", locale);
        addTranslation(translations, "downButton", locale);
        addTranslation(translations, "perPage", locale);
        addTranslation(translations, "outOfPages", locale);
        addTranslation(translations, "noRowSelectedError", locale);
        addTranslation(translations, "confirmDeleteMessage", locale);
        addTranslation(translations, "wrongSearchCharacterError", locale);
        addTranslation(translations, "header", locale);
        addTranslation(translations, "selectAll", locale);
        addTranslation(translations, "diselectAll", locale);
        addTranslation(translations, "operator_eq", locale);
        addTranslation(translations, "operator_gt", locale);
        addTranslation(translations, "operator_ge", locale);
        addTranslation(translations, "operator_lt", locale);
        addTranslation(translations, "operator_le", locale);
        addTranslation(translations, "operator_ne", locale);
        addTranslation(translations, "operator_in", locale);
        addTranslation(translations, "operator_cn", locale);
        addTranslation(translations, "operator_bw", locale);
        addTranslation(translations, "operator_ew", locale);
        addTranslation(translations, "operator_isnull", locale);
        addTranslation(translations, "operator_cin", locale);
        addTranslation(translations, "multiSearchTitle", locale);
        addTranslation(translations, "searchButton", locale);
        addTranslation(translations, "resetButton", locale);
        addTranslation(translations, "match", locale);
        addTranslation(translations, "matchAllRules", locale);
        addTranslation(translations, "matchAnyRules", locale);
        addTranslation(translations, "autoRefresh", locale);

        addTranslation(translations, "customPredefinedFilter", locale);
        for (PredefinedFilter filter : predefinedFilters.values()) {
            addTranslation(translations, "filter." + filter.getName(), locale);
        }
//ricefish_alex
        translations.put("loading", getTranslationService().translate("qcadooView.loading", locale));
        translations.put("columnChooserCaption", getTranslationService().translate("qcadooView.grid.columnChooserCaption", locale));
        translations.put("columnChooserSubmit", getTranslationService().translate("qcadooView.grid.columnChooserSubmit", locale));
        translations.put("columnChooserCancel", getTranslationService().translate("qcadooView.grid.columnChooserCancel", locale));

        json.put("translations", translations);

        return json;
    }

    public void addColumn(final String extendingPluginIdentifier, final ViewGridColumnModuleColumnModel columnModel) {
        final GridComponentColumn column = new GridComponentColumn(columnModel.getName(), extendingPluginIdentifier);
        for (FieldDefinition field : parseFields(columnModel.getFields(), column)) {
            column.addField(field);
        }
        column.setHidden(columnModel.getHidden());
        column.setAlign(columnModel.getAlign());
        column.setClassesNames(columnModel.getClassesNames());
        column.setClassesCondition(columnModel.getClassesCondition());
        column.setExpression(columnModel.getExpression());
        column.setLink(columnModel.getLink());
        if (columnModel.getWidth() != null) {
            column.setWidth(columnModel.getWidth());
        }
        columns.put(columnModel.getName(), column);
        if (columnModel.getOrderable()) {
            orderableColumns.add(columnModel.getName());
        }
        if (columnModel.getSearchable()) {
            searchableColumns.add(columnModel.getName());
        }
        if (columnModel.getMultiSearch()) {
            multiSearchColumns.add(columnModel.getName());
        }
    }

    public void removeColumn(final String name) {
        columns.remove(name);
        orderableColumns.remove(name);
        searchableColumns.remove(name);
    }

    private void addTranslation(final JSONObject translation, final String key, final Locale locale) throws JSONException {
        translation.put(key,
                getTranslationService().translate(getTranslationPath() + "." + key, "qcadooView.grid." + key, locale));
    }

    private JSONArray getColumnsForJsOptions(final Locale locale) throws JSONException {
        JSONArray jsonColumns = new JSONArray();
        String nameTranslation = null;
        boolean isLinkAllowed = isLinkAllowed();

        for (GridComponentColumn column : filterColumnsWithAccess(columns.values())) {
            if (!COLUMNS_VISIBLE_FOR_TENANT_PREDICATE.apply(column)) {
                continue;
            }
            if (column.getFields().size() == 1) {
                String fieldCode = getDataDefinition().getPluginIdentifier() + "." + getDataDefinition().getName() + "."
                        + column.getFields().get(0).getName();
                nameTranslation = getTranslationService().translate(getTranslationPath() + ".column." + column.getName(),
                        fieldCode + ".label", locale);
            } else {
                nameTranslation = getTranslationService().translate(getTranslationPath() + ".column." + column.getName(), locale);
            }
            JSONObject jsonColumn = new JSONObject();
            jsonColumn.put("name", column.getName());
            jsonColumn.put("label", nameTranslation);
            jsonColumn.put("link", isLinkAllowed && column.isLink());
            jsonColumn.put("hidden", column.isHidden());
            jsonColumn.put(L_WIDTH, column.getWidth());
            jsonColumn.put("align", column.getAlign().getStringValue());
            jsonColumn.put("classesNames", column.getClassesNames());
            jsonColumn.put("classesCondition", column.getClassesCondition());
            jsonColumn.put("filterValues", getFilterValuesForColumn(column, locale));
            jsonColumn.put("correspondingView", column.getCorrespondingView());
            jsonColumn.put("correspondingField", column.getCorrespondingField());
            jsonColumn.put("correspondingViewField", column.getCorrespondingViewField());
            jsonColumn.put("attachment", column.getAttachment());
            jsonColumns.put(jsonColumn);
        }

        return jsonColumns;
    }

    Collection<GridComponentColumn> filterColumnsWithAccess(Collection<GridComponentColumn> columns) {
        return columns.stream().filter(item -> {
            String columnAuthorizationRole = item.getAuthorizationRole();
            return Strings.isNullOrEmpty(columnAuthorizationRole) || securityRolesService.canAccess(columnAuthorizationRole);

        }).collect(Collectors.toList());
    }

    private JSONObject getFilterValuesForColumn(final GridComponentColumn column, final Locale locale) throws JSONException {
        if (column.getFields().size() != 1) {
            return null;
        }

        JSONObject json = new JSONObject();

        if (column.getFields().get(0).getType() instanceof EnumeratedType) {
            EnumeratedType type = (EnumeratedType) column.getFields().get(0).getType();
            Map<String, String> sortedValues = type.values(locale);
            for (Map.Entry<String, String> value : sortedValues.entrySet()) {
                json.put(value.getKey(), value.getValue());
            }
        } else if (column.getFields().get(0).getType().getType().equals(Boolean.class)) {
            json.put("1", getTranslationService().translate("qcadooView.true", locale));
            json.put("0", getTranslationService().translate("qcadooView.false", locale));
        }

        if (json.length() > 0) {
            return json;
        } else {
            return null;
        }

    }

    @Override
    public void parse(final Node componentNode, final ViewDefinitionParser parser) throws ViewDefinitionParserNodeException {
        super.parse(componentNode, parser);
        authorizationRole = parser.getAuthorizationRole(componentNode);
        final NodeList childNodes = componentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node child = childNodes.item(i);
            if ("predefinedFilters".equals(child.getNodeName())) {
                String defaultValue = parser.getStringAttribute(child, "default");
                if (defaultValue != null) {
                    defaultPredefinedFilterName = defaultValue;
                }
                NodeList predefinedFilterChildNodes = child.getChildNodes();
                parsePredefinedFilterChildNodes(predefinedFilterChildNodes, parser);
            } else if (RowStyleResolver.NODE_NAME.equals(child.getNodeName())) {
                rowStyleResolver = new RowStyleResolver(child, parser, getApplicationContext());
            } else if (CriteriaModifier.NODE_NAME.equals(child.getNodeName())) {
                criteriaModifier = new CriteriaModifier(child, parser, getApplicationContext());
            }
        }

    }

    private void parsePredefinedFilterChildNodes(final NodeList componentNodes, final ViewDefinitionParser parser)
            throws ViewDefinitionParserNodeException {
        for (int i = 0; i < componentNodes.getLength(); i++) {
            Node child = componentNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if ("predefinedFilter".equals(child.getNodeName())) {
                    PredefinedFilter predefinedFilter = new PredefinedFilter();
                    String predefinedFilterName = parser.getStringAttribute(child, "name");
                    predefinedFilter.setName(predefinedFilterName);

                    NodeList restrictionNodes = child.getChildNodes();
                    for (int restrictionNodesIndex = 0; restrictionNodesIndex < restrictionNodes
                            .getLength(); restrictionNodesIndex++) {
                        Node restrictionNode = restrictionNodes.item(restrictionNodesIndex);
                        if (restrictionNode.getNodeType() == Node.ELEMENT_NODE) {
                            if ("filterRestriction".equals(restrictionNode.getNodeName())) {
                                predefinedFilter.addFilterRestriction(parser.getStringAttribute(restrictionNode, L_COLUMN),
                                        parser.getStringAttribute(restrictionNode, "value"));
                            } else if ("filterOrder".equals(restrictionNode.getNodeName())) {
                                String column = parser.getStringAttribute(restrictionNode, L_COLUMN);
                                String direction = parser.getStringAttribute(restrictionNode, "direction");
                                if (column == null) {
                                    throw new ViewDefinitionParserNodeException(restrictionNode,
                                            "'filterOrder' tag must contain 'column' attribute");
                                }
                                if (direction == null) {
                                    direction = "asc";
                                } else {
                                    if (!("asc".equals(direction) || "desc".equals(direction))) {
                                        throw new ViewDefinitionParserNodeException(restrictionNode,
                                                "unknown order direction: " + direction);
                                    }
                                }
                                predefinedFilter.setOrderColumn(column);
                                predefinedFilter.setOrderDirection(direction);
                            } else {
                                throw new ViewDefinitionParserNodeException(restrictionNode,
                                        "predefinedFilter can only contain 'filterRestriction' or 'filterOrder' tag");
                            }
                        }
                    }

                    if (predefinedFilter.getOrderColumn() == null && defaultOrderColumn != null) {
                        predefinedFilter.setOrderColumn(defaultOrderColumn);
                        predefinedFilter.setOrderDirection(defaultOrderDirection);
                    }

                    predefinedFilters.put(predefinedFilterName, predefinedFilter);
                } else {
                    throwIllegalStateException("predefinedFilters can only contain 'predefinedFilter' tag");
                    throw new ViewDefinitionParserNodeException(child,
                            "predefinedFilters can only contain 'predefinedFilter' tag");
                }
            }
        }
    }

    private void parseOptions() {
        for (ComponentOption option : getOptions()) {
            if ("correspondingView".equals(option.getType())) {
                correspondingView = option.getValue();
            } else if ("correspondingComponent".equals(option.getType())) {
                correspondingComponent = option.getValue();
            } else if ("correspondingLookup".equals(option.getType())) {
                correspondingLookup = option.getValue();
            } else if ("correspondingViewInModal".equals(option.getType())) {
                correspondingViewInModal = Boolean.parseBoolean(option.getValue());
            } else if ("paginable".equals(option.getType())) {
                paginable = Boolean.parseBoolean(option.getValue());
            } else if ("creatable".equals(option.getType())) {
                creatable = Boolean.parseBoolean(option.getValue());
            } else if ("prioritizable".equals(option.getType())) {
                prioritizable = Boolean.parseBoolean(option.getValue());
            } else if ("onlyActive".equals(option.getType())) {
                onlyActive = Boolean.parseBoolean(option.getValue());
            } else if ("multiselect".equals(option.getType())) {
                multiselect = Boolean.parseBoolean(option.getValue());
            } else if ("hasPredefinedFilters".equals(option.getType())) {
                hasPredefinedFilters = Boolean.parseBoolean(option.getValue());
            } else if ("defaultPredefinedFilterName".equals(option.getType())) {
                defaultPredefinedFilterName = option.getValue();
            } else if ("filtersDefaultVisible".equals(option.getType())) {
                filtersDefaultVisible = Boolean.parseBoolean(option.getValue());
            } else if ("deletable".equals(option.getType())) {
                deletable = Boolean.parseBoolean(option.getValue());
            } else if ("deletableAuthorizationRole".equals(option.getType())) {
                deletableAuthorizationRole = option.getValue();
            } else if ("linkAuthorizationRole".equals(option.getType())) {
                linkAuthorizationRole = option.getValue();
            } else if ("height".equals(option.getType())) {
                height = Integer.parseInt(option.getValue());
            } else if (L_WIDTH.equals(option.getType())) {
                width = Integer.parseInt(option.getValue());
            } else if ("fullscreen".equals(option.getType())) {
                width = 0;
                height = 0;
            } else if ("lookup".equals(option.getType())) {
                lookup = Boolean.parseBoolean(option.getValue());
            } else if ("searchable".equals(option.getType())) {
                searchableColumns.addAll(parseColumns(option.getValue()));
            } else if ("multiSearch".equals(option.getType())) {
                multiSearchColumns.addAll(parseColumns(option.getValue()));
            } else if ("orderable".equals(option.getType())) {
                orderableColumns.addAll(parseColumns(option.getValue()));
            } else if ("order".equals(option.getType())) {
                defaultOrderColumn = option.getAttributeValue(L_COLUMN);
                defaultOrderDirection = option.getAttributeValue("direction");
                if (defaultOrderDirection == null) {
                    defaultOrderDirection = "asc";
                }
                if (predefinedFilters != null) {
                    for (PredefinedFilter predefinedFilter : predefinedFilters.values()) {
                        if (predefinedFilter.getOrderColumn() == null) {
                            predefinedFilter.setOrderColumn(defaultOrderColumn);
                            predefinedFilter.setOrderDirection(defaultOrderDirection);
                        }
                    }
                }
            } else if (L_COLUMN.equals(option.getType())) {
                parseColumnOption(option);
            } else if ("weakRelation".equals(option.getType())) {
                weakRelation = Boolean.parseBoolean(option.getValue());
            } else if ("fixedHeight".equals(option.getType())) {
                fixedHeight = Boolean.parseBoolean(option.getValue());
            } else if ("shrinkToFit".equals(option.getType())) {
                shrinkToFit = Boolean.parseBoolean(option.getValue());
            } else if ("autoRefresh".equals(option.getType())) {
                autoRefresh = Boolean.parseBoolean(option.getValue());
            } else if ("footerRow".equals(option.getType())) {
                footerRow = Boolean.parseBoolean(option.getValue());
            } else if ("columnsToSummary".equals(option.getType())) {
                columnsToSummary = option.getValue();
            } else if ("columnsToSummaryTime".equals(option.getType())) {
                columnsToSummaryTime = option.getValue();
            } else if ("suppressSelectEvent".equals(option.getType())) {
                suppressSelectEvent = Boolean.parseBoolean(option.getValue());
            }
        }
        if (defaultOrderColumn == null) {
            throwIllegalStateException("grid must contain 'order' option");
        }
    }

    private void parseColumnOption(final ComponentOption option) {
        GridComponentColumn column = new GridComponentColumn(option.getAttributeValue("name"));
        String fields = option.getAttributeValue("fields");
        if (fields != null) {
            for (FieldDefinition field : parseFields(fields, column)) {
                column.addField(field);
            }
        }
        column.setAuthorizationRole(parseColumnAuthorizationRole(option));
        column.setAlign(parseColumnAlignOption(option));
        column.setExpression(option.getAttributeValue("expression"));
        String columnWidth = option.getAttributeValue(L_WIDTH);
        if (columnWidth != null) {
            column.setWidth(Integer.valueOf(columnWidth));
        }
        if (option.getAttributeValue("link") != null) {
            column.setLink(Boolean.parseBoolean(option.getAttributeValue("link")));
        }
        if (option.getAttributeValue("hidden") != null) {
            column.setHidden(Boolean.parseBoolean(option.getAttributeValue("hidden")));
        }
        column.setClassesNames(option.getAttributeValue("classesNames"));
        column.setClassesCondition(option.getAttributeValue("classesCondition"));

        column.setCorrespondingView(option.getAttributeValue("correspondingView"));
        column.setCorrespondingField(option.getAttributeValue("correspondingField"));
        column.setCorrespondingViewField(option.getAttributeValue("correspondingViewField"));
        if (option.getAttributeValue("attachment") != null) {
            column.setAttachment(Boolean.parseBoolean(option.getAttributeValue("attachment")));
        }
        columns.put(column.getName(), column);
    }

    private Alignment parseColumnAlignOption(final ComponentOption options) {
        String alignStringVal = options.getAttributeValue("align");
        if (StringUtils.isNotEmpty(alignStringVal)) {
            return Alignment.parseString(alignStringVal);
        }
        return null;
    }

    private String parseColumnAuthorizationRole(ComponentOption option) {
        String optionAuthorizationRole = option.getAttributeValue("authorizationRole");
        if (StringUtils.isNotEmpty(optionAuthorizationRole)) {
            return optionAuthorizationRole;
        }
        return null;
    }

    private Set<String> parseColumns(final String columns) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, columns.split("\\s*,\\s*"));
        return set;
    }

    private Set<FieldDefinition> parseFields(final String fields, final GridComponentColumn column) {
        Set<FieldDefinition> set = new HashSet<FieldDefinition>();
        for (String field : fields.split("\\s*,\\s*")) {
            FieldDefinition fieldDefiniton = getDataDefinition().getField(field);
            if (fieldDefiniton == null) {
                throwIllegalStateException("field = " + field + " in option column = " + column.getName() + " doesn't exists");
            }
            set.add(fieldDefiniton);
        }
        return set;
    }

    private void throwIllegalStateException(final String message) {
        throw new IllegalStateException(getViewDefinition().getPluginIdentifier() + "." + getViewDefinition().getName() + "#"
                + getPath() + ": " + message);
    }

    public Set<String> getOrderableColumns() {
        return orderableColumns;
    }

    public boolean isWeakRelation() {
        return weakRelation;
    }

    public String getDefaultOrderColumn() {
        return defaultOrderColumn;
    }

    public String getDefaultOrderDirection() {
        return defaultOrderDirection;
    }

    public Map<String, GridComponentColumn> getColumns() {
        // FIXME MAKU -> KRNA: I think we should return an unmodifable (immutable) map or (if mutability were intended) pack them
        // into SynchronizedMap.
        return Maps.filterEntries(columns,
                Predicates.compose(COLUMNS_VISIBLE_FOR_TENANT_PREDICATE, VALUE_FROM_MAP_ENTRY_FUNCTION));
    }

    public boolean isActivable() {
        return activable;
    }

    public boolean isOnlyActive() {
        return onlyActive;
    }

    public FieldDefinition getBelongsToFieldDefinition() {
        return belongsToFieldDefinition;
    }

    public void setRowStyleResolver(final RowStyleResolver rowStyleResolver) {
        this.rowStyleResolver = rowStyleResolver;
    }

    public RowStyleResolver getRowStyleResolver() {
        return rowStyleResolver;
    }

    public void setCriteriaModifier(final CriteriaModifier criteriaModifier) {
        this.criteriaModifier = criteriaModifier;
    }

    public CriteriaModifier getCriteriaModifier() {
        return criteriaModifier;
    }

    public void setFixedHeight(final Boolean fixedHeight) {
        this.fixedHeight = fixedHeight;
    }

    public PredefinedFilter getDefaultPredefinedFilter() {
        return predefinedFilters.get(defaultPredefinedFilterName);
    }

    public SecurityRole getAuthorizationRole() {
        return authorizationRole;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public String getDeletableAuthorizationRole() {
        return deletableAuthorizationRole;
    }

    public String getLinkAuthorizationRole() {
        return linkAuthorizationRole;
    }

    public boolean isautoRefresh() {
        return autoRefresh;
    }

    public boolean isFooterRow() {
        return footerRow;
    }

    public String getColumnsToSummary() {
        return columnsToSummary;
    }

    public String getColumnsToSummaryTime() {
        return columnsToSummaryTime;
    }

    private boolean isLinkAllowed() {
        return Strings.isNullOrEmpty(linkAuthorizationRole) || securityRolesService.canAccess(linkAuthorizationRole);
    }
}
