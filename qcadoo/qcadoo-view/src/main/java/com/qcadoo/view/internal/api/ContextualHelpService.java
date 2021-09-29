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

public interface ContextualHelpService {

    /**
     * Lookup context help URL for specified component pattern
     * 
     * @param componentPattern
     *            pattern of component for which you want to find URL
     * @return URL string or null if URL for specified component was not found
     */
    String getHelpUrl(ComponentPattern componentPattern);

    /**
     * Returns contextual help code for given component
     * 
     * @param componentPattern
     *            pattern of component for which you want to get code
     * @return contextual help code or null if showContextualHelpPaths is set to false (@see
     *         {@link ContextualHelpService#isContextualHelpPathsVisible()}) or component is not supported.
     */
    String getContextualHelpKey(ComponentPattern componentPattern);

    /**
     * Lookup context help URL for specified String code
     * 
     * @param code
     *            key for which you want to find URL
     * @return URL string or null if URL for specified code was not found
     */
    String getHelpUrl(String code);

    /**
     * @return showContextualHelpPaths property value
     */
    boolean isContextualHelpPathsVisible();
}
