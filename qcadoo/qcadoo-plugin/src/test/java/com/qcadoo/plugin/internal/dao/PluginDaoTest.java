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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;

import com.google.common.collect.Lists;
import com.qcadoo.model.beans.qcadooPlugin.QcadooPluginPlugin;
import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginState;
import com.qcadoo.plugin.api.Version;
import com.qcadoo.plugin.internal.api.InternalPlugin;

public class PluginDaoTest {

    private DefaultPluginDao pluginDao;

    private final SessionFactory sessionFactory = mock(SessionFactory.class);

    private final QcadooPluginPlugin plugin1 = new QcadooPluginPlugin();

    private final QcadooPluginPlugin plugin2 = new QcadooPluginPlugin();

    private final Plugin plugin11 = mock(InternalPlugin.class);

    private final Session session = mock(Session.class);

    private final Criteria criteria = mock(Criteria.class);

    // private ClassPathXmlApplicationContext applicationContext;

    private TransactionStatus txStatus;

    private PlatformTransactionManager txManager;

    @Before
    public void init() {
        txStatus = mock(TransactionStatus.class);
        given(txStatus.isRollbackOnly()).willReturn(false);

        txManager = mock(PlatformTransactionManager.class);
        given(txManager.getTransaction((TransactionDefinition) Mockito.anyObject())).willReturn(txStatus);

        AnnotationTransactionAspect txAspect = AnnotationTransactionAspect.aspectOf();
        txAspect.setTransactionManager(txManager);

        plugin1.setIdentifier("plugin1");
        plugin2.setIdentifier("plugin2");

        given(sessionFactory.getCurrentSession()).willReturn(session);

        given(plugin11.getIdentifier()).willReturn("identifier1");
        given(session.createCriteria(QcadooPluginPlugin.class)).willReturn(criteria);
        given(criteria.add(any(Criterion.class))).willReturn(criteria);

        pluginDao = new DefaultPluginDao();
        pluginDao.setSessionFactory(sessionFactory);
    }

    @Test
    public void shouldSavePersistentPlugin() throws Exception {
        // given

        // when
        pluginDao.save(plugin1);

        // then
        verify(session).save(plugin1);
    }

    @Test
    public void shouldSaveNotPersistentExistingPlugin() throws Exception {
        // given
        given(criteria.uniqueResult()).willReturn(plugin1);
        given(plugin11.getState()).willReturn(PluginState.ENABLED);
        given(plugin11.getVersion()).willReturn(new Version("0.0.0"));

        // when
        pluginDao.save(plugin11);

        // then
        assertEquals(plugin1.getState(), PluginState.ENABLED.toString());
        assertEquals(plugin1.getVersion(), "0.0.0");
        verify(session).save(plugin1);
    }

    @Test
    public void shouldSaveNotPersistentPlugin() throws Exception {
        // given
        given(plugin11.getState()).willReturn(PluginState.ENABLED);
        given(plugin11.getVersion()).willReturn(new Version("0.0.0"));

        // when
        pluginDao.save(plugin11);

        // then
        verify(session, never()).save(plugin1);
        verify(session).save(any(QcadooPluginPlugin.class));
    }

    @Test
    public void shouldDeletePersistentPlugin() throws Exception {
        // given

        // when
        pluginDao.delete(plugin1);

        // then
        verify(session).delete(plugin1);
    }

    @Test
    public void shouldDeleteNotPersistentPlugin() throws Exception {
        // given
        given(criteria.uniqueResult()).willReturn(plugin1);

        // when
        pluginDao.delete(plugin11);

        // then
        verify(session).delete(plugin1);
    }

    @Test
    public void shouldListPlugin() throws Exception {
        // given
        given(criteria.list()).willReturn(Lists.newArrayList(plugin1, plugin2));

        // when
        Set<QcadooPluginPlugin> plugins = pluginDao.list();

        // then
        assertEquals(2, plugins.size());
        assertTrue(plugins.contains(plugin1));
        assertTrue(plugins.contains(plugin2));
    }
}
