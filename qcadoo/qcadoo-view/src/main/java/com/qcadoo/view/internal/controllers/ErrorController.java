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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.qcadoo.localization.api.TranslationService;

@Controller
public class ErrorController {

    @Autowired
    private TranslationService translationService;

    @Value("${showExceptionDetails}")
    private boolean showExceptionDetails;

    @Autowired
    private ViewParametersAppender viewParametersAppender;

    @RequestMapping(value = "error")
    public ModelAndView getAccessDeniedPageView(@RequestParam final int code, final Locale locale) {
        return getAccessDeniedPageView(code, null, null, null, locale);
    }

    public ModelAndView getAccessDeniedPageView(@RequestParam final int code, final Exception exception,
            final String predefinedExceptionMessageHeader, final String predefinedExceptionMessageExplanation, final Locale locale) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("qcadooView/error");
        viewParametersAppender.appendCommonViewObjects(mav);

        mav.addObject("code", code);

        if (predefinedExceptionMessageHeader == null || predefinedExceptionMessageExplanation == null) {
            String errorHeader = null;
            String errorExplanation = null;
            switch (code) {
                case 400: // Bad Request
                    errorHeader = "qcadooView.errorPage.error.badRequest.header";
                    errorExplanation = "qcadooView.errorPage.error.badRequest.explanation";
                    break;
                case 402: // Payment Required
                    errorHeader = "qcadooView.errorPage.error.paymentRequired.header";
                    errorExplanation = "qcadooView.errorPage.error.paymentRequired.explanation";
                    break;
                case 403: // Forbidden
                    errorHeader = "qcadooView.errorPage.error.forbidden.header";
                    errorExplanation = "qcadooView.errorPage.error.forbidden.explanation";
                    break;
                case 404: // Not found
                    errorHeader = "qcadooView.errorPage.error.notFound.header";
                    errorExplanation = "qcadooView.errorPage.error.notFound.explanation";
                    break;
                case 500: // Internal Error
                    errorHeader = "qcadooView.errorPage.error.internalError.header";
                    errorExplanation = "qcadooView.errorPage.error.internalError.explanation";
                    break;
                case 503: // Gateway Timeout
                    errorHeader = "qcadooView.errorPage.error.gatewayTimeout.header";
                    errorExplanation = "qcadooView.errorPage.error.gatewayTimeout.explanation";
                    break;
                default:
                    errorHeader = "qcadooView.errorPage.error.defaultError.header";
                    errorExplanation = "qcadooView.errorPage.error.defaultError.explanation";
                    break;
            }
            mav.addObject("errorHeader", translationService.translate(errorHeader, locale));
            mav.addObject("errorExplanation", translationService.translate(errorExplanation, locale));

        } else {
            mav.addObject("errorHeader", predefinedExceptionMessageHeader);
            mav.addObject("errorExplanation", predefinedExceptionMessageExplanation);
        }

        mav.addObject("showDetailsText", translationService.translate("qcadooView.errorPage.showDetails", locale));
        mav.addObject("hideDetailsText", translationService.translate("qcadooView.errorPage.hideDetails", locale));

        if (exception != null && showExceptionDetails) {
            mav.addObject("showDetails", true);

            mav.addObject("rootException", getRootException(exception));
            mav.addObject("stackTrace", getStackTrace(exception));

            mav.addObject("exceptionCauseText", translationService.translate("qcadooView.errorPage.details.causeText", locale));
            mav.addObject("exceptionStackTraceText",
                    translationService.translate("qcadooView.errorPage.details.stackTraceText", locale));
        } else {
            mav.addObject("showDetails", false);
        }

        return mav;
    }

    private Throwable getRootException(final Exception exception) {
        Throwable rootException = exception;
        while (rootException.getCause() != null) {
            rootException = rootException.getCause();
        }
        return rootException;
    }

    private String getStackTrace(final Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();
        return stackTrace.replace(System.getProperty("line.separator"), "<br/>\n").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }
}
