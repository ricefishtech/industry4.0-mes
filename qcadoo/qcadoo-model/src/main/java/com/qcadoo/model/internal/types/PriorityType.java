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
package com.qcadoo.model.internal.types;

import java.util.Locale;

import org.springframework.util.StringUtils;

import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.internal.api.ValueAndError;

public final class PriorityType extends AbstractFieldType {

    private final FieldDefinition scopeFieldDefinition;

    public PriorityType(final FieldDefinition scopeFieldDefinition) {
        super(true);
        this.scopeFieldDefinition = scopeFieldDefinition;
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        if (value instanceof Integer) {
            return ValueAndError.withoutError(value);
        }
        try {
            return ValueAndError.withoutError(Integer.parseInt(String.valueOf(value)));
        } catch (NumberFormatException e) {
            return ValueAndError.withError("form.validate.errors.invalidNumericFormat");
        }
    }

    @Override
    public String toString(final Object value, final Locale locale) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    @Override
    public Object fromString(final String value, final Locale locale) {
        if (StringUtils.hasText(value)) {
            return Integer.parseInt(value);
        } else {
            return null;
        }
    }

    public FieldDefinition getScopeFieldDefinition() {
        return scopeFieldDefinition;
    }

}
