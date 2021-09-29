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

import org.hibernate.criterion.Subqueries;

import com.qcadoo.model.internal.search.SearchCriteria;
import com.qcadoo.model.internal.search.SearchCriterionImpl;

/**
 * Utility with factory methods for {@link SearchCriterion} related with subqueries.
 * 
 * @since 0.4.1
 */
public final class SearchSubqueries {

    private SearchSubqueries() {
    }

    /**
     * Creates criterion which checks if none row exists in given subquery.
     * 
     * @param criteria
     *            subcriteria
     * @return criteria
     */
    public static SearchCriterion notExists(final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.notExists(((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    /**
     * Creates criterion which checks if any row exists in given subquery.
     * 
     * @param criteria
     *            subcriteria
     * @return criteria
     */
    public static SearchCriterion exists(final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.exists(((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    /**
     * Creates criterion which checks if given value exists in given subquery.
     * 
     * @param value
     *            value
     * @param criteria
     *            subcriteria
     * @return criteria
     */
    public static SearchCriterion in(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.in(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    /**
     * Creates criterion which checks if given field's value exists in given subquery.
     * 
     * @param field
     *            field
     * @param criteria
     *            subcriteria
     * @return criteria
     */
    public static SearchCriterion fieldIn(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(
                Subqueries.propertyIn(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    /**
     * Creates criterion which checks if given value doesn't exist in given subquery.
     * 
     * @param value
     *            value
     * @param criteria
     *            subcriteria
     * @return criteria
     */
    public static SearchCriterion notIn(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.notIn(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    /**
     * Creates criterion which checks if given field's value doesn't exist in given subquery.
     * 
     * @param field
     *            field
     * @param criteria
     *            subcriteria
     * @return criteria
     */
    public static SearchCriterion fieldNotIn(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(
                Subqueries.propertyNotIn(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion eq(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.eq(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion eqAll(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.eqAll(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldEq(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyEq(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldEqAll(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(
                Subqueries.propertyEqAll(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion ge(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.ge(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion geAll(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.geAll(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion geSome(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.geSome(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldGe(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyGe(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldGeAll(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(
                Subqueries.propertyGeAll(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldGeSome(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyGeSome(field,
                ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion gt(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.gt(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion gtAll(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.gtAll(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion gtSome(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.gtSome(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldGt(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyGt(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldGtAll(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(
                Subqueries.propertyGtAll(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldGtSome(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyGtSome(field,
                ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion lt(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.lt(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion ltAll(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.ltAll(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion ltSome(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.ltSome(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldLt(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyLt(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldLtAll(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(
                Subqueries.propertyLtAll(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldLtSome(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyLtSome(field,
                ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion le(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.le(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion leAll(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.leAll(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion leSome(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.leSome(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldLe(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyLe(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldLeAll(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(
                Subqueries.propertyLeAll(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldLeSome(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyLeSome(field,
                ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion ne(final Object value, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.ne(value, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

    public static SearchCriterion fieldNe(final String field, final SearchCriteriaBuilder criteria) {
        return new SearchCriterionImpl(Subqueries.propertyNe(field, ((SearchCriteria) criteria).getHibernateDetachedCriteria()));
    }

}
