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
package com.qcadoo.model.internal.types;

public final class EnumTypeKey {

    private final String value;

    private final String originPluginIdentifier;

    public EnumTypeKey(final String value, final String originPluginIdentifier) {
        this.value = value;
        this.originPluginIdentifier = originPluginIdentifier;
    }

    public String getValue() {
        return value;
    }

    public String getOriginPluginIdentifier() {
        return originPluginIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((originPluginIdentifier == null) ? 0 : originPluginIdentifier.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EnumTypeKey other = (EnumTypeKey) obj;
        if (originPluginIdentifier == null) {
            if (other.originPluginIdentifier != null) {
                return false;
            }
        } else if (!originPluginIdentifier.equals(other.originPluginIdentifier)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EnumTypeKey [value=" + value + ", originPluginIdentifier=" + originPluginIdentifier + "]";
    }

}
