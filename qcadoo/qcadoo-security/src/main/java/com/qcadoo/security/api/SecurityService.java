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
package com.qcadoo.security.api;

import com.qcadoo.model.api.Entity;

/**
 * Service for getting current user.
 * 
 * @since 0.4.0
 */
public interface SecurityService {

    /**
     * Returns the name of the currently logged in user
     * 
     * @return currently logged in user's name
     */
    String getCurrentUserName();

    /**
     * Returns the id of the currently logged in user
     * 
     * @return currently logged in user's id
     */
    Long getCurrentUserId();
    
    /**
     * Checks if given user can access resource with specified access role identifier
     * 
     * @param userEntity
     *            user to check
     * @param targetRoleIdetifier
     *            resource access role identifier
     * @return true if given user can access to defined role, false otherwise
     */
    boolean hasRole(final Entity userEntity, final String targetRoleIdetifier);

    boolean hasCurrentUserRole(final String targetRoleIdetifier);

}
