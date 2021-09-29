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
package com.qcadoo.model.internal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.qcadoo.model.api.NumberService;

@Component
public final class NumberServiceImpl implements NumberService {

    private static final Logger LOG = LoggerFactory.getLogger(NumberServiceImpl.class);

    private static final int MIN_PRECISION = DEFAULT_MIN_FRACTION_DIGITS_IN_DECIMAL;

    public static final int MAX_PRECISION = DEFAULT_MAX_FRACTION_DIGITS_IN_DECIMAL;

    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    private static final Map<Locale, DecimalFormat> DECIMAL_FORMATS = Maps.newConcurrentMap();

    private DecimalFormat getDecimalFormat() {
        final Locale locale = LocaleContextHolder.getLocale();
        DecimalFormat decimalFormat = DECIMAL_FORMATS.get(locale);
        if (decimalFormat == null) {
            decimalFormat = buildDecimalFormat(locale);
            DECIMAL_FORMATS.put(locale, decimalFormat);
        }
        return decimalFormat;
    }

    private DecimalFormat getDecimalFormat(final int minimumFractionDigits) {
        final Locale locale = LocaleContextHolder.getLocale();
        DecimalFormat decimalFormat = buildDecimalFormat(locale, minimumFractionDigits);
        return decimalFormat;
    }

    private DecimalFormat buildDecimalFormat(final Locale locale) {
        final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        decimalFormat.setMinimumFractionDigits(MIN_PRECISION);
        decimalFormat.setMaximumFractionDigits(MAX_PRECISION);
        decimalFormat.setRoundingMode(ROUNDING_MODE);
        return decimalFormat;
    }

    private DecimalFormat buildDecimalFormat(final Locale locale, final int minimumFractionDigits) {
        final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        decimalFormat.setMinimumFractionDigits(minimumFractionDigits);
        decimalFormat.setMaximumFractionDigits(MAX_PRECISION);
        decimalFormat.setRoundingMode(ROUNDING_MODE);
        return decimalFormat;
    }

    @Override
    public MathContext getMathContext() {
        return MathContext.DECIMAL64;
    }

    @Override
    public String format(final Object obj) {
        String formattedNumber = null;
        if (obj != null) {
            formattedNumber = getDecimalFormat().format(obj);
        }
        return formattedNumber;
    }

    @Override
    public String formatWithMinimumFractionDigits(final Object obj, final int minimumFractionDigits) {
        String formattedNumber = null;
        if (obj != null) {
            formattedNumber = getDecimalFormat(minimumFractionDigits).format(obj);
        }
        return formattedNumber;
    }

    @Override
    public BigDecimal setScaleWithDefaultMathContext(final BigDecimal decimal) {
        return setScaleWithDefaultMathContext(decimal, MAX_PRECISION);
    }

    @Override
    public BigDecimal setScaleWithDefaultMathContext(final BigDecimal decimal, final int newScale) {
        if (decimal == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("NumberService setScaleWithDefaultMathContext - decimal is null!");
            }
            return null;
        }
        return decimal.setScale(newScale, ROUNDING_MODE);
    }

}
