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

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;

public interface InternalViewDefinitionState extends ViewDefinitionState {

    /**
     * Performs event on this view. <b>For internal usage only</b>
     * 
     * @param path
     *            dotted separated path and name of component that send this event. If null than this event will be executed on
     *            all components inside this view.
     * @param event
     *            event name
     * @param args
     *            event additional arguments
     */
    void performEvent(String path, String event, String... args);

    /**
     * Registers new component into this view.
     * 
     * @param reference
     *            reference name of newly registered component
     * @param state
     *            component state to register
     */
    void registerComponent(String reference, ComponentState state);
}
