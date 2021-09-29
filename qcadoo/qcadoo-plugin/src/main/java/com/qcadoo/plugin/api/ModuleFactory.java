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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import org.jdom.Element;

/**
 * Factory responsible for parsing descriptors and creating instances of {@link Module}.
 * 
 * @see Plugin plugins and modules lifecycle
 * @since 0.4.0
 */
public abstract class ModuleFactory<T extends Module> {

    /**
     * Callback is invoke once on application startup, before the {@link Module#init()}.
     */
    public void preInit() {
        // empty
    }

    /**
     * Callback is invoke once on application startup, after the {@link Module#init()}.
     */
    public void postInit() {
        // empty
    }

    /**
     * Parses descriptor and creates instance of {@link Module}.
     * 
     * @param pluginIdentifier
     *            plugin identifier
     * @param element
     *            xml element describing module
     * @return module instance
     * @throws ModuleException
     *             when some exception occured
     */
    public final T parse(final String pluginIdentifier, final Element element) {
        try {
            return parseElement(pluginIdentifier, element);
        } catch (Exception e) {
            throw new ModuleException(pluginIdentifier, getIdentifier(), element, e);
        }
    }

    protected abstract T parseElement(final String pluginIdentifier, final Element element);

    /**
     * Identifier is used to distinguish the type of the module. It is equal to the name of the tag in section "modules" in plugin
     * descriptor.
     * 
     * @return module identifier
     */
    public abstract String getIdentifier();

    /**
     * Returns element attribute or null when attribute was not defined.
     * 
     * @param element
     *            node element
     * @param attributeName
     *            name of attribute
     * 
     * @return element attribute or null
     */
    protected final String getAttribute(final Element element, final String attributeName) {
        return element.getAttributeValue(attributeName);
    }

    /**
     * Returns element attribute. Throws exception when attribute was not defined.
     * 
     * @param element
     *            node element
     * @param attributeName
     *            name of attribute
     * 
     * @return element attribute
     * @throws NullPointerException
     *             when attribute was not defined
     */
    protected final String getRequiredAttribute(final Element element, final String attributeName) {
        String attribute = getAttribute(element, attributeName);
        checkNotNull(attribute, "Missing " + attributeName + " attribute of " + getIdentifier() + " module");
        return attribute;
    }

    /**
     * Returns inner element. Throws exception when element is empty or element contains more than one inner elements.
     * 
     * @param element
     *            node element
     * 
     * @return inner element
     * @throws IllegalStateException
     *             when element is empty or element contains more than one inner elements
     */
    protected final Element getOneElementContent(final Element element) {
        @SuppressWarnings("unchecked")
        List<Element> elements = element.getChildren();
        checkState(!elements.isEmpty(), "Missing content of " + getIdentifier() + " module");
        checkState(elements.size() == 1, "Only one element can be defined in single " + getIdentifier() + " module");
        return elements.get(0);
    }
}