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

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.engine.SessionImplementor;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.aspectj.AnnotationTransactionAspect;

import com.qcadoo.model.TransactionMockAwareTest;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.api.types.HasManyType;
import com.qcadoo.model.api.types.TreeType;
import com.qcadoo.model.beans.sample.SampleParentDatabaseObject;
import com.qcadoo.model.beans.sample.SampleSimpleDatabaseObject;
import com.qcadoo.model.beans.sample.SampleTreeDatabaseObject;
import com.qcadoo.model.internal.api.DataAccessService;
import com.qcadoo.model.internal.api.EntityService;
import com.qcadoo.model.internal.api.HibernateService;
import com.qcadoo.model.internal.api.PriorityService;
import com.qcadoo.model.internal.api.ValidationService;
import com.qcadoo.model.internal.types.BelongsToEntityType;
import com.qcadoo.model.internal.types.BooleanType;
import com.qcadoo.model.internal.types.DateType;
import com.qcadoo.model.internal.types.DecimalType;
import com.qcadoo.model.internal.types.HasManyEntitiesType;
import com.qcadoo.model.internal.types.IntegerType;
import com.qcadoo.model.internal.types.PriorityType;
import com.qcadoo.model.internal.types.StringType;
import com.qcadoo.model.internal.types.TreeEntitiesType;
import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginStateResolver;
import com.qcadoo.plugin.internal.PluginUtilsService;

public abstract class DataAccessTest extends TransactionMockAwareTest {

    protected final DataDefinitionService dataDefinitionService = mock(DataDefinitionService.class);

    protected final HibernateService hibernateService = mock(HibernateService.class);

    protected final Session session = mock(Session.class, Mockito.withSettings().extraInterfaces(SessionImplementor.class));

    protected final Criteria criteria = mock(Criteria.class, RETURNS_DEEP_STUBS);

    protected final DictionaryService dictionaryService = mock(DictionaryService.class);

    protected final ApplicationContext applicationContext = mock(ApplicationContext.class);

    protected final DataAccessService dataAccessServiceMock = mock(DataAccessService.class);

    protected static final String PLUGIN_IDENTIFIER = "somePlugin";

    protected EntityService entityService = null;

    protected ValidationService validationService = null;

    protected PriorityService priorityService = null;

    protected DataAccessService dataAccessService = null;

    protected DataDefinitionImpl parentDataDefinition = null;

    protected DataDefinitionImpl treeDataDefinition = null;

    protected DataDefinitionImpl dataDefinition = null;

    protected FieldDefinitionImpl fieldDefinitionReadOnly;

    protected FieldDefinitionImpl fieldDefinitionPriority = null;

    protected FieldDefinitionImpl fieldDefinitionBelongsTo = null;

    protected FieldDefinitionImpl fieldDefinitionBelongsToSimple = null;

    protected FieldDefinitionImpl fieldDefinitionLazyBelongsTo = null;

    protected FieldDefinitionImpl fieldDefinitionAge = null;

    protected FieldDefinitionImpl fieldDefinitionMoney = null;

    protected FieldDefinitionImpl fieldDefinitionRetired = null;

    protected FieldDefinitionImpl fieldDefinitionBirthDate = null;

    protected FieldDefinitionImpl fieldDefinitionName = null;

    protected FieldDefinitionImpl parentFieldDefinitionName = null;

    protected FieldDefinitionImpl parentFieldDefinitionHasMany = null;

    protected FieldDefinitionImpl parentFieldDefinitionTree = null;

    protected FieldDefinitionImpl treeFieldDefinitionName = null;

    protected FieldDefinitionImpl treeFieldDefinitionChildren = null;

    protected FieldDefinitionImpl treeFieldDefinitionParent = null;

    protected FieldDefinitionImpl treeFieldDefinitionOwner = null;

    protected PluginStateResolver pluginStateResolver = null;

    @Before
    public void superInit() {
        pluginStateResolver = Mockito.mock(PluginStateResolver.class);
        given(pluginStateResolver.isEnabled(Mockito.anyString())).willReturn(true);
        given(pluginStateResolver.isEnabled(Mockito.any(Plugin.class))).willReturn(true);
        given(pluginStateResolver.isEnabledOrEnabling(Mockito.anyString())).willReturn(true);
        given(pluginStateResolver.isEnabledOrEnabling(Mockito.any(Plugin.class))).willReturn(true);

        PluginUtilsService pluginUtilsService = new PluginUtilsService(pluginStateResolver);
        pluginUtilsService.init();

        validationService = new ValidationServiceImpl();

        entityService = new EntityServiceImpl();
        ReflectionTestUtils.setField(entityService, "hibernateService", hibernateService);

        priorityService = new PriorityServiceImpl();
        ReflectionTestUtils.setField(priorityService, "entityService", entityService);
        ReflectionTestUtils.setField(priorityService, "hibernateService", hibernateService);

        dataAccessService = new DataAccessServiceImpl();
        ReflectionTestUtils.setField(dataAccessService, "entityService", entityService);
        ReflectionTestUtils.setField(dataAccessService, "priorityService", priorityService);
        ReflectionTestUtils.setField(dataAccessService, "validationService", validationService);
        ReflectionTestUtils.setField(dataAccessService, "hibernateService", hibernateService);
        AnnotationTransactionAspect.aspectOf();

        SearchRestrictions restrictions = new SearchRestrictions();
        ReflectionTestUtils.setField(restrictions, "dataAccessService", dataAccessService);

        buildParentDataDefinition();

        buildTreeDataDefinition();

        buildDataDefinition();

        given(hibernateService.getCurrentSession()).willReturn(session);

        given(session.createCriteria(any(Class.class))).willReturn(criteria);

        given(criteria.add(any(Criterion.class))).willReturn(criteria);
        given(criteria.setProjection(any(Projection.class))).willReturn(criteria);
        given(criteria.setFirstResult(anyInt())).willReturn(criteria);
        given(criteria.setMaxResults(anyInt())).willReturn(criteria);
        given(criteria.addOrder(any(Order.class))).willReturn(criteria);

    }

