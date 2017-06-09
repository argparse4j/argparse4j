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

import static org.junit.Assert.*;

import org.junit.Test;

public class RangeArgumentChoiceTest {

    private RangeArgumentChoice<Integer> choice = new RangeArgumentChoice<Integer>(0, 255);
    
    @Test
    public void testContains() {
        assertFalse(choice.contains(-1));
        assertTrue(choice.contains(0));
        assertTrue(choice.contains(10));
        assertTrue(choice.contains(255));
        assertFalse(choice.contains(256));
    }

    @Test
    public void testContainsWithWrongType() {
        try {
            choice.contains("10");
            fail();
        } catch(IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testTextualFormat() {
        assertEquals("{0..255}", choice.textualFormat());
        assertEquals("{0.3..0.9}", new RangeArgumentChoice<Double>(0.3, 0.9).textualFormat());
        assertEquals("{a..z}", new RangeArgumentChoice<String>("a", "z").textualFormat());
    }

    @Test
    public void testToString() {
        assertEquals("{0..255}", choice.toString());
    }

}
