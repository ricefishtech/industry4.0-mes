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
package com.qcadoo.report.api.pdf;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.report.internal.PdfHelperImpl;

public class PdfHelperTest {

    private PdfHelper pdfHelper;

    @Mock
    private List<Integer> columnsListSize;

    @Mock
    private TranslationService translationService;

    @Before
    public void init() {
        pdfHelper = new PdfHelperImpl();
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(pdfHelper, "translationService", translationService);

    }

    @Test
    public final void shouldReturnMaxSizeOfColumnRows() {
        // given
        Integer int1 = new Integer(2);
        Integer int2 = new Integer(4);
        Integer int3 = new Integer(1);
        columnsListSize = Arrays.asList(int1, int2, int3);
        // when
        int size = pdfHelper.getMaxSizeOfColumnsRows(columnsListSize);
        // then
        Assert.assertEquals(4, size);
    }

    @Test
    public final void shouldReturnZeroWhenColumnRowsIsEmpty() {
        // given
        columnsListSize = Arrays.asList();
        // when
        int size = pdfHelper.getMaxSizeOfColumnsRows(columnsListSize);
        // then
        Assert.assertEquals(0, size);
    }
}
