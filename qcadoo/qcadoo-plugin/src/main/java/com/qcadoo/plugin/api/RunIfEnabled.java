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
package com.qcadoo.plugin.api;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

/**
 * Determines execution of annotated (type's) method. Run method(s) only if all specified plug-ins are enabled.
 * 
 * Annotating whole class/aspect is equivalent to annotating each method/advice (also setters and getters!)
 * 
 * This annotation also works with aspects. When applied to {@link Around} advice and required plug-in(s) is not enabled then
 * proceed given {@link ProceedingJoinPoint} omitting thereby execution of advice's body.
 * 
 * @since 1.1.7
 * @author Marcin Kubala
 */
@Retention(RUNTIME)
@Target(value = { TYPE, METHOD })
public @interface RunIfEnabled {

    /**
     * Plug-in identifiers
     * 
     * @return plug-in identifiers
     */
    String[] value();

}
