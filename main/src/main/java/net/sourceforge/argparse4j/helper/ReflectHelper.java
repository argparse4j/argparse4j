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
package net.sourceforge.argparse4j.helper;

import java.lang.reflect.Array;
import java.util.List;

/**
 * <p>
 * This class provides helper functions related to reflection.
 * </p>
 * <p>
 * <strong>The application code should not use this class directly.</strong>
 * </p>
 */
public final class ReflectHelper {

    private ReflectHelper() {
    }

    /**
     * <p>
     * Convert {@code src} to object of type {@code targetType} recursively
     * </p>
     * 
     * <p>
     * Convert {@code src} to object of type {@code targetType} recursively, but
     * it only converts {@link List} to array. If {@code targetType} is array
     * type and {@code src} is {@link List}, new array is created with the size
     * of {@code src} and for each element of {@code src},
     * this method will be called recursively with the
     * component type of {@code targetType} and the element of {@code src}. The
     * returned object is assigned to newly created array. If either
     * {@code targetType} is not array or {@code src} is not {@link List},
     * simply returns {@code src}.
     * </p>
     * 
     * @param targetType
     *            The target type
     * @param src
     *            The src object
     * @return The converted object
     */
    public static Object list2Array(Class<?> targetType, Object src) {
        if (targetType.isArray() && src instanceof List) {
            List<?> list = (List<?>) src;
            int len = list.size();
            Object dest = Array.newInstance(targetType.getComponentType(), len);
            for (int i = 0; i < len; ++i) {
                Array.set(dest, i,
                        list2Array(targetType.getComponentType(), list.get(i)));
            }
            return dest;
        } else {
            return src;
        }
    }
}
