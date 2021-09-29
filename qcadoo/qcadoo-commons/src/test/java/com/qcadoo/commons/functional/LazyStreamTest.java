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

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class LazyStreamTest {

    private static final Function<Integer, Integer> INCREMENT_BY_ONE = new Function<Integer, Integer>() {

        @Override
        public Integer apply(final Integer input) {
            return input + 1;
        }
    };

    @Test
    public final void shouldGenerateStream() {
        // when
        LazyStream<Integer> stream = LazyStream.create(1, INCREMENT_BY_ONE);

        // then
        Assert.assertEquals(1, (int) stream.head());
        Assert.assertEquals(INCREMENT_BY_ONE.apply(1), stream.tail().head());
        Assert.assertEquals(INCREMENT_BY_ONE.apply(INCREMENT_BY_ONE.apply(1)), stream.tail().tail().head());
    }

    @Test
    public final void shouldBeIterable() {
        // given
        LazyStream<Integer> stream = LazyStream.create(1, INCREMENT_BY_ONE);

        // when
        List<Integer> iterableValues = FluentIterable.from(stream).limit(5).toList();

        // then
        Assert.assertEquals(Lists.newArrayList(1, 2, 3, 4, 5), iterableValues);
    }

    @Test
    public final void shouldGenerateAlternateElementsDueToGivenFunction() {
        // given
        final Function<LocalDate, LocalDate> NEXT_WORKING_DAY = new Function<LocalDate, LocalDate>() {

            @Override
            public LocalDate apply(final LocalDate prevDate) {
                int prevDateDayOfWeek = prevDate.getDayOfWeek();
                if (prevDateDayOfWeek == 5) {
                    return prevDate.plusDays(3);
                }
                if (prevDateDayOfWeek == 6) {
                    return prevDate.plusDays(2);
                }
                return prevDate.plusDays(1);
            }
        };
        LocalDate thursday = new LocalDate(2014, 8, 14);
        LazyStream<LocalDate> stream = LazyStream.create(thursday, NEXT_WORKING_DAY);

        // when
        List<LocalDate> nextFourDays = FluentIterable.from(stream).limit(4).toList();

        // then
        LocalDate friday = thursday.plusDays(1);
        LocalDate monday = friday.plusDays(3);
        LocalDate tuesday = monday.plusDays(1);
        Assert.assertEquals(Lists.newArrayList(thursday, friday, monday, tuesday), nextFourDays);
    }

    @Test
    public final void shouldDropFirstNElementsThatDoNotMatchPredicate() {
        // given
        LazyStream<Integer> stream = LazyStream.create(1, INCREMENT_BY_ONE);

        // when
        LazyStream<Integer> dropResult = stream.dropWhile(new Predicate<Integer>() {

            @Override
            public boolean apply(final Integer input) {
                return input > 5;
            }
        });

        // then
        Assert.assertEquals((Integer) 6, dropResult.head());
    }

    @Test
    public final void shouldTakeFirstNElementsThatMatchPredicate() {
        // given
        LazyStream<Integer> stream = LazyStream.create(1, INCREMENT_BY_ONE);

        // when
        List<Integer> takeResult = stream.takeWhile(new Predicate<Integer>() {

            @Override
            public boolean apply(final Integer input) {
                return input <= 5;
            }
        });

        // then
        Assert.assertEquals(Lists.newArrayList(1, 2, 3, 4, 5), takeResult);
    }

}
