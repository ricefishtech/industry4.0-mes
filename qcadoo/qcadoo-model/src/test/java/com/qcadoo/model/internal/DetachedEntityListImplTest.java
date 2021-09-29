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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;

public class DetachedEntityListImplTest {

    private DetachedEntityListImpl detachedList;

    private DataDefinition dataDefinition;

    private List<Entity> entities;

    @Before
    public final void init() {
        dataDefinition = mock(DataDefinition.class);
        entities = getListOfMockEntities();
        detachedList = new DetachedEntityListImpl(dataDefinition, entities);
    }

    private List<Entity> getListOfMockEntities() {
        Entity e1 = mock(Entity.class);
        Entity e2 = mock(Entity.class);
        Entity e3 = mock(Entity.class);

        when(e1.getId()).thenReturn(1L);
        when(e2.getId()).thenReturn(2L);
        when(e3.getId()).thenReturn(3L);

        return Lists.newArrayList(e1, e2, e3);
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void shouldThrowExceptionWhenCallingFindMethod() throws Exception {
        // when
        detachedList.find();
    }

}
