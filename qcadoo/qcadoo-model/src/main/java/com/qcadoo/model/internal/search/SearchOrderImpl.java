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
package com.qcadoo.model.internal.search;

import org.hibernate.criterion.Order;

import com.qcadoo.model.api.search.SearchOrder;

public class SearchOrderImpl implements SearchOrder {

    private final Order order;

    public SearchOrderImpl(final Order order) {
        this.order = order;
    }

    @Override
    public Order getHibernateOrder() {
        return order;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((order == null) ? 0 : order.toString().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SearchOrderImpl)) {
            return false;
        }
        SearchOrderImpl other = (SearchOrderImpl) obj;
        if (order == null) {
            if (other.order != null) {
                return false;
            }
        } else if (!order.toString().equals(other.order.toString())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return order.toString();
    }

}
