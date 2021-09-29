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
package com.qcadoo.view.internal.components.select;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.qcadoo.view.internal.components.FieldComponentState;

public final class SelectComponentState extends FieldComponentState {

    private final SelectComponentPattern selectComponentPattern;

    public SelectComponentState(final SelectComponentPattern selectComponentPattern, final List<String> values) {
        super(selectComponentPattern);
        this.selectComponentPattern = selectComponentPattern;
        if (!values.isEmpty()){
            requestRender();
        }
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = super.renderContent();

        JSONArray valuesJson = selectComponentPattern.getValuesJson(getLocale());
        includeDeactivatedValue(valuesJson, getFieldValue());
        json.put("values", valuesJson);
        return json;
    }

    private void includeDeactivatedValue(final JSONArray valuesJson, final Object fieldValue) throws JSONException {
        if(StringUtils.isEmpty(fieldValue)){
            return;
        }

        int targetIndex = -1;
        String value = (String) fieldValue;
        for(int i = 0 ; i < valuesJson.length(); ++i){
            int result = value.compareTo(valuesJson.getJSONObject(i).getString("key"));
            if(result == 0){
                return;
            } else if(result > 0) {
                targetIndex = i;
            }
        }

        for(int i = valuesJson.length() -1; i > targetIndex; --i ){
            valuesJson.put(i+1, valuesJson.get(i));
        }

        JSONObject obj = new JSONObject();
        obj.put("key", fieldValue);
        obj.put("value", fieldValue);
        valuesJson.put(targetIndex + 1, obj);
    }

}
