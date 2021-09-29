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

import com.google.common.base.Optional;
import com.qcadoo.commons.functional.Either;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Locale;

public final class BigDecimalUtils {
    
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100L);
    
    private BigDecimalUtils() {
        
    }

    /**
     * Converts value, if null returns zero
     *
     * @param value value
     *
     * @return value or zero
     */
    public static BigDecimal convertNullToZero(final Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return BigDecimal.valueOf(Double.valueOf(value.toString()));
    }

    /**
     * Converts value, if null returns one
     *
     * @param value value
     *
     * @return value or one
     */
    public static BigDecimal convertNullToOne(final Object value) {
        if (value == null) {
            return BigDecimal.ONE;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return BigDecimal.valueOf(Double.valueOf(value.toString()));
        
    }

    /**
     * Converts decimal value to percent
     *
     * @param decimalValue decimal value
     *
     * @return percent
     */
    public static BigDecimal toPercent(final BigDecimal decimalValue, final MathContext mathContext) {
        return convertNullToZero(decimalValue).divide(ONE_HUNDRED, mathContext);
        
    }

    /**
     * Check if decimals represent the same numeric value, even if they have
     * different precisions or contexts.
     *
     * @param d1 first BigDecimal to compare
     * @param d2 second decimal to compare
     * @return true if decimals represent the same numeric value, even if they
     * have different precisions or contexts.
     */
    public static boolean valueEquals(final BigDecimal d1, final BigDecimal d2) {
        if (d1 == null) {
            return d2 == null;
        } else if (d2 == null) {
            return false;
        }
        return d1.compareTo(d2) == 0;
    }

    /**
     * Try parse string into BigDecimal.
     *
     * @param maybeStringWithDecimal String to be parsed as a BigDecimal number
     * @param locale locale to be used when parse.
     * @return either Exception that occur during parsing or parsed BigDecimal,
     * wrapped within Optional.
     */
    public static Either<Exception, Optional<BigDecimal>> tryParse(final String maybeStringWithDecimal, final Locale locale) {
        if (StringUtils.isBlank(maybeStringWithDecimal)) {
            return Either.right(Optional.<BigDecimal>absent());
        }
        try {
            DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(locale);
            format.setParseBigDecimal(true);
            BigDecimal parsedDecimal = new BigDecimal(format.parse(maybeStringWithDecimal).toString());
            return Either.right(Optional.fromNullable(parsedDecimal));
        } catch (Exception e) {
            return Either.left(e);
        }
    }
    
    public static Either<Exception, Optional<BigDecimal>> tryParseAndIgnoreSeparator(final String maybeStringWithDecimal, final Locale locale) {
        if (maybeStringWithDecimal != null && "pl".equals(locale.getLanguage())) {
            return tryParse(maybeStringWithDecimal.replace(".", ","), locale);
        }
        
        return tryParse(maybeStringWithDecimal, locale);        
    }

    public static boolean checkIfCorrectDecimalValue(final Entity entity, final String decimalFieldName){
        try {
            entity.getDecimalField(decimalFieldName);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
