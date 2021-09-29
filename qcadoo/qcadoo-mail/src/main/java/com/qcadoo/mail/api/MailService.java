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
package com.qcadoo.mail.api;

/**
 * Service for sending email.
 * 
 * @since 1.1.5
 */
public interface MailService {

    /**
     * Send plain-text mail using default (specified in mail.username property) sender e-mail.
     * 
     * @param recipient
     *            e-mail recipient
     * @param subject
     *            e-mail subject
     * @param body
     *            e-mail body
     * 
     * @throws IllegalArgumentException
     *             if given recipient is not valid e-mail address or any argument is blank
     * @throws MailConfigurationException
     *             if mail.properties is not valid
     * @throws InvalidMailAddressException
     *             if one of email address is blank or invalid
     */
    void sendEmail(String recipient, String subject, String body) throws IllegalArgumentException,
            MailConfigurationException, InvalidMailAddressException;
}
