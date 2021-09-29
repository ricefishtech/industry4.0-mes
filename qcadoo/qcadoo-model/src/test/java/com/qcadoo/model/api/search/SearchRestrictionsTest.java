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

import static org.junit.Assert.assertEquals;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import com.qcadoo.model.api.search.SearchRestrictions.SearchMatchMode;

public class SearchRestrictionsTest {

    private static final String FIELD_NAME = "someField";

    private static final Criterion IS_NULL = Restrictions.isNull(FIELD_NAME);

    @Test
    public final void shouldIlikeReturnIsNullCriteriaIfGivenValueIsNull() {
        // when
        SearchCriterion res = SearchRestrictions.ilike(FIELD_NAME, null);

        // then
        assertEquals(IS_NULL.toString(), res.getHibernateCriterion().toString());
    }

    @Test
    public final void shouldIlikeWithMatchModeReturnIsNullCriteriaIfGivenValueIsNull() {
        // when
        SearchCriterion res = SearchRestrictions.ilike(FIELD_NAME, null, SearchMatchMode.ANYWHERE);

        // then
        assertEquals(IS_NULL.toString(), res.getHibernateCriterion().toString());
    }

    @Test
    public final void shouldLikeReturnIsNullCriteriaIfGivenValueIsNull() {
        // when
        SearchCriterion res = SearchRestrictions.like(FIELD_NAME, null);

        // then
        assertEquals(IS_NULL.toString(), res.getHibernateCriterion().toString());
    }

    @Test
    public final void shouldLikeWithMatchModeReturnIsNullCriteriaIfGivenValueIsNull() {
        // when
        SearchCriterion res = SearchRestrictions.like(FIELD_NAME, null, SearchMatchMode.ANYWHERE);

        // then
        assertEquals(IS_NULL.toString(), res.getHibernateCriterion().toString());
    }

    @Test
    public final void shouldReplaceQcadooWilcardWithHibernateOne() {
        performWildcardTest("*", "%");
        performWildcardTest("test*", "test%");
        performWildcardTest("*test*", "%test%");
        performWildcardTest("*test", "%test");
        performWildcardTest("te*st", "te%st");

        performWildcardTest("?", "_");
        performWildcardTest("test?", "test_");
        performWildcardTest("?test?", "_test_");
        performWildcardTest("?test", "_test");
        performWildcardTest("te?st", "te_st");

        performWildcardTest("*test?", "%test_");
        performWildcardTest("?test*", "_test%");
        performWildcardTest("?te*st", "_te%st");
        performWildcardTest("*te?st", "%te_st");
    }

    private void performWildcardTest(final String input, final String expectedOutput) {
        SearchCriterion res = SearchRestrictions.like(FIELD_NAME, input);
        String actual = res.getHibernateCriterion().toString();
        String expected = FIELD_NAME + " like " + expectedOutput;
        assertEquals(expected, actual);
    }

}
