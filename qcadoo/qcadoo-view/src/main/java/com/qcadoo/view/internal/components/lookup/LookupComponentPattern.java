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
package com.qcadoo.view.internal.components.lookup;

import static com.google.common.base.Preconditions.checkState;
import static org.springframework.util.StringUtils.hasText;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.ImmutableMap;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.JoinFieldHolder;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ribbon.RibbonActionItem.Type;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.CriteriaModifier;
import com.qcadoo.view.internal.ModalDimensions;
import com.qcadoo.view.internal.RowStyleResolver;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.components.FieldComponentPattern;
import com.qcadoo.view.internal.components.grid.GridComponentPattern;
import com.qcadoo.view.internal.components.window.WindowComponentPattern;
import com.qcadoo.view.internal.internal.ViewDefinitionImpl;
import com.qcadoo.view.internal.ribbon.model.InternalRibbon;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonActionItem;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonGroup;
import com.qcadoo.view.internal.ribbon.model.RibbonActionItemImpl;
import com.qcadoo.view.internal.ribbon.model.RibbonGroupImpl;
import com.qcadoo.view.internal.ribbon.model.RibbonImpl;
import com.qcadoo.view.internal.ribbon.model.SingleRibbonGroupPack;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;

public class LookupComponentPattern extends FieldComponentPattern {

    private static final String L_LOOKUP_CODE = "lookupCode";

    private static final String L_VALUE = "value";

    private static final String L_TRUE = "true";

    private static final String JSP_PATH = "elements/lookup.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.Lookup";

    private boolean textRepresentationOnDisabled;

    private boolean boldTextRepresentationOnDisabled;

    private String expression;

    private String fieldCode;

    private boolean header = false;

    private boolean prioritizable = true;

    private boolean onlyActive = true;

    private ModalDimensions modalDimensions;

    private InternalViewDefinition lookupViewDefinition;

    private RowStyleResolver rowStyleResolver;

    private CriteriaModifier criteriaModifier;

    public LookupComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    public ComponentState getComponentStateInstance() {
        if (getScopeFieldDefinition() == null) {
            return new LookupComponentState(null, fieldCode, expression, this);
        }

        String joinFieldName = null;
        if (getScopeFieldDefinition().getType() instanceof JoinFieldHolder) {
            joinFieldName = ((JoinFieldHolder) getScopeFieldDefinition().getType()).getJoinFieldName();
        }
        FieldDefinition fieldDefinition = getDataDefinition().getField(joinFieldName);
        return new LookupComponentState(fieldDefinition, fieldCode, expression, this);
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

    @Override
    public void parse(final Node componentNode, final ViewDefinitionParser parser) throws ViewDefinitionParserNodeException {
        super.parse(componentNode, parser);
        final NodeList childNodes = componentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (RowStyleResolver.NODE_NAME.equals(child.getNodeName())) {
                rowStyleResolver = new RowStyleResolver(child, parser, getApplicationContext());
            } else if (CriteriaModifier.NODE_NAME.equals(child.getNodeName())) {
                criteriaModifier = new CriteriaModifier(child, parser, getApplicationContext());
            }
        }
    }

