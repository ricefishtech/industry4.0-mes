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

public class GridComponentMultiSearchFilterRule {

    public static final String JSON_FIELD_FIELD = "field";

    public static final String JSON_OPERATOR_FIELD = "op";

    public static final String JSON_DATA_FIELD = "data";

    private String field;

    private GridComponentFilterOperator filterOperator;

    private String data;

    public GridComponentMultiSearchFilterRule(String field, GridComponentFilterOperator filterOperator, String data) {
        this.setField(field);
        this.setFilterOperator(filterOperator);
        this.setData(data);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public GridComponentFilterOperator getFilterOperator() {
        return filterOperator;
    }

    public void setFilterOperator(GridComponentFilterOperator filterOperator) {
        this.filterOperator = filterOperator;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
