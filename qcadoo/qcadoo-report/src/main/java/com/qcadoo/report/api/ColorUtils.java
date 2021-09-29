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

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for pdf colors management.
 * 
 */
public final class ColorUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ColorUtils.class);

    private static Color lineLightColor;

    private static Color lineDarkColor;

    private static Color backgroundColor;

    private static Color lightColor;

    private static Color darkColor;

    private ColorUtils() {
    }

    /**
     * Prepare colors.
     */
    public static synchronized void prepare() {
        if (backgroundColor == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Pdf colors initialization");
            }
            lightColor = new Color(77, 77, 77);
            darkColor = new Color(26, 26, 26);
            lineDarkColor = new Color(102, 102, 102);
            lineLightColor = new Color(153, 153, 153);
            backgroundColor = new Color(230, 230, 230);
        }
    }

    public static Color getLineDarkColor() {
        return lineDarkColor;
    }

    public static Color getLineLightColor() {
        return lineLightColor;
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static Color getLightColor() {
        return lightColor;
    }

    public static Color getDarkColor() {
        return darkColor;
    }
}
