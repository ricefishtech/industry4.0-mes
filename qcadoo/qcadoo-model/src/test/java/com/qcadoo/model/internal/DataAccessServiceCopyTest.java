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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.types.HasManyType;
import com.qcadoo.model.api.types.TreeType;
import com.qcadoo.model.beans.sample.SampleParentDatabaseObject;
import com.qcadoo.model.beans.sample.SampleSimpleDatabaseObject;
import com.qcadoo.model.beans.sample.SampleTreeDatabaseObject;
import com.qcadoo.model.internal.api.FieldHookDefinition;
import com.qcadoo.model.internal.types.HasManyEntitiesType;
import com.qcadoo.model.internal.types.TreeEntitiesType;
import com.qcadoo.model.internal.validators.UniqueValidator;

public final class DataAccessServiceCopyTest extends DataAccessTest {

    private static final String BELONGS_TO_SIMPLE = "belongsToSimple";

    private static final String NAME = "name";

    private static final String SIMPLE_1 = "simple1";

    private static final String SIMPLE_2 = "simple1";

    private static final String SIMPLE_3 = "simple1";

    private static final String SIMPLE_4 = "simple1";

    @Test
    public void shouldCopyEntity() throws Exception {
        // given
        SampleSimpleDatabaseObject simpleDatabaseObject = new SampleSimpleDatabaseObject();
        simpleDatabaseObject.setId(13L);
        simpleDatabaseObject.setName("Mr T");
        simpleDatabaseObject.setAge(66);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(simpleDatabaseObject);

        // when
        List<Entity> entities = dataDefinition.copy(new Long[] { 13L });

        // then
        verify(session, times(1)).save(Mockito.any(SampleSimpleDatabaseObject.class));
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals(66, entities.get(0).getField("age"));
        Assert.assertEquals("Mr T", entities.get(0).getField(NAME));
    }

    @Test
    public void shouldCopyEntityWithUniqueField() throws Exception {
        // given
        FieldHookDefinition fieldHook = new UniqueValidator();
        // fieldHook.initialize(dataDefinition, fieldDefinitionName);

        fieldDefinitionName.withValidator(fieldHook);

        SampleSimpleDatabaseObject simpleDatabaseObject = new SampleSimpleDatabaseObject();
        simpleDatabaseObject.setId(13L);
        simpleDatabaseObject.setName("Mr T");
        simpleDatabaseObject.setAge(66);

        given(criteria.setProjection(Projections.rowCount()).uniqueResult()).willReturn(0);
        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(simpleDatabaseObject);

        // when
        List<Entity> entities = dataDefinition.copy(new Long[] { 13L });

        // then
        verify(session, times(1)).save(Mockito.any(SampleSimpleDatabaseObject.class));
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals(66, entities.get(0).getField("age"));
        Assert.assertEquals("Mr T(1)", entities.get(0).getField(NAME));
    }

    @Test
    public void shouldCopyEntityWithUniqueField2() throws Exception {
        // given
        FieldHookDefinition fieldHook = new UniqueValidator();
        // fieldHook.initialize(dataDefinition, fieldDefinitionName);

        fieldDefinitionName.withValidator(fieldHook);

        SampleSimpleDatabaseObject simpleDatabaseObject = new SampleSimpleDatabaseObject();
        simpleDatabaseObject.setId(13L);
        simpleDatabaseObject.setName("Mr T(1)");
        simpleDatabaseObject.setAge(66);

        given(hibernateService.getTotalNumberOfEntities(Mockito.any(Criteria.class))).willReturn(1, 0);
        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(simpleDatabaseObject);

        // when
        List<Entity> entities = dataDefinition.copy(new Long[] { 13L });

        // then
        verify(session, times(1)).save(Mockito.any(SampleSimpleDatabaseObject.class));
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals(66, entities.get(0).getField("age"));
        Assert.assertEquals("Mr T(3)", entities.get(0).getField(NAME));
    }

