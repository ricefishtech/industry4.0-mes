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
package com.qcadoo.view.internal.ribbon.templates.module;

import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import com.qcadoo.plugin.api.Module;
import com.qcadoo.view.internal.ribbon.templates.RibbonTemplatesService;
import com.qcadoo.view.internal.ribbon.templates.model.RibbonTemplate;
import com.qcadoo.view.internal.ribbon.templates.model.TemplateRibbonGroup;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserException;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;

public class RibbonTemplateModule extends Module {

    private final RibbonTemplatesService ribbonTemplatesService;

    private final RibbonTemplate template;

    public RibbonTemplateModule(final String pluginIdentifier, final Resource xmlFile, final ViewDefinitionParser parser,
            final RibbonTemplatesService ribbonTemplatesService) {
        super();

        final String fileName = xmlFile.getFilename();
        this.ribbonTemplatesService = ribbonTemplatesService;
        try {

            Node root = parser.getRootOfXmlDocument(xmlFile);
            parser.checkState("ribbonTemplate".equals(root.getNodeName()), root, "Wrong root node name '" + root.getNodeName()
                    + "'");

            String templateName = parser.getStringAttribute(root, "name");
            parser.checkState(templateName != null, root, "Ribbon template error: name not defined");

            template = new RibbonTemplate(pluginIdentifier, templateName);

            for (Node groupNode : parser.geElementChildren(root)) {
                String groupName = parser.getStringAttribute(groupNode, "name");
                String groupCondition = parser.getStringAttribute(groupNode, "if");
                parser.checkState(groupName != null, groupNode, "Ribbon template error: group name not defined");
                TemplateRibbonGroup templateGroup = new TemplateRibbonGroup(groupName, pluginIdentifier, groupCondition);

                for (Node itemNode : parser.geElementChildren(groupNode)) {
                    templateGroup.addActionItem(parser.parseRibbonItem(itemNode, null));
                }

                template.addTemplateGroup(templateGroup);
            }

        } catch (ViewDefinitionParserNodeException e) {
            throw ViewDefinitionParserException.forFileAndNode(fileName, e);
        } catch (Exception e) {
            throw ViewDefinitionParserException.forFile(fileName, e);
        }
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        ribbonTemplatesService.addTemplate(template);
    }

    @Override
    public void disable() {
        ribbonTemplatesService.removeTemplate(template);
    }
}
