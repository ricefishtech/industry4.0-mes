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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.qcadoo.model.api.aop.Monitorable;
import com.qcadoo.model.api.file.FileService;
import com.qcadoo.plugins.qcadooExport.api.ExportToCsvColumns;
import com.qcadoo.plugins.qcadooExport.api.helpers.ExportToFileColumnsHelper;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.GridComponent;
import com.qcadoo.view.api.crud.CrudService;

@Controller
public class ExportToCsvController {

    private static final String L_GRID = "grid";

    private static final String L_VIEW_NAME_VARIABLE = "viewName";

    private static final String L_PLUGIN_IDENTIFIER_VARIABLE = "pluginIdentifier";

    private static final String L_CONTROLLER_PATH = "exportToCsv/{" + L_PLUGIN_IDENTIFIER_VARIABLE + "}/{" + L_VIEW_NAME_VARIABLE
            + "}";

    @Value("${exportedCsvSeparator:','}")
    private String exportedCsvSeparator;

    @Autowired
    private FileService fileService;

    @Autowired
    private CrudService crudService;

    @Autowired
    private ExportToFileColumnsHelper<ExportToCsvColumns> exportToFileColumnsHelper;

    @Monitorable(threshold = 500)
    @ResponseBody
    @RequestMapping(value = { L_CONTROLLER_PATH }, method = RequestMethod.POST)
    public Object generateCsv(@PathVariable(L_PLUGIN_IDENTIFIER_VARIABLE) final String pluginIdentifier,
            @PathVariable(L_VIEW_NAME_VARIABLE) final String viewName, @RequestBody final JSONObject body, final Locale locale) {
        try {
            changeMaxResults(body);

            ViewDefinitionState state = crudService.invokeEvent(pluginIdentifier, viewName, body, locale);

            GridComponent grid = (GridComponent) state.getComponentByReference(L_GRID);

            String date = DateFormat.getDateInstance().format(new Date());
            File file = fileService.createExportFile("export_" + grid.getName() + "_" + date + ".csv");

            BufferedWriter bufferedWriter = null;

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                fileOutputStream.write(239);
                fileOutputStream.write(187);
                fileOutputStream.write(191);

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, Charset.forName("UTF-8")));

                boolean firstName = true;

                List<String> columns = getColumns(grid);
                List<String> columnNames = getColumnNames(grid, columns);

                for (String name : columnNames) {
                    if (firstName) {
                        firstName = false;
                    } else {
                        bufferedWriter.append(exportedCsvSeparator);
                    }

                    bufferedWriter.append("\"").append(normalizeString(name)).append("\"");
                }

                bufferedWriter.append("\n");

                List<Map<String, String>> rows;

                if (grid.getSelectedEntitiesIds().isEmpty()) {
                    rows = grid.getColumnValuesOfAllRecords();
                } else {
                    rows = grid.getColumnValuesOfSelectedRecords();
                }

                addCsvTableCells(bufferedWriter, rows, columns);

                bufferedWriter.flush();
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(bufferedWriter);
            }

            state.redirectTo(fileService.getUrl(file.getAbsolutePath()) + "?clean", true, false);

            return crudService.renderView(state);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private List<String> getColumns(final GridComponent grid) {
        return exportToFileColumnsHelper.getColumns(grid, ExportToCsvColumns.class);
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

    private void addCsvTableCells(final BufferedWriter bufferedWriter, final List<Map<String, String>> rows,
            final List<String> columns) throws IOException {
        for (Map<String, String> row : rows) {
            boolean firstValue = true;

            for (String column : columns) {
                if (firstValue) {
                    firstValue = false;
                } else {
                    bufferedWriter.append(exportedCsvSeparator);
                }

                bufferedWriter.append("\"").append(normalizeString(row.get(column))).append("\"");
            }

            bufferedWriter.append("\n");
        }
    }

    private String normalizeString(final String string) {
        if (StringUtils.hasText(string)) {
            return string.replaceAll("\"", "\\\"").replaceAll("\n", " ");
        } else {
            return "";
        }
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
