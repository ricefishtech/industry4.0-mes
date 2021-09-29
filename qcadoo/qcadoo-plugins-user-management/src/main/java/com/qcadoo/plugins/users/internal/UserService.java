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
package com.qcadoo.plugins.users.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.security.api.SecurityService;
import com.qcadoo.security.constants.QcadooSecurityConstants;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;
import com.qcadoo.view.api.components.WindowComponent;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import com.qcadoo.view.api.ribbon.RibbonGroup;

@Service
public final class UserService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void setPasswordAndOldPasswordAdRequired(final ViewDefinitionState state) {
        FieldComponent viewIdentifier = (FieldComponent) state.getComponentByReference("viewIdentifierHiddenInput");
        FieldComponent oldPassword = (FieldComponent) state.getComponentByReference("oldPasswordTextInput");
        FieldComponent password = (FieldComponent) state.getComponentByReference("passwordTextInput");
        FieldComponent passwordConfirmation = (FieldComponent) state.getComponentByReference("passwordConfirmationTextInput");

        oldPassword.setRequired(true);
        password.setRequired(true);
        passwordConfirmation.setRequired(true);
        viewIdentifier.setFieldValue("profileChangePassword");
    }

    public void setPasswordAsRequired(final ViewDefinitionState state) {
        FieldComponent viewIdentifier = (FieldComponent) state.getComponentByReference("viewIdentifierHiddenInput");
        FieldComponent password = (FieldComponent) state.getComponentByReference("passwordTextInput");
        FieldComponent passwordConfirmation = (FieldComponent) state.getComponentByReference("passwordConfirmationTextInput");

        password.setRequired(true);
        passwordConfirmation.setRequired(true);
        viewIdentifier.setFieldValue("userChangePassword");
    }

    public void hidePasswordOnUpdateForm(final ViewDefinitionState state) {
        FormComponent form = (FormComponent) state.getComponentByReference("form");
        FieldComponent password = (FieldComponent) state.getComponentByReference("passwordTextInput");
        FieldComponent passwordConfirmation = (FieldComponent) state.getComponentByReference("passwordConfirmationTextInput");
        ComponentState changePasswordButton = state.getComponentByReference("changePasswordButton");

        password.setRequired(true);
        passwordConfirmation.setRequired(true);

        if (form.getEntityId() == null) {
            password.setVisible(true);
            passwordConfirmation.setVisible(true);
            changePasswordButton.setVisible(false);
        } else {
            password.setVisible(false);
            passwordConfirmation.setVisible(false);
            changePasswordButton.setVisible(true);
        }
    }

    public void disableFormForAdmin(final ViewDefinitionState state) {
        FormComponent form = (FormComponent) state.getComponentByReference("form");

        Entity loggedUser = dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER,
                QcadooSecurityConstants.MODEL_USER).get(securityService.getCurrentUserId());

        if (!securityService.hasRole(loggedUser, "ROLE_SUPERADMIN")) {
            form.setFormEnabled(false);
        }
    }

    public void disableFormForSuperadmin(final ViewDefinitionState state) {
        FormComponent form = (FormComponent) state.getComponentByReference("form");

        Long viewedUserId = form.getEntityId();
        Entity viewedUser = dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER,
                QcadooSecurityConstants.MODEL_USER).get(viewedUserId);
        Entity loggedUser = dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER,
                QcadooSecurityConstants.MODEL_USER).get(securityService.getCurrentUserId());

        if (!securityService.hasRole(loggedUser, "ROLE_SUPERADMIN") && securityService.hasRole(viewedUser, "ROLE_SUPERADMIN")) {
            form.setFormEnabled(false);
        }
    }

    public void setupRibbonForAdmins(final ViewDefinitionState state) {
        WindowComponent window = (WindowComponent) state.getComponentByReference("window");
        if(!securityService.hasCurrentUserRole("ROLE_SUPERADMIN") && securityService.hasCurrentUserRole("ROLE_ADMIN")){
            RibbonGroup actions = window.getRibbon().getGroupByName("actions");
            for (RibbonActionItem actionItem : actions.getItems()) {
                actionItem.setEnabled(false);
                actionItem.requestUpdate(true);
            }
        }
    }

}
