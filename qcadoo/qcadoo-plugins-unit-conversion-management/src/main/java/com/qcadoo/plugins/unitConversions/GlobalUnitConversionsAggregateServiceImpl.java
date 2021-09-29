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
package com.qcadoo.plugins.unitConversions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.constants.QcadooModelConstants;

@Service
public final class GlobalUnitConversionsAggregateServiceImpl implements GlobalUnitConversionsAggregateService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    @Transactional
    public Long getAggregateId() {
        Entity existingAggregate = getAggregateDataDefinition().find().setMaxResults(1).uniqueResult();
        if (existingAggregate == null) {
            existingAggregate = getAggregateDataDefinition().create();
            existingAggregate = existingAggregate.getDataDefinition().save(existingAggregate);
            Preconditions.checkState(existingAggregate.isValid());
        }
        return existingAggregate.getId();
    }

    private DataDefinition getAggregateDataDefinition() {
        return dataDefinitionService.get(QcadooModelConstants.PLUGIN_IDENTIFIER,
                QcadooModelConstants.MODEL_GLOBAL_UNIT_CONVERSIONS_AGGREGATE);
    }

}
