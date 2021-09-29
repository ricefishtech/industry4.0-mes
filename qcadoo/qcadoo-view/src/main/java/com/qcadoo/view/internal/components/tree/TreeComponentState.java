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
package com.qcadoo.view.internal.components.tree;

import static com.qcadoo.model.api.types.TreeType.NODE_NUMBER_FIELD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityOpResult;
import com.qcadoo.model.api.EntityTree;
import com.qcadoo.model.api.EntityTreeNode;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.expression.ExpressionUtils;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.types.TreeType;
import com.qcadoo.model.api.utils.TreeNumberingServiceImpl;
import com.qcadoo.model.internal.DetachedEntityTreeImpl;
import com.qcadoo.view.api.components.TreeComponent;
import com.qcadoo.view.internal.components.FieldComponentState;

public final class TreeComponentState extends FieldComponentState implements TreeComponent {

    public static final String JSON_SELECTED_ENTITY_ID = "selectedEntityId";

    public static final String JSON_BELONGS_TO_ENTITY_ID = "belongsToEntityId";

    public static final String JSON_ROOT_NODE_ID = "root";

    public static final String JSON_OPENED_NODES_ID = "openedNodes";

    public static final String JSON_TREE_STRUCTURE = "treeStructure";

    private static final String INITIAL_NODE_NUMBER_VALUE = "1";

    private static final String CHILDREN = "children";

    private final TreeEventPerformer eventPerformer = new TreeEventPerformer();

    private TreeNode rootNode;

    private List<Long> openedNodes;

    private Long selectedEntityId;

    private JSONArray treeStructure;

    private final FieldDefinition belongsToFieldDefinition;

    private Long belongsToEntityId;

    private final Map<String, TreeDataType> dataTypes;

    private Map<Long, Entity> nodes = new HashMap<Long, Entity>();

    public TreeComponentState(final FieldDefinition scopeField, final Map<String, TreeDataType> dataTypes,
            final TreeComponentPattern pattern) {
        super(pattern);
        belongsToFieldDefinition = scopeField;
        this.dataTypes = dataTypes;
        registerEvent("initialize", eventPerformer, "initialize");
        registerEvent("initializeAfterBack", eventPerformer, "initializeAfterBack");
        registerEvent("refresh", eventPerformer, "refresh");
        registerEvent("select", eventPerformer, "selectEntity");
        registerEvent("remove", eventPerformer, "removeSelectedEntity");
        registerEvent("customAction", eventPerformer, "customAction");
        registerEvent("save", eventPerformer, "save");
        registerEvent("clear", eventPerformer, "clear");
    }

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        super.initializeContent(json);

        if (json.has(JSON_SELECTED_ENTITY_ID) && !json.isNull(JSON_SELECTED_ENTITY_ID)) {
            selectedEntityId = json.getLong(JSON_SELECTED_ENTITY_ID);
        }
        if (json.has(JSON_BELONGS_TO_ENTITY_ID) && !json.isNull(JSON_BELONGS_TO_ENTITY_ID)) {
            belongsToEntityId = json.getLong(JSON_BELONGS_TO_ENTITY_ID);
        }
        if (json.has(JSON_OPENED_NODES_ID) && !json.isNull(JSON_OPENED_NODES_ID)) {
            JSONArray openNodesArray = json.getJSONArray(JSON_OPENED_NODES_ID);
            for (int i = 0; i < openNodesArray.length(); i++) {
                addOpenedNode(openNodesArray.getLong(i));
            }
        }

        if (json.has(JSON_TREE_STRUCTURE) && !json.isNull(JSON_TREE_STRUCTURE)) {
            treeStructure = json.getJSONArray(JSON_TREE_STRUCTURE);
        }

        if (belongsToEntityId == null) {
            setEnabled(false);
        }
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        if (rootNode == null) {
            reload();
        }

        JSONObject json = super.renderContent();
        json.put(JSON_SELECTED_ENTITY_ID, selectedEntityId);
        json.put(JSON_BELONGS_TO_ENTITY_ID, belongsToEntityId);

