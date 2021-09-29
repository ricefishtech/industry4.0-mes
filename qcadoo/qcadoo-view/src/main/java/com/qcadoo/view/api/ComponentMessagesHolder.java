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
package com.qcadoo.view.api;

import com.qcadoo.model.api.validators.ErrorMessage;
import com.qcadoo.model.api.validators.GlobalMessage;
import com.qcadoo.view.api.ComponentState.MessageType;

/**
 * ComponentMessagesHolder is simple container of component-related messages to display.
 * 
 * @author marcinkubala
 * @since 1.2.1
 */
public interface ComponentMessagesHolder {

    /**
     * Adds message to this component with type set to MessageType.FAILURE. Message will automatically close after some time.
     * 
     * @param errorMessage
     *            validation error message
     */
    void addMessage(final ErrorMessage errorMessage);
    
    /**
     * Adds message to this component with type set to MessageType.INFO. Message will automatically close after some time.
     * 
     * @param globalMessage
     *            validation message
     */
    void addMessage(final GlobalMessage globalMessage);

    /**
     * Adds message (translated using given key) to this component. Message will automatically close after some time.
     * 
     * @param messageTranslationKey
     *            translation key for message content
     * @param type
     *            message type
     * @param args
     *            message's arguments
     */
    void addMessage(final String messageTranslationKey, final MessageType type, String... args);

    /**
     * Adds message (translated using given key) to this component.
     * 
     * @param messageTranslationKey
     *            translation key for message content
     * @param type
     *            message type
     * @param autoClose
     *            true if this message should automatically close after some time
     * @param args
     *            message's arguments
     */
    void addMessage(final String messageTranslationKey, final MessageType type, final boolean autoClose, final String... args);

    /**
     * Adds message (translated using given key) to this component.
     *
     * @param messageTranslationKey
     *            translation key for message content
     * @param type
     *            message type
     * @param autoClose
     *            true if this message should automatically close after some time
     * @param args
     *            message's arguments
     * @param extraLarge
     */
    void addMessage(final String messageTranslationKey, final MessageType type, final boolean autoClose, final boolean extraLarge, final String... args);

    /**
     * Adds already translated message to this component. Message will automatically close after some time.
     * 
     * @param translatedMessage
     *            translated message content
     * @param type
     *            message type
     * @param autoClose
     *            true if this message should automatically close after some time
     */
    void addTranslatedMessage(final String translatedMessage, final MessageType type);

    /**
     * Adds already translated message to this component.
     * 
     * @param translatedMessage
     *            translated message content
     * @param type
     *            message type
     * @param autoClose
     *            true if this message should automatically close after some time
     */
    void addTranslatedMessage(final String translatedMessage, final MessageType type, final boolean autoClose);

    /**
     * Adds already translated message to this component.
     *
     * @param translatedMessage
     *            translated message content
     * @param type
     *            message type
     * @param autoClose
     *            true if this message should automatically close after some time
     * @param extraLarge
     *
     */
    void addTranslatedMessage(final String translatedMessage, final MessageType type, final boolean autoClose, final boolean extraLarge);
}
