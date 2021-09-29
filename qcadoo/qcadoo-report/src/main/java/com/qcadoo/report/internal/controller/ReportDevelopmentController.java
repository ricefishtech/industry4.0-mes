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
package com.qcadoo.report.internal.controller;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.report.api.ReportException;
import com.qcadoo.report.api.ReportService;
import com.qcadoo.report.api.ReportService.ReportType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
public class ReportDevelopmentController {

    private static final String L_PARAMS = "params";

    private static final String L_LOCALE = "locale";

    private static final String L_TEMPLATE = "template";

    private static final String L_QCADOO_REPORT_REPORT = "qcadooReport/report";

    private static final String L_HQL = "hql";

    private static final String L_QCADOO_REPORT_HQL = "qcadooReport/hql";

    private static final Logger LOG = LoggerFactory.getLogger(ReportDevelopmentController.class);

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ReportService reportService;

    @Value("${showReportDevelopment}")
    private boolean showReportDevelopment;

    @RequestMapping(value = "developReport/hql", method = RequestMethod.GET)
    public ModelAndView showHqlForm() {
        if (!showReportDevelopment) {
            return new ModelAndView(new RedirectView("/"));
        }

        return new ModelAndView(L_QCADOO_REPORT_HQL);
    }

    @RequestMapping(value = "developReport/hql", method = RequestMethod.POST)
    public ModelAndView executeHql(@RequestParam(L_HQL) final String hql) {
        if (!showReportDevelopment) {
            return new ModelAndView(new RedirectView("/"));
        }

        try {
            List<Entity> entities = dataDefinitionService.get("qcadooPlugin", "plugin").find(hql).list().getEntities();

            if (entities.isEmpty()) {
                return new ModelAndView(L_QCADOO_REPORT_HQL).addObject(L_HQL, hql).addObject("isEmpty", true);
            } else {
                DataDefinition dataDefinition = entities.get(0).getDataDefinition();

                List<String> headers = new ArrayList<String>();

                if (!isDynamicDataDefinition(dataDefinition)) {
                    headers.add("id");
                }

                headers.addAll(dataDefinition.getFields().keySet());

                List<List<String>> rows = new ArrayList<List<String>>();

                for (Entity entity : entities) {
                    List<String> row = new ArrayList<String>();

                    if (!isDynamicDataDefinition(dataDefinition)) {
                        row.add(String.valueOf(entity.getId()));
                    }

                    for (String field : dataDefinition.getFields().keySet()) {
                        if (entity.getField(field) == null) {
                            row.add("");
                        } else if (entity.getField(field) instanceof EntityList) {
                            row.add("[]");
                        } else {
                            row.add(String.valueOf(entity.getField(field)));
                        }
                    }

                    rows.add(row);
                }

                return new ModelAndView(L_QCADOO_REPORT_HQL).addObject(L_HQL, hql).addObject("headers", headers)
                        .addObject("rows", rows).addObject("isOk", true);
            }
        } catch (Exception e) {
            return showException(L_QCADOO_REPORT_HQL, e).addObject(L_HQL, hql);
        }
    }

    private ModelAndView showException(final String view, final Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return new ModelAndView(view).addObject("exceptionMessage", e.getMessage()).addObject("exception", writer.toString())
                .addObject("isError", true);
    }

    private boolean isDynamicDataDefinition(final DataDefinition dataDefinition) {
        return dataDefinition.getPluginIdentifier().equals("qcadooModel") && dataDefinition.getName().startsWith("dynamic");
    }

    @RequestMapping(value = "developReport/report", method = RequestMethod.GET)
    public ModelAndView showReportForm() {
        if (!showReportDevelopment) {
            return new ModelAndView(new RedirectView("/"));
        }

        return new ModelAndView(L_QCADOO_REPORT_REPORT);
    }

    @RequestMapping(value = "developReport/report", method = RequestMethod.POST)
    public ModelAndView uploadReportFile(@RequestParam("file") final MultipartFile file, final HttpServletRequest request) {
        if (!showReportDevelopment) {
            return new ModelAndView(new RedirectView("/"));
        }

        if (file.isEmpty()) {
            return new ModelAndView(L_QCADOO_REPORT_REPORT).addObject("isFileInvalid", true);
        }

        try {
            String template = IOUtils.toString(file.getInputStream());

            List<ReportParameter> params = getReportParameters(template);

            return new ModelAndView(L_QCADOO_REPORT_REPORT).addObject(L_TEMPLATE, template).addObject("isParameter", true)
                    .addObject(L_PARAMS, params).addObject(L_LOCALE, "en");
        } catch (Exception e) {
            return showException(L_QCADOO_REPORT_REPORT, e);
        }
    }

