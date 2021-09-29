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
package com.qcadoo.model.internal.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;


public class JdomUtilsTest {

    private Element element;
    
    @Before
    public void init() {
        element = mock(Element.class);
    }
    
    @Test
    public void shouldRenameNamespace() throws Exception {
        // given
        Element childElement = mock(Element.class);
        when(element.getChildren()).thenReturn(Lists.newArrayList(childElement, childElement));
        
        // when
        JdomUtils.replaceNamespace(element, Mockito.any(Namespace.class));
        
        //then
        verify(element).setNamespace(Mockito.any(Namespace.class));
        verify(childElement, times(2)).setNamespace(Mockito.any(Namespace.class));
    }
}
