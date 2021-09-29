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
package com.qcadoo.plugin.api;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * Basic module exception
 * 
 * @since 0.4.0
 */
public class ModuleException extends RuntimeException {

    private static final long serialVersionUID = -5382056547567036558L;

    public ModuleException(final String pluginIdentifier, final String moduleType, final Throwable cause) {
        super(ModuleException.createMessage(pluginIdentifier, moduleType, null, cause, null), cause);
    }

    public ModuleException(final String pluginIdentifier, final String moduleType, final String message) {
        super(ModuleException.createMessage(pluginIdentifier, moduleType, null, null, message));
    }

    public ModuleException(final String pluginIdentifier, final String moduleType, final Element element, final Throwable cause) {
        super(ModuleException.createMessage(pluginIdentifier, moduleType, element, cause, null), cause);
    }

    private static String createMessage(final String pluginIdentifier, final String moduleType, final Element element,
            final Throwable cause, final String message) {
        StringBuilder builder = new StringBuilder("[PLUGIN: ");
        builder.append(pluginIdentifier);
        builder.append(", ");
        if (element == null) {
            builder.append("MODULE: ");
            builder.append(moduleType);
        } else {
            builder.append("ELEMENT: ");
            XMLOutputter outputter = new XMLOutputter();
            builder.append(outputter.outputString(element));
        }
        builder.append("] ");
        if (cause == null) {
            builder.append(message);
        } else {
            builder.append(cause.getMessage());
        }
        return builder.toString();
    }
}
