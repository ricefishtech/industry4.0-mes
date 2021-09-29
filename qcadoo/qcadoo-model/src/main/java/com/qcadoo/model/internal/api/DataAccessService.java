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
package com.qcadoo.model.internal.api;

import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityOpResult;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.internal.DataDefinitionImpl;
import com.qcadoo.model.internal.search.SearchCriteria;
import com.qcadoo.model.internal.search.SearchQuery;

import java.util.List;

/**
 * Service for manipulating data.
 * 
 * @since 0.4.0
 * 
 */
public interface DataAccessService {

    /**
     * Return dataDefinition for given pluginIdentifier and name
     * 
     * @param pluginIdentifier
     * @param name
     * @return the data definition
     */
    InternalDataDefinition getDataDefinition(String pluginIdentifier, String name);

    /**
     * Save the entity related with given data definition.
     * 
     * @param dataDefinition
     * @param entity
     * @return saved entity
     */
    Entity save(InternalDataDefinition dataDefinition, Entity entity);

    /**
     * Validate the entity related with given data definition.
     *
     * @param dataDefinition
     * @param entity
     * @return validated entity
     */
    Entity validate(DataDefinitionImpl dataDefinition, Entity entity);

    /**
     * Save the entity related with given data definition without invoke hooks.
     * 
     * @param dataDefinition
     * @param entity
     * @return saved entity
     */
    Entity fastSave(InternalDataDefinition dataDefinition, Entity entity);

    /**
     * Return the entity related with given data definition, by its id.
     * 
     * @param dataDefinition
     * @param entityId
     * @return entity
     */
    Entity get(InternalDataDefinition dataDefinition, Long entityId);

    /**
     * Return the entity related with master model data definition, by its id.
     * 
     * @param dataDefinition
     * @param id
     * @return entity
     */
    Entity getMasterModelEntity(InternalDataDefinition dataDefinition, final Long id);
    
    /**
     * Return the copied entity related with given data definition.
     * 
     * @param dataDefinition
     * @param entityId
     * @return entity
     */
    List<Entity> copy(InternalDataDefinition dataDefinition, Long... entityId);

    /**
     * Delete the entity related with given data definition, by its id.
     * 
     * @param dataDefinition
     * @param entityId
     * @return {@link EntityOpResult} which represent deletion results
     */
    EntityOpResult delete(InternalDataDefinition dataDefinition, Long... entityId);

    /**
     * Find search result for given search criteria.
     * 
     * @param searchCriteria
     * @return result of search
     */
    SearchResult find(SearchCriteria searchCriteria);

    /**
     * Find search result for given search query.
     * 
     * @param searchQuery
     * @return result of search
     */
    SearchResult find(SearchQuery searchQuery);

    /**
     * Move the prioritizable entity to the target position.
     * 
     * @param dataDefinition
     * @param entityId
     * @param position
     */
    void moveTo(InternalDataDefinition dataDefinition, Long entityId, int position);

    /**
     * Move the prioritizable entity by offset.
     * 
     * @param dataDefinition
     * @param entityId
     * @param offset
     */
    void move(InternalDataDefinition dataDefinition, Long entityId, int offset);

    /**
     * Convert given entity to database entity.
     * 
     * @param entity
     * @return database entity
     */
    Object convertToDatabaseEntity(Entity entity);

    /**
     * Deactivate given entities.
     * 
     * @param dataDefinition
     * @param entityId
     * @return deactivated entities
     */
    List<Entity> deactivate(InternalDataDefinition dataDefinition, Long... entityId);

    /**
     * Activate given entities.
     * 
     * @param dataDefinition
     * @param entityId
     * @return activated entities
     */
    List<Entity> activate(InternalDataDefinition dataDefinition, Long... entityId);

}
