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
package com.qcadoo.view.internal.internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.ribbon.RibbonActionItem.Type;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.ContainerPattern;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;
import com.qcadoo.view.internal.components.window.WindowComponentPattern;
import com.qcadoo.view.internal.hooks.AbstractViewHookDefinition;
import com.qcadoo.view.internal.hooks.HookType;
import com.qcadoo.view.internal.patterns.AbstractComponentPattern;
import com.qcadoo.view.internal.ribbon.model.*;
import com.qcadoo.view.internal.states.AbstractComponentState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.util.*;

public final class ViewDefinitionImpl implements InternalViewDefinition {

    private final String name;

    private final String pluginIdentifier;

    private final SecurityRole authorizationRole;

    private final DataDefinition dataDefinition;

    private final boolean menuAccessible;

    private Integer windowWidth;

    private Integer windowHeight;

    private final ViewHooksHolder viewHooksHolder;

    private final Set<String> jsFilePaths = Sets.newHashSet();

    private final Map<String, ComponentPattern> patterns = Maps.newLinkedHashMap();

    private final Map<String, ComponentPattern> registry = Maps.newLinkedHashMap();

    private final TranslationService translationService;

    private boolean alreadyHasNavigation;

    private boolean permanentlyDisabled;

    private RibbonGroupsPack ribbonNavigationGroupPack;
    
    private JSONObject jsonContext;

    public ViewDefinitionImpl(final String name, final String pluginIdentifier, final DataDefinition dataDefinition,
            final boolean menuAccessible, final TranslationService translationService) {
        this(name, pluginIdentifier, null, dataDefinition, menuAccessible, translationService);
    }

    public ViewDefinitionImpl(final String name, final String pluginIdentifier, final SecurityRole authorizationRole,
            final DataDefinition dataDefinition, final boolean menuAccessible, final TranslationService translationService) {
        this.name = name;
        this.authorizationRole = authorizationRole;
        this.dataDefinition = dataDefinition;
        this.pluginIdentifier = pluginIdentifier;
        this.menuAccessible = menuAccessible;
        this.translationService = translationService;
        this.viewHooksHolder = new ViewHooksHolder();
    }

