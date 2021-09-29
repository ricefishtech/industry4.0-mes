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
package com.qcadoo.model.internal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public final class JdomUtils {

    private JdomUtils() {
    }

    @SuppressWarnings("unchecked")
    public static Element replaceNamespace(final Element element, final Namespace namespace) {
        element.setNamespace(namespace);

        for (Element child : (List<Element>) element.getChildren()) {
            replaceNamespace(child, namespace);
        }

        return element;
    }

    public static byte[] documentToByteArray(final Document document) {
        try {
            XMLOutputter outputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            outputter.output(document, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static Document inputStreamToDocument(final InputStream stream) {
        try {
            SAXBuilder builder = new SAXBuilder();
            return builder.build(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (JDOMException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
