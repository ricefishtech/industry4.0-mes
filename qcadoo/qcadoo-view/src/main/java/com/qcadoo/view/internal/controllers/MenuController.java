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
package com.qcadoo.view.internal.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qcadoo.view.internal.menu.MenuService;

@Controller
public final class MenuController {

    @Autowired
    private MenuService menuService;

    @RequestMapping(value = "menu", method = RequestMethod.GET)
    public ResponseEntity<String> getMenu(final Locale locale) {

        String responseBody = menuService.getMenu(locale).getAsJson();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        // disable cache
        responseHeaders.add("Expires", "Tue, 03 Jul 2001 06:00:00 GMT");
        responseHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        responseHeaders.add("Cache-Control", "post-check=0, pre-check=0");
        responseHeaders.add("Pragma", "no-cache");
        try {
            responseHeaders.add("Content-Length", String.valueOf(responseBody.getBytes("utf-8").length));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return new ResponseEntity<String>(responseBody, responseHeaders, HttpStatus.OK);
    }
}
