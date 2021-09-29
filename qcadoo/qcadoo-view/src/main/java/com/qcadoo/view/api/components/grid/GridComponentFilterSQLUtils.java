/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 * <p>
 * This file is part of Qcadoo.
 * <p>
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.view.api.components.grid;

import static com.qcadoo.view.internal.components.grid.GridComponentFilterOperator.ISNULL;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.view.internal.components.grid.GridComponentColumn;
import com.qcadoo.view.internal.components.grid.GridComponentFilterException;
import com.qcadoo.view.internal.components.grid.GridComponentFilterGroupOperator;
import com.qcadoo.view.internal.components.grid.GridComponentFilterOperator;
import com.qcadoo.view.internal.components.grid.GridComponentMultiSearchFilterRule;

public final class GridComponentFilterSQLUtils {

    private GridComponentFilterSQLUtils() {
    }

    public static String addFilters(final Map<String, String> filters, final Map<String, GridComponentColumn> columns,
            String table, final DataDefinition dataDefinition) throws GridComponentFilterException {
        StringBuilder filterQuery = new StringBuilder(" 1=1 ");

        for (Entry<String, String> filter : filters.entrySet()) {

            String field = getFieldNameByColumnName(columns, filter.getKey());

            if (field != null) {
                try {
                    FieldDefinition fieldDefinition = getFieldDefinition(dataDefinition, field);

                    Entry<GridComponentFilterOperator, String> filterValue = parseFilterValue(filter.getValue());

                    if ("".equals(filterValue.getValue()) && !ISNULL.equals(filterValue.getKey())) {
                        continue;
                    }

                    if (fieldDefinition != null && String.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addStringFilter(table, filterQuery, filterValue, field);
                    } else if (fieldDefinition != null && Boolean.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addSimpleFilter(table, filterQuery, filterValue, field, "1".equals(filterValue.getValue()));
                    } else if (fieldDefinition != null && Date.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addDateFilter(table, filterQuery, filterValue, field);
                    } else if (fieldDefinition != null
                            && BigDecimal.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addDecimalFilter(table, filterQuery, filterValue, field);
                    } else if (fieldDefinition != null && Integer.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addIntegerFilter(table, filterQuery, filterValue, field);
                    } else {
                        addSimpleFilter(table, filterQuery, filterValue, field, filterValue.getValue());
                    }
                } catch (ParseException pe) {
                    throw new GridComponentFilterException(filter.getValue());
                }
            }
        }

        return filterQuery.toString();
    }

    public static String addMultiSearchFilter(GridComponentMultiSearchFilter multiSearchFilter,
            Map<String, GridComponentColumn> columns, String table, DataDefinition dataDefinition)
            throws GridComponentFilterException {
        StringBuilder filterQuery = new StringBuilder(" 1=1 ");

        for (GridComponentMultiSearchFilterRule rule : multiSearchFilter.getRules()) {
            String field = getFieldNameByColumnName(columns, rule.getField());

            if (field != null) {
                try {
                    FieldDefinition fieldDefinition = getFieldDefinition(dataDefinition, field);

                    if ("".equals(rule.getData()) && !ISNULL.equals(rule.getFilterOperator())) {
                        continue;
                    }

                    if (filterQuery.length() > 5) {
                        if (multiSearchFilter.getGroupOperator() == GridComponentFilterGroupOperator.AND) {
                            filterQuery.append(GridComponentFilterGroupOperator.AND + " ");
                        } else if (multiSearchFilter.getGroupOperator() == GridComponentFilterGroupOperator.OR) {
                            filterQuery.append(GridComponentFilterGroupOperator.OR + " ");
                        }
                    } else {
                        filterQuery.append(GridComponentFilterGroupOperator.AND + " (");
                    }

                    if (fieldDefinition != null && String.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        filterQuery.append(createStringCriterion(table, rule.getFilterOperator(), rule.getData(), field));
                    } else if (fieldDefinition != null && Boolean.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        filterQuery.append(
                                createSimpleCriterion(table, rule.getFilterOperator(), "1".equals(rule.getData()), field));
                    } else if (fieldDefinition != null && Date.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        filterQuery.append(createDateCriterion(table, rule.getFilterOperator(), rule.getData(), field));
                    } else if (fieldDefinition != null
                            && BigDecimal.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        filterQuery.append(createDecimalCriterion(table, rule.getFilterOperator(), rule.getData(), field));
                    } else if (fieldDefinition != null && Integer.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        filterQuery.append(createIntegerCriterion(table, rule.getFilterOperator(), rule.getData(), field));
                    } else {
                        filterQuery.append(createSimpleCriterion(table, rule.getFilterOperator(), rule.getData(), field));
                    }

                } catch (Exception pe) {
                    throw new GridComponentFilterException(rule.getData());
                }
            }
        }
        if (filterQuery.length() > 5) {
            filterQuery.append(") ");
        }

        return filterQuery.toString();
    }

