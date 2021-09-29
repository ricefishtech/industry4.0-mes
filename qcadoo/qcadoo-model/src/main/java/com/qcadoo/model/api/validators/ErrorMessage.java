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
package com.qcadoo.model.api.validators;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * Object holds validation error message.
 * 
 * @since 0.4.0
 */
public final class ErrorMessage {

    private final String message;

    private final String[] vars;

    private final boolean autoClose;

    private final boolean extraLarge;

    /**
     * Create new validation error message.
     * 
     * @param message
     *            message
     * @param vars
     *            message's vars
     */
    public ErrorMessage(final String message, final String... vars) {
        this.message = message;
        this.autoClose = true;
        this.extraLarge = false;
        if (ArrayUtils.isEmpty(vars)) {
            this.vars = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            this.vars = vars;
        }
    }

    /**
     * Create new validation error message.
     * 
     * @param message
     *            message
     * @param autoClose
     *            autoClose
     * @param vars
     *            message's vars
     */
    public ErrorMessage(final String message, final boolean autoClose, final String... vars) {
        this.message = message;
        this.autoClose = autoClose;
        this.extraLarge = false;
        if (ArrayUtils.isEmpty(vars)) {
            this.vars = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            this.vars = vars;
        }
    }

    /**
     * Create new validation error message.
     *
     * @param message
     *            message
     * @param autoClose
     *            autoClose
     * @param extraLarge
     *            extraLarge
     * @param vars
     *            message's vars
     */
    public ErrorMessage(final String message, final boolean autoClose, final boolean extraLarge, final String... vars) {
        this.message = message;
        this.autoClose = autoClose;
        this.extraLarge = extraLarge;
        if (ArrayUtils.isEmpty(vars)) {
            this.vars = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            this.vars = vars;
        }
    }

    /**
     * Return validation error message.
     * 
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Return validation error message's vars.
     * 
     * @return message's vars
     */
    public String[] getVars() {
        return Arrays.copyOf(vars, vars.length);
    }

    /**
     * Return autoClose.
     * 
     * @return autoClose
     */
    public boolean getAutoClose() {
        return autoClose;
    }

    /**
     * Return extraLarge.
     *
     * @return extraLarge
     */
    public boolean isExtraLarge() {
        return extraLarge;
    }
}
