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

import com.qcadoo.model.internal.api.InternalDictionaryService;
import com.qcadoo.plugin.api.Module;

public class DictionaryModule extends Module {

    private final String name;

    private final InternalDictionaryService dictionaryService;

    private final String pluginIdentifier;

    public DictionaryModule(final String pluginIdentifier, final String name, final InternalDictionaryService dictionaryService) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.name = name;
        this.dictionaryService = dictionaryService;
    }

    @Override
    public void multiTenantEnableOnStartup() {
        multiTenantEnable();
    }

    @Override
    public void multiTenantEnable() {
        dictionaryService.createIfNotExists(pluginIdentifier, name);
    }

    @Override
    public void disable() {
        multiTenantDisable();
    }

    @Override
    public void multiTenantDisable() {
        dictionaryService.disable(pluginIdentifier, name);
    }

}
