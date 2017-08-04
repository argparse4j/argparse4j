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

import net.sourceforge.argparse4j.helper.TextHelper;
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

    private T min_;
    private T max_;

    /**
     * Creates object using range [{@code min}, {@code max}], inclusive.
     * 
     * @param min
     *            The lower bound of the range, inclusive.
     * @param max
     *            The upper bound of the range, inclusive.
     */
    public RangeArgumentChoice(T min, T max) {
        min_ = min;
        max_ = max;
    }

    @Override
    public boolean contains(Object val) {
        if (min_.getClass().equals(val.getClass())) {
            //noinspection unchecked
            T v = (T) val;
            return min_.compareTo(v) <= 0 && 0 <= max_.compareTo(v);
        } else {
            throw new IllegalArgumentException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "type mismatch (Make sure that you specified correct Argument.type()):"
                            + " expected: %s actual: %s", min_.getClass()
                            .getName(), val.getClass().getName()));
        }
    }

    @Override
    public String textualFormat() {
        return String.format(TextHelper.LOCALE_ROOT, "{%s..%s}", min_, max_);
    }

    @Override
    public String toString() {
        return textualFormat();
    }
}
