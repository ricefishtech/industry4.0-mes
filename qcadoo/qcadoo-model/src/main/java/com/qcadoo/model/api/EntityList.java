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
package com.qcadoo.model.api;

import java.util.List;

import com.qcadoo.model.api.search.SearchCriteriaBuilder;

/**
 * Object represents list of entities from hasMany relation.
 * 
 * @since 0.4.0
 * @see Entity#getHasManyField(String)
 */
public interface EntityList extends List<Entity> {

    /**
     * Creates search criteria builder for this entities' data definition - it will be automatically restricted with hasMany
     * relation.
     * 
     * @see DataDefinition#find()
     * @return new search criteria builder
     */
    SearchCriteriaBuilder find();

}