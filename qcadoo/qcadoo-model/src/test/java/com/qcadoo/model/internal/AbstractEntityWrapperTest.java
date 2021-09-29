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

import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.BelongsToType;

public abstract class AbstractEntityWrapperTest {

    protected DataDefinition dataDefinition;

    protected FieldDefinition belongsToFieldDefinition;

    protected DataDefinition belongsToFieldDataDefinition;

    protected static final String BELONGS_TO_FIELD_NAME = "belongsToField";

    protected static final String STRING_FIELD_NAME = "stringField";

    protected static final String BOOLEAN_FIELD_NAME = "booleanField";

    @Before
    public final void superInit() {
        belongsToFieldDefinition = mock(FieldDefinition.class);
        BelongsToType belongsToType = mock(BelongsToType.class);
        when(belongsToFieldDefinition.getType()).thenReturn(belongsToType);
        belongsToFieldDataDefinition = mock(DataDefinition.class);
        when(belongsToFieldDefinition.getDataDefinition()).thenReturn(belongsToFieldDataDefinition);

        FieldDefinition stringFieldDefinition = mock(FieldDefinition.class);
        when(stringFieldDefinition.isPersistent()).thenReturn(false);

        dataDefinition = mock(DataDefinition.class);
        FieldDefinition booleanFieldDefinition = mock(FieldDefinition.class);

        Map<String, FieldDefinition> fieldsMap = Maps.newHashMap();
        fieldsMap.put(BELONGS_TO_FIELD_NAME, belongsToFieldDefinition);
        fieldsMap.put(STRING_FIELD_NAME, stringFieldDefinition);
        fieldsMap.put(BOOLEAN_FIELD_NAME, booleanFieldDefinition);

        for (Map.Entry<String, FieldDefinition> fieldEntry : fieldsMap.entrySet()) {
            when(dataDefinition.getField(fieldEntry.getKey())).thenReturn(fieldEntry.getValue());
        }

        when(dataDefinition.getFields()).thenReturn(fieldsMap);
    }

    protected abstract Entity buildEntityWrapper(final Entity entity);

    @Test
    public final void shouldCopyDoNotMakeInfinityCycleWith2Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        // when
        Entity copy = null;
        try {
            copy = firstWrappedEntity.copy();
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        assertNotSame(firstWrappedEntity, copy);
        assertNotSame(firstEntity, copy);
        assertEquals(firstWrappedEntity, copy);
        assertEquals(firstEntity, copy);
        assertEquals(secondWrappedEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(secondEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME));
    }

