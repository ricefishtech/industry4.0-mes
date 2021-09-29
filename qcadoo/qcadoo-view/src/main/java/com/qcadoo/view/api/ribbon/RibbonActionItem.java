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
package com.qcadoo.view.api.ribbon;

/**
 * Represents single ribbon item
 * 
 * @since 0.4.0
 */
public interface RibbonActionItem {

    /**
     * Type of ribbon item
     */
    public static enum Type {
        /**
         * simple big button
         */
        BIG_BUTTON,
        /**
         * simple small button
         */
        SMALL_BUTTON,
        /**
         * checkbox
         */
        CHECKBOX,
        /**
         * combobox
         */
        COMBOBOX,
        /**
         * small empty space
         */
        SMALL_EMPTY_SPACE
    }

    /**
     * Get defined item click action
     * 
     * @return defined item click action
     */
    String getAction();

    /**
     * Get identifier of this ribbon item
     *
     * @return identifier of this ribbon item
     */
    String getName();

    /**
     * Get identifier of this ribbon item
     *
     * @return identifier of this ribbon item
     */
    String getAccesskey();

    /**
     * Get item type
     * 
     * @return item type
     */
    Type getType();

    /**
     * Get item icon (null if item without icon)
     * 
     * @return item icon
     */
    String getIcon();

    /**
     * Set item icon (null if item without icon)
     * 
     * @param icon
     *            item icon
     */
    void setIcon(String icon);

    /**
     * Returns script of this ribbon item
     * 
     * @return script of this ribbon item
     */
    String getScript();

    /**
     * Returns true if this item is enabled
     * 
     * @return true if this item is enabled
     */
    boolean isEnabled();

    /**
     * Sets this item state
     * 
     * @param enabled
     *            true when this item should be enabled or false when this item should be disabled
     */
    void setEnabled(boolean enabled);

    /**
     * Returns message connected to this item
     * 
     * @return message connected to this item
     */
    String getMessage();

    /**
     * sets message connected to this item
     * 
     * @param message
     *            <b>translation key</b> for new message connected to this item
     */
    void setMessage(String message);

    /**
     * Informs that this item state should be updated
     * 
     * @param shouldBeUpdated
     *            true if this item state should be updated
     */
    void requestUpdate(boolean shouldBeUpdated);
}
