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

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.internal.api.DefaultValidatorsProvider;
import com.qcadoo.model.internal.api.FieldHookDefinition;
import com.qcadoo.model.internal.api.ValueAndError;
import com.qcadoo.model.internal.validators.LengthValidator;

public final class PasswordType extends AbstractFieldType implements DefaultValidatorsProvider {

    private final PasswordEncoder passwordEncoder;

    public PasswordType(final PasswordEncoder passwordEncoder) {
        this(passwordEncoder, true);
    }

    public PasswordType(final PasswordEncoder passwordEncoder, final boolean isCopyable) {
        super(isCopyable);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    @Override
    public ValueAndError toObject(final FieldDefinition fieldDefinition, final Object value) {
        if (isHashedPassword((String) value)) {
            return ValueAndError.withoutError(value);
        } else {
            return ValueAndError.withoutError(passwordEncoder.encodePassword(String.valueOf(value), null));
        }
    }

    @Override
    public String toString(final Object value, final Locale locale) {
        return (String) value;
    }

    @Override
    public Object fromString(final String value, final Locale locale) {
        return value;
    }

    private boolean isHashedPassword(final String value) {
        return Pattern.matches("[0-9a-f]{64}", value);
    }

    @Override
    public Collection<FieldHookDefinition> getMissingValidators(final Iterable<FieldHookDefinition> validators) {
        for (FieldHookDefinition validator : validators) {
            if (validator instanceof LengthValidator) {
                if (((LengthValidator) validator).hasUppuerBoundDefined()) {
                    return Collections.emptyList();
                }
            }
        }
        return Lists.<FieldHookDefinition> newArrayList(new LengthValidator(null, null, 255));
    }

}
