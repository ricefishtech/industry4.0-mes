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
package com.qcadoo.view.internal.ribbon;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.qcadoo.view.api.ribbon.RibbonActionItem;
import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonActionItem;
import com.qcadoo.view.internal.ribbon.model.InternalRibbonGroup;
import com.qcadoo.view.internal.ribbon.model.RibbonGroupImpl;
import com.qcadoo.view.internal.ribbon.model.RibbonGroupsPack;
import com.qcadoo.view.internal.ribbon.templates.RibbonTemplateParameters;
import com.qcadoo.view.internal.ribbon.templates.model.RibbonTemplate;
import com.qcadoo.view.internal.ribbon.templates.model.TemplateRibbonGroupsPack;

public class TemplateRibbonGroupsPackTest {

    private TemplateRibbonGroupsPack templateRibbonGroupsPack;

    @Mock
    private RibbonTemplate template;

    @Mock
    private ViewDefinition viewDefinition;

    private List<InternalRibbonGroup> groupsResult1;

    private List<InternalRibbonGroup> groupsResult2;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);
        templateRibbonGroupsPack = new TemplateRibbonGroupsPack(template, null, viewDefinition);
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void shouldGetGroupsAlwaysReturnTheSameInstancesOfItems() throws Exception {
        // given
        groupsResult1 = Lists.newArrayList(getInternalRibbonGroup("first", null, null),
                getInternalRibbonGroup("second", null, null));
        groupsResult2 = Lists.newArrayList(getInternalRibbonGroup("first", null, null),
                getInternalRibbonGroup("second", null, null));
        stubTemplateGetRibbonGroups(groupsResult1, groupsResult2);

        // when
        List<InternalRibbonGroup> groups1 = templateRibbonGroupsPack.getGroups();
        List<InternalRibbonGroup> groups2 = templateRibbonGroupsPack.getGroups();

        // then
        assertRibbonGroupsEquals(groups1, groups2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void shouldGetGroupsUpdatedByTemplate() throws Exception {
        // given
        groupsResult1 = Lists.newArrayList(getInternalRibbonGroup("first", null, null),
                getInternalRibbonGroup("second", null, null));
        groupsResult2 = Lists.newArrayList(getInternalRibbonGroup("first", null, null),
                getInternalRibbonGroup("second", null, null), getInternalRibbonGroup("third", null, null));

        stubTemplateGetRibbonGroups(groupsResult1, groupsResult2);

        // when
        List<InternalRibbonGroup> groups1 = templateRibbonGroupsPack.getGroups();
        List<InternalRibbonGroup> groups2 = templateRibbonGroupsPack.getGroups();

        // then
        Assert.assertEquals(3, groups2.size());
        Assert.assertTrue(groups2.containsAll(groups1));
        Assert.assertTrue(groups2.contains(getInternalRibbonGroup("third", null, null)));
    }

    @Test
    public final void shouldGetUpdateReturnNullIfUpdateGroupsListIsEmpty() throws Exception {
        // when
        RibbonGroupsPack ribbonGroupsPackUpdate = templateRibbonGroupsPack.getUpdate();

        // then
        Assert.assertNull(ribbonGroupsPackUpdate);
    }

    @Test
    public final void shouldNotCallTemplateFromUpdateInfoPack() throws Exception {
        // given
        InternalRibbonGroup internalRibbonGroup = mock(InternalRibbonGroup.class);
        given(internalRibbonGroup.getUpdate()).willReturn(internalRibbonGroup);

        stubTemplateGetRibbonGroups(Lists.newArrayList(internalRibbonGroup));
        RibbonGroupsPack ribbonGroupsPackUpdate = templateRibbonGroupsPack.getUpdate();

        template = mock(RibbonTemplate.class); // Flush

        // when
        ribbonGroupsPackUpdate.getGroups();

        // then
        verify(template, never()).getRibbonGroups(Mockito.any(RibbonTemplateParameters.class), Mockito.eq(viewDefinition));
    }

    private void assertRibbonGroupsEquals(final List<InternalRibbonGroup> expected, final List<InternalRibbonGroup> actual) {
        Assert.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertEquals(expected.get(i), actual.get(i));
        }
    }

    private void stubTemplateGetRibbonGroups(final List<InternalRibbonGroup> group, final List<InternalRibbonGroup>... groups) {
        given(template.getRibbonGroups(Mockito.any(RibbonTemplateParameters.class), Mockito.eq(viewDefinition))).willReturn(
                group, groups);
    }

    private InternalRibbonGroup getInternalRibbonGroup(final String name, final String extensionPluginIdentifier,
            final List<RibbonActionItem> items) {
        InternalRibbonGroup ribbonGroup = new RibbonGroupImpl(name);
        ribbonGroup.setExtensionPluginIdentifier(extensionPluginIdentifier);
        if (items != null) {
            for (RibbonActionItem item : items) {
                ribbonGroup.addItem((InternalRibbonActionItem) item);
            }
        }
        return ribbonGroup;
    }

}
