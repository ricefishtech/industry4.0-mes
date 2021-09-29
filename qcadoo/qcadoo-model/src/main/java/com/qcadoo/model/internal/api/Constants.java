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
package com.qcadoo.model.internal.api;

import org.springframework.core.io.support.ResourcePatternResolver;

public final class Constants {

    private Constants() {
    }

    public static final String XSL = "com/qcadoo/model/model.xsl";

    public static final String XSL_ORACLE_10G = "com/qcadoo/model/model-ora10g.xsl";

    public static final String XSD = "com/qcadoo/model/model.xsd";

    public static final String RESOURCE_PATTERN = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "model/*.xml";

    public static final String VALIDATION_MESSAGE_REQUIRED = "required";

    public static final String VALIDATION_MESSAGE_BELOW_RANGE = "below range";

    public static final String VALIDATION_MESSAGE_ABOVE_RANGE = "above range";

    public static final String VALIDATION_MESSAGE_INVALID_LENGTH = "invalid length";

    public static final String VALIDATION_MESSAGE_BELOW_MIN_LENGTH = "below min length";

    public static final String VALIDATION_MESSAGE_ABOVE_MAX_LENGTH = "above max length";

    public static final String VALIDATION_MESSAGE_INVALID_PRECISION = "invalid precision";

    public static final String VALIDATION_MESSAGE_BELOW_MIN_PRECISION = "below min presicion";

    public static final String VALIDATION_MESSAGE_ABOVE_MAX_PRECISION = "above max precision";

    public static final String VALIDATION_MESSAGE_INVALID_SCALE = "invalid scale";

    public static final String VALIDATION_MESSAGE_BELOW_MIN_SCALE = "below min scale";

    public static final String VALIDATION_MESSAGE_ABOVE_MAX_SCALE = "above max scale";

}
