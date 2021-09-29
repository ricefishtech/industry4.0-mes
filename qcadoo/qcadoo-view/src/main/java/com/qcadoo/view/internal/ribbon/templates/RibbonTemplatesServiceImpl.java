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
package com.qcadoo.view.internal.ribbon.templates;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.qcadoo.view.internal.api.ViewDefinition;
import com.qcadoo.view.internal.ribbon.model.InternalRibbon;
import com.qcadoo.view.internal.ribbon.templates.model.RibbonTemplate;
import com.qcadoo.view.internal.ribbon.templates.model.TemplateRibbonGroupsPack;

@Service
public class RibbonTemplatesServiceImpl implements RibbonTemplatesService {

    private final Map<String, RibbonTemplate> templates = new HashMap<String, RibbonTemplate>();

    @Override
    public void applyTemplate(final InternalRibbon ribbon, final RibbonTemplateParameters parameters,
            final ViewDefinition viewDefinition) {

        RibbonTemplate template = templates
                .get(getTemplateFullName(parameters.getTemplatePlugin(), parameters.getTemplateName()));
        if (template == null) {
            throw new IllegalStateException("ribbon template '" + parameters.getTemplatePlugin() + "."
                    + parameters.getTemplateName() + "' not found");
        }

        template.parseParameters(parameters);
        ribbon.addGroupsPack(new TemplateRibbonGroupsPack(template, parameters, viewDefinition));
    }

    @Override
    public RibbonTemplate getTemplate(final String templatePlugin, final String templateName) {
        return templates.get(getTemplateFullName(templatePlugin, templateName));
    }

    @Override
    public void addTemplate(final RibbonTemplate ribbonTemplate) {
        templates.put(getTemplateFullName(ribbonTemplate), ribbonTemplate);
    }

    @Override
    public void removeTemplate(final String templatePlugin, final String templateName) {
        templates.remove(getTemplateFullName(templatePlugin, templateName));
    }

    @Override
    public void removeTemplate(final RibbonTemplate ribbonTemplate) {
        templates.remove(getTemplateFullName(ribbonTemplate));
    }

    private String getTemplateFullName(final String templatePlugin, final String templateName) {
        if (templatePlugin == null) {
            return RibbonTemplateParameters.DEFAULT_TEMPLATE_PLUGIN + "." + templateName;
        }
        return templatePlugin + "." + templateName;
    }

    private String getTemplateFullName(final RibbonTemplate ribbonTemplate) {
        return ribbonTemplate.getPlugin() + "." + ribbonTemplate.getName();
    }

}
