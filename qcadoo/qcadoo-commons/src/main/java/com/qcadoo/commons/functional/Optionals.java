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
package com.qcadoo.commons.functional;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;

public final class Optionals {

    private Optionals() {
    }

    public static <F> Function<F, Optional<F>> lift() {
        return new Function<F, Optional<F>>() {

            @Override
            public Optional<F> apply(final F input) {
                return Optional.fromNullable(input);
            }
        };
    }

    public static <F, T> Function<F, Optional<T>> lift(final Function<F, T> f) {
        Function<T, Optional<T>> lift = lift();
        return Functions.compose(lift, f);
    }

    public static <F, T> Optional<T> flatMap(final Optional<F> optValue, final Function<F, Optional<T>> f) {
        return optValue.transform(f).or(Optional.<T> absent());
    }

}
