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
package com.qcadoo.view.internal;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Preconditions;

/**
 * This class encapsulate dimensions for modal window.
 * 
 * @author marcinkubala
 * @since 1.2.0
 */
public class ModalDimensions {

    /**
     * Default modal width
     */
    public static final int DEFAULT_WIDTH = 1000;

    /**
     * Default modal height
     */
    public static final int DEFAULT_HEIGHT = 560;

    private static final String JSON_WIDTH = "width";

    private static final String JSON_HEIGHT = "height";

    private static final String OPTION_WIDTH = "modalWidth";

    private static final String OPTION_HEIGHT = "modalHeight";

    private final int width;

    private final int height;

    private ModalDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Parse and create new instance of modal dimensions object from component options. Use default values defined in
     * {@link ModalDimensions#DEFAULT_WIDTH} and {@link ModalDimensions#DEFAULT_HEIGHT} if "modalWidth" or/and "modalHeight"
     * attribute was not specified.
     * 
     * @param componentOptions
     *            component's options
     * @return modal dimensions instance.
     */
    public static ModalDimensions parseFromOptions(final Iterable<ComponentOption> componentOptions) {
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        for (ComponentOption option : componentOptions) {
            if (OPTION_WIDTH.equals(option.getType())) {
                width = Integer.parseInt(option.getValue());
                Preconditions.checkState(width > 0, "Value of 'modalWidth' attribute should be greater than 0 (component '%s')");
            } else if (OPTION_HEIGHT.equals(option.getType())) {
                height = Integer.parseInt(option.getValue());
                Preconditions.checkState(height > 0, "Value of 'modalHeight' attribute should be greater than 0 (component'%s')");
            }
        }
        return new ModalDimensions(width, height);
    }

    /**
     * Get modal dimensions as JSON
     * 
     * @return modal dimensions as JSON
     * @throws JSONException
     */
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(JSON_WIDTH, width);
        json.put(JSON_HEIGHT, height);
        return json;
    }

    /**
     * get modal width
     * 
     * @return modal width
     */
    public int getWidth() {
        return width;
    }

    /**
     * get modal height
     * 
     * @return modal height
     */
    public int getHeight() {
        return height;
    }

}
