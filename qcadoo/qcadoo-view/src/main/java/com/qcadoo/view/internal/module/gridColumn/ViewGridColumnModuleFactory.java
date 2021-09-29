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
package com.qcadoo.view.internal.module.gridColumn;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.qcadoo.plugin.api.ModuleFactory;
import com.qcadoo.view.constants.Alignment;
import com.qcadoo.view.internal.api.InternalViewDefinitionService;

public class ViewGridColumnModuleFactory extends ModuleFactory<ViewGridColumnModule> {

    @Autowired
    private InternalViewDefinitionService viewDefinitionService;

    @Override
    protected ViewGridColumnModule parseElement(final String pluginIdentifier, final Element element) {

        String plugin = getRequiredAttribute(element, "plugin");
        String view = getRequiredAttribute(element, "view");
        String component = getRequiredAttribute(element, "component");
        List<ViewGridColumnModuleColumnModel> columns = new LinkedList<>();
        @SuppressWarnings("unchecked")
        List<Element> children = (List<Element>) element.getChildren();

        for (Element columnElement : children) {
            String columnName = getRequiredAttribute(columnElement, "name");
            String columnFields = getRequiredAttribute(columnElement, "fields");
            String columnExpression = getAttribute(columnElement, "expression");
            String columnLink = getAttribute(columnElement, "link");
            String columnWidth = getAttribute(columnElement, "width");
            String columnSearchable = getAttribute(columnElement, "searchable");
            String columnMultiSearch = getAttribute(columnElement, "multiSearch");
            String columnOrderable = getAttribute(columnElement, "orderable");
            String columnHidden = getAttribute(columnElement, "hidden");
            String columnAlign = getAttribute(columnElement, "align");
            String columnClassesNames = getAttribute(columnElement, "classesNames");
            String columnClassesCondition = getAttribute(columnElement, "classesCondition");

            ViewGridColumnModuleColumnModel columnModel = new ViewGridColumnModuleColumnModel(columnName, columnFields);
            columnModel.setExpression(columnExpression);
            if (columnLink != null) {
                columnModel.setLink(Boolean.parseBoolean(columnLink));
            }
            if (columnWidth != null) {
                columnModel.setWidth(Integer.parseInt(columnWidth));
            }
            if (columnSearchable != null) {
                columnModel.setSearchable(Boolean.parseBoolean(columnSearchable));
            }
            if (columnMultiSearch != null) {
                columnModel.setMultiSearch(Boolean.parseBoolean(columnMultiSearch));
            }
            if (columnOrderable != null) {
                columnModel.setOrderable(Boolean.parseBoolean(columnOrderable));
            }
            if (columnHidden != null) {
                columnModel.setHidden(Boolean.parseBoolean(columnHidden));
            }
            if (StringUtils.isNotEmpty(columnAlign)) {
                columnModel.setAlign(Alignment.parseString(columnAlign));
            }
            columnModel.setClassesNames(columnClassesNames);
            columnModel.setClassesCondition(columnClassesCondition);

            columns.add(columnModel);
        }

        return new ViewGridColumnModule(pluginIdentifier, plugin, view, component, columns, viewDefinitionService);
    }

    @Override
    public String getIdentifier() {
        return "view-grid-column";
    }

}
