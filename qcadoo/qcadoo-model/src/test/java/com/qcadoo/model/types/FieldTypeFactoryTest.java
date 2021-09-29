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
package com.qcadoo.model.types;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.EnumeratedType;
import com.qcadoo.model.api.types.FieldType;
import com.qcadoo.model.api.types.ManyToManyType;
import com.qcadoo.model.internal.DataAccessTest;
import com.qcadoo.model.internal.DefaultEntity;
import com.qcadoo.model.internal.FieldDefinitionImpl;
import com.qcadoo.model.internal.api.ValueAndError;
import com.qcadoo.model.internal.types.BelongsToEntityType;
import com.qcadoo.model.internal.types.BooleanType;
import com.qcadoo.model.internal.types.DateTimeType;
import com.qcadoo.model.internal.types.DateType;
import com.qcadoo.model.internal.types.DecimalType;
import com.qcadoo.model.internal.types.DictionaryType;
import com.qcadoo.model.internal.types.EnumType;
import com.qcadoo.model.internal.types.IntegerType;
import com.qcadoo.model.internal.types.ManyToManyEntitiesType;
import com.qcadoo.model.internal.types.PasswordType;
import com.qcadoo.model.internal.types.PriorityType;
import com.qcadoo.model.internal.types.StringType;
import com.qcadoo.model.internal.types.TextType;

public class FieldTypeFactoryTest extends DataAccessTest {

    private final FieldDefinition fieldDefinition = new FieldDefinitionImpl(null, "aa");

    @Test
    public void shouldReturnEnumType() throws Exception {
        // given
        TranslationService translationService = mock(TranslationService.class);
        given(translationService.translate("path.value.val1", Locale.ENGLISH)).willReturn("i18nVal1");
        given(translationService.translate("path.value.val2", Locale.ENGLISH)).willReturn("i18nVal2");
        given(translationService.translate("path.value.val3", Locale.ENGLISH)).willReturn("i18nVal3");

        // when
        EnumeratedType fieldType = new EnumType(translationService, "path", true, "val1", "val2", "val3");

        // then
        assertEquals(fieldType.values(Locale.ENGLISH).keySet(), Sets.newHashSet("val1", "val2", "val3"));
        assertEquals(Lists.newArrayList(fieldType.values(Locale.ENGLISH).values()), Lists.newArrayList("i18nVal1", "i18nVal2", "i18nVal3"));
        assertEquals(String.class, fieldType.getType());

        ValueAndError valueAndError1 = fieldType.toObject(fieldDefinition, "val1");
        ValueAndError valueAndError2 = fieldType.toObject(fieldDefinition, "val4");

        assertTrue(valueAndError1.isValid());
        assertFalse(valueAndError2.isValid());
        assertNotNull(valueAndError1.getValue());
        assertNull(valueAndError2.getValue());
        assertEquals("qcadooView.validate.field.error.invalidDictionaryItem", valueAndError2.getMessage());
        assertEquals("[val1, val2, val3]", valueAndError2.getArgs()[0]);
    }

    @Test
    public void shouldReturnDictionaryType() throws Exception {
        // given
        DictionaryService dictionaryService = mock(DictionaryService.class);
        given(dictionaryService.getValues("dictionary", Locale.ENGLISH)).willReturn(
                ImmutableMap.of("val1", "val1", "val2", "val2", "val3", "val3"));
        given(dictionaryService.getKeys("dictionary")).willReturn(Lists.newArrayList("val1", "val2", "val3"));

        // when
        EnumeratedType fieldType = new DictionaryType("dictionary", dictionaryService, true);

        // then
        assertEquals(fieldType.values(Locale.ENGLISH).keySet(), Sets.newHashSet("val1", "val2", "val3"));
        assertEquals(Lists.newArrayList(fieldType.values(Locale.ENGLISH).values()), Lists.newArrayList("val1", "val2", "val3"));
        assertEquals(String.class, fieldType.getType());

        ValueAndError valueAndError1 = fieldType.toObject(fieldDefinition, "val1");
        ValueAndError valueAndError2 = fieldType.toObject(fieldDefinition, "val4");
        assertNotNull(valueAndError1.getValue());
        assertNull(valueAndError2.getValue());
        assertEquals("qcadooView.validate.field.error.invalidDictionaryItem", valueAndError2.getMessage());
        assertEquals("[val1, val2, val3]", valueAndError2.getArgs()[0]);
    }

