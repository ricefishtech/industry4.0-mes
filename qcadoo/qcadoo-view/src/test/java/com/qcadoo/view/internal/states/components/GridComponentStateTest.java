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
package com.qcadoo.view.internal.states.components;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableMap;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.api.types.HasManyType;
import com.qcadoo.model.internal.DefaultEntity;
import com.qcadoo.model.internal.ExpressionServiceImpl;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.internal.FieldEntityIdChangeListener;
import com.qcadoo.view.internal.components.grid.GridComponentColumn;
import com.qcadoo.view.internal.components.grid.GridComponentOrderColumn;
import com.qcadoo.view.internal.components.grid.GridComponentPattern;
import com.qcadoo.view.internal.components.grid.GridComponentState;
import com.qcadoo.view.internal.states.AbstractComponentState;
import com.qcadoo.view.internal.states.AbstractStateTest;

public class GridComponentStateTest extends AbstractStateTest {

    private Entity entity;

    private ViewDefinitionState viewDefinitionState;

    private GridComponentState grid;

    private DataDefinition productDataDefinition;

    private DataDefinition substituteDataDefinition;

    private FieldDefinition substitutesFieldDefinition;

    private TranslationService translationService;

    private JSONObject json;

    private Map<String, GridComponentColumn> columns;

    private SearchCriteriaBuilder substituteCriteria;

