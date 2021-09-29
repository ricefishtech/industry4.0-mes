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
package com.qcadoo.view.internal.states;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.validators.ErrorMessage;
import com.qcadoo.model.api.validators.GlobalMessage;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.internal.FieldEntityIdChangeListener;
import com.qcadoo.view.internal.ScopeEntityIdChangeListener;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.InternalComponentState;
import com.qcadoo.view.internal.hooks.ViewEventListenerHook;
import com.qcadoo.view.internal.internal.EntityIdChangeListenerHolder;
import com.qcadoo.view.internal.internal.EventHandlerHolder;
import com.qcadoo.view.internal.internal.MessageHolder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractComponentState implements InternalComponentState, FieldEntityIdChangeListener,
        ScopeEntityIdChangeListener {

    public static final String JSON_UPDATE_STATE = "updateState";

    public static final String JSON_VISIBLE = "visible";

    public static final String JSON_ENABLED = "enabled";

    public static final String JSON_PERMANENTLY_DISABLED = "permanentlyDisabled";

    public static final String JSON_CONTENT = "content";

    public static final String JSON_CONTEXT = "context";

    public static final String JSON_VALUE = "value";

    public static final String JSON_CHILDREN = "components";

    public static final String JSON_MESSAGES = "messages";

    public static final String JSON_MESSAGE_TITLE = "title";

    public static final String JSON_MESSAGE_BODY = "content";

    public static final String JSON_MESSAGE_TYPE = "type";

    public static final String JSON_MESSAGE_AUTOCLOSE = "autoClose";

    public static final String JSON_MESSAGE_EXTRALARGE = "extraLarge";

    public static final String JSON_COMPONENT_OPTIONS = "options";

    private final EntityIdChangeListenerHolder listenerHolder = new EntityIdChangeListenerHolder();

    private final EventHandlerHolder eventHandlerHolder = new EventHandlerHolder(this);

    private MessageHolder messageHolder;

    private String name;

    private String uuid;

    private Locale locale;

    private DataDefinition dataDefinition;

    private TranslationService translationService;

    private boolean requestRender;

    private boolean requestUpdateState;

    private boolean enabled = true;

    private boolean permanentlyDisabled = false;

    private boolean visible = true;

    private boolean hasError = false;

    private String translationPath;

    public AbstractComponentState() {
        // empty
    }

    public AbstractComponentState(final ComponentPattern pattern) {
        if (pattern != null) {
            this.permanentlyDisabled = pattern.isPermanentlyDisabled();
        }
    }

    @Override
    public final String getName() {
        return name;
    };

    @Override
    public final String getUuid() {
        return uuid;
    };

    public final void setName(final String name) {
        this.name = name;
    }
    public final void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public final void setDataDefinition(final DataDefinition dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    protected final DataDefinition getDataDefinition() {
        return dataDefinition;
    }

    public final void setTranslationService(final TranslationService translationService) {
        this.translationService = translationService;
    }

    protected final TranslationService getTranslationService() {
        return translationService;
    }

    public final void setTranslationPath(final String translationPath) {
        this.translationPath = translationPath;
    }

    protected final String getTranslationPath() {
        return translationPath;
    }

    @Override
    public final void addMessage(final ErrorMessage errorMessage) {
        addMessage(errorMessage.getMessage(), MessageType.FAILURE, errorMessage.getAutoClose(), errorMessage.isExtraLarge(),
                errorMessage.getVars());
    }

    @Override
    public final void addMessage(final GlobalMessage globalMessage) {
        addMessage(globalMessage.getMessage(), MessageType.INFO, globalMessage.getAutoClose(), globalMessage.isExtraLarge(), globalMessage.getVars());
    }

    @Override
    public void addMessage(final String message, final MessageType type, final String... args) {
        addMessage(message, type, true, args);
    }

    @Override
    public final void addMessage(final String message, final MessageType type, final boolean autoClose, final String... args) {
        String translatedMessage = getTranslationService().translate(message, getLocale(), args);
        addTranslatedMessage(translatedMessage, type, autoClose);
    }

    @Override
    public final void addMessage(final String message, final MessageType type, final boolean autoClose, final boolean extraLarge,
            final String... args) {
        String translatedMessage = getTranslationService().translate(message, getLocale(), args);
        addTranslatedMessage(translatedMessage, type, autoClose, extraLarge);
    }

    @Override
    public final void addTranslatedMessage(final String translatedMessage, final MessageType type) {
        addTranslatedMessage(translatedMessage, type, true);
    }

    @Override
    public final void addTranslatedMessage(final String translatedMessage, final MessageType type, final boolean autoClose) {
        messageHolder.addMessage(null, translatedMessage, type, autoClose);
        if (MessageType.FAILURE.equals(type)) {
            hasError = true;
        }
    }

    @Override
    public final void addTranslatedMessage(final String translatedMessage, final MessageType type, final boolean autoClose,
            final boolean extraLarge) {
        messageHolder.addMessage(null, translatedMessage, type, autoClose, extraLarge);
        if (MessageType.FAILURE.equals(type)) {
            hasError = true;
        }
    }

    protected String translateMessage(final String key, final String... args) {
        return getTranslationService()
                .translate(getTranslationPath() + "." + key, "qcadooView.message." + key, getLocale(), args);
    }

    protected void copyMessage(final ComponentState componentState, final ErrorMessage message) {
        if (message != null) {
            componentState.addMessage(message);
        }
    }

    protected void copyMessage(final ComponentState componentState, final GlobalMessage message) {
        if (message != null) {
            componentState.addMessage(message);
        }
    }

    protected void copyMessages(final List<ErrorMessage> messages) {
        for (ErrorMessage message : messages) {
            copyMessage(this, message);
        }
    }

    protected void copyGlobalMessages(final List<GlobalMessage> messages) {
        for (GlobalMessage message : messages) {
            copyMessage(this, message);
        }
    }

    @Override
    public boolean isHasError() {
        return hasError;
    }

    @Override
    public void initialize(final JSONObject json, final Locale locale) throws JSONException {
        this.locale = locale;
        this.messageHolder = new MessageHolder(translationService, locale);

        if (json.has(JSON_PERMANENTLY_DISABLED) && !json.isNull(JSON_PERMANENTLY_DISABLED)
                && json.getBoolean(JSON_PERMANENTLY_DISABLED)) {
            setPermanentlyDisabled(true);
        }

        if (json.has(JSON_ENABLED)) {
            setEnabled(json.getBoolean(JSON_ENABLED));
        }

        if (json.has(JSON_VISIBLE)) {
            setVisible(json.getBoolean(JSON_VISIBLE));
        }

        if (json.has(JSON_CONTENT) && !json.isNull(JSON_CONTENT)) {
            initializeContent(json.getJSONObject(JSON_CONTENT));
        }

        if (json.has(JSON_CONTEXT)) {
            initializeContext(json.getJSONObject(JSON_CONTEXT));
        }
    }

    @Override
    public final Locale getLocale() {
        return locale;
    }

    protected abstract void initializeContent(final JSONObject json) throws JSONException;

    public final void registerCustomEvent(final ViewEventListenerHook eventListenerHook) {
        eventHandlerHolder.registerCustomEvent(eventListenerHook);
    }

    protected final void registerEvent(final String name, final Object obj, final String method) {
        eventHandlerHolder.registerEvent(name, obj, method);
    }

    @Override
    public final void performEvent(final ViewDefinitionState viewDefinitionState, final String event, final String... args) {
        eventHandlerHolder.performEvent(viewDefinitionState, event, args);
    }

    @Override
    public JSONObject render() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ENABLED, isEnabled());
        json.put(JSON_PERMANENTLY_DISABLED, permanentlyDisabled);
        json.put(JSON_VISIBLE, isVisible());

        if (requestRender) {
            json.put(JSON_CONTENT, renderContent());
            json.put(JSON_UPDATE_STATE, requestUpdateState);
        } else {
            json.put(JSON_UPDATE_STATE, false);
        }

        if (messageHolder != null) {
            json.put(JSON_MESSAGES, messageHolder.renderMessages());
        }

        return json;
    }

    protected abstract JSONObject renderContent() throws JSONException;

    protected final void notifyEntityIdChangeListeners(final Long entityId) {
        listenerHolder.notifyEntityIdChangeListeners(entityId);
    }

    protected final Map<String, FieldEntityIdChangeListener> getFieldEntityIdChangeListeners() {
        return listenerHolder.getFieldEntityIdChangeListeners();
    }

    protected final Map<String, ScopeEntityIdChangeListener> getScopeEntityIdChangeListeners() {
        return listenerHolder.getScopeEntityIdChangeListeners();
    }

    protected final void requestRender() {
        requestRender = true;
    }

    protected final void requestUpdateState() {
        requestUpdateState = true;
    }

    public final void addFieldEntityIdChangeListener(final String field, final FieldEntityIdChangeListener listener) {
        listenerHolder.addFieldEntityIdChangeListener(field, listener);
    }

    public final void addScopeEntityIdChangeListener(final String scope, final ScopeEntityIdChangeListener listener) {
        listenerHolder.addScopeEntityIdChangeListener(scope, listener);
    }

    @Override
    public final boolean isVisible() {
        return visible;
    }

    @Override
    public final void setVisible(final boolean visible) {
        this.visible = visible;
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (permanentlyDisabled) {
            this.enabled = false;
        } else {
            this.enabled = enabled;
        }
    }

    @Override
    public void setPermanentlyDisabled(final boolean permanentlyDisabled) {
        this.permanentlyDisabled = permanentlyDisabled;
        if (permanentlyDisabled) {
            setEnabled(false);
        }
    }

    @Override
    public void onFieldEntityIdChange(final Long entityId) {
        // implements if you want
    }

    @Override
    public void onScopeEntityIdChange(final Long entityId) {
        // implements if you want
    }

    protected void initializeContext(final JSONObject json) throws JSONException {
        if (json.has(JSON_COMPONENT_OPTIONS) && !json.isNull(JSON_COMPONENT_OPTIONS)) {
            JSONObject jsonOptions = json.getJSONObject(JSON_COMPONENT_OPTIONS);
            passEnabledFromJson(jsonOptions);
            passVisibleFromJson(jsonOptions);
        }
    }

    private void passEnabledFromJson(final JSONObject json) throws JSONException {
        if (json.has(JSON_ENABLED) && !json.isNull(JSON_ENABLED)) {
            enabled = json.getBoolean(JSON_ENABLED);
        }
    }

    protected void passVisibleFromJson(final JSONObject json) throws JSONException {
        if (json.has(JSON_VISIBLE) && !json.isNull(JSON_VISIBLE)) {
            visible = json.getBoolean(JSON_VISIBLE);
        }
    }

    @Override
    public void setFieldValue(final Object value) {
        // implements if you want
    }

    @Override
    public Object getFieldValue() {
        return null; // implements if you want
    }

    public boolean isPermanentlyDisabled() {
        return permanentlyDisabled;
    }

}
