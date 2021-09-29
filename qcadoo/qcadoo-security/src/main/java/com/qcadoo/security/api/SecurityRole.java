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
 * Defines single user role
 * 
 * @since 0.4.0
 */
public interface SecurityRole {

	/**
	 * Returns identifier of role
	 * 
	 * @return identifier of this role
	 */
	String getRoleIdentifier();

	/**
	 * Returns true if administrator is allowed to link this role to user
	 * 
	 * @return true if administrator is allowed to link this role to user
	 */
	boolean isAccessibleForUsers();

}
