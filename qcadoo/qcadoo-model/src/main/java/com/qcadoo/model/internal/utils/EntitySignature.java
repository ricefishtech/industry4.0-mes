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
package com.qcadoo.model.internal.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;

public final class EntitySignature {

    private final String plugin;

    private final String model;

    private final Long id;

    public static EntitySignature of(final Entity entity) {
        DataDefinition dataDefinition = entity.getDataDefinition();
        return new EntitySignature(dataDefinition.getPluginIdentifier(), dataDefinition.getName(), entity.getId());
    }

    private EntitySignature(final String plugin, final String model, final Long id) {
        this.plugin = plugin;
        this.model = model;
        this.id = id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31).append(id).append(model).append(plugin).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EntitySignature other = (EntitySignature) obj;
        return new EqualsBuilder().append(id, other.id).append(model, other.model).append(plugin, other.plugin).isEquals();
    }

    @Override
    public String toString() {
        return String.format("%s.%s[id=%s]", plugin, model, id);
    }
}
