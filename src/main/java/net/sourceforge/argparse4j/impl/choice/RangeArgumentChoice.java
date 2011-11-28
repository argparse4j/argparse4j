/*
 * Copyright (C) 2011 Tatsuhiro Tsujikawa
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.sourceforge.argparse4j.impl.choice;

import net.sourceforge.argparse4j.inf.ArgumentChoice;

/**
 * <p>
 * Choices from given range.
 * </p>
 * 
 * <p>
 * The given value will be checked if it is in range [min, max], inclusive. The
 * {@code min} and {@code max} are specified in constructor arguments.
 * </p>
 * 
 * @param <T>
 *            The type to compare.
 */
public class RangeArgumentChoice<T extends Comparable<T>> implements
        ArgumentChoice {

    private T min;
    private T max;

    /**
     * Creates object using range [{@code min}, {@code max}], inclusive.
     * 
     * @param min
     *            The lowerbound of the range, inclusive.
     * @param max
     *            The upperbound of the range, inclusive.
     */
    public RangeArgumentChoice(T min, T max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean contains(Object val) {
        try {
            if (min.getClass().equals(val.getClass())) {
                T v = (T) val;
                return min.compareTo(v) <= 0 && 0 <= max.compareTo(v);
            } else {
                throw new IllegalArgumentException(String.format(
                        "type mismatch (Make sure that you specified corrent Argument.type()):"
                                + " expected: %s actual: %s", min.getClass()
                                .getName(), val.getClass().getName()));
            }
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public String textualFormat() {
        return String.format("{%s..%s}", min, max);
    }

    @Override
    public String toString() {
        return textualFormat();
    }
}
