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
package com.qcadoo.view.internal.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CheckBoxComponentStateTest {

    private CheckBoxComponentState checkBoxComponentState;

    @Mock
    private FieldComponentPattern pattern;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);

        this.checkBoxComponentState = new CheckBoxComponentState(pattern);
    }

    @Test
    public final void shouldIsCheckedReturnTrueIfFieldValueIs1() {
        // given
        checkBoxComponentState.setFieldValue("1");

        // when
        boolean isChecked = checkBoxComponentState.isChecked();

        // then
        assertTrue(isChecked);
    }

    @Test
    public final void shouldIsCheckedReturnFalseIfFieldValueIsNull() {
        // given
        checkBoxComponentState.setFieldValue(null);

        // when
        boolean isChecked = checkBoxComponentState.isChecked();

        // then
        assertFalse(isChecked);
    }

    @Test
    public final void shouldIsCheckedReturnFalseIfFieldValueIsEmptyString() {
        // given
        checkBoxComponentState.setFieldValue("");

        // when
        boolean isChecked = checkBoxComponentState.isChecked();

        // then
        assertFalse(isChecked);
    }

    @Test
    public final void shouldIsCheckedReturnFalseIfFieldValueIsAnyStringOtherThan1() {
        // given
        checkBoxComponentState.setFieldValue("someRandomStringOtherThan-1");

        // when
        boolean isChecked = checkBoxComponentState.isChecked();

        // then
        assertFalse(isChecked);
    }

    @Test
    public final void shouldSetCheckedChangeFieldValueToTrue() {
        // given
        checkBoxComponentState.setChecked(true);

        // when
        Object fieldValue = checkBoxComponentState.getFieldValue();

        // then
        assertEquals("1", fieldValue);
    }

    @Test
    public final void shouldSetCheckedChangeFieldValueToNull() {
        // given
        checkBoxComponentState.setChecked(false);

        // when
        Object fieldValue = checkBoxComponentState.getFieldValue();

        // then
        assertNull(fieldValue);
    }
}
