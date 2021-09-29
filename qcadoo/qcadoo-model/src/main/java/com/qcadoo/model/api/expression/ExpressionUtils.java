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
package com.qcadoo.model.api.expression;

import java.util.List;
import java.util.Locale;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.internal.ExpressionServiceImpl;

/**
 * Utility to evaluate expression for entity.
 * 
 * @version 0.4.0
 */
public final class ExpressionUtils {

    private ExpressionUtils() {
    }

    /**
     * Evaluate expression - result is value of field (or comma separated fields values). Returns null when generated value is
     * null.
     * 
     * @param entity
     *            entity
     * @param fields
     *            fields
     * @param locale
     *            locale
     * @return evaluated expression or null
     */
    public static String getValue(final Entity entity, final List<FieldDefinition> fields, final Locale locale) {
        return ExpressionServiceImpl.getInstance().getValue(entity, fields, locale);
    }

    /**
     * Evaluate expression value using entity fields values. Returns null when generated value is null.
     * 
     * @param entity
     *            entity
     * @param locale
     *            locale
     * @return evaluated expression or null
     */
    public static String getValue(final Entity entity, final String expression, final Locale locale) {
        return ExpressionServiceImpl.getInstance().getValue(entity, expression, locale);
    }

}
