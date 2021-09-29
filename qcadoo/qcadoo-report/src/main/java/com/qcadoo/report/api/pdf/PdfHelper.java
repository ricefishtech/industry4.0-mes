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
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Helper for PDF.
 * 
 * @since 1.1.3
 * 
 */
public interface PdfHelper {

    /**
     * Add header to the document
     * 
     * @param document
     * @param name
     *            document name
     * @param documenTitle
     * @param documentAuthor
     * @param date
     *            actual date
     * @param username
     *            name of user generating document
     * @throws DocumentException
     */
    void addDocumentHeader(final Document document, final String name, final String documenTitle, final String documentAuthor,
            final Date date, final String username) throws DocumentException;

    /**
     * Add header to the document
     * 
     * @param document
     * @param name
     *            document name
     * @param documenTitle
     * @param documentAuthor
     * @param date
     *            actual date
     * @throws DocumentException
     */
    void addDocumentHeader(final Document document, final String name, final String documenTitle, final String documentAuthor,
            final Date date) throws DocumentException;

    /**
     * Add metadata to given document.
     * 
     * @param document
     */
    void addMetaData(final Document document);

    /**
     * Create panel table with given quantity of columns.
     * 
     * @param column
     *            quantity of columns
     * @return panel table
     */
    PdfPTable createPanelTable(final int column);

    /**
     * Create panel table with given quantity of columns and with simple format without border.
     * 
     * @param column
     *            quantity of columns
     * @return panel table
     */
    PdfPTable createPanelTableWithSimpleFormat(final int column);

    /**
     * Add cell with table to current table.
     * 
     * @param table
     * @param label
     *            header label
     * @param fieldValue
     *            value
     * @param headerFont
     * @param valueFont
     * @param columns
     *            quantity of columns
     */
    void addTableCellAsTable(final PdfPTable table, final String label, final Object fieldValue, final Font headerFont,
            final Font valueFont, final int columns);

    /**
     * Add cell with table to current table.
     * 
     * @param table
     * @param label
     *            header label
     * @param fieldValue
     *            value
     * @param nullValue
     *            string to display in case the value is null
     * @param headerFont
     * @param valueFont
     * @param columns
     *            quantity of columns
     */
    void addTableCellAsTable(final PdfPTable table, final String label, final Object fieldValue, final String nullValue,
            final Font headerFont, final Font valueFont, final int columns);

    /**
     * Add cell with two columns table to current table.
     * 
     * @param table
     * @param label
     *            header label
     * @param fieldValue
     *            value
     */
    void addTableCellAsTwoColumnsTable(final PdfPTable table, final String label, final Object fieldValue);

    /**
     * Add cell with two columns table to current table with given widths
     *
     * @param table
     * @param label
     *            header label
     * @param fieldValue
     *            value
     * @param columnWidths
     *            widths
     */
    void addTableCellAsTwoColumnsTable(final PdfPTable table, final String label, final Object fieldValue, final int[] columnWidths);

    /**
     * Add cell with one columns table to current table.
     *
     * @param table
     * @param label
     *            header label
     * @param fieldValue
     *            value
     */
    void addTableCellAsOneColumnTable(final PdfPTable table, final String label, final Object fieldValue);

    /**
     * Add cell with one columns table to current table.
     *
     * @param table
     * @param label
     *            header label
     * @param fieldValue
     *            value
     * @param boldAndBigger
     *            boldAndBigger
     */
    void addTableCellAsOneColumnTable(final PdfPTable table, final String label, final Object fieldValue, final boolean boldAndBigger);

    /**
     * Add image from file name to given document.
     * 
     * @param document
     * @param fileName
     */
    void addImage(final Document document, final String fileName);

    /**
     * Create new table with header and given column widths.
     * 
     * @param numOfColumns
     * @param header
     *            header labels
     * @param lastColumnAlignmentToLeft
     * @param columnWidths
     *            relative column widths
     * @param headerAlignment
     * @return table
     */
    PdfPTable createTableWithHeader(final int numOfColumns, final List<String> header, final boolean lastColumnAlignmentToLeft,
            final int[] columnWidths, final HeaderAlignment headerAlignment);

    /**
     * Create new table with header.
     * 
     * @param numOfColumns
     * @param header
     *            header labels
     * @param lastColumnAlignmentToLeft
     * @param headerAlignment
     * @return table
     */
    PdfPTable createTableWithHeader(final int numOfColumns, final List<String> header, final boolean lastColumnAlignmentToLeft,
            final HeaderAlignment headerAlignment);

    /**
     * Create new table with header and given column widths.
     * 
     * @param numOfColumns
     * @param header
     *            header labels
     * @param lastColumnAlignmentToLeft
     * @param columnWidths
     *            relative column widths
     * @return table
     */
    PdfPTable createTableWithHeader(final int numOfColumns, final List<String> header, final boolean lastColumnAlignmentToLeft,
            final int[] columnWidths);

    /**
     * Create new table with header.
     * 
     * @param numOfColumns
     * @param header
     *            header labels
     * @param lastColumnAlignmentToLeft
     * @return table
     */
    PdfPTable createTableWithHeader(final int numOfColumns, final List<String> header, final boolean lastColumnAlignmentToLeft);

    /**
     * Add cell to current dynamin header table with exists value or empty.
     * 
     * @param headerTable
     * @param column
     * @param locale
     * @return PdfPTable with added column
     */
    PdfPTable addDynamicHeaderTableCell(final PdfPTable headerTable, final Map<String, Object> column, final Locale locale);

    /**
     * Return max size of columns rows
     * 
     * @param columnsListSize
     * @return
     */
    int getMaxSizeOfColumnsRows(final List<Integer> columnsListSize);

    /**
     * Return column sizes
     * 
     * @param availableWidth
     * @param fixedColumns
     * @param allColumns
     *            column
     * @return
     */
    int[] getReportColumnWidths(final Integer availableWidth, final Map<String, Integer> fixedColumns,
            final List<String> allColumns);

    /**
     * Return document author
     * 
     * @return document author
     */
    String getDocumentAuthor();

    void addDocumentHeaderThin(final Document document, final String name, final String documentTitle,
            final String documentAuthor, final Date date) throws DocumentException;

    PdfPTable addDynamicHeaderTableCellOneRow(PdfPTable firstColumnHeaderTable, Map<String, Object> firstColumn, Locale locale);

    // FIXME dev_team: What if table has many columns with the same label?
    PdfPTable createTableWithHeader(final int numOfColumns, final List<String> header, final boolean lastColumnAlignmentToLeft,
            final int[] columnWidths, final Map<String, HeaderAlignment> alignments);

    PdfPTable createTableWithHeader(final int numOfColumns, List<String> header, final boolean lastColumnAlignmentToLeft,
            final Map<String, HeaderAlignment> alignments);

    boolean validateReportColumnWidths(final Integer availableWidth, final Map<String, Integer> fixedColumns,
            final List<String> allColumns);
}
