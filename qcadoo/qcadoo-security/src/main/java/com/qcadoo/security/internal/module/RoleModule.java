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

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.plugin.api.Module;
import com.qcadoo.security.internal.role.InternalSecurityRolesService;
import com.qcadoo.security.internal.role.SimpleSecurityRole;

public class RoleModule extends Module {

	private final String identifier;

	private final String description;

	private final DataDefinitionService dataDefinitionService;

	private final InternalSecurityRolesService securityRolesService;

	public RoleModule(final String identifier, final String description, final DataDefinitionService dataDefinitionService,
	        final InternalSecurityRolesService securityRolesService) {
		super();

		this.identifier = identifier;
		this.description = description;
		this.dataDefinitionService = dataDefinitionService;
		this.securityRolesService = securityRolesService;
	}

	@Override
	public void multiTenantEnable() {
		if (dataDefinitionService.get("qcadooSecurity", "role").find().add(SearchRestrictions.eq("identifier", identifier))
		        .list().getTotalNumberOfEntities() > 0) {
			return;
		}

		Entity entity = dataDefinitionService.get("qcadooSecurity", "role").create();
		entity.setField("identifier", identifier);
		entity.setField("description", description);
		dataDefinitionService.get("qcadooSecurity", "role").save(entity);
	}

	@Override
	public void enableOnStartup() {
		securityRolesService.addRole(new SimpleSecurityRole(identifier, true));
	}

	@Override
	public void enable() {
		enableOnStartup();
	}
}
