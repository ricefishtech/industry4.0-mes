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

import com.google.common.io.BaseEncoding;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.file.FileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public final class AttachmentViewerController {

    public static final String QCADOO_VIEW_ATTACHMENT_VIEWER_HEADER = "qcadooView.attachmentViewer.header";

    public static final String L_ATTACHMENT = "attachment";

    public static final String L_EXT = "ext";

    public static final String L_VIEW_ATTACHMENT_VIEWER = "qcadooView/attachmentViewer";

    public static final String L_TRANSLATIONS_MAP = "translationsMap";

    private static final String L_FILE_NAME = "fileName";

    @Autowired
    private TranslationService translationService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ViewParametersAppender viewParametersAppender;

    @RequestMapping(value = "/attachmentViewer", method = RequestMethod.GET)
    public ModelAndView getAttachmentViewerView(@RequestParam final Map<String, String> arguments, final Locale locale)
            throws UnsupportedEncodingException {

        ModelAndView mav = new ModelAndView();
        String file =  new String(BaseEncoding.base64Url().decode(arguments.get(L_ATTACHMENT)),"utf-8");
        String ext = FilenameUtils.getExtension(file);
        String name =  FilenameUtils.getName(file);
        String url = fileService.getUrl(file);
        mav.addObject(L_ATTACHMENT, url.substring(1, url.length() - 1));
        mav.addObject(L_EXT, ext);
        int index = name.indexOf('_');
        mav.addObject(L_FILE_NAME, name.substring(index+1, name.length() - 1));
        appendTranslations(locale, mav);
        viewParametersAppender.appendCommonViewObjects(mav);
        mav.setViewName(L_VIEW_ATTACHMENT_VIEWER);
        return mav;
    }

    private void appendTranslations(Locale locale, ModelAndView mav) {
        Map<String, String> translationsMap = new HashMap<String, String>();
        translationsMap.put(QCADOO_VIEW_ATTACHMENT_VIEWER_HEADER, translationService.translate(
                QCADOO_VIEW_ATTACHMENT_VIEWER_HEADER, locale));
        mav.addObject(L_TRANSLATIONS_MAP, translationsMap);
    }
}
