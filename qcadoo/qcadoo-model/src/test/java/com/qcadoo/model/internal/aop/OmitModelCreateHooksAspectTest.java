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
package com.qcadoo.model.internal.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;

import org.junit.Test;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.internal.DataAccessServiceImpl;
import com.qcadoo.model.internal.api.InternalDataDefinition;

public class OmitModelCreateHooksAspectTest {

    @Test
    public final void checkCopyEntityPointcutDefinition() throws NoSuchMethodException {
        final Class<?> clazz = DataAccessServiceImpl.class;
        assertEquals("com.qcadoo.model.internal.DataAccessServiceImpl", clazz.getCanonicalName());
        assertEquals("com.qcadoo.model.internal.api.InternalDataDefinition", InternalDataDefinition.class.getCanonicalName());
        final Method method = clazz.getMethod("copy", InternalDataDefinition.class, Long[].class);
        assertNotNull(method);
    }

    @Test
    public final void checkCallCreateHookPointcutDefinition() throws NoSuchMethodException {
        final Class<?> clazz = InternalDataDefinition.class;
        assertEquals("com.qcadoo.model.internal.api.InternalDataDefinition", clazz.getCanonicalName());
        assertEquals("com.qcadoo.model.api.Entity", Entity.class.getCanonicalName());
        final Method method = clazz.getMethod("callCreateHook", Entity.class);
        assertNotNull(method);
        assertEquals(boolean.class, method.getReturnType());
    }

}
