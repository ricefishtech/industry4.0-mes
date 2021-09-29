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
package com.qcadoo.model.api.utils;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.qcadoo.commons.functional.Optionals;
import com.qcadoo.model.api.Entity;

public class EntityUtils {

    private EntityUtils() {

    }

    private static final Function<Entity, Long> FUNC_EXTRACT_ID = new Function<Entity, Long>() {

        @Override
        public Long apply(final Entity entity) {
            if (entity == null) {
                return null;
            }
            Long id = entity.getId();
            if (id == null) {
                id = (Long) entity.getField("id");
            }
            return id;
        }
    };

    public static <T> Function<Entity, T> getFieldExtractor(final String fieldName) {
        return new Function<Entity, T>() {

            @Override
            public T apply(final Entity entity) {
                if (entity == null) {
                    return null;
                }
                return (T) entity.getField(fieldName);
            }
        };
    }

    public static Function<Entity, Entity> getBelongsToFieldExtractor(final String belongsToFieldName) {
        return new Function<Entity, Entity>() {

            @Override
            public Entity apply(final Entity entity) {
                if (entity == null) {
                    return null;
                }
                return entity.getBelongsToField(belongsToFieldName);
            }
        };
    }

    public static <T> Function<Entity, Optional<T>> getSafeFieldExtractor(final String fieldName) {
        Function<Entity, T> getFieldFunc = getFieldExtractor(fieldName);
        return Optionals.lift(getFieldFunc);
    }

    public static Function<Entity, Long> getIdExtractor() {
        return FUNC_EXTRACT_ID;
    }

    public static Function<Entity, Optional<Long>> getSafeIdExtractor() {
        return Optionals.lift(FUNC_EXTRACT_ID);
    }

    public static Collection<Long> getIdsView(final Collection<Entity> entities) {
        return Collections2.transform(entities, getIdExtractor());
    }

    public static <T> Collection<T> getFieldsView(final Collection<Entity> entities, final String fieldName) {
        return Collections2.transform(entities, (Function<Entity, T>) getFieldExtractor(fieldName));
    }

}
