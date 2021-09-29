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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.internal.api.ValueAndError;

public final class DateTimeType extends AbstractFieldType {

    public DateTimeType() {
        this(true);
    }

    public DateTimeType(final boolean copyable) {
        super(copyable);
    }

    @Override
    public Class<?> getType() {
        return Date.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        if (value instanceof Date) {
            return ValueAndError.withoutError(value);
        }
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(DateUtils.L_DATE_TIME_FORMAT);
            DateTime dt = fmt.parseDateTime(String.valueOf(value));
            int year = dt.getYear();
            if (year < 1500 || year > 2500) {
                return ValueAndError.withError("qcadooView.validate.field.error.invalidDateTimeFormat");
            }
            return ValueAndError.withoutError(dt.toDate());
        } catch (IllegalArgumentException e) {
            return ValueAndError.withError("qcadooView.validate.field.error.invalidDateTimeFormat");
        }
    }

    @Override
    public String toString(final Object value, final Locale locale) {
        return new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, locale).format((Date) value);
    }

    @Override
    public Object fromString(final String value, final Locale locale) {
        return value;
    }

}
