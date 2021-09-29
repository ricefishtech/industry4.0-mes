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
package com.qcadoo.customTranslation.internal.aop;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.customTranslation.api.CustomTranslationResolver;
import com.qcadoo.customTranslation.constants.CustomTranslationContants;
import com.qcadoo.plugin.api.PluginStateResolver;

@Ignore
public class TranslationServiceOverrideUtilTest {

    private TranslationServiceOverrideUtil translationServiceOverrideUtil;

    @Mock
    private PluginStateResolver pluginStateResolver;

    @Mock
    private CustomTranslationResolver customTranslationResolver;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        translationServiceOverrideUtil = new TranslationServiceOverrideUtil();

        ReflectionTestUtils.setField(translationServiceOverrideUtil, "pluginStateResolver", pluginStateResolver);
        ReflectionTestUtils.setField(translationServiceOverrideUtil, "customTranslationResolver", customTranslationResolver);
    }

    @Test
    public void shouldReturnTrueWhenShouldOverrideTranslation() {
        // given
        String key = "key";
        Locale locale = new Locale("pl");

        ReflectionTestUtils.setField(translationServiceOverrideUtil, "useCustomTranslations", true);

        given(pluginStateResolver.isEnabled(CustomTranslationContants.PLUGIN_IDENTIFIER)).willReturn(true);
        given(customTranslationResolver.isCustomTranslationActive(key, locale)).willReturn(true);

        // when
        boolean result = translationServiceOverrideUtil.shouldOverrideTranslation(key, locale);

        // then
        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenShouldOverrideTranslation() {
        // given
        String key = "key";
        Locale locale = new Locale("pl");

        ReflectionTestUtils.setField(translationServiceOverrideUtil, "useCustomTranslations", false);

        given(pluginStateResolver.isEnabled(CustomTranslationContants.PLUGIN_IDENTIFIER)).willReturn(false);
        given(customTranslationResolver.isCustomTranslationActive(key, locale)).willReturn(false);

        // when
        boolean result = translationServiceOverrideUtil.shouldOverrideTranslation(key, locale);

        // then
        assertFalse(result);
    }

    @Test
    public void shouldReturnNullWhenGetCustomTranslationIfCustomTranslationIsNull() {
        // given
        String key = "key";
        Locale locale = new Locale("pl");
        String[] args = null;

        given(customTranslationResolver.getCustomTranslation(key, locale, args)).willReturn(null);

        // when
        String result = translationServiceOverrideUtil.getCustomTranslation(key, locale, args);

        // then
        assertEquals(null, result);
    }

    @Test
    public void shouldReturnCustomTranslationWhenGetCustomTranslation() {
        // given
        String key = "key";
        Locale locale = new Locale("pl");
        String[] args = null;

        String translation = "translation";

        given(customTranslationResolver.getCustomTranslation(key, locale, args)).willReturn(translation);

        // when
        String result = translationServiceOverrideUtil.getCustomTranslation(key, locale, args);

        // then
        assertEquals(translation, result);
    }

}
