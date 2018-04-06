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

import com.qcadoo.mes.basic.constants.StaffFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.qcadoo.mes.basic.staff.importing.CellBinder.optional;
import static com.qcadoo.mes.basic.staff.importing.CellBinder.required;

@Component
class BasicPluginCellBinderRegistrarForStaff {

    @Autowired
    private CellBinderRegistryForStaff cellBinderRegistryForStaff;

    @Autowired
    private CellParser companyCellParser;

    @Autowired
    private CellParser divisionCellParser;

    @Autowired
    private CellParser wageGroupsCellParser;

    @PostConstruct
    private void init() {
        cellBinderRegistryForStaff.setCellBinder(required(StaffFields.NUMBER));
        cellBinderRegistryForStaff.setCellBinder(required(StaffFields.NAME));
        cellBinderRegistryForStaff.setCellBinder(required(StaffFields.SURNAME));
        cellBinderRegistryForStaff.setCellBinder(optional(StaffFields.PHONE));
        cellBinderRegistryForStaff.setCellBinder(optional(StaffFields.EMAIL));
        cellBinderRegistryForStaff.setCellBinder(optional(StaffFields.POST));
        cellBinderRegistryForStaff.setCellBinder(optional(StaffFields.WORK_FOR,companyCellParser));
        cellBinderRegistryForStaff.setCellBinder(optional(StaffFields.SHIFT));
        cellBinderRegistryForStaff.setCellBinder(optional(StaffFields.DIVISION, divisionCellParser));
        cellBinderRegistryForStaff.setCellBinder(optional(StaffFields.CREW));
        cellBinderRegistryForStaff.setCellBinder(required(StaffFields.WAGE_GROUP,wageGroupsCellParser));


    }

}
