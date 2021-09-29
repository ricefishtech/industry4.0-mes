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
package com.qcadoo.report.api;

/**
 * Footer definition object.
 * 
 * @author krzysztofnadolski
 * 
 */
public class Footer {

    private final String page;

    private final String in;

    private final String companyName;

    private final String address;

    private final String phoneEmail;

    private final String generatedBy;

    private final String additionalText;

    public Footer() {
        this.page = "";
        this.in = "";
        this.companyName = "";
        this.address = "";
        this.phoneEmail = "";
        this.generatedBy = "";
        this.additionalText = "";
    }

    public Footer(String page, String in, String companyName, String address, String phoneEmail, String generatedBy,
            String additionalText) {
        this.page = page;
        this.in = in;
        this.companyName = companyName;
        this.address = address;
        this.phoneEmail = phoneEmail;
        this.generatedBy = generatedBy;
        this.additionalText = additionalText;
    }

    public String getPage() {
        return page;
    }

    public String getIn() {
        return in;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneEmail() {
        return phoneEmail;
    }

}
