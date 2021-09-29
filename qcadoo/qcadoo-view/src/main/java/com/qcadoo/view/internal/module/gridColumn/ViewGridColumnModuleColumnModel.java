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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.qcadoo.view.constants.Alignment;

public class ViewGridColumnModuleColumnModel {

    private final String name;

    private final String fields;

    private String expression;

    private Integer width;

    private boolean link = false;

    private boolean searchable = false;

    private boolean multiSearch = false;

    private boolean orderable = false;

    private boolean hidden = false;

    private Alignment align;

    private String classesNames;

    private String classesCondition;

    public ViewGridColumnModuleColumnModel(final String name, final String fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(final Integer width) {
        this.width = width;
    }

    public boolean getLink() {
        return link;
    }

    public void setLink(final boolean link) {
        this.link = link;
    }

    public boolean getSearchable() {
        return searchable;
    }

    public void setSearchable(final boolean searchable) {
        this.searchable = searchable;
    }

    public boolean getMultiSearch() {
        return this.multiSearch;
    }

    public void setMultiSearch(final boolean multiSearch) {
        this.multiSearch = multiSearch;
    }

    public boolean getOrderable() {
        return orderable;
    }

    public void setOrderable(final Boolean orderable) {
        this.orderable = orderable;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getName() {
        return name;
    }

    public String getFields() {
        return fields;
    }

    public void setAlign(final Alignment align) {
        this.align = align;
    }

    public Alignment getAlign() {
        return this.align;
    }

    public String getClassesNames() {
        return classesNames;
    }

    public void setClassesNames(final String classesNames) {
        this.classesNames = classesNames;
    }

    public String getClassesCondition() {
        return classesCondition;
    }

    public void setClassesCondition(final String classesCondition) {
        this.classesCondition = classesCondition;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getFields()).append(getLink()).append(getWidth())
                .append(getExpression()).append(getAlign()).append(getOrderable()).append(getHidden()).append(getSearchable())
                .append(getMultiSearch()).append(getClassesNames()).append(getClassesCondition()).toHashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ViewGridColumnModuleColumnModel that = (ViewGridColumnModuleColumnModel) o;
        return new EqualsBuilder().append(this.getName(), that.getName()).append(this.getFields(), that.getFields())
                .append(this.getLink(), that.getLink()).append(this.getWidth(), that.getWidth())
                .append(this.getExpression(), that.getExpression()).append(this.getAlign(), that.getAlign())
                .append(this.getOrderable(), that.getOrderable()).append(this.getHidden(), that.getHidden())
                .append(this.getMultiSearch(), that.getMultiSearch()).append(this.getSearchable(), that.getSearchable())
                .append(this.getClassesNames(), that.getClassesNames()).append(this.getClassesCondition(), that.getClassesCondition())
                .isEquals();
    }
}
