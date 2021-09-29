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

import org.apache.commons.lang3.StringUtils;

public final class IntegerUtils {

    private IntegerUtils() {

    }

    /**
     * Converts value, if null returns zero
     * 
     * @param value
     *            value
     * 
     * @return value or zero
     */
    public static Integer convertNullToZero(final Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return Integer.valueOf(value.toString());
    }

    /**
     * Converts value, if null returns one
     * 
     * @param value
     *            value
     * 
     * @return value or one
     */
    public static Integer convertNullToOne(final Object value) {
        if (value == null) {
            return 1;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return Integer.valueOf(value.toString());
    }

    /**
     * Parse integer value from string.
     * 
     * @param stringValue
     *            value to be parsed
     * @return parsed integer number or null if given string is empty or blank
     * @throws NumberFormatException
     *             if given string does not represent correct number.
     */
    public static Integer parse(final String stringValue) {
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }
        return Integer.parseInt(StringUtils.trim(stringValue));
    }

}
