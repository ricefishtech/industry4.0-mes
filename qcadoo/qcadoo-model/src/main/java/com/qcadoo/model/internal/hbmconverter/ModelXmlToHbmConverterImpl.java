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
package com.qcadoo.model.internal.hbmconverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.qcadoo.model.internal.api.Constants;
import com.qcadoo.model.internal.api.ModelXmlToHbmConverter;

@Component
public class ModelXmlToHbmConverterImpl implements ModelXmlToHbmConverter {

    private static final Logger LOG = LoggerFactory.getLogger(ModelXmlToHbmConverterImpl.class);

    @Value("${hibernateDialect}")
    private String hibernateDialect;

    private Resource xsl;

    private Transformer transformer;

    @PostConstruct
    public final void init() {
        xsl = resolveXslResource();
        if (!xsl.isReadable()) {
            throw new IllegalStateException("Failed to read " + xsl.getFilename());
        }
        try {
            transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsl.getInputStream()));
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("Failed to initialize xsl transformer", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize xsl transformer", e);
        }
    }

    private Resource resolveXslResource() {
        String xslFileName = Constants.XSL;
        if (hibernateDialect != null && "org.hibernate.dialect.Oracle10gDialect".equalsIgnoreCase(hibernateDialect.trim())) {
            xslFileName = Constants.XSL_ORACLE_10G;
        }
        return new ClassPathResource(xslFileName);
    }

    @Override
    public Resource[] convert(final Resource... resources) {
        List<Resource> hbms = new ArrayList<Resource>();

        for (Resource resource : resources) {
            if (resource.isReadable()) {
                LOG.info("Converting " + resource + " to hbm.xml");

                byte[] hbm = getHbm(resource);

                if (LOG.isDebugEnabled()) {
                    LOG.debug(new String(hbm));
                }

                hbms.add(new InputStreamResource(new ByteArrayInputStream(hbm)));
            }
        }

        return hbms.toArray(new Resource[hbms.size()]);
    }

    private byte[] getHbm(final Resource resource) {
        try {
            return transform(resource);
        } catch (TransformerException e) {
            throw new IllegalStateException("Error while parsing model.xml: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Error while parsing model.xml: " + e.getMessage(), e);
        }
    }

    protected byte[] transform(final Resource resource) throws TransformerException, IOException {
        ByteArrayOutputStream hbm = new ByteArrayOutputStream();
        transformer.transform(new StreamSource(resource.getInputStream()), new StreamResult(hbm));
        return hbm.toByteArray();
    }
}
