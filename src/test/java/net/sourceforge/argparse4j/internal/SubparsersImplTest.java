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
