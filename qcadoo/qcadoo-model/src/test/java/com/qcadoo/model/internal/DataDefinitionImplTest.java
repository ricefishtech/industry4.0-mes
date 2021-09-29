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
package com.qcadoo.model.internal;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.internal.api.DataAccessService;
import com.qcadoo.model.internal.api.EntityHookDefinition;

public class DataDefinitionImplTest {

    private DataDefinitionImpl dataDefinitionImpl;

    @Mock
    private Entity entity;

    @Mock
    private DataAccessService dataAccessService;

    @Mock
    private EntityHookDefinition createHook;

    @Mock
    private EntityHookDefinition updateHook;

    @Mock
    private EntityHookDefinition saveHook;

    @Mock
    private EntityHookDefinition copyHook;

    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);
        dataDefinitionImpl = new DataDefinitionImpl("plugin", "model", dataAccessService);

        stubEntityHook(createHook, "onCreate");
        stubEntityHook(updateHook, "onUpdate");
        stubEntityHook(saveHook, "onSave");
        stubEntityHook(copyHook, "onCopy");

        dataDefinitionImpl.addCreateHook(createHook);
        dataDefinitionImpl.addUpdateHook(updateHook);
        dataDefinitionImpl.addSaveHook(saveHook);
        dataDefinitionImpl.addCopyHook(copyHook);
    }

    /* HOOKS - CREATE */
    @Test
    public final void shouldCallCreateHook() throws Exception {
        // given
        given(entity.isValid()).willReturn(true);

        // when
        boolean result = dataDefinitionImpl.callCreateHook(entity);

        // then
        assertTrue(result);
        verify(createHook).call(entity);
    }

    @Test
    public final void shouldCallCreateHookIfEntityIsInvalid() throws Exception {
        // given
        given(entity.isValid()).willReturn(false);

        // when
        boolean result = dataDefinitionImpl.callCreateHook(entity);

        // then
        assertTrue(result);
        verify(createHook).call(entity);
    }

    @Test
    public final void shouldNotCallCreateHookIfHookIsDisabled() throws Exception {
        // given
        given(createHook.isEnabled()).willReturn(false);

        // when
        boolean result = dataDefinitionImpl.callCreateHook(entity);

        // then
        assertTrue(result);
        verify(createHook, never()).call(entity);
    }

    /* HOOKS - UPDATE */

    @Test
    public final void shouldCallUpdateHook() throws Exception {
        // given
        given(entity.isValid()).willReturn(true);

        // when
        boolean result = dataDefinitionImpl.callUpdateHook(entity);

        // then
        assertTrue(result);
        verify(updateHook).call(entity);
    }

    @Test
    public final void shouldNotCallUpdateHookIfEntityIsInvalid() throws Exception {
        // given
        given(entity.isValid()).willReturn(false);

        // when
        boolean result = dataDefinitionImpl.callUpdateHook(entity);

        // then
        assertFalse(result);
        verify(updateHook, never()).call(entity);
    }

    @Test
    public final void shouldNotCallUpdateHookIfHookIsDisabled() throws Exception {
        // given
        given(updateHook.isEnabled()).willReturn(false);

        // when
        boolean result = dataDefinitionImpl.callUpdateHook(entity);

        // then
        assertFalse(result);
        verify(updateHook, never()).call(entity);
    }

    /* HOOKS - SAVE */

    @Test
    public final void shouldCallSaveHook() throws Exception {
        // given
        given(entity.isValid()).willReturn(true);

        // when
        boolean result = dataDefinitionImpl.callSaveHook(entity);

        // then
        assertTrue(result);
        verify(saveHook).call(entity);
    }

    @Test
    public final void shouldNotCallSaveHookIfEntityIsInvalid() throws Exception {
        // given
        given(entity.isValid()).willReturn(false);

        // when
        boolean result = dataDefinitionImpl.callSaveHook(entity);

        // then
        assertFalse(result);
        verify(saveHook, never()).call(entity);
    }

    @Test
    public final void shouldNotCallSaveHookIfHookIsDisabled() throws Exception {
        // given
        given(saveHook.isEnabled()).willReturn(false);

        // when
        boolean result = dataDefinitionImpl.callSaveHook(entity);

        // then
        assertFalse(result);
        verify(saveHook, never()).call(entity);
    }

    /* HOOKS - COPY */
    @Test
    public final void shouldCallCopyHook() throws Exception {
        // given
        given(entity.isValid()).willReturn(true);

        // when
        boolean result = dataDefinitionImpl.callCopyHook(entity);

        // then
        assertTrue(result);
        verify(copyHook).call(entity);
    }

    @Test
    public final void shouldCallCopyHookIfEntityIsInvalid() throws Exception {
        // given
        given(entity.isValid()).willReturn(false);

        // when
        boolean result = dataDefinitionImpl.callCopyHook(entity);

        // then
        assertTrue(result);
        verify(copyHook).call(entity);
    }

    @Test
    public final void shouldNotCallCopyHookIfHookIsDisabled() throws Exception {
        // given
        given(copyHook.isEnabled()).willReturn(false);

        // when
        boolean result = dataDefinitionImpl.callCopyHook(entity);

        // then
        assertTrue(result);
        verify(copyHook, never()).call(entity);
    }

    private void stubEntityHook(final EntityHookDefinition hook, final String name) {
        given(hook.getName()).willReturn(name);
        given(hook.isEnabled()).willReturn(true);
        given(hook.call(Mockito.any(Entity.class))).willReturn(true);
    }
}
