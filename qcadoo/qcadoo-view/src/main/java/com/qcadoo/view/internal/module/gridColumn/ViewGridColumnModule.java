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
package com.qcadoo.view.internal.module.gridColumn;

import java.util.List;

import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleException;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.components.grid.GridComponentPattern;

public class ViewGridColumnModule extends Module {

    private final String pluginIdentifier;

    private final String extendsViewPlugin;

    private final String extendsViewName;

    private final String extendsComponentName;

    private final List<ViewGridColumnModuleColumnModel> columns;

    private final InternalViewDefinitionService viewDefinitionService;

    ViewGridColumnModule(final String pluginIdentifier, final String extendsViewPlugin, final String extendsViewName,
            final String extendsComponentName, final List<ViewGridColumnModuleColumnModel> columns,
            final InternalViewDefinitionService viewDefinitionService) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.extendsViewPlugin = extendsViewPlugin;
        this.extendsViewName = extendsViewName;
        this.extendsComponentName = extendsComponentName;
        this.columns = columns;
        this.viewDefinitionService = viewDefinitionService;
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        GridComponentPattern grid = getGrid();
        for (ViewGridColumnModuleColumnModel columnModel : columns) {
            grid.addColumn(pluginIdentifier, columnModel);
        }
    }

    @Override
    public void disable() {
        GridComponentPattern grid = getGrid();
        for (ViewGridColumnModuleColumnModel columnModel : columns) {
            grid.removeColumn(columnModel.getName());
        }
    }

    private GridComponentPattern getGrid() {
        InternalViewDefinition viewDefinition = viewDefinitionService.getWithoutSession(extendsViewPlugin, extendsViewName);
        if (viewDefinition == null) {
            throw new ModuleException(pluginIdentifier, "view", "reference to view which not exists");
        }
        ComponentPattern component = viewDefinition.getComponentByReference(extendsComponentName);
        if (component == null) {
            throw new ModuleException(pluginIdentifier, "view",
                    "reference to component which not exists in " + extendsViewPlugin + "/" + extendsViewName);

        }
        if (!(component instanceof GridComponentPattern)) {
            throw new ModuleException(pluginIdentifier, "view", "component '" + extendsComponentName + "' in " + extendsViewPlugin
                    + "/" + extendsViewName + " is not a grid");
        }
        return (GridComponentPattern) component;
    }
}
