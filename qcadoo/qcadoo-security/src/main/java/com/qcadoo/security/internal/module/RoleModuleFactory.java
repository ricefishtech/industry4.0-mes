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
import com.qcadoo.security.internal.role.InternalSecurityRolesService;

public class RoleModuleFactory extends ModuleFactory<RoleModule> {

	@Autowired
	private DataDefinitionService dataDefinitionService;

	@Autowired
	private InternalSecurityRolesService securityRolesService;

	@Override
	protected RoleModule parseElement(final String pluginIdentifier, final Element element) {
		String identifier = getRequiredAttribute(element, "identifier");
		String description = getAttribute(element, "description");

		checkNotNull(identifier, "Missing identifier attribute of " + getIdentifier() + " module");

		return new RoleModule(identifier, description, dataDefinitionService, securityRolesService);
	}

	@Override
	public String getIdentifier() {
		return "role";
	}

}
