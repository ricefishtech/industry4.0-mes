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
package com.qcadoo.plugin.internal.dependencymanager;

import java.io.Serializable;
import java.util.Comparator;

import com.qcadoo.plugin.api.Plugin;

public class DependencyComparator implements Comparator<Plugin>, Serializable {

    private static final long serialVersionUID = 1821666538483568800L;

    @Override
    public int compare(final Plugin o1, final Plugin o2) {
        if (o1.isSystemPlugin() && !o2.isSystemPlugin()) {
            return -1;
        } else if (!o1.isSystemPlugin() && o2.isSystemPlugin()) {
            return 1;
        }
        return 0;
    }
}
