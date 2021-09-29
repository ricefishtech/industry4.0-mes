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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.model.api.EntityOpResult;

public class HasManyIntegrationTest extends IntegrationTest {

    // http://docs.jboss.org/hibernate/core/3.3/reference/en/html/performance.html#performance-collections

    private static final String FIELD_COMPONENTS = "components";

    private static final String FIELD_PARTS = "parts";

    private DataDefinition productDataDefinition, machineDataDefinition, componentDataDefinition, partDataDefinition;

    private static final Function<Entity, Long> EXTRACT_ID = new Function<Entity, Long>() {

        @Override
        public Long apply(final Entity entity) {
            return entity.getId();
        }
    };

    @Before
    public final void init() {
        productDataDefinition = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        machineDataDefinition = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        componentDataDefinition = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);
        partDataDefinition = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);
    }

    private void checkComponents(final Entity entity, final Entity... expectedEntities) {
        checkHasManyField(entity, FIELD_COMPONENTS, expectedEntities);
    }

    private void checkParts(final Entity entity, final Entity... expectedEntities) {
        checkHasManyField(entity, FIELD_PARTS, expectedEntities);
    }

    private void checkHasManyField(final Entity entity, final String fieldName, final Entity... expectedEntities) {
        EntityList entities = entity.getHasManyField(fieldName);
        Set<Long> ids = Sets.newHashSet(Collections2.transform(entity.getHasManyField(fieldName), EXTRACT_ID));
        assertEquals(expectedEntities.length, entities.size());
        for (Entity expectedEntity : expectedEntities) {
            assertTrue("missing " + expectedEntity, ids.contains(expectedEntity.getId()));
        }
    }

    @Test
    @Ignore
    public void shouldSaveHasManyField() throws Exception {
        // given
        Entity product = productDataDefinition.save(createProduct("asd", "asd"));
        Entity machine = machineDataDefinition.save(createMachine("asd"));
        Entity component1 = componentDataDefinition.save(createComponent("name1", product, machine));
        Entity component2 = componentDataDefinition.save(createComponent("name2", product, machine));

        // when
        product = fromDb(product);

        // then
        checkComponents(product, component1, component2);
    }

    @Test
    public final void shouldPerformCascadeDeletion() {
        // given
        Entity product = productDataDefinition.save(createProduct("someName", "someNumber"));
        Entity part1 = partDataDefinition.save(createPart("part1", product));
        Entity part2 = partDataDefinition.save(createPart("part2", product));
        Entity part3 = partDataDefinition.save(createPart("part3", null));

        product = fromDb(product);

        // when
        EntityOpResult result = productDataDefinition.delete(product.getId());

        // then
        assertTrue(result.isSuccessfull());

        assertNull(fromDb(product));
        assertNull(fromDb(part1));
        assertNull(fromDb(part2));

        assertNotNull(fromDb(part3));
    }

    @Test
    public final void shouldPerformOrphansDeletion() {
        // given
        Entity product = productDataDefinition.save(createProduct("someName", "someNumber"));
        Entity part1 = partDataDefinition.save(createPart("part1", product));
        Entity part2 = partDataDefinition.save(createPart("part2", product));
        Entity part3 = partDataDefinition.save(createPart("part3", null));

        product = fromDb(product);
        checkParts(product, part1, part2);

        // when
        product.setField(FIELD_PARTS, Lists.newArrayList(part2, part3));
        Entity savedProduct = productDataDefinition.save(product);

        // then
        assertTrue(savedProduct.isValid());
        assertNull(fromDb(part1));
        checkParts(savedProduct, part2, part3);
        checkParts(fromDb(product), part2, part3);
    }

    @Test
    public final void shouldOnDeleteHookRejectCascadeNullification() {
        // given
        Entity component = createComponent("component", null, null);
        component.setField("deletionIsAllowed", false);
        component = componentDataDefinition.save(component);

        Entity part1 = partDataDefinition.save(createComponentPart("part1", component));
        Entity part2 = partDataDefinition.save(createComponentPart("part2", component));
        Entity part3 = partDataDefinition.save(createComponentPart("part3", null));

        component = fromDb(component);

        // when
        EntityOpResult result = componentDataDefinition.delete(component.getId());

        // then
        assertFalse(result.isSuccessfull());

        Entity componentFromDb = fromDb(component);
        assertNotNull(componentFromDb);
        checkParts(componentFromDb, part1, part2);

        Entity part1fromDb = fromDb(part1);
        assertNotNull(part1fromDb);
        assertNotNull(part1fromDb.getField("component"));

        Entity part2fromDb = fromDb(part2);
        assertNotNull(part2fromDb);
        assertNotNull(part2fromDb.getField("component"));

        Entity part3fromDb = fromDb(part3);
        assertNotNull(part3fromDb);
        assertNull(part3fromDb.getField("component"));
    }

    @Test
    public final void shouldPerformCascadeNullification() {
        // given
        Entity component = createComponent("component", null, null);
        component.setField("deletionIsAllowed", true);
        component = componentDataDefinition.save(component);

        Entity part1 = partDataDefinition.save(createComponentPart("part1", component));
        Entity part2 = partDataDefinition.save(createComponentPart("part2", component));
        Entity part3 = partDataDefinition.save(createComponentPart("part3", null));

        component = fromDb(component);

        // when
        EntityOpResult result = componentDataDefinition.delete(component.getId());

        // then
        assertTrue(result.isSuccessfull());

        assertNull(fromDb(component));

        Entity part1fromDb = fromDb(part1);
        assertNotNull(part1fromDb);
        assertNull(part1fromDb.getField("component"));

        Entity part2fromDb = fromDb(part2);
        assertNotNull(part2fromDb);
        assertNull(part2fromDb.getField("component"));

        Entity part3fromDb = fromDb(part3);
        assertNotNull(part3fromDb);
        assertNull(part3fromDb.getField("component"));
    }

    @Test
    public final void shouldPerformOrphansNullification() {
        // given
        Entity component = createComponent("component", null, null);
        component.setField("deletionIsAllowed", true);
        component = componentDataDefinition.save(component);

        Entity part1 = partDataDefinition.save(createComponentPart("part1", component));
        Entity part2 = partDataDefinition.save(createComponentPart("part2", component));
        Entity part3 = partDataDefinition.save(createComponentPart("part3", null));

        component = fromDb(component);

        // when
        component.setField(FIELD_PARTS, Lists.newArrayList(part2, part3));
        Entity savedComponent = componentDataDefinition.save(component);

        // then
        assertTrue(savedComponent.isValid());

        checkParts(fromDb(savedComponent), part2, part3);

        Entity part1fromDb = fromDb(part1);
        assertNotNull(part1fromDb);
        assertNull(part1fromDb.getField("component"));
    }

}
