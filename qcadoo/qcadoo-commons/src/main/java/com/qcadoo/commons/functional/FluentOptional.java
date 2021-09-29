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
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public final class FluentOptional<T> {

    private final Optional<T> optional;

    public static <V> FluentOptional<V> fromNullable(final V valueOrNull) {
        return new FluentOptional<V>(Optional.fromNullable(valueOrNull));
    }

    public static <V> FluentOptional<V> wrap(final Optional<V> optional) {
        return new FluentOptional(optional);
    }

    private FluentOptional(final Optional<T> optional) {
        Preconditions.checkArgument(optional != null, "Cannot build FluentOptional wrapper from null reference!");
        this.optional = optional;
    }

    public <U> FluentOptional<U> transform(final Function<T, U> f) {
        return map(f);
    }

    public <U> FluentOptional<U> map(final Function<T, U> f) {
        return new FluentOptional<U>(optional.transform(f));
    }

    public <U> FluentOptional<U> flatMap(final Function<T, Optional<U>> f) {
        return new FluentOptional<U>(Optionals.flatMap(optional, f));
    }

    public T or(final T defaultValue) {
        return optional.or(defaultValue);
    }

    public Optional<T> toOpt() {
        return optional;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        FluentOptional rhs = (FluentOptional) obj;
        return optional.equals(rhs.optional);
    }

    @Override
    public int hashCode() {
        return optional.hashCode();
    }
}
