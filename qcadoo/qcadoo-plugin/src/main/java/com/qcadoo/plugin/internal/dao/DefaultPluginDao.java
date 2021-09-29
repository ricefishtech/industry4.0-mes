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
package com.qcadoo.plugin.internal.dao;

import com.google.common.collect.Sets;
import com.qcadoo.model.beans.qcadooPlugin.QcadooPluginPlugin;
import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.internal.api.PluginDao;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class DefaultPluginDao implements PluginDao {

    private static final String L_PLUGIN = "plugin";

    @Autowired
    @Qualifier(L_PLUGIN)
    private SessionFactory sessionFactory;

    @Override
    @Transactional(L_PLUGIN)
    public void save(final QcadooPluginPlugin plugin) {
        sessionFactory.getCurrentSession().save(plugin);
    }

    @Override
    @Transactional(L_PLUGIN)
    public void save(final Plugin plugin) {
        QcadooPluginPlugin existingPlugin = get(plugin.getIdentifier());
        if (existingPlugin == null) {
            existingPlugin = new QcadooPluginPlugin(plugin);
        } else {
            existingPlugin.setState(plugin.getState().toString());
            existingPlugin.setVersion(plugin.getVersion().toString());
            existingPlugin.setIsSystem(plugin.isSystemPlugin());
            existingPlugin.setGroupName(plugin.getPluginInformation() == null ? null : plugin.getPluginInformation().getGroup());
            existingPlugin.setLicense(plugin.getPluginInformation() == null ? null : plugin.getPluginInformation().getLicense());

        }
        save(existingPlugin);
    }

    @Override
    @Transactional(L_PLUGIN)
    public void delete(final QcadooPluginPlugin plugin) {
        sessionFactory.getCurrentSession().delete(plugin);
    }

    @Override
    @Transactional(L_PLUGIN)
    public void delete(final Plugin plugin) {
        QcadooPluginPlugin existingPlugin = get(plugin.getIdentifier());
        if (existingPlugin != null) {
            delete(existingPlugin);
        }
    }

    @Override
    @Transactional(value = L_PLUGIN, readOnly = true)
    @SuppressWarnings("unchecked")
    public Set<QcadooPluginPlugin> list() {
        return Sets.newHashSet(sessionFactory.getCurrentSession().createCriteria(QcadooPluginPlugin.class).list());
    }

    private QcadooPluginPlugin get(final String identifier) {
        return (QcadooPluginPlugin) sessionFactory.getCurrentSession().createCriteria(QcadooPluginPlugin.class)
                .add(Restrictions.eq("identifier", identifier)).uniqueResult();
    }

    void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
