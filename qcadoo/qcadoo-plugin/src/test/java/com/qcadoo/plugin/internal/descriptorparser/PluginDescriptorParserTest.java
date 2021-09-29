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
package com.qcadoo.plugin.internal.descriptorparser;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;

import java.util.Set;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.Lists;
import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleFactory;
import com.qcadoo.plugin.api.Plugin;
import com.qcadoo.plugin.api.PluginDependencyInformation;
import com.qcadoo.plugin.api.Version;
import com.qcadoo.plugin.api.VersionOfDependency;
import com.qcadoo.plugin.internal.DefaultPlugin;
import com.qcadoo.plugin.internal.PluginException;
import com.qcadoo.plugin.internal.api.InternalPlugin;
import com.qcadoo.plugin.internal.api.ModuleFactoryAccessor;
import com.qcadoo.plugin.internal.api.PluginDescriptorResolver;

public class PluginDescriptorParserTest {

    private ModuleFactoryAccessor moduleFactoryAccessor;

    private PluginDescriptorResolver pluginDescriptorResolver;

    private DefaultPluginDescriptorParser parser;

    private final Resource xmlFile1 = new FileSystemResource("src/test/resources/xml/testPlugin1.xml");

    private final Resource xmlFile2 = new FileSystemResource("src/test/resources/xml/testplugin2.xml");

    private final Resource xmlFile3 = new FileSystemResource("src/test/resources/xml/testIncorrectPlugin.xml");

    private Module testModule1;

    private Module testModule2;

    @SuppressWarnings("rawtypes")
    private ModuleFactory testModule1Factory;

    @SuppressWarnings("rawtypes")
    private ModuleFactory testModule2Factory;

    @SuppressWarnings("unchecked")
    @Before
    public void init() {
        moduleFactoryAccessor = mock(ModuleFactoryAccessor.class);
        pluginDescriptorResolver = mock(PluginDescriptorResolver.class);

        testModule1 = mock(Module.class);
        testModule2 = mock(Module.class);

        testModule1Factory = mock(ModuleFactory.class);
        testModule2Factory = mock(ModuleFactory.class);

        given(testModule1Factory.parse(Mockito.eq("testPlugin"), argThat(new HasNodeName("testModule1", "testModule1Content"))))
                .willReturn(testModule1);
        given(testModule2Factory.parse(Mockito.eq("testPlugin"), argThat(new HasNodeName("testModule2", "testModule2Content"))))
                .willReturn(testModule2);

        given(moduleFactoryAccessor.getModuleFactory("testModule1")).willReturn(testModule1Factory);
        given(moduleFactoryAccessor.getModuleFactory("testModule2")).willReturn(testModule2Factory);
        given(moduleFactoryAccessor.getModuleFactories()).willReturn(
                Lists.<ModuleFactory<?>> newArrayList(testModule1Factory, testModule2Factory));

        parser = new DefaultPluginDescriptorParser();
        parser.setModuleFactoryAccessor(moduleFactoryAccessor);
        parser.setPluginDescriptorResolver(pluginDescriptorResolver);

    }

    @Test
    public void shouldParseXml1() {
        // given

        // when
        Plugin result = parser.parse(xmlFile1);

        // then
        assertNotNull(result);
    }

    @Test
    public void shouldParseXml2() {
        // given

        // when
        Plugin result = parser.parse(xmlFile2);

        // then
        assertNotNull(result);
    }

    @Test
    public void shouldHaveIdentifierVersionAndSystemForXml1() {
        // given

        // when
        Plugin result = parser.parse(xmlFile1);

        // then
        assertEquals("testPlugin", result.getIdentifier());
        assertEquals(new Version("1.2.3"), result.getVersion());
        assertTrue(result.isSystemPlugin());
    }

    @Test
    public void shouldHaveIdentifierVersionAndSystemForXml2() {
        // given

        // when
        Plugin result = parser.parse(xmlFile2);

        // then
        assertEquals("testPlugin2", result.getIdentifier());
        assertEquals(new Version("2.3.1"), result.getVersion());
        assertFalse(result.isSystemPlugin());
    }

