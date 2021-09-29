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
package com.qcadoo.view.internal.components.select;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.view.internal.ComponentOption;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Maps;
import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.internal.types.DictionaryType;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinition;

public class SelectComponentStateTest {

    private static final String DICTIONARY_NAME = "dictionary";

    private static final String VALUES = "values";

    private static final String KEY = "key";

    private SelectComponentState componentState;

    @Mock
    private DictionaryService dictionaryService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        DictionaryType dictionaryType = new DictionaryType(DICTIONARY_NAME, dictionaryService, false);

        TranslationService translationService = mock(TranslationService.class);

        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        when(fieldDefinition.getType()).thenReturn(dictionaryType);
        when(fieldDefinition.isRequired()).thenReturn(true);
        when(fieldDefinition.getDefaultValue()).thenReturn("asd");

        ComponentDefinition definition = new ComponentDefinition();
        definition.setName("selectComponent");
        definition.setViewDefinition(mock(InternalViewDefinition.class));
        SelectComponentPattern pattern = new SelectComponentPattern(definition);

        setField(pattern, "fieldDefinition", fieldDefinition);
        setField(pattern, "translationService", translationService);
        componentState = new SelectComponentState(pattern, Lists.newArrayList());
        setField(componentState, "locale", Locale.ENGLISH);
        setField(pattern, "defaultRequired", true);
    }

    private void stubValues(String... values) throws JSONException {
        Map<String, String> array = Maps.newLinkedHashMap();
        for (int i = 0; i < values.length; ++i) {
            array.put(values[i], values[i]);
        }
        when(dictionaryService.getValues(DICTIONARY_NAME, Locale.ENGLISH)).thenReturn(array);
    }

    @Test
    public void shouldNotAddAnotherValue() throws JSONException {
        // given

        stubValues("aaaa", "bbbb", "cccc", "dddd");
        componentState.setFieldValue("cccc");

        // when
        JSONObject content = componentState.renderContent();

        // then
        assertNotNull(content.getJSONArray(VALUES));
        assertEquals(4, content.getJSONArray(VALUES).length());
        assertEquals("aaaa", content.getJSONArray(VALUES).getJSONObject(0).getString(KEY));
        assertEquals("bbbb", content.getJSONArray(VALUES).getJSONObject(1).getString(KEY));
        assertEquals("cccc", content.getJSONArray(VALUES).getJSONObject(2).getString(KEY));
        assertEquals("dddd", content.getJSONArray(VALUES).getJSONObject(3).getString(KEY));
    }

    @Test
    public void shouldAddDeactivatedValueAtTheBeginning() throws JSONException {
        // given

        stubValues("bbbb", "cccc", "dddd", "eeee");
        componentState.setFieldValue("aaaa");

        // when
        JSONObject content = componentState.renderContent();

        // then
        assertNotNull(content.getJSONArray(VALUES));
        assertEquals(5, content.getJSONArray(VALUES).length());
        assertEquals("aaaa", content.getJSONArray(VALUES).getJSONObject(0).getString(KEY));
        assertEquals("bbbb", content.getJSONArray(VALUES).getJSONObject(1).getString(KEY));
        assertEquals("cccc", content.getJSONArray(VALUES).getJSONObject(2).getString(KEY));
        assertEquals("dddd", content.getJSONArray(VALUES).getJSONObject(3).getString(KEY));
        assertEquals("eeee", content.getJSONArray(VALUES).getJSONObject(4).getString(KEY));
    }

    @Test
    public void shouldAddDeactivatedValueToTheEnd() throws JSONException {
        // given

        stubValues("aaaa", "bbbb", "cccc", "dddd");
        componentState.setFieldValue("eeee");

        // when
        JSONObject content = componentState.renderContent();

        // then
        assertNotNull(content.getJSONArray(VALUES));
        assertEquals(5, content.getJSONArray(VALUES).length());
        assertEquals("aaaa", content.getJSONArray(VALUES).getJSONObject(0).getString(KEY));
        assertEquals("bbbb", content.getJSONArray(VALUES).getJSONObject(1).getString(KEY));
        assertEquals("cccc", content.getJSONArray(VALUES).getJSONObject(2).getString(KEY));
        assertEquals("dddd", content.getJSONArray(VALUES).getJSONObject(3).getString(KEY));
        assertEquals("eeee", content.getJSONArray(VALUES).getJSONObject(4).getString(KEY));
    }

    @Test
    public void shouldAddDeactivatedValueInTheMiddle() throws JSONException {
        // given

        stubValues("aaaa", "bbbb", "dddd", "eeee");
        componentState.setFieldValue("cccc");

        // when
        JSONObject content = componentState.renderContent();

        // then
        assertNotNull(content.getJSONArray("values"));
        assertEquals(5, content.getJSONArray("values").length());
        assertEquals("aaaa", content.getJSONArray(VALUES).getJSONObject(0).getString(KEY));
        assertEquals("bbbb", content.getJSONArray(VALUES).getJSONObject(1).getString(KEY));
        assertEquals("cccc", content.getJSONArray(VALUES).getJSONObject(2).getString(KEY));
        assertEquals("dddd", content.getJSONArray(VALUES).getJSONObject(3).getString(KEY));
        assertEquals("eeee", content.getJSONArray(VALUES).getJSONObject(4).getString(KEY));
    }

    private void stubValuesOption(String optionValue) throws JSONException {
        SelectComponentPattern pattern = (SelectComponentPattern)getField(componentState, "selectComponentPattern");

        ComponentOption option = new ComponentOption("values", Collections.singletonMap("value", optionValue));

        pattern.addOption(option);
        pattern.initializeComponent();
    }

    @Test
    public void shouldLoadValuesFromOption() throws JSONException {
        // given
        SelectComponentPattern pattern = (SelectComponentPattern)getField(componentState, "selectComponentPattern");
        setField(pattern, "fieldDefinition", null);
        stubValuesOption("yes,no");

        // when
        JSONObject content = componentState.renderContent();
        // then
        assertNotNull(content.getJSONArray("values"));
        assertEquals(2, content.getJSONArray("values").length());
        assertEquals("yes", content.getJSONArray(VALUES).getJSONObject(0).getString(KEY));
        assertEquals("no", content.getJSONArray(VALUES).getJSONObject(1).getString(KEY));
    }

    @Test
    public void shouldLoadValuesFromOptionAlsoIfOptionValueHasWhiteChars() throws JSONException {
        // given
        SelectComponentPattern pattern = (SelectComponentPattern)getField(componentState, "selectComponentPattern");
        setField(pattern, "fieldDefinition", null);
        stubValuesOption(" yes , no ,");

        // when
        JSONObject content = componentState.renderContent();
        // then
        assertNotNull(content.getJSONArray("values"));
        assertEquals(2, content.getJSONArray("values").length());
        assertEquals("yes", content.getJSONArray(VALUES).getJSONObject(0).getString(KEY));
        assertEquals("no", content.getJSONArray(VALUES).getJSONObject(1).getString(KEY));
    }

    @Test
    public void shouldNotLoadValuesFromOption() throws JSONException {
        // given
        // base on fact that fieldDefinition is set in select component by init method
        stubValuesOption("yes,no");

        // when
        JSONObject content = componentState.renderContent();
        // then
        assertNotNull(content.getJSONArray("values"));
        assertEquals(0, content.getJSONArray("values").length());
    }
}
