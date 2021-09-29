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
package com.qcadoo.report.api.pdf.elements;

import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.qcadoo.report.api.FontUtils;

public final class Headers {

    private Headers() {
    }

    public static Paragraph big(final String text) {
        return paragraph(text, FontUtils.getDejavuBold11Dark(), 8.0f, 8.0f);
    }

    public static Paragraph small(final String text) {
        return paragraph(text, FontUtils.getDejavuBold8Dark(), 2.0f, 2.0f);
    }

    private static Paragraph paragraph(final String text, final Font font, final float marginTop, final float marginBottom) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setSpacingBefore(marginTop);
        paragraph.setSpacingAfter(marginBottom);
        return paragraph;
    }

}
