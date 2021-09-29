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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.qcadoo.customTranslation.api.CustomTranslationManagementService;
import com.qcadoo.customTranslation.constants.CustomTranslationContants;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.plugin.api.PluginStateResolver;

@Ignore
public class TranslationModuleOverrideUtilTest {

    private TranslationModuleOverrideUtil translationModuleOverrideUtil;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private TranslationService translationService;

    @Mock
    private PluginStateResolver pluginStateResolver;

    @Mock
    private CustomTranslationManagementService customTranslationManagementService;

    @Mock
    private Set<String> basenames;

    @Mock
    private Map<String, String> localesMap;

    @Mock
    private Set<String> locales;

    @Mock
    private List<Resource> resources;

    @Mock
    private Set<Object> keys;

    @Mock
    private Resource resource;

    @Mock
    private InputStream inputStream;

    @Mock
    private Properties properties;

    private static Set<String> mockStringSet(final Set<String> strings) {
        @SuppressWarnings("unchecked")
        final Set<String> stringSet = mock(Set.class);

        given(stringSet.iterator()).willAnswer(new Answer<Iterator<String>>() {

            @Override
            public Iterator<String> answer(final InvocationOnMock invocation) throws Throwable {
                return ImmutableSet.copyOf(strings).iterator();
            }
        });

        given(stringSet.isEmpty()).willReturn(strings.isEmpty());

        return stringSet;
    }

    private static List<Resource> mockResourcesList(final List<Resource> resources) {
        @SuppressWarnings("unchecked")
        final List<Resource> resourceList = mock(List.class);

        given(resourceList.iterator()).willAnswer(new Answer<Iterator<Resource>>() {

            @Override
            public Iterator<Resource> answer(final InvocationOnMock invocation) throws Throwable {
                return ImmutableList.copyOf(resources).iterator();
            }
        });

        given(resourceList.isEmpty()).willReturn(resources.isEmpty());

        return resourceList;
    }

    private static Set<Object> mockObjectSet(final Set<Object> objects) {
        @SuppressWarnings("unchecked")
        final Set<Object> objectSet = mock(Set.class);

        given(objectSet.iterator()).willAnswer(new Answer<Iterator<Object>>() {

            @Override
            public Iterator<Object> answer(final InvocationOnMock invocation) throws Throwable {
                return ImmutableSet.copyOf(objects).iterator();
            }
        });

        given(objectSet.isEmpty()).willReturn(objects.isEmpty());

        return objectSet;
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        translationModuleOverrideUtil = new TranslationModuleOverrideUtil();

        ReflectionTestUtils.setField(translationModuleOverrideUtil, "applicationContext", applicationContext);
        ReflectionTestUtils.setField(translationModuleOverrideUtil, "translationService", translationService);
        ReflectionTestUtils.setField(translationModuleOverrideUtil, "pluginStateResolver", pluginStateResolver);
        ReflectionTestUtils.setField(translationModuleOverrideUtil, "customTranslationManagementService",
                customTranslationManagementService);
    }

    @Test
    public void shouldReturnTrueWhenShouldOverride() {
        // given
        ReflectionTestUtils.setField(translationModuleOverrideUtil, "useCustomTranslations", true);

        given(pluginStateResolver.isEnabled(CustomTranslationContants.PLUGIN_IDENTIFIER)).willReturn(true);

        // when
        boolean result = translationModuleOverrideUtil.shouldOverride();

        // then
        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenShouldOverride() {
        // given
        ReflectionTestUtils.setField(translationModuleOverrideUtil, "useCustomTranslations", false);

        given(pluginStateResolver.isEnabled(CustomTranslationContants.PLUGIN_IDENTIFIER)).willReturn(false);

        // when
        boolean result = translationModuleOverrideUtil.shouldOverride();

        // then
        assertFalse(result);
    }

    @Test
    public void shouldntAddTranslationKeysForPluginIfLocalesAreEmpty() throws Exception {
        // given
        String pluginIdentifier = "plugin";

        locales = mockStringSet(new HashSet<String>());

        given(translationService.getLocales()).willReturn(localesMap);
        given(localesMap.keySet()).willReturn(locales);

        // when
        translationModuleOverrideUtil.addTranslationKeysForPlugin(pluginIdentifier, basenames);

        // then
        // verify(customTranslationManagementService, never()).addCustomTranslations(Mockito.anyString(), Mockito.anyString(),
        // Mockito.anySet());
    }

    // TODO lupo fix problem with test
    @Ignore
    @Test
    public void shouldAddTranslationKeysForPlugin() throws Exception {
        // given
        String pluginIdentifier = "plugin";

        String locale = "pl";
        String basename = "basename";
        String searchName = basename + "_" + locale + ".properties";
        Object key = "key";

        locales = mockStringSet(Sets.newHashSet(locale));

        given(translationService.getLocales()).willReturn(localesMap);
        given(localesMap.keySet()).willReturn(locales);

        basenames = mockStringSet(Sets.newHashSet(basename));

        given(applicationContext.getResource(searchName)).willReturn(resource);

        given(resource.getInputStream()).willReturn(inputStream);

        resources = mockResourcesList(Arrays.asList(resource));

        properties.load(inputStream);

        keys = mockObjectSet(Sets.newHashSet(key));

        given(properties.keySet()).willReturn(keys);

        // when
        translationModuleOverrideUtil.addTranslationKeysForPlugin(pluginIdentifier, basenames);

        // then
        // verify(customTranslationManagementService).addCustomTranslations(Mockito.anyString(), Mockito.anyString(),
        // Mockito.anySet());
    }

    // TODO lupo fix problem with test
    @Ignore
    @Test
    public void shouldRemoveTranslationKeysForPlugin() throws Exception {
        // given
        String pluginIdentifier = "plugin";

        String locale = "pl";
        String basename = "basename";
        String searchName = basename + "_" + locale + ".properties";
        Object key = "key";

        locales = mockStringSet(Sets.newHashSet(locale));

        given(translationService.getLocales()).willReturn(localesMap);
        given(localesMap.keySet()).willReturn(locales);

        basenames = mockStringSet(Sets.newHashSet(basename));

        given(applicationContext.getResource(searchName)).willReturn(resource);

        given(resource.getInputStream()).willReturn(inputStream);

        resources = mockResourcesList(Arrays.asList(resource));

        properties.load(inputStream);

        keys = mockObjectSet(Sets.newHashSet(key));

        given(properties.keySet()).willReturn(keys);

        // when
        translationModuleOverrideUtil.removeTranslationKeysForPlugin(pluginIdentifier);

        // then
        verify(customTranslationManagementService).removeCustomTranslations(Mockito.anyString());
    }

}
