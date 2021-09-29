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
package com.qcadoo.model.api.search;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.internal.api.DataAccessService;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.search.InExpressionIgnoringCase;
import com.qcadoo.model.internal.search.SearchConjunctionImpl;
import com.qcadoo.model.internal.search.SearchCriterionImpl;
import com.qcadoo.model.internal.search.SearchDisjunctionImpl;

/**
 * Utility with factory methods for {@link SearchCriterion}.
 * 
 * @since 0.4.1
 */
@Component
public final class SearchRestrictions {

    private static DataAccessService dataAccessService;

    private static final String[] QCADOO_WILDCARDS = new String[] { "*", "?" };

    private static final String[] HIBERNATE_WILDCARDS = new String[] { "%", "_" };

    private static void setStaticDataAccessService(final DataAccessService dataAccessService) {
        SearchRestrictions.dataAccessService = dataAccessService;
    }

    @Autowired
    protected void setDataAccessService(final DataAccessService dataAccessService) {
        SearchRestrictions.setStaticDataAccessService(dataAccessService);
    }

    /**
     * Match mode for "like" criterion.
     */
    public static enum SearchMatchMode {

        /**
         * Match anywhere.
         */
        ANYWHERE(MatchMode.ANYWHERE),

        /**
         * Match at the end.
         */
        END(MatchMode.END),

        /**
         * Match exact value.
         */
        EXACT(MatchMode.EXACT),

        /**
         * Match at the beginning.
         */
        START(MatchMode.START);

        private final MatchMode matchMode;

        private SearchMatchMode(final MatchMode matchMode) {
            this.matchMode = matchMode;
        }

        public MatchMode getHibernateMatchMode() {
            return matchMode;
        }

    }

    /**
     * Creates restriction which join given restrictions with "OR" operator.
     * 
     * @param firstCriterion
     *            first criterion
     * @param secondCriterion
     *            second criterion
     * @param otherCriteria
     *            other criteria
     * @return criterion
     */
    public static SearchCriterion or(final SearchCriterion firstCriterion, final SearchCriterion secondCriterion,
            final SearchCriterion... otherCriteria) {
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(firstCriterion.getHibernateCriterion());
        disjunction.add(secondCriterion.getHibernateCriterion());

        for (SearchCriterion criterion : otherCriteria) {
            disjunction.add(criterion.getHibernateCriterion());
        }

        return new SearchCriterionImpl(disjunction);
    }

    /**
     * Creates restriction which join given restrictions with "AND" operator.
     * 
     * @param firstCriterion
     *            first criterion
     * @param secondCriterion
     *            second criterion
     * @param otherCriteria
     *            other criteria
     * @return criterion
     */
    public static SearchCriterion and(final SearchCriterion firstCriterion, final SearchCriterion secondCriterion,
            final SearchCriterion... otherCriteria) {
        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add(firstCriterion.getHibernateCriterion());
        conjunction.add(secondCriterion.getHibernateCriterion());

        for (SearchCriterion criterion : otherCriteria) {
            conjunction.add(criterion.getHibernateCriterion());
        }

        return new SearchCriterionImpl(conjunction);
    }

    /**
     * Wraps given criterion with "not" criterion.
     * 
     * @param criterion
     *            criterion
     * @return negated criterion
     */
    public static SearchCriterion not(final SearchCriterion criterion) {
        return new SearchCriterionImpl(Restrictions.not(criterion.getHibernateCriterion()));
    }

    /**
     * Creates criterion which checks if id is equal to given value.
     * 
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion idEq(final long value) {
        return new SearchCriterionImpl(Restrictions.idEq(value));
    }

    /**
     * Creates criterion which checks if id isn't equal to given value.
     * 
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion idNe(final long value) {
        return new SearchCriterionImpl(Restrictions.not(Restrictions.idEq(value)));
    }

    /**
     * Creates criterion which checks if all given fields match given values.
     * 
     * @param values
     *            map where key is a field's name and value is the expected value
     * @return criterion
     */
    public static SearchCriterion allEq(final Map<String, Object> values) {
        return new SearchCriterionImpl(Restrictions.allEq(values));
    }

