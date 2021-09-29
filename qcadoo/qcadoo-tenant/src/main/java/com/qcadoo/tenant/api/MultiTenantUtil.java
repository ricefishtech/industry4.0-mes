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
package com.qcadoo.tenant.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Utilities for managin
 * 
 * 
 */
@Service
public class MultiTenantUtil {

    @Autowired
    private MultiTenantService multiTenantService;

    private static MultiTenantUtil instance;

    /**
     * Initialize utilities instance
     */
    @PostConstruct
    public void init() {
        initialise(this);
    }

    private static void initialise(final MultiTenantUtil multiTenantUtil) {
        MultiTenantUtil.instance = multiTenantUtil;
    }

    /**
     * Do callback in multitenant context.
     * 
     * @param callback
     */
    public static void doInMultiTenantContext(final MultiTenantCallback callback) {
        MultiTenantUtil.instance.multiTenantService.doInMultiTenantContext(callback);
    }

    /**
     * Do callback in multitenant context for given tenant id.
     * 
     * @param tenantId
     * @param callback
     */
    public static void doInMultiTenantContext(final int tenantId, final MultiTenantCallback callback) {
        MultiTenantUtil.instance.multiTenantService.doInMultiTenantContext(tenantId, callback);
    }

    /**
     * Get current tenant id.
     * 
     * @return tenant id
     */
    public static int getCurrentTenantId() {
        return MultiTenantUtil.instance.multiTenantService.getCurrentTenantId();
    }

}
