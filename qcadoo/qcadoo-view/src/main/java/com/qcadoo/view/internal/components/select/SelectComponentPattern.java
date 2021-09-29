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

import static org.springframework.util.StringUtils.hasText;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.api.types.EnumeratedType;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.components.FieldComponentPattern;

public final class SelectComponentPattern extends FieldComponentPattern {

    private static final String JSP_PATH = "elements/select.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.DynamicComboBox";

    private List<String> values = Lists.newArrayList();

    public SelectComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    protected void initializeComponent() throws JSONException {
        super.initializeComponent();
        for (ComponentOption option : getOptions()) {
            if ("values".equals(option.getType())) {
                String optionValue = option.getValue();
                if (hasText(optionValue)) {
                    values = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(optionValue);
                }
            }
        }
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new SelectComponentState(this, values);
    }

    public Map<String, String> getValuesMap(final Locale locale) {
        Map<String, String> values = new LinkedHashMap<String, String>();

        if (!isRequired() || (getFieldDefinition() != null && getFieldDefinition().getDefaultValue() == null)) {
            String coreBlankTranslationKey = "qcadooView.form.blankComboBoxValue";
            if (isRequired()) {
                coreBlankTranslationKey = "qcadooView.form.requiredBlankComboBoxValue";
            }
            values.put("",
                    getTranslationService().translate(getTranslationPath() + ".blankValue", coreBlankTranslationKey, locale));
        }

        if (getFieldDefinition() != null) {
            if (EnumeratedType.class.isAssignableFrom(getFieldDefinition().getType().getClass())) {
                values.putAll(((EnumeratedType) getFieldDefinition().getType()).values(locale));
            } else if (BelongsToType.class.isAssignableFrom(getFieldDefinition().getType().getClass())) {
                throw new IllegalStateException("Select for belongsTo type is not supported");
            } else {
                throw new IllegalStateException("Select for " + getFieldDefinition().getType().getClass().getSimpleName()
                        + " type is not supported");
            }
        } else if (!this.values.isEmpty()) {
            for (String value : this.values) {
                values.put(value, getTranslationService().translate(getTranslationPath() + ".values." + value, locale));
            }
        }

        return values;
    }

    public JSONArray getValuesJson(final Locale locale) throws JSONException {
        JSONArray values = new JSONArray();
        for (Map.Entry<String, String> valueEntry : getValuesMap(locale).entrySet()) {
            JSONObject obj = new JSONObject();
            obj.put("key", valueEntry.getKey());
            obj.put("value", valueEntry.getValue());
            values.put(obj);
        }
        return values;
    }

    @Override
    protected Map<String, Object> getJspOptions(final Locale locale) {
        Map<String, Object> options = super.getJspOptions(locale);
        options.put("values", getValuesMap(locale));
        return options;
    }

    @Override
    public String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    public String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    public String getJsObjectName() {
        return JS_OBJECT;
    }
}
