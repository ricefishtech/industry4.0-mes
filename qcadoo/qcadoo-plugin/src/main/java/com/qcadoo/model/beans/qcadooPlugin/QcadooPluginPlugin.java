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
package com.qcadoo.model.beans.qcadooPlugin;

import com.qcadoo.plugin.api.Plugin;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Locale;

@Entity
@Table(name = "qcadooplugin_plugin")
public class QcadooPluginPlugin {

    @SequenceGenerator(name="ID_GEN", sequenceName="qcadooplugin_plugin_id_seq")
    @GeneratedValue(generator="ID_GEN")
    @Id
    private Long id;

    @Column
    private String identifier;

    @Column
    private String version;

    @Column
    private String state;

    @Column
    private boolean isSystem;

    @Column
    private String groupName;

    @Column
    private String license;

    public QcadooPluginPlugin() {
        // empty
    }

    public QcadooPluginPlugin(final Plugin plugin) {
        identifier = plugin.getIdentifier();
        version = plugin.getVersion().toString();
        setState(plugin.getState().toString());
        isSystem = plugin.isSystemPlugin();
        groupName = plugin.getPluginInformation()== null ? null : plugin.getPluginInformation().getGroup();
        license = plugin.getPluginInformation()== null ? null : plugin.getPluginInformation().getLicense();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = StringUtils.trim(StringUtils.upperCase(state, Locale.ENGLISH));
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getGroupName(){
        return groupName;
    }

    public void setGroupName(final String groupName){
        this.groupName = groupName;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((license == null) ? 0 : license.hashCode());
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
        if (!(obj instanceof QcadooPluginPlugin)) {
            return false;
        }
        QcadooPluginPlugin other = (QcadooPluginPlugin) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (!state.equals(other.state)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        if (isSystem != other.isSystem) {
            return false;
        }
        if (groupName == null) {
            if (other.groupName != null) {
                return false;
            }
        } else if (!groupName.equals(other.groupName)) {
            return false;
        }
        if (license == null) {
            if (other.license != null) {
                return false;
            }
        } else if (!license.equals(other.license)) {
            return false;
        }
        return true;
    }

    public boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(final boolean isSystem) {
        this.isSystem = isSystem;
    }

}