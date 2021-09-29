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
package com.qcadoo.model.internal.types;

import com.qcadoo.model.api.NumberService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DecimalTypeTest {

    private DecimalType decimalType;

    private Locale locale = Locale.getDefault();

    private NumberFormat numberFormat;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        decimalType = new DecimalType();
        numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setMaximumFractionDigits(Integer.MAX_VALUE);
        numberFormat.setMaximumIntegerDigits(Integer.MAX_VALUE);
    }

    @Test
    public final void shouldFormatNullAsEmptyString() {
        // when
        String result = decimalType.toString(null, locale);

        // then
        assertEquals("", result);
    }

    @Test
    public final void shouldFormatDecimalWithoutOfficiousTrimmingFractionalValue() {
        // given
        final String decimalAsString = "1234567."
                + StringUtils.repeat("9", NumberService.DEFAULT_MAX_FRACTION_DIGITS_IN_DECIMAL + 3);
        BigDecimal value = new BigDecimal(decimalAsString);

        // when
        String result = decimalType.toString(value, locale);

        // then
        assertFormattedEquals(value, result);
    }

    @Test
    public final void shouldFormatDecimalWithoutOfficiousTrimmingFractionalValue2() {
        // given
        final String decimalAsString = "1234567." + StringUtils.repeat("9", NumberService.DEFAULT_MAX_FRACTION_DIGITS_IN_DECIMAL)
                + "000001";
        BigDecimal value = new BigDecimal(decimalAsString);

        // when
        String result = decimalType.toString(value, locale);

        // then
        assertFormattedEquals(value, result);
    }

    @Test
    public final void shouldFormatDecimalTrimTrailingZeroes() {
        // given
        final String decimalAsString = "1234567." + StringUtils.repeat("9", 3);
        BigDecimal value = new BigDecimal(decimalAsString + "00000");

        // when
        String result = decimalType.toString(value, locale);

        // then
        assertFormattedEquals(value, result);
    }

    @Test
    public final void shouldFormatDecimalTrimTrailingZeroes2() {
        // given
        BigDecimal value = BigDecimal.ZERO.setScale(10);

        // when
        String result = decimalType.toString(value, locale);

        // then
        assertEquals("0", result);
    }

    private void assertFormattedEquals(final Object value, final String result) {
        assertEquals(numberFormat.format(value), result);
    }

}
