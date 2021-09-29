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
package com.qcadoo.localization.internal.module;

import com.qcadoo.localization.internal.ConfigUtil;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.qcadoo.localization.internal.TranslationModuleService;
import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleFactory;

public class TranslationModuleFactory extends ModuleFactory<Module> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TranslationModuleService translationModuleService;

    @Autowired
    private ConfigUtil configUtil;
    
    @Override
    protected Module parseElement(final String pluginIdentifier, final Element element) {
        String path = getRequiredAttribute(element, "path");
        String basename = getAttribute(element, "basename");

        return new TranslationModule(applicationContext, translationModuleService, configUtil, pluginIdentifier, basename, path);
    }

    @Override
    public String getIdentifier() {
        return "translation";
    }

}
