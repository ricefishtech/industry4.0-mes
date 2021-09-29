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
package com.qcadoo.view.internal.xml;

import org.w3c.dom.Node;

public class ViewExtension {

    private final String pluginName;

    private final String viewName;

    private final Node extesionNode;

    public ViewExtension(final String pluginName, final String viewName, final Node extesionNode) {
        super();
        this.pluginName = pluginName;
        this.viewName = viewName;
        this.extesionNode = extesionNode;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getViewName() {
        return viewName;
    }

    public Node getExtesionNode() {
        return extesionNode;
    }

}
