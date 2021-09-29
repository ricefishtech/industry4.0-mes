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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.internal.api.DataAccessService;
import com.qcadoo.model.internal.api.InternalDataDefinition;

public class EntityListImplTest {

    private DataAccessService dataAccessService;

    @Before
    public void init() {
        dataAccessService = mock(DataAccessService.class);
        SearchRestrictions restrictions = new SearchRestrictions();
        ReflectionTestUtils.setField(restrictions, "dataAccessService", dataAccessService);
    }

    @Test
    public void shouldBeEmptyIfParentIdIsNull() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);
        EntityListImpl list = new EntityListImpl(dataDefinition, "hasMany", null);

        // then
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldLoadEntities() throws Exception {
        // given
        Entity entity = mock(Entity.class);
        List<Entity> entities = Collections.singletonList(entity);

        BelongsToType fieldType = mock(BelongsToType.class);
        InternalDataDefinition dataDefinition = mock(InternalDataDefinition.class, RETURNS_DEEP_STUBS);
        given(fieldType.getDataDefinition()).willReturn(dataDefinition);
        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        given(fieldDefinition.getName()).willReturn("field");
        given(fieldDefinition.getType()).willReturn(fieldType);
        given(dataDefinition.getField("hasMany")).willReturn(fieldDefinition);
        given(
                dataDefinition.find().createAlias(fieldDefinition.getName(), fieldDefinition.getName())
                        .add(SearchRestrictions.eq(fieldDefinition.getName() + ".id", 1L)).list().getEntities()).willReturn(
                entities);
        given(dataAccessService.get(dataDefinition, 1L)).willReturn(entity);

        EntityListImpl list = new EntityListImpl(dataDefinition, "hasMany", 1L);

        // then
        assertEquals(1, list.size());
        assertEquals(entity, list.get(0));
    }

    @Test
    public void shouldReturnCriteriaBuilder() throws Exception {
        // given
        BelongsToType fieldType = mock(BelongsToType.class);
        InternalDataDefinition dataDefinition = mock(InternalDataDefinition.class, RETURNS_DEEP_STUBS);
        given(fieldType.getDataDefinition()).willReturn(dataDefinition);
        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        given(fieldDefinition.getType()).willReturn(fieldType);
        given(fieldDefinition.getName()).willReturn("field");
        given(dataDefinition.getField("hasMany")).willReturn(fieldDefinition);
        SearchCriteriaBuilder searchCriteriaBuilder = mock(SearchCriteriaBuilder.class);
        given(
                dataDefinition.find().createAlias(fieldDefinition.getName(), fieldDefinition.getName())
                        .add(SearchRestrictions.eq(fieldDefinition.getName() + ".id", 1L))).willReturn(searchCriteriaBuilder);

        EntityList list = new EntityListImpl(dataDefinition, "hasMany", 1L);

        // then
        assertEquals(searchCriteriaBuilder, list.find());
    }

}
