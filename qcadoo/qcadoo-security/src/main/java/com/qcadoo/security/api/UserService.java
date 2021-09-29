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
 * This service provides common operations for user
 * 
 * @since 1.2.1
 */
public interface UserService {

    /**
     * Find user by id
     * 
     * @param userId
     *            id of the user to find
     * @return matching user entity or null
     */
    Entity find(final Long userId);

    /**
     * Find user by name
     * 
     * @param userName
     *            name of the user to find
     * @return matching user entity or null
     */
    Entity find(final String userName);

    /**
     * Get current user entity
     * 
     * @return current user entity
     */
    Entity getCurrentUserEntity();

    /**
     * Extract user's first and last names.
     * 
     * @param user
     *            user entity from which full name will be extracted.
     * @return string containing user's first and last names or null if given user is null.
     */
    String extractFullName(final Entity user);

}
