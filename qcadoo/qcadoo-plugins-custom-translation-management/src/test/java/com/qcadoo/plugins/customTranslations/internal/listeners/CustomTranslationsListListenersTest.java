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
package com.qcadoo.plugins.customTranslations.internal.listeners;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ComponentState.MessageType;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.GridComponent;

@Ignore
public class CustomTranslationsListListenersTest {

    private CustomTranslationsListListeners customTranslationsListListeners;

    private static final String L_GRID = "grid";

    private static final String L_WINDOW_ACTIVE_MENU = "window.activeMenu";

    @Mock
    private ViewDefinitionState view;

    @Mock
    private GridComponent customTranslationsGrid;

    @Mock
    private DataDefinition customTranslationDD;

    @Mock
    private Entity customTranslation;

    @Mock
    private List<Entity> customTranslations;

    private Map<String, Object> parameters = Maps.newHashMap();

    private static List<Entity> mockEntityList(final List<Entity> entities) {
        @SuppressWarnings("unchecked")
        final List<Entity> entityList = mock(List.class);

        given(entityList.iterator()).willAnswer(new Answer<Iterator<Entity>>() {

            @Override
            public Iterator<Entity> answer(final InvocationOnMock invocation) throws Throwable {
                return ImmutableList.copyOf(entities).iterator();
            }
        });

        given(entityList.isEmpty()).willReturn(entities.isEmpty());

        return entityList;
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        customTranslationsListListeners = new CustomTranslationsListListeners();
    }

    @Test
    public void shouldCleanCustomTranslationsWhenCustomTranslationIsSelected() {
        // given
        customTranslations = mockEntityList(Arrays.asList(customTranslation));

        given(view.getComponentByReference(L_GRID)).willReturn(customTranslationsGrid);
        given(customTranslationsGrid.getSelectedEntities()).willReturn(customTranslations);

        given(customTranslation.getDataDefinition()).willReturn(customTranslationDD);

        // when
        customTranslationsListListeners.cleanCustomTranslations(view, null, null);

        // then
        verify(customTranslation).setField(Mockito.anyString(), Mockito.any());
        verify(customTranslationDD).save(Mockito.any(Entity.class));
        verify(customTranslationsGrid).addMessage(Mockito.anyString(), Mockito.any(MessageType.class));
    }

    @Test
    public void shouldntCleanCustomTranslationsWhenCustomTranslationIsntSelected() {
        // given
        customTranslations = mockEntityList(new ArrayList<Entity>());

        given(view.getComponentByReference(L_GRID)).willReturn(customTranslationsGrid);
        given(customTranslationsGrid.getSelectedEntities()).willReturn(customTranslations);

        // when
        customTranslationsListListeners.cleanCustomTranslations(view, null, null);

        // then
        verify(customTranslation, never()).setField(Mockito.anyString(), Mockito.any());
        verify(customTranslationDD, never()).save(Mockito.any(Entity.class));
        verify(customTranslationsGrid, never()).addMessage(Mockito.anyString(), Mockito.any(MessageType.class));
    }

    @Test
    public void shouldReplaceCustomTranslations() {
        // given
        parameters.put(L_WINDOW_ACTIVE_MENU, "administration.customTranslations");

        String url = "../page/qcadooCustomTranslations/replaceCustomTranslations.html";

        // when
        customTranslationsListListeners.replaceCustomTranslations(view, null, null);

        // then
        verify(view).redirectTo(url, false, true, parameters);
    }

}
