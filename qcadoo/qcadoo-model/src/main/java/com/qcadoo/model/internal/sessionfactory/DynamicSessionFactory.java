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

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.TransactionManager;

import org.hibernate.Cache;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.StatelessSession;
import org.hibernate.TypeHelper;
import org.hibernate.cache.QueryCache;
import org.hibernate.cache.Region;
import org.hibernate.cache.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.classic.Session;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.InfrastructureProxy;

public class DynamicSessionFactory implements SessionFactory, SessionFactoryImplementor, InfrastructureProxy {

    private static final long serialVersionUID = -7254335636932770807L;

    private final FactoryBean<SessionFactory> sessionFactoryBean;

    private volatile SessionFactory sessionFactory;

    public DynamicSessionFactory(final FactoryBean<SessionFactory> sessionFactoryBean) {
        this.sessionFactoryBean = sessionFactoryBean;
    }

    private SessionFactory getSessionFactory(final boolean allowCreation) {
        SessionFactory result = sessionFactory;
        if (result == null) {
            synchronized (this) {
                result = sessionFactory;
                if (result == null) {
                    if (!allowCreation) {
                        throw new IllegalStateException("DynamicSessionFactory is not ready to use");
                    }
                    try {
                        result = sessionFactory = sessionFactoryBean.getObject();
                    } catch (Exception e) {
                        throw new IllegalStateException(e.getMessage(), e);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Reference getReference() throws NamingException {
        return getSessionFactory(true).getReference();
    }

    @Override
    public Session openSession() {
        return getSessionFactory(true).openSession();
    }

    @Override
    public Session openSession(final Interceptor interceptor) {
        return getSessionFactory(true).openSession(interceptor);
    }

    @Override
    public Session openSession(final Connection connection) {
        return getSessionFactory(true).openSession(connection);
    }

    @Override
    public Session openSession(final Connection connection, final Interceptor interceptor) {
        return getSessionFactory(true).openSession(connection, interceptor);
    }

    @Override
    public Session getCurrentSession() {
        return getSessionFactory(true).getCurrentSession();
    }

    @Override
    public StatelessSession openStatelessSession() {
        return getSessionFactory(true).openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(final Connection connection) {
        return getSessionFactory(true).openStatelessSession(connection);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ClassMetadata getClassMetadata(final Class entityClass) {
        return getSessionFactory(true).getClassMetadata(entityClass);
    }

    @Override
    public ClassMetadata getClassMetadata(final String entityName) {
        return getSessionFactory(true).getClassMetadata(entityName);
    }

    @Override
    public CollectionMetadata getCollectionMetadata(final String roleName) {
        return getSessionFactory(true).getCollectionMetadata(roleName);
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() {
        return getSessionFactory(true).getAllClassMetadata();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map getAllCollectionMetadata() {
        return getSessionFactory(true).getAllCollectionMetadata();
    }

    @Override
    public Statistics getStatistics() {
        return getSessionFactory(true).getStatistics();
    }

    @Override
    public void close() {
        getSessionFactory(true).close();
    }

    @Override
    public boolean isClosed() {
        return getSessionFactory(true).isClosed();
    }

    @Override
    public Cache getCache() {
        return getSessionFactory(true).getCache();
    }

    @Override
    @SuppressWarnings({ "deprecation", "rawtypes" })
    public void evict(final Class persistentClass) {
        getSessionFactory(true).evict(persistentClass);
    }

    @Override
    @SuppressWarnings({ "deprecation", "rawtypes" })
    public void evict(final Class persistentClass, final Serializable id) {
        getSessionFactory(true).evict(persistentClass, id);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void evictEntity(final String entityName) {
        getSessionFactory(true).evictEntity(entityName);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void evictEntity(final String entityName, final Serializable id) {
        getSessionFactory(true).evictEntity(entityName, id);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void evictCollection(final String roleName) {
        getSessionFactory(true).evictCollection(roleName);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void evictCollection(final String roleName, final Serializable id) {
        getSessionFactory(true).evictCollection(roleName, id);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void evictQueries(final String cacheRegion) {
        getSessionFactory(true).evictQueries(cacheRegion);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void evictQueries() {
        getSessionFactory(true).evictQueries();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Set getDefinedFilterNames() {
        return getSessionFactory(true).getDefinedFilterNames();
    }

    @Override
    public FilterDefinition getFilterDefinition(final String filterName) {
        return getSessionFactory(true).getFilterDefinition(filterName);
    }

    @Override
    public boolean containsFetchProfileDefinition(final String name) {
        return getSessionFactory(true).containsFetchProfileDefinition(name);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return getSessionFactory(true).getTypeHelper();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getIdentifierGeneratorFactory();
    }

    @Override
    public Type getIdentifierType(final String className) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getIdentifierType(className);
    }

    @Override
    public String getIdentifierPropertyName(final String className) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getIdentifierPropertyName(className);
    }

    @Override
    public Type getReferencedPropertyType(final String className, final String propertyName) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getReferencedPropertyType(className, propertyName);
    }

    @Override
    public TypeResolver getTypeResolver() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getTypeResolver();
    }

    @Override
    public Properties getProperties() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getProperties();
    }

    @Override
    public EntityPersister getEntityPersister(final String entityName) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getEntityPersister(entityName);
    }

    @Override
    public CollectionPersister getCollectionPersister(final String role) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getCollectionPersister(role);
    }

    @Override
    public Dialect getDialect() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getDialect();
    }

    @Override
    public Interceptor getInterceptor() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getInterceptor();
    }

    @Override
    public QueryPlanCache getQueryPlanCache() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getQueryPlanCache();
    }

    @Override
    public Type[] getReturnTypes(final String queryString) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getReturnTypes(queryString);
    }

    @Override
    public String[] getReturnAliases(final String queryString) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getReturnAliases(queryString);
    }

    @Override
    public ConnectionProvider getConnectionProvider() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getConnectionProvider();
    }

    @Override
    public String[] getImplementors(final String className) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getImplementors(className);
    }

    @Override
    public String getImportedClassName(final String name) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getImportedClassName(name);
    }

    @Override
    public TransactionManager getTransactionManager() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getTransactionManager();
    }

