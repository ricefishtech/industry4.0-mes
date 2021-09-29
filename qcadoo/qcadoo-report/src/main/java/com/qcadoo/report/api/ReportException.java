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

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class ReportException extends Exception {

    public static enum Type {
        JSON_EXCEPTION("jsonException"), WRONG_REPORT_TYPE("wrongType"), ERROR_WHILE_COPYING_REPORT_TO_RESPONSE(
                "errorWhileCopyingToResponse"), NO_TEMPLATE_FOUND("noTemplateFound"), GENERATE_REPORT_EXCEPTION(
                "generateReportException");

        private String code;

        Type(final String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    private final Type type;

    private final String[] args;

    private static final long serialVersionUID = -4187117804067638882L;

    public ReportException(final Type type, final String... args) {
        this(type, null, args);
    }

    public ReportException(final Type type, final Throwable cause, final String... args) {
        super(ReportException.generateMessage(type, args), cause);
        this.type = type;
        if (ArrayUtils.isEmpty(args)) {
            this.args = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            this.args = args;
        }
    }

    private static String generateMessage(final Type type, final String... args) {
        if (args.length == 0) {
            return type.getCode();
        } else {
            return type.getCode() + ": " + Arrays.toString(args);
        }
    }

    public String getCode() {
        return type.getCode();
    }

    public String[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }
}
