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
package com.qcadoo.model.api.types;

import java.util.Locale;

import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.internal.api.ValueAndError;

/**
 * Object represents field type.
 */
public interface FieldType {

    /**
     * Returns field class.
     * 
     * @return class
     */
    Class<?> getType();

    /**
     * Convert given value to valid field's value defined in {@link FieldType#getType()}. During conversion validation is
     * executed.
     * 
     * @param fieldDefinition
     *            field definition
     * @param value
     *            value
     * @return value with validation result
     */
    ValueAndError toObject(FieldDefinition fieldDefinition, Object value);

    /**
     * Convert field's value to string.
     * 
     * @param value
     *            value
     * @param locale
     *            locale
     * @return string value
     */
    String toString(Object value, Locale locale);

    /**
     * Convert field's value to object defined in {@link FieldType#getType()}.
     * 
     * @param value
     *            value
     * @param locale
     *            locale
     * @return converted value
     */
    Object fromString(String value, Locale locale);

    /**
     * Returns true if field should be copied.
     * 
     * @return true if should be copied
     */
    boolean isCopyable();
}