    @Test
    public final void shouldCopyDoNotMakeInfinityCycleWith3Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);
        final Entity thirdEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);
        thirdEntity.setId(3L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);
        when(dataDefinition.get(thirdEntity.getId())).thenReturn(thirdEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);
        final Entity thirdWrappedEntity = buildEntityWrapper(thirdEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdWrappedEntity);
        thirdWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        // when
        Entity copy = null;
        try {
            copy = firstWrappedEntity.copy();
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        assertNotSame(firstWrappedEntity, copy);
        assertNotSame(firstEntity, copy);
        assertEquals(firstWrappedEntity, copy);
        assertEquals(firstEntity, copy);
        assertEquals(secondWrappedEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(secondEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(thirdWrappedEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME).getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(thirdEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME).getBelongsToField(BELONGS_TO_FIELD_NAME));
    }

    @Test
    public final void shouldCopyDoNotMakeInfinityCycleWith4Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);
        final Entity thirdEntity = new DefaultEntity(dataDefinition);
        final Entity fourthEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);
        thirdEntity.setId(3L);
        fourthEntity.setId(4L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);
        when(dataDefinition.get(thirdEntity.getId())).thenReturn(thirdEntity);
        when(dataDefinition.get(fourthEntity.getId())).thenReturn(fourthEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);
        final Entity thirdWrappedEntity = buildEntityWrapper(thirdEntity);
        final Entity fourthWrappedEntity = buildEntityWrapper(fourthEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdWrappedEntity);
        thirdWrappedEntity.setField(BELONGS_TO_FIELD_NAME, fourthWrappedEntity);
        fourthWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        // when
        Entity copy = null;
        try {
            copy = firstWrappedEntity.copy();
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        assertNotSame(firstWrappedEntity, copy);
        assertNotSame(firstEntity, copy);
        assertEquals(firstWrappedEntity, copy);
        assertEquals(firstEntity, copy);
        assertEquals(secondWrappedEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(secondEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(thirdWrappedEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME).getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(thirdEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME).getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(fourthWrappedEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME).getBelongsToField(BELONGS_TO_FIELD_NAME)
                .getBelongsToField(BELONGS_TO_FIELD_NAME));
        assertEquals(fourthEntity, copy.getBelongsToField(BELONGS_TO_FIELD_NAME).getBelongsToField(BELONGS_TO_FIELD_NAME)
                .getBelongsToField(BELONGS_TO_FIELD_NAME));
    }

    @Test
    public final void shouldHashCodesForWrapperAndWrappedEntityBeEquals() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        firstEntity.setField(STRING_FIELD_NAME, "maku");
        firstEntity.setField(BOOLEAN_FIELD_NAME, true);
        secondEntity.setId(2L);
        secondEntity.setField(STRING_FIELD_NAME, "rocks");
        secondEntity.setField(BOOLEAN_FIELD_NAME, true);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        // when
        try {
            assertTrue(firstWrappedEntity.hashCode() == firstEntity.hashCode());
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldHashCodeDoNotMakeInfinityCycleWith2Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        // when
        int hashCode = -1;
        try {
            hashCode = firstWrappedEntity.hashCode();
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        assertTrue(hashCode == firstEntity.hashCode());
    }

    @Test
    public final void shouldHashCodeDoNotMakeInfinityCycleWith3Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);
        final Entity thirdEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);
        thirdEntity.setId(3L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);
        when(dataDefinition.get(thirdEntity.getId())).thenReturn(thirdEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);
        final Entity thirdWrappedEntity = buildEntityWrapper(thirdEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdWrappedEntity);
        thirdWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        // when
        int hashCode = -1;
        try {
            hashCode = firstWrappedEntity.hashCode();
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        assertTrue(hashCode == firstEntity.hashCode());
    }

    @Test
    public final void shouldHashCodeDoNotMakeInfinityCycleWith4Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);
        final Entity thirdEntity = new DefaultEntity(dataDefinition);
        final Entity fourthEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);
        thirdEntity.setId(3L);
        fourthEntity.setId(4L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);
        when(dataDefinition.get(thirdEntity.getId())).thenReturn(thirdEntity);
        when(dataDefinition.get(fourthEntity.getId())).thenReturn(fourthEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);
        final Entity thirdWrappedEntity = buildEntityWrapper(thirdEntity);
        final Entity fourthWrappedEntity = buildEntityWrapper(fourthEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdWrappedEntity);
        thirdWrappedEntity.setField(BELONGS_TO_FIELD_NAME, fourthWrappedEntity);
        fourthWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        // when
        int hashCode = -1;
        try {
            hashCode = firstWrappedEntity.hashCode();
        } catch (StackOverflowError e) {
            Assert.fail();
        }

        // then
        assertTrue(hashCode == firstEntity.hashCode());
    }

    @Test
    public final void shouldWrapperAndWrappedEntityBeEquals() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        firstEntity.setField(STRING_FIELD_NAME, "maku");
        firstEntity.setField(BOOLEAN_FIELD_NAME, true);
        secondEntity.setId(2L);
        secondEntity.setField(STRING_FIELD_NAME, "rocks");
        secondEntity.setField(BOOLEAN_FIELD_NAME, true);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        // when & then
        try {
            Assert.assertEquals(firstEntity, firstWrappedEntity);
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldEqualsReturnTrueAndDoNotMakeInfinityCycleWith2Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        final Entity secondOtherEntity = new DefaultEntity(dataDefinition);
        final Entity firstOtherEntity = new DefaultEntity(dataDefinition);

        firstOtherEntity.setId(1L);
        secondOtherEntity.setId(2L);

        when(dataDefinition.get(firstOtherEntity.getId())).thenReturn(firstOtherEntity);
        when(dataDefinition.get(secondOtherEntity.getId())).thenReturn(secondOtherEntity);

        final Entity firstOtherWrappedEntity = buildEntityWrapper(firstOtherEntity);
        final Entity secondOtherWrappedEntity = buildEntityWrapper(secondOtherEntity);

        firstOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondOtherWrappedEntity);
        secondOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstOtherWrappedEntity);

        // when & then
        try {
            assertEquals(firstWrappedEntity, firstOtherWrappedEntity);
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldEqualsReturnFalseAndDoNotMakeInfinityCycleWith2Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        final Entity secondOtherEntity = new DefaultEntity(dataDefinition);
        final Entity firstOtherEntity = new DefaultEntity(dataDefinition);

        firstOtherEntity.setId(1L);
        secondOtherEntity.setId(2L);
        secondOtherEntity.setField(STRING_FIELD_NAME, "difference");

        when(dataDefinition.get(firstOtherEntity.getId())).thenReturn(firstOtherEntity);
        when(dataDefinition.get(secondOtherEntity.getId())).thenReturn(secondOtherEntity);

        final Entity firstOtherWrappedEntity = buildEntityWrapper(firstOtherEntity);
        final Entity secondOtherWrappedEntity = buildEntityWrapper(secondOtherEntity);

        firstOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondOtherWrappedEntity);
        secondOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstOtherWrappedEntity);

        // when & then
        try {
            assertFalse(firstWrappedEntity.equals(firstOtherWrappedEntity));
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldEqualsReturnTrueAndDoNotMakeInfinityCycleWith3Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);
        final Entity thirdEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);
        thirdEntity.setId(3L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);
        when(dataDefinition.get(thirdEntity.getId())).thenReturn(thirdEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);
        final Entity thirdWrappedEntity = buildEntityWrapper(thirdEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdWrappedEntity);
        thirdWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        final Entity secondOtherEntity = new DefaultEntity(dataDefinition);
        final Entity firstOtherEntity = new DefaultEntity(dataDefinition);
        final Entity thirdOtherEntity = new DefaultEntity(dataDefinition);

        firstOtherEntity.setId(1L);
        secondOtherEntity.setId(2L);
        thirdOtherEntity.setId(3L);

        when(dataDefinition.get(firstOtherEntity.getId())).thenReturn(firstOtherEntity);
        when(dataDefinition.get(secondOtherEntity.getId())).thenReturn(secondOtherEntity);
        when(dataDefinition.get(thirdOtherEntity.getId())).thenReturn(thirdOtherEntity);

        final Entity firstOtherWrappedEntity = buildEntityWrapper(firstOtherEntity);
        final Entity secondOtherWrappedEntity = buildEntityWrapper(secondOtherEntity);
        final Entity thirdOtherWrappedEntity = buildEntityWrapper(thirdOtherEntity);

        firstOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondOtherWrappedEntity);
        secondOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdOtherWrappedEntity);
        thirdOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstOtherWrappedEntity);

        // when & then
        try {
            assertEquals(firstWrappedEntity, firstOtherWrappedEntity);
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldEqualsReturnFalseAndDoNotMakeInfinityCycleWith3Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);
        final Entity thirdEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);
        thirdEntity.setId(3L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);
        when(dataDefinition.get(thirdEntity.getId())).thenReturn(thirdEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);
        final Entity thirdWrappedEntity = buildEntityWrapper(thirdEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdWrappedEntity);
        thirdWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        final Entity secondOtherEntity = new DefaultEntity(dataDefinition);
        final Entity firstOtherEntity = new DefaultEntity(dataDefinition);
        final Entity thirdOtherEntity = new DefaultEntity(dataDefinition);

        firstOtherEntity.setId(1L);
        secondOtherEntity.setId(2L);
        thirdOtherEntity.setId(3L);
        thirdOtherEntity.setField(STRING_FIELD_NAME, "difference");

        when(dataDefinition.get(firstOtherEntity.getId())).thenReturn(firstOtherEntity);
        when(dataDefinition.get(secondOtherEntity.getId())).thenReturn(secondOtherEntity);
        when(dataDefinition.get(thirdOtherEntity.getId())).thenReturn(thirdOtherEntity);

        final Entity firstOtherWrappedEntity = buildEntityWrapper(firstOtherEntity);
        final Entity secondOtherWrappedEntity = buildEntityWrapper(secondOtherEntity);
        final Entity thirdOtherWrappedEntity = buildEntityWrapper(thirdOtherEntity);

        firstOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondOtherWrappedEntity);
        secondOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdOtherWrappedEntity);
        thirdOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstOtherWrappedEntity);

        // when & then
        try {
            assertFalse(firstWrappedEntity.equals(firstOtherWrappedEntity));
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldEqualsReturnTrueAndDoNotMakeInfinityCycleWith4Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);
        final Entity thirdEntity = new DefaultEntity(dataDefinition);
        final Entity fourthEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);
        thirdEntity.setId(3L);
        fourthEntity.setId(4L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);
        when(dataDefinition.get(thirdEntity.getId())).thenReturn(thirdEntity);
        when(dataDefinition.get(fourthEntity.getId())).thenReturn(fourthEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);
        final Entity thirdWrappedEntity = buildEntityWrapper(thirdEntity);
        final Entity fourthWrappedEntity = buildEntityWrapper(fourthEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdWrappedEntity);
        thirdWrappedEntity.setField(BELONGS_TO_FIELD_NAME, fourthWrappedEntity);
        fourthWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        final Entity secondOtherEntity = new DefaultEntity(dataDefinition);
        final Entity firstOtherEntity = new DefaultEntity(dataDefinition);
        final Entity thirdOtherEntity = new DefaultEntity(dataDefinition);
        final Entity fourthOtherEntity = new DefaultEntity(dataDefinition);

        firstOtherEntity.setId(1L);
        secondOtherEntity.setId(2L);
        thirdOtherEntity.setId(3L);
        fourthOtherEntity.setId(4L);

        when(dataDefinition.get(firstOtherEntity.getId())).thenReturn(firstOtherEntity);
        when(dataDefinition.get(secondOtherEntity.getId())).thenReturn(secondOtherEntity);
        when(dataDefinition.get(thirdOtherEntity.getId())).thenReturn(thirdOtherEntity);
        when(dataDefinition.get(fourthOtherEntity.getId())).thenReturn(fourthOtherEntity);

        final Entity firstOtherWrappedEntity = buildEntityWrapper(firstOtherEntity);
        final Entity secondOtherWrappedEntity = buildEntityWrapper(secondOtherEntity);
        final Entity thirdOtherWrappedEntity = buildEntityWrapper(thirdOtherEntity);
        final Entity fourthOtherWrappedEntity = buildEntityWrapper(fourthOtherEntity);

        firstOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondOtherWrappedEntity);
        secondOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdOtherWrappedEntity);
        thirdOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, fourthOtherWrappedEntity);
        fourthOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstOtherWrappedEntity);

        // when & then
        try {
            assertEquals(firstWrappedEntity, firstOtherWrappedEntity);
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public final void shouldEqualsReturnFalseAndDoNotMakeInfinityCycleWith4Entities() {
        // given
        final Entity secondEntity = new DefaultEntity(dataDefinition);
        final Entity firstEntity = new DefaultEntity(dataDefinition);
        final Entity thirdEntity = new DefaultEntity(dataDefinition);
        final Entity fourthEntity = new DefaultEntity(dataDefinition);

        firstEntity.setId(1L);
        secondEntity.setId(2L);
        thirdEntity.setId(3L);
        fourthEntity.setId(4L);

        when(dataDefinition.get(firstEntity.getId())).thenReturn(firstEntity);
        when(dataDefinition.get(secondEntity.getId())).thenReturn(secondEntity);
        when(dataDefinition.get(thirdEntity.getId())).thenReturn(thirdEntity);
        when(dataDefinition.get(fourthEntity.getId())).thenReturn(fourthEntity);

        final Entity firstWrappedEntity = buildEntityWrapper(firstEntity);
        final Entity secondWrappedEntity = buildEntityWrapper(secondEntity);
        final Entity thirdWrappedEntity = buildEntityWrapper(thirdEntity);
        final Entity fourthWrappedEntity = buildEntityWrapper(fourthEntity);

        firstWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondWrappedEntity);
        secondWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdWrappedEntity);
        thirdWrappedEntity.setField(BELONGS_TO_FIELD_NAME, fourthWrappedEntity);
        fourthWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstWrappedEntity);

        final Entity secondOtherEntity = new DefaultEntity(dataDefinition);
        final Entity firstOtherEntity = new DefaultEntity(dataDefinition);
        final Entity thirdOtherEntity = new DefaultEntity(dataDefinition);
        final Entity fourthOtherEntity = new DefaultEntity(dataDefinition);

        firstOtherEntity.setId(1L);
        secondOtherEntity.setId(2L);
        thirdOtherEntity.setId(3L);
        fourthOtherEntity.setId(4L);
        fourthOtherEntity.setField(STRING_FIELD_NAME, "difference");

        when(dataDefinition.get(firstOtherEntity.getId())).thenReturn(firstOtherEntity);
        when(dataDefinition.get(secondOtherEntity.getId())).thenReturn(secondOtherEntity);
        when(dataDefinition.get(thirdOtherEntity.getId())).thenReturn(thirdOtherEntity);
        when(dataDefinition.get(fourthOtherEntity.getId())).thenReturn(fourthOtherEntity);

        final Entity firstOtherWrappedEntity = buildEntityWrapper(firstOtherEntity);
        final Entity secondOtherWrappedEntity = buildEntityWrapper(secondOtherEntity);
        final Entity thirdOtherWrappedEntity = buildEntityWrapper(thirdOtherEntity);
        final Entity fourthOtherWrappedEntity = buildEntityWrapper(fourthOtherEntity);

        firstOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, secondOtherWrappedEntity);
        secondOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, thirdOtherWrappedEntity);
        thirdOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, fourthOtherWrappedEntity);
        fourthOtherWrappedEntity.setField(BELONGS_TO_FIELD_NAME, firstOtherWrappedEntity);

        // when & then
        try {
            assertFalse(firstWrappedEntity.equals(firstOtherWrappedEntity));
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

}
