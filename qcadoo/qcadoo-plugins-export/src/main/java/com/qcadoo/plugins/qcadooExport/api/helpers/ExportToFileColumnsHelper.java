package com.qcadoo.plugins.qcadooExport.api.helpers;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.qcadoo.plugin.api.PluginUtils;
import com.qcadoo.plugin.api.RunIfEnabled;
import com.qcadoo.plugins.qcadooExport.api.ExportToFileColumns;
import com.qcadoo.view.api.components.GridComponent;

@Service
public class ExportToFileColumnsHelper<T extends ExportToFileColumns> {

    private static final Logger logger = LoggerFactory.getLogger(ExportToFileColumnsHelper.class);

    @Autowired
    private ApplicationContext applicationContext;

    public T getColumnsService(final Class<?> clazz) {
        List<ExportToFileColumns> services = applicationContext.getBeansOfType(ExportToFileColumns.class).values().stream()
				.collect(Collectors.toList());

        AnnotationAwareOrderComparator.sort(services);

        for (ExportToFileColumns service : services) {
            if (serviceEnabled(service) && clazz.isAssignableFrom(service.getClass())) {
                return (T) service;
            }
        }

        throw new IllegalStateException("No active ExportToFileColumns found.");
    }

    private <M extends ExportToFileColumns> boolean serviceEnabled(M service) {
        RunIfEnabled runIfEnabled = service.getClass().getAnnotation(RunIfEnabled.class);

        if (runIfEnabled == null) {
            return true;
        }

        for (String pluginIdentifier : runIfEnabled.value()) {
            if (!PluginUtils.isEnabled(pluginIdentifier)) {
                return false;
            }
        }

        return true;
    }

    public List<String> getColumns(final GridComponent grid, final Class<?> clazz) {
        try {
            T service = getColumnsService(clazz);

            return service.getColumns(grid);
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
        }

        return Lists.newArrayList();
    }

}
