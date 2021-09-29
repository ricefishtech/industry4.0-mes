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

import org.junit.Ignore;
import org.junit.Test;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.*;
import com.qcadoo.model.internal.types.BelongsToEntityType;
import com.qcadoo.model.internal.types.IntegerType;
import com.qcadoo.model.internal.types.StringType;

public class CriteriaIntegrationTest extends IntegrationTest {

    @Test
    public void shouldFindByEmptyQuery() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));

        // when
        Entity product = productDao.find().uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getStringField("name"), product.getStringField("name"));
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfThereIsNoUniqueResult() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("asd2", "asd2"));

        // when
        productDao.find().uniqueResult();
    }

    @Test
    public void shouldFindAllByQuery() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("asd2", "asd2"));

        // when
        SearchResult result = productDao.find().list();

        // then
        assertEquals(2, result.getTotalNumberOfEntities());
        assertEquals(2, result.getEntities().size());
    }

    @Test
    public void shouldFindByQueryWithAlias() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("asd2", "asd2"));

        // when
        SearchResult result = productDao.findWithAlias("xxx").add(SearchRestrictions.eq("xxx.name", "asd")).list();

        // then
        assertEquals(1, result.getTotalNumberOfEntities());
        assertEquals(1, result.getEntities().size());
    }

    @Test
    public void shouldFindByQueryWithWhere() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity product5 = createProduct("cee", "cee");
        product5.setField("quantity", 3);
        productDao.save(product5);

        Entity product1 = createProduct("bsd", "bsd1");
        product1.setField("quantity", 1);
        productDao.save(product1);

        Entity product2 = createProduct("bsd", "bsd2");
        product2.setField("quantity", 3);
        productDao.save(product2);

        Entity product3 = createProduct("abc", "abc");
        product3.setField("quantity", 1);
        productDao.save(product3);

        Entity product4 = createProduct("bef", "bef");
        product4.setField("quantity", 2);
        productDao.save(product4);

        // when
        SearchResult searchResult = productDao
                .find()
                .add(SearchRestrictions.or(SearchRestrictions.like("name", "b%"), SearchRestrictions.like("name", "c%")))
                .setProjection(
                        SearchProjections.list().add(SearchProjections.groupField("name"))
                                .add(SearchProjections.alias(SearchProjections.sum("quantity"), "quantity"))
                                .add(SearchProjections.alias(SearchProjections.rowCount(), "products"))).setFirstResult(0)
                .setMaxResults(2).addOrder(SearchOrders.asc("name")).list();

        // then
        assertEquals(3, searchResult.getTotalNumberOfEntities());
        assertEquals(2, searchResult.getEntities().size());
        assertEquals("bef", searchResult.getEntities().get(0).getField("0"));
        assertEquals(Long.valueOf(2), searchResult.getEntities().get(0).getField("quantity"));
        assertEquals(Long.valueOf(1), searchResult.getEntities().get(0).getField("products"));
        assertEquals("bsd", searchResult.getEntities().get(1).getField("0"));
        assertEquals(Long.valueOf(4), searchResult.getEntities().get(1).getField("quantity"));
        assertEquals(Long.valueOf(2), searchResult.getEntities().get(1).getField("products"));
    }

    @Test
    public void shouldFindByQueryWithSubqueriesOnCollection() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        productDao.save(createProduct("qwe", "qwe"));
        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        partDao.save(createPart("name", expectedProduct));

        // when
        Entity product = productDao.find().add(SearchRestrictions.isNotEmpty("parts")).uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithBelongsToSubqueriesUsingDefaultJoinType() throws Exception {
        findByQueryWithBelongsToSubqueries(null);
    }

    @Test
    public void shouldFindByQueryWithBelongsToSubqueriesExplicitlyUsingInnerJoin() throws Exception {
        findByQueryWithBelongsToSubqueries(JoinType.INNER);
    }

    @Test
    public void shouldFindByQueryWithBelongsToSubqueriesExplicitlyUsingLeftJoin() throws Exception {
        findByQueryWithBelongsToSubqueries(JoinType.LEFT);
    }

    // TODO MAKU & KRNA - check after upgrade hibernate version
    @Ignore
    @Test
    public void shouldFindByQueryWithBelongsToSubqueriesExplicitlyUsingFullJoin() throws Exception {
        findByQueryWithBelongsToSubqueries(JoinType.FULL);
    }

    private void findByQueryWithBelongsToSubqueries(final JoinType joinType) throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        productDao.save(createProduct("qwe", "qwe"));
        Entity product = productDao.save(createProduct("asd", "asd"));
        Entity expectedPart = partDao.save(createPart("name", product));

        // when
        SearchCriteriaBuilder criteria = partDao.find();
        criteria.createCriteria("product", "product", joinType).add(SearchRestrictions.eq("name", "asd"));
        Entity part = criteria.uniqueResult();

        // then
        assertEquals(expectedPart.getId(), part.getId());
        assertEquals(expectedPart.getDataDefinition(), part.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithHasManySubqueriesUsingDefaultJoinType() throws Exception {
        findByQueryWithHasManySubqueries(null);
    }

    @Test
    public void shouldFindByQueryWithHasManySubqueriesExplicitlyUsingInnerJoin() throws Exception {
        findByQueryWithHasManySubqueries(JoinType.INNER);
    }

    @Test
    public void shouldFindByQueryWithHasManySubqueriesExplicitlyUsingLeftJoin() throws Exception {
        findByQueryWithHasManySubqueries(JoinType.LEFT);
    }

    // TODO MAKU & KRNA - check after upgrade hibernate version
    @Ignore
    @Test
    public void shouldFindByQueryWithHasManySubqueriesExplicitlyUsingFullJoin() throws Exception {
        findByQueryWithHasManySubqueries(JoinType.FULL);
    }

    private void findByQueryWithHasManySubqueries(final JoinType joinType) throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        productDao.save(createProduct("qwe", "qwe"));
        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        partDao.save(createPart("name", expectedProduct));

        // when
        SearchCriteriaBuilder criteria = productDao.find();
        criteria.createCriteria("parts", "parts", joinType).add(SearchRestrictions.eq("name", "name"));
        Entity product = criteria.uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithSubqueriesUsingDefaultJoinType() throws Exception {
        findByQueryWithSubqueries(null);
    }

    @Test
    public void shouldFindByQueryWithSubqueriesExplicitlyUsingInnerJoin() throws Exception {
        findByQueryWithSubqueries(JoinType.INNER);
    }

    @Test
    public void shouldFindByQueryWithSubqueriesExplicitlyUsingLeftJoin() throws Exception {
        findByQueryWithSubqueries(JoinType.LEFT);
    }

    // TODO MAKU & KRNA - check after upgrade hibernate version
    @Ignore
    @Test
    public void shouldFindByQueryWithSubqueriesExplicitlyUsingFullJoin() throws Exception {
        findByQueryWithSubqueries(JoinType.FULL);
    }

    private void findByQueryWithSubqueries(final JoinType joinType) throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        productDao.save(createProduct("qwe", "qwe"));
        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        partDao.save(createPart("name", expectedProduct));

        // when
        SearchCriteriaBuilder criteria = productDao.find();
        criteria.createCriteria("parts", "parts", joinType).add(SearchRestrictions.isNotNull("parts.id"));
        Entity product = criteria.uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithAliasedSubqueries() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        productDao.save(createProduct("qwe", "qwe"));
        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        partDao.save(createPart("name", expectedProduct));

        // when
        SearchCriteriaBuilder subcriteria = partDao.findWithAlias("part")
                .add(SearchRestrictions.eqField("part.product.id", "p.id")).setProjection(SearchProjections.id());
        Entity product = productDao.findWithAlias("p").add(SearchSubqueries.exists(subcriteria)).uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithEntityParameters() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        Entity expectedPart = partDao.save(createPart("name", expectedProduct));

        // when
        Entity part = partDao.find().add(SearchRestrictions.belongsTo("product", expectedProduct)).uniqueResult();

        // then
        assertEquals(expectedPart.getId(), part.getId());
        assertEquals(expectedPart.getDataDefinition(), part.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithEntityIdParameters() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        Entity expectedPart = partDao.save(createPart("name", expectedProduct));

        // when
        Entity part = partDao.find()
                .add(SearchRestrictions.belongsTo("product", PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT, expectedProduct.getId()))
                .uniqueResult();

        // then
        assertEquals(expectedPart.getId(), part.getId());
        assertEquals(expectedPart.getDataDefinition(), part.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithInlineEntityId() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        Entity expectedPart = partDao.save(createPart("name", expectedProduct));

        // when
        Entity part = partDao.find().add(SearchRestrictions.eq("product.id", expectedProduct.getId())).uniqueResult();

        // then
        assertEquals(expectedPart.getId(), part.getId());
        assertEquals(expectedPart.getDataDefinition(), part.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithBelongsToFieldUsingDefaultJoinType() throws Exception {
        findByQueryWithBelongsToField(null);
    }

    @Test
    public void shouldFindByQueryWithBelongsToFieldExplicitlyUsingInnerJoin() throws Exception {
        findByQueryWithBelongsToField(JoinType.INNER);
    }

    @Test
    public void shouldFindByQueryWithBelongsToFieldExplicitlyUsingLeftJoin() throws Exception {
        findByQueryWithBelongsToField(JoinType.LEFT);
    }

    // TODO MAKU & KRNA - check after upgrade hibernate version
    @Ignore
    @Test
    public void shouldFindByQueryWithBelongsToFieldExplicitlyUsingFullJoin() throws Exception {
        findByQueryWithBelongsToField(JoinType.FULL);
    }

    private void findByQueryWithBelongsToField(final JoinType joinType) throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        Entity product = createProduct("asd", "asd");
        product.setField("quantity", 1);
        product = productDao.save(product);

        partDao.save(createPart("name", product));

        // when
        Entity result = partDao
                .find()
                .add(SearchRestrictions.like("product.name", "asd"))
                .createAlias("product", "product", joinType)
                .setProjection(
                        SearchProjections.list().add(SearchProjections.field("product.quantity"))
                                .add(SearchProjections.field("name")).add(SearchProjections.field("product"))).uniqueResult();

        // then
        assertEquals(IntegerType.class, result.getDataDefinition().getField("0").getType().getClass());
        assertEquals(StringType.class, result.getDataDefinition().getField("1").getType().getClass());
        assertEquals(BelongsToEntityType.class, result.getDataDefinition().getField("2").getType().getClass());
        assertEquals(1, result.getField("0"));
        assertEquals("name", result.getStringField("1"));
        assertEquals(product.getId(), result.getBelongsToField("2").getId());
    }

    @Test
    public void shouldFindByQueryWithOrderBy() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("csd", "csd"));
        productDao.save(createProduct("bsd", "bsd"));

        // when
        SearchResult result = productDao.find().addOrder(SearchOrders.desc("name"))
                .setProjection(SearchProjections.field("name")).list();

        // then
        assertEquals(3, result.getTotalNumberOfEntities());
        assertEquals("csd", result.getEntities().get(0).getField("0"));
        assertEquals("bsd", result.getEntities().get(1).getField("0"));
        assertEquals("asd", result.getEntities().get(2).getField("0"));
    }

    @Test
    public void shouldFindByQueryWithDistinct() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("bsd", "bsd"));

        // when
        SearchResult result = productDao.find().addOrder(SearchOrders.desc("name"))
                .setProjection(SearchProjections.distinct(SearchProjections.field("name"))).list();

        // then
        assertEquals(2, result.getTotalNumberOfEntities());
        assertEquals("bsd", result.getEntities().get(0).getField("0"));
        assertEquals("asd", result.getEntities().get(1).getField("0"));
    }

    @Test
    public void shouldCountAllEntities() {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("bsd", "bsd"));
        productDao.save(createProduct("csd", "csd"));

        // when
        long count = productDao.count();

        // then
        assertEquals(3L, count);
    }

    @Test
    public void shouldCountRestrictedEntities() {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("asd1", "asd1"));
        productDao.save(createProduct("bsd", "bsd"));

        // when
        long count = productDao.count(SearchRestrictions.like("name", "asd", SearchRestrictions.SearchMatchMode.START));

        // then
        assertEquals(2L, count);
    }

}
