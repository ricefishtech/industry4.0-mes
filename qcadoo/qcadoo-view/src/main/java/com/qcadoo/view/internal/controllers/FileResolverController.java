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
package com.qcadoo.view.internal.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qcadoo.model.api.file.FileService;
import com.qcadoo.tenant.api.MultiTenantUtil;

@Controller
public class FileResolverController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "{tenantId:\\d+}/{firstLevel:\\d+}/{secondLevel:\\d+}/{fileName}", method = RequestMethod.GET)
    public void resolve(final HttpServletRequest request, final HttpServletResponse response,
            @PathVariable("tenantId") final String tenantId) {
        String path = fileService.getPathFromUrl(request.getRequestURI());

        boolean removeFileAfterProcessing = request.getParameterMap().containsKey("clean");

        if (Integer.valueOf(tenantId) != MultiTenantUtil.getCurrentTenantId()) {
            try {
                response.sendRedirect("/error.html?code=404");
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        InputStream input = null;

        try {
            input = fileService.getInputStream(path);

            if (input == null) {
                response.sendRedirect("/error.html?code=404");
            } else {
                response.setHeader("Content-disposition", "inline; filename=" + fileService.getName(path));
                response.setContentType(fileService.getContentType(path));
                OutputStream output = response.getOutputStream();
                IOUtils.copy(input, output);
                output.flush();
            }
        } catch (IOException e) {
            IOUtils.closeQuietly(input);
            throw new IllegalStateException(e.getMessage(), e);
        }

        if (removeFileAfterProcessing) {
            fileService.remove(path);
        }
    }

}
