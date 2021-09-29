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
package com.qcadoo.report.internal.controller;

import com.qcadoo.report.api.ReportException;
import com.qcadoo.view.api.exception.ExceptionInfo;
import com.qcadoo.view.api.exception.ExceptionInfoResolver;

public class ReportExceptionInfoResolver implements ExceptionInfoResolver<ReportException> {

    @Override
    public ExceptionInfo getExceptionInfo(final ReportException e) {

        String header = "qcadooReport.errorMessage." + e.getCode() + ".header";
        String message = "qcadooReport.errorMessage." + e.getCode() + ".explanation";

        return new ExceptionInfo(header, message, e.getArgs());
    }
}
