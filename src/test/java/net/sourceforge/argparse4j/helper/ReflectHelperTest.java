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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.argparse4j.helper.ReflectHelper;

import org.junit.Test;

public class ReflectHelperTest {

    @Test
    public void testList2Array() {
        int a1[] = (int[]) ReflectHelper.list2Array(int[].class, list(1, 2, 3));
        assertArrayEquals(new int[] { 1, 2, 3 }, a1);
        int a2[][] = (int[][]) ReflectHelper.list2Array(int[][].class,
                list(list(1, 2), list(3, 4)));
        assertArrayEquals(
                new int[][] { new int[] { 1, 2 }, new int[] { 3, 4 } }, a2);
    }

    private <T> List<T> list(T... args) {
        return Arrays.asList(args);
    }
}
