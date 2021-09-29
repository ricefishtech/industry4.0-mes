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
package com.qcadoo.model.validators;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.beans.sample.CustomEntityService;
import com.qcadoo.model.beans.sample.SampleSimpleDatabaseObject;
import com.qcadoo.model.internal.DataAccessTest;
import com.qcadoo.model.internal.DefaultEntity;
import com.qcadoo.model.internal.hooks.EntityHookDefinitionImpl;
import com.qcadoo.model.internal.hooks.FieldHookDefinitionImpl;
import com.qcadoo.model.internal.validators.CustomEntityValidator;
import com.qcadoo.model.internal.validators.CustomValidator;
import com.qcadoo.model.internal.validators.LengthValidator;
import com.qcadoo.model.internal.validators.RangeValidator;
import com.qcadoo.model.internal.validators.RegexValidator;
import com.qcadoo.model.internal.validators.RequiredValidator;
import com.qcadoo.model.internal.validators.ScaleValidator;
import com.qcadoo.model.internal.validators.UniqueValidator;
import com.qcadoo.model.internal.validators.UnscaledValueValidator;
import com.qcadoo.plugin.api.PluginStateResolver;
import com.qcadoo.plugin.internal.PluginUtilsService;

public class ValidatorTest extends DataAccessTest {

    @Before
    public void init() {
        given(applicationContext.getBean(CustomEntityService.class)).willReturn(new CustomEntityService());
    }

