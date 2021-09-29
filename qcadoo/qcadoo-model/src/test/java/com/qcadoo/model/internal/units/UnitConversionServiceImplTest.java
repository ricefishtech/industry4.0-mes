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
package com.qcadoo.model.internal.units;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.NumberService;
import com.qcadoo.model.api.units.PossibleUnitConversions;
import com.qcadoo.model.api.units.UnitConversionModelService;
import com.qcadoo.model.constants.UnitConversionItemFields;

public class UnitConversionServiceImplTest {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    private UnitConversionServiceImpl unitConversionService;

    @Mock
    private UnitConversionModelService unitConversionModelService;

    @Mock
    private DataDefinition unitConversionItemDD;

    @Mock
    private DictionaryService dictionaryService;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);
        unitConversionService = new UnitConversionServiceImpl();

        final NumberService numberService = mock(NumberService.class);
        given(numberService.getMathContext()).willReturn(MATH_CONTEXT);
        given(unitConversionModelService.getDataDefinition()).willReturn(unitConversionItemDD);
        ReflectionTestUtils.setField(unitConversionService, "numberService", numberService);
        ReflectionTestUtils.setField(unitConversionService, "unitConversionModelService", unitConversionModelService);
        ReflectionTestUtils.setField(unitConversionService, "dictionaryService", dictionaryService);
    }

    private void stubGetAll(final List<Entity> unitConversionItems) {
        given(unitConversionModelService.find(Mockito.anyString())).willReturn(unitConversionItems);
    }

    private Entity mockUnitConversionItem(final BigDecimal quantityFrom, final String unitFrom, final BigDecimal quantityTo,
            final String unitTo) {
        final Entity unitConversionItem = mock(Entity.class);
        given(unitConversionItem.getStringField(UnitConversionItemFields.UNIT_FROM)).willReturn(unitFrom);
        given(unitConversionItem.getStringField(UnitConversionItemFields.UNIT_TO)).willReturn(unitTo);
        given(unitConversionItem.getDecimalField(UnitConversionItemFields.QUANTITY_FROM)).willReturn(quantityFrom);
        given(unitConversionItem.getDecimalField(UnitConversionItemFields.QUANTITY_TO)).willReturn(quantityTo);

        return unitConversionItem;
    }

    private void assertBigDecimalEquals(final BigDecimal expected, final BigDecimal actual) {
        if (expected.compareTo(actual) != 0) {
            Assert.fail("expected " + expected + " but actual value is " + actual);
        }
    }

    @Test
    public final void shouldReturnEmptyConversionsSet() {
        // given
        stubGetAll(Collections.<Entity> emptyList());

        // when
        final PossibleUnitConversions result = unitConversionService.getPossibleConversions("m");

        // then
        assertTrue(result.isEmpty());

    }

    @Test
    @Ignore
    // TODO MAKU
    public final void shouldReturnNonEmptyConversionsSetUsingSimpleOneDirectionalTraverse() {
        // given
        final Entity mToDm = mockUnitConversionItem(BigDecimal.ONE, "m", BigDecimal.valueOf(10L), "dm");
        final Entity dmToCm = mockUnitConversionItem(BigDecimal.ONE, "dm", BigDecimal.valueOf(10L), "cm");
        final Entity cmToMm = mockUnitConversionItem(BigDecimal.ONE, "cm", BigDecimal.valueOf(10L), "mm");
        stubGetAll(Lists.newArrayList(mToDm, dmToCm, cmToMm));

        // when
        final PossibleUnitConversions result = unitConversionService.getPossibleConversions("m");

        // then
        assertEquals(3, result.asUnitToConversionMap().size());
        assertBigDecimalEquals(BigDecimal.valueOf(10L), result.convertTo(BigDecimal.ONE, "dm"));
        assertBigDecimalEquals(BigDecimal.valueOf(100L), result.convertTo(BigDecimal.ONE, "cm"));
        assertBigDecimalEquals(BigDecimal.valueOf(1000L), result.convertTo(BigDecimal.ONE, "mm"));
    }

    @Test
    @Ignore
    // TODO MAKU
    public final void shouldReturnNonEmptyConversionsSetUsingSimpleBiDirectionalTraverse() {
        // given
        final Entity kmToM = mockUnitConversionItem(BigDecimal.ONE, "km", BigDecimal.valueOf(1000L), "m");
        final Entity mToDm = mockUnitConversionItem(BigDecimal.ONE, "m", BigDecimal.valueOf(100L), "cm");
        final Entity cmToMm = mockUnitConversionItem(BigDecimal.ONE, "cm", BigDecimal.valueOf(10L), "mm");
        stubGetAll(Lists.newArrayList(mToDm, kmToM, cmToMm));

        // when
        final PossibleUnitConversions result = unitConversionService.getPossibleConversions("m");

        // then
        assertEquals(3, result.asUnitToConversionMap().size());
        assertBigDecimalEquals(new BigDecimal("0.001"), result.convertTo(BigDecimal.ONE, "km"));
        assertBigDecimalEquals(BigDecimal.valueOf(100L), result.convertTo(BigDecimal.ONE, "cm"));
        assertBigDecimalEquals(BigDecimal.valueOf(1000L), result.convertTo(BigDecimal.ONE, "mm"));
    }

    @Test
    @Ignore
    // TODO MAKU
    public final void shouldReturnNonEmptyConversionsSetUsingSimpleOneDirectionalTraverseWithCycle() {
        // given
        final Entity mToDm = mockUnitConversionItem(BigDecimal.ONE, "m", BigDecimal.valueOf(10L), "dm");
        final Entity dmToCm = mockUnitConversionItem(BigDecimal.ONE, "dm", BigDecimal.valueOf(10L), "cm");
        final Entity cmToMm = mockUnitConversionItem(BigDecimal.ONE, "cm", BigDecimal.valueOf(10L), "mm");
        final Entity mmToM = mockUnitConversionItem(BigDecimal.ONE, "mm", new BigDecimal("0.001"), "m");
        stubGetAll(Lists.newArrayList(mToDm, dmToCm, cmToMm, mmToM));

        // when
        final PossibleUnitConversions result = unitConversionService.getPossibleConversions("m");

        // then
        assertEquals(3, result.asUnitToConversionMap().size());
        assertBigDecimalEquals(BigDecimal.valueOf(10L), result.convertTo(BigDecimal.ONE, "dm"));
        assertBigDecimalEquals(BigDecimal.valueOf(100L), result.convertTo(BigDecimal.ONE, "cm"));
        assertBigDecimalEquals(BigDecimal.valueOf(1000L), result.convertTo(BigDecimal.ONE, "mm"));
    }

    @Test
    @Ignore
    // TODO MAKU
    public final void shouldReturnNonEmptyConversionsSetUsingSimpleBiDirectionalTraverseWithCycle() {
        // given
        final Entity kmToM = mockUnitConversionItem(BigDecimal.ONE, "km", BigDecimal.valueOf(1000L), "m");
        final Entity mToDm = mockUnitConversionItem(BigDecimal.ONE, "m", BigDecimal.valueOf(10L), "dm");
        final Entity kmToMm = mockUnitConversionItem(BigDecimal.ONE, "km", BigDecimal.valueOf(1000000L), "mm");
        final Entity mmToDm = mockUnitConversionItem(BigDecimal.valueOf(100L), "mm", BigDecimal.ONE, "dm");
        final Entity dmToM = mockUnitConversionItem(BigDecimal.valueOf(10L), "dm", BigDecimal.ONE, "m");
        stubGetAll(Lists.newArrayList(mToDm, kmToM, kmToMm, mmToDm, dmToM));

        // when
        final PossibleUnitConversions result = unitConversionService.getPossibleConversions("m");

        // then
        assertEquals(3, result.asUnitToConversionMap().size());
        assertBigDecimalEquals(new BigDecimal("0.001"), result.convertTo(BigDecimal.ONE, "km"));
        assertBigDecimalEquals(BigDecimal.valueOf(10L), result.convertTo(BigDecimal.ONE, "dm"));
        assertBigDecimalEquals(BigDecimal.valueOf(1000L), result.convertTo(BigDecimal.ONE, "mm"));
    }

}
