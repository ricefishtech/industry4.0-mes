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
package com.qcadoo.view.internal.ribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import com.qcadoo.view.api.ribbon.RibbonComboBox;
import com.qcadoo.view.api.ribbon.RibbonComboItem;
import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.ribbon.model.InternalRibbon;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonActionItem;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonComboItem;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonGroup;
import com.qcadoo.view.internal.ribbon.model.RibbonActionItemImpl;
import com.qcadoo.view.internal.ribbon.model.RibbonComboBoxImpl;
import com.qcadoo.view.internal.ribbon.model.RibbonComboItemImpl;
import com.qcadoo.view.internal.ribbon.model.RibbonGroupImpl;
import com.qcadoo.view.internal.ribbon.model.RibbonImpl;
import com.qcadoo.view.internal.ribbon.model.SingleRibbonGroupPack;
import com.qcadoo.view.internal.ribbon.templates.RibbonTemplateParameters;
import com.qcadoo.view.internal.ribbon.templates.RibbonTemplateParameters.RibbonTemplateParametersBuilder;
import com.qcadoo.view.internal.ribbon.templates.RibbonTemplatesService;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;

@Service
public class RibbonParserService {

    private static final String NAME = "name";

    private static final String ALIGNMENT = "alignment";

    @Autowired
    private RibbonTemplatesService ribbonTemplatesService;

    private final RibbonTemplates ribbonTemplates = new RibbonTemplates();

    public InternalRibbon parseRibbon(final Node ribbonNode, final ViewDefinitionParser parser,
            final ViewDefinition viewDefinition) throws ViewDefinitionParserNodeException {
        InternalRibbon ribbon = new RibbonImpl();

        NodeList childNodes = ribbonNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (Node.ELEMENT_NODE != child.getNodeType()) {
                continue;
            }
            if ("group".equals(child.getNodeName())) {
                ribbon.addGroupsPack(new SingleRibbonGroupPack(parseRibbonGroup(child, parser, viewDefinition)));
            } else if ("template".equals(child.getNodeName())) {
                applyTemplate(child, ribbon, parser, viewDefinition);
            } else {
                throw new ViewDefinitionParserNodeException(child, "Wrong node type - 'group' or 'template' expected");
            }
        }
        ribbon.setAlignment(parser.getStringAttribute(ribbonNode, ALIGNMENT));

