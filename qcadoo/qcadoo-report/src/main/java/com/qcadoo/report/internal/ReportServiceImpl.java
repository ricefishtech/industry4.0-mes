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
package com.qcadoo.report.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.report.api.ReportException;
import com.qcadoo.report.api.ReportService;
import com.qcadoo.report.api.pdf.PdfHelper;
import com.qcadoo.report.internal.templates.ReportTemplateService;
import com.qcadoo.report.internal.util.ReportFormatFactory;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ComponentState.MessageType;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FormComponent;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private ReportTemplateService reportTemplateService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private PdfHelper pdfHelper;

    @Override
    public byte[] generateReportForEntity(final String templatePlugin, final String templateName, final ReportType type,
            final List<Long> entityIds, final Map<String, String> userArgs, final Locale locale) throws ReportException {

        Map<String, Object> parameters = new HashMap<String, Object>(userArgs);
        parameters.put("EntityIds", entityIds);

        return generateReport(templatePlugin, templateName, type, parameters, locale);
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] generateReport(final String templatePlugin, final String templateName, final ReportType type,
            final Map<String, Object> parameters, final Locale locale) throws ReportException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Try to generate report [" + type + ", " + templatePlugin + "." + templateName + ", " + parameters + "]");
        }

        JasperReport template = reportTemplateService.getTemplate(templatePlugin, templateName);

        if (template == null) {
            throw new ReportException(ReportException.Type.NO_TEMPLATE_FOUND, templatePlugin + "." + templateName);
        }

        return generateReport(template, type, parameters, locale);
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] generateReport(final String templateContent, final ReportType type, final Map<String, Object> parameters,
            final Locale locale) throws ReportException {
        InputStream in = null;

        try {
            in = new ByteArrayInputStream(templateContent.getBytes("UTF-8"));

            JasperReport template = JasperCompileManager.compileReport(in);

            return generateReport(template, type, parameters, locale);
        } catch (JRException e) {
            throw new ReportException(ReportException.Type.NO_TEMPLATE_FOUND, e);
        } catch (UnsupportedEncodingException e) {
            throw new ReportException(ReportException.Type.NO_TEMPLATE_FOUND, e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private byte[] generateReport(final JasperReport template, final ReportType type, final Map<String, Object> parameters,
            final Locale locale) throws ReportException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            parameters.put(JRParameter.REPORT_LOCALE, locale);
            parameters.put("Author", pdfHelper.getDocumentAuthor());
            parameters.put(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION, session);

            ResourceBundle resourceBundle = new MessageSourceResourceBundle(messageSource, locale);
            parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);

            parameters.put(JRParameter.REPORT_FORMAT_FACTORY, new ReportFormatFactory());

            JasperPrint jasperPrint = JasperFillManager.fillReport(template, parameters);

            JRExporter exporter = getExporter(type);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, stream);

            exporter.exportReport();

            return stream.toByteArray();

        } catch (JRException e) {
            throw new ReportException(ReportException.Type.GENERATE_REPORT_EXCEPTION, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private JRExporter getExporter(final ReportType type) throws ReportException {
        JRExporter exporter = null;
        switch (type) {
            case PDF:
                exporter = new JRPdfExporter();
                break;
            case XLS:
                exporter = new JRXlsExporter();
                break;
            case CSV:
                exporter = new JRCsvExporter();
                break;
            default:
                throw new ReportException(ReportException.Type.WRONG_REPORT_TYPE, type.toString());
        }
        return exporter;
    }

    @Override
    public void printGeneratedReport(final ViewDefinitionState viewDefinitionState, final ComponentState state,
            final String[] args) {
        if (state.getFieldValue() instanceof Long) {
            Entity entity = dataDefinitionService.get(args[1], args[2]).get((Long) state.getFieldValue());
            if (entity == null) {
                state.addMessage("qcadooView.message.entityNotFound", MessageType.FAILURE);
            } else if (StringUtils.hasText(entity.getStringField("fileName"))) {
                final StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append("/generateSavedReport/").append(args[1]);
                urlBuilder.append("/").append(args[2]).append(".");
                urlBuilder.append(args[0]).append("?id=").append(state.getFieldValue());
                if (args.length >= 4) {
                    urlBuilder.append("&reportNo=").append(args[3]);
                }
                viewDefinitionState.redirectTo(urlBuilder.toString(), true, false);
            } else {
                state.addMessage("qcadooReport.errorMessage.documentsWasNotGenerated", MessageType.FAILURE);
            }
        } else {
            if (state instanceof FormComponent) {
                state.addMessage("qcadooView.form.entityWithoutIdentifier", MessageType.FAILURE);
            } else {
                state.addMessage("qcadooView.grid.noRowSelectedError", MessageType.FAILURE);
            }
        }
    }

}
