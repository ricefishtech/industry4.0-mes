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
package com.qcadoo.model.internal.units;

import com.google.common.base.Preconditions;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.units.UnitConversion;
import com.qcadoo.model.constants.UnitConversionItemFields;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.math.MathContext;

public final class UnitConversionImpl implements UnitConversion {

    private final MathContext mathContext;

    private final BigDecimal ratio;

    private final String unitFrom;

    private final String unitTo;

    private UnitConversionImpl(final String unitFrom, final String unitTo, final BigDecimal ratio, final MathContext mathContext) {
        Preconditions.checkNotNull(unitFrom);
        Preconditions.checkNotNull(ratio);
        Preconditions.checkNotNull(mathContext);

        this.unitFrom = unitFrom;
        this.unitTo = unitTo;
        this.ratio = ratio.setScale(mathContext.getPrecision(), mathContext.getRoundingMode());
        this.mathContext = mathContext;
    }

    public static UnitConversion build(final Entity unitConversionItem, final MathContext mathContext) {
        final String unitFrom = unitConversionItem.getStringField(UnitConversionItemFields.UNIT_FROM);
        final String unitTo = unitConversionItem.getStringField(UnitConversionItemFields.UNIT_TO);
        final BigDecimal quantityFrom = unitConversionItem.getDecimalField(UnitConversionItemFields.QUANTITY_FROM);
        final BigDecimal quantityTo = unitConversionItem.getDecimalField(UnitConversionItemFields.QUANTITY_TO);
        final BigDecimal ratio = quantityTo.divide(quantityFrom, mathContext);

        return new UnitConversionImpl(unitFrom, unitTo, ratio, mathContext);
    }

    public static UnitConversion build(final String unit, final MathContext mathContext) {
        return new UnitConversionImpl(unit, unit, BigDecimal.ONE, mathContext);
    }

    @Override
    public UnitConversion reverse() {
        return new UnitConversionImpl(unitTo, unitFrom, BigDecimal.ONE.divide(ratio, mathContext), mathContext);
    }

    @Override
    public UnitConversion merge(final UnitConversion other) {
        return new UnitConversionImpl(unitFrom, other.getUnitTo(), ratio.multiply(other.getRatio(), mathContext), mathContext);
    }

    @Override
    public BigDecimal getRatio() {
        return ratio;
    }

    @Override
    public String getUnitFrom() {
        return unitFrom;
    }

    @Override
    public String getUnitTo() {
        return unitTo;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31).append(unitFrom).append(unitTo).append(ratio).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final UnitConversionImpl other = (UnitConversionImpl) obj;
        return new EqualsBuilder().append(unitFrom, other.unitFrom).append(unitTo, other.unitTo).append(ratio, other.ratio)
                .isEquals();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnitConversion[");
        sb.append(unitFrom);
        sb.append(" -> ");
        sb.append(ratio.toPlainString());
        sb.append(' ');
        sb.append(unitTo);
        sb.append(']');
        return sb.toString();
    }

}
