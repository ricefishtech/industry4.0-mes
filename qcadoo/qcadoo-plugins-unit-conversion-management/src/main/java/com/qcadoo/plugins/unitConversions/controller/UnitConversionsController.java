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
package com.qcadoo.plugins.unitConversions.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;
import com.qcadoo.plugins.unitConversions.GlobalUnitConversionsAggregateService;
import com.qcadoo.plugins.unitConversions.constants.QcadooUnitConversionsConstants;
import com.qcadoo.view.api.crud.CrudService;

@Controller
public class UnitConversionsController {

    @Autowired
    private GlobalUnitConversionsAggregateService unitConversionsAggregateService;

    @Autowired
    private CrudService crudService;

    @RequestMapping(value = "unitConversions", method = RequestMethod.GET)
    public ModelAndView getUnitConversionsAggregateView(final Locale locale) {
        final Long aggregateId = unitConversionsAggregateService.getAggregateId();
        final Map<String, String> arguments = new HashMap<String, String>();
        final JSONObject json = new JSONObject(ImmutableMap.of("form.id", aggregateId.toString()));
        arguments.put("context", json.toString());

        return crudService.prepareView(QcadooUnitConversionsConstants.PLUGIN_IDENTIFIER,
                QcadooUnitConversionsConstants.VIEW_UNIT_CONVERSIONS, arguments, locale);
    }

}
