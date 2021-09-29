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
package com.qcadoo.view.internal.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qcadoo.view.api.ViewDefinitionState;

class ViewDefinitionStateLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewDefinitionState.class);

    private static final String MESSAGE_FORMAT = "View '%s': %s";

    private final String viewName;

    static ViewDefinitionStateLogger forView(final ViewDefinitionState view) {
        return new ViewDefinitionStateLogger(view.getName());
    }

    private ViewDefinitionStateLogger(final String viewName) {
        this.viewName = viewName;
    }

    public void logWarn(final String message) {
        logWarn(message, null);
    }

    public void logDebug(final String message) {
        LOGGER.debug(message);
    }

    public void logWarn(final String message, final Throwable cause) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(formatMessage(message), cause);
        }
    }

    private String formatMessage(final String message) {
        return String.format(MESSAGE_FORMAT, viewName, message);
    }

}
