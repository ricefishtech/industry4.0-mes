/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
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
package com.qcadoo.mes.technologies.criteriaModifiers;

import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.mes.technologies.constants.OperationFields;
import com.qcadoo.mes.technologies.constants.TechnologiesConstants;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.search.*;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StandardCriteriaModifiersT {

    private static final String L_DOT = ".";

    private static final String L_ID = "id";

    private static final String L_THIS_ID = "this.id";

    private static final String L_OPERATION_ID = "operationId";

    private static final String L_STANDARD_ID = "standardId";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void filterByOperation(final SearchCriteriaBuilder scb, final FilterValueHolder filterValueHolder) {
        if (filterValueHolder.has(L_OPERATION_ID)) {
            SearchCriteriaBuilder subCriteria = getOperationDD().findWithAlias(TechnologiesConstants.MODEL_OPERATION)
                    .add(SearchRestrictions.idEq(filterValueHolder.getLong(L_OPERATION_ID)))
                    .createAlias("operationStandards", "operationStandards", JoinType.INNER)
                    .createAlias("operationStandards" + L_DOT + "standard",
                            "standard", JoinType.INNER)
                    .add(SearchRestrictions.eqField("standard" + L_DOT + L_ID, L_THIS_ID))
                    .setProjection(SearchProjections.id());

            scb.add(SearchSubqueries.notExists(subCriteria));
        }
    }

    public void filterByStandard(final SearchCriteriaBuilder scb, final FilterValueHolder filterValueHolder) {
        if (filterValueHolder.has(L_STANDARD_ID)) {
            SearchCriteriaBuilder subCriteria = getStandardDD().findWithAlias("standard")
                    .add(SearchRestrictions.idEq(filterValueHolder.getLong(L_STANDARD_ID)))
                    .createAlias("operationStandards", "operationStandards", JoinType.INNER)
                    .createAlias("operationStandards" + L_DOT + "operation",
                            "operation", JoinType.INNER)
                    .add(SearchRestrictions.eqField("operation" + L_DOT + L_ID, L_THIS_ID))
                    .setProjection(SearchProjections.id());

            scb.add(SearchSubqueries.notExists(subCriteria));
        }
    }

    private DataDefinition getOperationDD() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_OPERATION);
    }

    private DataDefinition getStandardDD() {
        return dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, "standard");
    }
}
