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

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.security.api.SecurityService;
import com.qcadoo.view.internal.LogoComponent;
import com.qcadoo.view.internal.api.ViewDefinitionService;
import com.qcadoo.view.internal.menu.MenuService;

@Controller
public final class MainController {

    private static final String LOGO_PATH = "logoPath";

    @Value("${dbNotificationsEnabled:true}")
    private boolean dbNotificationsEnabled;

    @Value("${systemNotificationsEnabled:false}")
    private boolean systemNotificationsEnabled;

    @Value("${systemNotificationsIntervalInSeconds:30}")
    private int systemNotificationsIntervalInSeconds;

    @Value("${activityStreamEnabled:true}")
    private boolean activityStreamEnabled;

    @Value("${activityStreamIntervalInSeconds:10}")
    private int activityStreamIntervalInSeconds;

    @Autowired
    private ViewDefinitionService viewDefinitionService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ViewParametersAppender viewParametersAppender;

    @Autowired
    private LogoComponent logoComponent;

    @RequestMapping(value = "main", method = RequestMethod.GET)
    public ModelAndView getMainView(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("qcadooView/main");

        viewParametersAppender.appendCommonViewObjects(mav);

        mav.addObject("viewsList", viewDefinitionService.list());
        mav.addObject("commonTranslations", translationService.getMessagesGroup("commons", locale));
        mav.addObject("menuStructure", menuService.getMenu(locale).getAsJson());
        mav.addObject("userLogin", securityService.getCurrentUserName());
        mav.addObject("languageCode", LocaleContextHolder.getLocale().getLanguage());
        mav.addObject("dbNotificationsEnabled", dbNotificationsEnabled);
        mav.addObject("systemNotificationsEnabled", systemNotificationsEnabled);
        mav.addObject("systemNotificationsIntervalInSeconds", systemNotificationsIntervalInSeconds);
        mav.addObject("activityStreamEnabled", activityStreamEnabled);
        mav.addObject("activityStreamIntervalInSeconds", activityStreamIntervalInSeconds);
        mav.addObject("logoPath", logoComponent.prepareMenuLogoPath());

        return mav;
    }

    @RequestMapping(value = "noDashboard", method = RequestMethod.GET)
    public ModelAndView getStartViewWhenNoDashboard(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("qcadooView/noDashboard");

        viewParametersAppender.appendCommonViewObjects(mav);

        mav.addObject("userLogin", securityService.getCurrentUserName());
        mav.addObject("translationsMap", translationService.getMessagesGroup("noDashboard", locale));

        return mav;
    }

}
