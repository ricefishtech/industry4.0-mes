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

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.view.api.crud.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public final class SystemInfoController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private CrudService crudController;

    @Value("${buildApplicationName}")
    private String buildApplicationName;

    @Value("${buildApplicationVersion}")
    private String buildApplicationVersion;

    @Value("${buildVersionForUser}")
    private String buildVersionForUser;

    @Value("${buildFrameworkVersion}")
    private String buildFrameworkVersion;

    @Value("${buildTime}")
    private String buildTime;

    @Value("${buildNumber}")
    private String buildNumber;

    @Value("${buildRevision}")
    private String buildRevision;

    @Autowired
    private ViewParametersAppender viewParametersAppender;

    @RequestMapping(value = "systemInfo", method = RequestMethod.GET)
    public ModelAndView getSystemInfoView(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = crudController.prepareView("qcadooView", "systemInfo", arguments, locale);
        viewParametersAppender.appendCommonViewObjects(mav);

        Map<String, String> translationsMap = new HashMap<String, String>();
        translationsMap.put("qcadooView.systemInfo.header", translationService.translate("qcadooView.systemInfo.header", locale));
        translationsMap.put("qcadooView.systemInfo.buildApplicationName.label",
                translationService.translate("qcadooView.systemInfo.buildApplicationName.label", locale));
        translationsMap.put("qcadooView.systemInfo.buildApplicationVersion.label",
                translationService.translate("qcadooView.systemInfo.buildApplicationVersion.label", locale));
        translationsMap.put("qcadooView.systemInfo.buildFrameworkVersion.label",
                translationService.translate("qcadooView.systemInfo.buildFrameworkVersion.label", locale));
        translationsMap.put("qcadooView.systemInfo.buildNumber.label",
                translationService.translate("qcadooView.systemInfo.buildNumber.label", locale));
        translationsMap.put("qcadooView.systemInfo.buildRevision.label",
                translationService.translate("qcadooView.systemInfo.buildRevision.label", locale));
        translationsMap.put("qcadooView.systemInfo.buildTime.label",
                translationService.translate("qcadooView.systemInfo.buildTime.label", locale));
        mav.addObject("translationsMap", translationsMap);

        mav.addObject("buildApplicationName", buildApplicationName);
        mav.addObject("buildApplicationVersion", buildVersionForUser);
        mav.addObject("buildFrameworkVersion", buildVersionForUser);
        mav.addObject("buildNumber", buildNumber);
        mav.addObject("buildTime", buildTime);
        mav.addObject("buildRevision", buildRevision);

        return mav;
    }
}
