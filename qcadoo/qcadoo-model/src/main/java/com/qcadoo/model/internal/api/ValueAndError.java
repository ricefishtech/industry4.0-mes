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
package com.qcadoo.model.internal.api;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Immutable class representing value and error
 */
public final class ValueAndError {

    private static final ValueAndError EMPTY_VALUE_AND_ERROR = new ValueAndError(null, null);

    private final Object value;

    private final String message;

    private final String[] args;

    private ValueAndError(final Object value, final String message, final String... args) {
        this.value = value;
        this.message = message;
        if (ArrayUtils.isEmpty(args)) {
            this.args = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            this.args = args;
        }
    }

    /**
     * Build and returns new instance of {@link ValueAndError} with given
     * 
     * @param value
     * @return new instance of {@link ValueAndError}
     */
    public static ValueAndError withoutError(final Object value) {
        return new ValueAndError(value, null);
    }

    /**
     * Returns empty {@link ValueAndError} object
     * 
     * @return empty {@link ValueAndError} object
     */
    public static ValueAndError empty() {
        return EMPTY_VALUE_AND_ERROR;
    }

    /**
     * Build and returns new instance of {@link ValueAndError} with given message and args
     * 
     * @param message
     *            error message
     * @param args
     *            error message arguments (values which will replace message's place holders)
     * @return new instance of {@link ValueAndError}
     */
    public static ValueAndError withError(final String message, final String... args) {
        return new ValueAndError(null, message, args);
    }

    /**
     * Checks if value is valid.
     * 
     * @return true if field value is valid. Otherwise false.
     */
    public boolean isValid() {
        return message == null;
    }

    /**
     * Returns parsed value
     * 
     * @return parsed value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns error message arguments (values which will replace message's place holders)
     * 
     * @return error message arguments (values which will replace message's place holders)
     */
    public String[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }

    /**
     * Returns error message
     * 
     * @return error message
     */
    public String getMessage() {
        return message;
    }

}
