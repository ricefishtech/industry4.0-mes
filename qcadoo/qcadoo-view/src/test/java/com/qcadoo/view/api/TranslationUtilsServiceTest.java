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
package com.qcadoo.view.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.utils.TranslationUtilsService;

public class TranslationUtilsServiceTest {

    private TranslationUtilsService translationUtilsService;

    private TranslationService translationService;

    private Entity item;

    private Entity category;

    private Locale locale;

    private static final String PLUGIN_IDENTIFIER = "somePlugin";

    private static final String CATEGORY_NAME = "someCategory";

    private static final String ITEM_NAME = "someItem";

    private static final String MENU_ITEM_DESC_KEY = PLUGIN_IDENTIFIER + ".menu." + CATEGORY_NAME + '.' + ITEM_NAME
            + ".description";

    private static final String TRANSLATED_MENU_ITEM_DESC = "Translated menu item description";

    private static final String MENU_CATEGORY_DESC_KEY = PLUGIN_IDENTIFIER + ".menu." + CATEGORY_NAME + ".description";

    private static final String TRANSLATED_MENU_CATEGORY_DESC = "Translated menu group description";

    @Before
    public final void init() {
        translationService = mock(TranslationService.class);
        item = mock(Entity.class);
        category = mock(Entity.class);
        locale = Locale.getDefault();

        when(item.getStringField("pluginIdentifier")).thenReturn(PLUGIN_IDENTIFIER);
        when(category.getStringField("pluginIdentifier")).thenReturn(PLUGIN_IDENTIFIER);
        when(item.getStringField("name")).thenReturn(ITEM_NAME);
        when(category.getStringField("name")).thenReturn(CATEGORY_NAME);
        when(item.getBelongsToField("category")).thenReturn(category);

        translationUtilsService = new TranslationUtilsService();
        ReflectionTestUtils.setField(translationUtilsService, "translationService", translationService);
    }

    @Test
    public final void shouldReturnDescriptionTranslationForMenuItem() throws Exception {
        // given
        Mockito.when(translationService.translate(MENU_ITEM_DESC_KEY, locale)).thenReturn(TRANSLATED_MENU_ITEM_DESC);

        // when
        String translation = translationUtilsService.getItemDescriptionTranslation(item, locale);

        // then
        Assert.assertEquals(TRANSLATED_MENU_ITEM_DESC, translation);
    }

    @Test
    public final void shouldReturnDescriptionTranslationForMenuCategory() throws Exception {
        // given
        Mockito.when(translationService.translate(MENU_CATEGORY_DESC_KEY, locale)).thenReturn(TRANSLATED_MENU_CATEGORY_DESC);

        // when
        String translation = translationUtilsService.getCategoryDescriptionTranslation(category, locale);

        // then
        Assert.assertEquals(TRANSLATED_MENU_CATEGORY_DESC, translation);
    }

    @Test
    public final void shouldReturnEmptyStringForMenuCategoryIfGetDefaultMissingMessage() throws Exception {
        // given
        Mockito.when(translationService.translate(MENU_CATEGORY_DESC_KEY, locale)).thenReturn(
                TranslationService.DEFAULT_MISSING_MESSAGE);

        // when
        String translation = translationUtilsService.getCategoryDescriptionTranslation(category, locale);

        // then
        Assert.assertTrue(translation.isEmpty());
    }

    @Test
    public final void shouldReturnEmptyStringForMenuCategoryIfGetTranslationEqualToKey() throws Exception {
        // given
        Mockito.when(translationService.translate(MENU_CATEGORY_DESC_KEY, locale)).thenReturn(MENU_CATEGORY_DESC_KEY);

        // when
        String translation = translationUtilsService.getCategoryDescriptionTranslation(category, locale);

        // then
        Assert.assertTrue(translation.isEmpty());
    }

    @Test
    public final void shouldReturnEmptyStringForMenuItemIfGetDefaultMissingMessage() throws Exception {
        // given
        Mockito.when(translationService.translate(MENU_ITEM_DESC_KEY, locale)).thenReturn(
                TranslationService.DEFAULT_MISSING_MESSAGE);

        // when
        String translation = translationUtilsService.getItemDescriptionTranslation(item, locale);

        // then
        Assert.assertTrue(translation.isEmpty());
    }

    @Test
    public final void shouldReturnEmptyStringForMenuItemIfGetTranslationEqualToKey() throws Exception {
        // given
        Mockito.when(translationService.translate(MENU_ITEM_DESC_KEY, locale)).thenReturn(MENU_ITEM_DESC_KEY);

        // when
        String translation = translationUtilsService.getItemDescriptionTranslation(item, locale);

        // then
        Assert.assertTrue(translation.isEmpty());
    }
}
