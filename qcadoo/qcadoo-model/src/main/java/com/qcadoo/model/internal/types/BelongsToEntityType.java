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

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.internal.api.ValueAndError;

public final class BelongsToEntityType extends AbstractFieldType implements BelongsToType {

    private final DataDefinitionService dataDefinitionService;

    private final String pluginIdentifier;

    private final String entityName;

    private final boolean lazyLoading;

    public BelongsToEntityType(final String pluginIdentifier, final String entityName,
            final DataDefinitionService dataDefinitionService, final boolean lazyLoading, final boolean copyable) {
        super(copyable);
        this.pluginIdentifier = pluginIdentifier;
        this.entityName = entityName;
        this.dataDefinitionService = dataDefinitionService;
        this.lazyLoading = lazyLoading;
    }

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        return ValueAndError.withoutError(value);
    }

    @Override
    public String toString(final Object value, final Locale locale) {
        if (value instanceof Entity) {
            return String.valueOf(((Entity) value).getId());
        } else {
            return String.valueOf(value);
        }
    }

    @Override
    public Object fromString(final String value, final Locale locale) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Can't parse value '" + value + "' to (Long) entity id.", e);
        }
    }

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinitionService.get(pluginIdentifier, entityName);
    }

    @Override
    public boolean isLazyLoading() {
        return lazyLoading;
    }

}
