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

public class CollectionArgumentChoiceTest {

    private CollectionArgumentChoice<Integer> choice = new CollectionArgumentChoice<Integer>(
            1, 2, 3);

    @Test
    public void testContains() {
        assertTrue(choice.contains(2));
        assertFalse(choice.contains(0));
    }

    @Test
    public void testContainsWithEmptyCollection() {
        CollectionArgumentChoice<Integer> choice = new CollectionArgumentChoice<Integer>();
        assertFalse(choice.contains(0));
        assertFalse(choice.contains("0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsWithWrongType() {
        choice.contains("2");
    }

    @Test
    public void testTextualFormat() {
        assertEquals("{1,2,3}", choice.textualFormat());
    }

    @Test
    public void testToString() {
        assertEquals("{1,2,3}", choice.toString());
    }


    @Test
    public void testSimpleEnum() {
        CollectionArgumentChoice<Simple> c = new CollectionArgumentChoice<Simple>(Simple.values());
        assertTrue(c.contains(Simple.B));
    }

    @Test
    public void testAbstractEnum() {
        CollectionArgumentChoice<Fancy> c = new CollectionArgumentChoice<Fancy>(Fancy.values());
        assertTrue(c.contains(Fancy.B));
    }

    private enum Simple {A, B, C}

    private enum Fancy {
        A {
            @Override
            String getFoo() {
                return "aaa";
            }
        },
        B {
            @Override
            String getFoo() {
                return "bbb";
            }
        },
        C {
            @Override
            String getFoo() {
                return "ccc";
            }
        };
        abstract String getFoo();
    }
}