    @Before
    public void init() throws Exception {
        JSONObject jsonContent = new JSONObject();
        jsonContent.put(GridComponentState.JSON_SELECTED_ENTITY_ID, 13L);
        jsonContent.put(GridComponentState.JSON_MULTISELECT_MODE, false);
        JSONObject jsonSelected = new JSONObject();
        jsonSelected.put("13", true);
        jsonContent.put(GridComponentState.JSON_SELECTED_ENTITIES, jsonSelected);
        jsonContent.put(GridComponentState.JSON_BELONGS_TO_ENTITY_ID, 1L);
        jsonContent.put(GridComponentState.JSON_FIRST_ENTITY, 60);
        jsonContent.put(GridComponentState.JSON_MAX_ENTITIES, 30);
        jsonContent.put(GridComponentState.JSON_FILTERS_ENABLED, true);

        JSONArray jsonOrder = new JSONArray();
        jsonOrder.put(ImmutableMap.of("column", "asd", "direction", "asc"));

        jsonContent.put(GridComponentState.JSON_ORDER, jsonOrder);

        JSONObject jsonFilters = new JSONObject();
        jsonFilters.put("asd", "test");
        jsonFilters.put("qwe", "test2");

        jsonContent.put(GridComponentState.JSON_FILTERS, jsonFilters);

        json = new JSONObject(Collections.singletonMap(AbstractComponentState.JSON_CONTENT, jsonContent));

        entity = mock(Entity.class);
        given(entity.getField("name")).willReturn("text");

        viewDefinitionState = mock(ViewDefinitionState.class);

        productDataDefinition = mock(DataDefinition.class, RETURNS_DEEP_STUBS);
        substituteDataDefinition = mock(DataDefinition.class, "substituteDataDefinition");

        HasManyType substitutesFieldType = mock(HasManyType.class);
        given(substitutesFieldType.getDataDefinition()).willReturn(substituteDataDefinition);
        given(substitutesFieldType.getJoinFieldName()).willReturn("product");

        substitutesFieldDefinition = mock(FieldDefinition.class);
        given(substitutesFieldDefinition.getType()).willReturn(substitutesFieldType);
        given(substitutesFieldDefinition.getName()).willReturn("substitutes");
        given(substitutesFieldDefinition.getDataDefinition()).willReturn(substituteDataDefinition);

        substituteCriteria = mock(SearchCriteriaBuilder.class);

        given(substituteDataDefinition.getPluginIdentifier()).willReturn("plugin");
        given(substituteDataDefinition.getName()).willReturn("substitute");
        given(substituteDataDefinition.find()).willReturn(substituteCriteria);

        given(productDataDefinition.getPluginIdentifier()).willReturn("plugin");
        given(productDataDefinition.getName()).willReturn("product");
        given(productDataDefinition.getField("substitutes")).willReturn(substitutesFieldDefinition);

        columns = new LinkedHashMap<String, GridComponentColumn>();

        translationService = mock(TranslationService.class);
        given(translationService.translate(Mockito.anyString(), Mockito.any(Locale.class))).willReturn("i18n");
        given(translationService.translate(Mockito.anyString(), Mockito.anyString(), Mockito.any(Locale.class))).willReturn(
                "i18n");
        given(translationService.translate(Mockito.anyString(), Mockito.any(Locale.class))).willReturn("i18n");

        GridComponentPattern pattern = mock(GridComponentPattern.class);
        given(pattern.getColumns()).willReturn(columns);
        given(pattern.getBelongsToFieldDefinition()).willReturn(substitutesFieldDefinition);
        given(pattern.isActivable()).willReturn(false);
        given(pattern.isWeakRelation()).willReturn(false);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        setField(pattern, "applicationContext", applicationContext);
        SecurityRolesService securityRolesService = mock(SecurityRolesService.class);
        given(applicationContext.getBean(SecurityRolesService.class)).willReturn(securityRolesService);
        grid = new GridComponentState(productDataDefinition, pattern);
        grid.setDataDefinition(substituteDataDefinition);
        grid.setTranslationService(translationService);

        new ExpressionServiceImpl().init();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldInitialize() throws Exception {
        // when
        grid.initialize(json, Locale.ENGLISH);

        // then
        assertEquals(substitutesFieldDefinition, getField(grid, "belongsToFieldDefinition"));
        assertEquals(13L, getField(grid, "selectedEntityId"));
        assertEquals(1L, getField(grid, "belongsToEntityId"));
        assertNull(getField(grid, "entities"));
        assertEquals(0, getField(grid, "totalEntities"));
        assertEquals(60, getField(grid, "firstResult"));
        assertEquals(30, getField(grid, "maxResults"));
        assertTrue((Boolean) getField(grid, "filtersEnabled"));

        List<GridComponentOrderColumn> orderColumns = (List<GridComponentOrderColumn>) getField(grid, "orderColumns");
        assertEquals("asd", orderColumns.get(0).getName());
        assertEquals("asc", orderColumns.get(0).getDirection());

        Map<String, String> filters = (Map<String, String>) getField(grid, "filters");

        assertEquals(2, filters.size());
        assertEquals("test", filters.get("asd"));
        assertEquals("test2", filters.get("qwe"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldInitializeWithoutData() throws Exception {
        // given
        GridComponentPattern pattern = mock(GridComponentPattern.class);
        given(pattern.getColumns()).willReturn(columns);
        given(pattern.getBelongsToFieldDefinition()).willReturn(null);
        given(pattern.isActivable()).willReturn(false);
        given(pattern.isWeakRelation()).willReturn(false);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        setField(pattern, "applicationContext", applicationContext);
        SecurityRolesService securityRolesService = mock(SecurityRolesService.class);
        given(applicationContext.getBean(SecurityRolesService.class)).willReturn(securityRolesService);
        grid = new GridComponentState(productDataDefinition, pattern);
        grid.setDataDefinition(substituteDataDefinition);

        JSONObject json = new JSONObject(Collections.singletonMap(AbstractComponentState.JSON_CONTENT, new JSONObject()));

        // when
        grid.initialize(json, Locale.ENGLISH);

        // then
        assertNull(getField(grid, "belongsToFieldDefinition"));
        assertNull(getField(grid, "selectedEntityId"));
        assertNull(getField(grid, "belongsToEntityId"));
        assertNull(getField(grid, "entities"));
        assertEquals(0, getField(grid, "totalEntities"));
        assertEquals(0, getField(grid, "firstResult"));
        assertEquals(Integer.MAX_VALUE, getField(grid, "maxResults"));
        assertTrue((Boolean) getField(grid, "filtersEnabled"));

        List<GridComponentOrderColumn> orderColumns = (List<GridComponentOrderColumn>) getField(grid, "orderColumns");

        assertEquals(0, orderColumns.size());

        Map<String, String> filters = (Map<String, String>) getField(grid, "filters");

        assertEquals(0, filters.size());
    }

    @Test
    public void shouldSelectEntity() throws Exception {
        // given
        FieldEntityIdChangeListener listener = mock(FieldEntityIdChangeListener.class);
        SearchResult result = mock(SearchResult.class);
        given(substituteCriteria.list()).willReturn(result);
        given(result.getTotalNumberOfEntities()).willReturn(0);
        given(result.getEntities()).willReturn(Collections.<Entity> emptyList());
        grid.initialize(json, Locale.ENGLISH);
        grid.addFieldEntityIdChangeListener("field", listener);

        // when
        grid.performEvent(viewDefinitionState, "select", new String[0]);

        // then
        verify(listener).onFieldEntityIdChange(13L);
    }

    @Test
    public void shouldRefresh() throws Exception {
        // given
        FieldEntityIdChangeListener listener = mock(FieldEntityIdChangeListener.class);
        SearchResult result = mock(SearchResult.class);
        given(substituteCriteria.list()).willReturn(result);
        given(result.getTotalNumberOfEntities()).willReturn(0);
        given(result.getEntities()).willReturn(Collections.<Entity> emptyList());
        grid.initialize(json, Locale.ENGLISH);
        grid.addFieldEntityIdChangeListener("field", listener);

        // when
        grid.performEvent(viewDefinitionState, "refresh", new String[0]);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotModeUpSelectedEntityOnFail() throws Exception {
        // given
        FieldEntityIdChangeListener listener = mock(FieldEntityIdChangeListener.class);
        SearchResult result = mock(SearchResult.class);
        given(substituteCriteria.list()).willReturn(result);
        given(result.getTotalNumberOfEntities()).willReturn(0);
        given(result.getEntities()).willReturn(Collections.<Entity> emptyList());
        willThrow(new IllegalStateException()).given(substituteDataDefinition).move(13L, -1);
        grid.initialize(json, Locale.ENGLISH);
        grid.addFieldEntityIdChangeListener("field", listener);

        // when
        grid.performEvent(viewDefinitionState, "moveUp", new String[0]);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotModeDownSelectedEntityOnFail() throws Exception {
        // given
        FieldEntityIdChangeListener listener = mock(FieldEntityIdChangeListener.class);
        SearchResult result = mock(SearchResult.class);
        given(substituteCriteria.list()).willReturn(result);
        given(result.getTotalNumberOfEntities()).willReturn(0);
        given(result.getEntities()).willReturn(Collections.<Entity> emptyList());
        willThrow(new IllegalStateException()).given(substituteDataDefinition).move(13L, 1);
        grid.initialize(json, Locale.ENGLISH);
        grid.addFieldEntityIdChangeListener("field", listener);

        // when
        grid.performEvent(viewDefinitionState, "moveDown", new String[0]);
    }

    @Test
    public void shouldGetValueUsingExpression() throws Exception {
        // given
        FieldDefinition nameFieldDefinition = mock(FieldDefinition.class);
        given(productDataDefinition.getField("name")).willReturn(nameFieldDefinition);

        FieldType nameFieldType = mock(FieldType.class);
        given(nameFieldDefinition.getType()).willReturn(nameFieldType);

        given(nameFieldType.toString(anyString(), any(Locale.class))).willAnswer(
                invocation -> Objects.toString(invocation.getArguments()[0]));

        GridComponentColumn column = new GridComponentColumn("name");
        column.setExpression("#name + ' ' + #id");

        Entity entity = new DefaultEntity(productDataDefinition, 13L, ImmutableMap.of("name", (Object) "John"));

        // when
        String value = column.getValue(entity, Locale.ENGLISH);

        // then
        assertEquals("John 13", value);
    }

    @Test
    public void shouldGetValueUsingField() throws Exception {
        // given
        FieldDefinition field = mock(FieldDefinition.class);
        given(field.getName()).willReturn("name");
        given(field.getValue("John", Locale.ENGLISH)).willReturn("Johny");

        GridComponentColumn column = new GridComponentColumn("name");
        column.addField(field);

        Entity entity = new DefaultEntity(productDataDefinition, 13L, ImmutableMap.of("name", (Object) "John"));

        // when
        String value = column.getValue(entity, Locale.ENGLISH);

        // then
        assertEquals("Johny", value);
    }

    @Test
    public void shouldGetValueUsingFields() throws Exception {
        // given
        FieldDefinition field1 = mock(FieldDefinition.class);
        given(field1.getName()).willReturn("name");
        given(field1.getValue("John", Locale.ENGLISH)).willReturn("Johny");

        FieldDefinition field2 = mock(FieldDefinition.class);
        given(field2.getName()).willReturn("lastname");
        given(field2.getValue("Smith", Locale.ENGLISH)).willReturn("Smithy");

        GridComponentColumn column = new GridComponentColumn("name");
        column.addField(field1);
        column.addField(field2);

        Entity entity = new DefaultEntity(productDataDefinition, 13L, ImmutableMap.of("name", (Object) "John", "lastname",
                (Object) "Smith"));

        // when
        String value = column.getValue(entity, Locale.ENGLISH);

        // then
        assertEquals("Johny, Smithy", value);
    }

}
