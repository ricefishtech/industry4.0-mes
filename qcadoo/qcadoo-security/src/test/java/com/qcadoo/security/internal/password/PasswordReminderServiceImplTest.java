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
package com.qcadoo.security.internal.password;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.mail.api.MailService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.security.api.PasswordGeneratorService;
import com.qcadoo.security.internal.api.InternalSecurityService;

public class PasswordReminderServiceImplTest {

    private static final String USER_NAME_FIELD = "userName";

    private static final String USER_NAME_VALUE = "someUser";

    private static final String USER_EMAIL_FIELD = "email";

    private static final String USER_EMAIL_VALUE = "valid_address@qcadoo.com";

    private static final String GENERATED_RANDOM_PASS = "RandomPass";

    private PasswordReminderServiceImpl passwordReminderServiceImpl;

    private MailService mailService;

    private InternalSecurityService internalSecurityService;

    private PasswordGeneratorService passwordGeneratorService;

    private Entity userEntity;

    private DataDefinition userEntityDataDefinition;

    private TranslationService translationService;

    @Before
    public final void init() {
        passwordReminderServiceImpl = new PasswordReminderServiceImpl();

        mailService = mock(MailService.class);
        internalSecurityService = mock(InternalSecurityService.class);
        passwordGeneratorService = mock(PasswordGeneratorService.class);
        userEntity = mock(Entity.class);
        userEntityDataDefinition = mock(DataDefinition.class);

        translationService = mock(TranslationService.class);
        given(translationService.translate(Mockito.anyString(), Mockito.any(Locale.class), Mockito.any(String[].class)))
                .willReturn("default translated string");

        given(passwordGeneratorService.generatePassword()).willReturn(GENERATED_RANDOM_PASS);

        mockEntityStringField(userEntity, USER_NAME_FIELD, USER_NAME_VALUE);
        mockEntityStringField(userEntity, USER_EMAIL_FIELD, USER_EMAIL_VALUE);
        given(userEntity.getDataDefinition()).willReturn(userEntityDataDefinition);

        given(internalSecurityService.getUserEntity(USER_NAME_VALUE)).willReturn(userEntity);

        ReflectionTestUtils.setField(passwordReminderServiceImpl, "mailService", mailService);
        ReflectionTestUtils.setField(passwordReminderServiceImpl, "securityService", internalSecurityService);
        ReflectionTestUtils.setField(passwordReminderServiceImpl, "passwordGeneratorService", passwordGeneratorService);
        ReflectionTestUtils.setField(passwordReminderServiceImpl, "translationService", translationService);
    }

    @Test
    public final void shouldGenerateAndSendNewPassword() throws Exception {
        // when
        passwordReminderServiceImpl.generateAndSendNewPassword(USER_NAME_VALUE);

        // then
        verify(internalSecurityService, times(1)).getUserEntity(USER_NAME_VALUE);
        verify(mailService, times(1)).sendEmail(Mockito.eq(USER_EMAIL_VALUE), Mockito.anyString(), Mockito.anyString());
        verify(passwordGeneratorService, times(1)).generatePassword();
        verify(userEntity, times(1)).setField("password", GENERATED_RANDOM_PASS);
        verify(userEntity, times(1)).setField("passwordConfirmation", GENERATED_RANDOM_PASS);
        verify(userEntityDataDefinition, times(1)).save(userEntity);
    }

    @Test(expected = UsernameNotFoundException.class)
    public final void shouldThrowExceptionIfUserDoesNotExists() throws Exception {
        // when
        passwordReminderServiceImpl.generateAndSendNewPassword("someNoneExistentUser");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfUserNameIsNull() throws Exception {
        // when
        passwordReminderServiceImpl.generateAndSendNewPassword(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfUserNameIsEmpty() throws Exception {
        // when
        passwordReminderServiceImpl.generateAndSendNewPassword("");
    }

    private void mockEntityStringField(final Entity entity, final String fieldName, final String fieldValue) {
        given(entity.getStringField(fieldName)).willReturn(fieldValue);
        given(entity.getField(fieldName)).willReturn(fieldValue);
    }
}
