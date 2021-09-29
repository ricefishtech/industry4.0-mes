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
package com.qcadoo.view.api.utils;

import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.components.FormComponent;

/**
 * Helper class for Form
 * 
 * @deprecated
 */
@Deprecated
public final class FormUtils {

    private FormUtils() {
    }

    /**
     * Set Entity which be used to fill this form
     * 
     * @deprecated this method is deprecated, if you want set form's entity, use {@link FormComponent#setEntity(Entity)}
     * 
     * @param form
     *            form which want to fill
     * @param entity
     *            entity which be used to fill form
     */
    @Deprecated
    public static void setFormEntity(final FormComponent form, final Entity entity) {
        form.setEntity(entity);
    }
}
