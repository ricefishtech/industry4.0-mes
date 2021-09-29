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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.units.UnsupportedUnitConversionException;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.NumberService;
import com.qcadoo.model.api.units.UnitConversion;

public class PossibleUnitConversionsImplTest {

    private static final String UNIT_FROM = "unitFrom";

    private PossibleUnitConversionsImpl possibleUnitConversionsImpl;

    @Mock
    private DataDefinition unitConversionItemDD;

    @Mock
    private NumberService numberService;

    @Mock
    private DictionaryService dictionaryService;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        this.possibleUnitConversionsImpl = new PossibleUnitConversionsImpl(UNIT_FROM, numberService, unitConversionItemDD, dictionaryService);
    }

    private UnitConversion mockUnitConversion(final String unitFrom, final BigDecimal ratio, final String unitTo) {
        final UnitConversion unitConversion = mock(UnitConversion.class);
        given(unitConversion.getUnitFrom()).willReturn(unitFrom);
        given(unitConversion.getUnitTo()).willReturn(unitTo);
        given(unitConversion.getRatio()).willReturn(ratio);
        return unitConversion;
    }

    private void assertBigDecimalEquals(final BigDecimal expected, final BigDecimal actual) {
        if (expected.compareTo(actual) != 0) {
            Assert.fail("expected " + expected + " but actual value is " + actual);
        }
    }

    @Test(expected = UnsupportedUnitConversionException.class)
    public final void shouldThrowExceptionIfConversionDoesNotExists() {
        // when & then
        possibleUnitConversionsImpl.convertTo(BigDecimal.ONE, "kg");
    }

    @Test
    public final void shouldThrowExceptionWhenTryToInsertWrongConversion() {
        // given
        final String unitTo = "unitTo";
        final String unitFrom = "wrong" + UNIT_FROM;

        // when & then
        try {
            possibleUnitConversionsImpl.addConversion(mockUnitConversion(unitFrom, BigDecimal.ONE, unitTo));
            Assert.fail();
        } catch (Exception e) {
        }

    }

    @Test
    @Ignore
    // TODO MAKU
    public final void shouldReturnConvertedValueIFConversionExists() {
        // given
        final String unitTo = "unitTo";
        possibleUnitConversionsImpl.addConversion(mockUnitConversion(UNIT_FROM, new BigDecimal("0.5"), unitTo));

        // when
        final BigDecimal result = possibleUnitConversionsImpl.convertTo(BigDecimal.valueOf(10L), unitTo);

        // then
        assertBigDecimalEquals(BigDecimal.valueOf(5L), result);

    }

}
