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

import com.google.common.base.Optional;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.api.ContainerState;
import com.qcadoo.view.internal.api.InternalComponentState;
import com.qcadoo.view.internal.api.InternalViewDefinitionState;
import com.qcadoo.view.internal.states.AbstractContainerState;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public final class ViewDefinitionStateImpl extends AbstractContainerState implements InternalViewDefinitionState {

    private String redirectToUrl;

    private boolean openInNewWindow;

    private boolean openInModalWindow;

    private boolean shouldSerializeWindow;

    private final Map<String, ComponentState> registry = new HashMap<>();

    private final ViewDefinitionStateLogger logger;

    private boolean viewAfterReload = false;

    private boolean viewAfterRedirect = false;

    private JSONObject jsonContext;
    
    public ViewDefinitionStateImpl() {
        super();

        requestRender();
        logger = ViewDefinitionStateLogger.forView(this);
    }

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        // empty
    }

    @Override
    public JSONObject render() throws JSONException {
        if (redirectToUrl == null) {
            return super.render();
        }
        
        JSONObject json = new JSONObject();
        JSONObject jsonRedirect = new JSONObject();
        json.put("redirect", jsonRedirect);
        jsonRedirect.put("url", redirectToUrl);
        jsonRedirect.put("openInNewWindow", openInNewWindow);
        jsonRedirect.put("openInModalWindow", openInModalWindow);
        jsonRedirect.put("shouldSerializeWindow", shouldSerializeWindow);
        
        return json;
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = new JSONObject();

        boolean isOk = true;

        List<InternalComponentState> states = getStatesAsList(getChildren().values());

        for (ComponentState state : states) {
            if (state.isHasError()) {
                isOk = false;
                break;
            }
        }

        json.put("status", isOk ? "ok" : "error");

        return json;
    }

    @Override
    public void performEvent(final String component, final String event, final String... args) {
        if (component == null) {
            performEventOnChildren(getChildren().values(), event, args);
        } else {
            getComponentByPath(component).performEvent(this, event, args);
        }
    }

    private ComponentState getComponentByPath(final String path) {
        ComponentState componentState = this;
        String[] pathParts = path.split("\\.");
        for (int i = 0; i < pathParts.length; i++) {
            ContainerState container = (ContainerState) componentState;
            componentState = container.getChild(pathParts[i]);
            if (componentState == null) {
                return null;
            }
        }
        return componentState;
    }

    @Override
    public ComponentState getComponentByReference(final String reference) {
        return registry.get(reference);
    }

    @Override
    public <T extends ComponentState> Optional<T> tryFindComponentByReference(final String reference) {
        try {
            Optional<T> maybeComponent = Optional.fromNullable((T) getComponentByReference(reference));
            if (!maybeComponent.isPresent()) {
                logger.logDebug(String.format("Cannot find component with reference name = '%s'", reference));
            }
            return maybeComponent;
        } catch (ClassCastException cce) {
            logger.logWarn(
                    String.format("Component with reference name = '%s' isn't a kind of expected component type.", reference),
                    cce);
            return Optional.absent();
        }
    }

    private void performEventOnChildren(final Collection<InternalComponentState> components, final String event,
            final String... args) {
        for (ComponentState component : components) {
            component.performEvent(this, event, args);
            if (component instanceof ContainerState) {
                performEventOnChildren(((ContainerState) component).getChildren().values(), event, args);
            }
        }
    }

    private List<InternalComponentState> getStatesAsList(final Collection<InternalComponentState> states) {
        List<InternalComponentState> list = new ArrayList<>();
        list.addAll(states);
        for (InternalComponentState state : states) {
            if (state instanceof ContainerState) {
                list.addAll(getStatesAsList(((ContainerState) state).getChildren().values()));
            }
        }
        return list;
    }

    @Override
    public void redirectTo(final String redirectToUrl, final boolean openInNewWindow, final boolean shouldSerialize) {
        this.redirectToUrl = redirectToUrl;
        this.openInNewWindow = openInNewWindow;
        openInModalWindow = false;
        this.shouldSerializeWindow = shouldSerialize;
    }

    @Override
    public void redirectTo(final String redirectToUrl, final boolean openInNewWindow, final boolean shouldSerialize,
            final Map<String, Object> parameters) {
        JSONObject context = new JSONObject(parameters);
        StringBuilder url = new StringBuilder(redirectToUrl);
        if (redirectToUrl.contains("?")) {
            url.append("&");
        } else {
            url.append("?");
        }
        url.append("context=");
        url.append(context.toString());
        redirectTo(url.toString(), openInNewWindow, shouldSerialize);
    }

    @Override
    public void openModal(final String url) {
        this.redirectToUrl = url;
        this.openInNewWindow = false;
        openInModalWindow = true;
        this.shouldSerializeWindow = true;
    }

    @Override
    public void openModal(final String modalUrl, final Map<String, Object> parameters) {
        JSONObject context = new JSONObject(parameters);
        StringBuilder url = new StringBuilder(modalUrl);
        if (modalUrl.contains("?")) {
            url.append("&");
        } else {
            url.append("?");
        }
        url.append("context=");
        url.append(context.toString());
        openModal(url.toString());
    }

    @Override
    public void registerComponent(final String reference, final ComponentState state) {
        if (registry.containsKey(reference)) {
            throw new IllegalStateException("Duplicated state reference : " + reference);
        }
        registry.put(reference, state);
    }

    public void setViewAfterReload(boolean viewAfterReload) {
        this.viewAfterReload = viewAfterReload;
    }
    
    @Override
    public boolean isViewAfterReload() {
        return viewAfterReload;
    }

    public void setViewAfterRedirect(boolean viewAfterRedirect) {
        this.viewAfterRedirect = viewAfterRedirect;
    }

    @Override public boolean isViewAfterRedirect() {
        return viewAfterRedirect;
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
