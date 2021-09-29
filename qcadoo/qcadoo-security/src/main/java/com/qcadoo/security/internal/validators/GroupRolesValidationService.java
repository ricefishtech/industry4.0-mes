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
package com.qcadoo.security.internal.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.security.api.SecurityService;
import com.qcadoo.security.constants.GroupFields;
import com.qcadoo.security.constants.QcadooSecurityConstants;

@Service
public class GroupRolesValidationService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public boolean checkUserAddingRoleSuperadmin(final DataDefinition dataDefinition, final Entity entity) {

        Boolean isRoleSuperadminInNewGroup = hasRoleSuperAdmin(entity);
        Boolean isRoleSuperadminInOldGroup = entity.getId() == null ? false
                : hasRoleSuperAdmin(dataDefinition.get(entity.getId()));

        if (Objects.equal(isRoleSuperadminInNewGroup, isRoleSuperadminInOldGroup)
                || isCurrentUserShopOrSuperAdmin(dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER,
                        QcadooSecurityConstants.MODEL_USER))) {
            return true;
        }

        entity.addError(dataDefinition.getField(GroupFields.ROLES), "qcadooUsers.validate.global.error.forbiddenRole");
        return false;
    }

    private Boolean hasRoleSuperAdmin(final Entity entity) {
        List<Entity> roles = entity.getManyToManyField(GroupFields.ROLES);
        for (Entity role : roles) {
            if (QcadooSecurityConstants.ROLE_SUPERADMIN.equals(role.getStringField("identifier"))) {
                return true;
            }
        }
        return false;
    }

    private boolean isCurrentUserShopOrSuperAdmin(final DataDefinition userDataDefinition) {
        if (isCalledFromShop()) {
            return true;
        }
        final Long currentUserId = securityService.getCurrentUserId();
        final Entity currentUserEntity = userDataDefinition.get(currentUserId);
        return securityService.hasRole(currentUserEntity, QcadooSecurityConstants.ROLE_SUPERADMIN);
    }

    private boolean isCalledFromShop() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
