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
package com.qcadoo.view.internal.components.window;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.patterns.AbstractContainerPattern;
import com.qcadoo.view.internal.ribbon.RibbonUtils;
import com.qcadoo.view.internal.ribbon.model.InternalRibbon;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;

public class WindowComponentPattern extends AbstractContainerPattern {

    private static final String JSP_PATH = "containers/window.jsp";

    private static final String JS_OBJECT = "QCD.components.containers.Window";

    private static final String HEADER = "header";

    private Boolean header;

    private Boolean fixedHeight;

    private InternalRibbon ribbon;

    private boolean hasRibbon = true;

    private String firstTabName;

    private WindowTabComponentPattern mainTab;

    public WindowComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new WindowComponentState(this);
    }

    public void setHeader(final Boolean header) {
        this.header = header;
    }

    public void setFixedHeight(final Boolean fixedHeight) {
        this.fixedHeight = fixedHeight;
    }

    @Override
    public void parse(final Node componentNode, final ViewDefinitionParser parser) throws ViewDefinitionParserNodeException {
        super.parseWithoutChildren(componentNode, parser);

        Node ribbonNode = null;

        NodeList childNodes = componentNode.getChildNodes();

        Boolean tabMode = null;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("ribbon".equals(child.getNodeName())) {
                if (ribbonNode != null) {
                    throw new ViewDefinitionParserNodeException(child, "Window can contain only one ribbon");
                }
                ribbonNode = child;

            } else if ("windowTab".equals(child.getNodeName())) {

                if (tabMode != null && !tabMode) {
                    throw new ViewDefinitionParserNodeException(child, "Window cannot have both 'windowTab' and 'component' tags");
                }
                tabMode = true;

                WindowTabComponentPattern tab = new WindowTabComponentPattern(parser.getComponentDefinition(child, this,
                        getViewDefinition()));
                tab.parse(child, parser);
                addChild(tab);
                if (firstTabName == null) {
                    firstTabName = tab.getName();
                }

            } else if ("component".equals(child.getNodeName())) {

                if (tabMode != null && tabMode) {
                    throw new ViewDefinitionParserNodeException(child, "Window cannot have both 'windowTab' and 'component' tags");
                }
                tabMode = false;

                if (mainTab == null) {
                    ComponentDefinition componentDefinition = new ComponentDefinition();
                    componentDefinition.setName("mainTab");
                    componentDefinition.setParent(this);
                    componentDefinition.setTranslationService(getTranslationService());
                    componentDefinition.setContextualHelpService(getContextualHelpService());
                    componentDefinition.setViewDefinition(getViewDefinition());
                    componentDefinition.setReference("mainTab");
                    componentDefinition.setDataDefinition(null);
                    mainTab = new WindowTabComponentPattern(componentDefinition);
                    addChild(mainTab);
                    firstTabName = mainTab.getName();
                }

                mainTab.addChild(parser.parseComponent(child, mainTab));

            } else if ("option".equals(child.getNodeName())) {

                String type = parser.getStringAttribute(child, "type");
                String value = parser.getStringAttribute(child, "value");
                if (HEADER.equals(type)) {
                    header = Boolean.parseBoolean(value);
                } else if ("fixedHeight".equals(type)) {
                    fixedHeight = Boolean.parseBoolean(value);
                } else {
                    throw new ViewDefinitionParserNodeException(child, "Unknown option for window: " + type);
                }

            } else if (!"listener".equals(child.getNodeName()) && !"script".equals(child.getNodeName())) {
                throw new ViewDefinitionParserNodeException(child, "Unknown tag for window: " + child.getNodeName());
            }
        }

        if (header == null) {
            header = parser.getBooleanAttribute(componentNode, HEADER, true);
        }
        if (fixedHeight == null) {
            fixedHeight = parser.getBooleanAttribute(componentNode, "fixedHeight", false);
        }

        hasRibbon = parser.getBooleanAttribute(componentNode, "ribbon", true);

        if (ribbonNode != null) {
            setRibbon(parser.parseRibbon(ribbonNode, getViewDefinition()));
        }

    }

    @Override
    protected Map<String, Object> getJspOptions(final Locale locale) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(HEADER, header);
        options.put("oneTab", this.getChildren().size() < 2);
        options.put("hasRibbon", hasRibbon);
        return options;
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("fixedHeight", fixedHeight);
        json.put(HEADER, header);
        json.put("oneTab", this.getChildren().size() < 2);
        json.put("hasRibbon", hasRibbon);
        if (ribbon != null) {
            json.put("ribbon", RibbonUtils.translateRibbon(ribbon, locale, this));
        }
        json.put("firstTabName", firstTabName);
        JSONObject translations = new JSONObject();
        for (String childName : getChildren().keySet()) {
            translations.put(
                    "tab." + childName,
                    getTranslationService().translate(getTranslationPath() + "." + childName + ".tabLabel",
                            "qcadooView.tabs." + childName + ".tabLabel", locale));
        }

        translations.put("contextualHelpTooltip",
                getTranslationService().translate("qcadooView.tooltip.contextualHelpButton", locale));

        json.put("translations", translations);
        return json;
    }

    public void setRibbon(final InternalRibbon ribbon) {
        this.ribbon = ribbon;
    }

    public InternalRibbon getRibbon() {
        return ribbon;
    }

    @Override
    public String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    public String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    public String getJsObjectName() {
        return JS_OBJECT;
    }

    public String getFirstTabName() {
        return firstTabName;
    }

}