    /**
     * Creates criterion which checks if field is equal (using "like" operator) to given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion like(final String field, final String value) {
        if (value == null) {
            return isNull(field);
        }
        return new SearchCriterionImpl(Restrictions.like(field, convertWildcards(value)));
    }

    /**
     * Creates criterion which checks if field is equal (using "like" operator) to given value.
     * 
     * @param field
     *            field
     * @param mode
     *            match mode
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion like(final String field, final String value, final SearchMatchMode mode) {
        if (value == null) {
            return isNull(field);
        }
        return new SearchCriterionImpl(Restrictions.like(field, convertWildcards(value), mode.getHibernateMatchMode()));
    }

    /**
     * Creates criterion which checks if field is equal (using case-insensitive "like" operator) to given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion ilike(final String field, final String value) {
        if (value == null) {
            return isNull(field);
        }
        return new SearchCriterionImpl(Restrictions.ilike(field, convertWildcards(value)));
    }

    /**
     * Creates criterion which checks if field is equal (using case-insensitive "like" operator) to given value.
     * 
     * @param field
     *            field
     * @param mode
     *            match mode
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion ilike(final String field, final String value, final SearchMatchMode mode) {
        if (value == null) {
            return isNull(field);
        }
        return new SearchCriterionImpl(Restrictions.ilike(field, convertWildcards(value), mode.getHibernateMatchMode()));
    }

    private static String convertWildcards(final String value) {
        return StringUtils.replaceEach(value, QCADOO_WILDCARDS, HIBERNATE_WILDCARDS);
    }

    /**
     * Creates criterion which checks if field is less than or equal to given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion le(final String field, final Object value) {
        return new SearchCriterionImpl(Restrictions.le(field, value));
    }

    /**
     * Creates criterion which checks if field is less than given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion lt(final String field, final Object value) {
        return new SearchCriterionImpl(Restrictions.lt(field, value));
    }

    /**
     * Creates criterion which checks if field is greater than or equal to given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion ge(final String field, final Object value) {
        return new SearchCriterionImpl(Restrictions.ge(field, value));
    }

    /**
     * Creates criterion which checks if field is greater than given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion gt(final String field, final Object value) {
        return new SearchCriterionImpl(Restrictions.gt(field, value));
    }

    /**
     * Creates criterion which checks if field isn't equal to given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion ne(final String field, final Object value) {
        return new SearchCriterionImpl(Restrictions.ne(field, value));
    }

    /**
     * Creates criterion which checks if field is equal to given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion eq(final String field, final Object value) {
        return new SearchCriterionImpl(Restrictions.eq(field, value));
    }

    /**
     * Creates criterion which checks if field is equal (using case-insensitive "eq" operator) to given value.
     * 
     * @param field
     *            field
     * @param value
     *            value
     * @return criterion
     */
    public static SearchCriterion iEq(final String field, final Object value) {
        return new SearchCriterionImpl(Restrictions.eq(field, value).ignoreCase());
    }

    /**
     * Creates criterion which checks if field is less than or equal to other field.
     * 
     * @param field
     *            field
     * @param otherField
     *            other field
     * @return criterion
     */
    public static SearchCriterion leField(final String field, final String otherField) {
        return new SearchCriterionImpl(Restrictions.leProperty(field, otherField));
    }

    /**
     * Creates criterion which checks if field is less than other field.
     * 
     * @param field
     *            field
     * @param otherField
     *            other field
     * @return criterion
     */
    public static SearchCriterion ltField(final String field, final String otherField) {
        return new SearchCriterionImpl(Restrictions.ltProperty(field, otherField));
    }

