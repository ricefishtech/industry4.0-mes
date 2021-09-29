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
package com.qcadoo.report.api.pdf.layout;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Lists;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.qcadoo.commons.functional.BiFunction;

/**
 * Layout for placing document elements one under another (sometimes this kind of layout is called 'flow layout').
 * 
 * @since 1.3
 */
public class VerticalLayout {

    private final List<Element> contents = Lists.newLinkedList();

    /**
     * Useful when you need to fold few VerticalLayouts into one.
     */
    public static final BiFunction<VerticalLayout, VerticalLayout, VerticalLayout> REDUCE_BY_MERGE = new BiFunction<VerticalLayout, VerticalLayout, VerticalLayout>() {

        @Override
        public VerticalLayout apply(final VerticalLayout acc, final VerticalLayout element) {
            return acc.merge(element);
        }
    };

    /**
     * Create new instance VerticalLayout
     * 
     * @return new instance of VerticalLayout.
     */
    public static VerticalLayout create() {
        return VerticalLayout.empty();
    }

    /**
     * Create empty VerticalLayout
     * 
     * @return empty VerticalLayout.
     */
    public static VerticalLayout empty() {
        return new VerticalLayout();
    }

    private VerticalLayout() {

    }

    /**
     * Append an element to this layout.
     * 
     * @param element
     *            element to be appended
     * @return reference to this layout.
     */
    public VerticalLayout append(final Element element) {
        contents.add(element);
        return this;
    }

    /**
     * Merge this layout with given one.
     * 
     * @param verticalLayout
     *            layout to be merged
     * @return reference to this layout.
     */
    public VerticalLayout merge(final VerticalLayout verticalLayout) {
        contents.addAll(verticalLayout.contents);
        return this;
    }

    /**
     * Print this layout within given document.
     * 
     * @param document
     *            document to be printed into.
     * @throws DocumentException
     *             if the document isn't opened yet or is already closed.
     */
    public void appendToDocument(final Document document) throws DocumentException {
        for (Element content : contents) {
            document.add(content);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        VerticalLayout rhs = (VerticalLayout) obj;
        return ObjectUtils.equals(this.contents, rhs.contents);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(contents);
    }
}
