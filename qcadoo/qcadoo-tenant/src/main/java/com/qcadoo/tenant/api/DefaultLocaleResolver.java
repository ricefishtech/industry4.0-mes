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

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Service for resolving locales.
 * 
 * @since 1.1.7
 */
public interface DefaultLocaleResolver {

    /**
     * This method was made to support obtaining locale whenever you can not get them using Spring's {@link LocaleContextHolder},
     * on example during system or plug-in startup.
     * 
     * The main difference between this metod and {@link LocaleContextHolder#getLocale()} is that the second returns locale for
     * current qcadoo user, choosed by them during log-in.
     * 
     * @return default {@link Locale} for current instance/tenant
     */
    Locale getDefaultLocale();

}
