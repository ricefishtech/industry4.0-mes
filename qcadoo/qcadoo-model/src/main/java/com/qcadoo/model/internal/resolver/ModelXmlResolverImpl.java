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
package com.qcadoo.model.internal.resolver;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.qcadoo.model.internal.api.ModelXmlResolver;
import com.qcadoo.model.internal.module.ModelXmlHolder;
import com.qcadoo.model.internal.utils.JdomUtils;

@Component
public final class ModelXmlResolverImpl implements ModelXmlResolver, ModelXmlHolder {

    private static final Logger LOG = LoggerFactory.getLogger(ModelXmlResolverImpl.class);

    private final Map<String, Document> documents = new HashMap<String, Document>();

    private final Map<String, Set<Element>> fields = new HashMap<String, Set<Element>>();

    private final Map<String, Set<Element>> hooks = new HashMap<String, Set<Element>>();

    @Override
    public Resource[] getResources() {
        List<Resource> resources = new ArrayList<Resource>();

        addFields();
        addHooks();

        for (Document document : documents.values()) {
            byte[] out = JdomUtils.documentToByteArray(document);
            if (LOG.isDebugEnabled()) {
                LOG.debug(new String(out));
            }
            resources.add(new ByteArrayResource(out));
        }

        documents.clear();
        fields.clear();
        hooks.clear();

        return resources.toArray(new Resource[resources.size()]);
    }

    private void addFields() {
        for (Map.Entry<String, Set<Element>> modelFields : fields.entrySet()) {
            Document document = documents.get(modelFields.getKey());
            checkNotNull(document, "Cannot find model for " + modelFields.getKey());
            Element fieldsElement = (Element) document.getRootElement().getChildren().get(0);

            if (!"fields".equals(fieldsElement.getName())) {
                throw new IllegalStateException("Expected element fields, found " + fieldsElement.getName());
            }

            for (Element field : modelFields.getValue()) {
                field = JdomUtils.replaceNamespace(field, document.getRootElement().getNamespace());
                fieldsElement.addContent(field.detach());
            }
        }
    }

    private void addHooks() {
        for (Map.Entry<String, Set<Element>> modelHooks : hooks.entrySet()) {
            Document document = documents.get(modelHooks.getKey());
            checkNotNull(document, "Cannot find model for " + modelHooks.getKey());
            Element hooksElement = (Element) document.getRootElement().getChildren().get(1);

            if (!"hooks".equals(hooksElement.getName())) {
                throw new IllegalStateException("Expected element hooks, found " + hooksElement.getName());
            }

            for (Element hook : modelHooks.getValue()) {
                hook = JdomUtils.replaceNamespace(hook, document.getRootElement().getNamespace());
                hooksElement.addContent(hook.detach());
            }
        }
    }

    @Override
    public void put(final String pluginIdentifier, final String modelName, final InputStream stream) {
        Document document = JdomUtils.inputStreamToDocument(stream);
        document.getRootElement().setAttribute("plugin", pluginIdentifier);
        documents.put(pluginIdentifier + "." + modelName, document);
    }

    @Override
    public void addField(final String pluginIdentifier, final String modelName, final Element field) {
        if (!fields.containsKey(pluginIdentifier + "." + modelName)) {
            fields.put(pluginIdentifier + "." + modelName, new HashSet<Element>());
        }
        fields.get(pluginIdentifier + "." + modelName).add(field);
    }

    @Override
    public void addHook(final String pluginIdentifier, final String modelName, final Element hook) {
        if (!hooks.containsKey(pluginIdentifier + "." + modelName)) {
            hooks.put(pluginIdentifier + "." + modelName, new HashSet<Element>());
        }
        hooks.get(pluginIdentifier + "." + modelName).add(hook);
    }
}
