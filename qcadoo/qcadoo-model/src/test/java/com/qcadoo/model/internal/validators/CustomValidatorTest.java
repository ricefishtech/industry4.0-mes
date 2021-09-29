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
package com.qcadoo.model.internal.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Maps;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.validators.ErrorMessage;
import com.qcadoo.model.internal.api.FieldHookDefinition;

public class CustomValidatorTest {

    private static final String FIELD_NAME = "someFieldWithValidator";

    private static final String DEFAULT_MSG = "qcadooView.validate.field.error.custom";

    private static final String CUSTOM_MSG = "qcadoo.testing.custom.field.error.message";

    private CustomValidator customValidator;

    @Mock
    private FieldHookDefinition fieldHookDef;

    @Mock
    private Entity entity;

    @Mock
    private DataDefinition dataDefinition;

    @Mock
    private FieldDefinition fieldDefinition;

    @Mock
    private Object oldValue, newValue;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        customValidator = new CustomValidator(fieldHookDef);
        customValidator.initialize(dataDefinition, fieldDefinition);
        given(fieldDefinition.getName()).willReturn(FIELD_NAME);
    }

    @Test
    public final void shouldNotAddDefaultMessageIfValidationSuccess() {
        // given
        stubFieldHookDef(true, Maps.<String, ErrorMessage> newHashMap());

        // when
        boolean isValid = customValidator.call(entity, oldValue, newValue);

        // then
        assertTrue(isValid);
        verify(fieldHookDef, times(1)).call(entity, oldValue, newValue);
        verify(entity, never()).addError(Mockito.eq(fieldDefinition), Mockito.anyString(), Mockito.<String> anyVararg());
    }

    @Test
    public final void shouldAddDefaultMessageIfValidationFailAndThereIsNoMessageAssignedToField() {
        // given
        stubFieldHookDef(false, Maps.<String, ErrorMessage> newHashMap());

        // when
        boolean isValid = customValidator.call(entity, oldValue, newValue);

        // then
        assertFalse(isValid);
        verify(fieldHookDef, times(1)).call(entity, oldValue, newValue);
        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        verify(entity, times(1)).addError(Mockito.eq(fieldDefinition), argCaptor.capture(), Mockito.<String> anyVararg());
        assertEquals(DEFAULT_MSG, argCaptor.getValue());
    }

    @Test
    public final void shouldNotAddDefaultMessageIfValidationFailAndThereIsAlreadyMessageAssignedToField() {
        // given
        Map<String, ErrorMessage> errors = Maps.newHashMap();
        errors.put(FIELD_NAME, new ErrorMessage(CUSTOM_MSG));
        stubFieldHookDef(false, errors);

        // when
        boolean isValid = customValidator.call(entity, oldValue, newValue);

        // then
        assertFalse(isValid);
        verify(fieldHookDef, times(1)).call(entity, oldValue, newValue);
        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        verify(entity, times(1)).addError(Mockito.eq(fieldDefinition), argCaptor.capture(), Mockito.<String> anyVararg());
        assertEquals(CUSTOM_MSG, argCaptor.getValue());
    }

    @Test
    public final void shouldAddDefaultMessageIfValidationFailAndThereIsAlreadyMessageAssignedToOtherField() {
        // given
        Map<String, ErrorMessage> errors = Maps.newHashMap();
        errors.put("another" + FIELD_NAME, new ErrorMessage(CUSTOM_MSG));
        stubFieldHookDef(false, errors);

        // when
        boolean isValid = customValidator.call(entity, oldValue, newValue);

        // then
        assertFalse(isValid);
        verify(fieldHookDef, times(1)).call(entity, oldValue, newValue);
        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        verify(entity, times(1)).addError(Mockito.eq(fieldDefinition), argCaptor.capture(), Mockito.<String> anyVararg());
        assertEquals(DEFAULT_MSG, argCaptor.getValue());
    }

    @Test
    public final void shouldNotAddDefaultMessageIfValidationSuccessAndThereIsAlreadyMessageAssignedToField() {
        // given
        Map<String, ErrorMessage> errors = Maps.newHashMap();
        errors.put("another" + FIELD_NAME, new ErrorMessage(CUSTOM_MSG));
        stubFieldHookDef(true, errors);

        // when
        boolean isValid = customValidator.call(entity, oldValue, newValue);

        // then
        assertTrue(isValid);
        verify(fieldHookDef, times(1)).call(entity, oldValue, newValue);
        verify(entity, never()).addError(Mockito.eq(fieldDefinition), Mockito.eq(CUSTOM_MSG), Mockito.<String> anyVararg());
        verify(entity, never()).addError(Mockito.eq(fieldDefinition), Mockito.eq(DEFAULT_MSG), Mockito.<String> anyVararg());
    }

    @Test
    public final void shouldNotAddDefaultMessageIfValidationSuccessAndThereIsAlreadyMessageAssignedToOtherField() {
        // given
        Map<String, ErrorMessage> errors = Maps.newHashMap();
        errors.put(FIELD_NAME, new ErrorMessage(CUSTOM_MSG));
        stubFieldHookDef(true, errors);

        // when
        boolean isValid = customValidator.call(entity, oldValue, newValue);

        // then
        assertTrue(isValid);
        verify(fieldHookDef, times(1)).call(entity, oldValue, newValue);
        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        verify(entity, times(1)).addError(Mockito.eq(fieldDefinition), argCaptor.capture(), Mockito.<String> anyVararg());
        assertEquals(CUSTOM_MSG, argCaptor.getValue());
    }

    private void stubFieldHookDef(final Boolean result, final Map<String, ErrorMessage> errors) {
        given(fieldHookDef.call(Mockito.eq(entity), any(), any())).willAnswer(new Answer<Boolean>() {

            @Override
            public Boolean answer(final InvocationOnMock invocation) throws Throwable {
                given(entity.getErrors()).willReturn(Collections.unmodifiableMap(errors));
                for (Entry<String, ErrorMessage> fieldErrorEntry : errors.entrySet()) {
                    ErrorMessage errorMsg = fieldErrorEntry.getValue();
                    if (fieldErrorEntry.getKey().equals(fieldDefinition.getName())) {
                        entity.addError(fieldDefinition, errorMsg.getMessage(), errorMsg.getVars());
                    }
                    given(entity.getError(fieldErrorEntry.getKey())).willReturn(errorMsg);
                }
                return result;
            }

        });
    }
}