    /**
     * Creates criterion which checks if field is greater than or equal to other field.
     * 
     * @param field
     *            field
     * @param otherField
     *            other field
     * @return criterion
     */
    public static SearchCriterion geField(final String field, final String otherField) {
        return new SearchCriterionImpl(Restrictions.geProperty(field, otherField));
    }

    /**
     * Creates criterion which checks if field is greater than other field.
     * 
     * @param field
     *            field
     * @param otherField
     *            other field
     * @return criterion
     */
    public static SearchCriterion gtField(final String field, final String otherField) {
        return new SearchCriterionImpl(Restrictions.gtProperty(field, otherField));
    }

    /**
     * Creates criterion which checks if field isn't equal to other field.
     * 
     * @param field
     *            field
     * @param otherField
     *            other field
     * @return criterion
     */
    public static SearchCriterion neField(final String field, final String otherField) {
        return new SearchCriterionImpl(Restrictions.neProperty(field, otherField));
    }

    /**
     * Creates criterion which checks if field is equal to other field.
     * 
     * @param field
     *            field
     * @param otherField
     *            other field
     * @return criterion
     */
    public static SearchCriterion eqField(final String field, final String otherField) {
        return new SearchCriterionImpl(Restrictions.eqProperty(field, otherField));
    }

    /**
     * Creates criterion which checks if field is not null.
     * 
     * @param field
     *            field
     * @return criterion
     */
    public static SearchCriterion isNotNull(final String field) {
        return new SearchCriterionImpl(Restrictions.isNotNull(field));
    }

    /**
     * Creates criterion which checks if field is null.
     * 
     * @param field
     *            field
     * @return criterion
     */
    public static SearchCriterion isNull(final String field) {
        return new SearchCriterionImpl(Restrictions.isNull(field));
    }

    /**
     * Creates criterion which checks if "collection" field's size is equal to given size.
     * 
     * @param field
     *            field
     * @param size
     *            size
     * @return criterion
     */
    public static SearchCriterion sizeEq(final String field, final int size) {
        return new SearchCriterionImpl(Restrictions.sizeEq(field, size));
    }

    /**
     * Creates criterion which checks if "collection" field's size is less than or equal to given size.
     * 
     * @param field
     *            field
     * @param size
     *            size
     * @return criterion
     */
    public static SearchCriterion sizeLe(final String field, final int size) {
        return new SearchCriterionImpl(Restrictions.sizeLe(field, size));
    }

    /**
     * Creates criterion which checks if "collection" field's size is less than given size.
     * 
     * @param field
     *            field
     * @param size
     *            size
     * @return criterion
     */
    public static SearchCriterion sizeLt(final String field, final int size) {
        return new SearchCriterionImpl(Restrictions.sizeLt(field, size));
    }

    /**
     * Creates criterion which checks if "collection" field's size is greater than or equal to given size.
     * 
     * @param field
     *            field
     * @param size
     *            size
     * @return criterion
     */
    public static SearchCriterion sizeGe(final String field, final int size) {
        return new SearchCriterionImpl(Restrictions.sizeGe(field, size));
    }

    /**
     * Creates criterion which checks if "collection" field's size is greater than given size.
     * 
     * @param field
     *            field
     * @param size
     *            size
     * @return criterion
     */
    public static SearchCriterion sizeGt(final String field, final int size) {
        return new SearchCriterionImpl(Restrictions.sizeGt(field, size));
    }

    /**
     * Creates criterion which checks if "collection" field's size isn't equal to given size.
     * 
     * @param field
     *            field
     * @param size
     *            size
     * @return criterion
     */
    public static SearchCriterion sizeNe(final String field, final int size) {
        return new SearchCriterionImpl(Restrictions.sizeNe(field, size));
    }

    /**
     * Creates criterion which checks if "collection" field's size is empty.
     * 
     * @param field
     *            field
     * @return criterion
     */
    public static SearchCriterion isEmpty(final String field) {
        return new SearchCriterionImpl(Restrictions.isEmpty(field));
    }

