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
import com.google.common.collect.Sets;
import com.qcadoo.model.api.DictionaryService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.NumberService;
import com.qcadoo.model.api.search.CustomRestriction;
import com.qcadoo.model.api.units.PossibleUnitConversions;
import com.qcadoo.model.api.units.UnitConversion;
import com.qcadoo.model.api.units.UnitConversionModelService;
import com.qcadoo.model.api.units.UnitConversionService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public final class UnitConversionServiceImpl implements UnitConversionService {

    @Autowired
    private UnitConversionModelService unitConversionModelService;

    @Autowired
    private NumberService numberService;

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public PossibleUnitConversions getPossibleConversions(final String unit) {
        return getPossibleConversions(unit, unitConversionModelService.find(unit));
    }

    @Override
    public PossibleUnitConversions getPossibleConversions(final String unit, final CustomRestriction customRestriction) {
        return getPossibleConversions(unit, unitConversionModelService.find(unit, customRestriction));
    }

    private PossibleUnitConversions getPossibleConversions(final String unit, final List<Entity> matchingDomain) {
        Preconditions.checkNotNull(unit);
        final InternalPossibleUnitConversions possibleUnitConversions = new PossibleUnitConversionsImpl(unit, numberService,
                unitConversionModelService.getDataDefinition(), dictionaryService);
        final UnitConversion root = UnitConversionImpl.build(unit, numberService.getMathContext());
        traverse(possibleUnitConversions, root, convertEntities(matchingDomain));
        return possibleUnitConversions;
    }

    private void traverse(final InternalPossibleUnitConversions possibleUnitConversions, final UnitConversion parent,
            final Set<UnitConversion> domain) {
        for (final UnitConversion unitConversion : findMatchingConversions(parent.getUnitTo(), domain)) {
            final UnitConversion generatedConversion = parent.merge(unitConversion);
            possibleUnitConversions.addConversion(generatedConversion);
            traverse(possibleUnitConversions, generatedConversion, domain);
        }
    }

    private Set<UnitConversion> findMatchingConversions(final String unitToFind, final Set<UnitConversion> domain) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(unitToFind));
        final Set<UnitConversion> matchingConversions = Sets.newHashSet();
        for (final UnitConversion unitConversion : Sets.newHashSet(domain)) {
            if (unitToFind.equals(unitConversion.getUnitFrom())) {
                matchingConversions.add(unitConversion);
                domain.remove(unitConversion);
            } else if (unitToFind.equals(unitConversion.getUnitTo())) {
                matchingConversions.add(unitConversion.reverse());
                domain.remove(unitConversion);
            }
        }
        return matchingConversions;
    }

    private Set<UnitConversion> convertEntities(final List<Entity> unitConversionItems) {
        final Set<UnitConversion> unitConversions = Sets.newHashSet();
        for (final Entity unitConversionItem : unitConversionItems) {
            unitConversions.add(UnitConversionImpl.build(unitConversionItem, numberService.getMathContext()));
        }
        return unitConversions;
    }


}