    private void buildParentDataDefinition() {
        parentDataDefinition = new DataDefinitionImpl("parent", "parent.entity", dataAccessService);
        given(dataDefinitionService.get("parent", "entity")).willReturn(parentDataDefinition);

        parentFieldDefinitionName = buildFieldDefinition("name", parentDataDefinition, new StringType());

        parentFieldDefinitionHasMany = buildFieldDefinition("entities", parentDataDefinition, new HasManyEntitiesType("simple",
                "entity", "belongsTo", HasManyType.Cascade.DELETE, false, dataDefinitionService));

        parentFieldDefinitionTree = buildFieldDefinition("tree", parentDataDefinition, new TreeEntitiesType("tree", "entity",
                "owner", TreeType.Cascade.DELETE, false, dataDefinitionService));

        parentDataDefinition.withField(parentFieldDefinitionName);
        parentDataDefinition.withField(parentFieldDefinitionHasMany);
        parentDataDefinition.withField(parentFieldDefinitionTree);
        parentDataDefinition.setFullyQualifiedClassName(SampleParentDatabaseObject.class.getCanonicalName());
    }

    private void buildDataDefinition() {
        dataDefinition = new DataDefinitionImpl("simple", "simple.entity", dataAccessService);
        given(dataDefinitionService.get("simple", "entity")).willReturn(dataDefinition);

        fieldDefinitionBelongsTo = buildFieldDefinition("belongsTo", dataDefinition, new BelongsToEntityType("parent", "entity",
                dataDefinitionService, false, true));

        fieldDefinitionBelongsToSimple = buildFieldDefinition("belongsToSimple", dataDefinition, new BelongsToEntityType(
                "simple", "entity", dataDefinitionService, false, true));

        fieldDefinitionLazyBelongsTo = buildFieldDefinition("lazyBelongsTo", dataDefinition, new BelongsToEntityType("parent",
                "entity", dataDefinitionService, true, true));

        fieldDefinitionName = buildFieldDefinition("name", dataDefinition, new StringType());

        fieldDefinitionReadOnly = buildFieldDefinition("readOnly", dataDefinition, new StringType());
        fieldDefinitionReadOnly.withReadOnly(true);

        fieldDefinitionAge = buildFieldDefinition("age", dataDefinition, new IntegerType());

        fieldDefinitionPriority = buildFieldDefinition("priority", dataDefinition, new PriorityType(fieldDefinitionBelongsTo));
        fieldDefinitionPriority.withReadOnly(true);

        fieldDefinitionMoney = buildFieldDefinition("money", dataDefinition, new DecimalType());

        fieldDefinitionRetired = buildFieldDefinition("retired", dataDefinition, new BooleanType());

        fieldDefinitionBirthDate = buildFieldDefinition("birthDate", dataDefinition, new DateType());

        dataDefinition.withField(fieldDefinitionReadOnly);
        dataDefinition.withField(fieldDefinitionName);
        dataDefinition.withField(fieldDefinitionAge);
        dataDefinition.withField(fieldDefinitionMoney);
        dataDefinition.withField(fieldDefinitionRetired);
        dataDefinition.withField(fieldDefinitionBirthDate);
        dataDefinition.withField(fieldDefinitionBelongsTo);
        dataDefinition.withField(fieldDefinitionBelongsToSimple);
        dataDefinition.withField(fieldDefinitionLazyBelongsTo);
        dataDefinition.setFullyQualifiedClassName(SampleSimpleDatabaseObject.class.getCanonicalName());
    }

    private void buildTreeDataDefinition() {
        treeDataDefinition = new DataDefinitionImpl("tree", "tree.entity", dataAccessService);
        given(dataDefinitionService.get("tree", "entity")).willReturn(treeDataDefinition);

        treeFieldDefinitionName = buildFieldDefinition("name", treeDataDefinition, new StringType());

        treeFieldDefinitionChildren = buildFieldDefinition("children", treeDataDefinition, new HasManyEntitiesType("tree",
                "entity", "parent", HasManyType.Cascade.DELETE, false, dataDefinitionService));

        treeFieldDefinitionParent = buildFieldDefinition("parent", treeDataDefinition, new BelongsToEntityType("tree", "entity",
                dataDefinitionService, false, true));

        treeFieldDefinitionOwner = buildFieldDefinition("owner", treeDataDefinition, new BelongsToEntityType("parent", "entity",
                dataDefinitionService, false, true));

        treeDataDefinition.withField(treeFieldDefinitionName);
        treeDataDefinition.withField(treeFieldDefinitionChildren);
        treeDataDefinition.withField(treeFieldDefinitionParent);
        treeDataDefinition.withField(treeFieldDefinitionOwner);
        treeDataDefinition.setFullyQualifiedClassName(SampleTreeDatabaseObject.class.getCanonicalName());
    }

    private FieldDefinitionImpl buildFieldDefinition(final String name, final DataDefinition owningDataDefinition,
            final FieldType type) {
        FieldDefinitionImpl fieldDefinition = new FieldDefinitionImpl(owningDataDefinition, name).withType(type);
        fieldDefinition.setPluginIdentifier(PLUGIN_IDENTIFIER);
        return fieldDefinition;
    }

}
