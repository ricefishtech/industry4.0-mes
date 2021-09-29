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

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Preconditions;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.file.FileService;
import com.qcadoo.tenant.api.MultiTenantUtil;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private static final String L_FILE_URL_PREFIX = "/files/";

    private static FileService instance;

    @Autowired
    private TranslationService translationService;

    private File uploadDirectory;

    public FileServiceImpl() {
        FileServiceImpl.setInstance(this);
    }

    public static FileService getInstance() {
        return instance;
    }

    private static void setInstance(final FileService instance) {
        FileServiceImpl.instance = instance;
    }

    @Value("${reportPath}")
    public void setUploadDirectory(final String uploadDirectory) {
        this.uploadDirectory = new File(uploadDirectory);
    }

    @Override
    public String getName(final String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }

        return path.substring(path.lastIndexOf(File.separatorChar) + 1);
    }

    @Override
    public String getLastModificationDate(final String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }

        Date date = new Date(Long.valueOf(path.substring(path.lastIndexOf(File.separatorChar) + 1,
                path.lastIndexOf(File.separatorChar) + 14)));

        return new SimpleDateFormat(DateUtils.L_DATE_FORMAT, getLocale()).format(date);
    }

    @Override
    public String getUrl(final String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }

        int beginIndex = uploadDirectory.getAbsolutePath().length() + 1;

        if (beginIndex > path.length()) {
            return null;
        }

        return L_FILE_URL_PREFIX + normalizeSeparators(path.substring(beginIndex));
    }

    private String normalizeSeparators(final String string) {
        if ("\\".equals(File.separator)) {
            return string.replaceAll("\\\\", "/");
        } else {
            return string;
        }
    }

    private String denormalizeSeparators(final String string) {
        if ("\\".equals(File.separator)) {
            return string.replaceAll("/", "\\\\");
        } else {
            return string;
        }
    }

    @Override
    public String getPathFromUrl(final String url) {
        String denormalizedUrl = denormalizeSeparators(url);

        return uploadDirectory.getAbsolutePath() + File.separator
                + denormalizedUrl.substring(denormalizedUrl.indexOf(File.separatorChar) + L_FILE_URL_PREFIX.length() - 1);
    }

    @Override
    public InputStream getInputStream(final String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }

        try {
            return new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public String upload(final MultipartFile multipartFile) throws IOException {
        File file = getFileFromFilenameWithRandomDirectory(multipartFile.getOriginalFilename());

        OutputStream output = null;

        try {
            output = new FileOutputStream(file);

            IOUtils.copy(multipartFile.getInputStream(), output);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);

            IOUtils.closeQuietly(output);

            throw e;
        }

        return file.getAbsolutePath();
    }

    @Override
    public File createExportFile(final String filename) {
        return getFileFromFilenameWithRandomDirectory(filename);
    }

    @Override
    public File createReportFile(final String fileName) throws IOException {
        return getFileFromFilename(fileName);
    }

    private File getFileFromFilename(final String filename) throws IOException {
        File directory = new File(uploadDirectory, MultiTenantUtil.getCurrentTenantId() + File.separator);

        try {
            FileUtils.forceMkdir(directory);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);

            throw e;
        }

        return new File(directory, getNormalizedFileName(filename.substring(filename.lastIndexOf(File.separator) + 1)));
    }

    private File getFileFromFilenameWithRandomDirectory(final String filename) {
        String date = Long.toString(System.currentTimeMillis());

        File directory = new File(uploadDirectory, MultiTenantUtil.getCurrentTenantId() + File.separator
                + date.charAt(date.length() - 1) + File.separator + date.charAt(date.length() - 2) + File.separator);

        directory.mkdirs();

        return new File(directory, date + "_" + getNormalizedFileName(filename));
    }

    private String getNormalizedFileName(final String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.]+", "_");
    }

    @Override
    public Entity updateReportFileName(final Entity entity, final String dateFieldName, final String name) {
        String currentFiles = entity.getStringField("fileName");

        if (currentFiles == null) {
            currentFiles = "";
        }

        if (!currentFiles.isEmpty()) {
            currentFiles += ",";
        }

        entity.setField("fileName", currentFiles + getReportFullPath(name, (Date) entity.getField(dateFieldName)));

        return entity.getDataDefinition().save(entity);
    }

    @Override
    public Entity updateReportFileName(final Entity entity, final String dateFieldName, final String name, final String... args) {
        String currentFiles = entity.getStringField("fileName");

        if (currentFiles == null) {
            currentFiles = "";
        }

        if (!currentFiles.isEmpty()) {
            currentFiles += ",";
        }

        entity.setField("fileName", currentFiles + getReportFullPathWithArgs(name, (Date) entity.getField(dateFieldName), args));

        return entity.getDataDefinition().save(entity);
    }

    @Override
    public File compressToZipFile(List<File> documents, boolean removeCompressed) throws IOException {
        Preconditions.checkNotNull(documents, "documents argument is nullable.");
        Preconditions.checkArgument(!documents.isEmpty(), "documents list can't be empty");

        File zipFile = createExportFile("documents.zip");

        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        try {

            for (File document : documents) {
                ZipEntry ze = new ZipEntry(document.getName());
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(document);

                IOUtils.copy(in, zos);
                IOUtils.closeQuietly(in);

                zos.closeEntry();

                if (removeCompressed) {
                    remove(document.getAbsolutePath());
                }
            }

        } finally {
            IOUtils.closeQuietly(zos);
        }

        return zipFile;
    }

    private String getReportFullPath(final String name, final Date date) {
        String translatedReportName = translationService.translate(name, LocaleContextHolder.getLocale());

        return getReportPath() + translatedReportName + "_"
                + new SimpleDateFormat(DateUtils.L_REPORT_DATE_TIME_FORMAT, getLocale()).format(date);
    }

    private String getReportFullPathWithArgs(final String name, final Date date, final String... args) {
        String translatedReportName = translationService.translate(name, LocaleContextHolder.getLocale(), args);

        return getReportPath() + translatedReportName + "_"
                + new SimpleDateFormat(DateUtils.L_REPORT_DATE_TIME_FORMAT, getLocale()).format(date);
    }

    private String getReportPath() {
        return uploadDirectory.getAbsolutePath() + File.separator + MultiTenantUtil.getCurrentTenantId() + File.separator;
    }

    @Override
    public String getContentType(final String path) {
        return new MimetypesFileTypeMap().getContentType(new File(path));
    }

    @Override
    public void remove(final String path) {
        FileUtils.deleteQuietly(new File(path));
    }

}
