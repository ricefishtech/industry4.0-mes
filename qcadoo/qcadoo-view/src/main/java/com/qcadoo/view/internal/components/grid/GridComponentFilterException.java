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
 * @author marcinkubala
 * @since 1.2.0
 */
public class GridComponentFilterException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String filterValue;

    /**
     * @param filterValue
     *            value of column's filter
     */
    public GridComponentFilterException(final String filterValue) {
        this(filterValue, null);
    }

    public GridComponentFilterException(final String filterValue, final Throwable cause) {
        super(cause);
        this.filterValue = filterValue;
    }

    /**
     * Returns value of column's filter
     * 
     * @return value of column's filter
     */
    public String getFilterValue() {
        return filterValue;
    }

}
