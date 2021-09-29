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
package com.qcadoo.view.internal.components.awesomeDynamicList;

import java.util.Map.Entry;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.internal.ProxyEntity;

public class AwesomeDynamicListModelUtilsImpl implements AwesomeDynamicListModelUtils {

    public final void proxyBelongsToFields(final Entity entity) {
        DataDefinition dataDefinition = entity.getDataDefinition();
        for (Entry<String, FieldDefinition> fieldDefinitionEntry : dataDefinition.getFields().entrySet()) {
            if (fieldDefinitionEntry.getValue().getType() instanceof BelongsToType) {
                proxyBelongsToField(fieldDefinitionEntry.getValue(), entity);
            }
        }
    }

    private void proxyBelongsToField(final FieldDefinition fieldDefinition, final Entity entity) {
        Long belongsToEntityId = getBelongsToEntityId(entity.getField(fieldDefinition.getName()));
        if (belongsToEntityId == null) {
            return;
        }
        DataDefinition belongsToDataDefinition = ((BelongsToType) fieldDefinition.getType()).getDataDefinition();
        Entity belongsToEntity = new ProxyEntity(belongsToDataDefinition, belongsToEntityId);
        entity.setField(fieldDefinition.getName(), belongsToEntity);
    }

    private Long getBelongsToEntityId(final Object value) {
        if (value instanceof String) {
            return Long.valueOf((String) value);
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return Long.valueOf((Integer) value);
        }
        if (value instanceof Entity) {
            return ((Entity) value).getId();
        }
        return null;
    }
}