    /**
     * Creates criterion which checks if "collection" field's size isn't empty.
     * 
     * @param field
     *            field
     * @return criterion
     */
    public static SearchCriterion isNotEmpty(final String field) {
        return new SearchCriterionImpl(Restrictions.isNotEmpty(field));
    }

    /**
     * Creates criterion which checks if field is between given values.
     * 
     * @param field
     *            field
     * @param lo
     *            low value
     * @param hi
     *            high value
     * @return criterion
     */
    public static SearchCriterion between(final String field, final Object lo, final Object hi) {
        return new SearchCriterionImpl(Restrictions.between(field, lo, hi));
    }

    /**
     * Creates criterion which checks if field is in given values.
     * 
     * @param field
     *            field
     * @param values
     *            values
     * @return criterion
     */
    public static SearchCriterion in(final String field, final Collection<?> values) {
        return new SearchCriterionImpl(Restrictions.in(field, values));
    }

    /**
     * Creates criterion which checks if field is in given values ignoring case.
     *
     * @param field
     *            field
     * @param values
     *            values
     * @return criterion
     */
    public static SearchCriterion inIgnoringCase(final String field, final Collection<?> values) {
        return new SearchCriterionImpl(new InExpressionIgnoringCase(field, values.toArray()));
    }

    /**
     * Creates criterion which checks if field is in given values.
     * 
     * @param field
     *            field
     * @param values
     *            values
     * @return criterion
     */
    public static SearchCriterion in(final String field, final Object... values) {
        return new SearchCriterionImpl(Restrictions.in(field, values));
    }

    /**
     * Creates criterion which checks if "belongsTo" field is equal to given entity.
     * 
     * Current implementation of this method performs Entity loading and may cause StackOverflowError when used (for example)
     * within onView hook.
     * 
     * Therefore prefer belongsTo(String, Entity) and use this method only in case when you can't obtain reference to a whole
     * Entity.
     * 
     * @param field
     *            field
     * @param pluginIdentifier
     *            plugin's identifier
     * @param modelName
     *            model's name
     * @param id
     *            id
     * @return criterion
     */
    public static SearchCriterion belongsTo(final String field, final String pluginIdentifier, final String modelName,
            final long id) {
        return belongsTo(field, dataAccessService.getDataDefinition(pluginIdentifier, modelName), id);
    }

    /**
     * Creates criterion which checks if "belongsTo" field is equal to given entity.
     * 
     * Current implementation of this method performs Entity loading and may cause StackOverflowError when used (for example)
     * within onView hook.
     * 
     * Therefore prefer belongsTo(String, Entity) and use this method only in case when you can't obtain reference to a whole
     * Entity.
     * 
     * @param field
     *            field
     * @param dataDefinition
     *            data's definition
     * @param id
     *            id
     * @return criterion
     */
    public static SearchCriterion belongsTo(final String field, final DataDefinition dataDefinition, final long id) {
        return belongsTo(field, dataAccessService.get((InternalDataDefinition) dataDefinition, id));
    }

    /**
     * Creates criterion which checks if "belongsTo" field is equal to given entity.
     * 
     * @param field
     *            field
     * @param entity
     *            entity
     * @return criterion
     */
    public static SearchCriterion belongsTo(final String field, final Entity entity) {
        Object databaseEntity = null;

        if (entity != null) {
            databaseEntity = dataAccessService.convertToDatabaseEntity(entity);
        }

        if (databaseEntity == null) {
            return isNull(field);
        } else {
            return eq(field, databaseEntity);
        }
    }

    /**
     * Creates disjunction - (... OR ... OR ...).
     * 
     * @return disjunction
     */
    public static SearchDisjunction disjunction() {
        return new SearchDisjunctionImpl();
    }

    /**
     * Creates conjunction - (... AND ... AND ...).
     * 
     * @return conjunction
     */
    public static SearchConjunction conjunction() {
        return new SearchConjunctionImpl();
    }

}
