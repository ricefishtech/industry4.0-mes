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

/**
 * A String with priority option for sorting
 * 
 */
public class PrioritizedString implements Comparable<PrioritizedString> {

    private Pair<String, Integer> pair;

    public PrioritizedString(final String string) {
        pair = new Pair<String, Integer>(string, 0);
    }

    public PrioritizedString(final String string, final Integer priority) {
        pair = new Pair<String, Integer>(string, priority);
    }

    public String getString() {
        return pair.getKey();
    }

    public void setString(final String string) {
        pair.setKey(string);
    }

    public Integer getPriority() {
        return pair.getValue();
    }

    public void setPriority(final Integer priority) {
        pair.setValue(priority);
    }

    @Override
    public boolean equals(final Object obj) {
        return pair.equals(obj);
    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    @Override
    public String toString() {
        return pair.toString();
    }

    @Override
    public int compareTo(final PrioritizedString o) {
        String a = getPriority() + getString();
        String b = o.getPriority() + o.getString();
        return a.compareTo(b);
    }
}
