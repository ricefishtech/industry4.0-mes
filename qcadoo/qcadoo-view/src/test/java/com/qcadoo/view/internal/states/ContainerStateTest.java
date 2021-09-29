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
package com.qcadoo.view.internal.states;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.api.InternalComponentState;
import com.qcadoo.view.internal.components.form.FormComponentPattern;
import com.qcadoo.view.internal.components.form.FormComponentState;

public class ContainerStateTest extends AbstractStateTest {

    private ApplicationContext applicationContext;

    @Before
    public void init() throws Exception {
        applicationContext = mock(ApplicationContext.class);
    }

    @Test
    public void shouldHaveNoChildren() throws Exception {
        // given
        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState container = new FormComponentState(pattern);

        // when
        Map<String, InternalComponentState> children = container.getChildren();

        // then
        assertNotNull(children);
        assertEquals(0, children.size());
    }

    @Test
    public void shouldHaveChildren() throws Exception {
        // given
        InternalComponentState component1 = createMockComponent("component1");
        InternalComponentState component2 = createMockComponent("component2");

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState container = new FormComponentState(pattern);
        container.addChild(component1);
        container.addChild(component2);

        // when
        Map<String, InternalComponentState> children = container.getChildren();

        // then
        assertNotNull(children);
        assertEquals(2, children.size());
    }

    @Test
    public void shouldReturnChildByName() throws Exception {
        // given
        InternalComponentState component = createMockComponent("component");

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState container = new FormComponentState(pattern);
        container.addChild(component);

        // when
        ComponentState child = container.getChild("component");

        // then
        assertSame(component, child);
    }

    @Test
    public void shouldReturnNullIfChildNotExist() throws Exception {
        // given
        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState container = new FormComponentState(pattern);

        // when
        ComponentState child = container.getChild("component");

        // then
        assertNull(child);
    }

    @Test
    public void shouldInitializeChildren() throws Exception {
        // given
        InternalComponentState component1 = createMockComponent("component1");
        InternalComponentState component2 = createMockComponent("component2");

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState container = new FormComponentState(pattern);
        container.addChild(component1);
        container.addChild(component2);

        JSONObject json = new JSONObject();
        JSONObject children = new JSONObject();
        JSONObject component1Json = new JSONObject();
        component1Json.put(AbstractComponentState.JSON_CONTENT, new JSONObject());
        JSONObject component2Json = new JSONObject();
        component2Json.put(AbstractComponentState.JSON_CONTENT, new JSONObject());
        children.put("component1", component1Json);
        children.put("component2", component2Json);
        json.put(AbstractComponentState.JSON_CHILDREN, children);
        json.put(AbstractComponentState.JSON_CONTENT, new JSONObject(Collections.singletonMap("entityId", 13L)));

        // when
        container.initialize(json, Locale.ENGLISH);

        // then
        verify(component1).initialize(component1Json, Locale.ENGLISH);
        verify(component2).initialize(component2Json, Locale.ENGLISH);
    }

    @Test
    public void shouldRenderChildren() throws Exception {
        // given
        JSONObject component1Json = new JSONObject();
        component1Json.put(AbstractComponentState.JSON_CONTENT, "test1");
        JSONObject component2Json = new JSONObject();
        component2Json.put(AbstractComponentState.JSON_CONTENT, "test2");

        InternalComponentState component1 = createMockComponent("component1");
        given(component1.render()).willReturn(component1Json);
        InternalComponentState component2 = createMockComponent("component2");
        given(component2.render()).willReturn(component2Json);

        FormComponentPattern pattern = mock(FormComponentPattern.class);
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);
        setField(pattern, "applicationContext", applicationContext);
        FormComponentState container = new FormComponentState(pattern);
        container.addChild(component1);
        container.addChild(component2);

        // when
        JSONObject json = container.render();

        // then
        verify(component1).render();
        verify(component2).render();
        assertEquals(
                "test1",
                json.getJSONObject(AbstractComponentState.JSON_CHILDREN).getJSONObject("component1")
                        .getString(AbstractComponentState.JSON_CONTENT));
        assertEquals(
                "test2",
                json.getJSONObject(AbstractComponentState.JSON_CHILDREN).getJSONObject("component2")
                        .getString(AbstractComponentState.JSON_CONTENT));
    }

}
