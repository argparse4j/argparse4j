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
package net.sourceforge.argparse4j.impl.action;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Before;
import org.junit.Test;

public class AppendConstArgumentActionTest {

    private static class MyMockArgument extends MockArgument {
        
        @Override
        public String getDest() {
            return "dest";
        }

        @Override
        public Object getConst() {
            return "const";
        }
        
    }
    
    private MyMockArgument arg = new MyMockArgument();
    private AppendConstArgumentAction act = new AppendConstArgumentAction();
    private Map<String, Object> attrs;
    
    @Before
    public void setup() {
        attrs = new HashMap<>();
    }
    
    @Test
    public void testRun() {
        act.run(null, arg, attrs, null, null);
        assertEquals(singletonList("const"), attrs.get("dest"));
        act.run(null, arg, attrs, null, null);
        assertEquals(Arrays.asList("const", "const"), attrs.get("dest"));
    }

    @Test
    public void testRunWithDefaultNonList() {
        attrs.put("dest", "default");
        act.run(null, arg, attrs, null, null);
        assertEquals(singletonList("const"), attrs.get("dest"));
    }

}
