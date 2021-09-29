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
package com.qcadoo.view.internal.components.file;

import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.components.FieldComponentPattern;

public final class FileInputComponentPattern extends FieldComponentPattern {

    private static final String JSP_PATH = "elements/file.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.File";

    private boolean thumbnail = false;

    public FileInputComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    protected void initializeComponent() throws JSONException {
        super.initializeComponent();
        for (ComponentOption option : getOptions()) {
            if ("thumbnail".equals(option.getType())) {
                thumbnail = Boolean.parseBoolean(option.getValue());
                break;
            }
        }
    }

    @Override
    protected Map<String, Object> getJspOptions(final Locale locale) {
        Map<String, Object> options = super.getJspOptions(locale);
        options.put("thumbnail", thumbnail);
        return options;
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = new JSONObject();

        JSONObject translations = new JSONObject();
        translations.put("uploadSuccessHeader", getTranslation("uploadSuccessHeader", locale));
        translations.put("uploadSuccessContent", getTranslation("uploadSuccessContent", locale));
        translations.put("uploadErrorHeader", getTranslation("uploadErrorHeader", locale));
        translations.put("uploadErrorContent", getTranslation("uploadErrorContent", locale));
        translations.put("uploadButton", getTranslation("uploadButton", locale));
        translations.put("deleteButton", getTranslation("deleteButton", locale));
        translations.put("deleteConfirm", getTranslation("deleteConfirm", locale));
        json.put("translations", translations);

        return json;
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new FileInputComponentState(this);
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

    private String getTranslation(final String key, final Locale locale) {
        return getTranslationService().translate(getTranslationPath() + "." + key, "qcadooView.fileUpload." + key, locale);
    }
}
