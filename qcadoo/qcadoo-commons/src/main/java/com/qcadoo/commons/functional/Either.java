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

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Function;

/**
 * 
 * @param <L>
 * @param <R>
 */
public abstract class Either<L, R> {

    public static <L2, R2> Left<L2, R2> left(final L2 value) {
        return new Left(value);
    }

    public static <L2, R2> Right<L2, R2> right(final R2 value) {
        return new Right(value);
    }

    private Either() {
    }

    public abstract boolean isLeft();

    public boolean isRight() {
        return !isLeft();
    }

    public L getLeft() {
        throw new IllegalStateException("Calling getLeft() on Right!");
    }

    public R getRight() {
        throw new IllegalStateException("Calling getRight() on Left!");
    }

    public abstract <V> V fold(final Function<? super L, V> ifLeft, final Function<? super R, V> ifRight);

    public abstract <V> Either<L, V> map(final Function<? super R, V> f);

    public abstract <V> Either<L, V> flatMap(final Function<? super R, Either<L, V>> f);

    private static final class Left<L, R> extends Either<L, R> {

        private final L value;

        private Left(final L value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public <V> V fold(final Function<? super L, V> ifLeft, final Function<? super R, V> ignored) {
            return ifLeft.apply(value);
        }

        @Override
        public <V> Either<L, V> map(final Function<? super R, V> f) {
            return Either.left(value);
        }

        @Override
        public <V> Either<L, V> flatMap(final Function<? super R, Either<L, V>> f) {
            return Either.left(value);
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
            Left oth = (Left) obj;
            return ObjectUtils.equals(value, oth.value);
        }

        @Override
        public int hashCode() {
            return ObjectUtils.hashCode(value);
        }

        @Override
        public String toString() {
            return String.format("Left(%s)", ObjectUtils.toString(value));
        }
    }

    private static final class Right<L, R> extends Either<L, R> {

        private final R value;

        private Right(final R value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public R getRight() {
            return value;
        }

        @Override
        public <V> V fold(final Function<? super L, V> ignored, final Function<? super R, V> ifRight) {
            return ifRight.apply(value);
        }

        @Override
        public <V> Either<L, V> map(final Function<? super R, V> f) {
            return Either.right(f.apply(value));
        }

        @Override
        public <V> Either<L, V> flatMap(final Function<? super R, Either<L, V>> f) {
            return f.apply(value);
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
            Right oth = (Right) obj;
            return ObjectUtils.equals(value, oth.value);
        }

        @Override
        public int hashCode() {
            return ObjectUtils.hashCode(value);
        }

        @Override
        public String toString() {
            return String.format("Right(%s)", ObjectUtils.toString(value));
        }
    }

}
