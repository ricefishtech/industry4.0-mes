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
package com.qcadoo.model.internal.validators;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchProjections;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.internal.api.ErrorMessageDefinition;
import com.qcadoo.model.internal.api.FieldHookDefinition;

public final class UniqueValidator implements FieldHookDefinition, ErrorMessageDefinition {

    private static final String UNIQUE_ERROR = "qcadooView.validate.field.error.duplicated";

    private String errorMessage = UNIQUE_ERROR;

    private DataDefinition dataDefinition;

    private FieldDefinition fieldDefinition;

    @Override
    public void initialize(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition) {
        this.dataDefinition = dataDefinition;
        this.fieldDefinition = fieldDefinition;
    }

    @Override
    public boolean call(final Entity entity, final Object oldValue, final Object newValue) {
        if (entity.getField(fieldDefinition.getName()) == null) {
            return true;
        }
        SearchCriteriaBuilder searchCriteriaBuilder = dataDefinition.find().add(uniqueCriterionFor(entity)).setMaxResults(1);
        if (entity.getId() != null) {
            searchCriteriaBuilder.add(SearchRestrictions.idNe(entity.getId()));
        }
        SearchResult results = searchCriteriaBuilder.list();

        if (results.getTotalNumberOfEntities() == 0) {
            return true;
        } else {
            entity.addError(fieldDefinition, errorMessage);
            return false;
        }
    }

    private SearchCriterion uniqueCriterionFor(final Entity entity) {
        if (Objects.equals(String.class, fieldDefinition.getType().getType())) {
            return SearchRestrictions.iEq(fieldDefinition.getName(), entity.getStringField(fieldDefinition.getName()).trim());
        }
        return SearchRestrictions.eq(fieldDefinition.getName(), entity.getField(fieldDefinition.getName()));
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31).append(errorMessage).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UniqueValidator other = (UniqueValidator) obj;
        return new EqualsBuilder().append(errorMessage, other.errorMessage).isEquals();
    }

}
