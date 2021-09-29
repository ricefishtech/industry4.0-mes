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

import static com.qcadoo.view.internal.components.grid.GridComponentFilterOperator.ISNULL;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.JoinType;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchDisjunction;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchRestrictions.SearchMatchMode;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.view.api.components.grid.GridComponentMultiSearchFilter;

public final class GridComponentFilterUtils {

    private GridComponentFilterUtils() {
    }

    public static void addFilters(final Map<String, String> filters, final Map<String, GridComponentColumn> columns,
            final DataDefinition dataDefinition, final SearchCriteriaBuilder criteria) throws GridComponentFilterException {
        for (Map.Entry<String, String> filter : filters.entrySet()) {

            String field = getFieldNameByColumnName(columns, filter.getKey());

            if (field != null) {
                try {
                    FieldDefinition fieldDefinition = getFieldDefinition(dataDefinition, field);

                    Map.Entry<GridComponentFilterOperator, String> filterValue = parseFilterValue(filter.getValue());

                    if ("".equals(filterValue.getValue()) && !ISNULL.equals(filterValue.getKey())) {
                        continue;
                    }

                    field = addAliases(criteria, field, JoinType.LEFT);

                    if (fieldDefinition != null && String.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addStringFilter(criteria, filterValue, field);
                    } else if (fieldDefinition != null && Boolean.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addSimpleFilter(criteria, filterValue, field, "1".equals(filterValue.getValue()));
                    } else if (fieldDefinition != null && Date.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addDateFilter(criteria, filterValue, field);
                    } else if (fieldDefinition != null && BigDecimal.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addDecimalFilter(criteria, filterValue, field);
                    } else if (fieldDefinition != null && Integer.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        addIntegerFilter(criteria, filterValue, field);
                    } else {
                        addSimpleFilter(criteria, filterValue, field, filterValue.getValue());
                    }
                } catch (ParseException pe) {
                    throw new GridComponentFilterException(filter.getValue());
                }
            }
        }
    }

    public static void addMultiSearchFilter(GridComponentMultiSearchFilter multiSearchFilter,
            Map<String, GridComponentColumn> columns, DataDefinition dataDefinition, SearchCriteriaBuilder criteria)
            throws GridComponentFilterException {

        LinkedList<SearchCriterion> searchRules = Lists.newLinkedList();
        for (GridComponentMultiSearchFilterRule rule : multiSearchFilter.getRules()) {
            String field = getFieldNameByColumnName(columns, rule.getField());

            if (field != null) {
                try {
                    FieldDefinition fieldDefinition = getFieldDefinition(dataDefinition, field);

                    if ("".equals(rule.getData()) && !ISNULL.equals(rule.getFilterOperator())) {
                        continue;
                    }

                    field = addAliases(criteria, field, JoinType.LEFT);

                    SearchCriterion searchRule;
                    if (fieldDefinition != null && String.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        searchRule = createStringCriterion(rule.getFilterOperator(), rule.getData(), field);
                    } else if (fieldDefinition != null && Boolean.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        searchRule = createSimpleCriterion(rule.getFilterOperator(), "1".equals(rule.getData()), field);
                    } else if (fieldDefinition != null && Date.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        searchRule = createDateCriterion(rule.getFilterOperator(), rule.getData(), field);
                    } else if (fieldDefinition != null && BigDecimal.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        searchRule = createDecimalCriterion(rule.getFilterOperator(), rule.getData(), field);
                    } else if (fieldDefinition != null && Integer.class.isAssignableFrom(fieldDefinition.getType().getType())) {
                        searchRule = createIntegerCriterion(rule.getFilterOperator(), rule.getData(), field);
                    } else {
                        searchRule = createSimpleCriterion(rule.getFilterOperator(), rule.getData(), field);
                    }

                    searchRules.add(searchRule);
                } catch (Exception pe) {
                    throw new GridComponentFilterException(rule.getData());
                }
            }
        }

        SearchCriterion groupedRules = null;
        if (searchRules.size() == 1) {
            groupedRules = searchRules.pollFirst();
        } else if (searchRules.size() == 2) {
            if (multiSearchFilter.getGroupOperator() == GridComponentFilterGroupOperator.AND) {
                groupedRules = SearchRestrictions.and(searchRules.pollFirst(), searchRules.pollFirst());
            } else if (multiSearchFilter.getGroupOperator() == GridComponentFilterGroupOperator.OR) {
                groupedRules = SearchRestrictions.or(searchRules.pollFirst(), searchRules.pollFirst());
            }
        } else if (searchRules.size() > 2) {
            SearchCriterion firstRule = searchRules.pollFirst();
            SearchCriterion secondRule = searchRules.pollFirst();
            SearchCriterion[] otherRules = new SearchCriterion[searchRules.size()];
            searchRules.toArray(otherRules);
            if (multiSearchFilter.getGroupOperator() == GridComponentFilterGroupOperator.AND) {
                groupedRules = SearchRestrictions.and(firstRule, secondRule, otherRules);
            } else if (multiSearchFilter.getGroupOperator() == GridComponentFilterGroupOperator.OR) {
                groupedRules = SearchRestrictions.or(firstRule, secondRule, otherRules);
            }
        }
        if (groupedRules != null) {
            criteria.add(groupedRules);
        }

    }

