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
package com.qcadoo.view.internal.ribbon.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.ribbon.RibbonActionItem;

public interface InternalRibbonActionItem extends RibbonActionItem {

    /**
     * Set defined item action
     * 
     * @param clickAction
     *            defined item action
     */
    void setAction(String clickAction);

    /**
     * Set identifier of this ribbon item
     * 
     * @param name
     *            identifier of this ribbon item
     */
    void setName(String name);

    /**
     * Set item accesskey
     *
     * @param accesskey
     *            item accesskey
     */
    void setAccesskey(String accesskey);

    /**
     * Sets this item default state
     * 
     * @param enabled
     *            true when this item should be default enabled or false when this item should be default disabled
     */
    void setDefaultEnabled(boolean defaultEnabled);

    /**
     * Set item type
     * 
     * @param type
     *            item type
     */
    void setType(Type type);

    /**
     * Generates JSON representation of this ribbon item
     * 
     * @return JSON representation of this ribbon item
     * @throws JSONException
     */
    JSONObject getAsJson() throws JSONException;

    /**
     * Sets script of this ribbon item
     * 
     * @param script
     *            script of this ribbon item
     */
    void setScript(String script);

    /**
     * Returns copy of this item - internal usage only
     * 
     * @return copy of this item
     */
    InternalRibbonActionItem getCopy();

    /**
     * Returns information if this item state should be updated
     * 
     * @return information if this item state should be updated
     */
    boolean isShouldBeUpdated();

    /**
     * Defines if this item should be permanently disabled. Permanently disabled means that you can not enable them using {@link
     * RibbonActionItem.setEnabled()}
     * 
     * @param permanentlyDisabled
     *            true if this item should be permanently disabled
     */
    void setPermanentlyDisabled(boolean permanentlyDisabled);

}
