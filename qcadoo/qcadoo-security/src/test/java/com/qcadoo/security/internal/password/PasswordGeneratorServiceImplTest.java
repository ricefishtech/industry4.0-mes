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
package com.qcadoo.security.internal.password;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class PasswordGeneratorServiceImplTest {

    private PasswordGeneratorServiceImpl passwordGeneratorServiceImpl;

    @Before
    public final void init() {
        passwordGeneratorServiceImpl = new PasswordGeneratorServiceImpl();
    }

    @Test
    public final void shouldReturnValidPassword() throws Exception {
        // when
        String generatedPassword = passwordGeneratorServiceImpl.generatePassword();

        // then
        Assert.assertEquals(10, generatedPassword.length());
        Assert.assertFalse(generatedPassword.contains(" "));
    }

    @Test
    public final void shouldReturnValidPasswordWithSpecifiedLenght() throws Exception {
        // given
        int length = 8;

        // when
        String generatedPassword = passwordGeneratorServiceImpl.generatePassword(length);

        // then
        Assert.assertEquals(length, generatedPassword.length());
        Assert.assertFalse(generatedPassword.contains(" "));
    }

}
