/*
 * **************************************************************************
 * Copyright (c) 2018 RiceFish Limited
 * Project: SmartMES Framework
 * Version: 1.6
 *
 * This file is part of SmartMES.
 *
 * SmartMES is Authorized software; you can redistribute it and/or modify
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
 * **************************************************************************
 */
package com.qcadoo.mes.basic.staff.importing;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchRestrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import com.qcadoo.tenant.api.MultiTenantService;

@Component
class WageGroupsCellParser implements CellParser {

    private final DataDefinitionService dataDefinitionService;

    @Autowired
    private MultiTenantService multiTenantService;

    @Autowired
    WageGroupsCellParser(DataDefinitionService dataDefinitionService) {
        this.dataDefinitionService = dataDefinitionService;
    }

    private DataDefinition getWageGroupsDataDefinition() {
        return dataDefinitionService.get("wageGroups", "wageGroup");
    }

    @Override
    public void parse(String cellValue, BindingErrorsAccessor errorsAccessor, Consumer<Object> valueConsumer) {
        Entity wageGroupsCandidate = getWageGroupsDataDefinition()
                .find()
                .add(SearchRestrictions.eq("name", cellValue))
                .uniqueResult();
        if (null != wageGroupsCandidate) {
            valueConsumer.accept(wageGroupsCandidate);
        } else {
            errorsAccessor.addError("smartView.validate.field.error.lookupCodeNotFound");
        }
    }
}
