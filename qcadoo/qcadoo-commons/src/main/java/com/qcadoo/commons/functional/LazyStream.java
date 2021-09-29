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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * Infinite, lazy evaluated stream containing value of any arbitrary type. Stream will be immutable if their elements are
 * immutable.
 * 
 * HashCode & Equality: Behaviour of equals and hashCode depends on the behaviour of these two methods within element and also
 * passed induction function. If passed function doesn't override equals/hashCode, LazyStream.equals will return false for the 2
 * identical LazyStreams which uses two separate instances of the same function. Be aware of this.
 * 
 * Thread safety: Current implementation is not thread-safe.
 * 
 * Terminating: Because this collection has indefinable length, simply iteration over its element will cause infinite loop. To
 * obtain arbitrary number of elements, use Iterables.limit(lazyStream, n) or FluentIterable.from(lazyStream).limit(n)
 * 
 * Alternatively you can use head() & tail() methods to traverse the stream 'manually'.
 * 
 * @param <T>
 *            type of Stream elements. If type T is immutable, then whole Stream will be also immutable.
 * @since 1.3.0
 */
public class LazyStream<T> implements Iterable<T> {

    private final T head;

    private LazyStream<T> lazyTail;

    private final Function<T, T> inductionStep;

    /**
     * Create a new instance of LazyStream.
     * 
     * @param firstElement
     *            first element, will be used to calculate tail (and therefore alternate element)
     * @param inductionStep
     *            function consuming current stream's element and producing next one.
     * @param <U>
     *            type of Stream elements.
     * @return new instance of LazyStream
     */
    public static <U> LazyStream<U> create(final U firstElement, final Function<U, U> inductionStep) {
        return new LazyStream<U>(firstElement, inductionStep);
    }

    private LazyStream(final T head, final Function<T, T> inductionStep) {
        this.head = head;
        this.inductionStep = inductionStep;
    }

    /**
     * Return underlying value, contained in this particular LazyStream chain.
     * 
     * @return underlying value
     */
    public T head() {
        return head;
    }

    /**
     * Returns forwarding elements as a LazyStream. Returned stream is generated lazily, when you're calling tail() for the first
     * time, and stored for further invocations. Therefore further invocations of tail() will not evaluate induction function.
     * 
     * @return alternate elements as a LazyStream.
     */
    public LazyStream<T> tail() {
        // synchronize me to obtain thread-safety
        if (lazyTail == null) {
            lazyTail = new LazyStream<T>(inductionStep.apply(head), inductionStep);
        }
        return lazyTail;
    }

    public LazyStream<T> dropWhile(final Predicate<T> predicate) {
        LazyStream<T> res = this;
        while (!predicate.apply(res.head())) {
            res = res.tail();
        }
        return res;
    }

    public List<T> takeWhile(final Predicate<T> predicate) {
        List<T> res = Lists.newArrayList();
        LazyStream<T> curr = this;
        while (predicate.apply(curr.head)) {
            res.add(curr.head);
            curr = curr.tail();
        }
        return res;
    }

    @Override
    public Iterator<T> iterator() {
        return new LazyStreamIterator<T>(this);
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
        LazyStream rhs = (LazyStream) obj;
        // I'm not sure about using inductionStep to prove objects' equality.
        return new EqualsBuilder().append(this.head, rhs.head).append(this.inductionStep, rhs.inductionStep).isEquals();
    }

    @Override
    public int hashCode() {
        // I'm not sure about using inductionStep to calculate object's hashCode.
        return new HashCodeBuilder().append(head).append(inductionStep).toHashCode();
    }

}

class LazyStreamIterator<T> implements Iterator<T> {

    private LazyStream<T> stream;

    LazyStreamIterator(final LazyStream<T> forStream) {
        this.stream = forStream;
    }

    @Override
    public boolean hasNext() {
        // LazyStream is infinite
        return true;
    }

    @Override
    public T next() {
        // cut-off and return a stream's head and then reassign underlying stream to its tail.
        T head = stream.head();
        stream = stream.tail();
        return head;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from functional LazyStream");
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
        LazyStreamIterator rhs = (LazyStreamIterator) obj;
        return Objects.equals(this.stream, rhs.stream);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stream);
    }
}
