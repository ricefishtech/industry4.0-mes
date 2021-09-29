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
package com.qcadoo.security.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import com.qcadoo.security.internal.permissionEvaluators.QcadooPermisionEvaluator;

public class QcadooGlobalPermissionEvaluator implements PermissionEvaluator {

    private final Map<String, QcadooPermisionEvaluator> evaluators = new HashMap<String, QcadooPermisionEvaluator>();

    public void setQcadooEvaluators(final Set<QcadooPermisionEvaluator> evaluatorsSet) {
        for (QcadooPermisionEvaluator evaluator : evaluatorsSet) {
            evaluators.put(evaluator.getTargetType(), evaluator);
        }
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Object domainObject, final Object permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Serializable targetId, final String targetType,
            final Object permission) {

        QcadooPermisionEvaluator evaluator = evaluators.get(targetType);
        if (evaluator == null) {
            throw new IllegalArgumentException("there is no evaluator for target type '" + targetType + "'");
        }

        return evaluator.hasPermission(authentication, (String) permission, (String) targetId);
    }

}
