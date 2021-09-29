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
package com.qcadoo.report.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;

/**
 * Utilities for pdf fonts management.
 * 
 */
public final class FontUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FontUtils.class);

    private static Font dejavuBold70Light;

    private static Font dejavuBold70Dark;

    private static Font dejavuBold60Dark;

    private static Font dejavuBold19Light;

    private static Font dejavuBold19Dark;

    private static Font dejavuBold14Dark;

    private static Font dejavuBold17Light;

    private static Font dejavuBold14Light;

    private static Font dejavuBold17Dark;

    private static Font dejavuBold11Dark;

    private static Font dejavuBold11Light;

    private static Font dejavuRegular9Light;

    private static Font dejavuRegular9Dark;

    private static Font dejavuBold9Dark;

    private static Font dejavuRegular10Dark;

    private static Font dejavuBold10Dark;

    private static Font dejavuRegular7Light;

    private static Font dejavuRegular7Dark;

    private static Font dejavuBold7Dark;

    private static Font dejavuBold8Dark;

    private static Font dejavuBold140Dark;

    private static BaseFont dejavu;

    private FontUtils() {
    }

    /**
     * Prepare fonts.
     * 
     * @throws DocumentException
     * @throws IOException
     */
    public static synchronized void prepare() throws DocumentException, IOException {
        if (dejavuBold10Dark == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Pdf fonts initialization");
            }
            try {
                FontFactory.register("/fonts/dejaVu/stsong.ttf");
                dejavu = BaseFont.createFont("/fonts/dejaVu/stsong.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (ExceptionConverter e) {
                LOG.warn("Font not found, using embedded font helvetica");
                dejavu = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            }
            dejavuBold70Light = new Font(dejavu, 70);
            dejavuBold70Light.setStyle(Font.BOLD);
            dejavuBold70Light.setColor(ColorUtils.getLightColor());

            dejavuBold70Dark = new Font(dejavu, 70);
            dejavuBold70Dark.setStyle(Font.BOLD);
            dejavuBold70Dark.setColor(ColorUtils.getDarkColor());

            dejavuBold60Dark = new Font(dejavu, 60);
            dejavuBold60Dark.setStyle(Font.BOLD);
            dejavuBold60Dark.setColor(ColorUtils.getDarkColor());

            dejavuBold19Light = new Font(dejavu, 19);
            dejavuBold19Light.setStyle(Font.BOLD);
            dejavuBold19Light.setColor(ColorUtils.getLightColor());

            dejavuBold19Dark = new Font(dejavu, 19);
            dejavuBold19Dark.setStyle(Font.BOLD);
            dejavuBold19Dark.setColor(ColorUtils.getDarkColor());

            dejavuBold14Dark = new Font(dejavu, 14);
            dejavuBold14Dark.setStyle(Font.BOLD);
            dejavuBold14Dark.setColor(ColorUtils.getDarkColor());

            dejavuBold17Light = new Font(dejavu, 17);
            dejavuBold17Light.setStyle(Font.BOLD);
            dejavuBold17Light.setColor(ColorUtils.getLightColor());

            dejavuBold14Light = new Font(dejavu, 17);
            dejavuBold14Light.setStyle(Font.BOLD);
            dejavuBold14Light.setColor(ColorUtils.getLightColor());

            dejavuBold17Dark = new Font(dejavu, 17);
            dejavuBold17Dark.setStyle(Font.BOLD);
            dejavuBold17Dark.setColor(ColorUtils.getDarkColor());

            dejavuRegular9Light = new Font(dejavu, 9);
            dejavuRegular9Light.setColor(ColorUtils.getLightColor());

            dejavuRegular9Dark = new Font(dejavu, 9);
            dejavuRegular9Dark.setColor(ColorUtils.getDarkColor());

            dejavuRegular7Dark = new Font(dejavu, 7);
            dejavuRegular7Dark.setColor(ColorUtils.getDarkColor());

            dejavuBold9Dark = new Font(dejavu, 9);
            dejavuBold9Dark.setColor(ColorUtils.getDarkColor());
            dejavuBold9Dark.setStyle(Font.BOLD);

            dejavuBold11Dark = new Font(dejavu, 11);
            dejavuBold11Dark.setColor(ColorUtils.getDarkColor());
            dejavuBold11Dark.setStyle(Font.BOLD);

            dejavuBold11Light = new Font(dejavu, 11);
            dejavuBold11Light.setColor(ColorUtils.getLightColor());
            dejavuBold11Light.setStyle(Font.BOLD);

            dejavuRegular10Dark = new Font(dejavu, 10);
            dejavuRegular10Dark.setColor(ColorUtils.getDarkColor());

            dejavuBold10Dark = new Font(dejavu, 10);
            dejavuBold10Dark.setColor(ColorUtils.getDarkColor());
            dejavuBold10Dark.setStyle(Font.BOLD);

            dejavuRegular7Light = new Font(dejavu, 7);
            dejavuRegular7Light.setColor(ColorUtils.getLightColor());

            dejavuBold7Dark = new Font(dejavu, 8);
            dejavuBold7Dark.setColor(ColorUtils.getDarkColor());
            dejavuBold7Dark.setStyle(Font.BOLD);

            dejavuBold8Dark = new Font(dejavu, 8);
            dejavuBold8Dark.setColor(ColorUtils.getDarkColor());
            dejavuBold8Dark.setStyle(Font.BOLD);

            dejavuBold140Dark = new Font(dejavu, 120);
            dejavuBold140Dark.setStyle(Font.BOLD);
            dejavuBold140Dark.setColor(ColorUtils.getDarkColor());
        }
    }

    public static Font getDejavuBold70Light() {
        return dejavuBold70Light;
    }

    public static Font getDejavuBold70Dark() {
        return dejavuBold70Dark;
    }

    public static Font getDejavuBold60Dark() {
        return dejavuBold60Dark;
    }

    public static Font getDejavuBold19Light() {
        return dejavuBold19Light;
    }

    public static Font getDejavuBold19Dark() {
        return dejavuBold19Dark;
    }

    public static Font getDejavuBold17Light() {
        return dejavuBold17Light;
    }

    public static Font getDejavuBold14Light() {
        return dejavuBold17Light;
    }

    public static Font getDejavuBold17Dark() {
        return dejavuBold17Dark;
    }

    public static Font getDejavuBold11Dark() {
        return dejavuBold11Dark;
    }

    public static Font getDejavuBold11Light() {
        return dejavuBold11Light;
    }

    public static Font getDejavuRegular9Light() {
        return dejavuRegular9Light;
    }

    public static Font getDejavuRegular9Dark() {
        return dejavuRegular9Dark;
    }

    public static Font getDejavuBold9Dark() {
        return dejavuBold9Dark;
    }

    public static Font getDejavuRegular10Dark() {
        return dejavuRegular10Dark;
    }

    public static Font getDejavuBold10Dark() {
        return dejavuBold10Dark;
    }

    public static Font getDejavuRegular7Light() {
        return dejavuRegular7Light;
    }

    public static Font getDejavuRegular7Dark() {
        return dejavuRegular7Dark;
    }

    public static Font getDejavuBold7Dark() {
        return dejavuBold7Dark;
    }

    public static Font getDejavuBold8Dark() {
        return dejavuBold8Dark;
    }

    public static Font getDejavuBold14Dark() {
        return dejavuBold14Dark;
    }

    public static BaseFont getDejavu() {
        return dejavu;
    }

    public static Font getDejavuBold140Dark() {
        return dejavuBold140Dark;
    }

}
