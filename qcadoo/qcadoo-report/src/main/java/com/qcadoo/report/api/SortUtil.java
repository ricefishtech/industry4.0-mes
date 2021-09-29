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
package com.qcadoo.report.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Util class for sorting
 */
public final class SortUtil {

    private SortUtil() {
        // empty
    }

    /**
     * Sort given map by keys, using specified comparator
     * 
     * @param <T>
     *            key type
     * @param <V>
     *            value type
     * @param map
     *            Map, which will be sorted
     * @param comparator
     *            Comparator for comparing keys
     * @return new instance of map, sorted by keys
     */
    public static <T, V> Map<T, V> sortMapUsingComparator(final Map<T, V> map, final Comparator<T> comparator) {
        List<T> operationList = new LinkedList<T>(map.keySet());

        Collections.sort(operationList, comparator);

        Map<T, V> result = new LinkedHashMap<T, V>();

        for (T key : operationList) {
            result.put(key, map.get(key));
        }
        return result;
    }

}
