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
package com.qcadoo.security.internal.validators;

import static org.mockito.MockitoAnnotations.initMocks;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;

public class EmailValidationServiceTest {

    private EmailValidationService emailValidationService;

    @Mock
    private DataDefinition dataDefinition;

    @Mock
    private FieldDefinition fieldDefinition;

    @Mock
    private Entity entity;

    private Object oldValue;

    private Object newValue;

    @Before
    public final void init() {
        initMocks(this);
        oldValue = null;
        emailValidationService = new EmailValidationService();
    }

    @Test
    public final void shouldPassNullEmail() throws Exception {
        // given
        newValue = null;

        // when
        boolean result = emailValidationService.checkEmail(dataDefinition, fieldDefinition, entity, oldValue, newValue);

        // then
        Assert.assertTrue(result);
    }

    @Test
    public final void shouldPassEmptyEmail() throws Exception {
        // given
        newValue = "";

        // when
        boolean result = emailValidationService.checkEmail(dataDefinition, fieldDefinition, entity, oldValue, newValue);

        // then
        Assert.assertTrue(result);
    }

    @Test
    public final void shouldPassValidEmail() throws Exception {
        // given
        newValue = "some.email@qcadoo.com";

        // when
        boolean result = emailValidationService.checkEmail(dataDefinition, fieldDefinition, entity, oldValue, newValue);

        // then
        Assert.assertTrue(result);
    }

    @Test
    public final void shouldNotPassBlankEmail() throws Exception {
        // given
        newValue = " ";

        // when
        boolean result = emailValidationService.checkEmail(dataDefinition, fieldDefinition, entity, oldValue, newValue);

        // then
        Assert.assertFalse(result);
    }

    @Test
    public final void shouldNotPassLocalEmail() throws Exception {
        // given
        newValue = "some.email@192.168.0.1";

        // when
        boolean result = emailValidationService.checkEmail(dataDefinition, fieldDefinition, entity, oldValue, newValue);

        // then
        Assert.assertFalse(result);
    }

    @Test
    public final void shouldNotPassInvalidEmail() throws Exception {
        // given
        newValue = "invalid$email.com";

        // when
        boolean result = emailValidationService.checkEmail(dataDefinition, fieldDefinition, entity, oldValue, newValue);

        // then
        Assert.assertFalse(result);
    }
}
