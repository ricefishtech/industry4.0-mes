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

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.internal.api.PerformerEntitiesChain;

public final class PerformerEntitiesChainImpl implements PerformerEntitiesChain {

    private final List<Entity> performersChain;

    public PerformerEntitiesChainImpl(final Entity performer) {
        Preconditions.checkNotNull(performer);
        this.performersChain = Lists.newArrayList(performer);
    }

    @Override
    public void append(final Entity performer) {
        performersChain.add(performer);
    }

    @Override
    public Entity find(final Entity entity) {
        Preconditions.checkNotNull(entity);
        for (Entity performer : performersChain) {
            if (areShallowEqual(performer, entity)) {
                return performer;
            }
        }
        return null;
    }

    private boolean areShallowEqual(final Entity firstEntity, final Entity secondEntity) {
        return firstEntity.hashCode() == secondEntity.hashCode();
    }

    @Override
    public Entity getLast() {
        return performersChain.get(performersChain.size() - 1);
    }

}
