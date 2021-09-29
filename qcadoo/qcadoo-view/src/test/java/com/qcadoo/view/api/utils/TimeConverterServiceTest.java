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
package com.qcadoo.view.api.utils;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTimeConstants;
import org.junit.Test;

public class TimeConverterServiceTest {

    @Test
    public final void shouldConvertDurationToString() {
        // given
        Integer shortDuration = DateTimeConstants.SECONDS_PER_HOUR + DateTimeConstants.SECONDS_PER_MINUTE + 1;
        Integer longDuration = 2 * DateTimeConstants.SECONDS_PER_DAY + shortDuration;

        // when
        String shortDurationStringVal = TimeConverterService.durationToString(shortDuration);
        String longDurationStringVal = TimeConverterService.durationToString(longDuration);

        // then
        assertEquals("01:01:01", shortDurationStringVal);
        assertEquals("49:01:01", longDurationStringVal);
    }

    @Test
    public final void shouldReturnErrorStringValueIfDurationIsNull() {
        // when
        String strVal = TimeConverterService.durationToString(null);

        // then
        assertEquals("###", strVal);
    }

}