    @Override
    public QueryCache getQueryCache() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getQueryCache();
    }

    @Override
    public QueryCache getQueryCache(final String regionName) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getQueryCache(regionName);
    }

    @Override
    public UpdateTimestampsCache getUpdateTimestampsCache() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getUpdateTimestampsCache();
    }

    @Override
    public StatisticsImplementor getStatisticsImplementor() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getStatisticsImplementor();
    }

    @Override
    public NamedQueryDefinition getNamedQuery(final String queryName) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getNamedQuery(queryName);
    }

    @Override
    public NamedSQLQueryDefinition getNamedSQLQuery(final String queryName) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getNamedSQLQuery(queryName);
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(final String name) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getResultSetMapping(name);
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(final String rootEntityName) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getIdentifierGenerator(rootEntityName);
    }

    @Override
    public Region getSecondLevelCacheRegion(final String regionName) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getSecondLevelCacheRegion(regionName);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map getAllSecondLevelCacheRegions() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getAllSecondLevelCacheRegions();
    }

    @Override
    public SQLExceptionConverter getSQLExceptionConverter() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getSQLExceptionConverter();
    }

    @Override
    public Settings getSettings() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getSettings();
    }

    @Override
    public Session openTemporarySession() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).openTemporarySession();
    }

    @Override
    public Session openSession(final Connection connection, final boolean flushBeforeCompletionEnabled,
            final boolean autoCloseSessionEnabled, final ConnectionReleaseMode connectionReleaseMode) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).openTemporarySession();
    }

    @Override
    public Set<String> getCollectionRolesByEntityParticipant(final String entityName) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getCollectionRolesByEntityParticipant(entityName);
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getEntityNotFoundDelegate();
    }

    @Override
    public SQLFunctionRegistry getSqlFunctionRegistry() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getSqlFunctionRegistry();
    }

    @Override
    public FetchProfile getFetchProfile(final String name) {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getFetchProfile(name);
    }

    @Override
    public SessionFactoryObserver getFactoryObserver() {
        return ((SessionFactoryImplementor) getSessionFactory(false)).getFactoryObserver();
    }

    @Override
    public boolean equals(final Object obj) {
        if (sessionFactory == null) {
            return super.equals(obj);
        }
        return getSessionFactory(false).equals(obj);
    }

    @Override
    public int hashCode() {
        if (sessionFactory == null) {
            return super.hashCode();
        }
        return getSessionFactory(false).hashCode();
    }

    @Override
    public String toString() {
        if (sessionFactory == null) {
            return super.toString();
        }
        return getSessionFactory(false).toString();
    }

    @Override
    public Object getWrappedObject() {
        return sessionFactory;
    }

}
