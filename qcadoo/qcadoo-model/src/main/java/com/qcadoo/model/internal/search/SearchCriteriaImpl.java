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
package com.qcadoo.model.internal.search;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.JoinType;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchOrder;
import com.qcadoo.model.api.search.SearchOrders;
import com.qcadoo.model.api.search.SearchProjection;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.DetachedCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SearchCriteriaImpl implements SearchCriteriaBuilder, SearchCriteria {

    private static final String L_PLEASE_USE_NEW_CRITERIA_API = "Please use new criteria API";

    private final DataDefinition sourceDataDefinition;

    private final DetachedCriteria criteria;

    private int maxResults = Integer.MAX_VALUE;

    private int firstResult;

    private final List<SearchOrder> orders = new ArrayList<SearchOrder>();

    private boolean hasProjection;

    private final Map<String, String> aliases = new HashMap<String, String>();

    private boolean cacheable = false;

    public SearchCriteriaImpl(final DataDefinition dataDefinition) {
        checkNotNull(dataDefinition);
        sourceDataDefinition = dataDefinition;
        criteria = DetachedCriteria.forEntityName(((InternalDataDefinition) dataDefinition).getFullyQualifiedClassName());
    }

    public SearchCriteriaImpl(final DataDefinition dataDefinition, final String alias) {
        checkNotNull(dataDefinition);
        sourceDataDefinition = dataDefinition;
        criteria = DetachedCriteria.forEntityName(((InternalDataDefinition) dataDefinition).getFullyQualifiedClassName(), alias);
    }

    private SearchCriteriaImpl(final DetachedCriteria subcriteria) {
        sourceDataDefinition = null;
        criteria = subcriteria;
    }

    @Override
    public DataDefinition getDataDefinition() {
        if (hasProjection) {
            return null;
        }
        return sourceDataDefinition;
    }

    @Override
    public DetachedCriteria getHibernateDetachedCriteria() {
        return criteria;
    }

    @Override
    public SearchResult list() {
        return ((InternalDataDefinition) sourceDataDefinition).find(this);
    }

    @Override
    public Entity uniqueResult() {
        SearchResult results = list();

        if (results.getEntities().isEmpty()) {
            return null;
        } else if (results.getEntities().size() == 1) {
            return results.getEntities().get(0);
        } else {
            throw new IllegalStateException("Too many results, expected one, found " + results.getEntities().size());
        }
    }

    @Override
    public Criteria createCriteria(final Session session) {
        Criteria executableCriteria = criteria.getExecutableCriteria(session);

        return executableCriteria;
    }

    @Override
    public void addFirstAndMaxResults(final Criteria criteria) {
        criteria.setMaxResults(maxResults).setFirstResult(firstResult);
    }

    @Override
    public void addCacheable(Criteria criteria) {
        if (cacheable) {
            criteria.setCacheable(cacheable);
        }
    }

    @Override
    public void addOrders(final Criteria criteria) {
        if (orders.isEmpty()) {
            if (sourceDataDefinition != null && sourceDataDefinition.isPrioritizable()) {
                criteria.addOrder(org.hibernate.criterion.Order.asc(sourceDataDefinition.getPriorityField().getName()));
            } else {
                criteria.addOrder(org.hibernate.criterion.Order.asc("id"));
            }
        } else {
            for (SearchOrder order : orders) {
                criteria.addOrder(order.getHibernateOrder());
            }
        }
    }

    @Override
    public SearchCriteriaBuilder setMaxResults(final int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public SearchCriteriaBuilder setCacheable(final boolean cacheable) {
        this.cacheable = cacheable;
        return this;
    }

    @Override
    public SearchCriteriaBuilder setFirstResult(final int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    @Override
    public SearchCriteriaBuilder setProjection(final SearchProjection projection) {
        criteria.setProjection(projection.getHibernateProjection());
        hasProjection = true;
        return this;
    }

    @Override
    public SearchCriteriaBuilder add(final SearchCriterion criterion) {
        criteria.add(criterion.getHibernateCriterion());
        return this;
    }

    @Override
    public SearchCriteriaBuilder addOrder(final SearchOrder order) {
        orders.add(order);
        return this;
    }

    @Override
    public SearchCriteriaBuilder createAlias(final String associationPath, final String alias) {
        return createAlias(associationPath, alias, null);
    }

    @Override
    public SearchCriteriaBuilder createAlias(final String associationPath, final String alias, final JoinType joinType) {
        if (aliases.containsKey(alias)) {
            if (!associationPath.equals(aliases.get(alias))) {
                throw new IllegalStateException("Cannot register alias " + alias + " for " + associationPath
                        + ", already exists for " + aliases.get(alias));
            }
        } else {
            criteria.createAlias(associationPath, alias, getIntValueForJoinType(joinType));
            aliases.put(alias, associationPath);
        }
        return this;
    }

    @Override
    public SearchCriteriaBuilder createCriteria(final String associationPath, final String alias) {
        return createCriteria(associationPath, alias, null);
    }

    @Override
    public SearchCriteriaBuilder createCriteria(final String associationPath, final String alias, final JoinType joinType) {
        DetachedCriteria subcriteria = criteria.createCriteria(associationPath, alias, getIntValueForJoinType(joinType));
        return new SearchCriteriaImpl(subcriteria);
    }

    @Override
    public boolean existsAliasForAssociation(String association) {
        return aliases.containsValue(association);
    }

    @Override public String getAliasForAssociation(String association) {
        for(Map.Entry<String, String> entry: aliases.entrySet()) {
            if(association.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Cannot find alias for " + association);
    }

    private int getIntValueForJoinType(final JoinType joinType) {
        if (joinType == null) {
            return JoinType.INNER.getIntValue();
        }
        return joinType.getIntValue();
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public SearchCriteriaBuilder like(final String fieldName, final String value) {
        return add(SearchRestrictions.like(addAliasIfNessecary(fieldName), value));
    }

    @Override
    public SearchCriteriaBuilder isEq(final String fieldName, final Object value) {
        if (isLikeRestriction(value)) {
            return add(SearchRestrictions.like(addAliasIfNessecary(fieldName), (String) value));
        }

        return add(SearchRestrictions.eq(addAliasIfNessecary(fieldName), value));
    }

    private boolean isLikeRestriction(final Object value) {
        return value instanceof String && ((String) value).matches(".*[\\*%\\?_].*");
    }

    @Override
    public SearchCriteriaBuilder isLe(final String fieldName, final Object value) {
        return add(SearchRestrictions.le(addAliasIfNessecary(fieldName), value));
    }

    @Override
    public SearchCriteriaBuilder isLt(final String fieldName, final Object value) {
        return add(SearchRestrictions.lt(addAliasIfNessecary(fieldName), value));
    }

    @Override
    public SearchCriteriaBuilder isGe(final String fieldName, final Object value) {
        return add(SearchRestrictions.ge(addAliasIfNessecary(fieldName), value));
    }

    @Override
    public SearchCriteriaBuilder isGt(final String fieldName, final Object value) {
        return add(SearchRestrictions.gt(addAliasIfNessecary(fieldName), value));
    }

    @Override
    public SearchCriteriaBuilder isNe(final String fieldName, final Object value) {
        if (isLikeRestriction(value)) {
            return add(SearchRestrictions.not(SearchRestrictions.like(addAliasIfNessecary(fieldName), (String) value)));
        }

        return add(SearchRestrictions.ne(addAliasIfNessecary(fieldName), value));
    }

    @Override
    public SearchCriteriaBuilder isNotNull(final String fieldName) {
        return add(SearchRestrictions.isNotNull(addAliasIfNessecary(fieldName)));
    }

    @Override
    public SearchCriteriaBuilder isNull(final String fieldName) {
        return add(SearchRestrictions.isNull(addAliasIfNessecary(fieldName)));
    }

    @Override
    public SearchCriteriaBuilder openNot() {
        throw new UnsupportedOperationException(L_PLEASE_USE_NEW_CRITERIA_API);
    }

    @Override
    public SearchCriteriaBuilder closeNot() {
        throw new UnsupportedOperationException(L_PLEASE_USE_NEW_CRITERIA_API);
    }

    @Override
    public SearchCriteriaBuilder openOr() {
        throw new UnsupportedOperationException(L_PLEASE_USE_NEW_CRITERIA_API);
    }

    @Override
    public SearchCriteriaBuilder closeOr() {
        throw new UnsupportedOperationException(L_PLEASE_USE_NEW_CRITERIA_API);
    }

    @Override
    public SearchCriteriaBuilder openAnd() {
        throw new UnsupportedOperationException(L_PLEASE_USE_NEW_CRITERIA_API);
    }

    @Override
    public SearchCriteriaBuilder closeAnd() {
        throw new UnsupportedOperationException(L_PLEASE_USE_NEW_CRITERIA_API);
    }

    @Override
    public SearchCriteriaBuilder belongsTo(final String fieldName, final Object entityOrId) {
        if (entityOrId instanceof Entity) {
            return add(SearchRestrictions.belongsTo(addAliasIfNessecary(fieldName), (Entity) entityOrId));
        } else if (entityOrId == null) {
            return add(SearchRestrictions.isNull(addAliasIfNessecary(fieldName)));
        } else {
            return add(SearchRestrictions.belongsTo(addAliasIfNessecary(fieldName),
                    getDataDefinitionBelongsToForField(fieldName), (Long) entityOrId));
        }
    }

    @Override
    public SearchCriteriaBuilder isIdEq(final Long id) {
        return add(SearchRestrictions.eq("id", id));
    }

    @Override
    public SearchCriteriaBuilder isIdLe(final Long id) {
        return add(SearchRestrictions.le("id", id));
    }

    @Override
    public SearchCriteriaBuilder isIdLt(final Long id) {
        return add(SearchRestrictions.lt("id", id));
    }

    @Override
    public SearchCriteriaBuilder isIdGe(final Long id) {
        return add(SearchRestrictions.ge("id", id));
    }

    @Override
    public SearchCriteriaBuilder isIdGt(final Long id) {
        return add(SearchRestrictions.gt("id", id));
    }

    @Override
    public SearchCriteriaBuilder isIdNe(final Long id) {
        return add(SearchRestrictions.ne("id", id));
    }

    @Override
    public SearchCriteriaBuilder orderAscBy(final String fieldName) {
        return addOrder(SearchOrders.asc(addAliasIfNessecary(fieldName)));
    }

    @Override
    public SearchCriteriaBuilder orderDescBy(final String fieldName) {
        return addOrder(SearchOrders.desc(addAliasIfNessecary(fieldName)));
    }

    private DataDefinition getDataDefinitionBelongsToForField(final String field) {
        String[] path = field.split("\\.");

        DataDefinition parentDataDefinition = sourceDataDefinition;

        for (int i = 0; i < path.length; i++) {
            parentDataDefinition = ((BelongsToType) parentDataDefinition.getField(path[i]).getType()).getDataDefinition();
        }

        return parentDataDefinition;
    }

    private int aliasIndex = 1;

    private String addAliasIfNessecary(final String field) {
        String[] path = field.split("\\.");

        if (path.length == 1) {
            return field;
        } else {
            String lastAlias = "";

            for (int i = 0; i < path.length - 1; i++) {
                createAlias(lastAlias + path[i], path[i] + "_a" + aliasIndex);
                lastAlias = path[i] + "_a" + aliasIndex + ".";
                aliasIndex++;
            }

            return lastAlias + path[path.length - 1];
        }
    }

}
