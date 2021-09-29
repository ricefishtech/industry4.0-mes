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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.file.FileUtils;
import com.qcadoo.view.api.crud.CrudService;
import com.qcadoo.view.constants.QcadooViewConstants;

@Controller
public class FileUploadController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private CrudService crudController;

    @Autowired
    private ViewParametersAppender viewParametersAppender;

    @Value("${maxUploadSize:5242880}")
    private int maxUploadSize;

    @RequestMapping(value = "fileUpload", method = RequestMethod.GET)
    public ModelAndView upload(final Locale locale) {
        ModelAndView mav = getCrudPopupView(QcadooViewConstants.VIEW_FILE_UPLOAD, locale);
        viewParametersAppender.appendCommonViewObjects(mav);

        mav.addObject("headerLabel", translationService.translate("qcadooView.fileUpload.header", locale));
        mav.addObject("buttonLabel", translationService.translate("qcadooView.fileUpload.button", locale));
        mav.addObject("chooseFileLabel", translationService.translate("qcadooView.fileUpload.chooseFileLabel", locale));
        mav.addObject("maxUploadSizeExceeded",
                translationService.translate("qcadooView.errorPage.error.uploadException.maxSizeExceeded.explanation", locale, "" + maxUploadSize/1000000));

        return mav;
    }

    private ModelAndView getCrudPopupView(final String viewName, final Locale locale) {
        Map<String, String> crudArgs = new HashMap<String, String>();
        crudArgs.put("popup", "true");
        return crudController.prepareView(QcadooViewConstants.PLUGIN_IDENTIFIER, viewName, crudArgs, locale);
    }

    @RequestMapping(value = "fileUpload", method = RequestMethod.POST)
    public void upload(@RequestParam("file") final MultipartFile file, final HttpServletResponse httpResponse, final Locale locale) {
        String error = null;
        String path = null;

        try {
            path = FileUtils.getInstance().upload(file);
        } catch (IOException e) {
            error = e.getMessage();
        }

        JSONObject response = new JSONObject();
        try {
            if (path == null) {
                response.put("fileLastModificationDate", "");
                response.put("fileUrl", "");
                response.put("fileName", "");
                response.put("filePath", "");
            } else {
                response.put("fileLastModificationDate", FileUtils.getInstance().getLastModificationDate(path));
                response.put("fileUrl", FileUtils.getInstance().getUrl(path));
                response.put("fileName", FileUtils.getInstance().getName(path));
                response.put("filePath", path);
            }
            response.put("fileUploadError", error);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        try {
            httpResponse.setContentType("text/plain");
            httpResponse.getWriter().print(response.toString());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
