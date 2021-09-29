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
package com.qcadoo.view.internal.module;

import org.springframework.core.io.Resource;

import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleException;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;

public class ViewModule extends Module {

    private final ViewDefinitionParser viewDefinitionParser;

    private final InternalViewDefinitionService viewDefinitionService;

    private final String pluginIdentifier;

    private final Resource xmlFile;

    public ViewModule(final String pluginIdentifier, final Resource xmlFile, final ViewDefinitionParser viewDefinitionParser,
            final InternalViewDefinitionService viewDefinitionService) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.xmlFile = xmlFile;
        this.viewDefinitionParser = viewDefinitionParser;
        this.viewDefinitionService = viewDefinitionService;
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        try {
            InternalViewDefinition viewDefinition = viewDefinitionParser.parseViewXml(xmlFile, pluginIdentifier);
            viewDefinitionService.save(viewDefinition);
        } catch (Exception e) {
            throw new ModuleException(pluginIdentifier, "view", e);
        }
    }

    @Override
    public void disable() {
        InternalViewDefinition viewDefinition = viewDefinitionParser.parseViewXml(xmlFile, pluginIdentifier);
        viewDefinitionService.delete(viewDefinition);
    }

}
