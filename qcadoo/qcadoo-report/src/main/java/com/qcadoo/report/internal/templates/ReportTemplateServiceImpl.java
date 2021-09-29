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
package com.qcadoo.report.internal.templates;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;

import org.springframework.stereotype.Service;

@Service
public class ReportTemplateServiceImpl implements ReportTemplateService {

    final Map<String, JasperReport> teplates = new HashMap<String, JasperReport>();

    @Override
    public JasperReport getTemplate(final String plugin, final String name) {
        return teplates.get(generateKey(plugin, name));
    }

    @Override
    public void addTemplate(final String plugin, final String name, final JasperReport reportTemplate) {
        teplates.put(generateKey(plugin, name), reportTemplate);
    }

    @Override
    public void removeTemplate(final String plugin, final String name) {
        teplates.remove(generateKey(plugin, name));
    }

    private String generateKey(final String plugin, final String name) {
        return plugin + "." + name;
    }

}
