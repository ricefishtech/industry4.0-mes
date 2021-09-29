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
package com.qcadoo.plugin.internal.aop;

import static com.qcadoo.plugin.api.PluginUtils.isEnabled;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.qcadoo.plugin.api.RunIfEnabled;

@Aspect
public class RunIfEnabledAspect {

    @Around("(execution(* *(..)) || (adviceexecution() && !args(org.aspectj.lang.ProceedingJoinPoint, ..))) && @annotation(annotation)")
    public Object runMethodIfEnabledAdvice(final ProceedingJoinPoint pjp, final RunIfEnabled annotation) throws Throwable {
        return runIfEnabled(pjp, null, annotation);
    }

    @Around("(execution(* *(..)) || (adviceexecution() && !args(org.aspectj.lang.ProceedingJoinPoint, ..))) && @within(annotation) && !@annotation(com.qcadoo.plugin.api.RunIfEnabled)")
    public Object runClassMethodIfEnabledAdvice(final ProceedingJoinPoint pjp, final RunIfEnabled annotation) throws Throwable {
        return runIfEnabled(pjp, null, annotation);
    }

    @Around("adviceexecution() && args(innerPjp, ..) && @annotation(annotation)")
    public Object runAroundAdviceIfEnabledAdvice(final ProceedingJoinPoint pjp, final ProceedingJoinPoint innerPjp,
            final RunIfEnabled annotation) throws Throwable {
        return runIfEnabled(pjp, innerPjp, annotation);
    }

    @Around("adviceexecution() && args(innerPjp, ..) && @within(annotation) && !@annotation(com.qcadoo.plugin.api.RunIfEnabled)")
    public Object runAspectAroundIfEnabledAdvice(final ProceedingJoinPoint pjp, final ProceedingJoinPoint innerPjp,
            final RunIfEnabled annotation) throws Throwable {
        return runIfEnabled(pjp, innerPjp, annotation);
    }

    private Object runIfEnabled(final ProceedingJoinPoint pjp, final ProceedingJoinPoint innerPjp, final RunIfEnabled annotation)
            throws Throwable {
        Object result = null;
        if (pluginsAreEnabled(annotation.value())) {
            result = pjp.proceed();
        } else if (innerPjp != null) {
            result = innerPjp.proceed();
        }
        return result;
    }

    private boolean pluginsAreEnabled(final String[] pluginIdentifiers) {
        for (String pluginIdentifier : pluginIdentifiers) {
            if (!isEnabled(pluginIdentifier)) {
                return false;
            }
        }
        return true;
    }
}
