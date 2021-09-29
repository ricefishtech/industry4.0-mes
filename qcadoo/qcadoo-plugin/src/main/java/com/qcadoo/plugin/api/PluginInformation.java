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
package com.qcadoo.plugin.api;

/**
 * Plugin's additional information.
 * 
 * @since 0.4.0
 */
public class PluginInformation {

    private final String description;

    private final String vendor;

    private final String vendorUrl;

    private final String license;

    private final String name;

    private final String group;

    /**
     * Creates additional information with given fields.
     * 
     * @param name
     *            readable name
     * @param description
     *            description
     * @param vendor
     *            vendor
     * @param vendorUrl
     *            verdor's URL
     * @param license
     */
    public PluginInformation(final String name, final String description, final String vendor, final String vendorUrl,
            final String license, final String group) {
        super();
        this.name = name;
        this.description = description;
        this.vendor = vendor;
        this.vendorUrl = vendorUrl;
        this.license = license;
        this.group = group;
    }

    /**
     * Returns plugin's license
     * 
     * @return license
     */
    public String getLicense() {
        return license;
    }

    /**
     * Returns plugin's description
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns plugin's vendor
     * 
     * @return vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Return's plugin's vendor's URL.
     * 
     * @return vendor's url
     */
    public String getVendorUrl() {
        return vendorUrl;
    }

    /**
     * Return plugin's readable name.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    public String getGroup(){
        return this.group;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
        result = prime * result + ((vendorUrl == null) ? 0 : vendorUrl.hashCode());
        result = prime * result + ((license == null) ? 0 : license.hashCode());
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
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
        PluginInformation other = (PluginInformation) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (vendor == null) {
            if (other.vendor != null) {
                return false;
            }
        } else if (!vendor.equals(other.vendor)) {
            return false;
        }
        if (vendorUrl == null) {
            if (other.vendorUrl != null) {
                return false;
            }
        } else if (!vendorUrl.equals(other.vendorUrl)) {
            return false;
        }
        if (license == null) {
            if (other.license != null) {
                return false;
            }
        } else if (!license.equals(other.license)) {
            return false;
        }
        if (group == null) {
            if (other.group != null) {
                return false;
            }
        } else if (!group.equals(other.group)) {
            return false;
        }
        return true;
    }
}
