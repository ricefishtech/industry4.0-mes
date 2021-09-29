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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.model.internal.types.DateType;
import com.qcadoo.view.api.components.ganttChart.GanttChartItem;
import com.qcadoo.view.api.components.ganttChart.GanttChartItemTooltip;
import com.qcadoo.view.api.components.ganttChart.GanttChartItemTooltipBuilder;
import com.qcadoo.view.api.components.ganttChart.GanttChartScale;

public class GanttChartScaleImpl implements GanttChartScale {

    private final GanttChartComponentState ganttChartComponentState;

    private final GanttChartItemFactory ganttChartItemFactory;

    private static final DateType DATETYPE = new DateType();

    private final ZoomLevel zoomLevel;

    private Date dateFrom;

    private Date dateTo;

    private Boolean isDatesSet;

    private static final String JSON_ELEMENTS_IN_CATEGORY = "elementsInCategory";

    private static final String JSON_ELEMENT_LABELS_INTERVAL = "elementLabelsInterval";

    private static final String JSON_ELEMENT_LABEL_INITIAL_NUMBER = "elementLabelInitialNumber";

    private static final String JSON_CATEGORIES = "categories";

    public enum ZoomLevel {
        H1(1, 1), H3(3, 3), H6(6, 6), D1(24, 24);

        private final int maxRangeInMonths;

        private final int hoursInterval;

        private ZoomLevel(final int maxRangeInMonths, final int hoursInterval) {
            this.maxRangeInMonths = maxRangeInMonths;
            this.hoursInterval = hoursInterval;
        }

        public int getHoursInterval() {
            return hoursInterval;
        }

        public int getMaxRangeInMonths() {
            return maxRangeInMonths;
        }
    }

