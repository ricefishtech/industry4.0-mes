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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleException;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.components.window.WindowComponentPattern;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonGroup;
import com.qcadoo.view.internal.ribbon.model.RibbonGroupsPack;
import com.qcadoo.view.internal.ribbon.model.SingleRibbonGroupPack;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserException;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;
import com.qcadoo.view.internal.xml.ViewExtension;

public class ViewRibbonModule extends Module {

    private final String pluginIdentifier;

    private final String fileName;

    private final InternalViewDefinitionService viewDefinitionService;

    private final ViewDefinitionParser viewDefinitionParser;

    private final ViewExtension viewExtension;

    private Map<WindowComponentPattern, RibbonGroupsPack> addedGroups;

    public ViewRibbonModule(final String pluginIdentifier, final Resource xmlFile,
            final InternalViewDefinitionService viewDefinitionService, final ViewDefinitionParser viewDefinitionParser) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.viewDefinitionService = viewDefinitionService;
        this.viewDefinitionParser = viewDefinitionParser;
        fileName = xmlFile.getFilename();
        try {
            viewExtension = viewDefinitionParser.getViewExtensionNode(xmlFile.getInputStream(), "ribbonExtension");
        } catch (IOException e) {
            throw ViewDefinitionParserException.forFile(fileName, e);
        } catch (ViewDefinitionParserNodeException e) {
            throw ViewDefinitionParserException.forFileAndNode(fileName, e);
        }
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        addedGroups = new HashMap<WindowComponentPattern, RibbonGroupsPack>();

        InternalViewDefinition viewDefinition = viewDefinitionService.getWithoutSession(viewExtension.getPluginName(),
                viewExtension.getViewName());
        if (viewDefinition == null) {
            throw new ModuleException(pluginIdentifier, "view", "reference to view which not exists");
        }

        try {

            for (Node groupNode : viewDefinitionParser.geElementChildren(viewExtension.getExtesionNode())) {

                try {
                    InternalRibbonGroup group = viewDefinitionParser.parseRibbonGroup(groupNode, viewDefinition);
                    group.setExtensionPluginIdentifier(pluginIdentifier);

                    RibbonGroupsPack groupsPack = new SingleRibbonGroupPack(group);

                    WindowComponentPattern window = viewDefinition.getRootWindow();

                    window.getRibbon().addGroupsPack(groupsPack);
                    addedGroups.put(window, groupsPack);
                } catch (ViewDefinitionParserNodeException e) {
                    throw ViewDefinitionParserException.forFileAndNode(fileName, e);
                }
            }
        } catch (Exception e) {
            throw new ModuleException(pluginIdentifier, "view-ribbon-group", e);
        }
    }

    @Override
    public void disable() {
        for (Map.Entry<WindowComponentPattern, RibbonGroupsPack> addedGroupEntry : addedGroups.entrySet()) {
            addedGroupEntry.getKey().getRibbon().removeGroupsPack(addedGroupEntry.getValue());
        }
    }

}
