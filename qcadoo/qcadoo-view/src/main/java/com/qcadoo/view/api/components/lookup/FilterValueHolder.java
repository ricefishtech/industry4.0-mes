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
package com.qcadoo.view.api.components.lookup;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONObject;

/**
 * Holder that keeps key/value pairs to pass to the criteria modifier of lookup.
 * 
 * @author tola
 * @since 1.2.1
 */
public interface FilterValueHolder {

    /**
     * Put a key/int pair in the FilterValueHolder.
     * 
     * @param key
     * @param value
     */
    void put(String key, Integer value);

    /**
     * Put a key/double pair in the FilterValueHolder.
     * 
     * @param key
     * @param value
     */
    void put(String key, BigDecimal value);

    /**
     * Put a key/Long pair in the FilterValueHolder.
     * 
     * @param key
     * @param value
     */
    void put(String key, Long value);

    /**
     * Put a key/List<Long> pair in the FilterValueHolder.
     * 
     * @param key
     * @param value
     */
    void put(String key, List<Long> value);

    /**
     * Put a key/String pair in the FilterValueHolder.
     * 
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     * Put a key/boolean pair in the FilterValueHolder.
     * 
     * @param key
     * @param value
     */
    void put(String key, boolean value);

    /**
     * Get the boolean value associated with a key.
     * 
     * @param key
     * @return
     */
    boolean getBoolean(String key);

    /**
     * Get the int value associated with a key.
     * 
     * @param key
     * @return
     */
    Integer getInteger(String key);

    /**
     * Get the boolean value associated with a key.
     * 
     * @param key
     * @return
     */
    BigDecimal getBigDecimal(String key);

    /**
     * Get the Long value associated with a key.
     * 
     * @param key
     * @return
     */
    Long getLong(String key);

    /**
     * Get the List<Long> value associated with a key.
     * 
     * @param key
     * @return
     */
    List<Long> getListOfLongs(String key);

    /**
     * Get the String value associated with a key.
     * 
     * @param key
     * @return
     */
    String getString(String key);

    /**
     * Determine if the FilterValueHolder contains a specific key.
     * 
     * @param key
     * @return
     */
    boolean has(String key);

    /**
     * Remove a name and its value, if present.
     * 
     * @param key
     * @return The value that was associated with the name, or null if there was no value.
     */
    Object remove(String key);

    /**
     * Initializes holder with key/value pairs from JSONObject.
     * 
     * @param jsonObject
     */
    void initialize(final JSONObject jsonObject);

    /**
     * Indicates if any pair of key and value is present in holder.
     * 
     * @return
     */
    boolean isEmpty();

    /**
     * Returns JSON representation of holder values;
     * 
     * @return
     */
    JSONObject toJSON();
}