    public void setWindowDimmension(final Integer windowWidth, final Integer windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    @Override
    public void initialize() {
        List<ComponentPattern> list = getPatternsAsList(patterns.values());

        int lastNotInitialized = 0;

        while (true) {
            int notInitialized = 0;

            for (ComponentPattern pattern : list) {
                if (!pattern.initialize()) {
                    notInitialized++;
                }
            }

            if (notInitialized == 0) {
                break;
            }

            if (notInitialized == lastNotInitialized) {
                throw new IllegalStateException("There is cyclic dependency between components");
            }

            lastNotInitialized = notInitialized;
        }

        initAdditionalNavigation();
    }

    @Override
    public Map<String, Object> prepareView(final JSONObject jsonObject, final Locale locale) {
        viewHooksHolder.callConstructionHooks(this, jsonObject, locale);

        Map<String, Object> model = Maps.newHashMap();
        Map<String, Object> childrenModels = Maps.newHashMap();

        toggleAdditionalNavigationGroup(getBooleanFromJson(jsonObject, "window.showBack"));
        permanentlyDisabled = getBooleanFromJson(jsonObject, "window." + AbstractComponentState.JSON_PERMANENTLY_DISABLED);
        toggleRibbonPermanentlyDisabled();

        for (ComponentPattern componentPattern : patterns.values()) {
            childrenModels.put(componentPattern.getName(), componentPattern.prepareView(locale));
        }

        model.put(JSON_COMPONENTS, childrenModels);
        model.put(JSON_JS_FILE_PATHS, getJsFilePaths());

        model.put("hasDataDefinition", getDataDefinition() != null);

        try {
            JSONObject json = new JSONObject();
            JSONObject translations = new JSONObject();
            translations.put("backWithChangesConfirmation",
                    translationService.translate("qcadooView.backWithChangesConfirmation", locale));
            json.put("translations", translations);
            if (windowWidth != null) {
                json.put("windowWidth", windowWidth);
            }
            if (windowHeight != null) {
                json.put("windowHeight", windowHeight);
            }
            model.put("jsOptions", json);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return model;
    }

    private boolean getBooleanFromJson(final JSONObject jsonObject, final String fieldName) {
        try {
            return jsonObject.has(fieldName) && !jsonObject.isNull(fieldName) && jsonObject.getBoolean(fieldName);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private InternalRibbon getRibbon() {
        ComponentPattern window = patterns.get("window");
        if (window instanceof WindowComponentPattern) {
            return ((WindowComponentPattern) window).getRibbon();
        }
        return null;
    }

    private void initAdditionalNavigation() {
        InternalRibbon ribbon = getRibbon();
        if (ribbon == null || ribbon.getGroupByName("navigation") != null) {
            alreadyHasNavigation = ribbon != null;
            return;
        }
        InternalRibbonActionItem backButton = new RibbonActionItemImpl();
        backButton.setName("back");
        backButton.setIcon("backIcon24.png");
        backButton.setAction("#{window}.performBack");
        backButton.setEnabled(true);
        backButton.setType(Type.BIG_BUTTON);

        InternalRibbonGroup additionalNavigationGroup = new RibbonGroupImpl("navigation");
        additionalNavigationGroup.addItem(backButton);

        ribbonNavigationGroupPack = new SingleRibbonGroupPack(additionalNavigationGroup);
    }

    // TODO MAKU move additional navigation to template
    private void toggleAdditionalNavigationGroup(final boolean showBack) {
        InternalRibbon ribbon = getRibbon();
        if (ribbon == null || alreadyHasNavigation) {
            return;
        }
        if (!showBack) {
            ribbon.removeGroupsPack(ribbonNavigationGroupPack);
            return;
        }
        if (ribbon.getGroupByName("navigation") == null) {
            ribbon.addGroupPackAsFirst(ribbonNavigationGroupPack);
        }
    }

    private void toggleRibbonPermanentlyDisabled() {
        InternalRibbon ribbon = getRibbon();
        if (ribbon != null) {
            ribbon.setPermanentlyDisabled(permanentlyDisabled);
        }
    }

    @Override
    public ViewDefinitionState performEvent(final JSONObject jsonObject, final Locale locale) throws JSONException {
        viewHooksHolder.callConstructionHooks(this, jsonObject, locale);

        ViewDefinitionStateImpl viewDefinitionState = new ViewDefinitionStateImpl();
        viewDefinitionState.setTranslationService(translationService);
        viewDefinitionState.setJsonContext(getJsonContext());

        JSONObject eventJson = jsonObject.getJSONObject(JSON_EVENT);
        String eventName = eventJson.getString(JSON_EVENT_NAME);
        
        viewDefinitionState.setViewAfterReload(!(eventName.startsWith("initialize") || "reset".equals(eventName)));

        viewDefinitionState.setViewAfterRedirect(eventName.startsWith("redirect") || eventName.startsWith("initialize"));

        for (ComponentPattern cp : patterns.values()) {
            viewDefinitionState.addChild(cp.createComponentState(viewDefinitionState));
        }

        viewHooksHolder.callLifecycleHooks(HookType.BEFORE_INITIALIZE, viewDefinitionState);

        if (permanentlyDisabled) {
            jsonObject.put(AbstractComponentState.JSON_PERMANENTLY_DISABLED, true);
        }
        viewDefinitionState.initialize(jsonObject, locale);

        for (ComponentPattern cp : patterns.values()) {
            ((AbstractComponentPattern) cp).updateComponentStateListeners(viewDefinitionState);
        }

        viewHooksHolder.callLifecycleHooks(HookType.AFTER_INITIALIZE, viewDefinitionState);

        String eventComponent = eventJson.has(JSON_EVENT_COMPONENT) ? eventJson.getString(JSON_EVENT_COMPONENT) : null;
        JSONArray eventArgsArray = eventJson.has(JSON_EVENT_ARGS) ? eventJson.getJSONArray(JSON_EVENT_ARGS) : new JSONArray();
        String[] eventArgs = new String[eventArgsArray.length()];
        for (int i = 0; i < eventArgsArray.length(); i++) {
            eventArgs[i] = eventArgsArray.getString(i);
        }

        viewDefinitionState.performEvent(eventComponent, eventName, eventArgs);

        viewHooksHolder.callLifecycleHooks(HookType.BEFORE_RENDER, viewDefinitionState);

        return viewDefinitionState;
    }

    public void registerViews(final InternalViewDefinitionService viewDefinitionService) {
        for (ComponentPattern cp : patterns.values()) {
            cp.registerViews(viewDefinitionService);
        }
    }

    @Override
    public void addComponentPattern(final ComponentPattern componentPattern) {
        patterns.put(componentPattern.getName(), componentPattern);
    }

    @Override
    public ComponentPattern getComponentByReference(final String reference) {
        return registry.get(reference);
    }

    @Override
    public void registerComponent(final String reference, final String path, final ComponentPattern pattern) {
        if (registry.containsKey(reference)) {
            throw new IllegalStateException("Duplicated pattern reference '" + reference + "'");
        }
        registry.put(reference, pattern);
    }

    @Override
    public void unregisterComponent(final String reference, final String path) {
        registry.remove(reference);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPluginIdentifier() {
        return pluginIdentifier;
    }

    @Override
    public boolean isMenuAccessible() {
        return menuAccessible;
    }

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinition;
    };

    public Set<String> getJsFilePaths() {
        return jsFilePaths;
    }

    @Override
    public void addJsFilePath(final String jsFilePath) {
        jsFilePaths.add(jsFilePath);
    }

    @Override
    public void addHook(final AbstractViewHookDefinition viewHook) {
        viewHooksHolder.addHook(viewHook);
    }

    @Override
    public void removeHook(final AbstractViewHookDefinition viewHook) {
        viewHooksHolder.removeHook(viewHook);
    }

    private List<ComponentPattern> getPatternsAsList(final Collection<ComponentPattern> patterns) {
        List<ComponentPattern> list = new ArrayList<ComponentPattern>();
        list.addAll(patterns);
        for (ComponentPattern pattern : patterns) {
            if (pattern instanceof ContainerPattern) {
                list.addAll(getPatternsAsList(((ContainerPattern) pattern).getChildren().values()));
            }
        }
        return list;
    }

    @Override
    public WindowComponentPattern getRootWindow() {
        if (patterns.size() != 1) {
            return null;
        }
        ComponentPattern rootPattern = patterns.values().iterator().next();
        if (rootPattern instanceof WindowComponentPattern) {
            return (WindowComponentPattern) rootPattern;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String translateContextReferences(final String context) {
        if (context == null) {
            return null;
        }
        try {
            JSONObject oldContext = new JSONObject(context);
            JSONObject newContext = new JSONObject();
            Iterator<String> paths = oldContext.keys();

            while (paths.hasNext()) {
                String oldPath = paths.next();
                String[] newPath = oldPath.split("\\.");

                ComponentPattern pattern = getComponentByReference(newPath[0]);

                if (pattern == null) {
                    throw new IllegalStateException("Cannot find component for " + getPluginIdentifier() + "." + getName() + ": "
                            + newPath[0]);
                }

                newPath[0] = pattern.getPath();

                newContext.put(StringUtils.arrayToDelimitedString(newPath, "."), oldContext.get(oldPath));
            }

            return newContext.toString();
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public SecurityRole getAuthorizationRole() {
        return authorizationRole;
    }

    @Override
    public JSONObject getJsonContext() {
        return jsonContext;
    }

    @Override
    public void setJsonContext(JSONObject jsonContext) {
        this.jsonContext = jsonContext;
    }
}
