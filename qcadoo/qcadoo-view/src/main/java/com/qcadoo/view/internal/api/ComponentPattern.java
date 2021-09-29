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
package com.qcadoo.view.internal.api;

import com.qcadoo.view.internal.hooks.ViewEventListenerHook;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserNodeException;
import org.w3c.dom.Node;

import java.util.Locale;
import java.util.Map;

/**
 * ComponentPattern is a definition of single view element. It can be described as 'class' which main responsibility is to create
 * {@link com.qcadoo.view.api.ComponentState} which can be understood as 'instance' created in request scope.
 * <p>
 * Component pattern contains informations about static data (name, path, reference name), relations to other components and
 * events attached to this component.
 * 
 * @since 0.4.0
 * 
 * @see com.qcadoo.view.api.ComponentState
 */
public interface ComponentPattern {

    /**
     * Initialize this pattern. It should be called only once, after creating all ComponentPatterns in structure.
     * 
     * @return true if component was initialized, false when component was already initialized
     */
    boolean initialize();

    /**
     * Initialize this component (by calling initialize) and recurrently initialize all its children (by calling initializeAll on
     * every children component)
     */
    void initializeAll();

    /**
     * Registers some additional views to service if this component creates some. Used by components like 'lookup'.
     * 
     * @param viewDefinitionService
     *            instance of view definition service
     */
    void registerViews(InternalViewDefinitionService viewDefinitionService);

    /**
     * Unregisters component by removing it from {@link com.qcadoo.view.internal.api.ViewDefinition}, removing this component from
     * listeners list of all other components and removing all additional views added by this component from
     * viewDefinitionService.
     * 
     * @param viewDefinitionService
     *            instance of view definition service
     */
    void unregisterComponent(InternalViewDefinitionService viewDefinitionService);

    /**
     * Creates new ComponentState and registers it in ViewDefinitionState.
     * 
     * @param viewDefinitionState
     *            ViewDefinitionState where newly created ComponentState should be registered
     * 
     * @return created ComponentState
     */
    InternalComponentState createComponentState(InternalViewDefinitionState viewDefinitionState);

    /**
     * Returns map necessary to display this component to client. It contains options of this component, translations etc.
     * 
     * @param locale
     *            current localization
     * @return map of options for displaying this component
     */
    Map<String, Object> prepareView(Locale locale);

    /**
     * Returns name of this component
     * 
     * @return name of this component
     */
    String getName();

    /**
     * Returns identifier of plugin which created this component or null, when this identifier is not specified.
     * 
     * @return identifier of plugin which created this component
     */
    String getExtensionPluginIdentifier();

    /**
     * Returns dot separated path to this plugin
     * 
     * @return dot separated path to this plugin
     */
    String getPath();

    /**
     * Returns reference name of this plugin or null if no reference name specified
     * 
     * @return reference name of this plugin
     */
    String getReference();

    /**
     * Returns contextual help URL for this component or null if no URL specified
     * 
     * @return contextual help URL for this component
     */
    String getContextualHelpUrl();

    /**
     * Returns dot separated path to this plugin which doesn't take into consideration layout components.
     * 
     * @return dot separated path to this plugin which doesn't take into consideration layout components
     */
    String getFunctionalPath();

    /**
     * Performs parsing of this component from xml node.
     * 
     * @param componentNode
     *            node of this component definition
     * @param parser
     *            parser
     */
    void parse(Node componentNode, ViewDefinitionParser parser) throws ViewDefinitionParserNodeException;

    /**
     * Adds custom event to this component
     * 
     * @param eventListenerHook
     *            event listener to add
     */
    void addCustomEvent(final ViewEventListenerHook eventListenerHook);

    /**
     * Removes custom event from this component
     * 
     * @param eventListenerHook
     *            event listener to remove
     */
    void removeCustomEvent(final ViewEventListenerHook eventListenerHook);

    /**
     * Checks if field defined by this component is persistent
     * 
     * @return true if field defined by this component is persistent
     */
    boolean isPersistent();

    /**
     * Checks if field defined by this component is enabled by default
     * 
     * @return true if field defined by this component is enabled by default
     */
    boolean isDefaultEnabled();

    /**
     * Returns true if element defined by this component is permanently disabled
     * 
     * @return true if element defined by this component is permanently disabled
     */
    boolean isPermanentlyDisabled();

    /**
     * Returns unique identifier of this component.
     *
     * @return unique identifier of this component
     */
    String getUuid();
}
