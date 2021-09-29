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


/**
 * Service for manipulating view definitions.
 * 
 * @see com.qcadoo.view.constants.internal.xml.ViewDefinitionParserImpl.ViewDefinitionParser
 */
public interface InternalViewDefinitionService extends ViewDefinitionService {

    /**
     * Return the view definition matching the given plugin's identifier and view's name.
     * 
     * @param pluginIdentifier
     *            plugin's identifier
     * @param viewName
     *            view's name
     * @return the view definition, null if not found
     */
    InternalViewDefinition getWithoutSession(String pluginIdentifier, String viewName);

    /**
     * Save the data definition.
     * 
     * @param viewDefinition
     *            view definition
     */
    void save(InternalViewDefinition viewDefinition);

    /**
     * Delete the data definition.
     * 
     * @param viewDefinition
     *            view definition
     */
    void delete(InternalViewDefinition viewDefinition);

}
