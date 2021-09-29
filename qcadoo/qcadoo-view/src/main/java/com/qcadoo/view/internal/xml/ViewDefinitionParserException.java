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

public final class ViewDefinitionParserException extends RuntimeException {

    private static final long serialVersionUID = -7437167697213640121L;

    private final String fileName;

    private final String node;

    public static ViewDefinitionParserException forFile(final String fileName, final Throwable cause) {
        return new ViewDefinitionParserException(fileName, cause.getMessage(), cause, null);
    }

    public static ViewDefinitionParserException forFile(final String fileName, final String message, final Throwable cause) {
        return new ViewDefinitionParserException(fileName, message, cause, null);
    }

    public static ViewDefinitionParserException forFileAndNode(final String fileName,
            final ViewDefinitionParserNodeException cause) {
        return new ViewDefinitionParserException(fileName, cause.getOriginalMessage(), cause, cause.getNode());
    }

    private ViewDefinitionParserException(final String fileName, final String message, final Throwable cause, final String node) {
        super(message, cause);
        this.fileName = fileName;
        this.node = node;
    }

    @Override
    public String getMessage() {
        if (node == null) {
            return "Error while parsing view file '" + fileName + "': " + super.getMessage();
        } else {
            return "Error while parsing view file '" + fileName + "' in node " + node + ": " + super.getMessage();
        }
    }
}
