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
package com.qcadoo.view.internal.components;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.patterns.AbstractComponentPattern;

public final class StaticPageComponentPattern extends AbstractComponentPattern {

    private static final String JS_OBJECT = "QCD.components.elements.StaticComponent";

    private static final String JSP_PATH = "elements/staticPage.jsp";

    private String page;
    
    private String messagesGroup = "commons";

    public StaticPageComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    protected void initializeComponent() throws JSONException {
        for (ComponentOption option : getOptions()) {
            if ("page".equals(option.getType())) {
                page = option.getValue();
            } else if("messagesGroup".equals(option.getType())){
                messagesGroup = option.getValue();
            } else {
                throw new IllegalStateException("Unknown option for staticPage: " + option.getType());
            }
        }
    }

    @Override
    protected Map<String, Object> getJspOptions(final Locale locale) {
        Map<String, Object> options = new HashMap<>();

        options.put("page", page);
        Map<String, String> translationsMap = getTranslationService().getMessagesGroup(messagesGroup, locale);
        translationsMap.put("qcadooView.fileupload.dropzone", getTranslationService().translate("qcadooView.fileupload.dropzone",
                locale, "" + getTranslationService().getMaxUploadSize()));
        translationsMap.put("qcadooView.errorPage.error.uploadException.maxSizeExceeded.explanation",
                getTranslationService().translate("qcadooView.errorPage.error.uploadException.maxSizeExceeded.explanation",
                        locale, "" + getTranslationService().getMaxUploadSize()));
        options.put("translationsMap", translationsMap);

        return options;
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new EmptyComponentState();
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
