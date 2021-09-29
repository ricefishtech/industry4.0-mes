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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.plugin.api.PluginStateResolver;
import com.qcadoo.plugin.internal.PluginUtilsService;

public class FieldModuleIntegrationTest extends IntegrationTest {

    @Test
    public void shouldCallAdditinanalFieldValidators() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition machineDao = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        DataDefinition componentDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);

        pluginManager.enablePlugin(PLUGIN_MACHINES_NAME);

        Entity machine = machineDao.save(createMachine("asd"));

        Entity product = createProduct("asd", "asd");
        product = productDao.save(product);

        Entity component = createComponent("name", product, machine);
        component.setField("machineDescription", "as");

        // when
        component = componentDao.save(component);

        // then
        Assert.assertFalse(component.isValid());
    }

    @Test
    public void shouldCallAndPassAdditinanalFieldValidators() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition machineDao = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        DataDefinition componentDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);

        pluginManager.enablePlugin(PLUGIN_MACHINES_NAME);

        Entity machine = machineDao.save(createMachine("asd"));

        Entity product = createProduct("asd", "asd");
        product = productDao.save(product);

        Entity component = createComponent("name", product, machine);
        component.setField("machineDescription", "asdfghjkl");

        // when
        component = componentDao.save(component);

        // then
        Assert.assertTrue(component.isValid());
    }

    @Test
    public void shouldNotCallAdditinanalFieldValidatorsIfPluginIsSystemDisabled() throws Exception {
        // given
        DataDefinition machineDao = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        machineDao.save(createMachine("asd"));

        pluginManager.disablePlugin(PLUGIN_MACHINES_NAME);

        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition componentDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);

        Entity product = createProduct("asd", "asd");
        product = productDao.save(product);

        Entity component = createComponent("name", product, null);
        component.setField("machineDescription", "as");

        // when
        component = componentDao.save(component);

        // then
        Assert.assertTrue(component.isValid());
    }

    @Test
    public void shouldNotCallAdditinanalFieldValidatorsIfPluginIsDisabledForCurrentUser() throws Exception {
        // given
        DataDefinition machineDao = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        machineDao.save(createMachine("asd"));

        pluginManager.enablePlugin(PLUGIN_MACHINES_NAME);

        // This should be considered as an anti-pattern. Replacing static fields with mocks in an integration test suite weren't
        // my smartest idea ever..
        PluginStateResolver pluginStateResolver = mock(PluginStateResolver.class);
        PluginUtilsService pluginUtil = new PluginUtilsService(pluginStateResolver);
        pluginUtil.init();
        given(pluginStateResolver.isEnabled(PLUGIN_MACHINES_NAME)).willReturn(false);

        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition componentDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);

        Entity product = createProduct("asd", "asd");
        product = productDao.save(product);

        Entity component = createComponent("name", product, null);
        component.setField("machineDescription", "as");

        // when
        component = componentDao.save(component);

        // then
        Assert.assertTrue(component.isValid());
    }

    @Test
    public void shouldCallAndPassMaxStringLenFieldValidators() throws Exception {
        // given
        String stringWith300Characters = StringUtils.repeat("a", 300);
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);

        pluginManager.enablePlugin(PLUGIN_PRODUCTS_NAME);

        Entity product = createProduct(stringWith300Characters, "asd");

        // when
        Entity savedProduct = productDao.save(product);

        // then
        Assert.assertTrue(savedProduct.isValid());
        Assert.assertEquals(stringWith300Characters, savedProduct.getStringField("name"));
    }

    @Test
    public void shouldCallAndFailDefaultMaxStringLenFieldValidators() throws Exception {
        // given
        String stringWith300Characters = StringUtils.repeat("a", 300);

        // when & then
        Entity savedPart = performFieldValidationTestOnPart("name", stringWith300Characters, false);
        Assert.assertEquals("qcadooView.validate.field.error.invalidLength", savedPart.getError("name").getMessage());
    }

    @Test
    public void shouldCallAndPassDefaultDecimalFieldValidators() throws Exception {
        // given
        BigDecimal decimal = new BigDecimal("0.5");

        // when & then
        performFieldValidationTestOnPart("price", decimal, true);
    }

    @Test
    public void shouldCallAndFailDefaultMaxScaleValueLenFieldValidators() throws Exception {
        // given
        BigDecimal tooPreciseDecimal = new BigDecimal("0." + StringUtils.repeat("9", 20));

        // when & then
        Entity savedPart = performFieldValidationTestOnPart("price", tooPreciseDecimal, false);
        Assert.assertEquals("qcadooView.validate.field.error.invalidScale.max", savedPart.getError("price").getMessage());
    }

    @Test
    public void shouldCallAndFailDefaultMaxUnscaledValueLenFieldValidators() throws Exception {
        // given
        BigDecimal tooPreciseDecimal = new BigDecimal(StringUtils.repeat("9", 20) + ".0");

        // when & then
        Entity savedPart = performFieldValidationTestOnPart("price", tooPreciseDecimal, false);
        Assert.assertEquals("qcadooView.validate.field.error.invalidPrecision.max", savedPart.getError("price").getMessage());
    }

    @Test
    public void shouldCallAndPassDefaultIntegerFieldValidators() throws Exception {
        // given
        Integer integer = Integer.parseInt(StringUtils.repeat("1", 10));

        // when & then
        performFieldValidationTestOnPart("weight", integer, true);
    }

    @Test
    public void shouldCallAndPassDefaultTextFieldValidators() throws Exception {
        // given
        String textValue = StringUtils.repeat("a", 2048);

        // when & then
        performFieldValidationTestOnPart("description", textValue, true);
    }

    @Test
    public void shouldCallAndFailDefaultTextFieldValidators() throws Exception {
        // given
        String textValue = StringUtils.repeat("a", 2049);

        // when & then
        performFieldValidationTestOnPart("description", textValue, false);
    }

    @Test
    public void shouldCallAndPassTextFieldMaxLenValidators() throws Exception {
        // given
        String textValue = StringUtils.repeat("a", 4096);

        // when & then
        performFieldValidationTestOnPart("longDescription", textValue, true);
    }

    @Test
    public void shouldCallAndFailTextFieldMaxLenValidators() throws Exception {
        // given
        String textValue = StringUtils.repeat("a", 4097);

        // when & then
        performFieldValidationTestOnPart("longDescription", textValue, false);
    }

    @Test
    public void shouldCallAndPassDefaultPasswordFieldValidators() throws Exception {
        // given
        String textValue = StringUtils.repeat("a", 255);

        // when & then
        performFieldValidationTestOnPart("discountCode", textValue, true);
    }

    @Test
    public void shouldCallAndFailDefaultPasswordFieldValidators() throws Exception {
        // given
        String textValue = StringUtils.repeat("a", 256);

        // when & then
        performFieldValidationTestOnPart("discountCode", textValue, false);
    }

    @Test
    public void shouldCallAndPassPasswordFieldMaxLenValidators() throws Exception {
        // given
        String textValue = StringUtils.repeat("a", 512);

        // when & then
        performFieldValidationTestOnPart("longDiscountCode", textValue, true);
    }

    @Test
    public void shouldCallAndFailPasswordFieldMaxLenValidators() throws Exception {
        // given
        String textValue = StringUtils.repeat("a", 513);

        // when & then
        performFieldValidationTestOnPart("longDiscountCode", textValue, false);
    }

    private Entity performFieldValidationTestOnPart(final String fieldName, final Object fieldValue, final boolean shouldPass) {
        DataDefinition partDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PART);

        pluginManager.enablePlugin(PLUGIN_PRODUCTS_NAME);

        Entity part = createPart("somePart", null);
        part.setField(fieldName, fieldValue);

        // when
        Entity savedPart = partDao.save(part);

        // then
        Assert.assertEquals(shouldPass, savedPart.isValid());

        return savedPart;
    }

}
