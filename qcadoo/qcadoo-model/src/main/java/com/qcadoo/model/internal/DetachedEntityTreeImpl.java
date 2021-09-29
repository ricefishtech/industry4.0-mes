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

import static com.google.common.base.Preconditions.checkState;

import java.util.AbstractList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityTree;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;

public final class DetachedEntityTreeImpl extends AbstractList<Entity> implements EntityTree {

    private List<Entity> entities = null;

    private EntityTreeNodeImpl root = null;

    public DetachedEntityTreeImpl(final List<Entity> entities) {
        super();

        Preconditions.checkArgument(entities != null, "given entity list should not be null!");
        this.entities = entities;
        checkEntities();
    }

    private void checkEntities() {
        Map<Long, EntityTreeNodeImpl> entitiesById = new LinkedHashMap<Long, EntityTreeNodeImpl>();
        for (Entity entity : entities) {
            entitiesById.put(entity.getId(), new EntityTreeNodeImpl(entity));
        }

        for (EntityTreeNodeImpl entity : entitiesById.values()) {
            Entity parent = entity.getBelongsToField("parent");
            if (parent == null) {
                checkState(root == null, "Tree cannot have multiple roots");
                root = entity;
            } else {
                checkState(entitiesById.get(parent.getId()) != null, "Parent for tree node (" + entity + ") not found");
                entitiesById.get(parent.getId()).addChild(entity);
            }
        }

        Preconditions.checkState(entities.isEmpty() || root != null, "Root for tree not found");
    }

    @Override
    public SearchCriteriaBuilder find() {
        throw new UnsupportedOperationException("Cannot find entity for detached entity tree");
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
    public EntityTreeNodeImpl getRoot() {
        return root;
    }

    public boolean checkIfTreeContainsEntity(final Long entityId) {
        if (entityId == null) {
            return false;
        }
        for (Entity entity : entities) {
            if (entityId.equals(entity.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "EntityTree[DETACHED!][size=" + size() + "]";
    }

}
