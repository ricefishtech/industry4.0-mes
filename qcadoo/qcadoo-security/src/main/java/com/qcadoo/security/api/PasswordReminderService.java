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

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Service for resetting password
 * 
 * @since 1.1.5
 */
public interface PasswordReminderService {

    /**
     * Generate and send via email new password for specified user
     * 
     * @param userName
     *            user name whose password you want to reset
     * @throws UsernameNotFoundException
     *             when user with given name does not exist
     */
    void generateAndSendNewPassword(String userName) throws UsernameNotFoundException;
}
