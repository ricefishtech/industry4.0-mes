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
package com.qcadoo.report.api.xls;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.qcadoo.report.api.ReportService;

/**
 * Convenient superclass for report Excel document views. Compatible with Apache POI 3.0 as well as 3.5, as of Spring 3.0.
 * 
 * This class is similar to the ReportPdfView class in usage style.
 * 
 */
public abstract class ReportXlsView extends AbstractExcelView {

    @Override
    protected void buildExcelDocument(final Map<String, Object> model, final HSSFWorkbook workbook,
            final HttpServletRequest request, final HttpServletResponse response) {
        String fileName = addContent(model, workbook, LocaleContextHolder.getLocale());
        response.setHeader("Content-disposition",
                "inline; filename=" + fileName + "." + ReportService.ReportType.XLS.getExtension());
    }

    protected abstract String addContent(final Map<String, Object> model, final HSSFWorkbook workbook, final Locale locale);

}
