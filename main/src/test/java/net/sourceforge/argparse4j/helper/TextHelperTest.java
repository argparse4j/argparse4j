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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class TextHelperTest {

    @Test
    public void testConcat() {
        assertEquals("", TextHelper.concat(new String[] {}, 0, ","));
        assertEquals("", TextHelper.concat(new String[] {""}, 0, ","));
        assertEquals("alpha",
                TextHelper.concat(new String[] { "alpha" }, 0, ","));
        assertEquals("1, 2, 3",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 0, ", "));
        assertEquals("2, 3",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 1, ", "));
        assertEquals("3",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 2, ", "));
        assertEquals("",
                TextHelper.concat(new Integer[] { 1, 2, 3 }, 3, ", "));

        assertEquals("", TextHelper.concat(emptyList(), 0, ","));
        assertEquals("", TextHelper.concat(singletonList(""), 0, ","));
        assertEquals("alpha",
                TextHelper.concat(singletonList("alpha"), 0, ","));
        assertEquals("1, 2, 3",
                TextHelper.concat(Arrays.asList(1, 2, 3), 0, ", "));
        assertEquals("2, 3",
                TextHelper.concat(Arrays.asList(1, 2, 3), 1, ", "));
        assertEquals("3",
                TextHelper.concat(Arrays.asList(1, 2, 3), 2, ", "));
        assertEquals("",
                TextHelper.concat(Arrays.asList(1, 2, 3), 3, ", "));
    }

    @Test
    public void testTextWrap() {
        String s = String.format(
                   TextHelper.LOCALE_ROOT,
                   "alpha bravo charlie delta echo foxtrot golf hotel india%n"
                 + "juliet kilo lima");
        assertEquals(String.format(
                   TextHelper.LOCALE_ROOT,
                     "        alpha  bravo%n"
                   + "    charlie    delta%n"
                   + "    echo     foxtrot%n"
                   + "    golf       hotel%n"
                   + "    india%n"
                   + "    juliet kilo lima"),
                   TextHelper.wrap(
                           new ASCIITextWidthCounter(), s, 20, 0, "        ", "    "));
    }

    @Test
    public void testAdjustSpace() {
        StringBuilder s1 = new StringBuilder("  Do you like Java language?  ");
        assertEquals("Do  you  like  Java  language?",
                TextHelper.adjustSpace(s1, 30, 30).toString());
        assertEquals("Do  you  like   Java  language?",
                TextHelper.adjustSpace(s1, 31, 30).toString());
        StringBuilder s2 = new StringBuilder();
        assertEquals("", TextHelper.adjustSpace(s2, 30, 30).toString());
        StringBuilder s3 = new StringBuilder("The Argparse4j");
        assertEquals("The       Argparse4j", TextHelper.adjustSpace(s3, 20, 14).toString());
    }
}
