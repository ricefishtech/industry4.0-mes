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
package com.qcadoo.customTranslation.internal.hooks;

import static com.qcadoo.customTranslation.constants.CustomTranslationFields.ACTIVE;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.CUSTOM_TRANSLATION;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.KEY;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.LOCALE;
import static com.qcadoo.customTranslation.constants.CustomTranslationFields.PLUGIN_IDENTIFIER;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.customTranslation.api.CustomTranslationCacheService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.plugin.api.PluginStateResolver;

@Ignore
public class CustomTranslationModelHooksTest {

    private CustomTranslationModelHooks customTranslationModelHooks;

    @Mock
    private CustomTranslationCacheService customTranslationCacheService;

    @Mock
    private PluginStateResolver pluginStateResolver;

    @Mock
    private DataDefinition customTranslationDD;

    @Mock
    private Entity customTranslation;

    @Mock
    private SearchCriteriaBuilder searchCriteriaBuilder;

    @Mock
    private SearchResult searchResult;

    @Mock
    private List<Entity> customTranslations;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        customTranslationModelHooks = new CustomTranslationModelHooks();

        ReflectionTestUtils.setField(customTranslationModelHooks, "pluginStateResolver", pluginStateResolver);
        ReflectionTestUtils.setField(customTranslationModelHooks, "customTranslationCacheService", customTranslationCacheService);
    }

    @Test
    public void shouldReturnTrueWhenCheckIfCustomTranslationIsUniqueIfEntityIsntSaved() {
        // given
        given(customTranslation.getId()).willReturn(null);

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(null);
        given(customTranslation.getStringField(KEY)).willReturn(null);
        given(customTranslation.getStringField(LOCALE)).willReturn(null);

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.list()).willReturn(searchResult);
        given(searchResult.getEntities()).willReturn(customTranslations);
        given(customTranslations.isEmpty()).willReturn(true);

        // when
        boolean result = customTranslationModelHooks.checkIfCustomTranslationIsUnique(customTranslationDD, customTranslation);

        // then
        Assert.assertTrue(result);

        verify(customTranslation, never()).addError(Mockito.any(FieldDefinition.class), Mockito.anyString());
    }

    @Test
    public void shouldReturnTrueWhenCheckIfCustomTranslationIsUniqueIfEntityIsSaved() {
        // given
        given(customTranslation.getId()).willReturn(1L);

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(null);
        given(customTranslation.getStringField(KEY)).willReturn(null);
        given(customTranslation.getStringField(LOCALE)).willReturn(null);

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.list()).willReturn(searchResult);
        given(searchResult.getEntities()).willReturn(customTranslations);
        given(customTranslations.isEmpty()).willReturn(true);

        // when
        boolean result = customTranslationModelHooks.checkIfCustomTranslationIsUnique(customTranslationDD, customTranslation);

        // then
        Assert.assertTrue(result);

        verify(customTranslation, never()).addError(Mockito.any(FieldDefinition.class), Mockito.anyString());
    }

    @Test
    public void shouldReturnFalseWhenCheckIfCustomTranslationIsUniqueIfEntityIsntSaved() {
        // given
        given(customTranslation.getId()).willReturn(null);

        String pluginIdentifier = "plugin";
        String key = "key";
        String locale = "pl";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(pluginIdentifier);
        given(customTranslation.getStringField(KEY)).willReturn(key);
        given(customTranslation.getStringField(LOCALE)).willReturn(locale);

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.list()).willReturn(searchResult);
        given(searchResult.getEntities()).willReturn(customTranslations);
        given(customTranslations.isEmpty()).willReturn(false);

        // when
        boolean result = customTranslationModelHooks.checkIfCustomTranslationIsUnique(customTranslationDD, customTranslation);

        // then
        Assert.assertFalse(result);

        verify(customTranslation).addError(Mockito.any(FieldDefinition.class), Mockito.anyString());
    }

    @Test
    public void shouldReturnFalseWhenCheckIfCustomTranslationIsUniqueIfEntityIsSaved() {
        // given
        given(customTranslation.getId()).willReturn(1L);

        String pluginIdentifier = "plugin";
        String key = "key";
        String locale = "pl";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(pluginIdentifier);
        given(customTranslation.getStringField(KEY)).willReturn(key);
        given(customTranslation.getStringField(LOCALE)).willReturn(locale);

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.list()).willReturn(searchResult);
        given(searchResult.getEntities()).willReturn(customTranslations);
        given(customTranslations.isEmpty()).willReturn(false);

        // when
        boolean result = customTranslationModelHooks.checkIfCustomTranslationIsUnique(customTranslationDD, customTranslation);

        // then
        Assert.assertFalse(result);

        verify(customTranslation).addError(Mockito.any(FieldDefinition.class), Mockito.anyString());
    }

    @Test
    public void shouldntChangeActiveStateIfPluginIsntEnabled() {
        // given
        String plugin = "plugin";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(plugin);
        given(customTranslation.getStringField(CUSTOM_TRANSLATION)).willReturn(null);

        given(pluginStateResolver.isEnabled(plugin)).willReturn(false);

        // when
        customTranslationModelHooks.changeActiveState(customTranslationDD, customTranslation);

        // then
        verify(customTranslation, never()).setField(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void shouldChangeActiveStateIfPluginIsEnabledAndCustomTranslationIsNull() {
        // given
        String plugin = "plugin";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(plugin);
        given(customTranslation.getStringField(CUSTOM_TRANSLATION)).willReturn(null);

        given(pluginStateResolver.isEnabled(plugin)).willReturn(true);

        // when
        customTranslationModelHooks.changeActiveState(customTranslationDD, customTranslation);

        // then
        verify(customTranslation).setField(ACTIVE, false);
    }

    @Test
    public void shouldChangeActiveStateIfPluginIsEnabledAndCustomTranslationIsntNull() {
        // given
        String plugin = "plugin";
        String translation = "translation";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(plugin);
        given(customTranslation.getStringField(CUSTOM_TRANSLATION)).willReturn(translation);

        given(pluginStateResolver.isEnabled(plugin)).willReturn(true);

        // when
        customTranslationModelHooks.changeActiveState(customTranslationDD, customTranslation);

        // then
        verify(customTranslation).setField(ACTIVE, true);
    }

    @Test
    public void shouldUpdateCustomTranslationWhenUpdateCacheIfPluginIsEnabledAndContainsKey() {
        // given
        String plugin = "plugin";

        String key = "key";
        String locale = "locale";
        String translation = "translation";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(plugin);
        given(customTranslation.getStringField(KEY)).willReturn(key);
        given(customTranslation.getStringField(LOCALE)).willReturn(locale);
        given(customTranslation.getStringField(CUSTOM_TRANSLATION)).willReturn(translation);

        given(pluginStateResolver.isEnabled(plugin)).willReturn(true);
        given(customTranslationCacheService.isCustomTranslationAdded(key)).willReturn(true);

        // when
        customTranslationModelHooks.updateCache(customTranslationDD, customTranslation);

        // then
        verify(customTranslationCacheService, never()).addCustomTranslation(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
        verify(customTranslationCacheService).updateCustomTranslation(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
    }

    @Test
    public void shouldAddCustomTranslationWhenUpdateCacheIfPluginIsEnabledAndNotContainsKey() {
        // given
        String plugin = "plugin";

        String key = "key";
        String locale = "locale";
        String translation = "translation";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(plugin);
        given(customTranslation.getStringField(KEY)).willReturn(key);
        given(customTranslation.getStringField(LOCALE)).willReturn(locale);
        given(customTranslation.getStringField(CUSTOM_TRANSLATION)).willReturn(translation);

        given(pluginStateResolver.isEnabled(plugin)).willReturn(true);
        given(customTranslationCacheService.isCustomTranslationAdded(key)).willReturn(false);

        // when
        customTranslationModelHooks.updateCache(customTranslationDD, customTranslation);

        // then
        verify(customTranslationCacheService).addCustomTranslation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(customTranslationCacheService, never()).updateCustomTranslation(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
    }

    @Test
    public void shouldUpdateCustomTranslationWhenUpdateCacheIfPluginIsntEnabledAndContainsKey() {
        // given
        String plugin = "plugin";

        String key = "key";
        String locale = "locale";
        String translation = "translation";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(plugin);
        given(customTranslation.getStringField(KEY)).willReturn(key);
        given(customTranslation.getStringField(LOCALE)).willReturn(locale);
        given(customTranslation.getStringField(CUSTOM_TRANSLATION)).willReturn(translation);

        given(pluginStateResolver.isEnabled(plugin)).willReturn(false);
        given(customTranslationCacheService.isCustomTranslationAdded(key)).willReturn(true);

        // when
        customTranslationModelHooks.updateCache(customTranslationDD, customTranslation);

        // then
        verify(customTranslationCacheService, never()).addCustomTranslation(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
        verify(customTranslationCacheService).updateCustomTranslation(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
    }

    @Test
    public void shouldAddCustomTranslationWhenUpdateCacheIfPluginIsntEnabledAndNotContainsKey() {
        // given
        String plugin = "plugin";

        String key = "key";
        String locale = "locale";
        String translation = "translation";

        given(customTranslation.getStringField(PLUGIN_IDENTIFIER)).willReturn(plugin);
        given(customTranslation.getStringField(KEY)).willReturn(key);
        given(customTranslation.getStringField(LOCALE)).willReturn(locale);
        given(customTranslation.getStringField(CUSTOM_TRANSLATION)).willReturn(translation);

        given(pluginStateResolver.isEnabled(plugin)).willReturn(false);
        given(customTranslationCacheService.isCustomTranslationAdded(key)).willReturn(false);

        // when
        customTranslationModelHooks.updateCache(customTranslationDD, customTranslation);

        // then
        verify(customTranslationCacheService).addCustomTranslation(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(customTranslationCacheService, never()).updateCustomTranslation(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
    }

}
