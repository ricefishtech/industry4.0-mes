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
package com.qcadoo.model.api;

import java.math.BigDecimal;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.base.Optional;
import com.qcadoo.commons.functional.Either;

public class BigDecimalUtilsTest {

    @Test
    public final void shouldValuesEqualsReturnCorrectValue() {
        Assert.assertTrue(BigDecimalUtils.valueEquals(null, null));
        Assert.assertFalse(BigDecimalUtils.valueEquals(null, BigDecimal.ZERO));
        Assert.assertFalse(BigDecimalUtils.valueEquals(BigDecimal.ZERO, null));
        Assert.assertFalse(BigDecimalUtils.valueEquals(BigDecimal.ONE, BigDecimal.ZERO));
        Assert.assertTrue(BigDecimalUtils.valueEquals(BigDecimal.ZERO.setScale(100), BigDecimal.ZERO.setScale(2)));
    }

    @Test
    public final void shouldConvertNullToZero() {
        // given
        BigDecimal notNullDecimal = BigDecimal.TEN;

        // when
        BigDecimal fromNullRes = BigDecimalUtils.convertNullToZero(null);
        BigDecimal fromNotNullRes = BigDecimalUtils.convertNullToZero(notNullDecimal);

        // then
        Assert.assertEquals(0, notNullDecimal.compareTo(fromNotNullRes));
        Assert.assertEquals(0, BigDecimal.ZERO.compareTo(fromNullRes));
    }

    @Test
    public final void shouldConvertNullToOne() {
        // given
        BigDecimal notNullDecimal = BigDecimal.TEN;

        // when
        BigDecimal fromNullRes = BigDecimalUtils.convertNullToOne(null);
        BigDecimal fromNotNullRes = BigDecimalUtils.convertNullToOne(notNullDecimal);

        // then
        Assert.assertEquals(0, notNullDecimal.compareTo(fromNotNullRes));
        Assert.assertEquals(0, BigDecimal.ONE.compareTo(fromNullRes));
    }

    @Test
    public final void shouldParseNotEmptyEnDecimalString() {
        // given
        String stringValue = "1.2";

        // when
        Either<Exception, Optional<BigDecimal>> res = BigDecimalUtils.tryParse(stringValue, Locale.ENGLISH);

        // then
        Assert.assertEquals(Either.right(Optional.of(new BigDecimal("1.2"))), res);
    }

    @Test
    public final void shouldParseNotEmptyPlDecimalString() {
        // given
        String stringValue = "1,2";

        // when
        Either<Exception, Optional<BigDecimal>> res = BigDecimalUtils.tryParse(stringValue, new Locale("pl"));

        // then
        Assert.assertEquals(Either.right(Optional.of(new BigDecimal("1.2"))), res);
    }

    @Test
    public final void shouldParseEmptyDecimalString() {
        // given
        String stringValue = "";

        // when
        Either<Exception, Optional<BigDecimal>> res = BigDecimalUtils.tryParse(stringValue, Locale.ENGLISH);

        // then
        Assert.assertEquals(Either.right(Optional.<BigDecimal> absent()), res);
    }

    @Test
    public final void shouldReturnExceptionIfParseFail() {
        // given
        String incorrectDecimalString = "oops";

        // when
        Either<Exception, Optional<BigDecimal>> res = BigDecimalUtils.tryParse(incorrectDecimalString, Locale.ENGLISH);

        // then
        Assert.assertTrue(res.isLeft());
    }

}