        return ribbon;
    }

    private void applyTemplate(final Node templateNode, final InternalRibbon ribbon, final ViewDefinitionParser parser,
            final ViewDefinition viewDefinition) throws ViewDefinitionParserNodeException {
        String name = parser.getStringAttribute(templateNode, NAME);
        parser.checkState(name != null, templateNode, "Name attribute cannot be empty");
        String plugin = parser.getStringAttribute(templateNode, "plugin");

        RibbonTemplateParametersBuilder parametersBuilder = RibbonTemplateParameters.getBuilder(plugin, name);
        try {
            parametersBuilder.usingOnlyGroups(parser.getStringAttribute(templateNode, "includeGroups"));
            parametersBuilder.usingOnlyItems(parser.getStringAttribute(templateNode, "includeItems"));
            parametersBuilder.withoutGroups(parser.getStringAttribute(templateNode, "excludeGroups"));
            parametersBuilder.withoutItems(parser.getStringAttribute(templateNode, "excludeItems"));
        } catch (IllegalStateException e) {
            throw new ViewDefinitionParserNodeException(templateNode, e);
        }

        ribbonTemplatesService.applyTemplate(ribbon, parametersBuilder.build(), viewDefinition);
    }

    public InternalRibbonGroup parseRibbonGroup(final Node groupNode, final ViewDefinitionParser parser,
            final ViewDefinition viewDefinition) throws ViewDefinitionParserNodeException {
        String template = parser.getStringAttribute(groupNode, "template");
        SecurityRole role = parser.getAuthorizationRole(groupNode);

        if (template == null) {
            String groupName = parser.getStringAttribute(groupNode, NAME);
            if (groupName == null) {
                throw new ViewDefinitionParserNodeException(groupNode, "Name attribute cannot be empty");
            }
            InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(groupName, role);

            NodeList childNodes = groupNode.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);

                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    ribbonGroup.addItem(parseRibbonItem(child, parser, viewDefinition));
                }
            }

            return ribbonGroup;
        } else {
            try {
                return ribbonTemplates.getGroupTemplate(template, viewDefinition, role);
            } catch (IllegalStateException e) {
                throw new ViewDefinitionParserNodeException(groupNode, e);
            }
        }
    }

    public InternalRibbonActionItem parseRibbonItem(final Node itemNode, final ViewDefinitionParser parser,
            final ViewDefinition viewDefinition) throws ViewDefinitionParserNodeException {
        String stringType = itemNode.getNodeName();

        RibbonActionItem.Type type = null;
        if ("bigButtons".equals(stringType) || "bigButton".equals(stringType)) {
            type = RibbonActionItem.Type.BIG_BUTTON;
        } else if ("smallButtons".equals(stringType) || "smallButton".equals(stringType)) {
            type = RibbonActionItem.Type.SMALL_BUTTON;
        } else if ("combobox".equals(stringType)) {
            type = RibbonActionItem.Type.COMBOBOX;
        } else if ("smallEmptySpace".equals(stringType)) {
            type = RibbonActionItem.Type.SMALL_EMPTY_SPACE;
        } else {
            throw new ViewDefinitionParserNodeException(itemNode, "Unsupported ribbon item type '" + stringType + "'");
        }

        InternalRibbonActionItem item = null;
        if ("bigButtons".equals(stringType) || "smallButtons".equals(stringType)) {
            item = new RibbonComboItemImpl();
        } else if ("combobox".equals(stringType)) {
            item = new RibbonComboBoxImpl();
        } else {
            item = new RibbonActionItemImpl();
        }

        item.setIcon(parser.getStringAttribute(itemNode, "icon"));
        item.setName(parser.getStringAttribute(itemNode, NAME));
        String accesskey = parser.getStringAttribute(itemNode, "accesskey");
        if (accesskey != null) {
            item.setAccesskey(accesskey);
        }
        item.setAction(RibbonUtils.translateRibbonAction(parser.getStringAttribute(itemNode, "action"), viewDefinition));
        item.setType(type);
        String state = parser.getStringAttribute(itemNode, "state");
        if (state == null) {
            item.setEnabled(true);
        } else {
            if ("enabled".equals(state)) {
                item.setEnabled(true);
            } else if ("disabled".equals(state)) {
                item.setEnabled(false);
            } else {
                throw new ViewDefinitionParserNodeException(itemNode, "Unsupported ribbon item state : " + state);
            }
        }
        item.setDefaultEnabled(item.isEnabled());
        String message = parser.getStringAttribute(itemNode, "message");
        if (message != null) {
            item.setMessage(message);
        }

        NodeList childNodes = itemNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && "script".equals(child.getNodeName())) {
                item.setScript(parser.getStringNodeContent(child));
            }
        }

        if (item instanceof RibbonComboItem) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE && !"script".equals(child.getNodeName())) {
                    ((InternalRibbonComboItem) item).addItem(parseRibbonItem(child, parser, viewDefinition));
                }
            }
        } else if (item instanceof RibbonComboBox) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE && !"script".equals(child.getNodeName())) {
                    if (!"option".equals(child.getNodeName())) {
                        throw new ViewDefinitionParserNodeException(child, "ribbon combobox can only have 'option' elements");
                    }
                    ((RibbonComboBox) item).addOption(parser.getStringAttribute(child, NAME));
                }
            }
        } else {
            (item).setAction(RibbonUtils.translateRibbonAction(parser.getStringAttribute(itemNode, "action"), viewDefinition));
        }

        return item;
    }

}
