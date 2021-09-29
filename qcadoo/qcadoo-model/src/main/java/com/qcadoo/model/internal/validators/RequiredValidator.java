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

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.HasManyType;
import com.qcadoo.model.api.types.ManyToManyType;
import com.qcadoo.model.api.types.TreeType;
import com.qcadoo.model.internal.api.ErrorMessageDefinition;
import com.qcadoo.model.internal.api.FieldHookDefinition;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public final class RequiredValidator implements FieldHookDefinition, ErrorMessageDefinition {

    // TODO maku extract these two error messages into some public constants' class
    private static final String MISSING_ERROR = "qcadooView.validate.field.error.missing";

    private static final String MISSING_RELATION_ERROR = "qcadooView.validate.field.error.missingRelation";

    private String errorMessage = MISSING_ERROR;

    private String errorRelationMessage = MISSING_RELATION_ERROR;

    private FieldDefinition fieldDefinition;

    @Override
    public void initialize(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean call(final Entity entity, final Object oldValue, final Object newValue) {
        if (fieldDefinition.getType() instanceof HasManyType || fieldDefinition.getType() instanceof ManyToManyType
                || fieldDefinition.getType() instanceof TreeType) {
            if (entity.getField(fieldDefinition.getName()) == null
                    || ((List) entity.getField(fieldDefinition.getName())).isEmpty()) {
                entity.addError(fieldDefinition, errorRelationMessage);
                return false;
            }
        } else if (newValue == null) {
            entity.addError(fieldDefinition, errorMessage);
            return false;
        }

        return true;
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorRelationMessage = errorMessage;
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
        RequiredValidator other = (RequiredValidator) obj;
        return new EqualsBuilder().append(errorMessage, other.errorMessage).isEquals();
    }

}
