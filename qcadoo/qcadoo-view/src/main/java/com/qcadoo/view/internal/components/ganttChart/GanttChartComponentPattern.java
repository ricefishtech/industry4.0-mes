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
package com.qcadoo.view.internal.components.ganttChart;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.components.ganttChart.GanttChartItemResolver;
import com.qcadoo.view.api.components.ganttChart.GanttChartItemStrip;
import com.qcadoo.view.api.components.ganttChart.GanttChartItemStrip.Orientation;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.components.ganttChart.GanttChartScaleImpl.ZoomLevel;
import com.qcadoo.view.internal.patterns.AbstractComponentPattern;

public class GanttChartComponentPattern extends AbstractComponentPattern {

    private static final String JS_OBJECT = "QCD.components.elements.GanttChart";

    private static final String JSP_PATH = "elements/ganttChart.jsp";

    private String resolver;

    private GanttChartItemStrip.Orientation stripOrientation;

    private int defaultStartDay = 0;

    private int defaultEndDay = 21;

    private boolean allowDateSelection = true;

    private boolean hasPopupInfo = true;

    private ZoomLevel defaultZoomLevel = ZoomLevel.H3;

    private int itemsBorderWidth;

    private String itemsBorderColor;

    public GanttChartComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    protected ComponentState getComponentStateInstance() {
        return new GanttChartComponentState(getResolver(resolver), this);
    }

    private GanttChartItemResolver getResolver(final String resolver) {
        try {
            return (GanttChartItemResolver) getApplicationContext().getBean(
                    Thread.currentThread().getContextClassLoader().loadClass(resolver));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    protected void initializeComponent() throws JSONException {
        for (ComponentOption option : getOptions()) {
            if ("resolver".equals(option.getType())) {
                resolver = option.getValue();
            } else if ("defaultZoomLevel".equals(option.getType())) {
                defaultZoomLevel = ZoomLevel.valueOf(option.getValue());
            } else if ("defaultStartDay".equals(option.getType())) {
                defaultStartDay = Integer.valueOf(option.getValue());
            } else if ("defaultEndDay".equals(option.getType())) {
                defaultEndDay = Integer.valueOf(option.getValue());
            } else if ("hasPopupInfo".equals(option.getType())) {
                hasPopupInfo = Boolean.valueOf(option.getValue());
            } else if ("allowDateSelection".equals(option.getType())) {
                allowDateSelection = Boolean.valueOf(option.getValue());
            } else if ("stripsOrientation".equals(option.getType())) {
                stripOrientation = Orientation.parseString(option.getValue());
            } else if ("itemsBorderWidth".equals(option.getType())) {
                itemsBorderWidth = Integer.valueOf(option.getValue());
            } else if ("itemsBorderColor".equals(option.getType())) {
                itemsBorderColor = option.getValue();
            }
        }
        if (resolver == null) {
            throw new IllegalStateException("Gantt must contain 'resolver' option");
        }
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject translations = new JSONObject();
        addTranslation(translations, "header.label", locale);
        addTranslation(translations, "header.dateFrom", locale);
        addTranslation(translations, "header.dateTo", locale);
        addTranslation(translations, "header.zoom1h", locale);
        addTranslation(translations, "header.zoom3h", locale);
        addTranslation(translations, "header.zoom6h", locale);
        addTranslation(translations, "header.zoom1d", locale);

        addTranslation(translations, "description.dateFrom", locale);
        addTranslation(translations, "description.dateTo", locale);

        addTranslation(translations, "colisionElementName", locale);
        addTranslation(translations, "colisionBox.header", locale);
        addTranslation(translations, "colisionBox.closeButton", locale);

        JSONObject json = super.getJsOptions(locale);
        json.put("translations", translations);

        json.put("hasPopupInfo", hasPopupInfo);
        json.put("allowDateSelection", allowDateSelection);

        return json;
    }

    private void addTranslation(final JSONObject translation, final String key, final Locale locale) throws JSONException {
        translation.put(key,
                getTranslationService().translate(getTranslationPath() + "." + key, "qcadooView.gantt." + key, locale));
    }

    @Override
    protected String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    protected String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    protected String getJsObjectName() {
        return JS_OBJECT;
    }

    public final int getDefaultStartDay() {
        return defaultStartDay;
    }

    public final int getDefaultEndDay() {
        return defaultEndDay;
    }

    public final ZoomLevel getDefaultZoomLevel() {
        return defaultZoomLevel;
    }

    public final Orientation getStripOrientation() {
        return stripOrientation;
    }

    public int getItemsBorderWidth() {
        return itemsBorderWidth;
    }

    public String getItemsBorderColor() {
        return itemsBorderColor;
    }

}
