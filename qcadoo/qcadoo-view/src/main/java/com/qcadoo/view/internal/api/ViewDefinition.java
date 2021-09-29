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
package com.qcadoo.view.internal.api;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.security.api.SecurityRole;
import org.json.JSONObject;

/**
 * ViewDefinition defines single 'view' in system.
 * <p>
 * It contains all {@link com.qcadoo.view.internal.api.ComponentPattern ComponentPatterns} of this view and other data necessary
 * to create {@link com.qcadoo.view.api.ViewDefinitionState}.
 * 
 * @since 0.4.0
 * 
 * @see com.qcadoo.view.api.ViewDefinitionState
 * @see com.qcadoo.view.internal.api.ViewDefinitionService
 * @see com.qcadoo.view.internal.api.ComponentPattern
 */
public interface ViewDefinition {

    /**
     * Returns name of this view
     * 
     * @return name of this view
     */
    String getName();

    /**
     * Returns identifier of plugin that created this view
     * 
     * @return identifier of plugin that created this view
     */
    String getPluginIdentifier();

    /**
     * Returns default authorization role necessary to be able to see this view or null if no such role defined
     * 
     * @return default authorization role
     */
    SecurityRole getAuthorizationRole();

    /**
     * Returns main data definition of this view or null if no such data definition defined
     * 
     * @return main data definition of this view
     */
    DataDefinition getDataDefinition();
    
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
