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
package com.qcadoo.view.internal.ribbon.model;

import java.util.Collections;
import java.util.List;

public class SingleRibbonGroupPack implements RibbonGroupsPack {

    private final InternalRibbonGroup group;

    public SingleRibbonGroupPack(final InternalRibbonGroup group) {
        this.group = group;
    }

    @Override
    public List<InternalRibbonGroup> getGroups() {
        return Collections.singletonList(group);
    }

    @Override
    public InternalRibbonGroup getGroupByName(final String groupName) {
        if (group.getName().equals(groupName)) {
            return group;
        }
        return null;
    }

    @Override
    public RibbonGroupsPack getCopy() {
        return new SingleRibbonGroupPack(group.getCopy());
    }

    @Override
    public RibbonGroupsPack getUpdate() {
        InternalRibbonGroup diffGroup = group.getUpdate();
        if (diffGroup != null) {
            return new SingleRibbonGroupPack(diffGroup);
        }
        return null;
    }

}
