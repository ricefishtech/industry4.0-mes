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

import org.junit.Test;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.internal.types.BelongsToEntityType;
import com.qcadoo.model.internal.types.DecimalType;
import com.qcadoo.model.internal.types.IntegerType;
import com.qcadoo.model.internal.types.StringType;

public class HqlIntegrationTest extends IntegrationTest {

    @Test
    public void shouldFindByEmptyQuery() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));

        // when
        Entity product = productDao.find("").uniqueResult();

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
        productDao.find("").uniqueResult();
    }

    @Test
    public void shouldFindAllByQuery() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("asd2", "asd2"));

        // when
        SearchResult result = productDao.find("").list();

        // then
        assertEquals(2, result.getTotalNumberOfEntities());
        assertEquals(2, result.getEntities().size());
    }

    @Test
    public void shouldFindByQueryWithWhere() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));

        // when
        Entity product = productDao.find("where name like 'asd'").uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getStringField("name"), product.getStringField("name"));
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithFrom() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));

        // when
        Entity product = productDao.find("from #products_product").uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getStringField("name"), product.getStringField("name"));
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithWhereAndFrom() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));

        // when
        Entity product = productDao.find("from #products_product p where p.name = 'asd'").uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getStringField("name"), product.getStringField("name"));
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithParameters() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));

        // when
        Entity product = productDao.find("where name = :name").setString("name", "asd").uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getStringField("name"), product.getStringField("name"));
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
        Entity part = partDao.find("where product = :product").setEntity("product", expectedProduct).uniqueResult();

        // then
        assertEquals(expectedPart.getId(), part.getId());
        assertEquals(expectedPart.getDataDefinition(), part.getDataDefinition());
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
        Entity product = productDao.find("from #products_product as p where exists(from p.parts)").uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithSubqueries() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        productDao.save(createProduct("qwe", "qwe"));
        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        partDao.save(createPart("name", expectedProduct));

        // when
        Entity product = productDao.find("from #products_product as p where exists(from #products_part x where x.product = p)")
                .uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithSubqueriesInSelect() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        productDao.save(createProduct("qwe", "qwe"));
        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        partDao.save(createPart("name", expectedProduct));

        // when
        Entity row = productDao
                .find("select p, (select max(x.name) from #products_part x where x.product = p) from #products_product as p where exists(from #products_part x where x.product = p)")
                .uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), row.getBelongsToField("0").getId());
        assertEquals(expectedProduct.getDataDefinition(), row.getBelongsToField("0").getDataDefinition());
        assertEquals("name", row.getStringField("1"));
    }

    @Test
    public void shouldFindByQueryWithEntityIdParameters() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));
        Entity expectedPart = partDao.save(createPart("name", expectedProduct));

        // when
        Entity part = partDao.find("where product = :product")
                .setEntity("product", PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT, expectedProduct.getId()).uniqueResult();

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
        Entity part = partDao.find("where product.id = " + expectedProduct.getId()).uniqueResult();

        // then
        assertEquals(expectedPart.getId(), part.getId());
        assertEquals(expectedPart.getDataDefinition(), part.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithBeanSelect() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity expectedProduct = productDao.save(createProduct("asd", "asd"));

        // when
        Entity product = productDao.find("select p from #products_product p").uniqueResult();

        // then
        assertEquals(expectedProduct.getId(), product.getId());
        assertEquals(expectedProduct.getDataDefinition(), product.getDataDefinition());
    }

    @Test
    public void shouldFindByQueryWithSelectAndGroupBySection() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity product1 = createProduct("asd", "asd");
        product1.setField("quantity", 1);
        productDao.save(product1);

        Entity product2 = createProduct("ase", "ase");
        product2.setField("quantity", 2);
        productDao.save(product2);

        Entity product3 = createProduct("bef", "bef");
        product3.setField("quantity", 1);
        productDao.save(product3);

        // when
        Entity avg = productDao
                .find("select avg(quantity) as quantity, substring(name,0,1) as firstLetter from #products_product where name like :name group by substring(name,0,1)")
                .setString("name", "a%").uniqueResult();

        // then
        assertEquals("a", avg.getField("firstLetter"));
        assertEquals(1.5, avg.getField("quantity"));
        assertEquals(StringType.class, avg.getDataDefinition().getField("firstLetter").getType().getClass());
        assertEquals(DecimalType.class, avg.getDataDefinition().getField("quantity").getType().getClass());
    }

    @Test
    public void shouldFindByQueryWithBelongsToField() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        Entity product = createProduct("asd", "asd");
        product.setField("quantity", 1);
        productDao.save(product);

        // when
        Entity avg = productDao.find("select p, quantity + 1 from #products_product p where p.name like :name)")
                .setString("name", "asd").uniqueResult();

        // then
        assertEquals(BelongsToEntityType.class, avg.getDataDefinition().getField("0").getType().getClass());
        assertEquals(IntegerType.class, avg.getDataDefinition().getField("1").getType().getClass());
        assertEquals(product.getId(), avg.getBelongsToField("0").getField("id"));
        assertEquals(2, avg.getField("1"));
    }

    @Test
    public void shouldFindByQueryWithOrderBy() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        productDao.save(createProduct("asd", "asd"));
        productDao.save(createProduct("csd", "csd"));
        productDao.save(createProduct("bsd", "bsd"));

        // when
        SearchResult result = productDao.find("select p.name from #products_product p order by p.name desc)").list();

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
        SearchResult result = productDao.find("select distinct p.name from #products_product p order by p.name desc)").list();

        // then
        assertEquals(2, result.getTotalNumberOfEntities());
        assertEquals("bsd", result.getEntities().get(0).getField("0"));
        assertEquals("asd", result.getEntities().get(1).getField("0"));
    }

}
