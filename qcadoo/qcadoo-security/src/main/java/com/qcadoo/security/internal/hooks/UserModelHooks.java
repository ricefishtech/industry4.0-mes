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
package com.qcadoo.security.internal.hooks;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.security.api.SecurityService;
import com.qcadoo.security.constants.UserFields;

@Service
public class UserModelHooks {

    private static final String SELF_DELETION_ERROR = "security.message.error.selfDeletion";

    @Autowired
    private SecurityService securityService;

    public boolean preventSelfDeletion(final DataDefinition userDD, final Entity user) {
        if (ObjectUtils.equals(securityService.getCurrentUserId(), user.getId())) {
            user.addGlobalError(SELF_DELETION_ERROR);
            return false;
        }
        return true;
    }

    public void setDefaultNames(final DataDefinition userDD, final Entity user) {
        replaceByUserNameIfBlank(user, UserFields.FIRST_NAME);
        replaceByUserNameIfBlank(user, UserFields.LAST_NAME);
    }

    private void replaceByUserNameIfBlank(final Entity user, final String fieldName) {
        String fieldValue = user.getStringField(fieldName);
        if (StringUtils.isBlank(fieldValue)) {
            user.setField(fieldName, user.getStringField(UserFields.USER_NAME));
        }
    }

}
