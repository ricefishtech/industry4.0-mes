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
package com.qcadoo.model.api.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.qcadoo.model.api.Entity;

/**
 * Service for managing files.
 * 
 * @since 0.4.1
 */
public interface FileService {

    /**
     * Returns name of the file from given path.
     * 
     * @param path
     *            path
     * @return name
     */
    String getName(final String path);

    /**
     * Returns last modification date of the file from given path.
     * 
     * @param path
     *            path
     * @return last modification date
     */
    String getLastModificationDate(final String path);

    /**
     * Returns URL for the file from given path.
     * 
     * @param path
     *            path
     * @return URL
     */
    String getUrl(final String path);

    /**
     * Returns path for the file from given URL.
     * 
     * @param url
     * @return path
     */
    String getPathFromUrl(final String url);

    /**
     * Returns stream of the file from given path.
     * 
     * @param path
     *            path
     * @return stream
     */
    InputStream getInputStream(final String path);

    /**
     * Create file from given uploaded file.
     * 
     * @param multipartFile
     *            uploaded file
     * @return path
     */
    String upload(final MultipartFile multipartFile) throws IOException;

    /**
     * Returns content type of the file from given path.
     * 
     * @param path
     *            path
     * @return content type
     */
    String getContentType(final String path);

    /**
     * Create empty export file with given name.
     * 
     * @param filename
     *            filename
     * @return File
     */
    File createExportFile(String filename);

    /**
     * Create empty report file with given name.
     * 
     * @param filename
     *            filename
     * @return File
     * @throws IOException
     */
    File createReportFile(String filename) throws IOException;

    /**
     * Remove the file from given path.
     * 
     * @param path
     *            path
     */
    void remove(String path);

    /**
     * Update report file name for given report entity
     * 
     * @param entity
     * @param dateFieldName
     *            report date field name
     * @param name
     *            translation code for language specific file name
     * @return updated entity
     */
    Entity updateReportFileName(Entity entity, String dateFieldName, String name);

    /**
     * Update report file name for given report entity
     *
     * @param entity
     * @param dateFieldName
     *            report date field name
     * @param name
     *            translation code for language specific file name
     * @param args
     *            translation args
     * @return updated entity
     */
    Entity updateReportFileName(Entity entity, String dateFieldName, String name, String... args);

    /**
     * Compress documents to newly created zip file.
     *
     * @param documents
     *            documents to be compress
     * @param removeCompressed
     *            if true removes documents after compression
     * @return created zip file
     * @throws IOException
     */
    File compressToZipFile(List<File> documents, boolean removeCompressed) throws IOException;
}