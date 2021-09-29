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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ViewParametersAppender {

    @Value("${applicationDisplayName}")
    private String applicationDisplayName;

    @Value("${useCompressedStaticResources}")
    private boolean useCompressedStaticResources;

    @Value("${applicationProfile}")
    private String applicationProfile;

    public void appendCommonViewObjects(final ModelAndView mav) {
        mav.addObject("applicationDisplayName", applicationDisplayName);
        mav.addObject("useCompressedStaticResources", useCompressedStaticResources);
        mav.addObject("applicationProfile", applicationProfile);
    }

    public String getApplicationProfile() {
        return applicationProfile;
    }

}
