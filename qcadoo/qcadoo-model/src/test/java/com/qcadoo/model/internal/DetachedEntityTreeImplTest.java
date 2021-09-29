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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityTree;

public class DetachedEntityTreeImplTest {

    @Test
    public void shouldBeEmptyIfEntitiesIsEmpty() throws Exception {
        // when
        EntityTree tree = new DetachedEntityTreeImpl(new ArrayList<Entity>());

        // then
        assertTrue(tree.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfEntitiesIsNull() throws Exception {
        // when
        new DetachedEntityTreeImpl(null);
    }

    @Test
    public final void shouldBuildTree() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        Entity entity3 = mock(Entity.class);

        when(entity1.getId()).thenReturn(1L);
        when(entity2.getId()).thenReturn(2L);
        when(entity3.getId()).thenReturn(3L);

        when(entity1.getField("parent")).thenReturn(null);
        when(entity2.getField("parent")).thenReturn(1L);
        when(entity3.getField("parent")).thenReturn(2L);

        when(entity1.getBelongsToField("parent")).thenReturn(null);
        when(entity2.getBelongsToField("parent")).thenReturn(entity1);
        when(entity3.getBelongsToField("parent")).thenReturn(entity2);

        // when
        EntityTree tree = new DetachedEntityTreeImpl(Lists.newArrayList(entity1, entity2, entity3));

        // then
        Assert.assertEquals(3, tree.size());
        Assert.assertEquals(entity1.getId(), tree.getRoot().getId());
        Assert.assertEquals(entity2.getId(), tree.getRoot().getChildren().get(0).getId());
        Assert.assertEquals(entity3.getId(), tree.getRoot().getChildren().get(0).getChildren().get(0).getId());

    }

    @Test(expected = IllegalStateException.class)
    public final void shouldThrowExceptionWhenParentIsNotFound() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        Entity entity3 = mock(Entity.class);
        Entity wrongEntity = mock(Entity.class);

        when(entity1.getId()).thenReturn(1L);
        when(entity2.getId()).thenReturn(2L);
        when(entity3.getId()).thenReturn(3L);
        when(wrongEntity.getId()).thenReturn(99L);

        when(entity1.getField("parent")).thenReturn(null);
        when(entity2.getField("parent")).thenReturn(1L);
        when(entity3.getField("parent")).thenReturn(99L);

        when(entity1.getBelongsToField("parent")).thenReturn(null);
        when(entity2.getBelongsToField("parent")).thenReturn(entity1);
        when(entity3.getBelongsToField("parent")).thenReturn(wrongEntity);

        // when
        new DetachedEntityTreeImpl(Lists.newArrayList(entity1, entity2, entity3));
    }

    @Test(expected = IllegalStateException.class)
    public final void shouldThrowExceptionIfTreeHasMultipleRoots() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        Entity entity3 = mock(Entity.class);

        when(entity1.getId()).thenReturn(1L);
        when(entity2.getId()).thenReturn(2L);
        when(entity3.getId()).thenReturn(3L);

        when(entity1.getField("parent")).thenReturn(null);
        when(entity2.getField("parent")).thenReturn(1L);
        when(entity3.getField("parent")).thenReturn(null);

        when(entity1.getBelongsToField("parent")).thenReturn(null);
        when(entity2.getBelongsToField("parent")).thenReturn(entity1);
        when(entity3.getBelongsToField("parent")).thenReturn(null);

        // when
        new DetachedEntityTreeImpl(Lists.newArrayList(entity1, entity2, entity3));
    }

    @Test(expected = IllegalStateException.class)
    public final void shouldThrowExceptionIfTreeHasNotRoots() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        Entity entity3 = mock(Entity.class);

        when(entity1.getId()).thenReturn(1L);
        when(entity2.getId()).thenReturn(2L);
        when(entity3.getId()).thenReturn(3L);

        when(entity1.getField("parent")).thenReturn(3L);
        when(entity2.getField("parent")).thenReturn(1L);
        when(entity3.getField("parent")).thenReturn(2L);

        when(entity1.getBelongsToField("parent")).thenReturn(entity3);
        when(entity2.getBelongsToField("parent")).thenReturn(entity1);
        when(entity3.getBelongsToField("parent")).thenReturn(entity2);

        // when
        new DetachedEntityTreeImpl(Lists.newArrayList(entity1, entity2, entity3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void shouldThrowExceptionWhenTryingToGetSearchCriteria() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        Entity entity3 = mock(Entity.class);

        when(entity1.getId()).thenReturn(1L);
        when(entity2.getId()).thenReturn(2L);
        when(entity3.getId()).thenReturn(3L);

        when(entity1.getField("parent")).thenReturn(null);
        when(entity2.getField("parent")).thenReturn(1L);
        when(entity3.getField("parent")).thenReturn(2L);

        when(entity1.getBelongsToField("parent")).thenReturn(null);
        when(entity2.getBelongsToField("parent")).thenReturn(entity1);
        when(entity3.getBelongsToField("parent")).thenReturn(entity2);

        EntityTree tree = new DetachedEntityTreeImpl(Lists.newArrayList(entity1, entity2, entity3));

        // when
        tree.find();
    }

    @Test
    public final void shouldConvertToString() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        Entity entity3 = mock(Entity.class);

        when(entity1.getId()).thenReturn(1L);
        when(entity2.getId()).thenReturn(2L);
        when(entity3.getId()).thenReturn(3L);

        when(entity1.getField("parent")).thenReturn(null);
        when(entity2.getField("parent")).thenReturn(1L);
        when(entity3.getField("parent")).thenReturn(2L);

        when(entity1.getBelongsToField("parent")).thenReturn(null);
        when(entity2.getBelongsToField("parent")).thenReturn(entity1);
        when(entity3.getBelongsToField("parent")).thenReturn(entity2);

        EntityTree tree = new DetachedEntityTreeImpl(Lists.newArrayList(entity1, entity2, entity3));

        // when
        String stringTree = tree.toString();

        // then
        Assert.assertEquals("EntityTree[DETACHED!][size=3]", stringTree);
    }

    @Test
    public final void shouldReturnTreeItem() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        Entity entity3 = mock(Entity.class);

        when(entity1.getId()).thenReturn(1L);
        when(entity2.getId()).thenReturn(2L);
        when(entity3.getId()).thenReturn(3L);

        when(entity1.getField("parent")).thenReturn(null);
        when(entity2.getField("parent")).thenReturn(1L);
        when(entity3.getField("parent")).thenReturn(2L);

        when(entity1.getBelongsToField("parent")).thenReturn(null);
        when(entity2.getBelongsToField("parent")).thenReturn(entity1);
        when(entity3.getBelongsToField("parent")).thenReturn(entity2);

        EntityTree tree = new DetachedEntityTreeImpl(Lists.newArrayList(entity1, entity2, entity3));

        // when
        Entity res1 = tree.get(0);
        Entity res2 = tree.get(1);
        Entity res3 = tree.get(2);

        // then
        Assert.assertEquals(entity1, res1);
        Assert.assertEquals(entity2, res2);
        Assert.assertEquals(entity3, res3);
    }

}