    @Override
    protected void initializeComponent() throws JSONException {
        super.initializeComponent();

        for (ComponentOption option : getOptions()) {
            if ("expression".equals(option.getType())) {
                expression = option.getValue();
            } else if ("fieldCode".equals(option.getType())) {
                fieldCode = option.getValue();
            } else if ("header".equals(option.getType())) {
                header = Boolean.parseBoolean(option.getValue());
            } else if ("prioritizable".equals(option.getType())) {
                prioritizable = Boolean.parseBoolean(option.getValue());
            } else if ("onlyActive".equals(option.getType())) {
                onlyActive = Boolean.parseBoolean(option.getValue());
            } else if ("textRepresentationOnDisabled".equals(option.getType())) {
                textRepresentationOnDisabled = Boolean.parseBoolean(option.getValue());
            } else if ("boldTextRepresentationOnDisabled".equals(option.getType())) {
                Boolean optionValue = Boolean.parseBoolean(option.getValue());
                textRepresentationOnDisabled = optionValue;
                boldTextRepresentationOnDisabled = optionValue;
            }
        }

        modalDimensions = ModalDimensions.parseFromOptions(getOptions());

        checkState(hasText(fieldCode), "Missing fieldCode for lookup");
        checkState(hasText(expression), "Missing expression for lookup");

        String viewName = getViewName();

        DataDefinition dataDefinition = getDataDefinition();

        if (getScopeFieldDefinition() != null) {
            dataDefinition = getScopeFieldDefinition().getDataDefinition();
        }

        lookupViewDefinition = new ViewDefinitionImpl(viewName, getViewDefinition().getPluginIdentifier(), dataDefinition, false,
                getTranslationService());

        WindowComponentPattern window = createWindowComponentPattern(lookupViewDefinition);

        GridComponentPattern grid = createGridComponentPattern(lookupViewDefinition, window);

        for (ComponentOption option : getOptions()) {
            if ("orderable".equals(option.getType())) {
                Map<String, String> newAttributes = new HashMap<>();
                newAttributes.put(L_VALUE, option.getValue() + ",lookupCode");
                option = new ComponentOption("orderable", newAttributes);
                grid.addOption(option);
            } else if ("searchable".equals(option.getType())) {
                Map<String, String> newAttributes = new HashMap<>();
                newAttributes.put(L_VALUE, option.getValue() + ",lookupCode");
                option = new ComponentOption("searchable", newAttributes);
                grid.addOption(option);
            } else if (!"expression".equals(option.getType()) && !"fieldCode".equals(option.getType())
                    && !"textRepresentationOnDisabled".equals(option.getType()) && !"labelWidth".equals(option.getType())) {
                grid.addOption(option);
            }
        }

        grid.addOption(new ComponentOption("lookup", Collections.singletonMap(L_VALUE, L_TRUE)));

        window.addChild(grid);

        lookupViewDefinition.addComponentPattern(window);
        lookupViewDefinition.initialize();
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("viewName", getViewName());

        json.put("modalDimensions", modalDimensions.toJson());

        JSONObject translations = new JSONObject();

        if (getFieldDefinition() == null) {
            translations.put("labelOnFocus", getTranslationService().translate(getTranslationPath() + ".label.focus", locale));
        } else {
            String code = getFieldDefinition().getDataDefinition().getPluginIdentifier() + "."
                    + getFieldDefinition().getDataDefinition().getName() + "." + getFieldDefinition().getName() + ".label.focus";
            translations.put("labelOnFocus",
                    getTranslationService().translate(getTranslationPath() + ".label.focus", code, locale));
        }

        translations.put(
                "noMatchError",
                getTranslationService().translate(getTranslationPath() + ".noMatchError", "qcadooView.lookup.noMatchError",
                        locale));
        translations.put(
                "moreTahnOneMatchError",
                getTranslationService().translate(getTranslationPath() + ".moreTahnOneMatchError",
                        "qcadooView.lookup.moreTahnOneMatchError", locale));
        translations.put(
                "noResultsInfo",
                getTranslationService().translate(getTranslationPath() + ".noResultsInfo", "qcadooView.lookup.noResultsInfo",
                        locale));
        translations.put(
                "tooManyResultsInfo",
                getTranslationService().translate(getTranslationPath() + ".tooManyResultsInfo",
                        "qcadooView.lookup.tooManyResultsInfo", locale));

        json.put("translations", translations);

        return json;
    }

    @Override
    protected Map<String, Object> getJspOptions(final Locale locale) {
        Map<String, Object> options = super.getJspOptions(locale);
        options.put("textRepresentationOnDisabled", textRepresentationOnDisabled);
        options.put("boldTextRepresentationOnDisabled", boldTextRepresentationOnDisabled);
        return options;
    }

    @Override
    protected void registerComponentViews(final InternalViewDefinitionService viewDefinitionService) {
        viewDefinitionService.save(lookupViewDefinition);
    }

    @Override
    protected void unregisterComponentViews(final InternalViewDefinitionService viewDefinitionService) {
        viewDefinitionService.delete(lookupViewDefinition);
    }

    public CriteriaModifier getCriteriaModifier() {
        return criteriaModifier;
    }

