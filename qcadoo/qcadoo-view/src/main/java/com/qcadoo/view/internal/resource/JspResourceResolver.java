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
package com.qcadoo.view.internal.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class JspResourceResolver implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(JspResourceResolver.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${QCADOO_WEBAPP_PATH}")
    private String webappPath;

    @Value("${useJarStaticResources}")
    private boolean useJarStaticResources;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (!useJarStaticResources) {
            return;
        }

        LOG.info("Copying jsps ...");

        try {
            Resource[] resources = applicationContext.getResources("classpath*:WEB-INF/jsp/**/*");

            for (Resource resource : resources) {
                copyResource(resource);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot find resources jsp in classpath", e);
        }
    }

    private void copyResource(final Resource resource) {
        if (!resource.isReadable()) {
            return;
        }

        try {
            String path = resource.getURI().toString().split("WEB-INF/jsp/")[1];
            File file = new File(webappPath + "/WEB-INF/jsp/" + path);

            if (resource.getInputStream().available() == 0) {
                FileUtils.forceMkdir(file);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Copying " + path + " to " + file.getAbsolutePath());
                }

                OutputStream output = null;

                try {
                    output = new BufferedOutputStream(new FileOutputStream(file));
                    IOUtils.copy(resource.getInputStream(), output);
                } finally {
                    IOUtils.closeQuietly(output);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot copy resource " + resource, e);
        }
    }

}
