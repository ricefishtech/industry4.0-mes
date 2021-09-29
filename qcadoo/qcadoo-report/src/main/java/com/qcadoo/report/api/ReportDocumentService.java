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
import java.util.Locale;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.qcadoo.model.api.Entity;

/**
 * Service for creating report documents.
 * 
 * @since 0.4.1
 * 
 */
public interface ReportDocumentService {

    /**
     * Generate report document
     * 
     * @param entity
     * @param locale
     * @throws IOException
     * @throws DocumentException
     */
    void generateDocument(final Entity entity, final Locale locale) throws IOException, DocumentException;

    /**
     * Generate report document
     * 
     * @param entity
     * @param locale
     * @param pageSize
     * @throws IOException
     * @throws DocumentException
     */
    void generateDocument(final Entity entity, final Locale locale, final Rectangle pageSize) throws IOException,
            DocumentException;

    /**
     * Get report title
     * 
     * @param locale
     * @return report title
     */
    String getReportTitle(final Locale locale);

}
