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
package com.qcadoo.model.internal.file;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.tenant.api.MultiTenantUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MultiTenantUtil.class)
public class FileServiceImplTest {

    private FileServiceImpl fileService;

    @Mock
    private TranslationService translationService;

    @Mock
    private File uploadDirectory;

    @Mock
    private DataDefinition dataDefinition;

    @Mock
    private Entity entity;

    @Mock
    private Date date;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        fileService = new FileServiceImpl();

        ReflectionTestUtils.setField(fileService, "translationService", translationService);
        ReflectionTestUtils.setField(fileService, "uploadDirectory", uploadDirectory);

        PowerMockito.mockStatic(MultiTenantUtil.class);
        given(MultiTenantUtil.getCurrentTenantId()).willReturn(0);

        given(uploadDirectory.getAbsolutePath()).willReturn("");
        given(date.getTime()).willReturn(1234567L);
        given(entity.getField("date")).willReturn(date);
        given(entity.getDataDefinition()).willReturn(dataDefinition);
    }

    @Test
    public void shouldAddANewReportFilenameIfThereAreNoneYet() {
        // given
        String filename = "newFileName.abc";
        given(translationService.translate(Mockito.eq(filename), Mockito.any(Locale.class))).willReturn(filename);
        given(entity.getStringField("fileName")).willReturn("");

        // when
        fileService.updateReportFileName(entity, "date", filename);

        // then
        verify(entity).setField("fileName", "/0/newFileName.abc_1970_01_01_01_20_34");
    }

    @Test
    public void shouldStackThatNewReportFilenameToTheCurrentFilenamesAndSeparateThemWithCommas() {
        // given
        String filename = "newFileName.abc";
        given(translationService.translate(Mockito.eq(filename), Mockito.any(Locale.class))).willReturn(filename);
        given(entity.getStringField("fileName")).willReturn("oldFilename");

        // when
        fileService.updateReportFileName(entity, "date", filename);

        // then
        verify(entity).setField("fileName", "oldFilename,/0/newFileName.abc_1970_01_01_01_20_34");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException(){
        // when
        try {
            fileService.compressToZipFile(null, true);
        } catch (IOException e) { }
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException(){
        // when
        try {
            fileService.compressToZipFile(new ArrayList<File>(), true);
        } catch (IOException e) { }
    }
}
