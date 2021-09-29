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
package com.qcadoo.model.api.units;

import com.qcadoo.model.api.Entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object representing possible unit conversions
 * 
 * @since 1.1.8
 * @author maku
 */
public interface PossibleUnitConversions {

    /**
     * @return source unit
     */
    String getUnitFrom();

    /**
     * @param value
     * @param unitTo
     * @return value in specified unit
     * @throws UnsupportedUnitConversionException
     *             if conversion is not defined
     */
    BigDecimal convertTo(final BigDecimal value, final String unitTo);

    /**
     * @param value
     * @param unitTo
     * @param roundMode
     * @return value in specified unit
     * @throws UnsupportedUnitConversionException
     *             if conversion is not defined
     */
    BigDecimal convertTo(final BigDecimal value, final String unitTo, final int roundMode);

    /**
     * @return true if there is no available conversions matching source unit (source unit can be obtained using
     *         {@link #getUnitFrom()}).
     */
    boolean isEmpty();

    /**
     * @return Set of supported target units
     */
    Set<String> getSupportedUnits();

    /**
     * @param unit
     * @return true if conversion to specified unit is defined
     */
    boolean isDefinedFor(final String unit);

    /**
     * @return all matching conversions as map containing target unit -> conversion ratio pairs.
     */
    Map<String, BigDecimal> asUnitToConversionMap();

    /**
     * Get all matching conversions as list of UnitConversionItem entities, belonging to specified owner Entity.
     * 
     * @param ownerFieldName
     *            owner entity field name
     * @param ownerEntity
     *            owner entity
     * @return all matching conversions as list of UnitConversionItem entities
     */
    List<Entity> asEntities(final String ownerFieldName, final Entity ownerEntity);

}
