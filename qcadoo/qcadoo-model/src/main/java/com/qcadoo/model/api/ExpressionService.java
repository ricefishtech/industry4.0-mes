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

import java.util.List;
import java.util.Locale;

/**
 * Service to evaluate expression for entity.
 * 
 * @version 0.4.0
 */
public interface ExpressionService {

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
    String getValue(Entity entity, List<FieldDefinition> fields, Locale locale);

    /**
     * Evaluate expression value using entity fields values. Returns null when generated value is null.
     * 
     * @param entity
     *            entity
     * @param locale
     *            locale
     * @return evaluated expression or null
     */
    String getValue(Entity entity, String expression, Locale locale);

}
