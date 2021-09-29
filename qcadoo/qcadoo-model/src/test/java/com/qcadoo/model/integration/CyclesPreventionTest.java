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
package com.qcadoo.model.integration;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityOpResult;

public class CyclesPreventionTest extends IntegrationTest {

    protected static final String PLUGIN_CYCLES = "cycles";

    protected static final String MODEL_A = "modelA";

    protected static final String MODEL_B = "modelB";

    protected static final String MODEL_C = "modelC";

    protected static final String TABLE_NAME_A = PLUGIN_CYCLES + "_" + MODEL_A;

    protected static final String TABLE_NAME_B = PLUGIN_CYCLES + "_" + MODEL_B;

    protected static final String TABLE_NAME_C = PLUGIN_CYCLES + "_" + MODEL_C;

    protected static final String JOINTABLE_A_B = "JOINTABLE_" + MODEL_A.toUpperCase() + "_" + MODEL_B.toUpperCase();

    protected static final String JOINTABLE_B_C = "JOINTABLE_" + MODEL_B.toUpperCase() + "_" + MODEL_C.toUpperCase();

    protected static final String JOINTABLE_A_C = "JOINTABLE_" + MODEL_A.toUpperCase() + "_" + MODEL_C.toUpperCase();

    protected static final String FIELD_MTM_A = "manyToManyA";

    protected static final String FIELD_MTM_B = "manyToManyB";

    protected static final String FIELD_MTM_C = "manyToManyC";

    protected static final String FIELD_BT_A = "belongsToA";

    protected static final String FIELD_BT_B = "belongsToB";

    protected static final String FIELD_BT_C = "belongsToC";

    protected static final String FIELD_BT_A_NULLIFY = "nullifyBelongsToA";

    protected static final String FIELD_BT_B_NULLIFY = "nullifyBelongsToB";

    protected static final String FIELD_BT_C_NULLIFY = "nullifyBelongsToC";

    protected DataDefinition aDataDefinition, bDataDefinition, cDataDefinition;

    @Before
    public final void cyclesTestInit() {
        pluginManager.enablePlugin(PLUGIN_CYCLES);

        aDataDefinition = dataDefinitionService.get(PLUGIN_CYCLES, MODEL_A);
        bDataDefinition = dataDefinitionService.get(PLUGIN_CYCLES, MODEL_B);
        cDataDefinition = dataDefinitionService.get(PLUGIN_CYCLES, MODEL_C);
    }

    @After
    public void cyclesTestDestroy() {
        jdbcTemplate.execute("delete from " + JOINTABLE_A_B);
        jdbcTemplate.execute("delete from " + JOINTABLE_B_C);
        jdbcTemplate.execute("delete from " + JOINTABLE_A_C);
        // deletion order matters
        jdbcTemplate.execute("delete from " + TABLE_NAME_C);
        jdbcTemplate.execute("delete from " + TABLE_NAME_B);
        jdbcTemplate.execute("delete from " + TABLE_NAME_A);
    }

    protected Entity buildEntity(final DataDefinition dataDefinition) {
        return save(dataDefinition.create());
    }

    protected Entity save(final Entity entity) {
        return entity.getDataDefinition().save(entity);
    }

    @Test
    public final void shouldNotFallIntoInfiniteCycleDuringCascadeManyToManyDeletion2entities() {
        // given
        Entity modelA = buildEntity(aDataDefinition);
        Entity modelB = buildEntity(bDataDefinition);

        modelA.setField(FIELD_MTM_B, Lists.newArrayList(modelB));
        modelB.setField(FIELD_MTM_A, Lists.newArrayList(modelA));

        modelA = fromDb(save(modelA));
        modelB = fromDb(save(modelB));

        // when
        EntityOpResult result = aDataDefinition.delete(modelA.getId());

        // then
        Assert.assertTrue(result.isSuccessfull());
        Assert.assertNull(fromDb(modelA));
        Assert.assertNull(fromDb(modelB));
    }

    @Test
    public final void shouldNotFallIntoInfiniteCycleDuringCascadeManyToManyDeletion3entities() {
        // given
        Entity modelA = buildEntity(aDataDefinition);
        Entity modelB = buildEntity(bDataDefinition);
        Entity modelC = buildEntity(cDataDefinition);

        modelA.setField(FIELD_MTM_B, Lists.newArrayList(modelB));
        modelB.setField(FIELD_MTM_A, Lists.newArrayList(modelA));

        modelB.setField(FIELD_MTM_C, Lists.newArrayList(modelC));
        modelC.setField(FIELD_MTM_B, Lists.newArrayList(modelB));

        modelC.setField(FIELD_MTM_A, Lists.newArrayList(modelA));
        modelA.setField(FIELD_MTM_C, Lists.newArrayList(modelC));

        // BEWARE! Remember about filling both sides before save!
        modelA = fromDb(save(modelA));
        modelB = fromDb(save(modelB));
        modelC = fromDb(save(modelC));

        // when
        EntityOpResult result = aDataDefinition.delete(modelA.getId());

        // then
        Assert.assertTrue(result.isSuccessfull());
        Assert.assertNull(fromDb(modelA));
        Assert.assertNull(fromDb(modelB));
        Assert.assertNull(fromDb(modelC));
    }

