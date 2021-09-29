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
package com.qcadoo.view.internal.components.grid;

/**
 * Restriction comparison operator.
 * 
 * @since 0.4.0
 */
public enum GridComponentFilterOperator {

    /**
     * Equals.
     */
    EQ("eq"),

    /**
     * Greaten than or equals.
     */
    GE("ge"),

    /**
     * Greaten than.
     */
    GT("gt"),

    /**
     * Less than or equals.
     */
    LE("le"),

    /**
     * Less than.
     */
    LT("lt"),

    /**
     * Not equals.
     */
    NE("ne"),

    /**
     * Is in.
     */
    IN("in"),

    /**
     * Contains.
     */
    CN("cn"),

    /**
     * Contains in.
     */
    CIN("cin"),

    /**
     * Begins with.
     */
    BW("bw"),

    /**
     * Ends with.
     */
    EW("ew"),

    /**
     * Is null.
     */
    ISNULL("isnull");

    private String value;

    GridComponentFilterOperator(final String value) {
        this.value = value;
    }

    /**
     * Operator shortcats in javascript components.
     * 
     * @return representation of operator
     */
    public String getValue() {
        return value;
    }

}
