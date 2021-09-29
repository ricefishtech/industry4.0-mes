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
package com.qcadoo.security.internal.role;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.qcadoo.security.api.SecurityRole;

@Service
public class InternalSecurityRolesServiceImpl implements InternalSecurityRolesService {

	@Autowired
	private RoleHierarchy roleHierarchy;

	private final Map<String, SecurityRole> roles = new HashMap<String, SecurityRole>();

	@Override
	public SecurityRole getRoleByIdentifier(final String roleIdentifier) {
		return roles.get(roleIdentifier);
	}

	@Override
	public void addRole(final SecurityRole role) {
		roles.put(role.getRoleIdentifier(), role);
	}

	@Override
	public boolean canAccess(final String targetRoleIdetifier) {
		Preconditions.checkNotNull(targetRoleIdetifier, "targetRoleIdetifier must be not null");
		SecurityRole targetRole = getRoleByIdentifier(targetRoleIdetifier);
		Preconditions.checkState(targetRoleIdetifier != null, "No such role '" + targetRoleIdetifier + "'");
		return canAccess(targetRole);
	}

	@Override
	public boolean canAccess(final SecurityRole targetRole) {
		Preconditions.checkState(SecurityContextHolder.getContext() != null, "No security context");
		Preconditions.checkState(SecurityContextHolder.getContext().getAuthentication() != null,
		        "No authentication in security context");
		return canAccess(SecurityContextHolder.getContext().getAuthentication(), targetRole);
	}

	@Override
	public boolean canAccess(final Authentication userAuthentication, final SecurityRole targetRole) {
		Preconditions.checkNotNull(userAuthentication, "userAuthentication must be not null");

		if (targetRole == null) {
			return true;
		}

		Collection<? extends GrantedAuthority> reachableAuthorities = roleHierarchy
		        .getReachableGrantedAuthorities(userAuthentication.getAuthorities());

		for (GrantedAuthority grantedAuthority : reachableAuthorities) {
			if (grantedAuthority.getAuthority().equals(targetRole.getRoleIdentifier())) {
				return true;
			}
		}

		return false;
	}

}
