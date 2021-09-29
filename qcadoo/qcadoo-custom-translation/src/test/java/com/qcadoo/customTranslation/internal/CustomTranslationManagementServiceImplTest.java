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
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.customTranslation.api.CustomTranslationManagementService;
import com.qcadoo.customTranslation.constants.CustomTranslationContants;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchResult;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(CustomTranslationManagementServiceImplTest.class)
public class CustomTranslationManagementServiceImplTest {

    private CustomTranslationManagementService customTranslationManagementService;

    @Mock
    private DataDefinitionService dataDefinitionService;

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

        customTranslationManagementService = new CustomTranslationManagementServiceImpl();

        ReflectionTestUtils.setField(customTranslationManagementService, "dataDefinitionService", dataDefinitionService);

        given(
                dataDefinitionService.get(CustomTranslationContants.PLUGIN_IDENTIFIER,
                        CustomTranslationContants.MODEL_CUSTOM_TRANSLATION)).willReturn(customTranslationDD);
    }

    @Test
    public void shouldAddWhenAddCustomTranslationIfCustomTranslationIsNull() throws Exception {
        // given
        String pluginIdentifier = "plugin";
        String key = "key";
        String locale = "pl";

        Session session = Mockito.mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        CustomTranslationBean entity = new CustomTranslationBean();

        CustomTranslationManagementService mockedCustomTranslationManagementService = PowerMockito
                .spy(customTranslationManagementService);
        PowerMockito.doReturn(session).when(mockedCustomTranslationManagementService, "getCurrentSession", customTranslationDD);
        PowerMockito.doReturn(entity).when(mockedCustomTranslationManagementService, "getInstanceForEntity", customTranslationDD);

        given(customTranslation.getDataDefinition()).willReturn(customTranslationDD);

        // when
        // mockedCustomTranslationManagementService.addCustomTranslations(pluginIdentifier, locale, Collections.singleton(key));

        // then
        assertEquals("key", entity.key);
        assertEquals("pl", entity.locale);
        assertEquals("plugin", entity.pluginIdentifier);
        assertFalse(entity.active);
        verify(session).save(entity);
    }

    @Test
    public void shouldRemoveWhenRemoveCustomTranslationIfCustomTranslationIsntNull() throws Exception {
        // given
        String pluginIdentifier = "plugin";

        Session session = Mockito.mock(Session.class, Mockito.RETURNS_DEEP_STUBS);

        CustomTranslationManagementService mockedCustomTranslationManagementService = PowerMockito
                .spy(customTranslationManagementService);
        PowerMockito.doReturn(session).when(mockedCustomTranslationManagementService, "getCurrentSession", customTranslationDD);

        given(customTranslation.getDataDefinition()).willReturn(customTranslationDD);

        // when
        mockedCustomTranslationManagementService.removeCustomTranslations(pluginIdentifier);

        // then
        verify(session).createQuery(Mockito.anyString());
    }

    @Test
    public void shouldReturnNullWhenGetCustomTranslationIfCustomTranslationIsNull() {
        // given
        String pluginIdentifier = "plugin";
        String key = "key";
        String locale = "pl";

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.setMaxResults(1)).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.uniqueResult()).willReturn(null);

        // when
        Entity result = customTranslationManagementService.getCustomTranslation(pluginIdentifier, key, locale);

        // then
        assertEquals(null, result);
    }

    @Test
    public void shouldReturnCustomTranslationWhenGetCustomTranslationIfCustomTranslationIsntNull() {
        // given
        String pluginIdentifier = "plugin";
        String key = "key";
        String locale = "pl";

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.setMaxResults(1)).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.uniqueResult()).willReturn(customTranslation);

        // when
        Entity result = customTranslationManagementService.getCustomTranslation(pluginIdentifier, key, locale);

        // then
        assertEquals(customTranslation, result);
    }

    @Test
    public void shouldReturnNullWhenGetCustomTranslationsIfCustomTranslationsAreNull() {
        // given
        String locale = "pl";

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.list()).willReturn(searchResult);
        given(searchResult.getEntities()).willReturn(null);

        // when
        // List<Entity> result = customTranslationManagementService.getCustomTranslations(locale);

        // then
        // assertEquals(null, result);
    }

    @Test
    public void shouldReturnCustomTranslationsWhenGetCustomTranslationsIfCustomTranslationsArentNull() {
        // given
        String locale = "pl";

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.list()).willReturn(searchResult);
        given(searchResult.getEntities()).willReturn(customTranslations);

        // when
        // List<Entity> result = customTranslationManagementService.getCustomTranslations(locale);

        // then
        // assertEquals(customTranslations, result);
    }

    @Test
    public void shouldReturnNullWhenGetCustomTranslationsForPluginIfCustomTranslationsAreNull() {
        // given
        String plugin = "plugin";
        boolean active = true;

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.list()).willReturn(searchResult);
        given(searchResult.getEntities()).willReturn(null);

        // when
        // List<Entity> result = customTranslationManagementService.getCustomTranslationsForPlugin(plugin, active);

        // then
        // assertEquals(null, result);
    }

    @Test
    public void shouldReturnCustomTranslationsWhenGetCustomTranslationsForPluginIfCustomTranslationsArentNull() {
        // given
        String plugin = "plugin";
        boolean active = true;

        given(customTranslationDD.find()).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.add(Mockito.any(SearchCriterion.class))).willReturn(searchCriteriaBuilder);
        given(searchCriteriaBuilder.list()).willReturn(searchResult);
        given(searchResult.getEntities()).willReturn(customTranslations);

        // when
        // List<Entity> result = customTranslationManagementService.getCustomTranslationsForPlugin(plugin, active);

        // then
        // assertEquals(customTranslations, result);
    }

    @Test
    public void shouldReturnCustomTranslationDD() {
        // given

        // when
        DataDefinition result = customTranslationManagementService.getCustomTranslationDD();

        // then
        assertEquals(customTranslationDD, result);
    }

    private static class CustomTranslationBean {

        String pluginIdentifier;

        String key;

        String locale;

        boolean active;

    }

}
