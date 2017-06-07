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
package net.sourceforge.argparse4j;

import static org.junit.Assert.assertEquals;

import net.sourceforge.argparse4j.internal.ArgumentParserImpl;

import org.junit.Test;

public class ArgumentParsersTest {

    @Test
    public void testNewArgumentParser() {
        ArgumentParserImpl ap = (ArgumentParserImpl) ArgumentParsers
                .newFor("prog").addHelp(false).prefixChars("+")
                .fromFilePrefix("@").build();
        assertEquals("prog", ap.getProg());
        assertEquals("+", ap.getPrefixChars());
        assertEquals("@", ap.getFromFilePrefixChars());
        // Check +h can be added because addHelp is false
        ap.addArgument("+h");
    }

}
