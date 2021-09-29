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

import java.util.Map;

/**
 * ContainerState is a instance of single view element which can contain other components. Form more informations see
 * 
 * @link com.qcadoo.view.api.ComponentState}.
 * 
 * @since 0.4.0
 * 
 * @see com.qcadoo.view.api.ComponentState
 * @see com.qcadoo.view.internal.api.ContainerPattern
 */
public interface ContainerState extends InternalComponentState {

    /**
     * Returns map of all children components of this component by pair name -> component
     * 
     * @return map of all children components
     */
    Map<String, InternalComponentState> getChildren();

    /**
     * Returns child component with specified name or null if no such component can be found
     * 
     * @param name
     *            name of child component
     * @return child component with specified name
     */
    InternalComponentState getChild(String name);

    /**
     * Adds new child to this component
     * 
     * @param state
     *            child to add
     */
    void addChild(InternalComponentState state);

    /**
     * Returns child component with specified name or null if no such component found
     * 
     * @param name
     *            name of component
     * @return component with specified name
     * 
     * @throws NullPointerException
     *             if name is null
     */
    InternalComponentState findChild(String name);

}