    private static String createSimpleCriterion(String table, GridComponentFilterOperator filterOperator, Object data,
            String field) {
        if (!field.contains(".")) {
            field = table + "." + field;
        }
        switch (filterOperator) {
            case EQ:
            case CN:
            case BW:
            case EW:
                return field + " = '" + data + "' ";
            case NE:
                return field + " <> '" + data + "' ";
            case GT:
                return field + " > '" + data + "' ";
            case GE:
                return field + " >= '" + data + "' ";
            case LT:
                return field + " < '" + data + "' ";
            case LE:
                return field + " <= '" + data + "' ";
            case ISNULL:
                return field + " IS NULL ";
            case IN:
                if (data instanceof Collection<?>) {
                    // TODO
                    return " 1=1";
                } else {
                    throw new IllegalStateException("Unknown filter value, collection required");
                }
            default:
                throw new IllegalStateException("Unknown filter operator");
        }
    }

    private static String createIntegerCriterion(String table, GridComponentFilterOperator filterOperator, String data,
            String field) throws GridComponentFilterException {
        try {
            final Object value;
            if (filterOperator == GridComponentFilterOperator.IN) {
                Collection<String> values = parseListValue(data);
                Collection<Integer> integerValues = Lists.newArrayListWithCapacity(values.size());
                for (String stringValue : values) {
                    integerValues.add(Integer.valueOf(stringValue));
                }
                value = integerValues;

            } else {
                value = Integer.valueOf(data);
            }

            return createSimpleCriterion(table, filterOperator, value, field);
        } catch (NumberFormatException nfe) {
            throw new GridComponentFilterException(data, nfe);
        }
    }

    private static String createDecimalCriterion(String table, GridComponentFilterOperator filterOperator, String data,
            String field) throws GridComponentFilterException {
        try {
            final Object value;
            if (filterOperator == GridComponentFilterOperator.IN) {
                Collection<String> values = parseListValue(data);
                Collection<BigDecimal> decimalValues = Lists.newArrayListWithCapacity(values.size());
                for (String stringValue : values) {
                    decimalValues.add(new BigDecimal(stringValue));
                }
                value = decimalValues;

            } else {
                value = new BigDecimal(data);
            }

            return createSimpleCriterion(table, filterOperator, value, field);
        } catch (NumberFormatException nfe) {
            throw new GridComponentFilterException(data, nfe);
        }
    }

    private static String createDateCriterion(String table, GridComponentFilterOperator filterOperator, String data, String field)
            throws ParseException {
        if (filterOperator == GridComponentFilterOperator.IN) {
            Collection<String> values = parseListValue(data);
            Collection<Date> dates = Lists.newArrayListWithCapacity(values.size());
            for (String value : values) {
                dates.add(DateUtils.parseDate(value));
            }
            return "";
        }

        if (!field.contains(".")) {
            field = table + "." + field;
        }

        Date minDate = null;
        Date maxDate = null;

        if (!ISNULL.equals(filterOperator)) {
            minDate = DateUtils.parseAndComplete(data, false);
            maxDate = DateUtils.parseAndComplete(data, true);
        }

        switch (filterOperator) {
            case EQ:
            case CN:
            case BW:
            case EW:
                return field + " between '" + DateUtils.toDateTimeString(minDate) + "' and '"
                        + DateUtils.toDateTimeString(maxDate) + "' ";
            case NE:
                return field + " not between '" + DateUtils.toDateTimeString(minDate) + "' and '"
                        + DateUtils.toDateTimeString(maxDate) + "' ";
            case GT:
                return field + " > '" + DateUtils.toDateTimeString(maxDate) + "' ";
            case GE:
                return field + " >= '" + DateUtils.toDateTimeString(minDate) + "' ";
            case LT:
                return field + " < '" + DateUtils.toDateTimeString(minDate) + "' ";
            case LE:
                return field + " >= '" + DateUtils.toDateTimeString(maxDate) + "' ";
            case ISNULL:
                return field + " IS NULL ";
            default:
                throw new IllegalStateException("Unknown filter operator");
        }
    }

