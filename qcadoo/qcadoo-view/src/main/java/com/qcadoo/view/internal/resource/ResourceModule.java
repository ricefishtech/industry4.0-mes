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
package com.qcadoo.view.internal.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qcadoo.plugin.api.Module;

public abstract class ResourceModule extends Module {

    private final ResourceService resourceService;

    public ResourceModule(final ResourceService resourceService) {
        super();

        this.resourceService = resourceService;
    }

    @Override
    public final void enableOnStartup() {
        enable();
    }

    @Override
    public final void enable() {
        resourceService.addResourceModule(this);
    }

    @Override
    public final void disable() {
        resourceService.removeResourceModule(this);
    }

    /**
     * Serves resource to response
     * 
     * @param request
     * @param response
     * @return true when resource was served
     */
    public abstract boolean serveResource(final HttpServletRequest request, final HttpServletResponse response);
}
