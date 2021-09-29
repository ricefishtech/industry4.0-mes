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
package com.qcadoo.model.internal.module;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.qcadoo.model.internal.api.DynamicSessionFactoryBean;
import com.qcadoo.model.internal.api.ModelXmlResolver;
import com.qcadoo.model.internal.api.ModelXmlToClassConverter;
import com.qcadoo.model.internal.api.ModelXmlToDefinitionConverter;
import com.qcadoo.model.internal.api.ModelXmlToHbmConverter;
import com.qcadoo.plugin.api.ModuleFactory;

public class HibernateModuleFactory extends ModuleFactory<ModelModule> {

    @Autowired
    private ModelXmlToHbmConverter modelXmlToHbmConverter;

    @Autowired
    private ModelXmlToClassConverter modelXmlToClassConverter;

    @Autowired
    private ModelXmlToDefinitionConverter modelXmlToDefinitionConverter;

    @Autowired
    private ModelXmlResolver modelXmlResolver;

    @Autowired
    private DynamicSessionFactoryBean sessionFactoryBean;

    @Override
    public void postInit() {
        Resource[] resources = modelXmlResolver.getResources();

        modelXmlToClassConverter.convert(resources);

        sessionFactoryBean.initialize(modelXmlToHbmConverter.convert(resources));

        modelXmlToDefinitionConverter.convert(resources);
    }

    @Override
    protected ModelModule parseElement(final String pluginIdentifier, final Element element) {
        throw new IllegalStateException("Cannot create hibernate module");

    }

    @Override
    public String getIdentifier() {
        return "#hibernate";
    }

}