    private static String createStringCriterion(String table, GridComponentFilterOperator filterOperator, String data,
            String field) {
        if (!field.contains(".")) {
            field = table + "." + field;
        }
        switch (filterOperator) {
            case EQ:
            case LE:
            case GE:
                return field + " = '" + data + "' ";
            case CN:
                return field + " ilike '%" + data + "%' ";
            case BW:
                return field + " ilike '" + data + "%' ";
            case EW:
                return field + " ilike '%" + data + "' ";
            case IN:
                Collection<String> values = parseListValue(data);
                return "lower(" + field + ") in (" + convertToIn(values) + ") ";
            case CIN:
                StringBuilder sb = new StringBuilder("(");
                Collection<String> cinValues = parseListValue(data);
                final String finalField = field;
                String collected = cinValues.stream().map(s -> finalField + " ilike '%" + s + "%'")
                        .collect(Collectors.joining(" OR "));
                sb.append(collected).append(") ");
                return sb.toString();
            case NE:
            case GT:
            case LT:
                return field + " <> '" + data + "' ";
            case ISNULL:
                return field + " IS NULL ";
            default:
                throw new IllegalStateException("Unknown filter operator");
        }
    }

    private static String convertToIn(final Collection<String> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach(v -> joinToIn(v.toLowerCase(), builder));
        return builder.toString();
    }

