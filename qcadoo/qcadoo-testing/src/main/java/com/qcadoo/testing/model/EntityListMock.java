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
package com.qcadoo.testing.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.mockito.Mockito;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;

/**
 * EntityList mock class, useful for unit testing.
 * 
 * @since 1.2.1
 */
public class EntityListMock extends LinkedList<Entity> implements EntityList {

    /**
     * By deafult each invocation of find will return unique instance of SearchCriteriaBuilder mock
     */
    private static final Supplier<SearchCriteriaBuilder> CRITERIA_MOCK_SUPPLIER = new Supplier<SearchCriteriaBuilder>() {

        @Override
        public SearchCriteriaBuilder get() {
            return Mockito.mock(SearchCriteriaBuilder.class);
        }
    };

    private final Supplier<SearchCriteriaBuilder> criteriaBuilderFactory;

    /**
     * Create a new instance of an empty EntityList mock.
     * 
     * @return a new instance of an EntityList mock
     */
    public static EntityList create() {
        return create(Collections.<Entity> emptyList());
    }

    /**
     * Create a new mock instance of an EntityList, containing given values.
     * 
     * @param elements
     *            content of new mock
     * @return a new instance of an EntityList mock
     */
    public static EntityList create(final Collection<Entity> elements) {
        return create(elements, CRITERIA_MOCK_SUPPLIER);
    }

    /**
     * Create a new mock instance of an EntityList, containing given values, with find() method stubbed for returning
     * SearchCriteriaBuilder objects produced by given supplier (factory, provider).
     * 
     * @param elements
     *            content of new mock
     * @param criteriaBuilderFactory
     *            SearchCriteriaBuilder objects supplier
     * @return a new instance of an EntityList mock
     */
    public static EntityList create(final Collection<Entity> elements,
            final Supplier<SearchCriteriaBuilder> criteriaBuilderFactory) {
        return new EntityListMock(Lists.newLinkedList(elements), criteriaBuilderFactory);
    }

    public static EntityList copyOf(final EntityList entityList) {
        return create(entityList, new Supplier<SearchCriteriaBuilder>() {

            @Override
            public SearchCriteriaBuilder get() {
                return entityList.find();
            }
        });
    }

    private EntityListMock(final Collection<Entity> elements, final Supplier<SearchCriteriaBuilder> criteriaBuilderFactory) {
        super(elements);
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    @Override
    public SearchCriteriaBuilder find() {
        return criteriaBuilderFactory.get();
    }

}
