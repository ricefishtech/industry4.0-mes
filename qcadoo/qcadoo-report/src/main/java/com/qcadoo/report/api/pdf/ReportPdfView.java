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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfWriter;
import com.qcadoo.report.api.FooterResolver;
import com.qcadoo.report.api.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract superclass for report PDF views, using Bruno Lowagie's
 * <a href="http://www.lowagie.com/iText">iText</a> package.
 * Application-specific view classes will extend this class. The view will be
 * held in the subclass itself, not in a template.
 *
 */
public abstract class ReportPdfView extends AbstractPdfView {

    @Autowired
    private PdfHelper pdfHelper;

    @Autowired
    private FooterResolver footerResolver;

    @Override
    protected void buildPdfDocument(final Map<String, Object> model, final Document document, final PdfWriter writer,
            final HttpServletRequest request, final HttpServletResponse response) {
        String fileName;

        try {
            PdfAction ac = PdfAction.gotoLocalPage(1, new PdfDestination(PdfDestination.XYZ, -1, -1, 1f), writer);

            writer.setOpenAction(ac);

            fileName = addContent(document, model, LocaleContextHolder.getLocale(), writer);
        } catch (DocumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        response.setHeader("Content-disposition",
                "inline; filename=" + fileName + "." + ReportService.ReportType.PDF.getExtension());
    }

    public void buildTestedPdfDocumentToFile(final Map<String, Object> model, final String filePath) {

        try {
            Document document = newDocument();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            writer.setViewerPreferences(getViewerPreferences());
            setPageEvent(writer);

            document.open();
            PdfAction ac = PdfAction.gotoLocalPage(1, new PdfDestination(PdfDestination.XYZ, -1, -1, 1f), writer);

            writer.setOpenAction(ac);
            addContent(document, model, LocaleContextHolder.getLocale(), writer);

            document.close();

        } catch (DocumentException | IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    protected Document newDocument() {
        Document doc = super.newDocument();

        doc.setMargins(40, 40, 60, 60);

        return doc;
    }

    @Override
    protected void prepareWriter(final Map<String, Object> model, final PdfWriter writer, final HttpServletRequest request)
            throws DocumentException {
        super.prepareWriter(model, writer, request);

        setPageEvent(writer);
    }

    protected void prepareCoustomWriter(final Map<String, Object> model, final PdfWriter writer, final HttpServletRequest request)
            throws DocumentException {
        super.prepareWriter(model, writer, request);
    }

    protected void setPageEvent(final PdfWriter writer) {
        writer.setPageEvent(new PdfPageNumbering(footerResolver.resolveFooter(LocaleContextHolder.getLocale())));

    }

    @Override
    protected final void buildPdfMetadata(final Map<String, Object> model, final Document document,
            final HttpServletRequest request) {
        addTitle(document, LocaleContextHolder.getLocale());

        pdfHelper.addMetaData(document);
    }

    protected abstract String addContent(final Document document, final Map<String, Object> model, final Locale locale,
            final PdfWriter writer) throws DocumentException, IOException;

    protected abstract void addTitle(final Document document, final Locale locale);

}
