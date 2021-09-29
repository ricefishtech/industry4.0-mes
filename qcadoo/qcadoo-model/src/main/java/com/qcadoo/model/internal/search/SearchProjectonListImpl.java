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
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import com.qcadoo.model.api.search.SearchProjection;
import com.qcadoo.model.api.search.SearchProjectionList;

public class SearchProjectonListImpl implements SearchProjectionList {

    private final ProjectionList projections;

    public SearchProjectonListImpl() {
        projections = Projections.projectionList();
    }

    @Override
    public Projection getHibernateProjection() {
        return projections;
    }

    @Override
    public SearchProjectionList add(final SearchProjection projection) {
        projections.add(projection.getHibernateProjection());
        return this;
    }

}
