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
package com.qcadoo.model.internal;

import com.google.common.base.Preconditions;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.internal.api.HibernateService;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.types.BelongsToEntityType;
import com.qcadoo.model.internal.types.DateTimeType;
import com.qcadoo.model.internal.types.DecimalType;
import com.qcadoo.model.internal.types.StringType;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class HibernateServiceImpl implements HibernateService {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateServiceImpl.class);

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public int getTotalNumberOfEntities(final Criteria criteria) {
        final CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        final SessionImplementor session = (SessionImplementor) getCurrentSession();
        SessionFactoryImplementor factory = session.getFactory();
        CriteriaQueryTranslator translator = new CriteriaQueryTranslator(factory, criteriaImpl,
                criteriaImpl.getEntityOrClassName(), CriteriaQueryTranslator.ROOT_SQL_ALIAS);
        String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());

        CriteriaJoinWalker walker = new CriteriaJoinWalker((OuterJoinLoadable) factory.getEntityPersister(implementors[0]),
                translator, factory, criteriaImpl, criteriaImpl.getEntityOrClassName(), session.getLoadQueryInfluencers());

        final String sql = "select count(*) as cnt from (" + walker.getSQLString() + ") sq";

        getCurrentSession().flush(); // is this safe?

        return ((Number) getCurrentSession()
                .createSQLQuery(sql)
                .setParameters(translator.getQueryParameters().getPositionalParameterValues(),
                        translator.getQueryParameters().getPositionalParameterTypes()).uniqueResult()).intValue();
    }

    @Override
    public InternalDataDefinition resolveDataDefinition(final Criteria criteria) {
        final CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        final SessionImplementor session = (SessionImplementor) getCurrentSession();
        SessionFactoryImplementor factory = session.getFactory();
        CriteriaQueryTranslator translator = new CriteriaQueryTranslator(factory, criteriaImpl,
                criteriaImpl.getEntityOrClassName(), CriteriaQueryTranslator.ROOT_SQL_ALIAS);

        String[] aliases = criteriaImpl.getProjection().getAliases();
        Type[] types = criteriaImpl.getProjection().getTypes(criteriaImpl, translator);

        return resolveDataDefinition(types, aliases);
    }

    @Override
    public InternalDataDefinition resolveDataDefinition(final Query query) {
        return resolveDataDefinition(query.getReturnTypes(), query.getReturnAliases());
    }

    private InternalDataDefinition resolveDataDefinition(final Type[] types, final String[] aliases) {
        if (types.length == 1 && types[0] instanceof EntityType) {
            return resolveDataDefinitionFromEntityType((EntityType) types[0]);
        } else {
            DynamicDataDefinitionImpl dataDefinition = new DynamicDataDefinitionImpl();

            for (int i = 0; i < types.length; i++) {
                dataDefinition.addField(aliases[i] == null ? Integer.toString(i) : aliases[i], convertHibernateType(types[i]));
            }

            return dataDefinition;
        }
    }

    private InternalDataDefinition resolveDataDefinitionFromEntityType(final EntityType entityType) {
        return resolveDataDefinitionFromClassType(entityType.getName());
    }

    private InternalDataDefinition resolveDataDefinitionFromClassType(final String classType) {
        String[] tmp = classType.replaceFirst("com.qcadoo.model.beans.", "").split("\\.");
        String model = tmp[1].replaceFirst(tmp[0].substring(0, 1).toUpperCase(Locale.ENGLISH) + tmp[0].substring(1), "");
        Preconditions.checkState(StringUtils.isNotBlank(model), "Can't parse model name from class' binary name.");
        model = model.substring(0, 1).toLowerCase(Locale.ENGLISH) + model.substring(1);
        return (InternalDataDefinition) dataDefinitionService.get(tmp[0], model);
    }

    private FieldType convertHibernateType(final Type type) {
        if (type instanceof BigDecimalType || type instanceof DoubleType || type instanceof FloatType) {
            return new DecimalType();
        }
        if (type instanceof FloatType || type instanceof BigIntegerType || type instanceof IntegerType
                || type instanceof ShortType || type instanceof LongType) {
            return new com.qcadoo.model.internal.types.IntegerType();
        }
        if (type instanceof BooleanType) {
            return new com.qcadoo.model.internal.types.BooleanType();
        }
        if (type instanceof DateType) {
            return new com.qcadoo.model.internal.types.DateType();
        }
        if (type instanceof TimestampType || type instanceof TimeType) {
            return new DateTimeType();
        }
        if (type instanceof org.hibernate.type.StringType || type instanceof CharacterType) {
            return new StringType();
        }
        if (type instanceof TextType) {
            return new com.qcadoo.model.internal.types.TextType();
        }
        if (type instanceof EntityType) {
            DataDefinition dataDefinition = resolveDataDefinitionFromEntityType((EntityType) type);

            if (dataDefinition == null) {
                LOG.warn("Cannot find dataDefinition for class " + ((EntityType) type).getName());
            } else {
                return new BelongsToEntityType(dataDefinition.getPluginIdentifier(), dataDefinition.getName(),
                        dataDefinitionService, false, true);
            }
        }

        LOG.warn("Cannot map hibernate's type " + type.getClass().getCanonicalName() + ", using string type");

        return new StringType();
    }

    protected void setDataDefinitionService(final DataDefinitionService dataDefinitionService) {
        this.dataDefinitionService = dataDefinitionService;
    }

    protected void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<?> list(final Query query) {
        return query.list();
    }

    @Override
    public List<?> list(final Criteria criteria) {
        return criteria.list();
    }

}
