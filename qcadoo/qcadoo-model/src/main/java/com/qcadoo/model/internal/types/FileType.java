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

import java.io.File;
import java.util.Locale;

import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.internal.api.ValueAndError;

public final class FileType extends AbstractFieldType {

    public FileType() {
        this(true);
    }

    public FileType(final boolean copyable) {
        super(copyable);
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        File file = null;

        if (value instanceof File) {
            file = (File) value;
        } else {
            file = new File(value.toString());
        }

        if (file.exists() && file.canRead() && file.canWrite()) {
            return ValueAndError.withoutError(file.getAbsolutePath());
        } else {
            return ValueAndError.withError("qcadooView.validate.field.error.invalidFile");
        }
    }

    @Override
    public String toString(final Object value, final Locale locale) {
        return String.valueOf(value);
    }

    @Override
    public Object fromString(final String value, final Locale locale) {
        return value;
    }

}
