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

/**
 * Service for generating passwords
 * 
 */
public interface PasswordGeneratorService {

    /**
     * Default password length
     */
    int DEFAULT_PASSWORD_LENGTH = 10;

    /**
     * Generate random password with default length {@link PasswordGeneratorService#DEFAULT_PASSWORD_LENGTH}
     * 
     * @return generated random password
     */
    String generatePassword();

    /**
     * Generate random password with specified length
     * 
     * @param length
     *            expected password length
     * 
     * @return generated random password
     */
    String generatePassword(int length);
}
