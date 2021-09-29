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
package com.qcadoo.model.internal;

import java.util.AbstractList;
import java.util.List;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;

public final class DetachedEntityListImpl extends AbstractList<Entity> implements EntityList {

    private final DataDefinition dataDefinition;

    private final List<Entity> entities;

    public DetachedEntityListImpl(final DataDefinition dataDefinition, final List<Entity> entities) {
        super();

        this.dataDefinition = dataDefinition;
        if (entities == null) {
            this.entities = Lists.newArrayList();
        } else {
            this.entities = Lists.newArrayList(entities);
        }
    }

    @Override
    public SearchCriteriaBuilder find() {
        throw new UnsupportedOperationException("method find() is not supported for DetachedEntityList");
    }

    @Override
    public Entity get(final int index) {
        return entities.get(index);
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public boolean isEmpty() {
        return entities.isEmpty();
    }

    @Override
    public String toString() {
        return "EntityList[DETACHED!][" + dataDefinition.getPluginIdentifier() + "." + dataDefinition.getName() + "][size="
                + size() + "]";
    }

}
