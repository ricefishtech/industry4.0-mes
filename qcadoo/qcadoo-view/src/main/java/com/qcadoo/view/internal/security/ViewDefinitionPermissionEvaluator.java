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
package com.qcadoo.view.internal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;

import com.qcadoo.security.api.SecurityRole;
import com.qcadoo.security.internal.permissionEvaluators.QcadooPermisionEvaluator;
import com.qcadoo.security.internal.role.InternalSecurityRolesService;

public class ViewDefinitionPermissionEvaluator implements QcadooPermisionEvaluator {

    // Need to use late initialization - probably because ViewDefinitionService is authorized
    @Autowired
    private ApplicationContext context;

    private SecurityViewDefinitionRoleResolver viewDefinitionRoleResolver;

    @Autowired
    private InternalSecurityRolesService securityRolesService;

    @Override
    public String getTargetType() {
        return "viewDefinition";
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final String permission, final String targetId) {

        if ("isAuthorizedToSee".equals(permission)) {

            SecurityRole role = getViewRole(targetId);

            return securityRolesService.canAccess(authentication, role);

        } else {
            throw new IllegalArgumentException("permission type '" + permission + "' not supported");
        }
    }

    private SecurityRole getViewRole(final String targetId) {
        if (viewDefinitionRoleResolver == null) {
            viewDefinitionRoleResolver = context.getBean(SecurityViewDefinitionRoleResolver.class);
        }
        String[] viewNameParts = targetId.split("#");
        return viewDefinitionRoleResolver.getRoleForView(viewNameParts[0], viewNameParts[1]);
    }

}
