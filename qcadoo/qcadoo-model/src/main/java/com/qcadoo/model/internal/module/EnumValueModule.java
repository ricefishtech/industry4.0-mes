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
package com.qcadoo.model.internal.module;

import java.util.List;

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.internal.types.EnumType;
import com.qcadoo.model.internal.types.EnumTypeKey;
import com.qcadoo.plugin.api.Module;

public class EnumValueModule extends Module {

    private final String pluginIdentifier;

    private final String modelName;

    private final String fieldName;

    private final EnumTypeKey enumValue;

    private final DataDefinitionService dataDefinitionService;

    public EnumValueModule(final String originPluginIdentifier, final DataDefinitionService dataDefinitionService,
            final String pluginIdentifier, final String modelName, final String fieldName, final String value) {
        super();

        this.dataDefinitionService = dataDefinitionService;
        this.pluginIdentifier = pluginIdentifier;
        this.modelName = modelName;
        this.fieldName = fieldName;
        enumValue = new EnumTypeKey(value, originPluginIdentifier);
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        List<EnumTypeKey> keys = getKeys();

        for (EnumTypeKey key : keys) {
            if (key.equals(enumValue)) {
                return;
            }
        }

        keys.add(enumValue);
    }

    @Override
    public void disable() {
        getKeys().remove(enumValue);
    }

    private List<EnumTypeKey> getKeys() {
        return ((EnumType) dataDefinitionService.get(pluginIdentifier, modelName).getField(fieldName).getType()).getKeys();
    }

}
