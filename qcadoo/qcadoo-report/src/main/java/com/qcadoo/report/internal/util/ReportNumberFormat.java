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
package com.qcadoo.report.internal.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ReportNumberFormat extends NumberFormat {

    private static final long serialVersionUID = 8881156984775289396L;

    private final NumberFormat decimalNumberFormat;

    private final NumberFormat integerNumberFormat;

    private static final Map<Locale, ReportNumberFormat> FORMATTERS = new HashMap<Locale, ReportNumberFormat>();
	
    public static ReportNumberFormat getInstance(final Locale locale) {
	return FORMATTERS.computeIfAbsent(locale, f -> new ReportNumberFormat(locale));
    }

    private ReportNumberFormat(final Locale locale) {
        super();

        decimalNumberFormat = NumberFormat.getNumberInstance(locale);
        decimalNumberFormat.setMaximumFractionDigits(5);
        integerNumberFormat = NumberFormat.getNumberInstance(locale);
    }

    @Override
    public StringBuffer format(final double number, final StringBuffer toAppendTo, final FieldPosition pos) {
        return decimalNumberFormat.format(number, toAppendTo, pos);
    }

    @Override
    public StringBuffer format(final long number, final StringBuffer toAppendTo, final FieldPosition pos) {
        return integerNumberFormat.format(number, toAppendTo, pos);
    }

    @Override
    public Number parse(final String source, final ParsePosition parsePosition) {
        return decimalNumberFormat.parse(source, parsePosition);
    }
}
