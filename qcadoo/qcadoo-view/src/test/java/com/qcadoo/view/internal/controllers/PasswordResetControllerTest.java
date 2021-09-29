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
package com.qcadoo.view.internal.controllers;

import static junit.framework.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.qcadoo.mail.api.InvalidMailAddressException;
import com.qcadoo.mail.api.MailConfigurationException;
import com.qcadoo.security.api.PasswordReminderService;

public class PasswordResetControllerTest {

    private static final String EXISTING_USER_NAME = "admin";

    private PasswordResetController passwordResetController;

    private PasswordReminderService passwordReminderService;

    @Before
    public final void init() {
        passwordResetController = new PasswordResetController();
        passwordReminderService = mock(PasswordReminderService.class);

        mockStatic(PasswordReminderService.class);
        doThrow(new UsernameNotFoundException("")).when(passwordReminderService).generateAndSendNewPassword(
                not(Mockito.eq(EXISTING_USER_NAME)));

        setField(passwordResetController, "passwordReminderService", passwordReminderService);
    }

    @Test
    public final void shouldReturnSuccess() throws Exception {
        // when
        String result = passwordResetController.processForgotPasswordFormView(EXISTING_USER_NAME);

        // then
        assertEquals("success", result);
        verify(passwordReminderService, times(1)).generateAndSendNewPassword(EXISTING_USER_NAME);
        verify(passwordReminderService, never()).generateAndSendNewPassword(not(Mockito.eq(EXISTING_USER_NAME)));
    }

    @Test
    public final void shouldReturnUserNotFound() throws Exception {
        // when
        String result = passwordResetController.processForgotPasswordFormView("userThatDoesNotExists");

        // then
        assertEquals("userNotFound", result);
        verify(passwordReminderService, times(1)).generateAndSendNewPassword(Mockito.anyString());
    }

    @Test
    public final void shouldReturnLoginIsBlankWhenLoginIsNull() throws Exception {
        // when
        String result = passwordResetController.processForgotPasswordFormView(null);

        // then
        assertEquals("loginIsBlank", result);
        verifyZeroInteractions(passwordReminderService);
    }

    @Test
    public final void shouldReturnLoginIsBlankWhenLoginIsEmpty() throws Exception {
        // when
        String result = passwordResetController.processForgotPasswordFormView("");

        // then
        assertEquals("loginIsBlank", result);
        verifyZeroInteractions(passwordReminderService);
    }

    @Test
    public final void shouldReturnLoginIsBlank() throws Exception {
        // when
        String result = passwordResetController.processForgotPasswordFormView("   ");

        // then
        assertEquals("loginIsBlank", result);
        verifyZeroInteractions(passwordReminderService);
    }

    @Test
    public final void shouldReturnErrorWhenUnexpectedErrorOccured() throws Exception {
        // given
        doThrow(new RuntimeException()).when(passwordReminderService).generateAndSendNewPassword(Mockito.anyString());

        // when
        String result = passwordResetController.processForgotPasswordFormView(EXISTING_USER_NAME);

        // then
        assertEquals("error", result);
    }

    @Test
    public final void shouldReturnInvalidMailConfigWhenMailConfigurationErrorOccured() throws Exception {
        // given
        doThrow(new MailConfigurationException("error")).when(passwordReminderService).generateAndSendNewPassword(
                Mockito.anyString());

        // when
        String result = passwordResetController.processForgotPasswordFormView(EXISTING_USER_NAME);

        // then
        assertEquals("invalidMailConfig", result);
    }

    @Test
    public final void shouldReturnInvalidMailAddressWhenMailConfigurationErrorOccured() throws Exception {
        // given
        doThrow(new InvalidMailAddressException("error")).when(passwordReminderService).generateAndSendNewPassword(
                Mockito.anyString());

        // when
        String result = passwordResetController.processForgotPasswordFormView(EXISTING_USER_NAME);

        // then
        assertEquals("invalidMailAddress", result);
    }
}
