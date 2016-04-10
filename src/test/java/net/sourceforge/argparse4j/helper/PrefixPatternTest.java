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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class PrefixPatternTest {

    private PrefixPattern pat = new PrefixPattern("-+");

    @Test
    public void testmatch() {
        assertTrue(pat.match("-f"));
        assertTrue(pat.match("+-f"));
        assertTrue(pat.match("+f"));
        assertTrue(!pat.match("f"));
        assertTrue(!pat.match("-"));
        assertTrue(!pat.match("--"));
    }

    @Test
    public void testmatchLongFlag() {
        assertTrue(pat.matchLongFlag("--flag"));
        assertTrue(pat.matchLongFlag("+-+flag"));
        assertTrue(!pat.matchLongFlag("-f"));
        assertTrue(!pat.matchLongFlag("-flag"));
        assertTrue(!pat.matchLongFlag("flag"));
        assertTrue(!pat.match("-"));
        assertTrue(!pat.match("--"));
        assertTrue(!pat.match("---"));
    }

    @Test
    public void testRemovePrefix() {
        assertEquals("foo", pat.removePrefix("--foo"));
        assertEquals("f", pat.removePrefix("+f"));
        assertEquals("foo", pat.removePrefix("foo"));
        assertEquals("-", pat.removePrefix("-"));
        assertEquals("--", pat.removePrefix("--"));
    }
}