    @Test
    public void shouldHavePluginInformationsForXml1() {
        // given

        // when
        Plugin result = parser.parse(xmlFile1);

        // then
        assertEquals("testPluginName", result.getPluginInformation().getName());
        assertEquals("testPluginDescription", result.getPluginInformation().getDescription());
        assertEquals("testPluginVendorName", result.getPluginInformation().getVendor());
        assertEquals("testPluginVendorUrl", result.getPluginInformation().getVendorUrl());
    }

    @Test
    public void shouldHavePluginInformationsForXml2() {
        // given

        // when
        Plugin result = parser.parse(xmlFile2);

        // then
        assertEquals("testPlugin2Name", result.getPluginInformation().getName());
        assertNull(result.getPluginInformation().getDescription());
        assertNull(result.getPluginInformation().getVendor());
        assertNull(result.getPluginInformation().getVendorUrl());
    }

    @Test
    public void shouldHavePluginDependenciesInformationsForXml1() {
        // given

        // when
        Plugin result = parser.parse(xmlFile1);

        // then
        Set<PluginDependencyInformation> dependencies = result.getRequiredPlugins();
        assertEquals(3, dependencies.size());
        assertTrue(dependencies.contains(new PluginDependencyInformation("testPluginDependency1", new VersionOfDependency(
                "(1.2.3,2.3.4]"))));
        assertTrue(dependencies.contains(new PluginDependencyInformation("testPluginDependency2",
                new VersionOfDependency("1.1.1"))));
        assertTrue(dependencies.contains(new PluginDependencyInformation("testPluginDependency3", new VersionOfDependency(null))));
    }

    @Test
    public void shouldHavePluginDependenciesInformationsForXml2() {
        // given

        // when
        Plugin result = parser.parse(xmlFile2);

        // then
        assertEquals(0, result.getRequiredPlugins().size());
    }

    @Test
    public void shouldHaveModulesForXml1() throws Exception {
        // given

        // when
        Plugin result = parser.parse(xmlFile1);

        // then
        DefaultPlugin castedResult = (DefaultPlugin) result;
        assertTrue(castedResult.getModules(testModule1Factory).contains(testModule1));
        assertTrue(castedResult.getModules(testModule2Factory).contains(testModule2));
    }

    @Test
    public void shouldHaveModulesForXml2() throws Exception {
        // given

        // when
        Plugin result = parser.parse(xmlFile2);

        // then
        DefaultPlugin castedResult = (DefaultPlugin) result;
        assertEquals(0, castedResult.getModules(testModule1Factory).size());
        assertEquals(0, castedResult.getModules(testModule2Factory).size());
    }

    private class HasNodeName extends ArgumentMatcher<Element> {

        private final String expectedNodeName;

        private final String expectedNodeText;

        public HasNodeName(final String expectedNodeName, final String expectedNodeText) {
            this.expectedNodeName = expectedNodeName;
            this.expectedNodeText = expectedNodeText;
        }

        @Override
        public boolean matches(final Object element) {
            if (expectedNodeName.equals(((Element) element).getName())) {
                return expectedNodeText.equals(((Element) element).getText());
            }
            return false;
        }
    }

    @Test
    public void shouldParseAllPlugins() throws Exception {
        // given
        Resource[] testXmlsList = new Resource[] { xmlFile1, xmlFile2 };

        given(pluginDescriptorResolver.getDescriptors()).willReturn(testXmlsList);

        Plugin p1 = parser.parse(xmlFile1);
        Plugin p2 = parser.parse(xmlFile2);

        // when
        Set<InternalPlugin> result = parser.loadPlugins();

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(p1));
        assertTrue(result.contains(p2));
    }

    @Test(expected = PluginException.class)
    public void shouldNotParsePluginsWhenException() throws Exception {
        // given
        Resource[] testXmlsList = new Resource[] { xmlFile1, xmlFile2, xmlFile3 };

        given(pluginDescriptorResolver.getDescriptors()).willReturn(testXmlsList);

        // when
        parser.loadPlugins();

        // then
    }

    @Test(expected = PluginException.class)
    public void shouldFailForDuplicatedPlugins() throws Exception {
        // given
        Resource[] testXmlsList = new Resource[] { xmlFile1, xmlFile2, xmlFile2 };

        given(pluginDescriptorResolver.getDescriptors()).willReturn(testXmlsList);

        // when
        parser.loadPlugins();

        // then
    }

}
