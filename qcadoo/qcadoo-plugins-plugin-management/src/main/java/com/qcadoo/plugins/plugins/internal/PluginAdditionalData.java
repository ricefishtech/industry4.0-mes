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
package com.qcadoo.plugins.plugins.internal;

public class PluginAdditionalData {

    private final String name;

    private final String description;

    private final String vendor;

    private final String vendorUrl;

    private final boolean isSystem;

    private final String license;

    private final String group;

    public PluginAdditionalData(final String name, final String description, final String vendor, final String vendorUrl,
            final boolean isSystem, final String group, final String license) {
        this.name = name;
        this.description = description;
        this.vendor = vendor;
        this.vendorUrl = vendorUrl;
        this.isSystem = isSystem;
        this.group = group;
        this.license = license;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public String getVendorUrl() {
        return vendorUrl;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public String getGroup(){
        return group;
    }

    public String getLicense() {
        return license;
    }
}