        if (openedNodes != null) {
            JSONArray openedNodesArray = new JSONArray();
            for (Long openedNodeId : openedNodes) {
                openedNodesArray.put(openedNodeId);
            }
            json.put(JSON_OPENED_NODES_ID, openedNodesArray);
        }
        if (rootNode != null) {
            json.put(JSON_ROOT_NODE_ID, rootNode.toJson());
        }
        return json;
    }

    @Override
    public void onFieldEntityIdChange(final Long fieldEntityId) {
        if (belongsToEntityId != null && !belongsToEntityId.equals(fieldEntityId)) {
            setSelectedEntityId(null);
        }
        this.belongsToEntityId = fieldEntityId;
        setEnabled(fieldEntityId != null);
    }

    public List<Long> getOpenedNodes() {
        return openedNodes;
    }

    public void setOpenedNodes(final List<Long> openedNodes) {
        this.openedNodes = openedNodes;
    }

    public void addOpenedNode(final Long nodeId) {
        if (openedNodes == null) {
            openedNodes = new LinkedList<Long>();
        }
        openedNodes.add(nodeId);
    }

    @Override
    public Object getFieldValue() {
        if (treeStructure == null) {
            return null;
        }

        if (treeStructure.length() == 0) {
            return new ArrayList<Entity>();
        }

        if (treeStructure.length() > 1) {
            addMessage("qcadooView.validate.field.error.multipleRoots", MessageType.FAILURE);
            return null;
        }

        Entity entity = belongsToFieldDefinition.getDataDefinition().get(belongsToEntityId);

        EntityTree tree = entity.getTreeField(belongsToFieldDefinition.getName());

        nodes = new HashMap<Long, Entity>();

        for (Entity node : tree) {
            node.setField(CHILDREN, new ArrayList<Entity>());
            node.setField("parent", null);
            nodes.put(node.getId(), node);
        }

        try {
            Entity parent = nodes.get(treeStructure.getJSONObject(0).getLong("id"));

            if (treeStructure.getJSONObject(0).has(CHILDREN)) {
                reorganize(parent, treeStructure.getJSONObject(0).getJSONArray(CHILDREN),
                        Lists.newLinkedList(Lists.newArrayList(INITIAL_NODE_NUMBER_VALUE)));
            }

            return Collections.singletonList(parent);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void reorganize(final Entity parent, final JSONArray childrens, final Deque<String> nodeNumberChain)
            throws JSONException {

        parent.setField(TreeType.NODE_NUMBER_FIELD, TreeNumberingServiceImpl.convertCollectionToString(nodeNumberChain));

        int charNumber = 0;
        for (int i = 0; i < childrens.length(); i++) {
            Deque<String> newNodeNumberBranch = Lists.newLinkedList(nodeNumberChain);

            if (childrens.length() == 1) {
                TreeNumberingServiceImpl.incrementLastChainNumber(newNodeNumberBranch);
            } else {
                TreeNumberingServiceImpl.incrementLastChainCharacter(newNodeNumberBranch, charNumber++);
            }

            Entity nodeEntity = nodes.get(childrens.getJSONObject(i).getLong("id"));
            ((List<Entity>) parent.getField(CHILDREN)).add(nodeEntity);
            if (childrens.getJSONObject(i).has(CHILDREN)) {
                reorganize(nodeEntity, childrens.getJSONObject(i).getJSONArray(CHILDREN), newNodeNumberBranch);
            }
        }
    }

    @Override
    public void setFieldValue(final Object value) {
        if (!(value instanceof EntityTree) || !checkIfTreeContainsEntity((EntityTree) value, selectedEntityId)) {
            setSelectedEntityId(null);
        }
        requestRender();
        requestUpdateState();
    }

    private boolean checkIfTreeContainsEntity(final EntityTree tree, final Long entityId) {
        if (entityId == null) {
            return false;
        }
        if (tree instanceof DetachedEntityTreeImpl) {
            return ((DetachedEntityTreeImpl) tree).checkIfTreeContainsEntity(entityId);
        }
        SearchCriteriaBuilder searchBuilder = tree.find().add(SearchRestrictions.idEq(entityId));
        return searchBuilder.list().getTotalNumberOfEntities() > 0;
    }

    /*
     * (non-Javadoc)
     * @see com.qcadoo.view.components.tree.ITreeComponentState#getSelectedEntityId()
     */
    @Override
    public Long getSelectedEntityId() {
        return selectedEntityId;
    }

    public void setSelectedEntityId(final Long selectedEntityId) {
        this.selectedEntityId = selectedEntityId;
        notifyEntityIdChangeListeners(parseSelectedIdForListeners(selectedEntityId));
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(final TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public void setRootNode(final EntityTreeNode rootNodeEntity) {
        if (rootNodeEntity == null) {
            return;
        }
        rootNode = createNode(rootNodeEntity, Lists.newLinkedList(Lists.newArrayList(INITIAL_NODE_NUMBER_VALUE)));
        if (openedNodes == null) {
            addOpenedNode(rootNode.getId());
        }

    }

    private Long parseSelectedIdForListeners(final Long selectedEntityId) {
        if (selectedEntityId == null || selectedEntityId == 0) {
            return null;
        }
        return selectedEntityId;
    }

    private void reload() {
        if (belongsToEntityId == null) {
            return;
        }
        Entity entity = belongsToFieldDefinition.getDataDefinition().get(belongsToEntityId);

        EntityTree tree = entity.getTreeField(belongsToFieldDefinition.getName());

        if (tree == null || tree.getRoot() == null) {
            return;
        }

        rootNode = createNode(tree.getRoot(), Lists.newLinkedList(Lists.newArrayList(INITIAL_NODE_NUMBER_VALUE)));
        if (openedNodes == null) {
            addOpenedNode(rootNode.getId());
        }
    }

    private TreeNode createNode(final EntityTreeNode entityTreeNode, final Deque<String> nodeNumberChain) {
        entityTreeNode.setField(NODE_NUMBER_FIELD, TreeNumberingServiceImpl.convertCollectionToString(nodeNumberChain));

        List<EntityTreeNode> childs = entityTreeNode.getChildren();
        TreeDataType entityType = dataTypes.get(entityTreeNode.getEntityNoteType());
        String nodeLabel = ExpressionUtils.getValue(entityTreeNode, entityType.getNodeLabelExpression(), getLocale());
        TreeNode node = new TreeNode(entityTreeNode.getId(), nodeLabel, entityType);

        int charNumber = 0;
        for (EntityTreeNode childEntityTreeNode : childs) {
            Deque<String> newNodeNumberBranch = Lists.newLinkedList(nodeNumberChain);
            if (childs.size() == 1) {
                TreeNumberingServiceImpl.incrementLastChainNumber(newNodeNumberBranch);
            } else {
                TreeNumberingServiceImpl.incrementLastChainCharacter(newNodeNumberBranch, charNumber++);
            }
            node.addChild(createNode(childEntityTreeNode, newNodeNumberBranch));
        }
        return node;
    }

    protected class TreeEventPerformer {

        public void refresh(final String[] args) {
            // nothing interesting here
        }

        public void initialize(final String[] args) {
            if (rootNode != null) {
                addOpenedNode(rootNode.getId());
            }
            setSelectedEntityId(null);
            requestRender();
            requestUpdateState();
        }

        public void initializeAfterBack(final String[] args) {
            if (rootNode != null) {
                addOpenedNode(rootNode.getId());
            }
            requestRender();
            requestUpdateState();
        }

        public void selectEntity(final String[] args) {
            notifyEntityIdChangeListeners(parseSelectedIdForListeners(getSelectedEntityId()));
        }

        public void customAction(final String[] args) {
        }

        public void removeSelectedEntity(final String[] args) {
            EntityOpResult result = getDataDefinition().delete(selectedEntityId);
            if (result.isSuccessfull()) {
                setSelectedEntityId(null);
                addTranslatedMessage(translateMessage("deleteMessage"), MessageType.SUCCESS);
                requestRender();
                requestUpdateState();
            } else {
                copyMessages(result.getMessagesHolder().getGlobalErrors());
            }
            copyGlobalMessages(result.getMessagesHolder().getGlobalMessages());
        }

        public void save(final String[] args) {
            Object tree = getFieldValue();

            if (tree == null) {
                return;
            }

            Entity entity = belongsToFieldDefinition.getDataDefinition().get(belongsToEntityId);

            entity.setField(belongsToFieldDefinition.getName(), tree);
            Entity afterSaveEntity = belongsToFieldDefinition.getDataDefinition().save(entity);

            if (afterSaveEntity.isValid()) {
                requestRender();
                requestUpdateState();
                addTranslatedMessage(translateMessage("saveMessage"), MessageType.SUCCESS);
            }
        }

        public void clear(final String[] args) {
            rootNode = null;
            requestRender();
            requestUpdateState();
        }

    }

}
