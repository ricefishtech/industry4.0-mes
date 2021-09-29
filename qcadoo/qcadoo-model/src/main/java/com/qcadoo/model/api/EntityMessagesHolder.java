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
package com.qcadoo.model.api;

import com.qcadoo.model.api.validators.ErrorMessage;
import com.qcadoo.model.api.validators.GlobalMessage;

import java.util.List;
import java.util.Map;

public interface EntityMessagesHolder {

    /**
     * Add global info message not related with fields.
     * 
     * @param message message
     * @param vars message's vars
     */
    void addGlobalMessage(final String message, final String... vars);
    
    /**
     * Add global message, not related with fields.
     * 
     * @param message message
     * @param autoClose autoClose
     * @param extraLarge extraLarge
     * @param vars message's vars
     */
    void addGlobalMessage(final String message, final boolean autoClose, final boolean extraLarge, final String... vars);
    
    /**
     * Set global error, not related with fields.
     * 
     * @param message
     *            message
     * @param vars
     *            message's vars
     */
    void addGlobalError(final String message, final String... vars);

    /**
     * Set global error, not related with fields.
     * 
     * @param message
     *            message
     * @param autoClose
     *            autoClose
     * @param autoClose
     *            autoClose
     * @param vars
     *            message's vars
     */
    void addGlobalError(final String message, final boolean autoClose,final boolean extraLarge, final String... vars);

    /**
     * Set global error, not related with fields.
     *
     * @param message
     *            message
     * @param autoClose
     *            autoClose
     * @param vars
     *            message's vars
     */
    void addGlobalError(final String message, final boolean autoClose, final String... vars);

    /**
     * Set error for given field.
     * 
     * @param fieldDefinition
     *            field's definition
     * @param message
     *            message
     * @param vars
     *            message's vars
     */
    void addError(final FieldDefinition fieldDefinition, final String message, final String... vars);

    /**
     * Return all global errors.
     * 
     * @return errors
     */
    List<ErrorMessage> getGlobalErrors();
    
    /**
     * Return all global messages.
     * 
     * @return messages
     */
    List<GlobalMessage> getGlobalMessages();

    /**
     * Return all field's errors.
     * 
     * @return fields' errors
     */
    Map<String, ErrorMessage> getErrors();

    /**
     * Return error for given field.
     * 
     * @param fieldName
     *            field's name
     * @return field's error
     */
    ErrorMessage getError(final String fieldName);

}
