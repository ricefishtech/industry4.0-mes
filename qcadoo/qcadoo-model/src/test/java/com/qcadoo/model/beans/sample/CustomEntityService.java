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
package com.qcadoo.model.beans.sample;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;

public class CustomEntityService {

    private static final String READ_ONLY_FIELD_NAME = "readOnly";

    public void onUpdate(final DataDefinition dataDefinition, final Entity entity) {
        entity.setField("name", "update");
    }

    public void onSave(final DataDefinition dataDefinition, final Entity entity) {
        entity.setField("age", 11);
    }

    public void onCreate(final DataDefinition dataDefinition, final Entity entity) {
        entity.setField("name", "create");
    }

    public boolean onDelete(final DataDefinition dataDefinition, final Entity entity) {
        entity.setField("name", "delete");
        return true;
    }

    public void rewriteReadOnlyField(final DataDefinition dataDefinition, final Entity entity) {
        entity.setField("name", entity.getField(READ_ONLY_FIELD_NAME));
    }

    public void overrideReadOnlyField(final DataDefinition dataDefinition, final Entity entity) {
        entity.setField(READ_ONLY_FIELD_NAME, "overrided");
    }

    public boolean isEqualToQwerty(final DataDefinition dataDefinition, final FieldDefinition fieldDefinition,
            final Entity entity, final Object oldObject, final Object object) {
        return String.valueOf(object).equals("qwerty");
    }

    public boolean hasAge18AndNameMrT(final DataDefinition dataDefinition, final Entity entity) {
        if (entity.getField("age").equals(18) && entity.getField("name").equals("Mr T")) {
            return true;
        } else {
            entity.addError(dataDefinition.getField("name"), "xxx");
            return false;
        }
    }

    public void appendC(final DataDefinition dataDefinition, final Entity entity) {
        appendToName(entity, "c");
    }

    public void appendB(final DataDefinition dataDefinition, final Entity entity) {
        appendToName(entity, "b");
    }

    public void appendD(final DataDefinition dataDefinition, final Entity entity) {
        appendToName(entity, "d");
    }

    private void appendToName(final Entity entity, final String valueToAppend) {
        String name = entity.getStringField("name");
        entity.setField("name", name + valueToAppend);
    }

}