    private final List<String> ignoredParameters = Arrays.asList("Author");

    @SuppressWarnings("unchecked")
    private List<ReportParameter> getReportParameters(final String template) throws JDOMException, IOException {
        Document document = new SAXBuilder().build(new ByteArrayInputStream(template.getBytes()));

        Namespace namespace = Namespace.getNamespace("http://jasperreports.sourceforge.net/jasperreports");

        List<Element> parameters = document.getRootElement().getChildren("parameter", namespace);

        List<ReportParameter> params = new ArrayList<ReportParameter>();

        for (Element parameter : parameters) {
            String value = null;

            if (ignoredParameters.contains(parameter.getAttributeValue("name"))) {
                continue;
            }

            if (parameter.getChild("defaultValueExpression", namespace) != null) {
                value = parameter.getChild("defaultValueExpression", namespace).getTextNormalize().replaceAll("\\\"", "");
            }

            params.add(new ReportParameter(parameter.getAttributeValue("name"), parameter.getAttributeValue("class"), value));
        }

        return params;
    }

    @RequestMapping(value = "developReport/generate", method = RequestMethod.POST)
    public ModelAndView generateReport(@RequestParam(value = L_TEMPLATE) final String template,
            @RequestParam(value = "type") final String type, @RequestParam(value = L_LOCALE) final String locale,
            final HttpServletRequest request, final HttpServletResponse response) {
        if (!showReportDevelopment) {
            return new ModelAndView(new RedirectView("/"));
        }

        List<ReportParameter> params = null;

        try {
            params = getReportParameters(template);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return showException(L_QCADOO_REPORT_REPORT, e).addObject(L_TEMPLATE, template).addObject("isParameter", true)
                    .addObject(L_LOCALE, locale);
        }

        try {
            ReportType reportType = ReportType.valueOf(type.toUpperCase(Locale.ENGLISH));
            Locale reportLocale = new Locale(locale);

            Map<String, Object> parameters = new HashMap<String, Object>();

            for (ReportParameter param : params) {
                param.setValue(request.getParameter("params[" + param.getName() + "]"));
                parameters.put(param.getName(), param.getRawValue());
            }

            byte[] report = reportService.generateReport(template, reportType, parameters, reportLocale);

            response.setContentLength(report.length);
            response.setContentType(reportType.getMimeType());
            response.setHeader("Content-disposition", "attachment; filename=report." + type);
            response.addHeader("Expires", "Tue, 03 Jul 2001 06:00:00 GMT");
            response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.addHeader("Pragma", "no-cache");

            OutputStream out = response.getOutputStream();

            try {
                IOUtils.copy(new ByteArrayInputStream(report), out);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw new ReportException(ReportException.Type.ERROR_WHILE_COPYING_REPORT_TO_RESPONSE, e);
            }

            out.flush();

            return null;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return showException(L_QCADOO_REPORT_REPORT, e).addObject(L_TEMPLATE, template).addObject("isParameter", true)
                    .addObject(L_PARAMS, params).addObject(L_LOCALE, locale);
        }

    }

    public static class ReportParameter {

        private static final String JAVA_UTIL_LIST = "java.util.List";

        private final String name;

        private final String clazz;

        private Object value;

        public ReportParameter(final String name, final String clazz, final String value) {
            this.name = name;
            this.clazz = clazz;
            this.value = convertValueToObject(value);
        }

        public void setValue(final String value) {
            this.value = convertValueToObject(value);
        }

        public String getClazz() {
            return clazz;
        }

        public String getName() {
            return name;
        }

        public Object getRawValue() {
            return value;
        }

        public String getValue() {
            return convertValueToString(value);
        }

        @SuppressWarnings("rawtypes")
        private String convertValueToString(final Object value) {
            if (value == null) {
                return "";
            } else if (JAVA_UTIL_LIST.equals(clazz)) {
                return StringUtils.join((List) value, ",");
            } else {
                return String.valueOf(value);
            }
        }

        private Object convertValueToObject(final String value) {
            if (JAVA_UTIL_LIST.equals(clazz) && !org.springframework.util.StringUtils.hasText(value)) {
                return Collections.emptyList();
            } else if (JAVA_UTIL_LIST.equals(clazz) && "EntityIds".equals(name)) {
                String[] strings = value.trim().split("\\s*,\\s*");
                List<Long> values = new ArrayList<Long>();

                for (String string : strings) {
                    values.add(Long.valueOf(string));
                }

                return values;
            } else if (JAVA_UTIL_LIST.equals(clazz)) {
                String[] values = value.trim().split("\\s*,\\s*");
                return Arrays.asList(values);
            } else {
                return value;
            }
        }

    }

}
