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
package com.qcadoo.model;

import java.io.File;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.qcadoo.model.internal.sessionfactory.DynamicSessionFactoryBeanImpl;

public class Utils {

    private Utils() {

    }

    public static final String HBM_DTD_PATH = new File("src/test/resources/hibernate-mapping-3.0.dtd").getAbsolutePath();

    public static final String SPRING_CONTEXT_PATH = "spring.xml";

    public static final Resource FULL_FIRST_ENTITY_XML_RESOURCE = new FileSystemResource(
            "src/test/resources/model/full/firstEntity.xml");

    public static final Resource FULL_SECOND_ENTITY_XML_RESOURCE = new FileSystemResource(
            "src/test/resources/model/full/secondEntity.xml");

    public static final Resource FULL_THIRD_ENTITY_XML_RESOURCE = new FileSystemResource(
            "src/test/resources/model/full/thirdEntity.xml");

    public static final Resource OTHER_FIRST_ENTITY_XML_RESOURCE = new FileSystemResource(
            "src/test/resources/model/other/firstEntity.xml");

    public static final Resource OTHER_SECOND_ENTITY_XML_RESOURCE = new FileSystemResource(
            "src/test/resources/model/other/secondEntity.xml");

    public static final Resource UNIQUE_COPYABLE_ENTITY_XML_RESOURCE = new FileSystemResource(
            "src/test/resources/model/other/uniqueCopyableEntity.xml");

    public static final Resource UNIQUE_COPYABLE_BROKEN_ENTITY_XML_RESOURCE = new FileSystemResource(
            "src/test/resources/model/other/uniqueCopyableBrokenEntity.xml");

    public static final Resource MODEL_XML_INVALID_RESOURCE = new FileSystemResource("src/test/resources/spring.xml");

    public static final Resource FULL_HBM_RESOURCE = new FileSystemResource("src/test/resources/full.hbm.xml");

    public static final Resource EMPTY_HBM_RESOURCE = new FileSystemResource("src/test/resources/empty.hbm.xml");

    public static DataSource createDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:mes");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    public static DynamicSessionFactoryBeanImpl createNewSessionFactory() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        DynamicSessionFactoryBeanImpl sessionFactory = new DynamicSessionFactoryBeanImpl();
        sessionFactory.setDataSource(createDataSource());
        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }

}
