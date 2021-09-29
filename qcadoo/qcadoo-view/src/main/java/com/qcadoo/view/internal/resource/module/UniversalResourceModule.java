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
package com.qcadoo.view.internal.resource.module;

import java.io.IOException;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.qcadoo.view.internal.resource.ResourceModule;
import com.qcadoo.view.internal.resource.ResourceService;

public class UniversalResourceModule extends ResourceModule {

    private final ApplicationContext applicationContext;

    private final String uriPattern;

    private final PathMatcher matcher = new AntPathMatcher();

    public UniversalResourceModule(final ResourceService resourceService, final ApplicationContext applicationContext,
            final String pluginIdentifier, final String uriPattern) {
        super(resourceService);
        this.applicationContext = applicationContext;
        if (uriPattern.charAt(0) == '/') {
            this.uriPattern = "/" + pluginIdentifier + uriPattern;
        } else {
            this.uriPattern = "/" + pluginIdentifier + "/" + uriPattern;
        }

    }

    @Override
    public boolean serveResource(final HttpServletRequest request, final HttpServletResponse response) {
        Resource resource = getResourceFromURI(request.getRequestURI());
        if (resource != null && resource.exists()) {
            response.setContentType(getContentTypeFromURI(request));
            try {
                IOUtils.copy(resource.getInputStream(), response.getOutputStream());
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return true;
        } else {
            return false;
        }
    }

    private Resource getResourceFromURI(final String uri) {
        if (matcher.match(uriPattern, uri)) {
            return applicationContext.getResource("classpath:" + uri);
        }
        return null;
    }

    private String getContentTypeFromURI(final HttpServletRequest request) {
        String[] arr = request.getRequestURI().split("\\.");
        String ext = arr[arr.length - 1];
        if ("js".equals(ext)) {
            return "text/javascript";
        } else if ("css".equals(ext)) {
            return "text/css";
        } else {
            return URLConnection.guessContentTypeFromName(request.getRequestURL().toString());
        }
    }

}
