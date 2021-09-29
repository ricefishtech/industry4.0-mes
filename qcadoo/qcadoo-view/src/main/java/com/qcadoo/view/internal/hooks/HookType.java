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

public enum HookType {
    AFTER_INITIALIZE("afterInitialize", Category.LIFECYCLE_HOOK) {
    },
    BEFORE_RENDER("beforeRender", Category.LIFECYCLE_HOOK) {
    },
    BEFORE_INITIALIZE("beforeInitialize", Category.LIFECYCLE_HOOK) {
    },
    POST_CONSTRUCT("postConstruct", Category.CONSTRUCTION_HOOK) {
    },
    LISTENER("listener", Category.EVENT_LISTENER) {
    };

    public static enum Category {
        EVENT_LISTENER, LIFECYCLE_HOOK, CONSTRUCTION_HOOK;
    }

    private final String nodeName;

    private final Category category;

    private HookType(final String nodeName, final Category category) {
        this.nodeName = nodeName;
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public static HookType parseString(final String nodeName) {
        for (HookType hookType : values()) {
            if (StringUtils.equalsIgnoreCase(nodeName, hookType.nodeName)) {
                return hookType;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown hook type: %s", nodeName));
    }

}
