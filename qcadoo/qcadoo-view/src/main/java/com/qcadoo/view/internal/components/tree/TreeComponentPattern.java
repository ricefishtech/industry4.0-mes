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
package com.qcadoo.view.internal.components.tree;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.TreeType;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.components.FieldComponentPattern;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;

public final class TreeComponentPattern extends FieldComponentPattern {

    private static final String JSP_PATH = "elements/tree.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.Tree";

    private final Map<String, TreeDataType> dataTypes = new LinkedHashMap<String, TreeDataType>();

    private boolean hasNewButtons = true;

    private boolean hasDeleteButton = true;

    private boolean hasCustomActionButton = false;

    private boolean hasEditButton = true;

    private boolean hasMoveButton = true;

    private boolean selectableWhenDisabled = false;

    private String customActionIcon = "collapseAllIcon16.png";

    public TreeComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new TreeComponentState(getFieldDefinition(), dataTypes, this);
    }

    @Override
    public void parse(final Node componentNode, final ViewDefinitionParser parser) throws ViewDefinitionParserNodeException {
        super.parse(componentNode, parser);
        NodeList childNodes = componentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("dataType".equals(child.getNodeName())) {
                String dataTypeName = parser.getStringAttribute(child, "name");
                TreeDataType dataType = new TreeDataType(dataTypeName);
                NodeList dataTypeOptionNodes = child.getChildNodes();
                for (int dton = 0; dton < dataTypeOptionNodes.getLength(); dton++) {
                    Node dataTypeOptionNode = dataTypeOptionNodes.item(dton);
                    if (dataTypeOptionNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    if (!"option".equals(dataTypeOptionNode.getNodeName())) {
                        throw new ViewDefinitionParserNodeException(dataTypeOptionNode,
                                "Tree 'dataType' node can only contains 'option' nodes");
                    }
                    String optionType = parser.getStringAttribute(dataTypeOptionNode, "type");
                    String optionValue = parser.getStringAttribute(dataTypeOptionNode, "value");
                    dataType.setOption(optionType, optionValue);
                }
                try {
                    dataType.validate();
                } catch (IllegalStateException e) {
                    throw new ViewDefinitionParserNodeException(child, e);
                }
                dataTypes.put(dataTypeName, dataType);
            }
        }
        if (dataTypes.isEmpty()) {
            throw new ViewDefinitionParserNodeException(componentNode, "Tree must contains at least one 'dataType' node");
        }
    }

    @Override
    protected void initializeComponent() throws JSONException {
        for (ComponentOption option : getOptions()) {
            if ("hasNewButtons".equals(option.getType())) {
                hasNewButtons = Boolean.parseBoolean(option.getValue());
            } else if ("hasDeleteButton".equals(option.getType())) {
                hasDeleteButton = Boolean.parseBoolean(option.getValue());
            } else if ("hasCustomActionButton".equals(option.getType())) {
                hasCustomActionButton = Boolean.parseBoolean(option.getValue());
            } else if ("hasEditButton".equals(option.getType())) {
                hasEditButton = Boolean.parseBoolean(option.getValue());
            } else if ("hasMoveButton".equals(option.getType())) {
                hasMoveButton = Boolean.parseBoolean(option.getValue());
            } else if ("selectableWhenDisabled".equals(option.getType())) {
                selectableWhenDisabled = Boolean.parseBoolean(option.getValue());
            } else if ("customActionIcon".equals(option.getType())) {
                customActionIcon = String.valueOf(option.getValue());
            }
        }
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = new JSONObject();
        Map<String, JSONObject> dataTypesTemp = new LinkedHashMap<String, JSONObject>();
        for (Map.Entry<String, TreeDataType> dataTypeEntry : dataTypes.entrySet()) {
            dataTypesTemp.put(dataTypeEntry.getKey(), dataTypeEntry.getValue().toJson());
        }
        JSONObject dataTypesObject = new JSONObject(dataTypesTemp);

        json.put("dataTypes", dataTypesObject);

        json.put("belongsToFieldName", getBelongsToFieldDefinition().getName());

        JSONObject buttonsOptions = new JSONObject();
        buttonsOptions.put("hasNewButtons", hasNewButtons);
        buttonsOptions.put("hasDeleteButton", hasDeleteButton);
        buttonsOptions.put("hasCustomActionButton", hasCustomActionButton);
        buttonsOptions.put("hasEditButton", hasEditButton);
        buttonsOptions.put("hasMoveButton", hasMoveButton);
        buttonsOptions.put("customActionIcon", customActionIcon);

        json.put("buttonsOptions", buttonsOptions);

        json.put("selectableWhenDisabled", selectableWhenDisabled);

        JSONObject translations = new JSONObject();
        for (String dataTypeName : dataTypes.keySet()) {
            translations.put("newButton_" + dataTypeName, getTranslation("newButton." + dataTypeName, locale));
        }

        translations.put("newButton", getTranslation("newButton", locale));
        translations.put("editButton", getTranslation("editButton", locale));
        translations.put("deleteButton", getTranslation("deleteButton", locale));
        translations.put("confirmDeleteMessage", getTranslation("confirmDeleteMessage", locale));

        translations.put("moveModeButton", getTranslation("moveModeButton", locale));
        translations.put("moveModeSaveButton", getTranslation("moveModeSaveButton", locale));
        translations.put("moveModeCancelButton", getTranslation("moveModeCancelButton", locale));
        translations.put("moveModeCancelButtonConfirm", getTranslation("moveModeCancelButtonConfirm", locale));

        translations.put("header", getTranslationService().translate(getTranslationPath() + ".header", locale));
        translations.put("customActionTitle",
                getTranslationService().translate(getTranslationPath() + ".customActionTitle", locale));
        translations.put("customActionConfirm",
                getTranslationService().translate(getTranslationPath() + ".customActionConfirm", locale));

        translations.put("moveModeInfoHeader", getTranslation("moveModeInfoHeader", locale));
        translations.put("moveModeInfoContent", getTranslation("moveModeInfoContent", locale));

        translations.put("expandTreeButton", getTranslation("expandTreeButton", locale));
        translations.put("collapseTreeButton", getTranslation("collapseTreeButton", locale));

        translations.put("loading", getTranslationService().translate("qcadooView.loading", locale));

        json.put("translations", translations);

        return json;
    }

    private String getTranslation(final String key, final Locale locale) throws JSONException {
        return getTranslationService().translate(getTranslationPath() + "." + key, "qcadooView.tree." + key, locale);
    }

    private FieldDefinition getBelongsToFieldDefinition() {
        if (getFieldDefinition() != null && TreeType.class.isAssignableFrom(getFieldDefinition().getType().getClass())) {
            TreeType treeType = (TreeType) getFieldDefinition().getType();
            return treeType.getDataDefinition().getField(treeType.getJoinFieldName());
        }
        throw new IllegalStateException("Field has to be a tree one");
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
}