    @Test
    public void shouldCopyEntityWithUniqueFieldWhenNull() throws Exception {
        // given
        FieldHookDefinition fieldHook = new UniqueValidator();
        // fieldHook.initialize(dataDefinition, fieldDefinitionName);

        fieldDefinitionName.withValidator(fieldHook);

        SampleSimpleDatabaseObject simpleDatabaseObject = new SampleSimpleDatabaseObject();
        simpleDatabaseObject.setId(13L);
        simpleDatabaseObject.setName(null);
        simpleDatabaseObject.setAge(66);

        given(hibernateService.getTotalNumberOfEntities(Mockito.any(Criteria.class))).willReturn(0);
        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(simpleDatabaseObject);

        // when
        List<Entity> entities = dataDefinition.copy(new Long[] { 13L });

        // then
        verify(session, times(1)).save(Mockito.any(SampleSimpleDatabaseObject.class));
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals(66, entities.get(0).getField("age"));
        Assert.assertEquals(null, entities.get(0).getField(NAME));
    }

    @Test
    public void shouldCopyEntityWithoutHasManyField() throws Exception {
        // given
        SampleSimpleDatabaseObject simpleDatabaseObject = new SampleSimpleDatabaseObject();
        simpleDatabaseObject.setId(12L);
        simpleDatabaseObject.setName("Mr T");
        simpleDatabaseObject.setAge(66);

        SampleParentDatabaseObject parentDatabaseObject = new SampleParentDatabaseObject();
        parentDatabaseObject.setId(13L);
        parentDatabaseObject.setName("Mr T");

        given(criteria.setProjection(Projections.rowCount()).uniqueResult()).willReturn(1, 0);
        given(session.get(Mockito.eq(SampleSimpleDatabaseObject.class), Mockito.eq(12L))).willReturn(simpleDatabaseObject);
        given(session.get(Mockito.eq(SampleParentDatabaseObject.class), Mockito.eq(13L))).willReturn(parentDatabaseObject);
        given(criteria.list()).willReturn(Lists.newArrayList(simpleDatabaseObject));

        // when
        List<Entity> entities = parentDataDefinition.copy(new Long[] { 13L });

        // then
        verify(session, times(1)).save(Mockito.any(SampleSimpleDatabaseObject.class));
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals("Mr T", entities.get(0).getField(NAME));
        verify(session, times(1)).save(Mockito.any());
        verify(session, never()).get(Mockito.eq(SampleSimpleDatabaseObject.class), anyInt());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void shouldCopyEntityWithHasManyField() throws Exception {
        // given
        parentFieldDefinitionHasMany.withType(new HasManyEntitiesType("simple", "entity", "belongsTo",
                HasManyType.Cascade.DELETE, true, dataDefinitionService));

        SampleSimpleDatabaseObject simpleDatabaseObject = new SampleSimpleDatabaseObject();
        simpleDatabaseObject.setId(12L);
        simpleDatabaseObject.setName("Mr T");
        simpleDatabaseObject.setAge(66);

        SampleParentDatabaseObject parentDatabaseObject = new SampleParentDatabaseObject();
        parentDatabaseObject.setId(13L);
        parentDatabaseObject.setName("Mr T");

        given(hibernateService.getTotalNumberOfEntities(Mockito.any(Criteria.class))).willReturn(1, 0);
        given(session.get(Mockito.eq(SampleSimpleDatabaseObject.class), Mockito.eq(12L))).willReturn(simpleDatabaseObject);
        given(session.get(Mockito.eq(SampleParentDatabaseObject.class), Mockito.eq(13L))).willReturn(parentDatabaseObject);
        given(hibernateService.list(Mockito.any(Criteria.class))).willReturn((List) Lists.newArrayList(simpleDatabaseObject));

        // when
        List<Entity> entities = parentDataDefinition.copy(new Long[] { 13L });

        // then
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals("Mr T", entities.get(0).getField(NAME));
        verify(session, times(2)).save(Mockito.any());
        verify(session, never()).get(Mockito.eq(SampleSimpleDatabaseObject.class), anyInt());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldCopyEntityWithManyToManyField() throws Exception {
        // given
        SampleSimpleDatabaseObject simpleDatabaseObject = new SampleSimpleDatabaseObject();
        simpleDatabaseObject.setId(12L);
        simpleDatabaseObject.setName("Mr T");
        simpleDatabaseObject.setAge(30);

        SampleParentDatabaseObject parentDatabaseObject = new SampleParentDatabaseObject();
        parentDatabaseObject.setId(13L);
        parentDatabaseObject.setName("Mr T");

        given(hibernateService.getTotalNumberOfEntities(Mockito.any(Criteria.class))).willReturn(1, 0);
        given(session.get(Mockito.eq(SampleSimpleDatabaseObject.class), Mockito.eq(12L))).willReturn(simpleDatabaseObject);
        given(session.get(Mockito.eq(SampleParentDatabaseObject.class), Mockito.eq(13L))).willReturn(parentDatabaseObject);
        given(hibernateService.list(Mockito.any(Criteria.class))).willReturn((List) Lists.newArrayList(simpleDatabaseObject));

        // when
        List<Entity> entities = parentDataDefinition.copy(new Long[] { 13L });

        // then
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals("Mr T", entities.get(0).getField(NAME));
        verify(session).save(Mockito.any());
        verify(session, never()).get(Mockito.eq(SampleSimpleDatabaseObject.class), anyInt());
    }

    @Test
    public void shouldCopyEntityWithoutTreeField() throws Exception {
        // given
        SampleTreeDatabaseObject treeDatabaseObject = new SampleTreeDatabaseObject();
        treeDatabaseObject.setId(12L);
        treeDatabaseObject.setName("Mr T");

        SampleParentDatabaseObject parentDatabaseObject = new SampleParentDatabaseObject();
        parentDatabaseObject.setId(13L);
        parentDatabaseObject.setName("Mr T");

        given(criteria.setProjection(Projections.rowCount()).uniqueResult()).willReturn(1, 0);
        given(session.get(Mockito.eq(SampleSimpleDatabaseObject.class), Mockito.eq(12L))).willReturn(treeDatabaseObject);
        given(session.get(Mockito.eq(SampleParentDatabaseObject.class), Mockito.eq(13L))).willReturn(parentDatabaseObject);
        given(criteria.list()).willReturn(Lists.newArrayList(treeDatabaseObject));

        // when
        List<Entity> entities = parentDataDefinition.copy(new Long[] { 13L });

        // then
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals("Mr T", entities.get(0).getField(NAME));
        verify(session, times(1)).save(Mockito.any());
        verify(session, never()).get(Mockito.eq(SampleSimpleDatabaseObject.class), anyInt());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldCopyEntityWithTreeField() throws Exception {
        // given
        parentFieldDefinitionTree.withType(new TreeEntitiesType("tree", "entity", "owner", TreeType.Cascade.DELETE, true,
                dataDefinitionService));

        SampleTreeDatabaseObject treeDatabaseObject = new SampleTreeDatabaseObject();
        treeDatabaseObject.setId(12L);
        treeDatabaseObject.setName("Mr T");

        SampleParentDatabaseObject parentDatabaseObject = new SampleParentDatabaseObject();
        parentDatabaseObject.setId(13L);
        parentDatabaseObject.setName("Mr T");

        given(hibernateService.getTotalNumberOfEntities(Mockito.any(Criteria.class))).willReturn(1, 0);
        given(session.get(Mockito.eq(SampleSimpleDatabaseObject.class), Mockito.eq(12L))).willReturn(treeDatabaseObject);
        given(session.get(Mockito.eq(SampleParentDatabaseObject.class), Mockito.eq(13L))).willReturn(parentDatabaseObject);
        given(hibernateService.list(Mockito.any(Criteria.class))).willReturn((List) Lists.newArrayList(treeDatabaseObject));

        // when
        List<Entity> entities = parentDataDefinition.copy(new Long[] { 13L });

        // then
        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        Assert.assertEquals("Mr T", entities.get(0).getField(NAME));
        verify(session, times(2)).save(Mockito.any());
        verify(session, never()).get(Mockito.eq(SampleSimpleDatabaseObject.class), anyInt());
    }

    @Test
    public void shouldCopyEntityWithoutInfinityCycleWith2Entities() {
        // given
        SampleSimpleDatabaseObject simpleDatabaseObject1 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject1.setId(1L);
        simpleDatabaseObject1.setName(SIMPLE_1);

        SampleSimpleDatabaseObject simpleDatabaseObject2 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject2.setId(2L);
        simpleDatabaseObject2.setName(SIMPLE_2);

        simpleDatabaseObject1.setBelongsToSimple(simpleDatabaseObject2);
        simpleDatabaseObject2.setBelongsToSimple(simpleDatabaseObject1);

        stubSessionGet(simpleDatabaseObject1);
        stubSessionGet(simpleDatabaseObject2);

        // when
        List<Entity> entities = null;
        try {
            entities = dataDefinition.copy(new Long[] { simpleDatabaseObject1.getId() });
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        final ArgumentCaptor<SampleSimpleDatabaseObject> argCaptor = ArgumentCaptor.forClass(SampleSimpleDatabaseObject.class);

        verify(session, times(1)).save(Mockito.any(SampleSimpleDatabaseObject.class));
        verify(session, times(1)).save(argCaptor.capture());
        final SampleSimpleDatabaseObject savedObject = argCaptor.getValue();
        assertEquals(SIMPLE_1, savedObject.getName());
        assertFalse(simpleDatabaseObject1.getId().equals(savedObject.getId()));
        assertEquals(SIMPLE_2, savedObject.getBelongsToSimple().getName());
        assertEquals(simpleDatabaseObject2.getId(), savedObject.getBelongsToSimple().getId());

        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        final Entity entityCopy = entities.get(0);
        Assert.assertEquals(SIMPLE_1, entityCopy.getField(NAME));
        Assert.assertEquals(SIMPLE_2, entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getField(NAME));
        Assert.assertEquals(Long.valueOf(2L), entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getId());
        assertFalse(simpleDatabaseObject1.getId().equals(entityCopy.getId()));
    }

    @Test
    public void shouldCopyEntityWithoutInfinityCycleWith3Entities() {
        // given
        SampleSimpleDatabaseObject simpleDatabaseObject1 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject1.setId(1L);
        simpleDatabaseObject1.setName(SIMPLE_1);

        SampleSimpleDatabaseObject simpleDatabaseObject2 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject2.setId(2L);
        simpleDatabaseObject2.setName(SIMPLE_2);

        SampleSimpleDatabaseObject simpleDatabaseObject3 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject3.setId(3L);
        simpleDatabaseObject3.setName(SIMPLE_3);

        simpleDatabaseObject1.setBelongsToSimple(simpleDatabaseObject2);
        simpleDatabaseObject2.setBelongsToSimple(simpleDatabaseObject3);
        simpleDatabaseObject3.setBelongsToSimple(simpleDatabaseObject1);

        stubSessionGet(simpleDatabaseObject1);
        stubSessionGet(simpleDatabaseObject2);
        stubSessionGet(simpleDatabaseObject3);

        // when
        List<Entity> entities = null;
        try {
            entities = dataDefinition.copy(new Long[] { simpleDatabaseObject1.getId() });
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        final ArgumentCaptor<SampleSimpleDatabaseObject> argCaptor = ArgumentCaptor.forClass(SampleSimpleDatabaseObject.class);

        verify(session, times(1)).save(Mockito.any(SampleSimpleDatabaseObject.class));
        verify(session, times(1)).save(argCaptor.capture());
        final SampleSimpleDatabaseObject savedObject = argCaptor.getValue();

        assertEquals(SIMPLE_1, savedObject.getName());
        assertFalse(simpleDatabaseObject1.getId().equals(savedObject.getId()));

        assertEquals(SIMPLE_2, savedObject.getBelongsToSimple().getName());
        assertEquals(simpleDatabaseObject2.getId(), savedObject.getBelongsToSimple().getId());

        assertEquals(SIMPLE_3, savedObject.getBelongsToSimple().getBelongsToSimple().getName());
        assertEquals(simpleDatabaseObject3.getId(), savedObject.getBelongsToSimple().getBelongsToSimple().getId());

        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        final Entity entityCopy = entities.get(0);
        Assert.assertEquals(SIMPLE_1, entityCopy.getField(NAME));
        Assert.assertEquals(SIMPLE_2, entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getField(NAME));
        Assert.assertEquals(SIMPLE_3, entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getBelongsToField(BELONGS_TO_SIMPLE)
                .getField(NAME));
        Assert.assertEquals(Long.valueOf(2L), entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getId());
        assertFalse(simpleDatabaseObject1.getId().equals(entityCopy.getId()));
    }

    @Test
    public void shouldCopyEntityWithoutInfinityCycleWith4Entities() {
        // given
        SampleSimpleDatabaseObject simpleDatabaseObject1 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject1.setId(1L);
        simpleDatabaseObject1.setName(SIMPLE_1);

        SampleSimpleDatabaseObject simpleDatabaseObject2 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject2.setId(2L);
        simpleDatabaseObject2.setName(SIMPLE_2);

        SampleSimpleDatabaseObject simpleDatabaseObject3 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject3.setId(3L);
        simpleDatabaseObject3.setName(SIMPLE_3);

        SampleSimpleDatabaseObject simpleDatabaseObject4 = new SampleSimpleDatabaseObject();
        simpleDatabaseObject4.setId(4L);
        simpleDatabaseObject4.setName(SIMPLE_4);

        simpleDatabaseObject1.setBelongsToSimple(simpleDatabaseObject2);
        simpleDatabaseObject2.setBelongsToSimple(simpleDatabaseObject3);
        simpleDatabaseObject3.setBelongsToSimple(simpleDatabaseObject4);
        simpleDatabaseObject4.setBelongsToSimple(simpleDatabaseObject1);

        stubSessionGet(simpleDatabaseObject1);
        stubSessionGet(simpleDatabaseObject2);
        stubSessionGet(simpleDatabaseObject3);
        stubSessionGet(simpleDatabaseObject4);

        // when
        List<Entity> entities = null;
        try {
            entities = dataDefinition.copy(new Long[] { simpleDatabaseObject1.getId() });
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        final ArgumentCaptor<SampleSimpleDatabaseObject> argCaptor = ArgumentCaptor.forClass(SampleSimpleDatabaseObject.class);

        verify(session, times(1)).save(Mockito.any(SampleSimpleDatabaseObject.class));
        verify(session, times(1)).save(argCaptor.capture());
        final SampleSimpleDatabaseObject savedObject = argCaptor.getValue();

        assertEquals(SIMPLE_1, savedObject.getName());
        assertFalse(simpleDatabaseObject1.getId().equals(savedObject.getId()));

        assertEquals(SIMPLE_2, savedObject.getBelongsToSimple().getName());
        assertEquals(simpleDatabaseObject2.getId(), savedObject.getBelongsToSimple().getId());

        assertEquals(SIMPLE_3, savedObject.getBelongsToSimple().getBelongsToSimple().getName());
        assertEquals(simpleDatabaseObject3.getId(), savedObject.getBelongsToSimple().getBelongsToSimple().getId());

        assertEquals(SIMPLE_4, savedObject.getBelongsToSimple().getBelongsToSimple().getBelongsToSimple().getName());
        assertEquals(simpleDatabaseObject4.getId(), savedObject.getBelongsToSimple().getBelongsToSimple().getBelongsToSimple()
                .getId());

        assertEquals(1, entities.size());
        assertTrue(entities.get(0).isValid());
        final Entity entityCopy = entities.get(0);
        Assert.assertEquals(SIMPLE_1, entityCopy.getField(NAME));
        Assert.assertEquals(SIMPLE_2, entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getField(NAME));
        Assert.assertEquals(SIMPLE_3, entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getBelongsToField(BELONGS_TO_SIMPLE)
                .getField(NAME));
        Assert.assertEquals(SIMPLE_4, entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getBelongsToField(BELONGS_TO_SIMPLE)
                .getBelongsToField(BELONGS_TO_SIMPLE).getField(NAME));
        Assert.assertEquals(Long.valueOf(2L), entityCopy.getBelongsToField(BELONGS_TO_SIMPLE).getId());
        assertFalse(simpleDatabaseObject1.getId().equals(entityCopy.getId()));
    }

    private void stubSessionGet(final SampleSimpleDatabaseObject sampleSimpleDatabaseObject) {
        given(session.get(sampleSimpleDatabaseObject.getClass(), sampleSimpleDatabaseObject.getId())).willReturn(
                sampleSimpleDatabaseObject);
        given(session.load(sampleSimpleDatabaseObject.getClass(), sampleSimpleDatabaseObject.getId())).willReturn(
                sampleSimpleDatabaseObject);
    }

}
