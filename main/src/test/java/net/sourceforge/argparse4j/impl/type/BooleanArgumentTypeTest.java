/*
 * Copyright (C) 2015 Tatsuhiro Tsujikawa
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
package net.sourceforge.argparse4j.impl.type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Before;
import org.junit.Test;

public class BooleanArgumentTypeTest {

    private MockArgument ma;

    @Before
    public void setup() {
        ma = new MockArgument();
    }

    @Test
    public void testConvert() throws ArgumentParserException {
        ArgumentParser ap = ArgumentParsers
                .newFor("argparse4j").locale(Locale.US).build();
        BooleanArgumentType t = new BooleanArgumentType();

        assertEquals(Boolean.TRUE, t.convert(null, ma, "true"));
        assertEquals(Boolean.FALSE, t.convert(null, ma, "false"));

        try {
            t.convert(ap, ma, "TRUE");
            fail();
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: could not convert 'TRUE' (choose from {true,false})",
                    e.getMessage());
        }

        t = new BooleanArgumentType("yes", "no");

        assertEquals(Boolean.TRUE, t.convert(null, ma, "yes"));
        assertEquals(Boolean.FALSE, t.convert(null, ma, "no"));

        try {
            t.convert(ap, ma, "Yes");
            fail();
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: could not convert 'Yes' (choose from {yes,no})",
                    e.getMessage());
        }
    }

    @Test
    public void testInferMetavar() {
        BooleanArgumentType t = new BooleanArgumentType("yes", "no");

        assertArrayEquals(new String[] { "{yes,no}" }, t.inferMetavar());
    }

}
