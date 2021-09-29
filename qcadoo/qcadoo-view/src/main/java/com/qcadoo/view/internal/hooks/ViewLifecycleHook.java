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
package com.qcadoo.view.internal.hooks;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.context.ApplicationContext;

import com.qcadoo.model.internal.hooks.HookInitializationException;
import com.qcadoo.plugin.api.PluginUtils;
import com.qcadoo.view.api.ViewDefinitionState;

public class ViewLifecycleHook extends AbstractViewHookDefinition {

    private final HookType type;

    public ViewLifecycleHook(final String className, final String methodName, final String pluginIdentifier,
            final ApplicationContext applicationContext, final HookType type) throws HookInitializationException {
        super(className, methodName, pluginIdentifier, applicationContext);
        this.type = type;
    }

    @Override
    protected Class<?>[] getParameterTypes() {
        return new Class<?>[] { ViewDefinitionState.class };
    }

    public void callWithViewState(final ViewDefinitionState viewDefinitionState) {
        if (getPluginIdentifier() == null || PluginUtils.isEnabled(getPluginIdentifier())) {
            performCall(viewDefinitionState);
        }
    }

    public HookType getType() {
        return type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ViewLifecycleHook rhs = (ViewLifecycleHook) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.type, rhs.type).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(type).toHashCode();
    }

}
