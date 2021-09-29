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

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityTree;
import com.qcadoo.model.api.EntityTreeNode;
import com.qcadoo.model.internal.DetachedEntityTreeImpl;
import com.qcadoo.model.internal.api.PriorityService;

/**
 * Helper service for EntityTree
 */
@Service
public class EntityTreeUtilsService {

    @Autowired
    private PriorityService priorityService;

    /**
     * Return new instance of DetachedEntityTreeImpl, contains specified entities
     * 
     * @param entities
     *            entity tree nodes
     * @return new instance of DetachedEntityTreeImpl
     */
    public static EntityTree getDetachedEntityTree(final List<Entity> entities) {
        return new DetachedEntityTreeImpl(entities);
    }

    /**
     * Return list of entities sorted in the same order as they appear on the tree
     * 
     * @param tree
     *            entity tree containing entities to be listed
     * @return list of sorted entities
     * 
     * @since 1.1.5
     */
    public List<Entity> getSortedEntities(final EntityTree tree) {
        List<Entity> nodesList = Lists.newLinkedList();
        if (tree.isEmpty()) {
            return nodesList;
        }
        return getSortedEntitiesFromNode(tree.getRoot());
    }

    private List<Entity> getSortedEntitiesFromNode(final EntityTreeNode node) {
        List<Entity> nodesList = Lists.newLinkedList();
        nodesList.add(node);

        List<EntityTreeNode> childNodes = Lists.newLinkedList(node.getChildren());
        Collections.sort(childNodes, priorityService.getEntityPriorityComparator());
        for (EntityTreeNode childNode : childNodes) {
            nodesList.addAll(getSortedEntitiesFromNode(childNode));
        }

        return nodesList;
    }
}
