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
import com.qcadoo.view.api.components.lookup.FilterValueHolder;

public interface LookupComponent extends FieldComponent {

    /**
     * Returns entity which is selected in lookup
     *
     * @return entity which is selected in lookup or null when entity is not found in database
     */
    Entity getEntity();

    /**
     * Gets current criteria modifier parameters value. To modify them use setFilterValue method.
     *
     * @return Current criteria modifier parameters value.
     * @since 1.2.1
     */
    FilterValueHolder getFilterValue();

    /**
     * Sets filter value which will be send to criteria modifier hook. Set this value in beforeRender hook to make it work
     * correctly.
     *
     * @param value
     * @since 1.2.1
     */
    void setFilterValue(FilterValueHolder value);

    /**
     * Check if this lookup doesn't have selected any entity and its input is empty.
     *
     * @return true if this lookup doesn't have selected any entity and its input is empty.
     * @since 1.2.1
     */
    boolean isEmpty();

    /**
     * Get current filed code
     * @return current code
     */
    String getCurrentCode();

}
