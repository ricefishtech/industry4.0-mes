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
package com.qcadoo.model;

import org.springframework.stereotype.Service;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;

@Service
public class CustomHook {

    public void hook(final DataDefinition dataDefinition, final Entity entity) {

    }

    public void updateHook(final DataDefinition dataDefinition, final Entity entity) {

    }

    public void createHook(final DataDefinition dataDefinition, final Entity entity) {

    }

    public void copyHook(final DataDefinition dataDefinition, final Entity entity) {

    }

    public static void staticHook(final DataDefinition dataDefinition, final Entity entity) {

    }

    public boolean deleteHook(final DataDefinition dataDefinition, final Entity entity) {
        return true;
    }

    public void validate(final DataDefinition dataDefinition, final Entity entity) {

    }

    public void validateField(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition, final Entity entity,
            final Object oldValue, final Object newValue) {

    }

    public static void staticValidateField(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition,
            final Entity entity, final Object oldValue, final Object newValue) {

    }

}
