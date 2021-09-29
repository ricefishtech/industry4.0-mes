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
package com.qcadoo.report.api.pdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.file.FileService;
import com.qcadoo.report.api.FooterResolver;
import com.qcadoo.report.api.ReportDocumentService;
import com.qcadoo.report.api.ReportService;

/**
 * Service for creating PDF report documents.
 *
 * @since 0.4.1
 */
public abstract class PdfDocumentService implements ReportDocumentService {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PdfHelper pdfHelper;

    @Autowired
    private FooterResolver footerResolver;

    private static final Logger LOG = LoggerFactory.getLogger(PdfDocumentService.class);

    @Override
    public void generateDocument(final Entity entity, final Locale locale, final Rectangle pageSize) throws IOException,
            DocumentException {
        String path = entity.getStringField("fileName").split(",")[0];

        generate(entity, locale, path, pageSize);
    }

    @Override
    public void generateDocument(final Entity entity, final Locale locale) throws IOException, DocumentException {
        generateDocument(entity, locale, PageSize.A4);
    }

    public void generateDocument(final Entity entity, final Locale locale, final String localePrefixToMatch) throws IOException,
            DocumentException {
        generateDocument(entity, locale, localePrefixToMatch, PageSize.A4);
    }

    public void generateDocument(final Entity entity, final Locale locale, final String localePrefixToMatch,
            final Rectangle pageSize) throws IOException, DocumentException {

        String prefix = translationService.translate(localePrefixToMatch, locale);

        String path = null;
        for (String pt : entity.getStringField("fileName").split(",")) {
            if (fileService.getName(pt).startsWith(prefix)) {
                path = pt;
            }
        }

        if (path == null) {
            throw new IllegalStateException("filename pattern not found");
        }

        generate(entity, locale, path, pageSize);
    }

    private void generate(final Entity entity, final Locale locale, final String filename, final Rectangle pageSize)
            throws IOException, DocumentException {
        Document document = new Document(pageSize);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileService.createReportFile(filename + "."
                    + ReportService.ReportType.PDF.getExtension()));
            PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);
            writer.setPageEvent(new PdfPageNumbering(footerResolver.resolveFooter(locale)));
            document.setMargins(40, 40, 60, 60);
            buildPdfMetadata(document, locale);
            writer.createXmpMetadata();
            document.open();
            buildPdfContent(document, entity, locale);
            document.close();
        } catch (DocumentException e) {
            LOG.error("Problem with generating document - " + e.getMessage());
            document.close();
            throw e;
        }
    }

    protected void buildPdfMetadata(final Document document, final Locale locale) {
        document.addTitle(getReportTitle(locale));
        pdfHelper.addMetaData(document);
    }

    protected abstract void buildPdfContent(final Document document, final Entity entity, final Locale locale)
            throws DocumentException;

}
