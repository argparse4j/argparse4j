/*
 * Copyright (C) 2012 Tatsuhiro Tsujikawa
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

import static org.junit.Assert.*;

import java.io.File;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.mock.MockArgument;

import org.junit.Test;

public class FileArgumentTypeTest {

    @Test
    public void testConvert() throws ArgumentParserException {
        FileArgumentType type = new FileArgumentType();
        File file = type.convert(null, null, "foobar");
        assertEquals("foobar", file.getName());
        file = new FileArgumentType().verifyCanRead().acceptSystemIn()
                .convert(null, null, "-");
        assertEquals("-", file.getName());
        try {
            new FileArgumentType().verifyCanRead().convert(null,
                    new MockArgument(), "-");
        } catch (ArgumentParserException e) {
            assertEquals(
                    "argument null: Insufficient permissions to read file: '-'",
                    e.getMessage());
        }
    }

}
