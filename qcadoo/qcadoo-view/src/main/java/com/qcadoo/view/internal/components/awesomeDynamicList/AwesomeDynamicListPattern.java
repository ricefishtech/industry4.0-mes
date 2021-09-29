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
package com.qcadoo.view.internal.components.awesomeDynamicList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.ContainerPattern;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.components.FieldComponentPattern;
import com.qcadoo.view.internal.components.form.FormComponentPattern;
import com.qcadoo.view.internal.components.layout.FlowLayoutPattern;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;

public class AwesomeDynamicListPattern extends FieldComponentPattern {

    private static final String JS_OBJECT = "QCD.components.elements.AwesomeDynamicList";

    private static final String JSP_PATH = "elements/awesomeDynamicList.jsp";

    private final FormComponentPattern innerFormPattern;

    private ComponentPattern headerFormPattern;

    private boolean hasButtons = true;

    private boolean hasBorder = true;

    private boolean flipOrder = false;

    private final Map<String, ComponentPattern> children = new LinkedHashMap<>();

    @Override
    public void unregisterComponent(InternalViewDefinitionService viewDefinitionService) {
        super.unregisterComponent(viewDefinitionService);

        for (ComponentPattern componentPattern : children.values()) {
            componentPattern.unregisterComponent(viewDefinitionService);
        }
    }

    public AwesomeDynamicListPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
        ComponentDefinition formComponentDefinition = new ComponentDefinition();
        formComponentDefinition.setName("innerForm_@innerFormId");
        formComponentDefinition.setFieldPath(null);
        formComponentDefinition.setSourceFieldPath(null);
        formComponentDefinition.setTranslationService(getTranslationService());
        formComponentDefinition.setApplicationContext(getApplicationContext());
        formComponentDefinition.setViewDefinition(getViewDefinition());
        formComponentDefinition.setParent(this);
        formComponentDefinition.setContextualHelpService(getContextualHelpService());
        innerFormPattern = new FormComponentPattern(formComponentDefinition);
        children.put(innerFormPattern.getName(), innerFormPattern);
    }

    @Override
    protected void initializeComponent() throws JSONException {
        initializeComponent(innerFormPattern);
        if (headerFormPattern != null) {
            initializeComponent(headerFormPattern);
        }
        for (ComponentOption option : getOptions()) {
            if ("hasButtons".equals(option.getType())) {
                hasButtons = Boolean.parseBoolean(option.getValue());
            } else if ("hasBorder".equals(option.getType())) {
                hasBorder = Boolean.parseBoolean(option.getValue());
            } else if ("flipOrder".equals(option.getType())) {
                flipOrder = Boolean.parseBoolean(option.getValue());
            } else {
                throw new IllegalStateException("Unknown option for AwesomeDynamicList: " + option.getType());
            }
        }
    }

    @Override
    protected void registerComponentViews(final InternalViewDefinitionService viewDefinitionService) {
        innerFormPattern.registerViews(viewDefinitionService);
        if (headerFormPattern != null) {
            headerFormPattern.registerViews(viewDefinitionService);
        }
    }

    private void initializeComponent(final ComponentPattern component) {
        component.initialize();
        if (component instanceof ContainerPattern) {
            ContainerPattern container = (ContainerPattern) component;
            for (ComponentPattern kids : container.getChildren().values()) {
                initializeComponent(kids);
            }
        }
    }

    @Override
    public void parse(final Node componentNode, final ViewDefinitionParser parser) throws ViewDefinitionParserNodeException {
        super.parse(componentNode, parser);
        NodeList childNodes = componentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if ("components".equals(child.getNodeName())) {
                innerFormPattern.parse(child, parser);
            } else if ("header".equals(child.getNodeName())) {
                ComponentDefinition formComponentDefinition = new ComponentDefinition();
                formComponentDefinition.setName("header");
                formComponentDefinition.setFieldPath(null);
                formComponentDefinition.setSourceFieldPath(null);
                formComponentDefinition.setTranslationService(getTranslationService());
                formComponentDefinition.setViewDefinition(getViewDefinition());
                formComponentDefinition.setParent(this);
                formComponentDefinition.setContextualHelpService(getContextualHelpService());
                headerFormPattern = new FlowLayoutPattern(formComponentDefinition);
                headerFormPattern.parse(child, parser);
                children.put(headerFormPattern.getName(), headerFormPattern);
            }
        }
    }

    public FormComponentPattern getFormComponentPattern() {
        return innerFormPattern;
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("hasButtons", hasButtons);
        json.put("hasBorder", hasBorder);
        json.put("flipOrder", flipOrder);
        return json;
    }

    @Override
    protected Map<String, Object> getJspOptions(final Locale locale) {
        Map<String, Object> options = new HashMap<>();
        options.put("innerForm", innerFormPattern.prepareView(locale));
        options.put("hasBorder", hasBorder);
        options.put("hasButtons", hasButtons);
        if (headerFormPattern != null) {
            options.put("header", headerFormPattern.prepareView(locale));
        }
        return options;
    }

    @Override
    protected String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    protected String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    protected String getJsObjectName() {
        return JS_OBJECT;
    }

    @Override
    protected ComponentState getComponentStateInstance() {
        return new AwesomeDynamicListState(this, innerFormPattern);
    }

}
