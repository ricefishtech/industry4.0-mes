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
package com.qcadoo.model.internal.types;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import com.google.common.collect.Sets;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.NumberService;
import com.qcadoo.model.internal.NumberServiceImpl;
import com.qcadoo.model.internal.api.DefaultValidatorsProvider;
import com.qcadoo.model.internal.api.FieldHookDefinition;
import com.qcadoo.model.internal.api.ValueAndError;
import com.qcadoo.model.internal.validators.ScaleValidator;
import com.qcadoo.model.internal.validators.UnscaledValueValidator;

public final class DecimalType extends AbstractFieldType implements DefaultValidatorsProvider {

    public DecimalType() {
        this(true);
    }

    public DecimalType(final boolean copyable) {
        super(copyable);
    }

    @Override
    public Class<?> getType() {
        return BigDecimal.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        BigDecimal decimal = null;

        if (value instanceof BigDecimal) {
            decimal = ((BigDecimal) value).stripTrailingZeros();
        } else {
            try {
                decimal = new BigDecimal(String.valueOf(value)).stripTrailingZeros();
            } catch (NumberFormatException e) {
                return ValueAndError.withError("qcadooView.validate.field.error.invalidNumericFormat");
            }
        }
        return ValueAndError.withoutError(decimal);
    }

    @Override
    public String toString(final Object value, final Locale locale) {
        if (value == null) {
            return "";
        }
        if(value instanceof String){
            return (String) value;
        }
        NumberFormat format = null;
        if (locale == null) {
            format = NumberFormat.getNumberInstance();
        } else {
            format = NumberFormat.getNumberInstance(locale);
        }

        format.setMaximumFractionDigits(getMaxFractionDigits(value));
        return format.format(value);
    }

    private int getMaxFractionDigits(final Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).stripTrailingZeros().scale();
        }
        return NumberServiceImpl.MAX_PRECISION;
    }

    @Override
    public Object fromString(final String value, final Locale locale) {
        ParsePosition parsePosition = new ParsePosition(0);
        String trimedValue = value.replaceAll(" ", "");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        formatter.setParseBigDecimal(true);
        Object parsedValue = formatter.parseObject(trimedValue, parsePosition);

        if (parsePosition.getIndex() == trimedValue.length()) {
            return parsedValue;
        }

        return value;
    }

    @Override
    public Collection<FieldHookDefinition> getMissingValidators(final Iterable<FieldHookDefinition> validators) {
        FieldHookDefinition defaultScaleValidator = new ScaleValidator(null, null,
                NumberService.DEFAULT_MAX_FRACTION_DIGITS_IN_DECIMAL);
        FieldHookDefinition defaultUnscaledValueValidator = new UnscaledValueValidator(null, null,
                NumberService.DEFAULT_MAX_INTEGER_DIGITS_IN_DECIMAL);
        Set<FieldHookDefinition> missingValidators = Sets.<FieldHookDefinition> newHashSet(defaultScaleValidator,
                defaultUnscaledValueValidator);

        for (FieldHookDefinition validator : validators) {
            if (validator instanceof UnscaledValueValidator && ((UnscaledValueValidator) validator).hasUppuerBoundDefined()) {
                missingValidators.remove(defaultUnscaledValueValidator);
            }
            if (validator instanceof ScaleValidator && ((ScaleValidator) validator).hasUppuerBoundDefined()) {
                missingValidators.remove(defaultScaleValidator);
            }
        }
        return missingValidators;
    }

}
