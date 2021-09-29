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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Service for getting dictionaries.
 * 
 * @since 0.4.0
 */
public interface DictionaryService {

    List<String> getKeys(String dictionary);

    List<String> getActiveKeys(String dictionary);

    /**
     * Return all values for given dictionary's name.
     * 
     * @param dictionary
     *            dictionary's name
     * @return the dictionary's values
     */
    Map<String, String> getValues(String dictionary, Locale locale);

    Map<String, String> getKeyValues(String dictionary, Locale locale);

    /**
     * Return all defined dictionaries.
     * 
     * @return the dictionaries
     */
    Set<String> getDictionaries();

    /**
     * Translate dictionary name.
     * 
     * @param dictionary
     *            dictionary
     * @param locale
     *            locale
     * @return translated dictionary name
     */
    String getName(String dictionary, Locale locale);

    /**
     * Return dictionary item's entity
     * 
     * @since 1.2.1
     * 
     * @param dictionary
     *            dictionary's name
     * @param item
     *            dictionary item's name
     * @return dictionary item's entity
     */
    Entity getItemEntity(String dictionary, String item);

    Boolean checkIfUnitIsInteger(String unit);

}
