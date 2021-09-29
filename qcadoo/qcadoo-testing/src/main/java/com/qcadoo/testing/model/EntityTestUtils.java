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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.mockito.BDDMockito;
import org.mockito.Matchers;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.model.api.EntityOpResult;

/**
 * This is a set of common entity testing helpers.
 * 
 * @since 1.2.1
 */
public final class EntityTestUtils {

    private EntityTestUtils() {
    }

    public static DataDefinition mockDataDefinition() {
        DataDefinition dd = mock(DataDefinition.class);
        BDDMockito.given(dd.save(any(Entity.class))).willAnswer(invocation -> (Entity) invocation.getArguments()[0]);
        BDDMockito.given(dd.delete(Matchers.<Long> anyVararg())).willAnswer(invocation -> EntityOpResult.successfull());
        return dd;
    }

    public static Entity mockEntity() {
        return mockEntity(null, mockDataDefinition());
    }

    public static Entity mockEntity(final Long id) {
        return mockEntity(id, mockDataDefinition());
    }

    public static Entity mockEntity(final DataDefinition dataDefinition) {
        return mockEntity(null, dataDefinition);
    }

    public static Entity mockEntity(final Long id, final DataDefinition dataDefinition) {
        Entity entityMock = mock(Entity.class);
        if (id != null) {
            stubId(entityMock, id);
        }
        BDDMockito.given(entityMock.getDataDefinition()).willReturn(dataDefinition);
        return entityMock;
    }

    public static void stubId(final Entity entity, final Long id) {
        BDDMockito.given(entity.getId()).willReturn(id);
        stubField(entity, "id", id);
    }

    public static void stubField(final Entity entity, final String fieldName, final Object fieldValue) {
        BDDMockito.given(entity.getField(fieldName)).willReturn(fieldValue);
    }

    public static void stubField(final Entity entity, final String fieldName, final Answer<?> answer) {
        BDDMockito.given(entity.getField(fieldName)).willAnswer(answer);
    }

    public static void stubBooleanField(final Entity entity, final String fieldName, final boolean fieldValue) {
        BDDMockito.given(entity.getBooleanField(fieldName)).willReturn(fieldValue);
        stubField(entity, fieldName, fieldValue);
    }

    public static void stubStringField(final Entity entity, final String fieldName, final String fieldValue) {
        BDDMockito.given(entity.getStringField(fieldName)).willReturn(fieldValue);
        stubField(entity, fieldName, fieldValue);
    }

    public static void stubDateField(final Entity entity, final String fieldName, final Date fieldValue) {
        Answer<Date> answer = invocation -> {
            if (fieldValue == null) {
                return null;
            }
            return new Date(fieldValue.getTime());
        };
        BDDMockito.given(entity.getDateField(fieldName)).willAnswer(answer);
        stubField(entity, fieldName, answer);
    }

    public static void stubIntegerField(final Entity entity, final String fieldName, final Integer fieldValue) {
        BDDMockito.given(entity.getIntegerField(fieldName)).willReturn(fieldValue);
        stubField(entity, fieldName, fieldValue);
    }

    public static void stubDecimalField(final Entity entity, final String fieldName, final BigDecimal fieldValue) {
        BDDMockito.given(entity.getDecimalField(fieldName)).willReturn(fieldValue);
        stubField(entity, fieldName, fieldValue);
    }

    public static void stubManyToManyField(final Entity entity, final String fieldName, final Set<Entity> fieldValue) {
        Answer<Set<Entity>> answer = invocation -> {
            if (fieldValue == null) {
                return Collections.emptySet();
            }
            return Sets.newHashSet(fieldValue);
        };
        BDDMockito.given(entity.getManyToManyField(fieldName)).willAnswer(answer);
        stubField(entity, fieldName, answer);
    }

    public static void stubHasManyField(final Entity entity, final String fieldName, final Iterable<Entity> fieldValue) {
        EntityList entityListMock = EntityListMock.create(Lists.newArrayList(fieldValue));
        stubHasManyField(entity, fieldName, entityListMock);
    }

    public static void stubHasManyField(final Entity entity, final String fieldName, final EntityList fieldValue) {
        Answer<EntityList> answer = invocation -> {
            if (fieldValue == null) {
                return EntityListMock.create();
            }
            return EntityListMock.copyOf(fieldValue);
        };
        BDDMockito.given(entity.getHasManyField(fieldName)).willAnswer(answer);
        stubField(entity, fieldName, answer);
    }

    public static void stubBelongsToField(final Entity entity, final String fieldName, final Entity fieldValue) {
        BDDMockito.given(entity.getBelongsToField(fieldName)).willReturn(fieldValue);
        stubField(entity, fieldName, fieldValue);
    }

}
