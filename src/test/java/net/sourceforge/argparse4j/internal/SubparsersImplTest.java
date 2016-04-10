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
package net.sourceforge.argparse4j.internal;

import static org.junit.Assert.*;

import org.junit.Test;

public class SubparsersImplTest {

    @Test
    public void testFormatShortSyntax() {
        SubparsersImpl subparsers = new SubparsersImpl(new ArgumentParserImpl(
                "prog"));
        assertEquals("{}", subparsers.formatShortSyntax());
        subparsers.addParser("install");
        assertEquals("{install}", subparsers.formatShortSyntax());
        subparsers.addParser("checkout");
        assertEquals("{install,checkout}", subparsers.formatShortSyntax());

        subparsers.metavar("COMMAND");
        assertEquals("COMMAND", subparsers.formatShortSyntax());
    }

    @Test
    public void testAddParserNotUnique() {
        SubparsersImpl subparsers = new SubparsersImpl(new ArgumentParserImpl(
                "prog"));
        SubparserImpl subparser = subparsers.addParser("checkout");
        try {
            subparsers.addParser("checkout");
        } catch(IllegalArgumentException e) {
            assertEquals("command 'checkout' has been already used", e.getMessage());
        }
    }

    @Test
    public void testAddAlias() {
        SubparsersImpl subparsers = new SubparsersImpl(new ArgumentParserImpl(
                "prog"));
        SubparserImpl subparser = subparsers.addParser("checkout");
        subparsers.addAlias(subparser, "co", "out");
        assertTrue(subparsers.getCommands().contains("co"));
        assertTrue(subparsers.getCommands().contains("out"));
        try {
            subparsers.addAlias(subparser, "co");
        } catch(IllegalArgumentException e) {
            assertEquals("command 'co' has been already used", e.getMessage());
        }
    }
}
