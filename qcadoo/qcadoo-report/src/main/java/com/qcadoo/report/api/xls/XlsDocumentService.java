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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.file.FileService;
import com.qcadoo.report.api.ReportDocumentService;
import com.qcadoo.report.api.ReportService;

/**
 * Service for creating XLS report documents.
 * 
 * 
 */
public abstract class XlsDocumentService implements ReportDocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(XlsDocumentService.class);

    @Autowired
    private FileService fileService;

    @Override
    public final void generateDocument(final Entity entity, final Locale locale) throws IOException {
        generateDocument(entity, locale, PageSize.A4);
    }

    @Override
    public final void generateDocument(final Entity entity, final Locale locale, final Rectangle pageSize) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = createSheet(workbook, getReportTitle(locale));
        addHeader(sheet, locale, entity);
        addSeries(sheet, entity);
        addExtraSheets(workbook, entity, locale);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileService.createReportFile((String) entity.getField("fileName") + "."
                    + ReportService.ReportType.XLS.getExtension()));
            workbook.write(outputStream);
        } catch (IOException e) {
            LOG.error("Problem with generating document - " + e.getMessage());
            if (outputStream != null) {
                outputStream.close();
            }
            throw e;
        }
        outputStream.close();
    }

    protected abstract void addHeader(final HSSFSheet sheet, final Locale locale, final Entity entity);

    protected abstract void addSeries(final HSSFSheet sheet, final Entity entity);

    protected void addExtraSheets(final HSSFWorkbook workbook, Entity entity, Locale locale) {

    }

    protected HSSFSheet createSheet(final HSSFWorkbook workbook, final String title) {
        HSSFSheet sheet = workbook.createSheet(title);
        sheet.setZoom(4, 3);
        return sheet;
    }

}
