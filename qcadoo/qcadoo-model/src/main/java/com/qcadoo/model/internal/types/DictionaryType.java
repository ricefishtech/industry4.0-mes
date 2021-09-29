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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.EnumeratedType;
import com.qcadoo.model.internal.api.ValueAndError;

public final class DictionaryType extends AbstractFieldType implements EnumeratedType {

    private final String dictionary;

    private final DictionaryService dictionaryService;

    public DictionaryType(final String dictionary, final DictionaryService dictionaryService, final boolean copyable) {
        super(copyable);
        this.dictionary = dictionary;
        this.dictionaryService = dictionaryService;
    }

    @Override
    public Map<String, String> values(final Locale locale) {
        return dictionaryService.getValues(dictionary, locale);
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        String stringValue = String.valueOf(value);
        List<String> keys = dictionaryService.getKeys(dictionary);
        if (!keys.contains(stringValue)) {
            return ValueAndError.withError("qcadooView.validate.field.error.invalidDictionaryItem", String.valueOf(keys));
        }
        return ValueAndError.withoutError(stringValue);
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
