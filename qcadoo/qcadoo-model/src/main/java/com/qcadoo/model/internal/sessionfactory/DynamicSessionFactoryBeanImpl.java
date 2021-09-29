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
package com.qcadoo.model.internal.sessionfactory;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import com.qcadoo.model.internal.api.DynamicSessionFactoryBean;

public class DynamicSessionFactoryBeanImpl implements DynamicSessionFactoryBean {

    private final LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();

    @Override
    public void afterPropertiesSet() {
        // ignore
    }

    @Override
    public void initialize(final Resource[] hbms) {
        factoryBean.setMappingLocations(hbms);

        try {
            factoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        factoryBean.setBeanClassLoader(classLoader);
    }

    @Override
    public SessionFactory getObject() {
        return new DynamicSessionFactory(factoryBean);
    }

    @Override
    public Class<?> getObjectType() {
        return factoryBean.getObjectType();
    }

    @Override
    public boolean isSingleton() {
        return factoryBean.isSingleton();
    }

    @Override
    public void destroy() {
        factoryBean.destroy();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(final RuntimeException ex) {
        return factoryBean.translateExceptionIfPossible(ex);
    }

    public void setDataSource(final DataSource dataSource) {
        factoryBean.setDataSource(dataSource);
    }

    public void setHibernateProperties(final Properties hibernateProperties) {
        factoryBean.setHibernateProperties(hibernateProperties);
    }

    protected LocalSessionFactoryBean getFactoryBean() {
        return factoryBean;
    }

}
