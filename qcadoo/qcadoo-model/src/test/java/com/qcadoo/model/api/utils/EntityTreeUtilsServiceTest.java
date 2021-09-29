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
package com.qcadoo.model.api.utils;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityTree;
import com.qcadoo.model.api.EntityTreeNode;
import com.qcadoo.model.internal.api.PriorityService;

public class EntityTreeUtilsServiceTest {

    private static final String PRIORITY = "priority";

    private EntityTreeUtilsService entityTreeUtils;

    @Mock
    private EntityTree tree;

    @Mock
    private PriorityService priorityService;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        given(priorityService.getEntityPriorityComparator()).willReturn(new Comparator<Entity>() {

            @Override
            public int compare(Entity e1, Entity e2) {
                Integer p1 = (Integer) e1.getField(PRIORITY);
                Integer p2 = (Integer) e2.getField(PRIORITY);
                return p1.compareTo(p2);
            }
        });

        entityTreeUtils = new EntityTreeUtilsService();
        ReflectionTestUtils.setField(entityTreeUtils, "priorityService", priorityService);

    }

    @Test
    public final void shouldReturnListOfNodesInTheSameOrderAsTheyAppearOnTheTree() throws Exception {
        // given
        EntityTreeNode l1b1a11 = mockEntityTreeNode(null, 1, "1.B.1.A.11");
        EntityTreeNode l1b1a10 = mockEntityTreeNode(Lists.newArrayList(l1b1a11), 1, "1.B.1.A.10");
        EntityTreeNode l1b1a9 = mockEntityTreeNode(Lists.newArrayList(l1b1a10), 1, "1.B.1.A.9");
        EntityTreeNode l1b1a8 = mockEntityTreeNode(Lists.newArrayList(l1b1a9), 1, "1.B.1.A.8");
        EntityTreeNode l1b1a7 = mockEntityTreeNode(Lists.newArrayList(l1b1a8), 1, "1.B.1.A.7");
        EntityTreeNode l1b1a6 = mockEntityTreeNode(Lists.newArrayList(l1b1a7), 1, "1.B.1.A.6");
        EntityTreeNode l1b1a5 = mockEntityTreeNode(Lists.newArrayList(l1b1a6), 1, "1.B.1.A.5");
        EntityTreeNode l1b1a4 = mockEntityTreeNode(Lists.newArrayList(l1b1a5), 1, "1.B.1.A.4");
        EntityTreeNode l1b1a3 = mockEntityTreeNode(Lists.newArrayList(l1b1a4), 1, "1.B.1.A.3");
        EntityTreeNode l1b1a2 = mockEntityTreeNode(Lists.newArrayList(l1b1a3), 1, "1.B.1.A.2");
        EntityTreeNode l1b1a1 = mockEntityTreeNode(Lists.newArrayList(l1b1a2), 1, "1.B.1.A.1");
        EntityTreeNode l1b1b1 = mockEntityTreeNode(null, 2, "1.B.1.B.1");

        EntityTreeNode l1a1 = mockEntityTreeNode(null, 1, "1.A.1");
        EntityTreeNode l1b1 = mockEntityTreeNode(Lists.newLinkedList(Lists.newArrayList(l1b1b1, l1b1a1)), 2, "1.B.1");

        EntityTreeNode root = mockEntityTreeNode(Lists.newLinkedList(Lists.newArrayList(l1a1, l1b1)), 1, "1");

        given(tree.getRoot()).willReturn(root);
        given(tree.isEmpty()).willReturn(false);

        // when
        List<Entity> nodesList = entityTreeUtils.getSortedEntities(tree);

        // then
        Assert.assertEquals(15, nodesList.size());
        Assert.assertEquals("1", nodesList.get(0).getField("nodeNumber"));
        Assert.assertEquals("1.A.1", nodesList.get(1).getField("nodeNumber"));
        Assert.assertEquals("1.B.1", nodesList.get(2).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.1", nodesList.get(3).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.2", nodesList.get(4).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.3", nodesList.get(5).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.4", nodesList.get(6).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.5", nodesList.get(7).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.6", nodesList.get(8).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.7", nodesList.get(9).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.8", nodesList.get(10).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.9", nodesList.get(11).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.10", nodesList.get(12).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.A.11", nodesList.get(13).getField("nodeNumber"));
        Assert.assertEquals("1.B.1.B.1", nodesList.get(14).getField("nodeNumber"));
    }

    @Test
    public final void shouldReturnEmptyListIfTreeIsEmpty() throws Exception {
        // given
        given(tree.isEmpty()).willReturn(true);

        // when
        List<Entity> nodesList = entityTreeUtils.getSortedEntities(tree);

        // then
        Assert.assertTrue(nodesList.isEmpty());
    }

    @Test
    public final void shouldReturnDetachedEntityTree() throws Exception {
        // given
        Entity rootEntity = mockEntityTreeNode(null, 1, "1");

        // when
        EntityTree entityTree = EntityTreeUtilsService.getDetachedEntityTree(Lists.newArrayList(rootEntity));

        // then
        Assert.assertEquals(entityTree.getRoot(), rootEntity);
        Assert.assertEquals(1, entityTree.size());
    }

    private EntityTreeNode mockEntityTreeNode(final List<EntityTreeNode> children, final int priority, final String nodeNumber) {
        EntityTreeNode node = mock(EntityTreeNode.class);

        given(node.getField(PRIORITY)).willReturn(priority);
        given(node.getChildren()).willReturn(children == null ? new ArrayList<EntityTreeNode>() : children);

        given(node.getStringField("nodeNumber")).willReturn(nodeNumber);
        given(node.getField("nodeNumber")).willReturn(nodeNumber);

        return node;
    }

}