    @Test
    public void shouldReturnBooleanType() throws Exception {
        // when
        FieldType fieldType = new BooleanType();

        // then
        assertEquals(Boolean.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, false).isValid());
    }

    @Test
    public void shouldReturnDateType() throws Exception {
        // when
        FieldType fieldType = new DateType();

        // then
        assertEquals(Date.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, new Date()).isValid());
    }

    @Test
    public void shouldReturnDateTimeType() throws Exception {
        // when
        FieldType fieldType = new DateTimeType();

        // then
        assertEquals(Date.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, new Date()).isValid());
    }

    @Test
    public void shouldReturnDecimalType() throws Exception {
        // when
        FieldType fieldType = new DecimalType();

        // then
        assertEquals(BigDecimal.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(1.21)).isValid());
        assertTrue(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(1)).isValid());
        assertTrue(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(1)).isValid());
        assertTrue(fieldType.toObject(fieldDefinition, BigDecimal.valueOf(1234567)).isValid());
    }

    @Test
    public void shouldReturnIntegerType() throws Exception {
        // when
        FieldType fieldType = new IntegerType();

        // then
        assertEquals(Integer.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, 1).isValid());
        assertTrue(fieldType.toObject(fieldDefinition, 1234567890).isValid());
    }

    @Test
    public void shouldReturnStringType() throws Exception {
        // when
        FieldType fieldType = new StringType();

        // then
        assertEquals(String.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, "test").isValid());
        assertTrue(fieldType.toObject(fieldDefinition, StringUtils.repeat("a", 255)).isValid());
        assertTrue(fieldType.toObject(fieldDefinition, StringUtils.repeat("a", 300)).isValid());
    }

    @Test
    public void shouldReturnTextType() throws Exception {
        // when
        FieldType fieldType = new TextType();

        // then
        assertEquals(String.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, "test").isValid());
        assertTrue(fieldType.toObject(fieldDefinition, StringUtils.repeat("a", 2048)).isValid());
        assertTrue(fieldType.toObject(fieldDefinition, StringUtils.repeat("a", 2049)).isValid());
    }

    @Test
    public void shouldReturnBelongToType() throws Exception {
        // when
        FieldType fieldType = new BelongsToEntityType("parent", "entity", dataDefinitionService, false, true);

        // then
        assertEquals(Object.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, new DefaultEntity(dataDefinition)).isValid());
    }

    @Test
    public void shouldReturnManyToManyType() throws Exception {
        // when
        FieldType fieldType = new ManyToManyEntitiesType("parent", "entity", "joinFieldName", ManyToManyType.Cascade.NULLIFY,
                true, false, dataDefinitionService);

        // then
        assertEquals(Set.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, new DefaultEntity(dataDefinition)).isValid());
    }

    @Test
    public void shouldReturnPasswordType() throws Exception {
        // when
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        FieldType fieldType = new PasswordType(passwordEncoder);

        // then
        assertEquals(String.class, fieldType.getType());
    }

    @Test
    public void shouldReturnPriorityType() throws Exception {
        // given
        FieldDefinition fieldDefinition = new FieldDefinitionImpl(null, "aaa");

        // when
        FieldType fieldType = new PriorityType(fieldDefinition);

        // then
        assertEquals(Integer.class, fieldType.getType());
        assertTrue(fieldType.toObject(fieldDefinition, 1).isValid());
        assertEquals(fieldDefinition, ((PriorityType) fieldType).getScopeFieldDefinition());
    }
}
