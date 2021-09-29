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
package com.qcadoo.model.internal.search;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchQueryBuilder;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.internal.api.DataAccessService;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchQueryImpl implements SearchQuery {

    private final Pattern pattern = Pattern.compile("#([a-zA-Z0-9]+)_([a-zA-Z0-9]+)");

    private final Map<String, String> strings = new HashMap<String, String>();

    private final Map<String, Boolean> booleans = new HashMap<String, Boolean>();

    private final Map<String, Byte> bytes = new HashMap<String, Byte>();

    private final Map<String, Short> shorts = new HashMap<String, Short>();

    private final Map<String, Integer> integers = new HashMap<String, Integer>();

    private final Map<String, Long> longs = new HashMap<String, Long>();

    private final Map<String, Float> floats = new HashMap<String, Float>();

    private final Map<String, Double> doubles = new HashMap<String, Double>();

    private final Map<String, BigDecimal> bigDecimals = new HashMap<String, BigDecimal>();

    private final Map<String, Date> dates = new HashMap<String, Date>();

    private final Map<String, Date> times = new HashMap<String, Date>();

    private final Map<String, Date> timestamps = new HashMap<String, Date>();

    private final Map<String, Object> parameters = new HashMap<String, Object>();

    private final Map<String, Collection<? extends Object>> parameterLists = new HashMap<String, Collection<? extends Object>>();

    private final Map<String, Object> entities = new HashMap<String, Object>();

    private final DataAccessService dataAccessService;

    private final InternalDataDefinition sourceDataDefinition;

    private final String queryString;

    private InternalDataDefinition mainDataDefinition = null;

    private int maxResults;

    private int firstResult;

    private boolean cacheable = false;

    public SearchQueryImpl(final InternalDataDefinition dataDefinition, final DataAccessService dataAccessService,
            final String queryString) {
        this.sourceDataDefinition = dataDefinition;
        this.dataAccessService = dataAccessService;
        this.queryString = prepareDataDefinitions(prepareQuery(queryString));
    }

    private String prepareDataDefinitions(final String queryString) {
        boolean hasSelectSection = !queryString.startsWith("from");

        Matcher matcher = pattern.matcher(queryString);

        String newQueryString = queryString;

        while (matcher.find()) {
            InternalDataDefinition dataDefinition = dataAccessService.getDataDefinition(matcher.group(1), matcher.group(2));

            newQueryString = newQueryString.replaceAll(
                    "#" + dataDefinition.getPluginIdentifier() + "_" + dataDefinition.getName(),
                    dataDefinition.getFullyQualifiedClassName());

            if (!hasSelectSection && mainDataDefinition == null) {
                mainDataDefinition = dataDefinition;
            }
        }

        return newQueryString;
    }

    private String prepareQuery(final String queryString) {
        if (!StringUtils.hasText(queryString)) {
            return "from #" + sourceDataDefinition.getPluginIdentifier() + "_" + sourceDataDefinition.getName();
        }
        if (queryString.trim().startsWith("where")) {
            return "from #" + sourceDataDefinition.getPluginIdentifier() + "_" + sourceDataDefinition.getName() + " "
                    + queryString.trim();
        }
        return queryString.trim();
    }

    @Override
    public DataDefinition getDataDefinition() {
        return mainDataDefinition;
    }

    @Override
    public SearchResult list() {
        return dataAccessService.find(this);
    }

    @Override
    public Entity uniqueResult() {
        SearchResult results = list();

        if (results.getEntities().isEmpty()) {
            return null;
        } else if (results.getEntities().size() == 1) {
            return results.getEntities().get(0);
        } else {
            throw new IllegalStateException("Too many results, expected one, found " + results.getEntities().size());
        }
    }

    public SearchQueryBuilder setCacheable(final boolean cacheable){
        this.cacheable = cacheable;
        return this;
    }

    @Override
    public void addCacheable(Query query) {
        query.setCacheable(cacheable);
    }

    @Override
    public SearchQueryBuilder setMaxResults(final int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public SearchQueryBuilder setFirstResult(final int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    @Override
    public SearchQueryBuilder setString(final String name, final String val) {
        strings.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setBoolean(final String name, final boolean val) {
        booleans.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setByte(final String name, final byte val) {
        bytes.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setShort(final String name, final short val) {
        shorts.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setInteger(final String name, final int val) {
        integers.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setLong(final String name, final long val) {
        longs.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setFloat(final String name, final float val) {
        floats.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setDouble(final String name, final double val) {
        doubles.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setBigDecimal(final String name, final BigDecimal number) {
        bigDecimals.put(name, number);
        return this;
    }

    @Override
    public SearchQueryBuilder setDate(final String name, final Date date) {
        dates.put(name, date);
        return this;
    }

    @Override
    public SearchQueryBuilder setTime(final String name, final Date date) {
        times.put(name, date);
        return this;
    }

    @Override
    public SearchQueryBuilder setTimestamp(final String name, final Date date) {
        timestamps.put(name, date);
        return this;
    }

    @Override
    public SearchQueryBuilder setEntity(final String name, final Entity entity) {
        Object databaseEntity = null;

        if (entity != null) {
            databaseEntity = dataAccessService.convertToDatabaseEntity(entity);
        }

        if (databaseEntity == null) {
            parameters.put(name, null);
        } else {
            entities.put(name, databaseEntity);
        }

        return this;
    }

    @Override
    public SearchQueryBuilder setEntity(final String name, final String pluginIdentifier, final String modelName,
            final long entityId) {
        return setEntity(name, dataAccessService.get(dataAccessService.getDataDefinition(pluginIdentifier, modelName), entityId));
    }

    @Override
    public SearchQueryBuilder setParameter(final String name, final Object val) {
        parameters.put(name, val);
        return this;
    }

    @Override
    public SearchQueryBuilder setParameterList(final String name, final Collection<? extends Object> values) {
        parameterLists.put(name, values);
        return this;
    }

    @Override
    public Query createQuery(final Session session) {
        return session.createQuery(queryString);
    }

    @Override
    public void addParameters(final Query query) {
        for (Map.Entry<String, String> parameter : strings.entrySet()) {
            query.setString(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Boolean> parameter : booleans.entrySet()) {
            query.setBoolean(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Byte> parameter : bytes.entrySet()) {
            query.setByte(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Short> parameter : shorts.entrySet()) {
            query.setShort(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Integer> parameter : integers.entrySet()) {
            query.setInteger(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Long> parameter : longs.entrySet()) {
            query.setLong(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Float> parameter : floats.entrySet()) {
            query.setFloat(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Double> parameter : doubles.entrySet()) {
            query.setDouble(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, BigDecimal> parameter : bigDecimals.entrySet()) {
            query.setBigDecimal(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Date> parameter : dates.entrySet()) {
            query.setDate(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Date> parameter : times.entrySet()) {
            query.setTime(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Date> parameter : timestamps.entrySet()) {
            query.setTimestamp(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            query.setParameter(parameter.getKey(), parameter.getValue());
        }
        for (Map.Entry<String, Collection<? extends Object>> parametersList : parameterLists.entrySet()) {
            query.setParameterList(parametersList.getKey(), parametersList.getValue());
        }
        for (Map.Entry<String, Object> parameter : entities.entrySet()) {
            query.setEntity(parameter.getKey(), parameter.getValue());
        }
    }

    @Override
    public void addFirstAndMaxResults(final Query query) {
        if (firstResult > 0) {
            query.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
    }

    @Override
    public boolean hasFirstAndMaxResults() {
        return firstResult > 0 || maxResults > 0;
    }

}