    private static void joinToIn(final String v, final StringBuilder builder) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append("'");
        builder.append(v);
        builder.append("'");
    }

    private static Collection<String> parseListValue(String data) {
        String[] tokens = data.split(",");
        Collection<String> values = Lists.newArrayListWithCapacity(tokens.length);
        for (int i = 0; i < tokens.length; ++i) {
            values.add(tokens[i].trim());
        }
        return values;
    }

    private static void addIntegerFilter(String table, StringBuilder filterQuery,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field)
            throws GridComponentFilterException {
        filterQuery.append(GridComponentFilterGroupOperator.AND + " ");
        filterQuery.append(createIntegerCriterion(table, filterValue.getKey(), filterValue.getValue(), field));
    }

    private static void addDecimalFilter(String table, StringBuilder filterQuery,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field)
            throws GridComponentFilterException {
        filterQuery.append(GridComponentFilterGroupOperator.AND + " ");
        filterQuery.append(createDecimalCriterion(table, filterValue.getKey(), filterValue.getValue(), field));
    }

    private static void addSimpleFilter(String table, StringBuilder filterQuery,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field, final Object value) {
        filterQuery.append(GridComponentFilterGroupOperator.AND + " ");
        filterQuery.append(createSimpleCriterion(table, filterValue.getKey(), value, field));
    }

    private static void addStringFilter(final String table, final StringBuilder filterQuery,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field) {
        String value = filterValue.getValue();

        GridComponentFilterOperator operator = filterValue.getKey();
        if (filterValue.getKey() == GridComponentFilterOperator.EQ) {
            operator = GridComponentFilterOperator.CN;
        }
        filterQuery.append(GridComponentFilterGroupOperator.AND + " ");
        filterQuery.append(createStringCriterion(table, operator, value, field));
    }

    private static void addDateFilter(String table, final StringBuilder filterQuery,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field) throws ParseException {
        filterQuery.append(GridComponentFilterGroupOperator.AND + " ");
        filterQuery.append(createDateCriterion(table, filterValue.getKey(), filterValue.getValue(), field));
    }

    private static Entry<GridComponentFilterOperator, String> parseFilterValue(final String filterValue) {
        GridComponentFilterOperator operator = GridComponentFilterOperator.EQ;
        String value;
        if (filterValue.charAt(0) == '>') {
            if (filterValue.length() > 1 && filterValue.charAt(1) == '=') {
                operator = GridComponentFilterOperator.GE;
                value = filterValue.substring(2);
            } else if (filterValue.length() > 1 && filterValue.charAt(1) == '<') {
                operator = GridComponentFilterOperator.NE;
                value = filterValue.substring(2);
            } else {
                operator = GridComponentFilterOperator.GT;
                value = filterValue.substring(1);
            }
        } else if (filterValue.charAt(0) == '<') {
            if (filterValue.length() > 1 && filterValue.charAt(1) == '=') {
                operator = GridComponentFilterOperator.LE;
                value = filterValue.substring(2);
            } else if (filterValue.length() > 1 && filterValue.charAt(1) == '>') {
                operator = GridComponentFilterOperator.NE;
                value = filterValue.substring(2);
            } else {
                operator = GridComponentFilterOperator.LT;
                value = filterValue.substring(1);
            }
        } else if (filterValue.charAt(0) == '=') {
            if (filterValue.length() > 1 && filterValue.charAt(1) == '<') {
                operator = GridComponentFilterOperator.LE;
                value = filterValue.substring(2);
            } else if (filterValue.length() > 1 && filterValue.charAt(1) == '>') {
                operator = GridComponentFilterOperator.GE;
                value = filterValue.substring(2);
            } else if (filterValue.length() > 1 && filterValue.charAt(1) == '=') {
                value = filterValue.substring(2);
            } else {
                value = filterValue.substring(1);
            }
        } else if (filterValue.charAt(0) == '[' && filterValue.charAt(filterValue.length() - 1) == ']') {
            operator = GridComponentFilterOperator.IN;
            value = filterValue.substring(1, filterValue.length() - 1);
        } else if (filterValue.charAt(0) == '{' && filterValue.charAt(filterValue.length() - 1) == '}') {
            operator = GridComponentFilterOperator.CIN;
            value = filterValue.substring(1, filterValue.length() - 1);
        } else if (ISNULL.name().equals(filterValue.toUpperCase())) {
            operator = GridComponentFilterOperator.ISNULL;
            value = "";
        } else {
            value = filterValue;
        }
        return Collections.singletonMap(operator, value.trim()).entrySet().iterator().next();
    }

    protected static FieldDefinition getFieldDefinition(DataDefinition dataDefinition, final String field) {
        String[] path = field.split("\\.");

        for (int i = 0; i < path.length; i++) {

            if (dataDefinition.getField(path[i]) == null) {
                return null;
            }

            FieldDefinition fieldDefinition = dataDefinition.getField(path[i]);

            if (i < path.length - 1) {
                if (fieldDefinition.getType() instanceof BelongsToType) {
                    dataDefinition = ((BelongsToType) fieldDefinition.getType()).getDataDefinition();
                    continue;
                } else {
                    return null;
                }
            }

            return fieldDefinition;
        }

        return null;
    }

    public static String getFieldNameByColumnName(final Map<String, GridComponentColumn> columns, final String columnName) {
        GridComponentColumn column = columns.get(columnName);
        if (column == null) {
            return null;
        }

        final String expression = column.getExpression();
        if (StringUtils.isNotBlank(expression)) {
            return getFieldNameFromExpression(expression);
        } else if (column.getFields().size() == 1) {
            return column.getFields().get(0).getName();
        }
        return null;
    }

    private static String getFieldNameFromExpression(final String expression) {
        String pattern = "#(\\w+)(\\['(\\w+)'\\])?([[?]?.[get|getStringField|getBooleanField|getDecimalField|getIntegerField|getDateField|getBelongsToField]\\('\\w+'\\)]*)";
        Matcher matcher = Pattern.compile(pattern).matcher(StringUtils.trim(expression));
        if (matcher.matches()) {
            final StringBuilder fieldNameBuilder = new StringBuilder(matcher.group(1));
            if (StringUtils.isNotBlank(matcher.group(3))) {
                fieldNameBuilder.append(".");
                fieldNameBuilder.append(matcher.group(3));
            }
            if (StringUtils.isNotBlank(matcher.group(4))) {
                final String[] searchList = new String[] { "get('", "?.get('", "getStringField('", "?.getStringField('",
                        "getBooleanField('", "?.getBooleanField('", "getDecimalField('", "?.getDecimalField('",
                        "getIntegerField('", "?.getIntegerField('", "getDateField('", "?.getDateField('", "getBelongsToField('",
                        "?.getBelongsToField('", "')" };
                final String[] replacementList = new String[] { "", ".", "", ".", "", ".", "", ".", "", ".", "", ".", "", ".",
                        "" };

                fieldNameBuilder.append(StringUtils.replaceEach(matcher.group(4), searchList, replacementList));
            }
            return fieldNameBuilder.toString();
        }
        return null;
    }

}
