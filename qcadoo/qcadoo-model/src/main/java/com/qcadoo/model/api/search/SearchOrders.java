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
package com.qcadoo.model.api.search;

import org.hibernate.criterion.Order;

import com.qcadoo.model.internal.search.SearchOrderImpl;

/**
 * Utility with factory methods for {@link SearchOrder}.
 * 
 * @since 0.4.1
 */
public final class SearchOrders {

    private SearchOrders() {
    }

    /**
     * Creates ascending order using given field.
     * 
     * @param field
     *            field
     * @return order
     */
    public static SearchOrder asc(final String field) {
        return new SearchOrderImpl(Order.asc(field).ignoreCase());
    }

    /**
     * Creates descending order using given field.
     * 
     * @param field
     *            field
     * @return order
     */
    public static SearchOrder desc(final String field) {
        return new SearchOrderImpl(Order.desc(field).ignoreCase());
    }

}
