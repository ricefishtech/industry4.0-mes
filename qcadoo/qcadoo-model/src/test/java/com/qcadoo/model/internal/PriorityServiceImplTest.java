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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Comparator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;

public class PriorityServiceImplTest {

    private PriorityServiceImpl priorityServiceImpl;

    @Mock
    private Entity firstEntity;

    @Mock
    private Entity secondEntity;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);
        priorityServiceImpl = new PriorityServiceImpl();
    }

    @Test
    public final void shouldComparatorFindPriorityFieldAndCompareEntities() throws Exception {
        // given
        final String priorityFieldName = "priority123field";

        Comparator<Entity> entityPriorityComparator = priorityServiceImpl.getEntityPriorityComparator();

        mockPriorityFieldDefinition(firstEntity, priorityFieldName);
        mockPriorityFieldDefinition(secondEntity, priorityFieldName);

        given(firstEntity.getField(priorityFieldName)).willReturn(1);
        given(secondEntity.getField(priorityFieldName)).willReturn(2);

        // when
        int comparationResult = entityPriorityComparator.compare(firstEntity, secondEntity);

        // then
        verify(firstEntity).getField(priorityFieldName);
        verify(secondEntity).getField(priorityFieldName);
        Assert.assertEquals(-1, comparationResult);
    }

    @Test
    public final void shouldThrowExceptionIfEntityDoNotHavePriorityFields() throws Exception {
        // given
        Comparator<Entity> entityPriorityComparator = priorityServiceImpl.getEntityPriorityComparator();

        DataDefinition dataDefinition = mock(DataDefinition.class);
        given(firstEntity.getDataDefinition()).willReturn(dataDefinition);
        given(secondEntity.getDataDefinition()).willReturn(dataDefinition);
        given(dataDefinition.getPriorityField()).willReturn(null);

        boolean success = false;

        // when
        try {
            entityPriorityComparator.compare(firstEntity, secondEntity);
        } catch (NullPointerException e) {
            success = true;
        }

        // then
        Assert.assertTrue(success);
    }

    private void mockPriorityFieldDefinition(final Entity entity, final String priorityFieldName) {
        DataDefinition dataDefinition = mock(DataDefinition.class);
        given(entity.getDataDefinition()).willReturn(dataDefinition);

        FieldDefinition priorityField = mock(FieldDefinition.class);
        given(dataDefinition.getPriorityField()).willReturn(priorityField);

        given(priorityField.getName()).willReturn(priorityFieldName);
    }
}
