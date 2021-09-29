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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.CustomRestriction;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchDisjunction;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.units.UnitConversionModelService;
import com.qcadoo.model.constants.QcadooModelConstants;
import com.qcadoo.model.constants.UnitConversionItemFields;

@Service
public class UnitConversionModelServiceImpl implements UnitConversionModelService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    private static final OnlyGlobalConversionRestriction ONLY_GLOBAL_CONVERSION_RESTRICTION = new OnlyGlobalConversionRestriction();

    @Override
    public List<Entity> find(final String unit) {
        return find(unit, ONLY_GLOBAL_CONVERSION_RESTRICTION);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entity> find(final String unit, final CustomRestriction customRestriction) {
        final CustomRestriction unitMatchingRestriction = new ConversionMatchingUnitRestriction(unit, customRestriction);
        final SearchCriteriaBuilder searchCriteriaBuilder = getDataDefinition().find();
        unitMatchingRestriction.addRestriction(searchCriteriaBuilder);
        return searchCriteriaBuilder.list().getEntities();
    }

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinitionService.get(QcadooModelConstants.PLUGIN_IDENTIFIER, QcadooModelConstants.MODEL_UNIT_CONVERSION_ITEM);
    }

    private static final class ConversionMatchingUnitRestriction implements CustomRestriction {

        private final String unit;

        private final CustomRestriction customRestriction;

        private ConversionMatchingUnitRestriction(final String unit, final CustomRestriction customRestriction) {
            Preconditions.checkNotNull(unit);
            Preconditions.checkNotNull(customRestriction);
            this.unit = unit;
            this.customRestriction = customRestriction;
        }

        @Override
        public void addRestriction(final SearchCriteriaBuilder searchCriteriaBuilder) {
            customRestriction.addRestriction(searchCriteriaBuilder);
            final SearchDisjunction disjunction = SearchRestrictions.disjunction();
            disjunction.add(SearchRestrictions.eq(UnitConversionItemFields.UNIT_FROM, unit));
            disjunction.add(SearchRestrictions.eq(UnitConversionItemFields.UNIT_TO, unit));
            searchCriteriaBuilder.add(disjunction);
        }
    }

    private static final class OnlyGlobalConversionRestriction implements CustomRestriction {

        @Override
        public void addRestriction(final SearchCriteriaBuilder searchCriteriaBuilder) {
            searchCriteriaBuilder.add(SearchRestrictions.isNotNull(UnitConversionItemFields.GLOBAL_UNIT_CONVERSIONS_AGGREGATE));
        }

    }

}
