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
package com.qcadoo.view.api.components;

import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ComponentState;

/**
 * Represents form component
 * 
 * @since 0.4.0
 */
public interface FormComponent extends ComponentState {

    /**
     * Gets id of this form entity
     * 
     * @return id of entity
     */
    Long getEntityId();

    /**
     * Returns entity filled with this forms values
     * 
     * @return entity filled with this forms values
     */
    Entity getEntity();

    /**
     * Returns entity from database updated with this form values. If persisted entity doesn't exist (for example during creation)
     * then this method will behave exactly like FormComponent#getEntity. Otherwise persisted entity will be fetched and updated
     * with this form children components values.
     * 
     * @return persisted entity with this forms values included
     * @since 1.2.1
     */
    Entity getPersistedEntityWithIncludedFormValues();

    /**
     * Checks if all fields of this form and entity itself are valid
     * 
     * @return false when at least one field is not valid, true otherwise
     */
    boolean isValid();

    /**
     * Enables or disables this form and all its inner components
     * 
     * @param enabled
     *            true if this form and all its inner components should be enabled, false if disabled
     */
    void setFormEnabled(boolean enabled);

    /**
     * Returns child field component with specified name (first occurence) or null if no such component found
     * 
     * @param name
     *            name of component
     * @return field component with specified name
     */
    FieldComponent findFieldComponentByName(String name);

    /**
     * Set Entity which be used to fill this form
     * 
     * @param entity
     *            entity which be used to fill form
     * @since 1.1.5
     */
    void setEntity(Entity entity);

}