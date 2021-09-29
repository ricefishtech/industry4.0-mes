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
package com.qcadoo.mail.internal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.mail.api.InvalidMailAddressException;
import com.qcadoo.mail.api.MailConfigurationException;

public class MailServiceImplTest {

    private static final String DEFAULT_SENDER = "test-sender@qcadoo.com";

    private static final String DEFAULT_RECIPIENT = "test-recipient@qcadoo.com";

    private static final String DEFAULT_SUBJECT = "test-subject";

    private static final String DEFAULT_BODY = "test-body";

    private MailServiceImpl mailServiceImpl;

    private JavaMailSender mailSender;

    private MimeMessage mimeMessage;

    @Before
    public final void init() {
        mailServiceImpl = new MailServiceImpl();

        mailSender = mock(JavaMailSender.class);
        mimeMessage = mock(MimeMessage.class);
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        ReflectionTestUtils.setField(mailServiceImpl, "mailSender", mailSender);
        ReflectionTestUtils.setField(mailServiceImpl, "defaultSender", DEFAULT_SENDER);
    }

    @Test
    public final void shouldSendPlainTextEmail() throws Exception {
        // given
        ArgumentCaptor<MimeMessage> mailMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);

        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);

        // then
        verify(mailSender, Mockito.times(1)).send(mailMessageCaptor.capture());
        verify(mimeMessage).setFrom(new InternetAddress(DEFAULT_SENDER));
        verify(mimeMessage).setRecipient(Message.RecipientType.TO, new InternetAddress(DEFAULT_RECIPIENT));
        verify(mimeMessage).setSubject(DEFAULT_SUBJECT);
        verify(mimeMessage).setContent(DEFAULT_BODY, "text/html");
    }

    @Test(expected = MailConfigurationException.class)
    public final void shouldThrowExceptionIfDefaultSenderIsNull() throws Exception {
        // given
        setDefaultSender(null);

        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = MailConfigurationException.class)
    public final void shouldThrowExceptionIfDefaultSenderIsEmpty() throws Exception {
        // given
        setDefaultSender("");

        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = MailConfigurationException.class)
    public final void shouldThrowExceptionIfDefaultSenderIsBlank() throws Exception {
        // given
        setDefaultSender("  ");

        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = MailConfigurationException.class)
    public final void shouldThrowExceptionIfDefaultSenderIsNotValid() throws Exception {
        // given
        setDefaultSender("invalid");

        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    private void setDefaultSender(final String defaultSender) {
        ReflectionTestUtils.setField(mailServiceImpl, "defaultSender", defaultSender);
    }

    @Test(expected = InvalidMailAddressException.class)
    public final void shouldThrowExceptionIfSenderIsNull() throws Exception {
        // when
        mailServiceImpl.sendHtmlTextEmail(null, DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = InvalidMailAddressException.class)
    public final void shouldThrowExceptionIfSenderIsEmpty() throws Exception {
        // when
        mailServiceImpl.sendHtmlTextEmail("", DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = InvalidMailAddressException.class)
    public final void shouldThrowExceptionIfSenderIsBlank() throws Exception {
        // when
        mailServiceImpl.sendHtmlTextEmail(" ", DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = InvalidMailAddressException.class)
    public final void shouldThrowExceptionIfSenderIsNotValid() throws Exception {
        // when
        mailServiceImpl.sendHtmlTextEmail("invalid", DEFAULT_RECIPIENT, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = InvalidMailAddressException.class)
    public final void shouldThrowExceptionIfRecipientIsNull() throws Exception {
        // when
        mailServiceImpl.sendEmail(null, DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = InvalidMailAddressException.class)
    public final void shouldThrowExceptionIfRecipientIsEmpty() throws Exception {
        // when
        mailServiceImpl.sendEmail("", DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = InvalidMailAddressException.class)
    public final void shouldThrowExceptionIfRecipientIsBlank() throws Exception {
        // when
        mailServiceImpl.sendEmail(" ", DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = InvalidMailAddressException.class)
    public final void shouldThrowExceptionIfRecipientIsNotValid() throws Exception {
        // when
        mailServiceImpl.sendEmail("invalid", DEFAULT_SUBJECT, DEFAULT_BODY);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfSubjectIsNull() throws Exception {
        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, null, DEFAULT_BODY);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfSubjectIsEmpty() throws Exception {
        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, "", DEFAULT_BODY);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfSubjectIsBlank() throws Exception {
        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, " ", DEFAULT_BODY);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfBodyIsNull() throws Exception {
        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, DEFAULT_SUBJECT, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfBodyIsEmpty() throws Exception {
        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, DEFAULT_SUBJECT, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowExceptionIfBodyIsBlank() throws Exception {
        // when
        mailServiceImpl.sendEmail(DEFAULT_RECIPIENT, DEFAULT_SUBJECT, " ");
    }

}
