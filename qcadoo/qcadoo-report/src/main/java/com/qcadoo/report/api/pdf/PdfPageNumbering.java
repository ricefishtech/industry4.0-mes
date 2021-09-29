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

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.report.api.ColorUtils;
import com.qcadoo.report.api.FontUtils;
import com.qcadoo.report.api.Footer;

/**
 * /** Helps the use of <CODE>PdfPageEvent</CODE> to provide page numbering, page footer and page header .
 * 
 * 
 */
public final class PdfPageNumbering extends PdfPageEventHelper {

    /** The PdfTemplate that contains the total number of pages. */
    private PdfTemplate total;

    private final String generationDate;

    private final Footer footer;

    private final boolean addHeader;

    private final boolean addFooter;

    private static final Logger LOG = LoggerFactory.getLogger(PdfPageNumbering.class);

    /**
     * Constructor which prepare data for class events.
     * 
     * @param footer
     * 
     */
    public PdfPageNumbering(final Footer footer) {
        super();

        this.footer = footer;
        this.generationDate = new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, getLocale()).format(new Date());
        this.addHeader = true;
        this.addFooter = true;
    }

    /**
     * Constructor which prepare data for class events.
     *
     * @param footer
     *
     * @param addHeader
     *
     * @param addFooter
     *
     */
    public PdfPageNumbering(final Footer footer, final boolean addHeader, final boolean addFooter) {
        super();

        this.footer = footer;
        this.generationDate = new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, getLocale()).format(new Date());
        this.addHeader = addHeader;
        this.addFooter = addFooter;
    }

    /**
     * @see com.lowagie.text.pdf.PdfPageEvent#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onOpenDocument(final PdfWriter writer, final Document document) {
        total = writer.getDirectContent().createTemplate(100, 100);
        total.setBoundingBox(new Rectangle(-20, -20, 100, 100));

        try {
            ColorUtils.prepare();

            FontUtils.prepare();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /**
     * @see com.lowagie.text.pdf.PdfPageEvent#onStartPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onStartPage(final PdfWriter writer, final Document document) {

    }

    /**
     * @see com.lowagie.text.pdf.PdfPageEvent#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onEndPage(final PdfWriter writer, final Document document) {
        if (addHeader) {
            buildHeader(writer, document);
        }
        if (addFooter) {
            buildFooter(writer, document);
        }
    }

    private void buildFooter(final PdfWriter writer, final Document document) {
        PdfContentByte cb = writer.getDirectContent();

        cb.saveState();

        String text = footer.getPage() + " " + writer.getPageNumber() + " " + footer.getIn() + " ";

        float textBase = document.bottom() - 25;
        float textSize = FontUtils.getDejavu().getWidthPoint(text, 7);

        cb.setColorFill(ColorUtils.getLightColor());
        cb.setColorStroke(ColorUtils.getLightColor());
        cb.setLineWidth(1);
        cb.setLineDash(2, 2, 1);
        cb.moveTo(document.left(), document.bottom() - 10);
        cb.lineTo(document.right(), document.bottom() - 10);
        cb.stroke();
        cb.beginText();
        cb.setFontAndSize(FontUtils.getDejavu(), 7);

        float adjust = FontUtils.getDejavu().getWidthPoint("0", 7);

        cb.setTextMatrix(document.right() - textSize - adjust, textBase);
        cb.showText(text);

        textSize = FontUtils.getDejavu().getWidthPoint(footer.getGeneratedBy(), 7);

        cb.setTextMatrix(document.right() - textSize, textBase - 10);
        cb.showText(footer.getGeneratedBy());

        textSize = FontUtils.getDejavu().getWidthPoint(generationDate, 7);

        cb.setTextMatrix(document.right() - textSize, textBase - 20);
        cb.showText(generationDate);
        cb.endText();

        try {
            textSize = FontUtils.getDejavu().getWidthPoint(footer.getAdditionalText(), 7);

            ColumnText ct = new ColumnText(cb);

            ct.setSimpleColumn(new Phrase(footer.getAdditionalText(), FontUtils.getDejavuRegular7Light()), document.left() + 240,
                    textBase + 10, document.left() + 390, textBase - 25, 10, Element.ALIGN_LEFT);
            ct.go();
        } catch (DocumentException e) {
            LOG.warn("Problem with additional text generation in report footer.");
        }

        try {
            ColumnText ct = new ColumnText(cb);

            ct.setSimpleColumn(document.left(), textBase + 10, document.left() + 230, textBase - 25, 10, Element.ALIGN_LEFT);
            ct.addText(new Phrase(footer.getCompanyName() + "\n", FontUtils.getDejavuRegular7Light()));

            if (!"".equals(footer.getAddress())) {
                ct.addText(new Phrase(footer.getAddress() + "\n", FontUtils.getDejavuRegular7Light()));
            }
            if (!"".equals(footer.getPhoneEmail())) {
                ct.addText(new Phrase(footer.getPhoneEmail(), FontUtils.getDejavuRegular7Light()));
            }

            ct.go();
        } catch (DocumentException e) {
            LOG.warn("Problem with company text generation in report footer.");
        }

        cb.addTemplate(total, document.right() - adjust, textBase);
        cb.restoreState();

    }

    private void buildHeader(final PdfWriter writer, final Document document) {
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        String text = footer.getPage() + " " + writer.getPageNumber() + " " + footer.getIn() + " ";

        float textBase = document.top() + 22;
        float textSize = FontUtils.getDejavu().getWidthPoint(text, 7);

        cb.setColorFill(ColorUtils.getLightColor());
        cb.setColorStroke(ColorUtils.getLightColor());
        cb.beginText();
        cb.setFontAndSize(FontUtils.getDejavu(), 7);

        float adjust = FontUtils.getDejavu().getWidthPoint("0", 7);

        cb.setTextMatrix(document.right() - textSize - adjust, textBase);
        cb.showText(text);
        cb.endText();
        cb.addTemplate(total, document.right() - adjust, textBase);
        cb.setLineWidth(1);
        cb.setLineDash(2, 2, 1);
        cb.moveTo(document.left(), document.top() + 12);
        cb.lineTo(document.right(), document.top() + 12);
        cb.stroke();
        cb.restoreState();

    }

    /**
     * @see com.lowagie.text.pdf.PdfPageEvent#onCloseDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onCloseDocument(final PdfWriter writer, final Document document) {
        total.beginText();
        total.setFontAndSize(FontUtils.getDejavu(), 7);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(writer.getPageNumber() - 1));
        total.endText();
    }

}
