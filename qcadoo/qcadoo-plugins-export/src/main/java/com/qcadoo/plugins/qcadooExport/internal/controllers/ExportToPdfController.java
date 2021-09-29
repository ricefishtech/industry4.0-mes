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
package com.qcadoo.plugins.qcadooExport.internal.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.aop.Monitorable;
import com.qcadoo.model.api.file.FileService;
import com.qcadoo.plugins.qcadooExport.api.ExportToPdfColumns;
import com.qcadoo.plugins.qcadooExport.api.helpers.ExportToFileColumnsHelper;
import com.qcadoo.report.api.FontUtils;
import com.qcadoo.report.api.FooterResolver;
import com.qcadoo.report.api.pdf.PdfHelper;
import com.qcadoo.report.api.pdf.PdfPageNumbering;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.GridComponent;
import com.qcadoo.view.api.crud.CrudService;

@Controller
public class ExportToPdfController {

    private static final String L_GRID = "grid";

    private static final String L_VIEW_NAME_VARIABLE = "viewName";

    private static final String L_PLUGIN_IDENTIFIER_VARIABLE = "pluginIdentifier";

    private static final String L_CONTROLLER_PATH = "exportToPdf/{" + L_PLUGIN_IDENTIFIER_VARIABLE + "}/{" + L_VIEW_NAME_VARIABLE
            + "}";

    @Autowired
    private CrudService crudService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SecurityRolesService securityRolesService;

    @Autowired
    private PdfHelper pdfHelper;

    @Autowired
    private FooterResolver footerResolver;

    @Autowired
	private ExportToFileColumnsHelper<ExportToPdfColumns> exportToFileColumnsHelper;

    @Monitorable(threshold = 500)
    @ResponseBody
    @RequestMapping(value = { L_CONTROLLER_PATH }, method = RequestMethod.POST)
    public Object generatePdf(@PathVariable(L_PLUGIN_IDENTIFIER_VARIABLE) final String pluginIdentifier,
            @PathVariable(L_VIEW_NAME_VARIABLE) final String viewName, @RequestBody final JSONObject body, final Locale locale) {
        try {
            changeMaxResults(body);

            ViewDefinitionState state = crudService.invokeEvent(pluginIdentifier, viewName, body, locale);

            GridComponent grid = (GridComponent) state.getComponentByReference(L_GRID);

            Document document = new Document(PageSize.A4.rotate());
            String date = DateFormat.getDateInstance().format(new Date());
            File file = fileService.createExportFile("export_" + grid.getName() + "_" + date + ".pdf");

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fileOutputStream);

            pdfWriter.setPageEvent(new PdfPageNumbering(footerResolver.resolveFooter(locale)));

            document.setMargins(40, 40, 60, 60);

            document.addTitle("export.pdf");
            pdfHelper.addMetaData(document);
            pdfWriter.createXmpMetadata();
            document.open();

            String title = translationService
                    .translate(pluginIdentifier + "." + viewName + ".window.mainTab." + grid.getName() + ".header", locale);

            Date generationDate = new Date();

            pdfHelper.addDocumentHeader(document, "", title,
                    translationService.translate("qcadooReport.commons.generatedBy.label", locale), generationDate);

            List<String> columns = getColumns(grid);
            List<String> columnNames = getColumnNames(grid, columns);

            PdfPTable pdfTable = pdfHelper.createTableWithHeader(columnNames.size(), columnNames, false);

            List<Map<String, String>> rows;

            if (grid.getSelectedEntitiesIds().isEmpty()) {
                rows = grid.getColumnValuesOfAllRecords();
            } else {
                rows = grid.getColumnValuesOfSelectedRecords();
            }

            addPdfTableCells(pdfTable, rows, columns, viewName);

            document.add(pdfTable);
            document.close();

            state.redirectTo(fileService.getUrl(file.getAbsolutePath()) + "?clean", true, false);

            return crudService.renderView(state);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (DocumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private List<String> getColumns(final GridComponent grid) {
        return exportToFileColumnsHelper.getColumns(grid, ExportToPdfColumns.class);
    }

    private List<String> getColumnNames(final GridComponent grid, final List<String> columns) {
        List<String> columnNames = Lists.newLinkedList();

        columns.forEach(column -> {
            String columnName = grid.getColumnNames().get(column);

            if (!Strings.isNullOrEmpty(columnName)) {
                columnNames.add(columnName);
            }
        });

        return columnNames;
    }

    private void addPdfTableCells(final PdfPTable pdfTable, final List<Map<String, String>> rows, final List<String> columns,
            final String viewName) {
        rows.forEach(row -> {
            columns.forEach(column -> {
                pdfTable.addCell(new Phrase(row.get(column), FontUtils.getDejavuRegular7Dark()));
            });
        });
    }

    private void changeMaxResults(final JSONObject json) throws JSONException {
        JSONObject component = getComponent(json, getComponentName(json));

        component.getJSONObject("content").put("firstEntity", 0);
        component.getJSONObject("content").put("maxEntities", Integer.MAX_VALUE);
    }

    private JSONObject getComponent(final JSONObject json, final String componentName) throws JSONException {
        String[] path = componentName.split("\\.");

        JSONObject component = json;

        for (String p : path) {
            component = component.getJSONObject("components").getJSONObject(p);
        }

        return component;
    }

    private String getComponentName(final JSONObject body) throws JSONException {
        return body.getJSONObject("event").getString("component");
    }

}
