/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.3
 * <p>
 * This file is part of Qcadoo.
 * <p>
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.view.internal.components.layout;

import com.google.common.base.Preconditions;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Locale;
import java.util.Map;

public class FlowGridLayoutPattern extends AbstractLayoutPattern {

    private static final String JS_OBJECT = "QCD.components.containers.layout.FlowGridLayout";

    private static final String JSP_PATH = "containers/layout/flowGridLayout.jsp";

    private static final String JS_PATH = "/qcadooView/public/js/crud/qcd/components/containers/layout/flowGridLayout.js";

    private FlowGridLayoutCell[][] cells;

    public FlowGridLayoutPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    public void parse(final Node componentNode, final ViewDefinitionParser parser) throws ViewDefinitionParserNodeException {
        super.parse(componentNode, parser);

        Integer columns = getIntAttribute(componentNode, "columns", parser);
        Integer rows = getIntAttribute(componentNode, "rows", parser);

        parser.checkState(columns != null, componentNode, "columns not definied");
        parser.checkState(rows != null, componentNode, "rows not definied");

        // check again - because sonar is stupid
        if (rows == null) {
            throw new IllegalStateException("rows not definied");
        }

        cells = new FlowGridLayoutCell[rows][];
        for (int row = 0; row < cells.length; row++) {
            cells[row] = new FlowGridLayoutCell[columns];
            for (int col = 0; col < cells[row].length; col++) {
                cells[row][col] = new FlowGridLayoutCell();
            }
        }

        NodeList childNodes = componentNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            parser.checkState("layoutElement".equals(child.getNodeName()), child, "flowGridlayout can contains only layoutElements");
            Integer column = getIntAttribute(child, "column", parser);
            Integer row = getIntAttribute(child, "row", parser);

            FlowGridLayoutCell cell = createFlowGridLayoutCell(child, parser);

            try {
                insertCell(cell, column, row);
            } catch (IllegalStateException e) {
                throw new ViewDefinitionParserNodeException(child, e);
            }
        }
    }

    private FlowGridLayoutCell createFlowGridLayoutCell(final Node child, final ViewDefinitionParser parser) throws ViewDefinitionParserNodeException {
        Integer colspan = getIntAttribute(child, "width", parser);
        Integer rowspan = getIntAttribute(child, "height", parser);
        Integer minHeight = null;
        if (rowspan == null) {
            rowspan = minHeight = getIntAttribute(child, "minHeight", parser);
            if (minHeight == null) {
                rowspan = 1;                
            }
        }

        ComponentPattern elementComponent = null;
        NodeList elementComponentNodes = child.getChildNodes();
        FlowGridLayoutCell cell = new FlowGridLayoutCell();
        for (int elementComponentNodesIter = 0; elementComponentNodesIter < elementComponentNodes.getLength(); elementComponentNodesIter++) {
            Node elementComponentNode = elementComponentNodes.item(elementComponentNodesIter);
            if (elementComponentNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            parser.checkState("component".equals(elementComponentNode.getNodeName()), elementComponentNode, "layoutElement can contains only components");
            elementComponent = parser.parseComponent(elementComponentNode, this);
            this.addChild(elementComponent);
            cell.addComponent(elementComponent);
        }
        if (colspan != null) {
            cell.setColspan(colspan);
        }
        if (rowspan != null) {
            cell.setRowspan(rowspan);
        }
        cell.setMinHeight(minHeight);

        return cell;
    }

    private void insertCell(final FlowGridLayoutCell cell, final int column, final int row) {
        Preconditions.checkState(column > 0, "column number less than zero");
        Preconditions.checkState(row > 0, "row number less than zero");
        Preconditions.checkState(column <= cells[0].length, "column number to large");
        Preconditions.checkState(row <= cells.length, "row number to large");
        Preconditions.checkState(column + cell.getColspan() - 1 <= cells[0].length, "width number to large");
        Preconditions.checkState(row + cell.getRowspan() - 1 <= cells.length, "height number to large");

        for (int rowIter = row; rowIter < row + cell.getRowspan(); rowIter++) {
            for (int colIter = column; colIter < column + cell.getColspan(); colIter++) {
                FlowGridLayoutCell beforeCell = cells[rowIter - 1][colIter - 1];
                Preconditions.checkState(beforeCell.isAvailable(), "cell [" + rowIter + "x" + colIter + "] is not available");
                beforeCell.setAvailable(false);
            }
        }

        cells[row - 1][column - 1] = cell;
    }

    private Integer getIntAttribute(final Node node, final String attribute, final ViewDefinitionParser parser)
            throws ViewDefinitionParserNodeException {
        String valueStr = parser.getStringAttribute(node, attribute);
        if (valueStr == null) {
            return null;
        }
        try {
            return Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            throw new ViewDefinitionParserNodeException(node, "value of attribute '" + attribute + "' is not a number");
        }
    }

    @Override
    public final Map<String, Object> prepareView(final Locale locale) {
        Map<String, Object> model = super.prepareView(locale);
        model.put("cells", cells);
        return model;
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("colsNumber", cells[0].length);
        return json;
    }

    @Override
    public String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    public String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    public String getJsObjectName() {
        return JS_OBJECT;
    }

    public FlowGridLayoutCell[][] getCells() {
        return cells;
    }
}
