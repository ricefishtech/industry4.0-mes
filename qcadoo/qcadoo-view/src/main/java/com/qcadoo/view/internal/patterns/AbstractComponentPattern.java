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
package com.qcadoo.view.internal.patterns;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.DataDefinitionHolder;
import com.qcadoo.model.constants.VersionableConstants;
import com.qcadoo.plugin.api.PluginUtils;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.FieldEntityIdChangeListener;
import com.qcadoo.view.internal.ScopeEntityIdChangeListener;
import com.qcadoo.view.internal.api.*;
import com.qcadoo.view.internal.hooks.ViewEventListenerHook;
import com.qcadoo.view.internal.states.AbstractComponentState;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserImpl;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.util.StringUtils.hasText;

public abstract class AbstractComponentPattern implements ComponentPattern {

    protected static final String JSP_PATH = "AbstractJspPath";

    protected static final String JS_PATH = null;

    protected static final String JS_OBJECT = "AbstractJavascriptObject";

    private final String name;

    private final String uuid;

    private final boolean useDto;

    private String extensionPluginIdentifier;

    private final String fieldPath;

    private final String scopeFieldPath;

    private final ComponentPattern parent;

    private final boolean defaultEnabled;

    private final boolean permanentlyDisabled;

    private final boolean defaultVisible;

    private final boolean hasDescription;

    private final boolean hasLabel;

    private final String reference;

    private final TranslationService translationService;

    private final ContextualHelpService contextualHelpService;

    private final ApplicationContext applicationContext;

    private final InternalViewDefinition viewDefinition;

    private final Map<String, ComponentPattern> fieldEntityIdChangeListeners = Maps.newHashMap();

    private final Map<String, ComponentPattern> scopeEntityIdChangeListeners = Maps.newHashMap();

    private final List<ComponentOption> options = Lists.newArrayList();

    private final List<ViewEventListenerHook> customEventListeners = Lists.newArrayList();

    private String script;
    
    private final List<String> scriptFiles = new ArrayList<>();

    private FieldDefinition fieldDefinition;

    private FieldDefinition scopeFieldDefinition;

    private DataDefinition dataDefinition;

    private boolean initialized;

    private int indexOrder;

    private AbstractComponentPattern fieldComponent;

    private AbstractComponentPattern scopeFieldComponent;

    private boolean persistent;

    public AbstractComponentPattern(final ComponentDefinition componentDefinition) {
        checkArgument(hasText(componentDefinition.getName()), "Component name must be specified");
        this.name = componentDefinition.getName();
        this.uuid = UUID.randomUUID().toString();
        this.useDto = componentDefinition.isUseDto();
        this.extensionPluginIdentifier = componentDefinition.getExtensionPluginIdentifier();
        this.fieldPath = componentDefinition.getFieldPath();
        this.scopeFieldPath = componentDefinition.getSourceFieldPath();
        this.parent = componentDefinition.getParent();
        this.reference = componentDefinition.getReference();
        this.hasDescription = componentDefinition.isHasDescription();
        this.hasLabel = componentDefinition.isHasLabel();
        this.defaultEnabled = componentDefinition.isDefaultEnabled();
        this.permanentlyDisabled = componentDefinition.isPermanentlyDisabled();
        this.defaultVisible = componentDefinition.isDefaultVisible();
        this.translationService = componentDefinition.getTranslationService();
        this.contextualHelpService = componentDefinition.getContextualHelpService();
        this.dataDefinition = componentDefinition.getDataDefinition();
        this.applicationContext = componentDefinition.getApplicationContext();
        this.viewDefinition = (InternalViewDefinition) componentDefinition.getViewDefinition();
        this.viewDefinition.registerComponent(getReference(), getPath(), this);
    }

    protected abstract String getJspFilePath();

    protected abstract String getJsFilePath();

    protected abstract String getJsObjectName();

