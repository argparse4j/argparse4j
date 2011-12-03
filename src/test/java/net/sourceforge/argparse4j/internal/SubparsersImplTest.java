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

}
