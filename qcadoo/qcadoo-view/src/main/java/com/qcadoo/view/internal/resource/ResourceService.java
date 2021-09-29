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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service
public class ResourceService {

    private final Set<ResourceModule> resourceModules = new HashSet<ResourceModule>();

    public void addResourceModule(final ResourceModule resourceModule) {
        if (resourceModules.contains(resourceModule)) {
            throw new IllegalStateException("Module " + resourceModule + " is already added");
        }
        resourceModules.add(resourceModule);
    }

    public void removeResourceModule(final ResourceModule resourceModule) {
        resourceModules.remove(resourceModule);
    }

    public void serveResource(final HttpServletRequest request, final HttpServletResponse response) {

        boolean resourceServed = false;
        for (ResourceModule resourceModule : resourceModules) {
            if (resourceModule.serveResource(request, response)) {
                resourceServed = true;
                continue;
            }
        }
        if (!resourceServed) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "resource not found");
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

}
