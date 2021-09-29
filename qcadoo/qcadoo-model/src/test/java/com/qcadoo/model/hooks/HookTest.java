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
package com.qcadoo.model.hooks;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.beans.sample.CustomEntityService;
import com.qcadoo.model.beans.sample.SampleSimpleDatabaseObject;
import com.qcadoo.model.internal.DataAccessTest;
import com.qcadoo.model.internal.DefaultEntity;
import com.qcadoo.model.internal.api.EntityHookDefinition;
import com.qcadoo.model.internal.hooks.EntityHookDefinitionImpl;
import com.qcadoo.model.internal.hooks.HookInitializationException;

public class HookTest extends DataAccessTest {

    @Before
    public void init() {
        given(applicationContext.getBean(CustomEntityService.class)).willReturn(new CustomEntityService());
    }

    @Test
    public void shouldNotCallAnyHookIfNotDefined() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", null);
        entity.setField("age", null);

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals(null, entity.getField("name"));
        assertEquals(null, entity.getField("age"));
    }

    @Test
    public void shouldCallOnCreateHook() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", null);
        entity.setField("age", null);

        dataDefinition.addCreateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "onCreate",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("create", entity.getField("name"));
        assertEquals(null, entity.getField("age"));
    }

    @Test
    public void shouldCallOnUpdateHook() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", null);
        entity.setField("age", null);

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);

        dataDefinition.addUpdateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "onUpdate",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("update", entity.getField("name"));
        assertEquals(null, entity.getField("age"));
    }

    @Test
    public void shouldCallAllDefinedHooksWhileCreating() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", null);
        entity.setField("age", null);

        dataDefinition.addCreateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "onCreate",
                PLUGIN_IDENTIFIER, applicationContext));
        dataDefinition.addSaveHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "onSave", PLUGIN_IDENTIFIER,
                applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("create", entity.getField("name"));
        assertEquals(Integer.valueOf(11), entity.getField("age"));
    }

    @Test
    public void shouldCallOnSaveHookWhileUpdating() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", null);
        entity.setField("age", null);

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);

        dataDefinition.addSaveHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "onSave", PLUGIN_IDENTIFIER,
                applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals(null, entity.getField("name"));
        assertEquals(Integer.valueOf(11), entity.getField("age"));
    }

    @Test
    public void shouldCallAllDefinedHooksWhileUpdating() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", null);
        entity.setField("age", null);

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);

        dataDefinition.addUpdateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "onUpdate",
                PLUGIN_IDENTIFIER, applicationContext));
        dataDefinition.addSaveHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "onSave", PLUGIN_IDENTIFIER,
                applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("update", entity.getField("name"));
        assertEquals(Integer.valueOf(11), entity.getField("age"));
    }

    @Test
    public void shouldCallOnSaveHookWhileCreating() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", null);
        entity.setField("age", null);

        dataDefinition.addCreateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "onSave",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals(null, entity.getField("name"));
        assertEquals(Integer.valueOf(11), entity.getField("age"));
    }

    @Test
    public void shouldCreateHookNotSeeNewValueOfReadOnlyField() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, null);
        entity.setField("name", null);
        entity.setField("age", null);
        entity.setField("readOnly", "youCanNotSeeMe!");

        dataDefinition.addCreateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "rewriteReadOnlyField",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertNull(entity.getField("readOnly"));
        assertNull(entity.getField("name"));
    }

    @Test
    public void shouldUpdateHookNotSeeNewValueOfReadOnlyField() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", null);
        entity.setField("age", null);
        entity.setField("readOnly", "youCanNotSeeMe!");

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);

        dataDefinition.addUpdateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "rewriteReadOnlyField",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertNull(entity.getField("readOnly"));
        assertNull(entity.getField("name"));
    }

    @Test
    public void shouldSaveHookNotSeeNewValueOfReadOnlyField() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", null);
        entity.setField("age", null);
        entity.setField("readOnly", "youCanNotSeeMe!");

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);

        dataDefinition.addSaveHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "rewriteReadOnlyField",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertNull(entity.getField("readOnly"));
        assertNull(entity.getField("name"));
    }

    @Test
    public void shouldCreateHookCanOverrideValueOfReadOnlyField() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, null);
        entity.setField("name", null);
        entity.setField("age", null);
        entity.setField("readOnly", "youCanNotSeeMe!");

        dataDefinition.addCreateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "overrideReadOnlyField",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("overrided", entity.getField("readOnly"));
    }

    @Test
    public void shouldUpdateHookCanOverrideValueOfReadOnlyField() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", null);
        entity.setField("age", null);
        entity.setField("readOnly", "youCanNotSeeMe!");

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);

        dataDefinition.addUpdateHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "overrideReadOnlyField",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("overrided", entity.getField("readOnly"));
    }

    @Test
    public void shouldSaveHookCanOverrideValueOfReadOnlyField() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", null);
        entity.setField("age", null);
        entity.setField("readOnly", "youCanNotSeeMe!");

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);

        dataDefinition.addSaveHook(new EntityHookDefinitionImpl(CustomEntityService.class.getName(), "overrideReadOnlyField",
                PLUGIN_IDENTIFIER, applicationContext));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("overrided", entity.getField("readOnly"));
    }

    @Test
    public final void shouldBeTriggeredInOrderOfAdding() {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "a");

        dataDefinition.addSaveHook(buildHook("appendB"));
        dataDefinition.addSaveHook(buildHook("appendC"));
        dataDefinition.addSaveHook(buildHook("appendD"));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("abcd", entity.getStringField("name"));
    }

    @Test
    public final void shouldBeTriggeredInOrderOfAdding2() {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "a");

        dataDefinition.addSaveHook(buildHook("appendC"));
        dataDefinition.addSaveHook(buildHook("appendB"));
        dataDefinition.addSaveHook(buildHook("appendD"));

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals("acbd", entity.getStringField("name"));
    }

    private EntityHookDefinition buildHook(final String methodName) {
        try {
            return new EntityHookDefinitionImpl(CustomEntityService.class.getName(), methodName, PLUGIN_IDENTIFIER,
                    applicationContext);
        } catch (HookInitializationException hie) {
            throw new IllegalStateException(hie);
        }
    }

}
