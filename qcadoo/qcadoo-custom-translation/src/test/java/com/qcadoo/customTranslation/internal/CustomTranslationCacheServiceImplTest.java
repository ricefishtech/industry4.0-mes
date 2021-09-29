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
package com.qcadoo.customTranslation.internal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.qcadoo.customTranslation.api.CustomTranslationCacheService;

@Ignore
public class CustomTranslationCacheServiceImplTest {

    private CustomTranslationCacheService customTranslationCacheService;

    @Mock
    private Map<String, Map<String, String>> customTranslations;

    @Mock
    private Map<String, String> localeAndCustomTranslation;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        customTranslationCacheService = new CustomTranslationCacheServiceImpl();
    }

    // TODO lupo fix problem with test
    @Ignore
    @Test
    public void shouldAddCustomTranslation() {
        // given
        String key = "key";
        String locale = "pl";
        String translation = "translation";

        // when
        customTranslationCacheService.addCustomTranslation(key, locale, translation);

        // then
        verify(customTranslations).put(key, localeAndCustomTranslation);
    }

    // TODO lupo fix problem with test
    @Ignore
    @Test
    public void shouldUpdateWhenUpdateCustomTranslationIfContainsKey() {
        // given
        String key = "key";
        String locale = "pl";
        String translation = "translation";

        given(customTranslations.containsKey(key)).willReturn(true);
        given(customTranslations.get(key)).willReturn(localeAndCustomTranslation);

        // when
        customTranslationCacheService.updateCustomTranslation(key, locale, translation);

        // then
        verify(customTranslations).put(key, localeAndCustomTranslation);
    }

    @Test
    public void shouldntUpdateWhenUpdateCustomTranslationIfNotContainsKey() {
        // given
        String key = "key";
        String locale = "pl";
        String translation = "translation";

        given(customTranslations.containsKey(key)).willReturn(false);

        // when
        customTranslationCacheService.updateCustomTranslation(key, locale, translation);

        // then
        verify(customTranslations, never()).put(key, localeAndCustomTranslation);
    }

    @Test
    public void shouldReturnNullWhenGetCustomTranslationIfNotContainsKey() {
        // given
        String key = "key";
        String locale = "pl";

        given(customTranslations.containsKey(key)).willReturn(false);
        // when
        String result = customTranslationCacheService.getCustomTranslation(key, locale);

        // then
        assertEquals(null, result);
    }

    @Test
    public void shouldReturnNullWhenGetCustomTranslationIfContainsKeyAndNotContainsLocale() {
        // given
        String key = "key";
        String locale = "pl";

        given(customTranslations.containsKey(key)).willReturn(true);
        given(customTranslations.get(key)).willReturn(localeAndCustomTranslation);
        given(localeAndCustomTranslation.containsKey(locale)).willReturn(false);

        // when
        String result = customTranslationCacheService.getCustomTranslation(key, locale);

        // then
        assertEquals(null, result);
    }

    // TODO lupo fix problem with test
    @Ignore
    @Test
    public void shouldReturnCustomTranslationWhenGetCustomTranslationIfContainsKeyAndContainsLocale() {
        // given
        String key = "key";
        String locale = "pl";
        String translation = "translation";

        given(customTranslations.containsKey(key)).willReturn(true);
        given(customTranslations.get(key)).willReturn(localeAndCustomTranslation);
        given(localeAndCustomTranslation.containsKey(locale)).willReturn(true);
        given(localeAndCustomTranslation.get(locale)).willReturn(translation);

        // when
        String result = customTranslationCacheService.getCustomTranslation(key, locale);

        // then
        assertEquals(translation, result);
    }

    // TODO lupo fix problem with test
    @Ignore
    @Test
    public void shouldGetCustomTranslations() {
        // given

        // when
        Map<String, Map<String, String>> result = customTranslationCacheService.getCustomTranslations();

        // then
        assertEquals(customTranslations, result);
    }

    @Test
    public void shouldReturnFalseWhenIsCustomTranslationAddedIfNotContainsKey() {
        // given
        String key = "key";

        given(customTranslations.containsKey(key)).willReturn(false);

        // when
        boolean result = customTranslationCacheService.isCustomTranslationAdded(key);

        // then
        assertFalse(result);
    }

    // TODO lupo fix problem with test
    @Ignore
    @Test
    public void shouldReturnTrueWhenIsCustomTranslationAddedIfContainsKey() {
        // given
        String key = "key";

        given(customTranslations.containsKey(key)).willReturn(false);

        // when
        boolean result = customTranslationCacheService.isCustomTranslationAdded(key);

        // then
        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenIsCustomTranslationActiveIfNotContainsKey() {
        // given
        String key = "key";
        String locale = "pl";

        given(customTranslations.containsKey(key)).willReturn(false);

        // when
        boolean result = customTranslationCacheService.isCustomTranslationActive(key, locale);

        // then
        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenIsCustomTranslationActiveIfContainsKeyAndNotContainsLocale() {
        // given
        String key = "key";
        String locale = "pl";

        given(customTranslations.containsKey(key)).willReturn(true);
        given(customTranslations.get(key)).willReturn(localeAndCustomTranslation);
        given(localeAndCustomTranslation.containsKey(locale)).willReturn(false);

        // when
        boolean result = customTranslationCacheService.isCustomTranslationActive(key, locale);

        // then
        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenIsCustomTranslationActiveIfContainsKeyAndContainsLocaleAndCustomTranslationIsNull() {
        // given
        String key = "key";
        String locale = "pl";

        given(customTranslations.containsKey(key)).willReturn(true);
        given(customTranslations.get(key)).willReturn(localeAndCustomTranslation);
        given(localeAndCustomTranslation.containsKey(locale)).willReturn(true);
        given(localeAndCustomTranslation.get(locale)).willReturn(null);

        // when
        boolean result = customTranslationCacheService.isCustomTranslationActive(key, locale);

        // then
        assertFalse(result);
    }

    // TODO lupo fix problem with test
    @Ignore
    @Test
    public void shouldReturnTrueWhenIsCustomTranslationActiveIfContainsKeyAndContainsLocaleAndCustomTranslationIsntNull() {
        // given
        String key = "key";
        String locale = "pl";
        String translation = "translation";

        given(customTranslations.containsKey(key)).willReturn(true);
        given(customTranslations.get(key)).willReturn(localeAndCustomTranslation);
        given(localeAndCustomTranslation.containsKey(locale)).willReturn(true);
        given(localeAndCustomTranslation.get(locale)).willReturn(translation);

        // when
        boolean result = customTranslationCacheService.isCustomTranslationActive(key, locale);

        // then
        assertTrue(result);
    }

}
