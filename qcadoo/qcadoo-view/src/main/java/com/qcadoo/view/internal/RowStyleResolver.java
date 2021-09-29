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
package com.qcadoo.view.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Node;

import com.google.common.collect.Maps;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;

/**
 * CSS classes resolver for components with rows.
 * 
 * @author marcinkubala
 * @since 1.2.0
 */
public final class RowStyleResolver {

    public static final String NODE_NAME = "rowStyleResolver";

    private final CustomMethodHolder customMethodHolder;

    public RowStyleResolver(final Node holderNode, final ViewDefinitionParser parser, final ApplicationContext applicationContext) {
        customMethodHolder = new CustomMethodHolder(holderNode, parser, applicationContext, Set.class,
                new Class[] { Entity.class });
    }

    /**
     * Resolve CSS classes for grid rows
     * 
     * @param entities
     *            list of entities which the CSS class will be resolved
     * @return entity identifiers mapped to appropriate set of the CSS classes.
     */
    public Map<Long, Set<String>> resolve(final List<Entity> entities) {
        final Map<Long, Set<String>> entityIdsToStylesMap = Maps.newHashMap();
        for (Entity entity : entities) {
            if (entity.getId() == null) {
                continue;
            }
            @SuppressWarnings("unchecked")
            final Set<String> cssClassesForEntity = (Set<String>) customMethodHolder.invoke(entity);
            if (!cssClassesForEntity.isEmpty()) {
                entityIdsToStylesMap.put(entity.getId(), cssClassesForEntity);
            }
        }
        return entityIdsToStylesMap;
    }

}
