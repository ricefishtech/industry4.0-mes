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
package com.qcadoo.model.api;

import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchQueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Object defines database structure. The {@link #getPluginIdentifier()} and {@link #getName()} are used to calculate table name.
 * 
 * @since 0.4.0
 */
public interface DataDefinition {

    /**
     * Return name of this data definition.
     * 
     * @return name
     */
    String getName();

    /**
     * Return plugin's identifier for this data definition.
     * 
     * @return plugin's identifier
     */
    String getPluginIdentifier();

    /**
     * Return the entity related with this data definition, by its id.
     * 
     * @param id
     *            id
     * @return entity
     */
    Entity get(final Long id);

    /**
     * Return the entity related with master model data definition, by its id.
     * 
     * @param id
     *            id
     * @return entity
     */
    Entity getMasterModelEntity(final Long id);

    /**
     * Return the entity related with master model data definition, by its id.
     *
     * @param id
     *            id
     * @return entity
     */
    Entity tryGetMasterModelEntity(Long id);


    /**
     * Return the copied entity related with this data definition.
     * 
     * @param id
     *            id
     * @return entity
     */
    List<Entity> copy(final Long... id);

    /**
     * Delete the entity related with this data definition, by its id.
     * 
     * @param id
     *            id
     * @return {@link EntityOpResult}, an object containing operation's results
     */
    EntityOpResult delete(final Long... id);

    /**
     * Save the entity related with this data definition.
     * 
     * @param entity
     *            entity to save
     * @return saved entity
     */
    Entity save(final Entity entity);

    /**
     * Validate the entity related with this data definition.
     *
     * @param entity
     *            entity to validate
     * @return validate entity
     */
    Entity validate(final Entity entity);

    /**
     * Save the entity related with this data definition without invoke hooks.
     * 
     * @param entity entity to save
     * @return saved entity
     */
    Entity fastSave(final Entity entity);

    /**
     * Create search criteria builder for this data definition.
     * 
     * @return new search criteria builder
     * @see #findWithAlias(String)
     */
    SearchCriteriaBuilder find();

    /**
     * Counts all model entities.
     *
     * @return entities count
     * @since 1.4
     */
    long count();

    /**
     * Counts entities found by given criteria.
     *
     * @param criterion
     *          criterion to restrict entities
     * @return entities count
     * @since 1.4
     */
    long count(final SearchCriterion criterion);

    /**
     * Create search criteria builder for this data definition. Root data definition will use given alias. This is usable for
     * subqueries.
     * 
     * @param alias
     *            alias
     * @return new search criteria builder
     */
    SearchCriteriaBuilder findWithAlias(String alias);

    /**
     * Create search query builder for given HQL query string.
     * 
     * @param queryString
     *            query string
     * @return new search query builder
     * @see SearchQueryBuilder
     * @since 0.4.1
     */
    SearchQueryBuilder find(String queryString);

    /**
     * Move the prioritizable entity by offset.
     * 
     * @param id
     *            id
     * @param offset
     *            offset
     */
    void move(final Long id, final int offset);

    /**
     * Move the prioritizable entity to the target position.
     * 
     * @param id
     *            id
     * @param position
     *            position
     */
    void moveTo(final Long id, final int position);

    /**
     * Return all defined fields' definitions.
     * 
     * @return fields' definitions
     */
    Map<String, FieldDefinition> getFields();

    /**
     * Return field definition by its name.
     * 
     * @param fieldName
     *            field's name
     * @return field's definition
     */
    FieldDefinition getField(final String fieldName);

    /**
     * Return priority field's definition.
     * 
     * @return priority field's definion, null if entity is not prioritizable
     */
    FieldDefinition getPriorityField();

    /**
     * Return true if entity is prioritizable.
     * 
     * @return true if entity is prioritizable
     */
    boolean isPrioritizable();

    /**
     * Return true if entity is activable.
     * 
     * @return true if entity is activable
     * @since 0.4.2
     */
    boolean isActivable();

    /**
     * Return true if entity is auditable.
     * 
     * @return true if entity is auditable
     */
    boolean isAuditable();

    /**
     * Return true if entity is versionable
     *
     * @return true if entity is versionable
     */
    boolean isVersionable();

    /**
     * Create entity with given id.
     * 
     * @param id
     *            id
     * @return entity
     */
    Entity create(Long id);

    /**
     * Create entity.
     * 
     * @return entity
     */
    Entity create();

    /**
     * Deactivate given entities.
     * 
     * @param ids
     *            ids
     * @return deactivated entities
     * @since 0.4.2
     */
    List<Entity> deactivate(Long... ids);

    /**
     * Activate given entities.
     * 
     * @param ids
     *            ids
     * @return activated entities
     * @since 0.4.2
     */
    List<Entity> activate(Long... ids);

    /**
     * Validate specified entity.
     * 
     * @param targetEntity
     *            entity which be validated.
     * @return true if given entity is valid.
     */
    boolean callValidators(final Entity targetEntity);
}
