/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 * <p>
 * This file is part of Qcadoo.
 * <p>
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.view.internal.hooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.qcadoo.model.internal.hooks.AbstractHookDefinition;
import com.qcadoo.model.internal.hooks.HookInitializationException;
import com.qcadoo.plugin.api.PluginUtils;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.internal.ViewHookDefinition;

public abstract class AbstractViewHookDefinition extends AbstractHookDefinition implements ViewHookDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractViewHookDefinition.class);

    public AbstractViewHookDefinition(final String className, final String methodName, final String pluginIdentifier,
            final ApplicationContext applicationContext) throws HookInitializationException {
        super(className, methodName, pluginIdentifier, applicationContext);
    }

    @Override
    protected Object performCall(final Object... args) {
        if (getPluginIdentifier() != null && !PluginUtils.isEnabled(getPluginIdentifier())) {
            return null;
        }
        try {
            return super.performCall(args);
        } catch (Exception e) {

            LOG.warn("Failed to invoke view hook", e);

            if (args != null && args[0] != null && args[0] instanceof ViewDefinitionState) {
                ViewDefinitionState viewDefinitionState = (ViewDefinitionState) args[0];
                viewDefinitionState.addMessage("qcadooView.errorPage.error.internalError.explanation",
                        ComponentState.MessageType.FAILURE);
            }
        }
        return null;
    }

}
