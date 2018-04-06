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

import java.util.HashMap;
import java.util.Map;

public class SpreadsheetSchemaInfo {
    static final int START_ROW_INDEX = 1;
    static final int COLUMN_NUMBER =11;
    private static Map<String, Integer> nameToIndexMap = new HashMap<String, Integer>(SpreadsheetSchemaInfo.COLUMN_NUMBER);

    static {
        int position = 0;
        nameToIndexMap.put(StaffFields.NUMBER, position++);
        nameToIndexMap.put(StaffFields.NAME, position++);
        nameToIndexMap.put(StaffFields.SURNAME, position++);
        nameToIndexMap.put(StaffFields.PHONE, position++);
        nameToIndexMap.put(StaffFields.EMAIL, position++);
        nameToIndexMap.put(StaffFields.POST, position++);
        nameToIndexMap.put(StaffFields.WORK_FOR, position++);
        nameToIndexMap.put(StaffFields.SHIFT, position++);
        nameToIndexMap.put(StaffFields.DIVISION, position++);
        nameToIndexMap.put(StaffFields.CREW, position++);
        nameToIndexMap.put(StaffFields.WAGE_GROUP, position);
    }

    private SpreadsheetSchemaInfo() {
        // empty by design
    }

    public static int getIndexUsingFieldName(String fieldName) {
        return nameToIndexMap.get(fieldName);
    }

}
