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
package com.qcadoo.view.internal.components.grid;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.qcadoo.localization.api.utils.DateUtils;

public class PredefinedFilter {

    private static final Function<String, String> PARSE_FILTER_VALUE_FUNC = new Function<String, String>() {

        @Override
        public String apply(final String rawFilterValue) {
            return parseRestriction(rawFilterValue);
        }

    };

    private String name;

    private Map<String, String> filterRestrictions = new HashMap<String, String>();

    private String orderColumn;

    private String orderDirection;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Map<String, String> getFilterRestrictions() {
        return filterRestrictions;
    }

    public Map<String, String> getParsedFilterRestrictions() {
        return Maps.transformValues(filterRestrictions, PARSE_FILTER_VALUE_FUNC);
    }

    public void addFilterRestriction(final String column, final String restriction) {
        filterRestrictions.put(column, restriction);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("label", name);
        object.put("orderColumn", orderColumn);
        object.put("orderDirection", orderDirection);
        object.put("filter", new JSONObject(getParsedFilterRestrictions()));
        return object;
    }

    private static String parseRestriction(final String restriction) {
        Pattern p = Pattern.compile("@\\{.*?\\}");
        Matcher m = p.matcher(restriction);
        int lastEnd = 0;
        StringBuilder result = new StringBuilder();
        while (m.find()) {
            String expression = restriction.substring(m.start() + 2, m.end() - 1);
            result.append(restriction.substring(lastEnd, m.start()));
            result.append(evalExpression(expression));
            lastEnd = m.end();
        }
        if (lastEnd > 0) {
            return result.toString();
        } else {
            return restriction;
        }
    }

    private static String evalExpression(final String expression) {
        DateTime today = new DateTime();
        DateTime date;
        if ("today".equals(expression)) {
            date = today;
        } else if ("yesterday".equals(expression)) {
            date = today.minusDays(1);
        } else if ("tomorrow".equals(expression)) {
            date = today.plusDays(1);
        } else {
            throw new IllegalStateException("unsupported predefined filter expression: '" + expression + "'");
        }
        return new SimpleDateFormat(DateUtils.L_DATE_FORMAT, getLocale()).format(date.toDate());
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(final String orderColumn) {
        this.orderColumn = orderColumn;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(final String orderDirection) {
        this.orderDirection = orderDirection;
    }
}
