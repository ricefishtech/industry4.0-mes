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

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.ImmutableMap;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.JoinType;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchRestrictions.SearchMatchMode;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.api.types.FieldType;

import junit.framework.Assert;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SearchRestrictions.class)
public class GridComponentFilterUtilsTest {

    private static final String TEST_COL = "testCol";

    private static final String TEST_FIELD = "testField";

    @Mock
    private DataDefinition dataDefinition;

    @Mock
    private SearchCriteriaBuilder criteria;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(SearchRestrictions.class);
    }

    @Test
    public final void shouldFilterColumnWithIntegerValues() throws GridComponentFilterException {
        // when
        performFiltering("3", Integer.class);

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq(TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, "3");

        Mockito.verify(criteria).add(SearchRestrictions.eq(TEST_FIELD, 3));
    }

    @Test
    public final void shouldNotFilterColumnWithIntegerValuesIfFilterIsBlank() throws GridComponentFilterException {
        // when
        performFiltering(" ", Integer.class);

        // then
        verify(criteria, never()).add(SearchRestrictions.eq(Mockito.eq(TEST_FIELD), Mockito.any()));
    }

    @Test
    public final void shouldThrowExceptionForIncorenctFilterValueForIntegerColumn() throws GridComponentFilterException {
        try {
            performFiltering("aaa", Integer.class);
            Assert.fail();
        } catch (GridComponentFilterException gcfe) {
            // success
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldFilterColumnWithBigDecimalValues() throws GridComponentFilterException {
        // when
        performFiltering("3.14", BigDecimal.class);

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq(TEST_FIELD, new BigDecimal("3.14"));

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, "3.14");

        verify(criteria).add(SearchRestrictions.eq(TEST_FIELD, new BigDecimal("3.14")));
    }

    @Test
    public final void shouldNotFilterColumnWithBigDecimalValuesIfFilterIsBlank() throws GridComponentFilterException {
        // when
        performFiltering(" ", BigDecimal.class);

        // then
        verify(criteria, never()).add(SearchRestrictions.eq(Mockito.eq(TEST_FIELD), Mockito.any()));
    }

    @Test
    public final void shouldThrowExceptionForIncorenctFilterValueForBigDecimalColumn() throws GridComponentFilterException {
        try {
            performFiltering("aaa", BigDecimal.class);
            Assert.fail();
        } catch (GridComponentFilterException gcfe) {
            // success
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldFilterColumnWithStringValues() throws GridComponentFilterException {
        // when
        performFiltering("someValue", String.class);

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.ilike(TEST_FIELD, "someValue", SearchMatchMode.ANYWHERE);

        verify(criteria).add(SearchRestrictions.ilike(TEST_FIELD, "someValue", SearchMatchMode.ANYWHERE));
    }

    @Test
    public final void shouldNotFilterColumnWithStringValuesIfFilterIsBlank() throws GridComponentFilterException {
        // when
        performFiltering(" ", String.class);

        // then
        verify(criteria, never()).add(
                SearchRestrictions.ilike(Mockito.eq(TEST_FIELD), Mockito.anyString(), Mockito.any(SearchMatchMode.class)));
    }

    @Test
    public final void shouldReturnFieldNameUsingFieldNameAttribute() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals(TEST_FIELD, fieldName);

    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithSquareBracket() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo['field']"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithSquareBracketAndOneGet() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo['secondBelongsTo'].get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithSquareBracketAndTwoGet() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(
                TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition,
                        "#belongsTo['secondBelongsTo'].get('thirdBelongsTo').get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.thirdBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGet() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetStringField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getStringField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetBooleanField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getBooleanField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetDecimalField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getDecimalField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetIntegerField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getIntegerField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetDateField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getDateField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetBelongsToField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getBelongsToField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetHasManyField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getHasManyField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.getHasManyField('field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetManyToManyField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getManyToManyField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.getManyToManyField('field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingSimpleExpressionWithOneGetTreeField() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.getTreeField('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.getTreeField('field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithTwoGet() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo.get('secondBelongsTo').get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithThreeGet() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(
                TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition,
                        "#belongsTo.get('secondBelongsTo').get('thirdBelongsTo').get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.thirdBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithSaveNavOperator1() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(
                TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition,
                        "#belongsTo?.get('secondBelongsTo').get('thirdBelongsTo').get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.thirdBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithSaveNavOperator2() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(
                TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition,
                        "#belongsTo['secondBelongsTo']?.get('thirdBelongsTo').get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.thirdBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithSaveNavOperator3() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(
                TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition,
                        "#belongsTo['secondBelongsTo']?.get('thirdBelongsTo')?.get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.thirdBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithSaveNavOperator4() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(
                TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition,
                        "#belongsTo['secondBelongsTo'].get('thirdBelongsTo')?.get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.thirdBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnFieldNameUsingExpressionWithSaveNavOperator5() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(
                TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition,
                        "#belongsTo?.get('secondBelongsTo').get('thirdBelongsTo')?.get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertEquals("belongsTo.secondBelongsTo.thirdBelongsTo.field", fieldName);
    }

    @Test
    public final void shouldReturnNullForUnsupportedExpression1() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(
                TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition,
                        "#belongsTo['secondBelongsTo'].get('thirdBelongsTo')[0].get('field')"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertNull(fieldName);
    }

    @Test
    public final void shouldReturnNullForUnsupportedExpression2() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "'some' + ' concatenated ' + 'value'"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertNull(fieldName);
    }

    @Test
    public final void shouldReturnNullForUnsupportedExpression3() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo['field1'] + ' ' + #belongsTo['field2']"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertNull(fieldName);
    }

    @Test
    public final void shouldReturnNullForUnsupportedExpression4() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "#belongsTo == null ? '' : #belongsTo['field']"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertNull(fieldName);
    }

    @Test
    public final void shouldReturnNullForUnsupportedExpression5() {
        // given
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Object.class);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL,
                buildGridComponentColumn(TEST_COL, fieldDefinition, "T(SomeHelper).nullToBlank(#belongsTo['field'])"));

        // when
        final String fieldName = GridComponentFilterUtils.getFieldNameByColumnName(columns, TEST_COL);

        // then
        assertNull(fieldName);
    }

    @Test
    public final void shouldReturnFieldDefinitionForSimplePath() {
        // given
        FieldDefinition fieldDefinition = mockFieldDefinition("fieldName", String.class);
        String field = "fieldName";

        // when
        final FieldDefinition res = GridComponentFilterUtils.getFieldDefinition(dataDefinition, field);

        // then
        assertEquals(fieldDefinition, res);
    }

    @Test
    public final void shouldReturnFieldDefinitionForOneLevelDeepPath() {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("firstBelongsTo", dataDefinition, firstBtDataDef);

        FieldDefinition fieldDefinition = mockFieldDefinition("fieldName", String.class, firstBtDataDef);
        String field = "firstBelongsTo.fieldName";

        // when
        final FieldDefinition res = GridComponentFilterUtils.getFieldDefinition(dataDefinition, field);

        // then
        assertEquals(fieldDefinition, res);
    }

    @Test
    public final void shouldReturnFieldDefinitionForTwoLevelsDeepPath() {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("firstBelongsTo", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBelongsTo", firstBtDataDef, secondBtDataDef);

        FieldDefinition fieldDefinition = mockFieldDefinition("fieldName", String.class, secondBtDataDef);
        String field = "firstBelongsTo.secondBelongsTo.fieldName";

        // when
        final FieldDefinition res = GridComponentFilterUtils.getFieldDefinition(dataDefinition, field);

        // then
        assertEquals(fieldDefinition, res);
    }

    @Test
    public final void shouldReturnFieldDefinitionForThreeLevelsDeepPath() {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("firstBelongsTo", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBelongsTo", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBelongsTo", secondBtDataDef, thirdBtDataDef);

        FieldDefinition fieldDefinition = mockFieldDefinition("fieldName", String.class, thirdBtDataDef);
        String field = "firstBelongsTo.secondBelongsTo.thirdBelongsTo.fieldName";

        // when
        final FieldDefinition res = GridComponentFilterUtils.getFieldDefinition(dataDefinition, field);

        // then
        assertEquals(fieldDefinition, res);
    }

    @Test
    public final void shouldReturnFieldDefinitionForFourLevelsDeepPath() {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("firstBelongsTo", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBelongsTo", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBelongsTo", secondBtDataDef, thirdBtDataDef);

        DataDefinition fourthBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("fourthBelongsTo", thirdBtDataDef, fourthBtDataDef);

        FieldDefinition fieldDefinition = mockFieldDefinition("fieldName", String.class, fourthBtDataDef);
        String field = "firstBelongsTo.secondBelongsTo.thirdBelongsTo.fourthBelongsTo.fieldName";

        // when
        final FieldDefinition res = GridComponentFilterUtils.getFieldDefinition(dataDefinition, field);

        // then
        assertEquals(fieldDefinition, res);
    }

    @Test
    public final void shouldNotCreateAliasForSimplePath() {
        // given
        String field = "fieldName";

        // when
        GridComponentFilterUtils.addAliases(criteria, field, JoinType.LEFT);

        // then
        verify(criteria, never()).createAlias(Mockito.anyString(), Mockito.anyString(), Mockito.any(JoinType.class));
    }

    @Test
    public final void shouldCreateAliasForOneLevelDeepPath() {
        // given
        String field = "firstBelongsTo.fieldName";

        // when
        GridComponentFilterUtils.addAliases(criteria, field, JoinType.LEFT);

        // then
        verify(criteria).createAlias("firstBelongsTo", "firstBelongsTo_a", JoinType.LEFT);
    }

    @Test
    public final void shouldCreateAliasForTwoLevelDeepPath() {
        // given
        String field = "firstBelongsTo.secondBelongsTo.fieldName";

        // when
        GridComponentFilterUtils.addAliases(criteria, field, JoinType.LEFT);

        // then
        verify(criteria).createAlias("firstBelongsTo", "firstBelongsTo_a", JoinType.LEFT);
        verify(criteria).createAlias("firstBelongsTo_a.secondBelongsTo", "secondBelongsTo_a", JoinType.LEFT);
    }

    @Test
    public final void shouldCreateAliasForThreeLevelDeepPath() {
        // given
        String field = "firstBelongsTo.secondBelongsTo.thirdBelongsTo.fieldName";

        // when
        GridComponentFilterUtils.addAliases(criteria, field, JoinType.LEFT);

        // then
        verify(criteria).createAlias("firstBelongsTo", "firstBelongsTo_a", JoinType.LEFT);
        verify(criteria).createAlias("firstBelongsTo_a.secondBelongsTo", "secondBelongsTo_a", JoinType.LEFT);
        verify(criteria).createAlias("secondBelongsTo_a.thirdBelongsTo", "thirdBelongsTo_a", JoinType.LEFT);
    }

    @Test
    public final void shouldCreateAliasForFourLevelDeepPath() {
        // given
        String field = "firstBelongsTo.secondBelongsTo.thirdBelongsTo.fourthBelongsTo.fieldName";

        // when
        GridComponentFilterUtils.addAliases(criteria, field, JoinType.LEFT);

        // then
        verify(criteria).createAlias("firstBelongsTo", "firstBelongsTo_a", JoinType.LEFT);
        verify(criteria).createAlias("firstBelongsTo_a.secondBelongsTo", "secondBelongsTo_a", JoinType.LEFT);
        verify(criteria).createAlias("secondBelongsTo_a.thirdBelongsTo", "thirdBelongsTo_a", JoinType.LEFT);
        verify(criteria).createAlias("thirdBelongsTo_a.fourthBelongsTo", "fourthBelongsTo_a", JoinType.LEFT);
    }

    @Test
    public final void shouldFilterColumnWithSimplePathInExpression() throws GridComponentFilterException {
        // given
        FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, Integer.class);

        // when
        performFiltering("3", buildGridComponentColumn(TEST_COL, fieldDefinition, "#" + TEST_FIELD));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq(TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithOneLevelDeepPathInExpressionWithBrackets() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, firstBtDataDef);

        // when
        performFiltering("3", buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt['" + TEST_FIELD + "']"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithTwoLevelsDeepPathInExpressionWithBrackets() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, secondBtDataDef);

        // when
        performFiltering("3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt['secondBt'].get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithThreeLevelsDeepPathInExpressionWithBrackets() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBt", secondBtDataDef, thirdBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, thirdBtDataDef);

        // when
        performFiltering(
                "3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt['secondBt'].get('thirdBt').get('" + TEST_FIELD
                        + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("thirdBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithFourLevelsDeepPathInExpressionWithBrackets() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBt", secondBtDataDef, thirdBtDataDef);

        DataDefinition fourthBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("fourthBt", thirdBtDataDef, fourthBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, fourthBtDataDef);

        // when
        performFiltering(
                "3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt['secondBt'].get('thirdBt').get('fourthBt').get('"
                        + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("fourthBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("thirdBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithOneLevelDeepPathInExpression() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, firstBtDataDef);

        // when
        performFiltering("3", buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt.get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithTwoLevelsDeepPathInExpression() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, secondBtDataDef);

        // when
        performFiltering("3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt.get('secondBt').get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithThreeLevelsDeepPathInExpression() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBt", secondBtDataDef, thirdBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, thirdBtDataDef);

        // when
        performFiltering(
                "3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt.get('secondBt').get('thirdBt').get('" + TEST_FIELD
                        + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("thirdBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithFourLevelsDeepPathInExpression() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBt", secondBtDataDef, thirdBtDataDef);

        DataDefinition fourthBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("fourthBt", thirdBtDataDef, fourthBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, fourthBtDataDef);

        // when
        performFiltering(
                "3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef,
                        "#firstBt.get('secondBt').get('thirdBt').get('fourthBt').get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("fourthBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("thirdBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldNotFilterColumnWithOneLevelDeepPathInExpressionWithBracketsAndSafetyNavOp()
            throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, firstBtDataDef);

        // when
        performFiltering("3", buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt?['" + TEST_FIELD + "']"));

        // then
        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithTwoLevelsDeepPathInExpressionWithBracketsAndSafetyNavOp()
            throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, secondBtDataDef);

        // when
        performFiltering("3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt['secondBt']?.get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithThreeLevelsDeepPathInExpressionWithBracketsAndSafetyNavOp()
            throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBt", secondBtDataDef, thirdBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, thirdBtDataDef);

        // when
        performFiltering(
                "3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt['secondBt']?.get('thirdBt')?.get('" + TEST_FIELD
                        + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("thirdBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithFourLevelsDeepPathInExpressionWithBracketsAndSafetyNavOp()
            throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBt", secondBtDataDef, thirdBtDataDef);

        DataDefinition fourthBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("fourthBt", thirdBtDataDef, fourthBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, fourthBtDataDef);

        // when
        performFiltering(
                "3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef,
                        "#firstBt['secondBt']?.get('thirdBt')?.get('fourthBt')?.get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("fourthBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("thirdBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithOneLevelDeepPathInExpressionWithSafetyNavOp() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, firstBtDataDef);

        // when
        performFiltering("3", buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt?.get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithTwoLevelsDeepPathInExpressionWithSafetyNavOp() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, secondBtDataDef);

        // when
        performFiltering("3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt?.get('secondBt')?.get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithThreeLevelsDeepPathInExpressionWithSafetyNavOp() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBt", secondBtDataDef, thirdBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, thirdBtDataDef);

        // when
        performFiltering(
                "3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef, "#firstBt?.get('secondBt')?.get('thirdBt')?.get('"
                        + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("thirdBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);
    }

    @Test
    public final void shouldFilterColumnWithFourLevelsDeepPathInExpressionWithSafetyNavOp() throws GridComponentFilterException {
        // given
        DataDefinition firstBtDataDef = mock(DataDefinition.class);
        FieldDefinition firstBtFieldDef = mockBelongsToField("firstBt", dataDefinition, firstBtDataDef);

        DataDefinition secondBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("secondBt", firstBtDataDef, secondBtDataDef);

        DataDefinition thirdBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("thirdBt", secondBtDataDef, thirdBtDataDef);

        DataDefinition fourthBtDataDef = mock(DataDefinition.class);
        mockBelongsToField("fourthBt", thirdBtDataDef, fourthBtDataDef);

        mockFieldDefinition(TEST_FIELD, Integer.class, fourthBtDataDef);

        // when
        performFiltering(
                "3",
                buildGridComponentColumn(TEST_COL, firstBtFieldDef,
                        "#firstBt?.get('secondBt')?.get('thirdBt')?.get('fourthBt')?.get('" + TEST_FIELD + "')"));

        // then
        PowerMockito.verifyStatic();
        SearchRestrictions.eq("fourthBt_a." + TEST_FIELD, 3);

        PowerMockito.verifyStatic(never());
        SearchRestrictions.eq(TEST_FIELD, 3);
        SearchRestrictions.eq("firstBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("secondBt_a." + TEST_FIELD, 3);
        SearchRestrictions.eq("thirdBt_a." + TEST_FIELD, 3);
    }

    private void performFiltering(final String filterValue, final Class<?> clazz) throws GridComponentFilterException {
        final FieldDefinition fieldDefinition = mockFieldDefinition(TEST_FIELD, clazz);
        performFiltering(filterValue, buildGridComponentColumn(TEST_COL, fieldDefinition));
    }

    private void performFiltering(final String filterValue, final GridComponentColumn gridComponentColumn)
            throws GridComponentFilterException {
        final Map<String, String> filters = ImmutableMap.of(TEST_COL, filterValue);
        final Map<String, GridComponentColumn> columns = ImmutableMap.of(TEST_COL, gridComponentColumn);

        GridComponentFilterUtils.addFilters(filters, columns, dataDefinition, criteria);
    }

    private FieldDefinition mockBelongsToField(final String fieldName, final DataDefinition sourceDataDefinition,
            final DataDefinition targetDataDefinition) {
        final FieldDefinition belongsToField = mock(FieldDefinition.class);
        given(belongsToField.getName()).willReturn(fieldName);

        final BelongsToType belongsToType = mock(BelongsToType.class);
        given(belongsToField.getType()).willReturn(belongsToType);
        given(belongsToType.getDataDefinition()).willReturn(targetDataDefinition);

        given(sourceDataDefinition.getField(fieldName)).willReturn(belongsToField);

        return belongsToField;
    }

    private FieldDefinition mockFieldDefinition(final String fieldName, @SuppressWarnings("rawtypes") final Class typeClass) {
        return mockFieldDefinition(fieldName, typeClass, null);
    }

    @SuppressWarnings("unchecked")
    private FieldDefinition mockFieldDefinition(final String fieldName, @SuppressWarnings("rawtypes") final Class typeClass,
            final DataDefinition dataDefinition) {
        final FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        given(fieldDefinition.getName()).willReturn(fieldName);

        final FieldType fieldType = mock(FieldType.class);
        given(fieldType.getType()).willReturn(typeClass);
        given(fieldDefinition.getType()).willReturn(fieldType);

        DataDefinition ddMock = this.dataDefinition;
        if (dataDefinition != null) {
            ddMock = dataDefinition;
        }
        given(ddMock.getField(fieldName)).willReturn(fieldDefinition);

        return fieldDefinition;
    }

    private GridComponentColumn buildGridComponentColumn(final String name, final FieldDefinition fieldDefinition) {
        return buildGridComponentColumn(name, fieldDefinition, null);
    }

    private GridComponentColumn buildGridComponentColumn(final String name, final FieldDefinition fieldDefinition,
            final String expression) {
        final GridComponentColumn gridComponentColumn = new GridComponentColumn(name);
        gridComponentColumn.addField(fieldDefinition);
        if (expression != null) {
            gridComponentColumn.setExpression(expression);
        }
        return gridComponentColumn;

    }

}
