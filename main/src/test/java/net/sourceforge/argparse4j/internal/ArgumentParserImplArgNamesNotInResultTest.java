package net.sourceforge.argparse4j.internal;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ArgumentParserImplArgNamesNotInResultTest {
    @Test
    public void singlePrefixOneWordNamedSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a");

        Namespace namespace = parser.parseArgs(new String[]{"-a", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void singlePrefixTwoWordsNamedSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"-a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singlePrefixTwoWordsNamedDoesNotAddName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a-b");

        Namespace namespace = parser.parseArgs(new String[]{"-a-b", "value"});

        assertNull(namespace.get("a-b"));
    }

    @Test
    public void doublePrefixWordNamedSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("--a");

        Namespace namespace = parser.parseArgs(new String[]{"--a", "value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void doublePrefixTwoWordsNamedSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"--a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedAfterSinglePrefixSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a", "--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"--a-b", "value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedDoesNotAddName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"--a-b", "value"});

        assertNull(namespace.get("a-b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedAfterSinglePrefixDoesNotAddName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("-a", "--a-b");

        Namespace namespace = parser.parseArgs(new String[]{"--a-b", "value"});

        assertNull(namespace.get("a-b"));
    }

    @Test
    public void oneWordPositionalSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("a");

        Namespace namespace = parser.parseArgs(new String[]{"value"});

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void twoWordPositionalSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("a-b");

        Namespace namespace = parser.parseArgs(new String[]{"value"});

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void twoWordPositionalDoesNotAddName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        parser.addArgument("a-b");

        Namespace namespace = parser.parseArgs(new String[]{"value"});

        assertNull(namespace.get("a-b"));
    }

    @Test
    public void singlePrefixOneWordNamedFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void singlePrefixTwoWordsNamedFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void singlePrefixTwoWordsNamedFallingBackToDefaultDoesNotAddName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertNull(namespace.get("a-b"));
    }

    @Test
    public void doublePrefixWordNamedFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("--a");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void doublePrefixTwoWordsNamedFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedAfterSinglePrefixFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a", "--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedFallingBackToDefaultDoesNotAddName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertNull(namespace.get("a-b"));
    }

    @Test
    public void doublePrefixTwoWordsNamedAfterSinglePrefixFallingBackToDefaultDoesNotAddName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("-a", "--a-b");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertNull(namespace.get("a-b"));
    }

    @Test
    public void oneWordPositionalFallingBackToDefaultSucceeds() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("a");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a"));
    }

    @Test
    public void twoWordPositionalFallingBackToDefaultSucceedsForDest() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("a-b");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertEquals("value", namespace.get("a_b"));
    }

    @Test
    public void twoWordPositionalFallingBackToDefaultDoesNotAddName() throws ArgumentParserException {
        ArgumentParser parser = createParser();
        Argument argument = parser.addArgument("a-b");
        argument.nargs("*");
        argument.setDefault("value");

        Namespace namespace = parser.parseArgs(new String[0]);

        assertNull(namespace.get("a-b"));
    }

    @Test
    public void singleOtherPrefixTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("test")
                .prefixChars("|")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        parser.addArgument("|a-b");

        Namespace namespace = parser.parseArgs(new String[]{"|a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    @Test
    public void doubleOtherPrefixTwoWordsNamedSucceedsForName() throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("test")
                .prefixChars("|")
                .includeArgumentNamesAsKeysInResult(true)
                .build();
        parser.addArgument("||a-b");

        Namespace namespace = parser.parseArgs(new String[]{"||a-b", "value"});

        assertEquals("value", namespace.get("a-b"));
    }

    private ArgumentParser createParser() {
        return ArgumentParsers.newFor("test")
                .build();
    }
}