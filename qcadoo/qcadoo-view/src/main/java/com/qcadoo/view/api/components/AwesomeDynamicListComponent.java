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

import java.util.List;

import com.qcadoo.model.api.Entity;

/**
 * Represents awesome dynamic list component
 * 
 * @since 1.1.2
 */
public interface AwesomeDynamicListComponent extends FieldComponent {

    /**
     * Returns list of all children form components (rows) of this awesome dynamic list component
     * 
     * @return list of all children form components
     */
    List<FormComponent> getFormComponents();

    /**
     * Returns child form component (row) with specified id or null if no such component can be found
     * 
     * @param id
     *            id of child form entity
     * @return child form component with specified entity id
     */
    FormComponent getFormComponent(Long id);

    /**
     * Returns a list of underlying entities (entity proxies).
     * 
     * @return underlying entities (entity proxies).
     * 
     * @since 1.3.0
     */
    List<Entity> getEntities();

}
