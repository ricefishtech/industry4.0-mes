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
package com.qcadoo.view.internal.xml;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.ContainerPattern;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.hooks.ViewEventListenerHook;
import com.qcadoo.view.internal.ribbon.model.InternalRibbon;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonActionItem;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonGroup;

public interface ViewDefinitionParser {

    ComponentOption parseOption(final Node option);

    ComponentPattern parseComponent(final Node componentNode, final ContainerPattern parent)
            throws ViewDefinitionParserNodeException;

    ComponentDefinition getComponentDefinition(final Node componentNode, final ContainerPattern parent,
            final ViewDefinition viewDefinition);

    ViewEventListenerHook parseEventListener(Node listenerNode) throws ViewDefinitionParserNodeException;

    String getStringAttribute(final Node groupNode, final String string);

    String getStringNodeContent(final Node node);

    Boolean getBooleanAttribute(final Node node, final String name, final boolean defaultValue);

    List<Node> geElementChildren(final Node node);

    Node getRootOfXmlDocument(final Resource xmlFile);

    InternalViewDefinition parseViewXml(final Resource viewXml, final String pluginIdentifier);

    ViewExtension getViewExtensionNode(final InputStream resource, final String tagType) throws ViewDefinitionParserNodeException;

    InternalRibbon parseRibbon(final Node groupNode, final ViewDefinition viewDefinition)
            throws ViewDefinitionParserNodeException;

    InternalRibbonGroup parseRibbonGroup(final Node groupNode, final ViewDefinition viewDefinition)
            throws ViewDefinitionParserNodeException;

    InternalRibbonActionItem parseRibbonItem(final Node itemNode, final ViewDefinition viewDefinition)
            throws ViewDefinitionParserNodeException;

    void checkState(final boolean state, final Node node, final String message) throws ViewDefinitionParserNodeException;

    SecurityRole getAuthorizationRole(final Node node) throws ViewDefinitionParserNodeException;

}
