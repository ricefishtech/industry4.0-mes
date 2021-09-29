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

import org.hibernate.criterion.CriteriaSpecification;

/**
 * Supported join types
 * 
 * @author Marcin Kubala
 * @since 1.2.1
 */
public enum JoinType {
    /**
     * Specifies joining to an entity based on a full join.
     */
    FULL(CriteriaSpecification.FULL_JOIN),

    /**
     * Specifies joining to an entity based on an inner join.
     */
    INNER(CriteriaSpecification.INNER_JOIN),

    /**
     * Specifies joining to an entity based on a left outer join.
     */
    LEFT(CriteriaSpecification.LEFT_JOIN);

    private final int intValue;

    private JoinType(final int intValue) {
        this.intValue = intValue;
    }

    /**
     * Get corresponding Hibernate's integer constant value for this join type.
     * 
     * @return corresponding Hibernate's integer constant value for this join type.
     */
    public int getIntValue() {
        return intValue;
    }
}