    public GanttChartScaleImpl(final GanttChartComponentState ganttChartComponentState, final ZoomLevel zoomLevel,
            final Date dateFrom, final Date dateTo) {
        this.ganttChartComponentState = ganttChartComponentState;
        this.zoomLevel = zoomLevel;
        this.dateFrom = new Date(dateFrom.getTime());
        this.dateTo = new DateTime(dateTo).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).toDate();
        this.ganttChartItemFactory = new GanttChartItemFactory(zoomLevel.getHoursInterval());
    }

    public JSONObject getAsJson() throws JSONException {
        JSONObject scaleObject = new JSONObject();
        JSONArray categoriesArray = null;
        switch (zoomLevel) {
            case H1:
                categoriesArray = getDaysArray();
                scaleObject.put(JSON_ELEMENTS_IN_CATEGORY, 24);
                scaleObject.put(JSON_ELEMENT_LABELS_INTERVAL, 1);
                scaleObject.put(JSON_ELEMENT_LABEL_INITIAL_NUMBER, 0);
                break;
            case H3:
                categoriesArray = getDaysArray();
                scaleObject.put(JSON_ELEMENTS_IN_CATEGORY, 8);
                scaleObject.put(JSON_ELEMENT_LABELS_INTERVAL, 3);
                scaleObject.put(JSON_ELEMENT_LABEL_INITIAL_NUMBER, 0);
                break;
            case H6:
                categoriesArray = getDaysArray();
                scaleObject.put(JSON_ELEMENTS_IN_CATEGORY, 4);
                scaleObject.put(JSON_ELEMENT_LABELS_INTERVAL, 6);
                scaleObject.put(JSON_ELEMENT_LABEL_INITIAL_NUMBER, 0);
                break;
            case D1:
                return getWeeksScale();

            default:
                break;
        }
        scaleObject.put(JSON_CATEGORIES, categoriesArray);
        return scaleObject;
    }

    private JSONArray getDaysArray() {
        JSONArray daysArray = new JSONArray();
        DateTime dateTimeFrom = new DateTime(dateFrom);
        DateTime dateTimeTo = new DateTime(dateTo);
        while (dateTimeFrom.compareTo(dateTimeTo) <= 0) {
            daysArray.put(DATETYPE.toString(dateTimeFrom.toDate(), ganttChartComponentState.getLocale()));
            dateTimeFrom = dateTimeFrom.plusDays(1);
        }
        return daysArray;
    }

    private JSONObject getWeeksScale() throws JSONException {

        DateTime dateTimeFrom = new DateTime(dateFrom);
        DateTime dateTimeTo = new DateTime(dateTo);
        int dateTimeFromDayOfWeek = dateTimeFrom.getDayOfWeek();
        int dateTimeToDayOfWeek = dateTimeTo.getDayOfWeek();

        List<Integer> weekNumbers = new ArrayList<Integer>();

        int lastAddedWeekNumber = 0;
        while (dateTimeFrom.compareTo(dateTimeTo) < 0) {
            lastAddedWeekNumber = dateTimeFrom.getWeekOfWeekyear();
            weekNumbers.add(lastAddedWeekNumber);
            dateTimeFrom = dateTimeFrom.plusWeeks(1);
        }
        if (dateTimeTo.getWeekOfWeekyear() > lastAddedWeekNumber) {
            lastAddedWeekNumber = dateTimeTo.getWeekOfWeekyear();
            weekNumbers.add(lastAddedWeekNumber);
        }

        JSONArray weeksArray = new JSONArray();
        for (int i = 0; i < weekNumbers.size(); i++) {
            if (i == 0 && dateTimeFromDayOfWeek > 5) {
                weeksArray.put(weekNumbers.get(i).toString());
            } else if (i == weekNumbers.size() - 1 && dateTimeToDayOfWeek < 3) {
                weeksArray.put(weekNumbers.get(i).toString());
            } else {
                weeksArray.put(ganttChartComponentState.translate("week") + " " + weekNumbers.get(i));
            }

        }

        JSONObject scaleObject = new JSONObject();
        JSONArray weekDays = new JSONArray();
        weekDays.put(ganttChartComponentState.translate("weekDay.short.monday"));
        weekDays.put(ganttChartComponentState.translate("weekDay.short.tuesday"));
        weekDays.put(ganttChartComponentState.translate("weekDay.short.wensday"));
        weekDays.put(ganttChartComponentState.translate("weekDay.short.thursday"));
        weekDays.put(ganttChartComponentState.translate("weekDay.short.friday"));
        weekDays.put(ganttChartComponentState.translate("weekDay.short.saturday"));
        weekDays.put(ganttChartComponentState.translate("weekDay.short.sunday"));
        scaleObject.put(JSON_ELEMENTS_IN_CATEGORY, 7);
        scaleObject.put("elementLabelsValues", weekDays);
        scaleObject.put("firstCategoryFirstElement", dateTimeFromDayOfWeek);
        scaleObject.put("lastCategoryLastElement", dateTimeToDayOfWeek);

        scaleObject.put(JSON_CATEGORIES, weeksArray);

        return scaleObject;
    }

    public boolean isFromLargerThanTo() {
        return (dateFrom != null && dateTo != null && dateFrom.compareTo(dateTo) > 0);
    }

    public boolean isTooLargeRange() {
        DateTime dateTimeFrom = new DateTime(dateFrom);
        DateTime dateTimeTo = new DateTime(dateTo);
        return (dateTimeFrom.plusMonths(zoomLevel.getMaxRangeInMonths()).compareTo(dateTimeTo) < 0);
    }

    public int getMaxRangeInMonths() {
        return zoomLevel.getMaxRangeInMonths();
    }

    public ZoomLevel getZoomLevel() {
        return zoomLevel;
    }

    @Override
    public Date getDateTo() {
        if (dateTo == null) {
            return null;
        }
        return new Date(dateTo.getTime());
    }

    @Override
    public Date getDateFrom() {
        if (dateFrom == null) {
            return null;
        }
        return new Date(dateFrom.getTime());
    }

    @Override
    public void setDateFrom(final Date dateFrom) {
        this.dateFrom = new DateTime(dateFrom).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
                .toDate();
    }

    @Override
    public void setDateTo(final Date dateTo) {
        this.dateTo = new DateTime(dateTo).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
                .toDate();
    }

    @Override
    public GanttChartItem createGanttChartItem(final String rowName, final String name, final Long entityId,
            final Date itemDateFrom, final Date itemDateTo) {
        return createGanttChartItem(rowName, name, new GanttChartItemTooltipBuilder().withHeader(name).build(), entityId,
                itemDateFrom, itemDateTo);
    }

    @Override
    public GanttChartItem createGanttChartItem(final String rowName, final String label, final GanttChartItemTooltip tooltip,
            final Long entityId, final Date itemDateFrom, final Date itemDateTo) {
        return ganttChartItemFactory.createGanttChartItem(rowName, label, tooltip, entityId, dateFrom, dateTo, itemDateFrom,
                itemDateTo);
    }

    public Boolean getIsDatesSet() {
        return isDatesSet;
    }

    public void setIsDatesSet(final Boolean isDatesSet) {
        this.isDatesSet = isDatesSet;
    }

}
