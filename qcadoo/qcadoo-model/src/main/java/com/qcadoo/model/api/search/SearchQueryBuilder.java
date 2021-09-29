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
package com.qcadoo.model.api.search;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.qcadoo.model.api.Entity;
import org.hibernate.Query;

/**
 * Object represents the query builder for finding entities.<br/>
 * <br/>
 * The query is based on HQL - the Hibernate's query language. The only difference is the way to name the entities. You must use
 * hash, plugin's name, underscore and model's name. For example "#plugin_model".<br/>
 * <br/>
 * If You start the query from "where" keyword, the "from" section will be automatically taken from current dataDefinition object.<br/>
 * <br/>
 * Parameters are binded to the query using placeholders - colon with parameter's name.<br/>
 * <br/>
 * Please see more on <a href="http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html/queryhql.html">the official
 * reference</a>.<br/>
 * <br/>
 * Examples:<br/>
 * 
 * <ul>
 * <li>where name = :name - select all entities from current data definition with given name, list of "products_product" entities
 * will be returned</li>
 * <li>from #products_product where name = :name - select all "products_product" entities with given name, list of
 * "products_product" entities will be returned</li>
 * <li>from #products_product as p where p.name = :name - select all "products_product" entities with given name, list of
 * "products_product" entities will be returned</li>
 * <li>from #products_product as p where p.vendor.name = :name - select all "products_product" entities with belongs to field
 * "vendor" with given name, list of "products_product" entities will be returned</li>
 * <li>from #products_product as p where size(p.components) > 0 - select all "products_product" entities which have components,
 * list of "products_product" entities will be returned</li>
 * <li>select p from #products_product as p where p.name = :name - select all "products_product" entities with given name, list of
 * "products_product" entities will be returned</li>
 * <li>select p, upper(p.name) from #products_product as p where p.name = :name - select all "products_product" entities with
 * given name, list of dynamic entities will be returned, field "0" will contain "products_product" entity, field "1" will contain
 * uppercased name</li>
 * <li>select p as product, upper(p.name) as name from #products_product as p where p.name = :name - select all "products_product"
 * entities with given name, list of dynamic entities will be returned, field "product" will contain "products_product" entity,
 * field "name" will contain uppercased name</li>
 * <li>from #products_product order by name asc- select all "products_product" ordered by name, list of "products_product"
 * entities will be returned</li>
 * <li>select distinct p.name as name from #products_product as p where lower(p.name) like 'a%' - select all unique names started
 * with letter "a", list of dynamic entities will be returned, field "name" will contain name</li>
 * </ul>
 * 
 * @since 0.4.1
 */
public interface SearchQueryBuilder {

    /**
     * Finds entities using this criteria.
     * 
     * @return search result
     */
    SearchResult list();

    /**
     * Finds unique entity.
     * 
     * @return entity
     */
    Entity uniqueResult();

    /**
     * Sets the max results, by default there is no limit.
     * 
     * @param maxResults
     *            max results
     * @return this query builder
     */
    SearchQueryBuilder setMaxResults(int maxResults);

    /**
     * Sets the first result, by default the first result is equal to zero.
     * 
     * @param firstResult
     *            first result
     * @return this query builder
     */
    SearchQueryBuilder setFirstResult(int firstResult);

    /**
     * Sets the "string" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setString(String name, String val);

    /**
     * Sets the "boolean" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setBoolean(String name, boolean val);

    /**
     * Sets the "byte" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setByte(String name, byte val);

    /**
     * Sets the "short" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setShort(String name, short val);

    /**
     * Sets the "integer" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setInteger(String name, int val);

    /**
     * Sets the "long" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setLong(String name, long val);

    /**
     * Sets the "float" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setFloat(String name, float val);

    /**
     * Sets the "double" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setDouble(String name, double val);

    /**
     * Sets the "bigDecimal" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setBigDecimal(String name, BigDecimal val);

    /**
     * Sets the "date" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setDate(String name, Date val);

    /**
     * Sets the "time" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setTime(String name, Date val);

    /**
     * Sets the "timestamp" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setTimestamp(String name, Date date);

    /**
     * Sets the "entity" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param entity
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setEntity(String name, Entity entity);

    /**
     * Sets the "entity" parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param id
     *            entity's id
     * @param modelName
     *            entity's model
     * @param pluginIdentifier
     *            entity's plugin
     * @return this query builder
     */
    SearchQueryBuilder setEntity(String name, String pluginIdentifier, String modelName, long id);

    /**
     * Sets the parameter for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            value of the parameter
     * @return this query builder
     */
    SearchQueryBuilder setParameter(String name, Object val);

    /**
     * Sets the collection of parameters for given placeholder.
     * 
     * @param name
     *            placeholder
     * @param val
     *            collection of values for the parameter
     * @return this query builder
     */
    SearchQueryBuilder setParameterList(String name, Collection<? extends Object> parameters);

    /**
     * Add caching parameter to query
     *
     * @param query
     */
    void addCacheable(Query query);

    /**
     * Enable caching of this query result, provided query caching is enabled for the underlying session factory.
     *
     * @param cacheable
     * @return this search builder
     */
    SearchQueryBuilder setCacheable(boolean cacheable);
}
