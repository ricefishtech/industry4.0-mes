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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.qcadoo.model.internal.hooks.HookInitializationException;

@Service
public final class HookFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public ViewEventListenerHook buildViewEventListener(final String eventName, final String fullyQualifiedClassName,
            final String methodName, final String pluginIdentifier) {
        try {
            return new ViewEventListenerHook(eventName, fullyQualifiedClassName, methodName, pluginIdentifier, applicationContext);
        } catch (HookInitializationException e) {
            throw new IllegalArgumentException("Can't build view listener hook.", e);
        }
    }

    public ViewLifecycleHook buildViewLifecycleHook(final String fullyQualifiedClassName, final String methodName,
            final String pluginIdentifier, final HookType type) {
        Preconditions.checkArgument(type != null, "View hook type have to be passed!");
        try {
            return new ViewLifecycleHook(fullyQualifiedClassName, methodName, pluginIdentifier, applicationContext, type);
        } catch (HookInitializationException e) {
            throw new IllegalArgumentException("Can't build view lifecycle hook.", e);
        }
    }

    public ViewConstructionHook buildViewConstructionHook(final String fullyQualifiedClassName, final String methodName,
            final String pluginIdentifier) {
        try {
            return new ViewConstructionHook(fullyQualifiedClassName, methodName, pluginIdentifier, applicationContext);
        } catch (HookInitializationException e) {
            throw new IllegalArgumentException("Can't build view construction hook.", e);
        }
    }

}
