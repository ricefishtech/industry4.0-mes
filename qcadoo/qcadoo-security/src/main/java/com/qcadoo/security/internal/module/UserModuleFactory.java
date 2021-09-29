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
package com.qcadoo.security.internal.module;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.plugin.api.ModuleFactory;

public class UserModuleFactory extends ModuleFactory<UserModule> {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    protected UserModule parseElement(final String pluginIdentifier, final Element element) {
        String login = getRequiredAttribute(element, "login");
        String email = getAttribute(element, "email");
        String firstName = getAttribute(element, "firstName");
        String lastName = getAttribute(element, "lastName");
        String password = getRequiredAttribute(element, "password");
        String groupIdentifier = getRequiredAttribute(element, "groupIdentifier");

        checkNotNull(login, "Missing login attribute of " + getIdentifier() + " module");
        checkNotNull(password, "Missing password attribute of " + getIdentifier() + " module");
        checkNotNull(groupIdentifier, "Missing groupIdentifier attribute of " + getIdentifier() + " module");

        return new UserModule(login, email, firstName, lastName, password, groupIdentifier, dataDefinitionService);
    }

    @Override
    public String getIdentifier() {
        return "user";
    }

}
