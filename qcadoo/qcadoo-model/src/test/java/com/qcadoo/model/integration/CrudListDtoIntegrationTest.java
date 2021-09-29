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

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.integration.VerifyHooks.HookType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CrudListDtoIntegrationTest extends IntegrationTest {

    @Test
    public void shouldCopyEntity() throws Exception {
        // given
        DataDefinition productListDtoDataDefinition = getProductListDtoDataDefinition();
        DataDefinition productDataDefinition = getProductDataDefinition();

        Entity product = productDataDefinition.save(createProduct("asd", "def"));
        Entity productListDto = productListDtoDataDefinition.get(product.getId());

        verifyHooks.clear();

        // when
        Entity productListDtoCopy = productListDtoDataDefinition.copy(product.getId()).get(0);

        // then
        assertEquals(productListDto.getField("name"), productListDtoCopy.getField("name"));
        assertEquals(productListDto.getField("number") + "(1)", productListDtoCopy.getField("number"));
        assertNotNull(productListDtoCopy.getId());
        assertFalse(productListDtoCopy.getId().equals(productListDto.getId()));
        assertTrue(productListDtoCopy.isValid());

        // copy return master model object!
        //assertEquals(productListDto.getDataDefinition().getName(), productListDtoCopy.getDataDefinition().getName());
        assertEquals(productDataDefinition.getName(), productListDtoCopy.getDataDefinition().getName());

        assertEquals(productListDto.getDataDefinition().getPluginIdentifier(), productListDtoCopy.getDataDefinition().getPluginIdentifier());

        List<Map<String, Object>> result = jdbcTemplate.queryForList("select * from " + TABLE_NAME_PRODUCT + " order by id asc");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(product.getId(), result.get(0).get("id"));
        assertEquals(productListDtoCopy.getId(), result.get(1).get("id"));
        assertEquals("asd", result.get(0).get("name"));
        assertEquals("asd", result.get(1).get("name"));
        assertEquals("def", result.get(0).get("number"));
        assertEquals("def(1)", result.get(1).get("number"));

        assertEquals(1, verifyHooks.getNumOfInvocations(HookType.SAVE));
        assertEquals(0, verifyHooks.getNumOfInvocations(HookType.CREATE));
        assertEquals(1, verifyHooks.getNumOfInvocations(HookType.COPY));
        assertEquals(0, verifyHooks.getNumOfInvocations(HookType.UPDATE));
        assertEquals(0, verifyHooks.getNumOfInvocations(HookType.DELETE));
    }

    @Test
    public void shouldHardDeleteEntity() throws Exception {
        // given
        DataDefinition productDataDefinition = getProductDataDefinition();
        DataDefinition productListDtoDataDefinition = getProductListDtoDataDefinition();

        Entity product = productDataDefinition.save(createProduct("asd", "asd"));
        Entity productListDto = productListDtoDataDefinition.get(product.getId());

        verifyHooks.clear();

        // when
        productListDtoDataDefinition.delete(productListDto.getId());

        // then
        int total = jdbcTemplate.queryForInt("select count(*) from " + TABLE_NAME_PRODUCT);

        assertEquals(0, total);

        assertEquals(0, verifyHooks.getNumOfInvocations(HookType.SAVE));
        assertEquals(0, verifyHooks.getNumOfInvocations(HookType.CREATE));
        assertEquals(0, verifyHooks.getNumOfInvocations(HookType.COPY));
        assertEquals(0, verifyHooks.getNumOfInvocations(HookType.UPDATE));
        assertEquals(0, verifyHooks.getNumOfInvocations(HookType.DELETE));
    }

    private DataDefinition getProductListDtoDataDefinition() {
        return dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, "productListDto");
    }

    private DataDefinition getProductDataDefinition() {
        return dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
    }

    private Entity createProductListDto(final String name, final String number) {
        Entity entity = getProductListDtoDataDefinition().create();
        entity.setField("name", name);
        entity.setField("number", number);
        return entity;
    }

}