    @Test
    public final void shouldNotFallIntoInfiniteCycleDuringCascadeHasManyDeletion2entities() {
        // given
        Entity modelA = buildEntity(aDataDefinition);
        Entity modelB = buildEntity(bDataDefinition);

        modelA.setField(FIELD_BT_B, modelB.getId());
        modelB.setField(FIELD_BT_A, modelA.getId());

        modelA = fromDb(save(modelA));
        modelB = fromDb(save(modelB));

        // when
        EntityOpResult result = aDataDefinition.delete(modelA.getId());

        // then
        Assert.assertTrue(result.isSuccessfull());
        Assert.assertNull(fromDb(modelA));
        Assert.assertNull(fromDb(modelB));
    }

    @Test
    public final void shouldNotFallIntoInfiniteCycleDuringCascadeHasManyDeletion3entities() {
        // given
        Entity modelA = buildEntity(aDataDefinition);
        Entity modelB = buildEntity(bDataDefinition);
        Entity modelC = buildEntity(cDataDefinition);

        modelA.setField(FIELD_BT_C, modelC.getId());
        modelB.setField(FIELD_BT_A, modelA.getId());
        modelC.setField(FIELD_BT_B, modelB.getId());

        modelA = fromDb(save(modelA));
        modelB = fromDb(save(modelB));
        modelC = fromDb(save(modelC));

        // when
        EntityOpResult result = aDataDefinition.delete(modelA.getId());

        // then
        Assert.assertTrue(result.isSuccessfull());
        Assert.assertNull(fromDb(modelA));
        Assert.assertNull(fromDb(modelB));
        Assert.assertNull(fromDb(modelC));
    }

    @Test
    public final void shouldNotFallIntoInfiniteCycleDuringCascadeHasManyNullification2entities() {
        // given
        Entity modelA = buildEntity(aDataDefinition);
        Entity modelB = buildEntity(bDataDefinition);

        modelA.setField(FIELD_BT_B_NULLIFY, modelB.getId());
        modelB.setField(FIELD_BT_A_NULLIFY, modelA.getId());

        modelA = fromDb(save(modelA));
        modelB = fromDb(save(modelB));

        // when
        EntityOpResult result = aDataDefinition.delete(modelA.getId());

        // then
        Assert.assertTrue(result.isSuccessfull());
        Assert.assertNull(fromDb(modelA));

        Assert.assertNotNull(fromDb(modelB));
        Assert.assertNull(fromDb(modelB).getField(FIELD_BT_A_NULLIFY));
    }

    @Test
    public final void shouldNotFallIntoInfiniteCycleDuringCascadeHasManyNullification3entities() {
        // given
        Entity modelA = buildEntity(aDataDefinition);
        Entity modelB = buildEntity(bDataDefinition);
        Entity modelC = buildEntity(cDataDefinition);

        modelA.setField(FIELD_BT_C_NULLIFY, modelC.getId());
        // modelC.setField("nullifyHasManyA", Lists.newArrayList(modelA));

        modelB.setField(FIELD_BT_A_NULLIFY, modelA.getId());
        // modelA.setField("nullifyHasManyB", Lists.newArrayList(modelB));

        modelC.setField(FIELD_BT_B_NULLIFY, modelB.getId());
        // modelB.setField("nullifyHasManyC", Lists.newArrayList(modelC));

        modelC = fromDb(save(modelC));
        modelB = fromDb(save(modelB));
        modelA = fromDb(save(modelA));

        // when
        EntityOpResult result = aDataDefinition.delete(modelA.getId());

        // then
        Assert.assertTrue(result.isSuccessfull());
        Assert.assertNull(fromDb(modelA));

        Assert.assertNotNull(fromDb(modelB));
        Assert.assertNull(fromDb(modelB).getField(FIELD_BT_A_NULLIFY));

        Assert.assertNotNull(fromDb(modelC));
        Assert.assertNotNull(fromDb(modelC).getField(FIELD_BT_B_NULLIFY));
    }

}