    private static SearchCriterion createSimpleCriterion(GridComponentFilterOperator filterOperator, Object data, String field) {
        switch (filterOperator) {
            case EQ:
            case CN:
            case BW:
            case EW:
                return SearchRestrictions.eq(field, data);
            case NE:
                return SearchRestrictions.ne(field, data);
            case GT:
                return SearchRestrictions.gt(field, data);
            case GE:
                return SearchRestrictions.ge(field, data);
            case LT:
                return SearchRestrictions.lt(field, data);
            case LE:
                return SearchRestrictions.le(field, data);
            case ISNULL:
                return SearchRestrictions.isNull(field);
            case IN:
                if (data instanceof Collection<?>) {
                    return SearchRestrictions.in(field, (Collection<?>) data);
                } else {
                    throw new IllegalStateException("Unknown filter value, collection required");
                }
            default:
                throw new IllegalStateException("Unknown filter operator");
        }
    }

    private static SearchCriterion createIntegerCriterion(GridComponentFilterOperator filterOperator, String data, String field)
            throws GridComponentFilterException {
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

            return createSimpleCriterion(filterOperator, value, field);
        } catch (NumberFormatException nfe) {
            throw new GridComponentFilterException(data, nfe);
        }
    }

    private static SearchCriterion createDecimalCriterion(GridComponentFilterOperator filterOperator, String data, String field)
            throws GridComponentFilterException {
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

            return createSimpleCriterion(filterOperator, value, field);
        } catch (NumberFormatException nfe) {
            throw new GridComponentFilterException(data, nfe);
        }
    }

    private static SearchCriterion createDateCriterion(GridComponentFilterOperator filterOperator, String data, String field)
            throws ParseException {
        if (filterOperator == GridComponentFilterOperator.IN) {
            Collection<String> values = parseListValue(data);
            Collection<Date> dates = Lists.newArrayListWithCapacity(values.size());
            for (String value : values) {
                dates.add(DateUtils.parseDate(value));
            }
            return SearchRestrictions.in(field, dates);
        }

        Date minDate = null;
        Date maxDate = null;

        if(!ISNULL.equals(filterOperator)){
            minDate = DateUtils.parseAndComplete(data, false);
            maxDate = DateUtils.parseAndComplete(data, true);
        }

        switch (filterOperator) {
            case EQ:
            case CN:
            case BW:
            case EW:
                return SearchRestrictions.between(field, minDate, maxDate);
            case NE:
                return SearchRestrictions.not(SearchRestrictions.between(field, minDate, maxDate));
            case GT:
                return SearchRestrictions.gt(field, maxDate);
            case GE:
                return SearchRestrictions.ge(field, minDate);
            case LT:
                return SearchRestrictions.lt(field, minDate);
            case LE:
                return SearchRestrictions.le(field, maxDate);
            case ISNULL:
                return SearchRestrictions.isNull(field);
            default:
                throw new IllegalStateException("Unknown filter operator");
        }
    }

    private static SearchCriterion createStringCriterion(GridComponentFilterOperator filterOperator, String data, String field) {
        switch (filterOperator) {
            case EQ:
            case LE:
            case GE:
                return SearchRestrictions.eq(field, data);
            case CN:
                return SearchRestrictions.ilike(field, data, SearchMatchMode.ANYWHERE);
            case BW:
                return SearchRestrictions.ilike(field, data, SearchMatchMode.START);
            case EW:
                return SearchRestrictions.ilike(field, data, SearchMatchMode.END);
            case IN:
                Collection<String> values = parseListValue(data);
                return SearchRestrictions.inIgnoringCase(field, values);
            case CIN:
                SearchDisjunction disjunction = SearchRestrictions.disjunction();
                parseListValue(data)
                        .forEach(value -> disjunction.add(SearchRestrictions.ilike(field, value, SearchMatchMode.ANYWHERE)));
                return disjunction;
            case NE:
            case GT:
            case LT:
                return SearchRestrictions.ne(field, data);
            case ISNULL:
                return SearchRestrictions.isNull(field);
            default:
                throw new IllegalStateException("Unknown filter operator");
        }
    }

    private static Collection<String> parseListValue(String data) {
        String[] tokens = data.split(",");
        Collection<String> values = Lists.newArrayListWithCapacity(tokens.length);
        for (int i = 0; i < tokens.length; ++i) {
            values.add(tokens[i].trim());
        }
        return values;
    }

    public static String addAliases(final SearchCriteriaBuilder criteria, final String field, final JoinType joinType) {
        if (field == null) {
            return null;
        }

        String[] path = field.split("\\.");

        if (path.length == 1) {
            return field;
        }

        String lastAlias = "";

        for (int i = 0; i < path.length - 1; i++) {
            criteria.createAlias(lastAlias + path[i], path[i] + "_a", joinType);
            lastAlias = path[i] + "_a.";
        }

        return lastAlias + path[path.length - 1];
    }

    private static void addIntegerFilter(final SearchCriteriaBuilder criteria,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field) throws GridComponentFilterException {

        criteria.add(createIntegerCriterion(filterValue.getKey(), filterValue.getValue(), field));
    }

    private static void addDecimalFilter(final SearchCriteriaBuilder criteria,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field) throws GridComponentFilterException {

        criteria.add(createDecimalCriterion(filterValue.getKey(), filterValue.getValue(), field));
    }

    private static void addSimpleFilter(final SearchCriteriaBuilder criteria,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field, final Object value) {

        criteria.add(createSimpleCriterion(filterValue.getKey(), value, field));
    }

    private static void addStringFilter(final SearchCriteriaBuilder criteria,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field) {
        String value = filterValue.getValue();

        GridComponentFilterOperator operator = filterValue.getKey();
        if (filterValue.getKey() == GridComponentFilterOperator.EQ) {
            operator = GridComponentFilterOperator.CN;
        }

        criteria.add(createStringCriterion(operator, value, field));

    }

    private static void addDateFilter(final SearchCriteriaBuilder criteria,
            final Entry<GridComponentFilterOperator, String> filterValue, final String field) throws ParseException {

        criteria.add(createDateCriterion(filterValue.getKey(), filterValue.getValue(), field));
    }

    private static Map.Entry<GridComponentFilterOperator, String> parseFilterValue(final String filterValue) {
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
        } else if (ISNULL.name().equals(filterValue.toUpperCase())) {
            operator = GridComponentFilterOperator.ISNULL;
            value = "";
        } else if (filterValue.charAt(0) == '{' && filterValue.charAt(filterValue.length() - 1) == '}') {
            operator = GridComponentFilterOperator.CIN;
            value = filterValue.substring(1, filterValue.length() - 1);
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
                final String[] replacementList = new String[] { "", ".", "", ".", "", ".", "", ".", "", ".", "", ".", "", ".", "" };

                fieldNameBuilder.append(StringUtils.replaceEach(matcher.group(4), searchList, replacementList));
            }
            return fieldNameBuilder.toString();
        }
        return null;
    }

}
