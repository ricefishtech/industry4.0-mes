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

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityTree;
import com.qcadoo.model.api.EntityTreeNode;
import com.qcadoo.model.api.types.TreeType;
import com.qcadoo.model.internal.EntityTreeImpl;
import com.qcadoo.model.internal.api.PriorityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.google.common.collect.Lists.newLinkedList;

@Service
public class TreeNumberingServiceImpl implements TreeNumberingService {

    private static final String ROOT_NODE_NUMBER = "1";

    @Autowired
    private PriorityService priorityService;

    @Override
    public final void generateTreeNumbers(final EntityTree tree) {
        if (tree.getRoot() == null) {
            return;
        }
        generateTreeNumbers(tree.getRoot());
    }

    @Override
    public final void generateTreeNumbers(final EntityTreeNode treeNode) {
        assignNumberToTreeNode(treeNode, Lists.newLinkedList(Lists.newArrayList(ROOT_NODE_NUMBER)));
    }

    @Override
    @Transactional
    public final void generateNumbersAndUpdateTree(final EntityTree tree) {
        if (tree.getRoot() == null) {
            return;
        }
        generateTreeNumbers(tree);
        for (Entity treeNode : tree) {
            treeNode.getDataDefinition().save(treeNode);
        }
    }

    @Override
    public final void generateNumbersAndUpdateTree(final DataDefinition dd, final String joinFieldName,
            final Long belongsToEntityId) {
        EntityTree tree = new EntityTreeImpl(dd, joinFieldName, belongsToEntityId);
        generateNumbersAndUpdateTree(tree);
    }

    @Override
    public final Comparator<Entity> getTreeNodesNumberComparator() {
        return new TreeNodesNumberComparator();
    }

    final void assignNumberToTreeNode(final EntityTreeNode treeNode, final Deque<String> chain) {
        treeNode.setField(TreeType.NODE_NUMBER_FIELD, convertCollectionToString(chain));

        List<EntityTreeNode> childrens = newLinkedList(treeNode.getChildren());
        Collections.sort(childrens, priorityService.getEntityPriorityComparator());

        int charNumber = 0;
        for (EntityTreeNode child : childrens) {
            Deque<String> newBranch = Lists.newLinkedList(chain);
            if (childrens.size() == 1) {
                incrementLastChainNumber(newBranch);
            } else {
                incrementLastChainCharacter(newBranch, charNumber++);
            }
            assignNumberToTreeNode(child, newBranch);
        }

    }

    public static void incrementLastChainNumber(final Deque<String> chain) {
        Integer nextNumber = Integer.valueOf(chain.pollLast()) + 1;
        chain.addLast(nextNumber.toString());
    }

    public static void incrementLastChainCharacter(final Deque<String> chain, final int charNumber) {
        int quotient = charNumber / 26;
        int modulo = charNumber % 26;

        if (quotient <= 0) {
            chain.addLast(String.valueOf((char) (65 + charNumber)));
        } else {
            chain.addLast(String.valueOf((char) (65 + (quotient - 1))).concat(String.valueOf((char) (65 + modulo))));
        }

        chain.addLast("1");
    }

    public static String convertCollectionToString(final Collection<String> collection) {
        return StringUtils.join(collection, '.') + '.';
    }

    private final class TreeNodesNumberComparator implements Comparator<Entity> {

        @Override
        public int compare(final Entity e1, final Entity e2) {
            String n1 = e1.getStringField(TreeType.NODE_NUMBER_FIELD);
            String n2 = e2.getStringField(TreeType.NODE_NUMBER_FIELD);
            if (n1 == null) {
                if (n2 == null) {
                    return 0;
                }
                return 1;
            }
            if (n2 == null) {
                return -1;
            }
            return n1.compareTo(n2);
        }
    }

}
