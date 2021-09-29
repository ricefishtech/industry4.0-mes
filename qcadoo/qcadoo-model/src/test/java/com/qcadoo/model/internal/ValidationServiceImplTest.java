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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Maps;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.api.InternalFieldDefinition;
import com.qcadoo.model.internal.api.ValidationService;
import com.qcadoo.model.internal.types.StringType;

public class ValidationServiceImplTest {

    private ValidationService validationService;

    private Entity genericEntity;

    private Entity existingGenericEntity;

    private InternalDataDefinition dataDefinition;

    @Before
    public final void init() {
        genericEntity = mock(Entity.class);
        existingGenericEntity = mock(Entity.class);
        dataDefinition = mock(InternalDataDefinition.class);

        validationService = new ValidationServiceImpl();
    }

    @Test
    public final void shouldCallCreateAndSaveHooks() throws Exception {
        // given
        when(genericEntity.getId()).thenReturn(null);

        // when
        validationService.validateGenericEntity(dataDefinition, genericEntity, existingGenericEntity);

        // then
        verify(dataDefinition).callCreateHook(genericEntity);
        verify(dataDefinition).callSaveHook(genericEntity);
    }

    @Test
    public final void shouldCallUpdateAndSaveHooks() throws Exception {
        // given
        when(genericEntity.getId()).thenReturn(1L);

        // when
        validationService.validateGenericEntity(dataDefinition, genericEntity, existingGenericEntity);

        // then
        verify(dataDefinition).callUpdateHook(genericEntity);
        verify(dataDefinition).callSaveHook(genericEntity);
    }

    @Test
    public final void shouldDelegateCallCreateHooksAlsoIfEntityIsNotValid() throws Exception {
        // given
        when(genericEntity.getId()).thenReturn(null);
        when(genericEntity.isValid()).thenReturn(false);

        // when
        validationService.validateGenericEntity(dataDefinition, genericEntity, null);

        // then
        verify(dataDefinition).callCreateHook(genericEntity);
        verify(dataDefinition).callSaveHook(genericEntity);
    }

    @Test
    public final void shouldDelegateCallUpdateHooksAlsoIfEntityIsNotValid() throws Exception {
        // given
        when(genericEntity.getId()).thenReturn(1L);
        when(genericEntity.isValid()).thenReturn(false);

        // when
        validationService.validateGenericEntity(dataDefinition, genericEntity, null);

        // then
        verify(dataDefinition).callUpdateHook(genericEntity);
        verify(dataDefinition).callSaveHook(genericEntity);
    }

    @Test
    public final void shouldCallFieldCustomValidatorWithNonNullOldValueOnUpdate() {
        // given
        final Long savedEntityId = 1L;

        final String someFieldName = "someField";
        final String someFieldOldValue = "someFieldValue";
        final String someFieldNewValue = "someFieldValueAfterUpdate";
        InternalFieldDefinition someFieldDefinition = mockFieldDefinition(someFieldName, new StringType());
        Map<String, FieldDefinition> fieldsMap = Maps.newHashMap();
        fieldsMap.put(someFieldName, someFieldDefinition);
        stubFieldDefinitions(dataDefinition, fieldsMap);

        when(genericEntity.getId()).thenReturn(savedEntityId);
        when(genericEntity.getField(someFieldName)).thenReturn(someFieldNewValue);
        when(genericEntity.isFieldValid(someFieldName)).thenReturn(true);
        when(existingGenericEntity.getField(someFieldName)).thenReturn(someFieldOldValue);

        // when
        validationService.validateGenericEntity(dataDefinition, genericEntity, existingGenericEntity);

        // then
        verify(someFieldDefinition, times(1)).callValidators(genericEntity, someFieldOldValue, someFieldNewValue);
    }

    @Test
    public final void shouldCallFieldCustomValidatorWithNonNullOldValueOnCopy() {
        // given
        final Long savedEntityId = null;

        final String someFieldName = "someField";
        final String someFieldOldValue = "someFieldValue";
        final String someFieldNewValue = "someFieldValueAfterUpdate";
        InternalFieldDefinition someFieldDefinition = mockFieldDefinition(someFieldName, new StringType());
        Map<String, FieldDefinition> fieldsMap = Maps.newHashMap();
        fieldsMap.put(someFieldName, someFieldDefinition);
        stubFieldDefinitions(dataDefinition, fieldsMap);

        when(genericEntity.getId()).thenReturn(savedEntityId);
        when(genericEntity.getField(someFieldName)).thenReturn(someFieldNewValue);
        when(genericEntity.isFieldValid(someFieldName)).thenReturn(true);
        when(existingGenericEntity.getField(someFieldName)).thenReturn(someFieldOldValue);

        // when
        validationService.validateGenericEntity(dataDefinition, genericEntity, existingGenericEntity);

        // then
        verify(someFieldDefinition, times(1)).callValidators(genericEntity, someFieldOldValue, someFieldNewValue);
    }

    @Test
    public final void shouldCallFieldCustomValidatorWithNullOldValueOnCreate() {
        // given
        final Long savedEntityId = null;

        final String someFieldName = "someField";
        final String someFieldNewValue = "someFieldValueAfterUpdate";
        InternalFieldDefinition someFieldDefinition = mockFieldDefinition(someFieldName, new StringType());
        Map<String, FieldDefinition> fieldsMap = Maps.newHashMap();
        fieldsMap.put(someFieldName, someFieldDefinition);
        stubFieldDefinitions(dataDefinition, fieldsMap);

        when(genericEntity.getId()).thenReturn(savedEntityId);
        when(genericEntity.getField(someFieldName)).thenReturn(someFieldNewValue);
        when(genericEntity.isFieldValid(someFieldName)).thenReturn(true);

        // when
        validationService.validateGenericEntity(dataDefinition, genericEntity, null);

        // then
        verify(someFieldDefinition, times(1)).callValidators(genericEntity, null, someFieldNewValue);
    }

    private InternalFieldDefinition mockFieldDefinition(final String name, final FieldType type) {
        InternalFieldDefinition fieldDefinition = mock(InternalFieldDefinition.class);
        when(fieldDefinition.getName()).thenReturn(name);
        when(fieldDefinition.getType()).thenReturn(type);
        when(fieldDefinition.callValidators(Mockito.any(Entity.class), Mockito.any(), Mockito.any())).thenReturn(true);
        return fieldDefinition;
    }

    private void stubFieldDefinitions(final DataDefinition dataDefinitionMock, final Map<String, FieldDefinition> fieldsMap) {
        when(dataDefinitionMock.getFields()).thenReturn(Collections.unmodifiableMap(fieldsMap));
        for (Entry<String, FieldDefinition> fieldEntry : fieldsMap.entrySet()) {
            when(dataDefinitionMock.getField(fieldEntry.getKey())).thenReturn(fieldEntry.getValue());
        }
    }

}
