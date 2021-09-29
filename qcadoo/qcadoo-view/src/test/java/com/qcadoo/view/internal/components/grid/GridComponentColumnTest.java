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
package com.qcadoo.view.internal.components.grid;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.view.constants.Alignment;

public class GridComponentColumnTest {

    private static final String COL_NAME = "columnName";

    private GridComponentColumn gridColumn;

    @Mock
    private FieldDefinition fieldDefinition;

    @Mock
    private FieldType fieldType;

    @Before
    public void init() {
        gridColumn = new GridComponentColumn(COL_NAME);

        MockitoAnnotations.initMocks(this);

        given(fieldDefinition.getType()).willReturn(fieldType);

    }

    private void stubFieldType(final Class<?> clazz) {
        given(fieldType.getType()).willAnswer(new Answer<Class<?>>() {

            @Override
            public Class<?> answer(InvocationOnMock invocation) throws Throwable {
                return clazz;
            }
        });
    }

    @Test
    public void shouldAlignToLeftByDefault() {
        // then
        assertEquals(Alignment.LEFT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignAccordingToFieldValueTypeAfterSetAlignToNull() {
        // when
        gridColumn.setAlign(null);

        // then
        assertEquals(Alignment.LEFT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignNonNumericValuesToLeft() {
        // given
        stubFieldType(String.class);

        // when
        gridColumn.addField(fieldDefinition);

        // then
        assertEquals(Alignment.LEFT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignNumericValuesToRight() {
        // given
        stubFieldType(BigDecimal.class);

        // when
        gridColumn.addField(fieldDefinition);

        // then
        assertEquals(Alignment.RIGHT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignNonNumericValuesToRightIfSpecified1() {
        // given
        stubFieldType(String.class);

        // when
        gridColumn.setAlign(Alignment.RIGHT);
        gridColumn.addField(fieldDefinition);

        // then
        assertEquals(Alignment.RIGHT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignNonNumericValuesToRightIfSpecified2() {
        // given
        stubFieldType(String.class);

        // when
        gridColumn.addField(fieldDefinition);
        gridColumn.setAlign(Alignment.RIGHT);

        // then
        assertEquals(Alignment.RIGHT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignNumericValuesToLeftIfSpecified1() {
        // given
        stubFieldType(BigDecimal.class);

        // when
        gridColumn.setAlign(Alignment.LEFT);
        gridColumn.addField(fieldDefinition);

        // then
        assertEquals(Alignment.LEFT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignNumericValuesToLeftIfSpecified2() {
        // given
        stubFieldType(String.class);

        // when
        gridColumn.addField(fieldDefinition);
        gridColumn.setAlign(Alignment.LEFT);

        // then
        assertEquals(Alignment.LEFT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignToLeftIfColumnHasManyFields() {
        // given
        FieldDefinition secondFieldDefinition = mock(FieldDefinition.class);

        // both fields will be sharing the same field type,
        // so both of them will represents the same kind of values.
        given(secondFieldDefinition.getType()).willReturn(fieldType);

        stubFieldType(BigDecimal.class);

        // when
        gridColumn.addField(fieldDefinition);
        gridColumn.addField(fieldDefinition);

        // then
        assertEquals(Alignment.LEFT, gridColumn.getAlign());
    }

    @Test
    public void shouldAlignToLeftIfColumnHasManyFieldsUntilWeSetAlignExplicitly() {
        // given
        FieldDefinition secondFieldDefinition = mock(FieldDefinition.class);

        // both fields will be sharing the same field type,
        // so both of them will represents the same kind of values.
        given(secondFieldDefinition.getType()).willReturn(fieldType);

        stubFieldType(BigDecimal.class);

        // when
        gridColumn.addField(fieldDefinition);
        gridColumn.addField(fieldDefinition);
        gridColumn.setAlign(Alignment.RIGHT);

        // then
        assertEquals(Alignment.RIGHT, gridColumn.getAlign());
    }

}
