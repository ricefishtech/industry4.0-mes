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

import com.qcadoo.view.api.ComponentState;

/**
 * Represents universal field component.
 * 
 * @since 0.4.0
 */
public interface FieldComponent extends ComponentState {

    /**
     * Checks if field defined by this component is required
     * 
     * @return true if field defined by this component is required
     */
    boolean isRequired();

    /**
     * Sets if field is required
     * 
     * @param required
     *            true if field should be required
     */
    void setRequired(boolean required);

    /**
     * Checks if field defined by this component is persistent
     * 
     * @return true if field defined by this component is persistent
     */
    boolean isPersistent();

    /**
     * Informs that this component should be updated
     */
    void requestComponentUpdateState();

}