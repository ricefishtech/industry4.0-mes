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
package com.qcadoo.view.internal.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.api.ComponentPattern;
import com.qcadoo.view.internal.api.ViewComponentsResolver;

@Service
public final class ViewComponentsResolverImpl implements ViewComponentsResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ViewComponentsResolverImpl.class);

    private final Map<String, Class<? extends ComponentPattern>> components = new HashMap<String, Class<? extends ComponentPattern>>();

    @Override
    public boolean hasComponent(final String componentName) {
        return components.containsKey(componentName);
    }

    @Override
    public Set<String> getAvailableComponents() {
        return components.keySet();
    }

    @Override
    public Class<? extends ComponentPattern> getComponentClass(final String componentName) {
        return components.get(componentName);
    }

    @Override
    public ComponentPattern getComponentInstance(final String componentName, final ComponentDefinition componentDefinition) {
        Class<? extends ComponentPattern> clazz = getComponentClass(componentName);

        if (clazz == null) {
            throw new IllegalStateException("Unsupported component: " + componentName);
        }

        try {
            Constructor<? extends ComponentPattern> constructor = clazz.getConstructor(ComponentDefinition.class);
            return constructor.newInstance(componentDefinition);
        } catch (SecurityException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getTargetException().getMessage(), e.getTargetException());
        }
    }

    @Override
    public void register(final String name, final Class<? extends ComponentPattern> clazz) {
        LOG.info("Registering view component " + name);

        if (components.get(name) != null && components.get(name) != clazz) {
            throw new IllegalStateException("Trying to put component " + name + " with different classes: " + clazz + " and "
                    + components.get(name));
        }
        components.put(name, clazz);
    }

}
