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
package com.qcadoo.report.internal.module;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

import org.springframework.core.io.Resource;

import com.qcadoo.plugin.api.Module;
import com.qcadoo.plugin.api.ModuleException;
import com.qcadoo.report.internal.templates.ReportTemplateService;

public class ReportTemplateModule extends Module {

    private final String pluginIdentifier;

    private final String templateName;

    private final Resource templateFile;

    private final ReportTemplateService reportTemplateService;

    public ReportTemplateModule(final String pluginIdentifier, final String templateName, final Resource templateFile,
            final ReportTemplateService reportTemplateService) {
        super();

        this.pluginIdentifier = pluginIdentifier;
        this.templateName = templateName;
        this.templateFile = templateFile;
        this.reportTemplateService = reportTemplateService;
    }

    @Override
    public void enableOnStartup() {
        enable();
    }

    @Override
    public void enable() {
        try {
            JasperReport reportTemplate = JasperCompileManager.compileReport(templateFile.getInputStream());
            reportTemplateService.addTemplate(pluginIdentifier, templateName, reportTemplate);
        } catch (Exception e) {
            throw new ModuleException(pluginIdentifier, "report-template", e);
        }
    }

    @Override
    public void disable() {
        reportTemplateService.removeTemplate(pluginIdentifier, templateName);
    }

}
