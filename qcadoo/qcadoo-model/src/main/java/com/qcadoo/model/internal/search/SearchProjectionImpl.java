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

import org.hibernate.criterion.Projection;

import com.qcadoo.model.api.search.SearchProjection;

public class SearchProjectionImpl implements SearchProjection {

    private final Projection projection;

    public SearchProjectionImpl(final Projection projection) {
        this.projection = projection;
    }

    @Override
    public Projection getHibernateProjection() {
        return projection;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((projection == null) ? 0 : projection.toString().hashCode());
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
        if (!(obj instanceof SearchProjectionImpl)) {
            return false;
        }
        SearchProjectionImpl other = (SearchProjectionImpl) obj;
        if (projection == null) {
            if (other.projection != null) {
                return false;
            }
        } else if (!projection.toString().equals(other.projection.toString())) {
            return false;
        }
        return true;
    }

}
