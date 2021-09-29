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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleException;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.components.window.WindowComponentPattern;
import com.qcadoo.view.internal.components.window.WindowTabComponentPattern;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserException;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;
import com.qcadoo.view.internal.xml.ViewExtension;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Map;

public class ViewTabModule extends Module {

    private final String pluginIdentifier;

    private final InternalViewDefinitionService viewDefinitionService;

    private final ViewDefinitionParser viewDefinitionParser;

    private final ViewExtension viewExtension;

    private final String fileName;

    private Multimap<WindowComponentPattern, ComponentPattern> addedTabs;

    public ViewTabModule(final String pluginIdentifier, final Resource xmlFile,
            final InternalViewDefinitionService viewDefinitionService, final ViewDefinitionParser viewDefinitionParser) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.viewDefinitionService = viewDefinitionService;
        this.viewDefinitionParser = viewDefinitionParser;
        fileName = xmlFile.getFilename();
        try {
            viewExtension = viewDefinitionParser.getViewExtensionNode(xmlFile.getInputStream(), "windowTabExtension");
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
        addedTabs = ArrayListMultimap.create();

        InternalViewDefinition viewDefinition = viewDefinitionService.getWithoutSession(viewExtension.getPluginName(), viewExtension.getViewName());
        if (viewDefinition == null) {
            throw new ModuleException(pluginIdentifier, "view", String.format("Reference to view which not exists: %s[%s]",viewExtension.getPluginName(), viewExtension.getViewName()));
        }

        try {
            for (Node tabNode : viewDefinitionParser.geElementChildren(viewExtension.getExtesionNode())) {

                WindowComponentPattern window = viewDefinition.getRootWindow();

                ComponentDefinition tabDefinition = viewDefinitionParser.getComponentDefinition(tabNode, window, viewDefinition);
                tabDefinition.setExtensionPluginIdentifier(pluginIdentifier);

                ComponentPattern tabPattern = new WindowTabComponentPattern(tabDefinition);

                try {
                    tabPattern.parse(tabNode, viewDefinitionParser);
                } catch (ViewDefinitionParserNodeException e) {
                    throw ViewDefinitionParserException.forFileAndNode(fileName, e);
                }

                window.addChild(tabPattern);
                addedTabs.put(window, tabPattern);

                tabPattern.initializeAll();
                tabPattern.registerViews(viewDefinitionService);
            }
        } catch (Exception e) {
            throw new ModuleException(pluginIdentifier, "view-tab", e);
        }
    }

    @Override
    public void disable() {
        for (Map.Entry<WindowComponentPattern, ComponentPattern> addedGroupEntry : addedTabs.entries()) {
            addedGroupEntry.getValue().unregisterComponent(viewDefinitionService);
            addedGroupEntry.getKey().removeChild(addedGroupEntry.getValue().getName());
        }
    }

}
