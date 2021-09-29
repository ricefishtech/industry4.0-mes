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
package com.qcadoo.report.api.pdf;

public enum HeaderAlignment {
    LEFT("01left"), RIGHT("02right"), CENTER("03center");

    private String alignment;

    private HeaderAlignment(final String alignment) {
        this.alignment = alignment;
    }

    public String getStringValue() {
        return alignment;
    }

    public static HeaderAlignment parseString(final String alignment) {
        if ("01left".equals(alignment)) {
            return LEFT;
        } else if ("02right".equals(alignment)) {
            return RIGHT;
        } else if ("03center".equals(alignment)) {
            return CENTER;
        }

        throw new IllegalStateException("Unsupported header alignment '" + alignment + "'");
    }
}