    @Test
    public void shouldHasNoErrorsIfAllFieldAreNotRequired() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", null);
        entity.setField("age", null);

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
        assertTrue(entity.getErrors().isEmpty());
        assertTrue(entity.getGlobalErrors().isEmpty());
    }

    @Test
    public void shouldHasErrorMessage() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", "");

        fieldDefinitionAge.withValidator(new RequiredValidator());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
        assertEquals(1, entity.getErrors().size());
        assertEquals("qcadooView.validate.field.error.missing", entity.getError("age").getMessage());
        assertEquals(0, entity.getGlobalErrors().size());
    }

    @Test
    public void shouldHasCustomErrorMessage() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", "");

        RequiredValidator requiredValidator = new RequiredValidator();
        requiredValidator.setErrorMessage("missing age");

        fieldDefinitionAge.withValidator(requiredValidator);

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
        assertEquals(1, entity.getErrors().size());
        assertEquals("missing age", entity.getError("age").getMessage());
        assertEquals(0, entity.getGlobalErrors().size());
    }

    @Test
    public void shouldBeRequiredIfHasRequiredValidator() throws Exception {
        // given
        fieldDefinitionName.withValidator(new RequiredValidator());

        // then
        assertTrue(fieldDefinitionName.isRequired());
    }

    @Test
    public void shouldNotBeRequiredIfDoesNotHasRequiredValidator() throws Exception {
        // then
        assertFalse(fieldDefinitionName.isRequired());
    }

    @Test
    public void shouldHasErrorIfIntegerTypeIsWrong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", "21w");

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorIfBigDecimalTypeIsWrong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", "221.2w");

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorIfDateTypeIsWrong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", "2010-01-a");

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorIfBooleanTypeIsWrong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", "a");

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldBeUniqueIfHasUniqueValidator() throws Exception {
        // given
        fieldDefinitionName.withValidator(new UniqueValidator());

        // then
        assertTrue(fieldDefinitionName.isUnique());
    }

    @Test
    public void shouldNotBeUniqueIfDoesNotHasUniqueValidator() throws Exception {
        // then
        assertFalse(fieldDefinitionName.isUnique());
    }

    @Test
    public void shouldHasErrorsIfRequiredFieldsAreNotSet() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "");
        entity.setField("age", null);

        fieldDefinitionName.withValidator(new RequiredValidator());
        fieldDefinitionAge.withValidator(new RequiredValidator());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfStringValueIsTooLong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "qwerty");

        fieldDefinitionName.withValidator(new LengthValidator(null, null, 5));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfStringValueIsOutsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "ddd");

        fieldDefinitionName.withValidator(new RangeValidator("a", "c", true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfStringValueIsInsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "bbb");

        fieldDefinitionName.withValidator(new RangeValidator("a", "c", true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfIntegerValueIsOutsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", "11");

        fieldDefinitionAge.withValidator(new RangeValidator(null, 10, true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfIntegerValueIsInsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", 5);

        fieldDefinitionAge.withValidator(new RangeValidator(4, null, true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalValueIsOutsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", "31.22");

        fieldDefinitionMoney.withValidator(new RangeValidator(40, 50, true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfBigDecimalValueIsInsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", "31.22");

        fieldDefinitionMoney.withValidator(new RangeValidator(30, 40, true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfDateValueIsOutsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("birthDate", "2010-01-01");

        fieldDefinitionBirthDate.withValidator(new RangeValidator(new Date(), new Date(), true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfDateValueIsInsideTheRange() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("birthDate", "2010-01-01");

        fieldDefinitionBirthDate.withValidator(new RangeValidator(null, new Date(), true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoCheckRangeOfBoolean() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("retired", "false");

        fieldDefinitionRetired.withValidator(new RangeValidator(true, true, true));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfIntegerValueIsTooLong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", 123456);

        fieldDefinitionAge.withValidator(new UnscaledValueValidator(null, null, 5));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldIgnoreUnscaledValueValidatorForStringValue() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "Qcadoo Framework RLZ!");

        fieldDefinitionName.withValidator(new UnscaledValueValidator(null, 1, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldIgnoreScaleValidatorForStringValue() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "Qcadoo Framework RLZ!");

        fieldDefinitionName.withValidator(new ScaleValidator(null, 1, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldIgnoreLengthValidatorForBigDecimalValue() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", new BigDecimal("123.456"));

        fieldDefinitionMoney.withValidator(new LengthValidator(null, 1, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldIgnoreLengthValidatorForIntegerValue() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", new Integer("123456"));

        fieldDefinitionAge.withValidator(new LengthValidator(null, 1, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalPresicionAndScaleAreTooLong() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", new BigDecimal("123.456"));

        fieldDefinitionMoney.withValidator(new ScaleValidator(null, null, 2)).withValidator(
                new UnscaledValueValidator(null, null, 4));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalUnscaledValueAreTooShort() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", new BigDecimal("123.45"));

        fieldDefinitionMoney.withValidator(new UnscaledValueValidator(5, null, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfIntegerUnscaledValueAreTooShort() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", new Integer("123"));

        fieldDefinitionAge.withValidator(new UnscaledValueValidator(5, null, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalScaleAreTooShort() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", new BigDecimal("123.45"));

        fieldDefinitionMoney.withValidator(new ScaleValidator(3, null, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalScaleIsNotEqual() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", new BigDecimal("123.45"));

        fieldDefinitionMoney.withValidator(new ScaleValidator(null, 4, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfIntegerUnscaledValueIsNotEqual() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", new Integer("123"));

        fieldDefinitionAge.withValidator(new UnscaledValueValidator(null, 4, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfBigDecimalUnscaledValueIsNotEqual() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", new BigDecimal("123.45"));

        fieldDefinitionMoney.withValidator(new UnscaledValueValidator(null, 4, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfBigDecimalValuePresicionAndScaleIsOk() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("money", new BigDecimal("123.4"));

        fieldDefinitionMoney.withValidator(new UnscaledValueValidator(null, null, 3)).withValidator(
                new ScaleValidator(null, null, 1));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfStringLengthIsNotEqual() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "Qcadoo Framework RLZ!");

        fieldDefinitionName.withValidator(new LengthValidator(null, 4, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfStringIsTooShort() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "Qcadoo Framework RLZ!");

        fieldDefinitionName.withValidator(new LengthValidator(50, null, null));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoCheckLenghtOfBoolean() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("retired", false);

        fieldDefinitionRetired.withValidator(new LengthValidator(null, null, 0));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoCheckLenghtOfDate() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("birthDate", "2010-01-01");

        fieldDefinitionBirthDate.withValidator(new LengthValidator(null, null, 0));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfStringValueLenghtIsOk() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "qwert");

        fieldDefinitionName.withValidator(new LengthValidator(null, null, 5));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfFieldIsNotDuplicated() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "not existed");

        given(criteria.uniqueResult()).willReturn(0);

        fieldDefinitionName.withValidator(new UniqueValidator());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorsIfUpdatedFieldIsNotDuplicated() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", "not existed");

        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);

        given(session.get(SampleSimpleDatabaseObject.class, 1L)).willReturn(databaseObject);
        given(criteria.uniqueResult()).willReturn(0);

        fieldDefinitionName.withValidator(new UniqueValidator());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void shouldHasErrorsIfFieldIsDuplicated() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "existed");

        given(hibernateService.getTotalNumberOfEntities(any(Criteria.class))).willReturn(1);
        given(hibernateService.list(any(Criteria.class))).willReturn((List) Collections.singletonList(entity));

        fieldDefinitionName.withValidator(new UniqueValidator());

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasNoErrorIfCustomValidatorReturnsTrue() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "qwerty");

        fieldDefinitionName.withValidator(new CustomValidator(new FieldHookDefinitionImpl(CustomEntityService.class.getName(),
                "isEqualToQwerty", PLUGIN_IDENTIFIER, applicationContext)));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHaveErrorIfCustomValidatorReturnsFalse() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "qwert");

        fieldDefinitionName.withValidator(new CustomValidator(new FieldHookDefinitionImpl(CustomEntityService.class.getName(),
                "isEqualToQwerty", PLUGIN_IDENTIFIER, applicationContext)));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
        assertEquals(1, entity.getErrors().size());
        assertEquals("qcadooView.validate.field.error.custom", entity.getError("name").getMessage());
        assertEquals(0, entity.getGlobalErrors().size());
    }

    @Test
    public void shouldHasNoErrorIfCustomEntityValidatorReturnsTrue() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "Mr T");
        entity.setField("age", "18");

        dataDefinition.addValidatorHook(new CustomEntityValidator(new EntityHookDefinitionImpl(CustomEntityService.class
                .getName(), "hasAge18AndNameMrT", PLUGIN_IDENTIFIER, applicationContext)));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public void shouldHaveErrorIfCustomEntityValidatorReturnsFalse() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "Mr");
        entity.setField("age", "18");

        dataDefinition.addValidatorHook(new CustomEntityValidator(new EntityHookDefinitionImpl(CustomEntityService.class
                .getName(), "hasAge18AndNameMrT", PLUGIN_IDENTIFIER, applicationContext)));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHasErrorsIfStringNotMatchExpression() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "Qcadoo Framework RLZ!");

        fieldDefinitionName.withValidator(new RegexValidator(".*MES.*"));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    @Test
    public void shouldHaveNoErrorsIfStringMatchExpression() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "Qcadoo Framework RLZ!");

        fieldDefinitionName.withValidator(new RegexValidator("^Qcadoo.*"));

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session).save(any(SampleSimpleDatabaseObject.class));
        assertTrue(entity.isValid());
    }

    @Test
    public final void shouldNotCallEntityValidatorIfSourcePluginIsNotEnabled() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        dataDefinition.addValidatorHook(new CustomEntityValidator(new EntityHookDefinitionImpl(CustomEntityService.class
                .getName(), "hasAge18AndNameMrT", PLUGIN_IDENTIFIER, applicationContext)));

        stubPluginIsEnabled(false);

        // when
        Entity savedEntity = dataDefinition.save(entity);

        // then
        assertTrue(savedEntity.isValid());
    }

    @Test
    public final void shouldCallEntityValidatorIfSourcePluginIsEnabled() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("age", 24);
        entity.setField("name", "Fantomas");

        dataDefinition.addValidatorHook(new CustomEntityValidator(new EntityHookDefinitionImpl(CustomEntityService.class
                .getName(), "hasAge18AndNameMrT", PLUGIN_IDENTIFIER, applicationContext)));

        stubPluginIsEnabled(true);

        // when
        Entity savedEntity = dataDefinition.save(entity);

        // then
        assertFalse(savedEntity.isValid());
    }

    @Test
    public final void shouldNotCallFieldValidatorIfSourcePluginIsNotEnabled() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "EverythingButNotQWERTY :)");
        fieldDefinitionName.withValidator(new CustomValidator(new FieldHookDefinitionImpl(CustomEntityService.class.getName(),
                "isEqualToQwerty", PLUGIN_IDENTIFIER, applicationContext)));

        stubPluginIsEnabled(false);

        // when
        Entity savedEntity = dataDefinition.save(entity);

        // then
        assertTrue(savedEntity.isValid());
    }

    @Test
    public final void shouldCallFieldValidatorIfSourcePluginIsEnabled() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("name", "not equals to qwerty string");

        fieldDefinitionName.withValidator(new CustomValidator(new FieldHookDefinitionImpl(CustomEntityService.class.getName(),
                "isEqualToQwerty", PLUGIN_IDENTIFIER, applicationContext)));

        stubPluginIsEnabled(true);

        // when
        entity = dataDefinition.save(entity);

        // then
        verify(session, never()).save(any(SampleSimpleDatabaseObject.class));
        assertFalse(entity.isValid());
    }

    private void stubPluginIsEnabled(final boolean isEnabled) {
        PluginStateResolver pluginStateResolver = mock(PluginStateResolver.class);
        PluginUtilsService pluginUtil = new PluginUtilsService(pluginStateResolver);
        pluginUtil.init();
        given(pluginStateResolver.isEnabled(PLUGIN_IDENTIFIER)).willReturn(isEnabled);
        given(pluginStateResolver.isEnabledOrEnabling(PLUGIN_IDENTIFIER)).willReturn(isEnabled);
    }

}