    protected abstract ComponentState getComponentStateInstance();

    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        // reimplement me if you want
        return new JSONObject();
    }

    protected Map<String, Object> getJspOptions(final Locale locale) {
        // reimplement me if you want
        return new HashMap<>();
    }

    protected void initializeComponent() throws JSONException {
        // implement me if you want
    }

    protected void registerComponentViews(final InternalViewDefinitionService viewDefinitionService) {
        // implement me if you want
    }

    protected void unregisterComponentViews(final InternalViewDefinitionService viewDefinitionService) {
        // implement me if you want
    }

    protected ComponentPattern getParent() {
        return parent;
    }

    @Override
    public String getExtensionPluginIdentifier() {
        return extensionPluginIdentifier;
    }

    @Override
    public void registerViews(final InternalViewDefinitionService viewDefinitionService) {
        registerComponentViews(viewDefinitionService);
    }

    @Override
    public void unregisterComponent(final InternalViewDefinitionService viewDefinitionService) {
        viewDefinition.unregisterComponent(getReference(), getPath());
        unregisterComponentViews(viewDefinitionService);

        if (fieldComponent != null) {
            if (!fieldComponent.getComponentAndField(fieldPath)[1].equals(VersionableConstants.VERSION_FIELD_NAME)
                    || fieldDefinition != null) {
                fieldComponent.removeFieldEntityIdChangeListener(fieldDefinition.getName());
            }
        }

        if (scopeFieldComponent != null && scopeFieldDefinition != null) {
            scopeFieldComponent.removeScopeEntityIdChangeListener(scopeFieldDefinition.getName());
        }
    }

    @Override
    public InternalComponentState createComponentState(final InternalViewDefinitionState viewDefinitionState) {
        AbstractComponentState state = (AbstractComponentState) getComponentStateInstance();
        state.setDataDefinition(dataDefinition);
        state.setName(name);
        state.setUuid(UUID.randomUUID().toString());
        state.setEnabled(isDefaultEnabled());
        state.setVisible(isDefaultVisible());
        state.setTranslationService(translationService);
        state.setTranslationPath(getTranslationPath());
        for (ViewEventListenerHook customEventListener : customEventListeners) {
            state.registerCustomEvent(customEventListener);
        }
        if (viewDefinitionState != null) {
            viewDefinitionState.registerComponent(getReference(), state);
        }
        return state;
    }

    @Override
    public Map<String, Object> prepareView(final Locale locale) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", getName());
        map.put("uuid", getUuid());
        map.put("path", getPath());
        map.put("indexOrder", indexOrder);
        map.put("jspFilePath", getJspFilePath());
        map.put("jsFilePath", getJsFilePath());
        map.put("jsObjectName", getJsObjectName());
        map.put("hasDescription", isHasDescription());
        map.put("hasLabel", isHasLabel());
        appendContextualHelpPath(map);

        Map<String, Object> jspOptions = getJspOptions(locale);
        jspOptions.put("defaultEnabled", isDefaultEnabled());
        jspOptions.put("defaultRequired", isDefaultRequired());
        jspOptions.put("defaultVisible", isDefaultVisible());
        map.put("jspOptions", jspOptions);

        try {
            JSONObject jsOptions = getJsOptions(locale);
            addListenersToJsOptions(jsOptions);
            jsOptions.put("defaultEnabled", isDefaultEnabled());
            jsOptions.put("defaultRequired", isDefaultRequired());
            jsOptions.put("defaultVisible", isDefaultVisible());
            jsOptions.put("referenceName", reference);
            jsOptions.put("persistent", isPersistent());
            if (script != null) {
                jsOptions.put("script", prepareScript(script, locale));
            }
            jsOptions.put("scriptFiles", scriptFiles);
            map.put("jsOptions", jsOptions);
            
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return map;
    }

    private void appendContextualHelpPath(final Map<String, Object> map) {
        if (getContextualHelpService().isContextualHelpPathsVisible()) {
            map.put("helpPath", getContextualHelpService().getContextualHelpKey(this));
        }
    }

    public String prepareScript(final String scriptBody, final Locale locale) {
        Pattern p = Pattern.compile("#\\{translate\\(.*?\\)\\}");
        Matcher m = p.matcher(scriptBody);
        int lastEnd = 0;
        StringBuilder result = new StringBuilder();
        while (m.find()) {
            String expression = scriptBody.substring(m.start() + 12, m.end() - 2);
            result.append(scriptBody.substring(lastEnd, m.start()));
            if (expression.contains(".")) {
                result.append(translationService.translate(expression, locale));
            } else {
                result.append(translationService.translate("qcadooView.message." + expression, locale));
            }
            lastEnd = m.end();
        }
        if (lastEnd > 0) {
            result.append(scriptBody.substring(lastEnd));
            return result.toString();
        } else {
            return scriptBody;
        }
    }

    protected void prepareComponentView(final Map<String, Object> map, final Locale locale) throws JSONException {
        // implement me if you want
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getUuid() {
        return uuid;
    }

    @Override
    public final String getPath() {
        if (parent == null) {
            return name;
        } else {
            return parent.getPath() + "." + name;
        }
    }

    @Override
    public String getFunctionalPath() {
        if (parent == null) {
            return name;
        } else {
            return parent.getFunctionalPath() + "." + name;
        }
    }

    @Override
    public void initializeAll() {
        initialize();
    }

    @Override
    public boolean initialize() {
        if (initialized) {
            return true;
        }

        viewDefinition.addJsFilePath(getJsFilePath());

        String[] field = null;
        String[] scopeField = null;

        if (dataDefinition == null || useDto) {
            if (fieldPath != null) {
                field = getComponentAndField(fieldPath);
                fieldComponent = (AbstractComponentPattern) (field[0] == null ? parent : viewDefinition
                        .getComponentByReference(field[0]));
                checkNotNull(fieldComponent, "Cannot find field component for " + getPath() + ": " + fieldPath);
                fieldComponent.addFieldEntityIdChangeListener(field[1], this);
            }

            if (scopeFieldPath != null) {
                scopeField = getComponentAndField(scopeFieldPath);
                scopeFieldComponent = (AbstractComponentPattern) (scopeField[0] == null ? parent : viewDefinition
                        .getComponentByReference(scopeField[0]));
                checkNotNull(scopeFieldComponent, "Cannot find sourceField component for " + getPath() + ": " + scopeFieldPath);
                scopeFieldComponent.addScopeEntityIdChangeListener(scopeField[1], this);
            }

            if (isComponentInitialized(fieldComponent) && isComponentInitialized(scopeFieldComponent)) {
                initialized = true;
            } else {
                return false;
            }
        }

        getDataDefinition(viewDefinition, fieldComponent, scopeFieldComponent, dataDefinition);

        getFieldAndScopeFieldDefinitions(field, fieldComponent, scopeField, scopeFieldComponent);

        getDataDefinitionFromFieldDefinition();

        try {
            initializeComponent();
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return true;
    }

    public void addFieldEntityIdChangeListener(final String field, final ComponentPattern listener) {
        fieldEntityIdChangeListeners.put(field, listener);
    }

    public void addScopeEntityIdChangeListener(final String field, final ComponentPattern listener) {
        scopeEntityIdChangeListeners.put(field, listener);
    }

    public void removeFieldEntityIdChangeListener(final String field) {
        fieldEntityIdChangeListeners.remove(field);
    }

    public void removeScopeEntityIdChangeListener(final String field) {
        scopeEntityIdChangeListeners.remove(field);
    }

    public void updateComponentStateListeners(final ViewDefinitionState viewDefinitionState) {
        if (!fieldEntityIdChangeListeners.isEmpty()) {
            AbstractComponentState thisComponentState = (AbstractComponentState) viewDefinitionState
                    .getComponentByReference(getReference());
            for (Map.Entry<String, ComponentPattern> listenerPattern : fieldEntityIdChangeListeners.entrySet()) {
                if (isComponentEnabled(listenerPattern.getValue())) {
                    ComponentState listenerState = viewDefinitionState.getComponentByReference(listenerPattern.getValue()
                            .getReference());
                    if (listenerState != null) {
                        thisComponentState.addFieldEntityIdChangeListener(listenerPattern.getKey(),
                                (FieldEntityIdChangeListener) listenerState);
                    }
                }
            }
        }
        if (!scopeEntityIdChangeListeners.isEmpty()) {
            AbstractComponentState thisComponentState = (AbstractComponentState) viewDefinitionState
                    .getComponentByReference(getReference());
            for (Map.Entry<String, ComponentPattern> listenerPattern : scopeEntityIdChangeListeners.entrySet()) {
                if (isComponentEnabled(listenerPattern.getValue())) {
                    ComponentState listenerState = viewDefinitionState.getComponentByReference(listenerPattern.getValue()
                            .getReference());
                    if (listenerState != null) {
                        thisComponentState.addScopeEntityIdChangeListener(listenerPattern.getKey(),
                                (ScopeEntityIdChangeListener) listenerState);
                    }
                }
            }
        }
    }

    protected boolean isComponentEnabled(final ComponentPattern componentPattern) {
        return componentPattern.getExtensionPluginIdentifier() == null
                || PluginUtils.isEnabled(componentPattern.getExtensionPluginIdentifier());
    }

    protected final Map<String, ComponentPattern> getFieldEntityIdChangeListeners() {
        return fieldEntityIdChangeListeners;
    }

    protected final Map<String, ComponentPattern> getScopeEntityIdChangeListeners() {
        return scopeEntityIdChangeListeners;
    }

    @Override
    public final boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    @Override
    public boolean isPermanentlyDisabled() {
        return permanentlyDisabled;
    }

    protected final boolean isDefaultRequired() {
        if (getFieldDefinition() != null) {
            return getFieldDefinition().isRequired();
        }

        return false;
    }

    protected final boolean isDefaultVisible() {
        return defaultVisible;
    }

    protected final boolean isHasDescription() {
        return hasDescription;
    }

    protected final boolean isHasLabel() {
        return hasLabel;
    }

    public final void addOption(final ComponentOption option) {
        options.add(option);
    }

    protected final List<ComponentOption> getOptions() {
        return options;
    }

    @Override
    public final String getReference() {
        if (reference == null) {
            return getPath();
        }
        return reference;
    }

    @Override
    public final String getContextualHelpUrl() {
        if (getContextualHelpService() != null) {
            return getContextualHelpService().getHelpUrl(this);
        }
        return null;
    }

    protected final FieldDefinition getFieldDefinition() {
        return fieldDefinition;
    }

    protected final FieldDefinition getScopeFieldDefinition() {
        return scopeFieldDefinition;
    }

    public final DataDefinition getDataDefinition() {
        return dataDefinition;
    }

    public final TranslationService getTranslationService() {
        return translationService;
    }

    public ContextualHelpService getContextualHelpService() {
        return contextualHelpService;
    }

    public final ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public final ViewDefinition getViewDefinition() {
        return viewDefinition;
    }

    public void setExtensionPluginIdentifier(final String extensionPluginIdentifier) {
        this.extensionPluginIdentifier = extensionPluginIdentifier;
    }

    public final String getTranslationPath() {
        return getViewDefinition().getPluginIdentifier() + "." + getViewDefinition().getName() + "." + getFunctionalPath();
    }

    private void addListenersToJsOptions(final JSONObject jsOptions) throws JSONException {
        JSONArray listeners = new JSONArray();
        if (!fieldEntityIdChangeListeners.isEmpty() || !scopeEntityIdChangeListeners.isEmpty()) {
            for (ComponentPattern listener : fieldEntityIdChangeListeners.values()) {
                if (isComponentEnabled(listener)) {
                    listeners.put(listener.getPath());
                }
            }
            for (ComponentPattern listener : scopeEntityIdChangeListeners.values()) {
                if (isComponentEnabled(listener)) {
                    listeners.put(listener.getPath());
                }
            }
        }
        if (!customEventListeners.isEmpty()) {
            listeners.put(getPath());
        }
        jsOptions.put("listeners", listeners);
    }

    private boolean isComponentInitialized(final AbstractComponentPattern fieldComponent) {
        return fieldComponent == null || fieldComponent.initialized;
    }

    private void getDataDefinitionFromFieldDefinition() {
        if (fieldDefinition != null) {
            getDataDefinitionFromFieldDefinition(fieldDefinition);
            return;
        }
        if (scopeFieldDefinition != null) {
            getDataDefinitionFromFieldDefinition(scopeFieldDefinition);
            return;
        }
    }

    private void getFieldAndScopeFieldDefinitions(final String[] field, final AbstractComponentPattern fieldComponent,
            final String[] scopeField, final AbstractComponentPattern scopeFieldComponent) {

        if (dataDefinition != null) {
            if (fieldPath != null && field[1] != null) {
                fieldDefinition = fieldComponent.getDataDefinition().getField(field[1]);
                checkNotNullFieldDefinition(fieldDefinition, field, fieldPath);
            }

            if (scopeFieldPath != null && scopeField[1] != null) {
                scopeFieldDefinition = scopeFieldComponent.getDataDefinition().getField(scopeField[1]);
                checkNotNull(scopeFieldDefinition, "Cannot find sourceField definition for " + getPath() + ": " + scopeFieldPath);
            }
        }
    }

    private void getDataDefinition(final ViewDefinition viewDefinition, final AbstractComponentPattern fieldComponent,
            final AbstractComponentPattern scopeFieldComponent, final DataDefinition localDataDefinition) {
        if (fieldPath != null && fieldComponent != null) {
            dataDefinition = fieldComponent.getDataDefinition();
            return;
        }

        if (scopeFieldPath != null && scopeFieldComponent != null) {
            dataDefinition = scopeFieldComponent.getDataDefinition();
            return;
        }

        if (localDataDefinition != null) {
            dataDefinition = localDataDefinition;
            return;
        }

        if (parent != null) {
            dataDefinition = ((AbstractComponentPattern) parent).getDataDefinition();
            return;
        }

        dataDefinition = viewDefinition.getDataDefinition();
    }

    private void getDataDefinitionFromFieldDefinition(final FieldDefinition fieldDefinition) {
        if (fieldDefinition.getType() instanceof DataDefinitionHolder) {
            dataDefinition = ((DataDefinitionHolder) fieldDefinition.getType()).getDataDefinition();
        }
    }

    private String[] getComponentAndField(final String path) {
        Pattern pField = Pattern.compile("^#\\{(.+)\\}(\\.(\\w+))?");
        Matcher mField = pField.matcher(path);
        if (mField.find()) {
            return new String[]{mField.group(1), mField.group(3)};
        } else {
            return new String[]{null, path};
        }
    }

    @Override
    public void addCustomEvent(final ViewEventListenerHook eventListenerHook) {
        customEventListeners.add(eventListenerHook);
    }

    @Override
    public void removeCustomEvent(final ViewEventListenerHook eventListenerHook) {
        customEventListeners.remove(eventListenerHook);
    }

    @Override
    public void parse(final Node componentNode, final ViewDefinitionParser parser) throws ViewDefinitionParserNodeException {
        indexOrder = ((ViewDefinitionParserImpl) parser).getCurrentIndexOrder();

        persistent = getPersistentAttribute(componentNode);

        NodeList childNodes = componentNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);

            if ("option".equals(child.getNodeName())) {
                addOption(parser.parseOption(child));
            } else if ("listener".equals(child.getNodeName())) {
                addCustomEvent(parser.parseEventListener(child));
            } else if ("script".equals(child.getNodeName())) {
                if (script == null) {
                    script = "";
                }

                if (nodeHasAttribute(child, "src")) {
                    String src = child.getAttributes().getNamedItem("src").getNodeValue();
                    scriptFiles.add(src);
                    
                } else {
                    script += parser.getStringNodeContent(child) + ";";
                }

            }
        }
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    private boolean getPersistentAttribute(final Node componentNode) {
        if (nodeHasAttribute(componentNode, "persistent")) {
            return Boolean.valueOf(componentNode.getAttributes().getNamedItem("persistent").toString());
        }
        return true;
    }

    private boolean nodeHasAttribute(final Node componentNode, final String attributeName) {
        return componentNode.getAttributes() != null && componentNode.getAttributes().getNamedItem(attributeName) != null;
    }

    private void checkNotNullFieldDefinition(final FieldDefinition fieldDefinition, final String[] field, final String fieldPath) {
        if (VersionableConstants.VERSION_FIELD_NAME.equals(field[1])) {
            // version field, ignore if empty fieldDefinition
        } else {
            checkNotNull(fieldDefinition, "Cannot find field definition for " + getPath() + ": " + fieldPath);
        }
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isUseDto() {
        return useDto;
    }
}