    private GridComponentPattern createGridComponentPattern(final ViewDefinition lookupViewDefinition,
            final WindowComponentPattern window) {
        final ComponentDefinition gridComponentDefinition = new ComponentDefinition();
        gridComponentDefinition.setName("grid");
        gridComponentDefinition.setTranslationService(getTranslationService());
        gridComponentDefinition.setApplicationContext(getApplicationContext());
        gridComponentDefinition.setViewDefinition(lookupViewDefinition);
        gridComponentDefinition.setParent(window);
        gridComponentDefinition.setContextualHelpService(getContextualHelpService());
        gridComponentDefinition.setReference("grid");

        if (getScopeFieldDefinition() != null) {
            gridComponentDefinition.setSourceFieldPath(getScopeFieldDefinition().getName());
        }

        final GridComponentPattern grid = new GridComponentPattern(gridComponentDefinition);
        grid.setRowStyleResolver(rowStyleResolver);
        grid.setCriteriaModifier(criteriaModifier);
        grid.addOption(new ComponentOption("lookup", ImmutableMap.of(L_VALUE, L_TRUE)));
        grid.addOption(new ComponentOption("fullscreen", ImmutableMap.of(L_VALUE, L_TRUE)));
        grid.addOption(new ComponentOption("orderable", ImmutableMap.of(L_VALUE, L_LOOKUP_CODE)));
        grid.addOption(new ComponentOption("order", ImmutableMap.of("column", L_LOOKUP_CODE, "direction", "asc")));
        grid.addOption(new ComponentOption("searchable", ImmutableMap.of(L_VALUE, L_LOOKUP_CODE)));
        grid.addOption(new ComponentOption("prioritizable", ImmutableMap.of(L_VALUE, Boolean.toString(prioritizable))));
        grid.addOption(new ComponentOption("onlyActive", ImmutableMap.of(L_VALUE, Boolean.toString(onlyActive))));
        grid.addOption(createLookupCodeColumn());
        grid.addOption(createLookupValueColumn());

        return grid;
    }

    private ComponentOption createLookupValueColumn() {
        Map<String, String> valueColumnOptions = new HashMap<>();
        valueColumnOptions.put("name", "lookupValue");
        valueColumnOptions.put("expression", expression);
        valueColumnOptions.put("hidden", L_TRUE);
        return new ComponentOption("column", valueColumnOptions);
    }

    private ComponentOption createLookupCodeColumn() {
        Map<String, String> codeVisibleColumnOptions = new HashMap<>();
        codeVisibleColumnOptions.put("name", L_LOOKUP_CODE);
        codeVisibleColumnOptions.put("fields", fieldCode);
        codeVisibleColumnOptions.put("hidden", "false");
        codeVisibleColumnOptions.put("link", L_TRUE);
        return new ComponentOption("column", codeVisibleColumnOptions);
    }

    private WindowComponentPattern createWindowComponentPattern(final ViewDefinition lookupViewDefinition) {
        ComponentDefinition windowComponentDefinition = new ComponentDefinition();
        windowComponentDefinition.setName("window");
        windowComponentDefinition.setTranslationService(getTranslationService());
        windowComponentDefinition.setApplicationContext(getApplicationContext());
        windowComponentDefinition.setViewDefinition(lookupViewDefinition);
        windowComponentDefinition.setContextualHelpService(getContextualHelpService());

        WindowComponentPattern window = new WindowComponentPattern(windowComponentDefinition);
        window.setFixedHeight(true);
        window.setHeader(header);
        window.setRibbon(createRibbon());

        return window;
    }

    private InternalRibbon createRibbon() {
        InternalRibbonActionItem ribbonSelectActionItem = new RibbonActionItemImpl();
        ribbonSelectActionItem.setName("select");
        ribbonSelectActionItem.setIcon("acceptIcon24.png");
        ribbonSelectActionItem.setAction("#{window.grid}.performLinkClicked();");
        ribbonSelectActionItem.setType(Type.BIG_BUTTON);
        ribbonSelectActionItem.setEnabled(false);
        ribbonSelectActionItem.setDefaultEnabled(false);
        ribbonSelectActionItem.setMessage("#{translate(noRecordSelected)}");
        ribbonSelectActionItem
                .setScript("#{grid}.addOnChangeListener({onChange: function(selectedArray) {if (!selectedArray || selectedArray.length == 0) {"
                        + "this.disable('#{translate(noRecordSelected)}');} else {this.enable();}}});");

        InternalRibbonActionItem ribbonCancelActionItem = new RibbonActionItemImpl();
        ribbonCancelActionItem.setName("cancel");
        ribbonCancelActionItem.setIcon("cancelIcon24.png");
        ribbonCancelActionItem.setAction("#{window}.closeThisModalWindow(false)");
        ribbonCancelActionItem.setType(Type.BIG_BUTTON);
        ribbonCancelActionItem.setEnabled(true);

        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl("navigation");
        ribbonGroup.addItem(ribbonSelectActionItem);
        ribbonGroup.addItem(ribbonCancelActionItem);

        InternalRibbon ribbon = new RibbonImpl();
        ribbon.addGroupsPack(new SingleRibbonGroupPack(ribbonGroup));

        return ribbon;
    }

    private String getViewName() {
        return getViewDefinition().getName() + "." + getFunctionalPath() + ".lookup";
    }

    boolean isOnlyActive() {
        return onlyActive;
    }

}
