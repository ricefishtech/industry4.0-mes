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

import com.google.common.base.Preconditions;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.mail.api.InvalidMailAddressException;
import com.qcadoo.mail.api.MailService;
import com.qcadoo.mail.internal.MailServiceImpl;
import com.qcadoo.model.api.Entity;
import com.qcadoo.security.api.PasswordGeneratorService;
import com.qcadoo.security.api.PasswordReminderService;
import com.qcadoo.security.internal.api.InternalSecurityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Service
public class PasswordReminderServiceImpl implements PasswordReminderService {

    @Value("${mail.company}")
    private String company;

    @Value("${mail.email}")
    private String contactMail;

    @Autowired
    private MailService mailService;

    @Autowired
    private InternalSecurityService securityService;

    @Autowired
    private PasswordGeneratorService passwordGeneratorService;

    @Autowired
    private TranslationService translationService;

    @Override
    @Transactional
    public void generateAndSendNewPassword(final String userName) throws UsernameNotFoundException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(userName), "user name should not be empty");
        Entity userEntity = getUserEntity(userName);
        String userEmail = userEntity.getStringField("email");
        if (!MailServiceImpl.isValidEmail(userEmail)) {
            throw new InvalidMailAddressException("invalid recipient email address");
        }
        Preconditions.checkNotNull(userEmail, "e-mail for user " + userName + " was not specified.");
        String newPassword = passwordGeneratorService.generatePassword();

        updateUserPassword(userEntity, newPassword);
        sendNewPassword(userEmail, userName, newPassword);
    }

    private Entity getUserEntity(final String userName) {
        Entity userEntity = securityService.getUserEntity(userName);
        if (userEntity == null) {
            throw new UsernameNotFoundException("Username " + userName + " not found");
        }
        return userEntity;
    }

    private void updateUserPassword(final Entity userEntity, final String password) {
        userEntity.setField("password", password);
        userEntity.setField("passwordConfirmation", password);
        userEntity.getDataDefinition().save(userEntity);
    }

    private void sendNewPassword(final String userEmail, final String userName, final String newPassword) {
        String topic = translationService.translate("security.message.passwordReset.mail.topic", getLocale(), company);
        String body = translationService.translate("security.message.passwordReset.mail.body", getLocale(), userName,
                newPassword, company, contactMail);
        mailService.sendEmail(userEmail, topic, body);
    }

}
