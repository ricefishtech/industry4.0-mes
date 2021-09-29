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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.internal.api.ErrorMessageDefinition;
import com.qcadoo.model.internal.api.FieldHookDefinition;

public final class LengthValidator implements FieldHookDefinition, ErrorMessageDefinition {

    private static final String INVALID_LENGTH_ERROR = "qcadooView.validate.field.error.invalidLength";

    private final Integer max;

    private final Integer min;

    private final Integer is;

    private transient Integer hashCode = null;

    private String errorMessage = INVALID_LENGTH_ERROR;

    private FieldDefinition fieldDefinition;

    public LengthValidator(final Integer min, final Integer is, final Integer max) {
        this.min = min;
        this.is = is;
        this.max = max;
    }

    @Override
    public void initialize(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
    }

    @Override
    public boolean call(final Entity entity, final Object oldValue, final Object newValue) {
        if (newValue == null) {
            return true;
        }

        Class<?> fieldClass = fieldDefinition.getType().getType();

        if (!fieldClass.equals(String.class)) {
            return true;
        }

        int length = newValue.toString().length();

        return validateLength(fieldDefinition, entity, length);
    }

    private boolean validateLength(final FieldDefinition fieldDefinition, final Entity validatedEntity, final int length) {
        if (max != null && length > max) {
            validatedEntity.addError(fieldDefinition, errorMessage);
            return false;
        }
        if (min != null && length < min) {
            validatedEntity.addError(fieldDefinition, errorMessage);
            return false;
        }
        if (is != null && !is.equals(length)) {
            validatedEntity.addError(fieldDefinition, errorMessage);
            return false;
        }

        return true;
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean hasUppuerBoundDefined() {
        return max != null || is != null;
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = new HashCodeBuilder(1, 31).append(min).append(is).append(max).toHashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LengthValidator other = (LengthValidator) obj;
        return new EqualsBuilder().append(min, other.min).append(is, other.is).append(max, other.max)
                .append(errorMessage, other.errorMessage).isEquals();
    }

}
