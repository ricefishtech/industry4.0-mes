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
package com.qcadoo.view.internal.components.window;

import org.json.JSONException;
import org.json.JSONObject;

import com.qcadoo.view.api.components.WindowTabComponent;
import com.qcadoo.view.api.ribbon.Ribbon;
import com.qcadoo.view.internal.ribbon.RibbonUtils;
import com.qcadoo.view.internal.ribbon.model.InternalRibbon;
import com.qcadoo.view.internal.states.AbstractContainerState;

public class WindowTabComponentState extends AbstractContainerState implements WindowTabComponent {

    private final InternalRibbon ribbon;

    private final WindowTabComponentPattern pattern;

    WindowTabComponentState(final WindowTabComponentPattern pattern) {
        super(pattern);
        this.pattern = pattern;
        if (pattern.getRibbon() != null) {
            this.ribbon = pattern.getRibbon().getCopy();
        } else {
            this.ribbon = null;
        }
    }

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        passVisibleFromJson(json);
        requestRender();
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = new JSONObject();
        if (pattern.getContextualHelpUrl() != null) {
            json.put("contextualHelpUrl", pattern.getContextualHelpUrl());
        }
        if (ribbon != null) {
            InternalRibbon differenceRibbon = ribbon.getUpdate();
            if (differenceRibbon != null) {
                json.put("ribbon", RibbonUtils.translateRibbon(differenceRibbon, getLocale(), pattern));
            }
        }
        return json;
    }

    @Override
    public Ribbon getRibbon() {
        return ribbon;
    }
}
