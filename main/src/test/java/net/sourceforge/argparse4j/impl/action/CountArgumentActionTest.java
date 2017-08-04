/*
 * Copyright (C) 2013 Tatsuhiro Tsujikawa
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
package net.sourceforge.argparse4j.impl.action;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Before;
import org.junit.Test;

public class CountArgumentActionTest {

    private static class MyMockArgument extends MockArgument {

        @Override
        public String getDest() {
            return "dest";
        }
    }

    private MyMockArgument arg;
    private Map<String, Object> attrs;
    private CountArgumentAction act;

    @Before
    public void setup() {
        act = new CountArgumentAction();
        arg = new MyMockArgument();
        attrs = new HashMap<String, Object>();
        attrs.put(arg.getDest(), 0);
    }

    @Test
    public void testRun() throws ArgumentParserException {
        act.run(null, arg, attrs, "-f", null);
        assertEquals(1, attrs.get(arg.getDest()));
        act.run(null, arg, attrs, "-f", null);
        assertEquals(2, attrs.get(arg.getDest()));
    }
}
