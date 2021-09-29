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

import org.springframework.mail.MailException;

/**
 * Exception thrown when a mail configuration error is encountered.
 * 
 * @since 1.1.5
 */
@SuppressWarnings("serial")
public class MailConfigurationException extends MailException {

    /**
     * Create a new MailConfigurationException
     * 
     * @param message
     *            the detail message
     */
    public MailConfigurationException(final String message) {
        super(message);
    }

    /**
     * Create a new MailConfigurationException
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the root cause
     */
    public MailConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
