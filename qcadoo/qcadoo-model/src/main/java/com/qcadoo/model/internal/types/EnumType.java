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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.EnumeratedType;
import com.qcadoo.model.internal.api.ValueAndError;
import com.qcadoo.plugin.api.PluginUtils;

public final class EnumType extends AbstractFieldType implements EnumeratedType {

    private static final EnumTypeComparator ENUM_TYPE_COMPARATOR = new EnumTypeComparator();

    private final List<EnumTypeKey> keys;

    private final TranslationService translationService;

    private final String translationPath;

    public EnumType(final TranslationService translationService, final String translationPath, final boolean copyable,
            final String... keys) {
        super(copyable);
        this.translationService = translationService;
        this.translationPath = translationPath;
        this.keys = new ArrayList<EnumTypeKey>();
        for (String key : keys) {
            this.keys.add(new EnumTypeKey(key, null));
        }
        Collections.sort(this.keys, ENUM_TYPE_COMPARATOR);
    }

    public List<EnumTypeKey> getKeys() {
        return keys;
    }

    @Override
    public Map<String, String> values(final Locale locale) {
        LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
        for (String key : toStringList()) {
            values.put(key, translationService.translate(translationPath + ".value." + key, locale));
        }
        return values;
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        String stringValue = String.valueOf(value);
        if (toStringList().contains(stringValue)) {
            return ValueAndError.withoutError(stringValue);
        }
        return ValueAndError.withError("qcadooView.validate.field.error.invalidDictionaryItem", String.valueOf(toStringList()));
    }

    private List<String> toStringList() {
        List<String> result = new ArrayList<String>();
        for (EnumTypeKey key : keys) {
            if (key.getOriginPluginIdentifier() == null || PluginUtils.isEnabled(key.getOriginPluginIdentifier())) {
                result.add(key.getValue());
            }
        }
        return result;
    }

    @Override
    public String toString(final Object value, final Locale locale) {
        return String.valueOf(value);
    }

    @Override
    public Object fromString(final String value, final Locale locale) {
        return value;
    }

    private static final class EnumTypeComparator implements Comparator<EnumTypeKey>, Serializable {

        private static final long serialVersionUID = 1323570043651191244L;

        @Override
        public int compare(final EnumTypeKey arg0, final EnumTypeKey arg1) {
            return arg0.getValue().compareTo(arg1.getValue());
        }

    }

}
