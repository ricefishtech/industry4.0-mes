/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 * <p>
 * This file is part of Qcadoo.
 * <p>
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.model.integration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityOpResult;
import com.qcadoo.model.internal.ProxyEntity;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ManyToManyIntegrationTest extends IntegrationTest {

    // http://docs.jboss.org/hibernate/core/3.3/reference/en/html/performance.html#performance-collections

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSaveManyToManyField() throws Exception {
        // given
        DataDefinition productDataDefinition = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDataDefinition = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        Entity firstProduct = productDataDefinition.save(createProduct("asd", "00001"));
        Entity secondProduct = productDataDefinition.save(createProduct("fgh", "00002"));
        Entity thirdProduct = productDataDefinition.save(createProduct("jkl", "00003"));

        Entity firstPart = fromDb(save(createPart("qwe", firstProduct, Lists.newArrayList(firstProduct, secondProduct))));
        Entity secondPart = fromDb(save(createPart("rty", secondProduct, Lists.newArrayList(firstProduct, thirdProduct))));
        Entity thirdPart = fromDb(save(createPart("uiop", thirdProduct,
                Lists.newArrayList(firstProduct, secondProduct, thirdProduct))));

        // when
        firstProduct = productDataDefinition.get(firstProduct.getId());
        secondProduct = productDataDefinition.get(secondProduct.getId());
        thirdProduct = productDataDefinition.get(thirdProduct.getId());

        // then
        Collection<Entity> firstProductParts = (Collection<Entity>) firstProduct.getField("partsManyToMany");
        assertNotNull(firstProductParts);
        assertEquals(3, firstProductParts.size());
        checkProxyCollection(firstProductParts, Lists.newArrayList(firstPart, secondPart, thirdPart));

        Collection<Entity> secondProductParts = (Collection<Entity>) secondProduct.getField("partsManyToMany");
        assertNotNull(secondProductParts);
        assertEquals(2, secondProductParts.size());
        checkProxyCollection(secondProductParts, Lists.newArrayList(firstPart, thirdPart));

        Collection<Entity> thirdProductParts = (Collection<Entity>) thirdProduct.getField("partsManyToMany");
        assertNotNull(thirdProductParts);
        assertEquals(2, thirdProductParts.size());
        checkProxyCollection(thirdProductParts, Lists.newArrayList(secondPart, thirdPart));
    }

    private void checkProxyCollection(final Collection<Entity> proxyEntitiesSet, final List<Entity> entitiesList) {
        Set<Entity> loadedEntities = Sets.newHashSet();
        for (Entity proxyEntity : proxyEntitiesSet) {
            assertTrue(proxyEntity instanceof ProxyEntity);
            assertTrue(proxyEntity.isValid());
            loadedEntities.add(fromDb(proxyEntity));
        }
        assertEquals(entitiesList.size(), loadedEntities.size());
        assertTrue(loadedEntities.containsAll(entitiesList));
    }

    @Test
    public void shouldGeteManyToManyFieldReturnDistinctCollection() throws Exception {
        // given
        Entity firstProduct = save(createProduct("asd", "00001"));

        Entity firstPart = fromDb(save(createPart("qwe", firstProduct,
                Lists.newArrayList(firstProduct, firstProduct, firstProduct))));

        firstProduct = fromDb(firstProduct);

        // when
        List<Entity> firstProductParts = firstProduct.getManyToManyField("partsManyToMany");

        // then
        assertEquals(1, firstProductParts.size());
        assertEquals(firstPart, fromDb(firstProductParts.get(0)));
    }

    @Test
    public void shouldEntityWithManyToManyFieldHashCodeDoNotMakeInfinityCycle() throws Exception {
        // given
        Entity firstProduct = save(createProduct("asd", "00001"));

        save(createPart("qwe", firstProduct, Lists.newArrayList(firstProduct)));

        firstProduct = fromDb(firstProduct);

        // when
        try {
            firstProduct.hashCode();
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldEntityWithManyToManyFieldEqualsDoNotMakeInfinityCycleAndReturnTue() throws Exception {
        // given
        Entity product = createProduct("asd", "00001");
        product.setField("partsManyToMany", Lists.newArrayList(createPart("qwe", product, Lists.newArrayList(product))));

        Entity otherProduct = createProduct("asd", "00001");
        otherProduct.setField("partsManyToMany",
                Lists.newArrayList(createPart("qwe", otherProduct, Lists.newArrayList(otherProduct))));

        // when
        try {
            Assert.assertEquals(product, otherProduct);
        } catch (StackOverflowError e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldCopyManyToManyField() {
        // given
        Entity firstProduct = save(createProduct("asd", "00001"));
        Entity secondProduct = save(createProduct("fgh", "00002"));
        Entity thirdProduct = save(createProduct("jkl", "00003"));

        Entity firstPart = fromDb(save(createPart("qwe", firstProduct, Lists.newArrayList(firstProduct, secondProduct))));
        Entity secondPart = fromDb(save(createPart("rty", secondProduct, Lists.newArrayList(firstProduct, thirdProduct))));
        Entity thirdPart = fromDb(save(createPart("uiop", thirdProduct,
                Lists.newArrayList(firstProduct, secondProduct, thirdProduct))));

        // when
        Entity copyFirstProduct = firstProduct.getDataDefinition().copy(firstProduct.getId()).get(0);
        copyFirstProduct = fromDb(copyFirstProduct);
        Entity copyFirstPart = firstPart.getDataDefinition().copy(firstPart.getId()).get(0);
        copyFirstPart = fromDb(copyFirstPart);

        // then
        Collection<Entity> firstProductParts = copyFirstProduct.getManyToManyField("partsManyToMany");
        assertNotNull(firstProductParts);
        assertEquals(3, firstProductParts.size());
        checkProxyCollection(firstProductParts, Lists.newArrayList(firstPart, secondPart, thirdPart));

        Collection<Entity> firstPartsCopied = copyFirstPart.getManyToManyField("products");
        assertNotNull(firstPartsCopied);
        assertEquals(0, firstPartsCopied.size());
    }

    @Test
    public final void shouldPerformCascadeDeletion() {
        // given
        Entity firstProduct = save(createProduct("asd", "00001"));
        Entity secondProduct = save(createProduct("fgh", "00002"));
        Entity thirdProduct = save(createProduct("jkl", "00003"));
        Entity anotherProduct = save(createProduct("qwertyuiop", "00004"));

        Entity firstPart = save(createPart("qwe", anotherProduct, Lists.newArrayList(firstProduct, secondProduct)));
        Entity secondPart = save(createPart("rty", anotherProduct, Lists.newArrayList(firstProduct, thirdProduct)));
        Entity thirdPart = save(createPart("uiop", anotherProduct, Lists.newArrayList(firstProduct, secondProduct, thirdProduct)));

        // when
        EntityOpResult result = delete(secondProduct);

        // then
        Assert.assertTrue(result.isSuccessfull());

        Assert.assertNull(fromDb(firstPart));
        Assert.assertNotNull(fromDb(secondPart));
        Assert.assertNull(fromDb(thirdPart));

        Assert.assertNotNull(fromDb(firstProduct));
        Assert.assertNull(fromDb(secondProduct));
        Assert.assertNotNull(fromDb(thirdProduct));
    }

    @Test
    public final void shouldPerformCascadeDeletionDeeplyVariant() {
        // given
        Entity firstProduct = save(createProduct("asd", "00001"));
        Entity secondProduct = save(createProduct("fgh", "00002"));
        Entity thirdProduct = save(createProduct("jkl", "00003"));
        Entity anotherProduct = save(createProduct("qwertyuiop", "00004"));

        Entity firstPart = save(createPart("qwe", anotherProduct, Lists.newArrayList(firstProduct, secondProduct)));
        Entity secondPart = save(createPart("rty", anotherProduct, Lists.newArrayList(firstProduct, thirdProduct)));
        Entity thirdPart = save(createPart("uiop", anotherProduct, Lists.newArrayList(firstProduct, secondProduct, thirdProduct)));
        Entity factory1 = createFactory("factory1");
        factory1.setField("parentPart", firstPart);
        factory1.setField("deletionIsProhibited", false);
        factory1 = save(factory1);
        firstPart = fromDb(firstPart);

        // when
        EntityOpResult result = delete(secondProduct);

        // then
        Assert.assertTrue(result.isSuccessfull());

        Assert.assertNull(fromDb(firstPart));
        Assert.assertNotNull(fromDb(secondPart));
        Assert.assertNull(fromDb(thirdPart));
        Assert.assertNull(fromDb(factory1));

        Assert.assertNotNull(fromDb(firstProduct));
        Assert.assertNull(fromDb(secondProduct));
        Assert.assertNotNull(fromDb(thirdProduct));
    }

    @Test
    public final void shouldOnDeleteHookRejectCascadeDeletion() {
        // given
        Entity firstProduct = save(createProduct("asd", "00001"));
        Entity secondProduct = save(createProduct("fgh", "00002"));
        Entity thirdProduct = save(createProduct("jkl", "00003"));
        Entity anotherProduct = save(createProduct("qwertyuiop", "00004"));

        Entity firstPart = save(createPart("qwe", anotherProduct, Lists.newArrayList(firstProduct, secondProduct)));
        Entity secondPart = save(createPart("rty", anotherProduct, Lists.newArrayList(firstProduct, thirdProduct)));
        Entity thirdPart = save(createPart("uiop", anotherProduct, Lists.newArrayList(firstProduct, secondProduct, thirdProduct)));

        thirdPart.setField("deletionIsProhibited", true);
        thirdPart = save(thirdPart);

        // when
        EntityOpResult result = delete(secondProduct);

        // then
        Assert.assertFalse(result.isSuccessfull());

        Assert.assertNotNull(fromDb(firstPart));
        Assert.assertNotNull(fromDb(secondPart));
        Assert.assertNotNull(fromDb(thirdPart));

        Assert.assertNotNull(fromDb(firstProduct));
        Assert.assertNotNull(fromDb(secondProduct));
        Assert.assertNotNull(fromDb(thirdProduct));
    }

    @Test
    public final void shouldOnDeleteHookRejectCascadeDeletionDeeplyVariant() {
        // given
        Entity firstProduct = save(createProduct("asd", "00001"));
        Entity secondProduct = save(createProduct("fgh", "00002"));
        Entity thirdProduct = save(createProduct("jkl", "00003"));
        Entity anotherProduct = save(createProduct("qwertyuiop", "00004"));

        Entity firstPart = save(createPart("qwe", anotherProduct, Lists.newArrayList(firstProduct, secondProduct)));
        Entity secondPart = save(createPart("rty", anotherProduct, Lists.newArrayList(firstProduct, thirdProduct)));
        Entity thirdPart = save(createPart("uiop", anotherProduct, Lists.newArrayList(firstProduct, secondProduct, thirdProduct)));
        Entity factory1 = createFactory("factory1");
        factory1.setField("parentPart", firstPart);
        factory1.setField("deletionIsProhibited", true);
        factory1 = save(factory1);
        firstPart = fromDb(firstPart);

        // when
        EntityOpResult result = delete(secondProduct);

        // then
        Assert.assertFalse(result.isSuccessfull());

        Assert.assertNotNull(fromDb(firstPart));
        Assert.assertNotNull(fromDb(secondPart));
        Assert.assertNotNull(fromDb(thirdPart));
        Assert.assertNotNull(fromDb(factory1));

        Assert.assertNotNull(fromDb(firstProduct));
        Assert.assertNotNull(fromDb(secondProduct));
        Assert.assertNotNull(fromDb(thirdProduct));
    }

    @Test
    public final void shouldPerformCascadeNullification() {
        // given
        Entity firstProduct = save(createProduct("asd", "00001"));
        Entity secondProduct = save(createProduct("fgh", "00002"));
        Entity thirdProduct = save(createProduct("jkl", "00003"));
        Entity anotherProduct = save(createProduct("qwertyuiop", "00004"));

        Entity firstPart = save(createPart("qwe", anotherProduct, Lists.newArrayList(firstProduct, secondProduct)));
        Entity secondPart = save(createPart("rty", anotherProduct, Lists.newArrayList(firstProduct, thirdProduct)));
        Entity thirdPart = save(createPart("uiop", anotherProduct, Lists.newArrayList(firstProduct, secondProduct, thirdProduct)));

        // when
        delete(secondPart);

        // then
        Entity firstProductFromDb = fromDb(firstProduct);
        Assert.assertNotNull(firstProductFromDb);
        Collection<Entity> firstProductParts = firstProductFromDb.getManyToManyField("partsManyToMany");
        Assert.assertEquals(2, firstProductParts.size());
        Assert.assertTrue(firstProductParts.contains(fromDb(firstPart)));
        Assert.assertFalse(firstProductParts.contains(fromDb(secondPart)));
        Assert.assertTrue(firstProductParts.contains(fromDb(thirdPart)));

        Entity secondProductFromDb = fromDb(secondProduct);
        Assert.assertNotNull(secondProductFromDb);
        Collection<Entity> secondProductParts = secondProductFromDb.getManyToManyField("partsManyToMany");
        Assert.assertEquals(2, secondProductParts.size());
        Assert.assertTrue(secondProductParts.contains(fromDb(firstPart)));
        Assert.assertFalse(secondProductParts.contains(fromDb(secondPart)));
        Assert.assertTrue(secondProductParts.contains(fromDb(thirdPart)));

        Entity thirdProductFromDb = fromDb(thirdProduct);
        Assert.assertNotNull(thirdProductFromDb);
        Collection<Entity> thirdProductParts = thirdProductFromDb.getManyToManyField("partsManyToMany");
        Assert.assertEquals(1, thirdProductParts.size());
        Assert.assertFalse(thirdProductParts.contains(fromDb(firstPart)));
        Assert.assertFalse(thirdProductParts.contains(fromDb(secondPart)));
        Assert.assertTrue(thirdProductParts.contains(fromDb(thirdPart)));

        Assert.assertNotNull(fromDb(firstPart));
        Assert.assertNull(fromDb(secondPart));
        Assert.assertNotNull(fromDb(thirdPart));
    }

    @Test
    public void shouldLoadLazyLoading() {
        // given
        Entity product1 = save(createProduct("name-lazy-1", "number-lazy-1"));
        Entity vEntity = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, "versionableEntity").create();
        vEntity.setField("name", "name-vEntity-1");
        vEntity.setField("number", "number-vEntity-1");
        vEntity.setField("products", Arrays.asList(product1, fromDb(save(createProduct("name-lazy-2", "number-lazy-2"))),
                fromDb(save(createProduct("name-lazy-3", "number-lazy-3")))));

        vEntity = save(vEntity);

        // when
        Entity vEntityDb = fromDb(vEntity);
        Entity product1Db = fromDb(product1);

        // then
        Assert.assertEquals(3, vEntityDb.getManyToManyField("products").size());
        Assert.assertEquals(1, product1Db.getManyToManyField("lazyManyToMany").size());
        Assert.assertTrue(vEntity.getManyToManyField("products").contains(product1Db));
    }
}
