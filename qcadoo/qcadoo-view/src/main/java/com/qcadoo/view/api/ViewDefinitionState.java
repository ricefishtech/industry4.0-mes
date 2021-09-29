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
package com.qcadoo.view.api;

import com.google.common.base.Optional;
import org.json.JSONObject;

import java.util.Map;

/**
 * ViewDefinitionState is instance of single view. It is generated using ViewDefinition in request scope.
 * <p>
 * It contains all {@link com.qcadoo.view.api.ComponentState ComponentStates} of this view and other data necessary display this
 * view to client. Changing its data will also change state displayed to system user.
 * 
 * @since 0.4.0
 * 
 * @see com.qcadoo.view.api.ComponentState
 */
public interface ViewDefinitionState extends ComponentState {

    /**
     * Returns component state with specified reference name or null if no such component found
     * 
     * @param reference
     *            reference name of component
     * @return component state with specified reference name
     */
    ComponentState getComponentByReference(String reference);

    /**
     * Returns Optional containing component with specified reference name or empty Optional if no such component found or found
     * component has other type than demanded (see T type parameter).
     * 
     * @since 1.3
     * @param reference
     *            reference name of component
     * @return component state with specified reference name wrapped within Optional, or Optional.absent() if no such component
     *         found or found component has other type than demanded.
     */
    <T extends ComponentState> Optional<T> tryFindComponentByReference(String reference);

    /**
     * Informs client that should redirect to some url.
     * 
     * @param redirectToUrl
     *            target url of redirection
     * @param openInNewWindow
     *            true if client should open given url in new window
     * @param shouldSerialize
     *            true if before this redirection client should save window state
     */
    void redirectTo(String redirectToUrl, boolean openInNewWindow, boolean shouldSerialize);

    /**
     * Informs client that should redirect to some url and pass custom component parameters.
     * 
     * @param redirectToUrl
     *            target url of redirection
     * @param openInNewWindow
     *            true if client should open given url in new window
     * @param shouldSerialize
     *            true if before this redirection client should save window state
     * @param parameters
     *            map of parameters which be passed between views
     */
    void redirectTo(String redirectToUrl, boolean openInNewWindow, boolean shouldSerialize, Map<String, Object> parameters);

    /**
     * Informs client that should open new modal window.
     * 
     * @param url
     *            target url of opened window
     */
    void openModal(String url);

    /**
     * Informs client that should open new modal window and pass custom component parameters.
     *
     * @param url
     *            target url of opened window
     */
    void openModal(String url, Map<String, Object> parameters);

    /**
     * Checks whether the view is generated for the first time or after reload
     * 
     * @return boolean
     */
    boolean isViewAfterReload();

    /**
     * Checks whether the view is generated after redirect event
     *
     * @return boolean
     */
    boolean isViewAfterRedirect();
    
    /**
     * Set context form url
     * 
     * @param jSONObject 
     */
    void setJsonContext(JSONObject jSONObject);
    
    /**
     * Get context from url
     * 
     * @return 
     */
    JSONObject getJsonContext();
}
